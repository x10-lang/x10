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

//#line 32 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.io.File;

import x10.ast.X10Binary_c;
import x10.ast.X10Unary_c;
import polyglot.types.QName;
import polyglot.types.Name;
import polyglot.ast.AmbExpr;
import polyglot.ast.AmbTypeNode;
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
import polyglot.ast.Initializer;
import polyglot.ast.IntLit;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.New;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Receiver;
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
import x10.extension.X10Ext;
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
import x10.types.X10Flags;
import x10.types.checker.Converter;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.util.CollectionUtil;

import lpg.runtime.BacktrackingParser;
import lpg.runtime.BadParseException;
import lpg.runtime.BadParseSymFileException;
import lpg.runtime.DiagnoseParser;
import lpg.runtime.IToken;
import lpg.runtime.LexStream;
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
    

    //#line 323 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
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
               //#line 8 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 6 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 8 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 18 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 16 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 18 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 28 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 26 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 28 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 38 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 36 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 38 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 48 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 46 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 48 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 58 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 56 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 58 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 68 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 66 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 74 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 74 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 80 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 78 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 78 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 87 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 85 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 87 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 94 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 92 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 94 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 100 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 98 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 100 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 109 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 107 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 117 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 115 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                      break;
            }
    
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 122 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 120 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 120 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "C:/eclipsews/head5/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 920 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 918 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 918 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 918 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 918 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 918 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 918 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 920 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 933 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 931 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 933 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
   setResult(PropertyList);
                 break;
            } 
            //
            // Rule 18:  PropertyList ::= Property
            //
            case 18: {
               //#line 938 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 936 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 938 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 19:  PropertyList ::= PropertyList , Property
            //
            case 19: {
               //#line 945 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 943 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 943 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 945 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 20:  Property ::= Annotationsopt Identifier ResultType
            //
            case 20: {
               //#line 952 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 950 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 950 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 950 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 952 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 961 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 961 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 993 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(13);
                //#line 991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(14);
                //#line 991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(15);
                //#line 993 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1011 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1009 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1009 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1009 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1009 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 1009 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 1009 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 1009 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1009 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1009 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1011 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1029 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1027 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1027 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1027 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 1027 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 1027 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1027 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1027 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1027 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1027 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1029 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1048 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1046 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1046 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1046 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1046 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1046 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1046 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1046 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1046 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1046 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1048 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1068 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1066 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1066 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1066 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1066 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1066 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1066 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1066 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1066 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1068 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1086 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1084 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1084 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1084 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1084 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1084 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1084 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1084 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1084 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1086 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1104 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1102 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1102 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1102 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1102 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1102 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1102 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1102 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(12);
                //#line 1102 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(13);
                //#line 1102 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 1104 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1122 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1120 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1120 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1120 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1120 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1120 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1120 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1120 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1120 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1122 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1140 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1138 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1138 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1138 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1138 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1138 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1138 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1138 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1138 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1140 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1158 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1156 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1156 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1156 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1156 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1156 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(8);
                //#line 1156 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(9);
                //#line 1156 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(10);
                //#line 1156 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1158 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1177 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1175 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1175 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1175 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1175 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1175 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1175 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1175 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(8);
                //#line 1177 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1193 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1191 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1191 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1191 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(4);
                //#line 1191 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 1191 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(6);
                //#line 1193 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1210 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1208 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1208 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1210 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 35:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 35: {
               //#line 1215 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1213 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1213 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1215 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 36:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 36: {
               //#line 1220 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1218 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1218 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1218 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1220 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 37:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 37: {
               //#line 1225 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1223 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1223 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1223 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1225 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 38:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 38: {
               //#line 1231 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1229 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 1229 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1229 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1229 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1229 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1229 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 1229 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1231 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1252 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1250 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1250 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1250 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1250 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1252 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 40:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 40: {
               //#line 1259 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1257 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1257 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1257 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1257 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1257 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1259 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1267 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1265 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1265 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1265 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1265 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1265 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1267 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1276 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1274 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1274 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1276 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 45:  Type ::= proto ConstrainedType
            //
            case 45: {
               //#line 1285 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1283 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ConstrainedType = (TypeNode) getRhsSym(2);
                //#line 1285 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
        AddFlags tn = (AddFlags) ConstrainedType;
        tn.addFlags(X10Flags.PROTO);
        setResult(ConstrainedType.position(pos()));
                      break;
            }
    
            //
            // Rule 46:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt Offersopt => Type
            //
            case 46: {
               //#line 1293 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1291 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 1291 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1291 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1291 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1291 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(7);
                //#line 1291 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(9);
                //#line 1293 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt, Offersopt));
                      break;
            }
    
            //
            // Rule 51:  AnnotatedType ::= Type Annotations
            //
            case 51: {
               //#line 1302 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1300 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1300 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1302 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn.position(pos()));
                      break;
            }
    
            //
            // Rule 54:  ConstrainedType ::= ( Type )
            //
            case 54: {
               //#line 1312 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1310 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1312 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 56:  NamedType ::= Primary . Identifier TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 56: {
               //#line 1326 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1324 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1324 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1324 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1324 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1324 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1326 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1337 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1335 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1335 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1335 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(3);
                //#line 1335 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                //#line 1337 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1355 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1353 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1353 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object Conjunctionopt = (Object) getRhsSym(3);
                //#line 1355 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, (List) Conjunctionopt));
                      break;
            }
    
            //
            // Rule 59:  DepParameters ::= ! PlaceType
            //
            case 59: {
               //#line 1360 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1358 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1360 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 60:  DepParameters ::= !
            //
            case 60: {
               //#line 1366 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1366 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 61:  DepParameters ::= ! PlaceType { ExistentialListopt Conjunction }
            //
            case 61: {
               //#line 1372 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1370 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1370 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(4);
                //#line 1370 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(5);
                //#line 1372 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 62:  DepParameters ::= ! { ExistentialListopt Conjunction }
            //
            case 62: {
               //#line 1378 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1376 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(3);
                //#line 1376 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(4);
                //#line 1378 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 63:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 63: {
               //#line 1386 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1384 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(2);
                //#line 1386 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 64:  TypeParameters ::= [ TypeParameterList ]
            //
            case 64: {
               //#line 1392 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1390 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1392 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 65:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 65: {
               //#line 1397 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1395 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1397 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 66:  Conjunction ::= Expression
            //
            case 66: {
               //#line 1403 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1401 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1403 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 67:  Conjunction ::= Conjunction , Expression
            //
            case 67: {
               //#line 1410 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1408 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1408 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1410 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 68:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 68: {
               //#line 1416 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1414 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1414 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1416 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 69:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 69: {
               //#line 1421 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1419 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1419 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1421 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 70:  WhereClause ::= DepParameters
            //
            case 70: {
               //#line 1427 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1425 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1427 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 71:  Conjunctionopt ::= $Empty
            //
            case 71: {
               //#line 1433 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1433 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                setResult(l);
                      break;
            }
      
            //
            // Rule 72:  Conjunctionopt ::= Conjunction
            //
            case 72: {
               //#line 1439 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1437 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1439 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(Conjunction);
                      break;
            }
    
            //
            // Rule 73:  ExistentialListopt ::= $Empty
            //
            case 73: {
               //#line 1445 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1445 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 74:  ExistentialListopt ::= ExistentialList ;
            //
            case 74: {
               //#line 1450 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1448 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1450 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 75:  ExistentialList ::= FormalParameter
            //
            case 75: {
               //#line 1456 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1454 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1456 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(Position.COMPILER_GENERATED, Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 76:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 76: {
               //#line 1463 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1461 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1461 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1463 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(Position.COMPILER_GENERATED, Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 79:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 79: {
               //#line 1474 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1472 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1472 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1472 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1472 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1472 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1472 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1472 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1472 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1474 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1490 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1488 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1488 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1488 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1488 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1488 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1488 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1488 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1490 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1504 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1502 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1502 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1502 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1502 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1502 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1502 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1502 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1502 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(10);
                //#line 1504 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1521 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1519 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1521 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 83:  FieldKeyword ::= val
            //
            case 83: {
               //#line 1527 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1527 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 84:  FieldKeyword ::= var
            //
            case 84: {
               //#line 1532 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1532 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 85:  FieldKeyword ::= const
            //
            case 85: {
               //#line 1537 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1537 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                      break;
            }
    
            //
            // Rule 86:  VarKeyword ::= val
            //
            case 86: {
               //#line 1545 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1545 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 87:  VarKeyword ::= var
            //
            case 87: {
               //#line 1550 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1550 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 88:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 88: {
               //#line 1557 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1555 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1555 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1555 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1557 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1582 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1580 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1580 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 1582 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1614 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1612 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 1612 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt NonExpressionStatement = (Stmt) getRhsSym(2);
                //#line 1614 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1650 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1648 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1650 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Offer(pos(), Expression));
                      break;
            }
    
            //
            // Rule 120:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 120: {
               //#line 1656 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1654 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1654 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1656 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 121:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 121: {
               //#line 1662 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1660 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1660 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1660 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1662 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 122:  EmptyStatement ::= ;
            //
            case 122: {
               //#line 1668 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1668 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 123:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 123: {
               //#line 1674 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1672 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1672 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 1674 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 129:  ExpressionStatement ::= StatementExpression ;
            //
            case 129: {
               //#line 1686 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1684 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1686 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 137:  AssertStatement ::= assert Expression ;
            //
            case 137: {
               //#line 1700 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1698 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1700 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 138:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 138: {
               //#line 1705 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1703 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1703 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1705 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 139:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 139: {
               //#line 1711 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1709 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1709 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1711 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 140:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 140: {
               //#line 1717 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1715 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1715 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1717 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 142:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 142: {
               //#line 1725 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1723 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1723 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1725 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 143:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 143: {
               //#line 1732 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1730 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1730 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1732 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1741 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1739 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1741 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 145:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 145: {
               //#line 1748 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1746 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1746 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1748 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 146:  SwitchLabel ::= case ConstantExpression :
            //
            case 146: {
               //#line 1755 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1753 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1755 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 147:  SwitchLabel ::= default :
            //
            case 147: {
               //#line 1760 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1760 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 148:  WhileStatement ::= while ( Expression ) Statement
            //
            case 148: {
               //#line 1766 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1764 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1764 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1766 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 149:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 149: {
               //#line 1772 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1770 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1770 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1772 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 152:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 152: {
               //#line 1781 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1779 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1779 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1779 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1779 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1781 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 154:  ForInit ::= LocalVariableDeclaration
            //
            case 154: {
               //#line 1788 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1786 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1788 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 156:  StatementExpressionList ::= StatementExpression
            //
            case 156: {
               //#line 1798 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1796 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1798 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 157:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 157: {
               //#line 1805 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1803 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1803 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1805 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 158:  BreakStatement ::= break Identifieropt ;
            //
            case 158: {
               //#line 1811 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1809 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1811 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 159:  ContinueStatement ::= continue Identifieropt ;
            //
            case 159: {
               //#line 1817 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1815 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1817 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 160:  ReturnStatement ::= return Expressionopt ;
            //
            case 160: {
               //#line 1823 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1821 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1823 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 161:  ThrowStatement ::= throw Expression ;
            //
            case 161: {
               //#line 1829 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1827 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1829 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 162:  TryStatement ::= try Block Catches
            //
            case 162: {
               //#line 1835 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1833 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1833 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1835 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 163:  TryStatement ::= try Block Catchesopt Finally
            //
            case 163: {
               //#line 1840 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1838 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1838 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1838 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1840 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 164:  Catches ::= CatchClause
            //
            case 164: {
               //#line 1846 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1844 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1846 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 165:  Catches ::= Catches CatchClause
            //
            case 165: {
               //#line 1853 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1851 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1851 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1853 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 166:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 166: {
               //#line 1860 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1858 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1858 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1860 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 167:  Finally ::= finally Block
            //
            case 167: {
               //#line 1866 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1864 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1866 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 168:  ClockedClause ::= clocked ( ClockList )
            //
            case 168: {
               //#line 1872 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1870 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1872 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 169:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 169: {
               //#line 1878 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1876 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1876 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1876 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1878 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1887 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1885 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1885 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1887 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 171:  AtomicStatement ::= atomic Statement
            //
            case 171: {
               //#line 1893 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1891 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1893 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 172:  WhenStatement ::= when ( Expression ) Statement
            //
            case 172: {
               //#line 1900 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1898 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1898 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1900 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 173:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 173: {
               //#line 1905 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1903 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1903 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1903 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1903 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1905 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 174:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 174: {
               //#line 1912 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1910 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1910 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1910 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1910 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1912 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1928 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1926 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1926 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1926 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1926 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1928 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1944 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1942 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1942 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1942 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1944 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1958 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1956 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1958 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 178:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 178: {
               //#line 1964 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1962 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1964 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 180:  NextStatement ::= next ;
            //
            case 180: {
               //#line 1972 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1972 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 181:  AwaitStatement ::= await Expression ;
            //
            case 181: {
               //#line 1978 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1976 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1978 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Await(pos(), Expression));
                      break;
            }
    
            //
            // Rule 182:  ClockList ::= Clock
            //
            case 182: {
               //#line 1984 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1982 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1984 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 183:  ClockList ::= ClockList , Clock
            //
            case 183: {
               //#line 1991 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1989 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1989 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1991 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 184:  Clock ::= Expression
            //
            case 184: {
               //#line 1999 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1997 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1999 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 185:  CastExpression ::= CastExpression as Type
            //
            case 185: {
               //#line 2013 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2011 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2011 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2013 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 187:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 187: {
               //#line 2027 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2025 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2027 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 188:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 188: {
               //#line 2034 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2032 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(1);
                //#line 2032 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2034 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 189:  TypeParameterList ::= TypeParameter
            //
            case 189: {
               //#line 2041 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2039 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2041 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 190:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 190: {
               //#line 2048 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2046 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 2046 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2048 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 191:  TypeParamWithVariance ::= Identifier
            //
            case 191: {
               //#line 2055 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2053 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2055 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 192:  TypeParamWithVariance ::= + Identifier
            //
            case 192: {
               //#line 2060 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2058 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2060 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 193:  TypeParamWithVariance ::= - Identifier
            //
            case 193: {
               //#line 2065 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2065 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 194:  TypeParameter ::= Identifier
            //
            case 194: {
               //#line 2071 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2069 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2071 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 195:  Primary ::= here
            //
            case 195: {
               //#line 2077 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2077 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 197:  RegionExpressionList ::= RegionExpression
            //
            case 197: {
               //#line 2085 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2083 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 2085 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 198:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 198: {
               //#line 2092 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2090 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 2090 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 2092 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                      break;
            }
    
            //
            // Rule 199:  Primary ::= [ ArgumentListopt ]
            //
            case 199: {
               //#line 2099 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2097 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 2099 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 200:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 200: {
               //#line 2106 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2104 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2104 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2106 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 201:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt => ClosureBody
            //
            case 201: {
               //#line 2112 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2110 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(1);
                //#line 2110 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2110 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2110 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(4);
                //#line 2110 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(5);
                //#line 2110 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 2112 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 202:  LastExpression ::= Expression
            //
            case 202: {
               //#line 2119 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2117 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2119 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 203:  ClosureBody ::= CastExpression
            //
            case 203: {
               //#line 2125 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2123 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2125 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), CastExpression, true)));
                      break;
            }
    
            //
            // Rule 204:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 204: {
               //#line 2130 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2128 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2128 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2128 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2130 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 205:  ClosureBody ::= Annotationsopt Block
            //
            case 205: {
               //#line 2140 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2138 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2138 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2140 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b.position(pos()));
                      break;
            }
    
            //
            // Rule 206:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 206: {
               //#line 2149 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2147 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2147 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2149 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 207:  AsyncExpression ::= async ClosureBody
            //
            case 207: {
               //#line 2155 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2153 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2155 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 208:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 208: {
               //#line 2160 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2158 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2158 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2160 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 209:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 209: {
               //#line 2165 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2163 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2163 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2165 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 210:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 210: {
               //#line 2170 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2168 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2168 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2168 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2170 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 211:  FinishExpression ::= finish ( Expression ) Block
            //
            case 211: {
               //#line 2177 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2175 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2175 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2177 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FinishExpr(pos(), Expression, Block));
                      break;
            }
    
            //
            // Rule 212:  FutureExpression ::= future ClosureBody
            //
            case 212: {
               //#line 2183 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2181 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2183 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 213:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 213: {
               //#line 2188 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2186 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2186 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2188 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 214:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 214: {
               //#line 2193 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2191 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2191 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2193 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 215:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 215: {
               //#line 2198 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2196 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2196 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2196 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2198 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 216:  DepParametersopt ::= $Empty
            //
            case 216:
                setResult(null);
                break;

            //
            // Rule 218:  PropertyListopt ::= $Empty
            //
            case 218: {
               //#line 2209 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2209 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 220:  WhereClauseopt ::= $Empty
            //
            case 220:
                setResult(null);
                break;

            //
            // Rule 222:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 222:
                setResult(null);
                break;

            //
            // Rule 224:  ClassModifiersopt ::= $Empty
            //
            case 224: {
               //#line 2224 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2224 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 226:  TypeDefModifiersopt ::= $Empty
            //
            case 226: {
               //#line 2230 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2230 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 228:  ClockedClauseopt ::= $Empty
            //
            case 228: {
               //#line 2236 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2236 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 230:  identifier ::= IDENTIFIER$ident
            //
            case 230: {
               //#line 2247 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2245 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2247 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 231:  TypeName ::= Identifier
            //
            case 231: {
               //#line 2254 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2252 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2254 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 232:  TypeName ::= TypeName . Identifier
            //
            case 232: {
               //#line 2259 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2257 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2257 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2259 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 234:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 234: {
               //#line 2271 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2269 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2271 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 235:  TypeArgumentList ::= Type
            //
            case 235: {
               //#line 2278 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2276 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2278 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 236:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 236: {
               //#line 2285 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2283 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2283 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2285 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 237:  PackageName ::= Identifier
            //
            case 237: {
               //#line 2295 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2293 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2295 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 238:  PackageName ::= PackageName . Identifier
            //
            case 238: {
               //#line 2300 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2298 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2298 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2300 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 239:  ExpressionName ::= Identifier
            //
            case 239: {
               //#line 2316 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2314 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2316 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 240:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 240: {
               //#line 2321 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2319 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2319 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2321 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 241:  MethodName ::= Identifier
            //
            case 241: {
               //#line 2331 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2329 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2331 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 242:  MethodName ::= AmbiguousName . Identifier
            //
            case 242: {
               //#line 2336 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2334 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2334 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2336 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 243:  PackageOrTypeName ::= Identifier
            //
            case 243: {
               //#line 2346 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2344 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2346 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 244:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 244: {
               //#line 2351 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2349 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2349 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2351 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 245:  AmbiguousName ::= Identifier
            //
            case 245: {
               //#line 2361 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2359 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2361 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 246:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 246: {
               //#line 2366 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2364 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2364 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2366 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 247:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 247: {
               //#line 2378 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2376 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2376 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2376 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2378 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 248:  ImportDeclarations ::= ImportDeclaration
            //
            case 248: {
               //#line 2394 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2392 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2394 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 249:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 249: {
               //#line 2401 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2399 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2399 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2401 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 250:  TypeDeclarations ::= TypeDeclaration
            //
            case 250: {
               //#line 2409 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2407 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2409 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 251:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 251: {
               //#line 2417 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2415 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2415 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2417 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 252:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 252: {
               //#line 2425 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2423 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2423 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2425 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn.position(pos()));
                      break;
            }
    
            //
            // Rule 255:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 255: {
               //#line 2439 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2437 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2439 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 256:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 256: {
               //#line 2445 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2443 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2445 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 260:  TypeDeclaration ::= ;
            //
            case 260: {
               //#line 2460 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2460 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 261:  ClassModifiers ::= ClassModifier
            //
            case 261: {
               //#line 2468 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2466 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2468 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 262:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 262: {
               //#line 2475 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2473 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2473 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2475 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassModifiers.addAll(ClassModifier);
                      break;
            }
    
            //
            // Rule 263:  ClassModifier ::= Annotation
            //
            case 263: {
               //#line 2481 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2479 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2481 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 264:  ClassModifier ::= public
            //
            case 264: {
               //#line 2486 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2486 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 265:  ClassModifier ::= protected
            //
            case 265: {
               //#line 2491 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2491 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 266:  ClassModifier ::= private
            //
            case 266: {
               //#line 2496 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2496 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 267:  ClassModifier ::= abstract
            //
            case 267: {
               //#line 2501 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2501 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 268:  ClassModifier ::= static
            //
            case 268: {
               //#line 2506 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2506 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 269:  ClassModifier ::= final
            //
            case 269: {
               //#line 2511 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2511 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 270:  ClassModifier ::= safe
            //
            case 270: {
               //#line 2516 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2516 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 271:  TypeDefModifiers ::= TypeDefModifier
            //
            case 271: {
               //#line 2522 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2520 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2522 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 272:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 272: {
               //#line 2529 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2527 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2527 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2529 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                      break;
            }
    
            //
            // Rule 273:  TypeDefModifier ::= Annotation
            //
            case 273: {
               //#line 2535 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2533 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2535 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 274:  TypeDefModifier ::= public
            //
            case 274: {
               //#line 2540 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2540 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 275:  TypeDefModifier ::= protected
            //
            case 275: {
               //#line 2545 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2545 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 276:  TypeDefModifier ::= private
            //
            case 276: {
               //#line 2550 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2550 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 277:  TypeDefModifier ::= abstract
            //
            case 277: {
               //#line 2555 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2555 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 278:  TypeDefModifier ::= static
            //
            case 278: {
               //#line 2560 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2560 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 279:  TypeDefModifier ::= final
            //
            case 279: {
               //#line 2565 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2565 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 280:  Interfaces ::= implements InterfaceTypeList
            //
            case 280: {
               //#line 2574 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2572 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2574 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 281:  InterfaceTypeList ::= Type
            //
            case 281: {
               //#line 2580 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2578 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2580 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 282:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 282: {
               //#line 2587 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2585 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2585 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2587 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 283:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 283: {
               //#line 2597 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2595 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2597 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 285:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 285: {
               //#line 2604 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2602 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2602 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2604 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 287:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 287: {
               //#line 2626 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2624 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2626 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 289:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 289: {
               //#line 2635 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2633 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2635 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 290:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 290: {
               //#line 2642 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2640 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2642 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 291:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 291: {
               //#line 2649 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2647 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2649 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 292:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 292: {
               //#line 2656 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2654 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2656 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 293:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 293: {
               //#line 2663 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2661 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2663 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 294:  ClassMemberDeclaration ::= ;
            //
            case 294: {
               //#line 2670 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2670 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 295:  FormalDeclarators ::= FormalDeclarator
            //
            case 295: {
               //#line 2677 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2675 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2677 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 296:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 296: {
               //#line 2684 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2682 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2682 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2684 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 297:  FieldDeclarators ::= FieldDeclarator
            //
            case 297: {
               //#line 2691 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2689 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2691 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 298:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 298: {
               //#line 2698 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2696 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2696 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2698 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 299:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 299: {
               //#line 2706 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2704 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 2706 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 300:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 300: {
               //#line 2713 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2711 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 2711 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 2713 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 301:  VariableDeclarators ::= VariableDeclarator
            //
            case 301: {
               //#line 2720 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2718 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2720 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 302:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 302: {
               //#line 2727 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2725 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2725 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2727 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 304:  FieldModifiers ::= FieldModifier
            //
            case 304: {
               //#line 2736 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2734 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2736 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 305:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 305: {
               //#line 2743 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2741 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2741 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2743 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldModifiers.addAll(FieldModifier);
                      break;
            }
    
            //
            // Rule 306:  FieldModifier ::= Annotation
            //
            case 306: {
               //#line 2749 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2747 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2749 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 307:  FieldModifier ::= public
            //
            case 307: {
               //#line 2754 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2754 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 308:  FieldModifier ::= protected
            //
            case 308: {
               //#line 2759 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2759 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 309:  FieldModifier ::= private
            //
            case 309: {
               //#line 2764 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2764 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 310:  FieldModifier ::= static
            //
            case 310: {
               //#line 2769 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2769 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 311:  FieldModifier ::= global
            //
            case 311: {
               //#line 2774 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2774 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 312:  ResultType ::= : Type
            //
            case 312: {
               //#line 2780 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2778 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2780 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 313:  HasResultType ::= : Type
            //
            case 313: {
               //#line 2785 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2783 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2785 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 314:  HasResultType ::= <: Type
            //
            case 314: {
               //#line 2790 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2788 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2790 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.HasType(Type));
                      break;
            }
    
            //
            // Rule 315:  FormalParameters ::= ( FormalParameterList )
            //
            case 315: {
               //#line 2796 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2794 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2796 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterList);
                      break;
            }
    
            //
            // Rule 316:  FormalParameterList ::= FormalParameter
            //
            case 316: {
               //#line 2802 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2800 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2802 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 317:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 317: {
               //#line 2809 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2807 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2807 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2809 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 318:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 318: {
               //#line 2815 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2813 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2813 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2815 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 319:  LoopIndexDeclarator ::= ( IdentifierList ) HasResultTypeopt
            //
            case 319: {
               //#line 2820 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2818 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2818 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2820 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 320:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt
            //
            case 320: {
               //#line 2825 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2823 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2823 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2823 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2825 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 321:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 321: {
               //#line 2831 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2829 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2829 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2831 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 322:  LoopIndex ::= VariableModifiersopt VarKeyword LoopIndexDeclarator
            //
            case 322: {
               //#line 2854 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2852 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2852 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2852 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2854 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 323:  FormalParameter ::= VariableModifiersopt FormalDeclarator
            //
            case 323: {
               //#line 2878 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2876 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2876 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2878 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 324:  FormalParameter ::= VariableModifiersopt VarKeyword FormalDeclarator
            //
            case 324: {
               //#line 2902 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2900 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2900 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2900 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2902 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 325:  FormalParameter ::= Type
            //
            case 325: {
               //#line 2926 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2924 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2926 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh("id$")), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 326:  VariableModifiers ::= VariableModifier
            //
            case 326: {
               //#line 2934 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2932 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2934 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 327:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 327: {
               //#line 2941 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2939 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2939 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2941 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableModifiers.addAll(VariableModifier);
                      break;
            }
    
            //
            // Rule 328:  VariableModifier ::= Annotation
            //
            case 328: {
               //#line 2947 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2945 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2947 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 329:  VariableModifier ::= shared
            //
            case 329: {
               //#line 2952 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2952 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                      break;
            }
    
            //
            // Rule 330:  MethodModifiers ::= MethodModifier
            //
            case 330: {
               //#line 2961 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2961 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 331:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 331: {
               //#line 2968 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2966 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 2966 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 2968 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiers.addAll(MethodModifier);
                      break;
            }
    
            //
            // Rule 332:  MethodModifier ::= Annotation
            //
            case 332: {
               //#line 2974 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2972 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2974 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 333:  MethodModifier ::= public
            //
            case 333: {
               //#line 2979 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2979 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 334:  MethodModifier ::= protected
            //
            case 334: {
               //#line 2984 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2984 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 335:  MethodModifier ::= private
            //
            case 335: {
               //#line 2989 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2989 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 336:  MethodModifier ::= abstract
            //
            case 336: {
               //#line 2994 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2994 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 337:  MethodModifier ::= static
            //
            case 337: {
               //#line 2999 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2999 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 338:  MethodModifier ::= final
            //
            case 338: {
               //#line 3004 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3004 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 339:  MethodModifier ::= native
            //
            case 339: {
               //#line 3009 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3009 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 340:  MethodModifier ::= atomic
            //
            case 340: {
               //#line 3014 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3014 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                      break;
            }
    
            //
            // Rule 341:  MethodModifier ::= extern
            //
            case 341: {
               //#line 3019 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3019 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
                      break;
            }
    
            //
            // Rule 342:  MethodModifier ::= safe
            //
            case 342: {
               //#line 3024 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3024 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 343:  MethodModifier ::= sequential
            //
            case 343: {
               //#line 3029 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3029 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                      break;
            }
    
            //
            // Rule 344:  MethodModifier ::= nonblocking
            //
            case 344: {
               //#line 3034 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3034 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                      break;
            }
    
            //
            // Rule 345:  MethodModifier ::= incomplete
            //
            case 345: {
               //#line 3039 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3039 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                      break;
            }
    
            //
            // Rule 346:  MethodModifier ::= property
            //
            case 346: {
               //#line 3044 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3044 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                      break;
            }
    
            //
            // Rule 347:  MethodModifier ::= global
            //
            case 347: {
               //#line 3049 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3049 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 348:  MethodModifier ::= proto
            //
            case 348: {
               //#line 3054 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3054 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROTO)));
                      break;
            }
    
            //
            // Rule 349:  Throws ::= throws ExceptionTypeList
            //
            case 349: {
               //#line 3061 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3059 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 3061 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 350:  Offers ::= offers Type
            //
            case 350: {
               //#line 3066 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3064 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3066 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 351:  ExceptionTypeList ::= ExceptionType
            //
            case 351: {
               //#line 3072 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3070 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3072 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 352:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 352: {
               //#line 3079 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3077 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3077 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3079 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 354:  MethodBody ::= = LastExpression ;
            //
            case 354: {
               //#line 3087 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3085 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3087 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 355:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 355: {
               //#line 3092 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3090 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3090 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(4);
                //#line 3090 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3092 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult((Block) ((X10Ext) nf.Block(pos(),l).ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 356:  MethodBody ::= = Annotationsopt Block
            //
            case 356: {
               //#line 3100 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3098 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3098 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3100 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 357:  MethodBody ::= Annotationsopt Block
            //
            case 357: {
               //#line 3105 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3103 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 3103 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3105 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 358:  MethodBody ::= ;
            //
            case 358:
                setResult(null);
                break;

            //
            // Rule 359:  SimpleTypeName ::= Identifier
            //
            case 359: {
               //#line 3125 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3123 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3125 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 360:  ConstructorModifiers ::= ConstructorModifier
            //
            case 360: {
               //#line 3131 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3129 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 3131 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 361:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 361: {
               //#line 3138 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3136 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 3136 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 3138 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                      break;
            }
    
            //
            // Rule 362:  ConstructorModifier ::= Annotation
            //
            case 362: {
               //#line 3144 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3142 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3144 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 363:  ConstructorModifier ::= public
            //
            case 363: {
               //#line 3149 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3149 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 364:  ConstructorModifier ::= protected
            //
            case 364: {
               //#line 3154 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3154 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 365:  ConstructorModifier ::= private
            //
            case 365: {
               //#line 3159 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3159 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 366:  ConstructorModifier ::= native
            //
            case 366: {
               //#line 3164 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3164 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 367:  ConstructorBody ::= = ConstructorBlock
            //
            case 367: {
               //#line 3170 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3168 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3170 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 368:  ConstructorBody ::= ConstructorBlock
            //
            case 368: {
               //#line 3175 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3173 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3175 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 369:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 369: {
               //#line 3180 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3178 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3180 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 370:  ConstructorBody ::= = AssignPropertyCall
            //
            case 370: {
               //#line 3188 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3186 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3188 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.SuperCall(pos(), Collections.EMPTY_LIST));
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 371:  ConstructorBody ::= ;
            //
            case 371:
                setResult(null);
                break;

            //
            // Rule 372:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 372: {
               //#line 3200 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3198 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3198 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3200 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                if (ExplicitConstructorInvocationopt == null)
                {
                    l.add(nf.SuperCall(pos(), Collections.EMPTY_LIST));
                }
                else
                {
                    l.add(ExplicitConstructorInvocationopt);
                }
                l.addAll(BlockStatementsopt);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 373:  Arguments ::= ( ArgumentListopt )
            //
            case 373: {
               //#line 3217 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3215 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3217 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 375:  InterfaceModifiers ::= InterfaceModifier
            //
            case 375: {
               //#line 3227 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3225 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 3227 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 376:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 376: {
               //#line 3234 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3232 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 3232 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 3234 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                      break;
            }
    
            //
            // Rule 377:  InterfaceModifier ::= Annotation
            //
            case 377: {
               //#line 3240 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3238 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3240 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 378:  InterfaceModifier ::= public
            //
            case 378: {
               //#line 3245 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3245 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 379:  InterfaceModifier ::= protected
            //
            case 379: {
               //#line 3250 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3250 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 380:  InterfaceModifier ::= private
            //
            case 380: {
               //#line 3255 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3255 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 381:  InterfaceModifier ::= abstract
            //
            case 381: {
               //#line 3260 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3260 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 382:  InterfaceModifier ::= static
            //
            case 382: {
               //#line 3265 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3265 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 383:  ExtendsInterfaces ::= extends Type
            //
            case 383: {
               //#line 3271 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3269 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3271 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 384:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 384: {
               //#line 3278 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3276 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3276 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3278 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 385:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 385: {
               //#line 3287 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3285 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3287 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 387:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 387: {
               //#line 3294 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3292 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3292 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3294 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 388:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 388: {
               //#line 3301 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3299 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3301 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 389:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 389: {
               //#line 3308 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3306 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3308 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 390:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 390: {
               //#line 3315 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3313 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3315 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 391:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 391: {
               //#line 3322 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3320 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3322 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 392:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 392: {
               //#line 3329 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3327 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3329 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 393:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 393: {
               //#line 3336 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3334 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3336 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 394:  InterfaceMemberDeclaration ::= ;
            //
            case 394: {
               //#line 3343 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3343 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 395:  Annotations ::= Annotation
            //
            case 395: {
               //#line 3349 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3347 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3349 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 396:  Annotations ::= Annotations Annotation
            //
            case 396: {
               //#line 3356 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3354 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3354 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3356 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 397:  Annotation ::= @ NamedType
            //
            case 397: {
               //#line 3362 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3360 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3362 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 398:  SimpleName ::= Identifier
            //
            case 398: {
               //#line 3368 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3366 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3368 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 399:  Identifier ::= identifier
            //
            case 399: {
               //#line 3374 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3372 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3374 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 400:  VariableInitializers ::= VariableInitializer
            //
            case 400: {
               //#line 3382 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3380 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3382 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 401:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 401: {
               //#line 3389 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3387 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3387 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3389 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                      break;
            }
    
            //
            // Rule 402:  Block ::= { BlockStatementsopt }
            //
            case 402: {
               //#line 3407 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3405 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3407 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 403:  BlockStatements ::= BlockStatement
            //
            case 403: {
               //#line 3413 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3411 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3413 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 404:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 404: {
               //#line 3420 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3418 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3418 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3420 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 406:  BlockStatement ::= ClassDeclaration
            //
            case 406: {
               //#line 3428 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3426 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3428 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 407:  BlockStatement ::= TypeDefDeclaration
            //
            case 407: {
               //#line 3435 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3433 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3435 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 408:  BlockStatement ::= Statement
            //
            case 408: {
               //#line 3442 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3440 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3442 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 409:  IdentifierList ::= Identifier
            //
            case 409: {
               //#line 3450 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3448 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3450 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 410:  IdentifierList ::= IdentifierList , Identifier
            //
            case 410: {
               //#line 3457 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3455 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3455 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3457 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 411:  FormalDeclarator ::= Identifier ResultType
            //
            case 411: {
               //#line 3463 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3461 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3461 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3463 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 412:  FormalDeclarator ::= ( IdentifierList ) ResultType
            //
            case 412: {
               //#line 3468 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3466 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3466 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3468 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 413:  FormalDeclarator ::= Identifier ( IdentifierList ) ResultType
            //
            case 413: {
               //#line 3473 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3471 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3471 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3471 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3473 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 414:  FieldDeclarator ::= Identifier HasResultType
            //
            case 414: {
               //#line 3479 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3477 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3477 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3479 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultType, null });
                      break;
            }
    
            //
            // Rule 415:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 415: {
               //#line 3484 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3482 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3482 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3482 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3484 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 416:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 416: {
               //#line 3490 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3488 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3488 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3488 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3490 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 417:  VariableDeclarator ::= ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 417: {
               //#line 3495 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3493 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3493 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3493 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3495 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 418:  VariableDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 418: {
               //#line 3500 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3498 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3498 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3498 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3498 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3500 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 419:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 419: {
               //#line 3506 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3504 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3504 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3504 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3506 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 420:  VariableDeclaratorWithType ::= ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 420: {
               //#line 3511 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3509 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3509 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(4);
                //#line 3509 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3511 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 421:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 421: {
               //#line 3516 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3514 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3514 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3514 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(5);
                //#line 3514 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3516 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 423:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 423: {
               //#line 3524 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3522 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3522 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3522 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3524 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 424:  LocalVariableDeclaration ::= VariableModifiersopt VariableDeclaratorsWithType
            //
            case 424: {
               //#line 3557 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3555 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3555 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 3557 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 425:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword FormalDeclarators
            //
            case 425: {
               //#line 3591 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3589 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3589 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3589 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3591 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 427:  Primary ::= self
            //
            case 427: {
               //#line 3633 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3633 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 428:  Primary ::= this
            //
            case 428: {
               //#line 3638 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3638 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 429:  Primary ::= ClassName . this
            //
            case 429: {
               //#line 3643 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3641 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3643 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 430:  Primary ::= ( Expression )
            //
            case 430: {
               //#line 3648 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3646 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3648 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 436:  OperatorFunction ::= TypeName . +
            //
            case 436: {
               //#line 3659 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3657 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3659 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 437:  OperatorFunction ::= TypeName . -
            //
            case 437: {
               //#line 3670 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3668 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3670 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 438:  OperatorFunction ::= TypeName . *
            //
            case 438: {
               //#line 3681 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3679 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3681 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 439:  OperatorFunction ::= TypeName . /
            //
            case 439: {
               //#line 3692 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3690 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3692 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 440:  OperatorFunction ::= TypeName . %
            //
            case 440: {
               //#line 3703 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3701 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3703 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 441:  OperatorFunction ::= TypeName . &
            //
            case 441: {
               //#line 3714 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3712 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3714 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 442:  OperatorFunction ::= TypeName . |
            //
            case 442: {
               //#line 3725 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3723 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3725 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 443:  OperatorFunction ::= TypeName . ^
            //
            case 443: {
               //#line 3736 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3734 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3736 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 444:  OperatorFunction ::= TypeName . <<
            //
            case 444: {
               //#line 3747 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3745 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3747 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 445:  OperatorFunction ::= TypeName . >>
            //
            case 445: {
               //#line 3758 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3756 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3758 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 446:  OperatorFunction ::= TypeName . >>>
            //
            case 446: {
               //#line 3769 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3767 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3769 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 447:  OperatorFunction ::= TypeName . <
            //
            case 447: {
               //#line 3780 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3778 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3780 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 448:  OperatorFunction ::= TypeName . <=
            //
            case 448: {
               //#line 3791 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3789 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3791 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 449:  OperatorFunction ::= TypeName . >=
            //
            case 449: {
               //#line 3802 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3800 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3802 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 450:  OperatorFunction ::= TypeName . >
            //
            case 450: {
               //#line 3813 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3811 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3813 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 451:  OperatorFunction ::= TypeName . ==
            //
            case 451: {
               //#line 3824 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3822 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3824 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 452:  OperatorFunction ::= TypeName . !=
            //
            case 452: {
               //#line 3835 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3833 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3835 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 453:  Literal ::= IntegerLiteral$lit
            //
            case 453: {
               //#line 3848 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3846 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3848 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 454:  Literal ::= LongLiteral$lit
            //
            case 454: {
               //#line 3854 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3852 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3854 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 455:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 455: {
               //#line 3860 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3858 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3860 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 456:  Literal ::= UnsignedLongLiteral$lit
            //
            case 456: {
               //#line 3866 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3864 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3866 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 457:  Literal ::= FloatingPointLiteral$lit
            //
            case 457: {
               //#line 3872 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3870 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3872 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 458:  Literal ::= DoubleLiteral$lit
            //
            case 458: {
               //#line 3878 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3876 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3878 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 459:  Literal ::= BooleanLiteral
            //
            case 459: {
               //#line 3884 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3882 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3884 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 460:  Literal ::= CharacterLiteral$lit
            //
            case 460: {
               //#line 3889 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3887 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3889 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 461:  Literal ::= StringLiteral$str
            //
            case 461: {
               //#line 3895 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3893 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3895 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 462:  Literal ::= null
            //
            case 462: {
               //#line 3901 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3901 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 463:  BooleanLiteral ::= true$trueLiteral
            //
            case 463: {
               //#line 3907 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3905 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3907 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 464:  BooleanLiteral ::= false$falseLiteral
            //
            case 464: {
               //#line 3912 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3910 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3912 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 465:  ArgumentList ::= Expression
            //
            case 465: {
               //#line 3921 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3919 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3921 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 466:  ArgumentList ::= ArgumentList , Expression
            //
            case 466: {
               //#line 3928 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3926 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3926 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3928 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 467:  FieldAccess ::= Primary . Identifier
            //
            case 467: {
               //#line 3934 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3932 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3932 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3934 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 468:  FieldAccess ::= super . Identifier
            //
            case 468: {
               //#line 3939 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3937 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3939 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 469:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 469: {
               //#line 3944 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3942 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3942 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3942 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3944 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 470:  FieldAccess ::= Primary . class$c
            //
            case 470: {
               //#line 3949 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3947 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3947 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3949 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 471:  FieldAccess ::= super . class$c
            //
            case 471: {
               //#line 3954 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3952 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3954 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 472:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 472: {
               //#line 3959 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3957 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3957 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3957 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 3959 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 473:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 473: {
               //#line 3965 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3963 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3963 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3963 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3965 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 474:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 474: {
               //#line 3972 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3970 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3970 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3970 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3970 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3972 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 475:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 475: {
               //#line 3977 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3975 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3975 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3975 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3977 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 476:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 476: {
               //#line 3982 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3980 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3980 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3980 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3980 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 3980 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 3982 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 477:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 477: {
               //#line 3987 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3985 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3985 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3985 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3987 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 478:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 478: {
               //#line 4007 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4005 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4005 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(4);
                //#line 4007 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 479:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 479: {
               //#line 4020 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4018 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4018 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4018 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4020 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 480:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 480: {
               //#line 4032 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4030 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4030 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4032 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 481:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 481: {
               //#line 4044 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4042 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4042 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4042 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4042 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(8);
                //#line 4044 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 483:  PostfixExpression ::= ExpressionName
            //
            case 483: {
               //#line 4059 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4057 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4059 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 486:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 486: {
               //#line 4067 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4065 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4067 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 487:  PostDecrementExpression ::= PostfixExpression --
            //
            case 487: {
               //#line 4073 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4071 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4073 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 490:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 490: {
               //#line 4081 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4079 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4081 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 491:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 491: {
               //#line 4086 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4084 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4086 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 493:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 493: {
               //#line 4093 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4091 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4093 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 494:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 494: {
               //#line 4099 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4097 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4099 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 496:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 496: {
               //#line 4106 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4104 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4106 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 497:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 497: {
               //#line 4111 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4109 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4109 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4111 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e.position(pos()));
                      break;
            }
    
            //
            // Rule 498:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 498: {
               //#line 4118 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4116 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4118 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 500:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 500: {
               //#line 4125 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4123 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4123 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4125 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 501:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 501: {
               //#line 4130 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4128 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4128 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4130 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 502:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 502: {
               //#line 4135 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4133 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4133 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4135 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 504:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 504: {
               //#line 4142 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4140 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4140 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4142 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 505:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 505: {
               //#line 4147 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4145 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4145 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4147 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 507:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 507: {
               //#line 4154 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4152 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4152 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4154 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 508:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 508: {
               //#line 4159 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4157 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4157 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4159 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 509:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 509: {
               //#line 4164 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4162 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4162 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4164 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 511:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 511: {
               //#line 4171 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4169 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4169 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4171 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 514:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 514: {
               //#line 4180 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4178 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4178 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4180 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 515:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 515: {
               //#line 4185 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4183 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4183 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4185 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 516:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 516: {
               //#line 4190 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4188 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4188 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4190 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 517:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 517: {
               //#line 4195 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4193 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4193 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4195 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 518:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 518: {
               //#line 4200 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4198 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4198 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4200 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 519:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 519: {
               //#line 4205 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4203 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4203 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4205 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 521:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 521: {
               //#line 4212 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4210 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4210 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4212 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 522:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 522: {
               //#line 4217 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4215 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4215 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4217 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 523:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 523: {
               //#line 4222 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4220 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4220 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4222 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 525:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 525: {
               //#line 4229 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4227 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4227 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4229 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 527:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 527: {
               //#line 4236 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4234 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4234 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4236 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 529:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 529: {
               //#line 4243 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4241 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4241 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4243 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 531:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 531: {
               //#line 4250 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4248 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4248 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4250 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 533:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 533: {
               //#line 4257 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4255 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4255 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4257 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 540:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 540: {
               //#line 4270 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4268 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4268 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4268 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4270 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 543:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 543: {
               //#line 4279 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4277 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4277 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4277 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4279 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 544:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 544: {
               //#line 4284 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4282 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4282 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4282 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4282 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4284 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 545:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 545: {
               //#line 4289 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4287 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4287 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4287 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4287 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4289 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 546:  LeftHandSide ::= ExpressionName
            //
            case 546: {
               //#line 4295 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4293 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4295 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 548:  AssignmentOperator ::= =
            //
            case 548: {
               //#line 4302 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4302 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 549:  AssignmentOperator ::= *=
            //
            case 549: {
               //#line 4307 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4307 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 550:  AssignmentOperator ::= /=
            //
            case 550: {
               //#line 4312 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4312 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 551:  AssignmentOperator ::= %=
            //
            case 551: {
               //#line 4317 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4317 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 552:  AssignmentOperator ::= +=
            //
            case 552: {
               //#line 4322 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4322 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 553:  AssignmentOperator ::= -=
            //
            case 553: {
               //#line 4327 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4327 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 554:  AssignmentOperator ::= <<=
            //
            case 554: {
               //#line 4332 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4332 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 555:  AssignmentOperator ::= >>=
            //
            case 555: {
               //#line 4337 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4337 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 556:  AssignmentOperator ::= >>>=
            //
            case 556: {
               //#line 4342 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4342 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 557:  AssignmentOperator ::= &=
            //
            case 557: {
               //#line 4347 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4347 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 558:  AssignmentOperator ::= ^=
            //
            case 558: {
               //#line 4352 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4352 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 559:  AssignmentOperator ::= |=
            //
            case 559: {
               //#line 4357 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4357 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 562:  PrefixOp ::= +
            //
            case 562: {
               //#line 4368 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4368 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 563:  PrefixOp ::= -
            //
            case 563: {
               //#line 4373 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4373 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 564:  PrefixOp ::= !
            //
            case 564: {
               //#line 4378 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4378 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 565:  PrefixOp ::= ~
            //
            case 565: {
               //#line 4383 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4383 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 566:  BinOp ::= +
            //
            case 566: {
               //#line 4389 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4389 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 567:  BinOp ::= -
            //
            case 567: {
               //#line 4394 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4394 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 568:  BinOp ::= *
            //
            case 568: {
               //#line 4399 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4399 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 569:  BinOp ::= /
            //
            case 569: {
               //#line 4404 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4404 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 570:  BinOp ::= %
            //
            case 570: {
               //#line 4409 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4409 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 571:  BinOp ::= &
            //
            case 571: {
               //#line 4414 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4414 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 572:  BinOp ::= |
            //
            case 572: {
               //#line 4419 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4419 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 573:  BinOp ::= ^
            //
            case 573: {
               //#line 4424 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4424 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 574:  BinOp ::= &&
            //
            case 574: {
               //#line 4429 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4429 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 575:  BinOp ::= ||
            //
            case 575: {
               //#line 4434 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4434 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 576:  BinOp ::= <<
            //
            case 576: {
               //#line 4439 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4439 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 577:  BinOp ::= >>
            //
            case 577: {
               //#line 4444 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4444 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 578:  BinOp ::= >>>
            //
            case 578: {
               //#line 4449 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4449 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 579:  BinOp ::= >=
            //
            case 579: {
               //#line 4454 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4454 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 580:  BinOp ::= <=
            //
            case 580: {
               //#line 4459 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4459 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 581:  BinOp ::= >
            //
            case 581: {
               //#line 4464 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4464 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 582:  BinOp ::= <
            //
            case 582: {
               //#line 4469 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4469 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 583:  BinOp ::= ==
            //
            case 583: {
               //#line 4477 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4477 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 584:  BinOp ::= !=
            //
            case 584: {
               //#line 4482 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4482 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 585:  Catchesopt ::= $Empty
            //
            case 585: {
               //#line 4491 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4491 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4500 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4498 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4500 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 589:  ForUpdateopt ::= $Empty
            //
            case 589: {
               //#line 4506 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4506 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4517 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4517 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 595:  SwitchLabelsopt ::= $Empty
            //
            case 595: {
               //#line 4524 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4524 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 597:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 597: {
               //#line 4531 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4531 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 599:  VariableModifiersopt ::= $Empty
            //
            case 599: {
               //#line 4538 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4538 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4549 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4549 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 605:  ExtendsInterfacesopt ::= $Empty
            //
            case 605: {
               //#line 4556 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4556 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 607:  InterfaceModifiersopt ::= $Empty
            //
            case 607: {
               //#line 4563 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4563 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4574 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4574 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 613:  ArgumentListopt ::= $Empty
            //
            case 613: {
               //#line 4581 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4581 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 615:  BlockStatementsopt ::= $Empty
            //
            case 615: {
               //#line 4588 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4588 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4599 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4599 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 621:  FormalParameterListopt ::= $Empty
            //
            case 621: {
               //#line 4606 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4606 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 623:  Throwsopt ::= $Empty
            //
            case 623: {
               //#line 4613 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4613 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 625:  Offersopt ::= $Empty
            //
            case 625: {
               //#line 4619 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4619 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 627:  MethodModifiersopt ::= $Empty
            //
            case 627: {
               //#line 4626 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4626 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 629:  TypeModifieropt ::= $Empty
            //
            case 629: {
               //#line 4633 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4633 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 631:  FieldModifiersopt ::= $Empty
            //
            case 631: {
               //#line 4640 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4640 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 633:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 633: {
               //#line 4647 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4647 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 635:  Interfacesopt ::= $Empty
            //
            case 635: {
               //#line 4654 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4654 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4665 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4665 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 641:  FormalParametersopt ::= $Empty
            //
            case 641: {
               //#line 4672 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4672 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 643:  Annotationsopt ::= $Empty
            //
            case 643: {
               //#line 4679 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4679 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 645:  TypeDeclarationsopt ::= $Empty
            //
            case 645: {
               //#line 4686 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4686 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 647:  ImportDeclarationsopt ::= $Empty
            //
            case 647: {
               //#line 4693 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4693 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4711 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4711 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 657:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 657: {
               //#line 4718 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4718 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 659:  Propertiesopt ::= $Empty
            //
            case 659: {
               //#line 4725 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4725 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

