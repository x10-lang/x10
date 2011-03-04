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
import polyglot.ast.Receiver;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.SwitchElement;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.FlagsNode;
import polyglot.ast.ProcedureDecl;
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
import polyglot.frontend.Globals;
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
    

    //#line 328 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
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
               //#line 925 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 923 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 923 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 923 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 923 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 923 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 923 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 925 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 17:  TypeDefDeclaration ::= TypeDefModifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt ;
            //
            case 17: {
                syntaxError("Type definition is missing '= Type'",pos());
            }
    
            //
            // Rule 18:  Properties ::= ( PropertyList )
            //
            case 18: {
               //#line 950 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 948 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 950 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
   setResult(PropertyList);
                 break;
            } 
            //
            // Rule 19:  PropertyList ::= Property
            //
            case 19: {
               //#line 955 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 953 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 955 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 20:  PropertyList ::= PropertyList , Property
            //
            case 20: {
               //#line 962 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 960 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 960 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 962 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 21:  Property ::= Annotationsopt Identifier ResultType
            //
            case 21: {
               //#line 969 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 967 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 967 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 967 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 969 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List annotations = extractAnnotations(Annotationsopt);
                PropertyDecl cd = nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), ResultType, Identifier);
                cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 22:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 22: {
               //#line 978 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 976 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 976 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 976 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 976 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 976 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 976 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 976 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 976 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 976 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 978 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 23:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 23: {
               //#line 1010 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1008 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1008 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1008 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1008 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1008 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 1008 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 1008 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 1008 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(13);
                //#line 1008 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(14);
                //#line 1008 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(15);
                //#line 1010 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 24:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 24: {
               //#line 1028 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1026 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1026 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1026 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1026 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 1026 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 1026 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 1026 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1026 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1026 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1028 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(12)),
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
            // Rule 25:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 25: {
               //#line 1046 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1044 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1044 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1044 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 1044 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 1044 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1044 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1044 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1044 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1044 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1046 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(13)),
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
            // Rule 26:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 26: {
               //#line 1065 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1065 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
       Name op = X10Binary_c.invBinaryMethodName(BinOp);
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(13)),
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
            // Rule 27:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 27: {
               //#line 1085 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1083 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1083 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1083 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1083 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1083 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1083 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1083 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1083 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1085 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(10)),
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
            // Rule 28:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 28: {
               //#line 1103 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1101 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1101 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1101 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1101 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1101 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1101 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1101 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1101 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1103 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(10)),
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
            // Rule 29:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 29: {
               //#line 1121 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1119 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1119 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1119 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1119 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1119 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1119 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1119 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(12);
                //#line 1119 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(13);
                //#line 1119 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 1121 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(14)),
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
            // Rule 30:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Throwsopt Offersopt MethodBody
            //
            case 30: {
               //#line 1139 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1137 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1137 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1137 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1137 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1137 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1137 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1137 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1137 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1139 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(12)),
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
            // Rule 31:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 31: {
               //#line 1157 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1155 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1155 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1155 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1155 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1155 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1155 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1155 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1155 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1157 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(13)),
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
            // Rule 32:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 32: {
               //#line 1175 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1173 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1173 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1173 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1173 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1173 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(8);
                //#line 1173 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(9);
                //#line 1173 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(10);
                //#line 1173 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1175 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(11)),
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
            // Rule 33:  PropertyMethodDeclaration ::= MethodModifiersopt property Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt MethodBody
            //
            case 33: {
               //#line 1194 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1192 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1192 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1192 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1192 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1192 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1192 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1192 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1192 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1194 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(9)),
          extractFlags(MethodModifiersopt, X10Flags.PROPERTY),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          Identifier,
          TypeParametersopt,
          FormalParameters,
          WhereClauseopt,
          Throwsopt,
          null, // offersOpt
          MethodBody);
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 34:  PropertyMethodDeclaration ::= MethodModifiersopt property Identifier WhereClauseopt HasResultTypeopt MethodBody
            //
            case 34: {
               //#line 1210 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1208 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1208 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1208 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(4);
                //#line 1208 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 1208 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(6);
                //#line 1210 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(6)),
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
            // Rule 35:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 35: {
               //#line 1227 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1225 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1225 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1227 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 36:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 36: {
               //#line 1232 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1230 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1230 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1232 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 37:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 37: {
               //#line 1237 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1235 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1235 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1235 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1237 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 38:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 38: {
               //#line 1242 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1240 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1240 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1240 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1242 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 39:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 39: {
               //#line 1248 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1246 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 1246 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1246 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1246 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1246 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1246 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 1246 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1248 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 40:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 40: {
               //#line 1269 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1267 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1267 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1267 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1267 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1269 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 41:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 41: {
               //#line 1276 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1274 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1274 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1274 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1274 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1274 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1276 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 42:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 42: {
               //#line 1284 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1282 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1282 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1282 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1282 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1282 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1284 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 43:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 43: {
               //#line 1293 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1291 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1291 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1293 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 46:  Type ::= proto ConstrainedType
            //
            case 46: {
               //#line 1302 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1300 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ConstrainedType = (TypeNode) getRhsSym(2);
                //#line 1302 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
        AddFlags tn = (AddFlags) ConstrainedType;
        tn.addFlags(X10Flags.PROTO);
        setResult(ConstrainedType.position(pos()));
                      break;
            }
    
            //
            // Rule 47:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt Offersopt => Type
            //
            case 47: {
               //#line 1310 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1308 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 1308 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1308 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1308 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1308 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(7);
                //#line 1308 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(9);
                //#line 1310 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt, Offersopt));
                      break;
            }
    
            //
            // Rule 52:  AnnotatedType ::= Type Annotations
            //
            case 52: {
               //#line 1319 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1317 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1317 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1319 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn.position(pos()));
                      break;
            }
    
            //
            // Rule 55:  ConstrainedType ::= ( Type )
            //
            case 55: {
               //#line 1329 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1327 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1329 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 57:  NamedType ::= Primary . Identifier TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 57: {
               //#line 1343 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1341 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1341 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1341 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1341 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1341 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1343 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbTypeNode(pos(), Primary, Identifier);
            // TODO: place constraint
            if (DepParametersopt != null || (TypeArgumentsopt != null && ! TypeArgumentsopt.isEmpty()) || (Argumentsopt != null && ! Argumentsopt.isEmpty())) {
                type = nf.AmbDepTypeNode(pos(), Primary, Identifier, TypeArgumentsopt, Argumentsopt, DepParametersopt);
            }
            setResult(type);
                      break;
            }
    
            //
            // Rule 58:  NamedType ::= TypeName TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 58: {
               //#line 1354 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1352 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1352 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1352 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(3);
                //#line 1352 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                //#line 1354 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type;
            
            if (TypeName.name.id().toString().equals("void")) {
                type = nf.CanonicalTypeNode(pos(), ts.Void());
            } else
            if (ts.isPrimitiveTypeName(TypeName.name.id())) {
                try {
                    type = nf.CanonicalTypeNode(pos(), ts.primitiveForName(TypeName.name.id()));
                }
                catch (SemanticException e) {
                    throw new InternalCompilerError("Unable to create primitive type for '" + TypeName.name.id() + "'!");
                }
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
            // Rule 59:  DepParameters ::= { ExistentialListopt Conjunctionopt }
            //
            case 59: {
               //#line 1380 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1378 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1378 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object Conjunctionopt = (Object) getRhsSym(3);
                //#line 1380 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, (List) Conjunctionopt));
                      break;
            }
    
            //
            // Rule 60:  DepParameters ::= ! PlaceType
            //
            case 60: {
               //#line 1385 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1383 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1385 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 61:  DepParameters ::= !
            //
            case 61: {
               //#line 1391 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1391 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 62:  DepParameters ::= ! PlaceType { ExistentialListopt Conjunction }
            //
            case 62: {
               //#line 1397 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1395 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1395 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(4);
                //#line 1395 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(5);
                //#line 1397 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 63:  DepParameters ::= ! { ExistentialListopt Conjunction }
            //
            case 63: {
               //#line 1403 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1401 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(3);
                //#line 1401 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(4);
                //#line 1403 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 64:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 64: {
               //#line 1411 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1409 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(2);
                //#line 1411 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 65:  TypeParameters ::= [ TypeParameterList ]
            //
            case 65: {
               //#line 1417 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1415 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1417 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 66:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 66: {
               //#line 1422 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1420 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1422 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 67:  Conjunction ::= Expression
            //
            case 67: {
               //#line 1428 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1426 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1428 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 68:  Conjunction ::= Conjunction , Expression
            //
            case 68: {
               //#line 1435 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1433 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1433 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1435 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 69:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 69: {
               //#line 1441 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1439 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1439 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1441 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 70:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 70: {
               //#line 1446 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1444 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1444 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1446 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 71:  WhereClause ::= DepParameters
            //
            case 71: {
               //#line 1452 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1450 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1452 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 72:  Conjunctionopt ::= $Empty
            //
            case 72: {
               //#line 1458 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1458 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                setResult(l);
                      break;
            }
      
            //
            // Rule 73:  Conjunctionopt ::= Conjunction
            //
            case 73: {
               //#line 1464 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1462 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1464 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(Conjunction);
                      break;
            }
    
            //
            // Rule 74:  ExistentialListopt ::= $Empty
            //
            case 74: {
               //#line 1470 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1470 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 75:  ExistentialListopt ::= ExistentialList ;
            //
            case 75: {
               //#line 1475 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1473 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1475 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 76:  ExistentialList ::= FormalParameter
            //
            case 76: {
               //#line 1481 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1479 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1481 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(Position.COMPILER_GENERATED, Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 77:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 77: {
               //#line 1488 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1486 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1486 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1488 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(Position.COMPILER_GENERATED, Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 80:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 80: {
               //#line 1499 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1497 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1497 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1497 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1497 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1497 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1497 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1497 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1497 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1499 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 81:  StructDeclaration ::= ClassModifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 81: {
               //#line 1515 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1513 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1513 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1513 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1513 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1513 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1513 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1513 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1515 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 82:  ConstructorDeclaration ::= ConstructorModifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt ConstructorBody
            //
            case 82: {
               //#line 1529 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1527 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1527 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1527 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1527 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1527 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1527 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1527 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1527 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(10);
                //#line 1529 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 83:  Super ::= extends ClassType
            //
            case 83: {
               //#line 1546 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1544 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1546 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 84:  FieldKeyword ::= val
            //
            case 84: {
               //#line 1552 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1552 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 85:  FieldKeyword ::= var
            //
            case 85: {
               //#line 1557 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1557 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 86:  FieldKeyword ::= const
            //
            case 86: {
               //#line 1562 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1562 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                      break;
            }
    
            //
            // Rule 87:  VarKeyword ::= val
            //
            case 87: {
               //#line 1570 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1570 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 88:  VarKeyword ::= var
            //
            case 88: {
               //#line 1575 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1575 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 89:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 89: {
               //#line 1582 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1580 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1580 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1580 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1582 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 90:  FieldDeclaration ::= FieldModifiersopt FieldDeclarators ;
            //
            case 90: {
               //#line 1607 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1605 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1605 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 1607 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 93:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 93: {
               //#line 1639 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1637 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 1637 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt NonExpressionStatement = (Stmt) getRhsSym(2);
                //#line 1639 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                if (NonExpressionStatement.ext() instanceof X10Ext && Annotationsopt instanceof List) {
                    NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations((List) Annotationsopt);
                }
                setResult(NonExpressionStatement.position(pos()));
                      break;
            }
    
            //
            // Rule 121:  Statement ::= offer Expression ;
            //
            case 121: {
               //#line 1676 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1674 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1676 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Offer(pos(), Expression));
                      break;
            }
    
            //
            // Rule 122:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 122: {
               //#line 1682 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1680 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1680 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1682 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 123:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 123: {
               //#line 1688 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1686 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1686 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1686 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1688 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 124:  EmptyStatement ::= ;
            //
            case 124: {
               //#line 1694 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1694 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 125:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 125: {
               //#line 1700 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1698 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1698 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 1700 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 131:  ExpressionStatement ::= StatementExpression ;
            //
            case 131: {
               //#line 1712 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1710 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1712 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"                        
                setResult(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 139:  AssertStatement ::= assert Expression ;
            //
            case 139: {
               //#line 1743 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1741 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1743 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 140:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 140: {
               //#line 1748 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1746 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1746 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1748 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 141:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 141: {
               //#line 1754 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1752 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1752 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1754 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 142:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 142: {
               //#line 1760 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1758 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1758 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1760 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 144:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 144: {
               //#line 1768 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1766 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1766 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1768 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 145:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 145: {
               //#line 1775 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1773 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1773 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1775 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 146:  SwitchLabels ::= SwitchLabel
            //
            case 146: {
               //#line 1784 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1782 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1784 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 147:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 147: {
               //#line 1791 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1789 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1789 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1791 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 148:  SwitchLabel ::= case ConstantExpression :
            //
            case 148: {
               //#line 1798 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1796 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1798 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 149:  SwitchLabel ::= default :
            //
            case 149: {
               //#line 1803 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1803 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 150:  WhileStatement ::= while ( Expression ) Statement
            //
            case 150: {
               //#line 1809 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1807 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1807 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1809 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 151:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 151: {
               //#line 1815 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1813 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1813 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1815 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 154:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 154: {
               //#line 1824 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1822 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1822 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1822 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1822 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1824 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 156:  ForInit ::= LocalVariableDeclaration
            //
            case 156: {
               //#line 1831 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1829 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1831 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 158:  StatementExpressionList ::= StatementExpression
            //
            case 158: {
               //#line 1841 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1839 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1841 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 159:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 159: {
               //#line 1848 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1846 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1846 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1848 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 160:  BreakStatement ::= break Identifieropt ;
            //
            case 160: {
               //#line 1854 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1852 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1854 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 161:  ContinueStatement ::= continue Identifieropt ;
            //
            case 161: {
               //#line 1860 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1858 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1860 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 162:  ReturnStatement ::= return Expressionopt ;
            //
            case 162: {
               //#line 1866 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1864 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1866 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 163:  ThrowStatement ::= throw Expression ;
            //
            case 163: {
               //#line 1872 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1870 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1872 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 164:  TryStatement ::= try Block Catches
            //
            case 164: {
               //#line 1878 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1876 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1876 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1878 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 165:  TryStatement ::= try Block Catchesopt Finally
            //
            case 165: {
               //#line 1883 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1881 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1881 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1881 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1883 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 166:  Catches ::= CatchClause
            //
            case 166: {
               //#line 1889 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1887 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1889 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 167:  Catches ::= Catches CatchClause
            //
            case 167: {
               //#line 1896 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1894 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1894 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1896 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 168:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 168: {
               //#line 1903 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1901 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1901 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1903 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 169:  Finally ::= finally Block
            //
            case 169: {
               //#line 1909 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1907 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1909 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 170:  NowStatement ::= now ( Clock ) Statement
            //
            case 170: {
               //#line 1915 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1913 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1913 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1915 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Now(pos(), Clock, Statement));
                      break;
            }
    
            //
            // Rule 171:  ClockedClause ::= clocked ( ClockList )
            //
            case 171: {
               //#line 1921 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1919 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1921 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 172:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 172: {
               //#line 1927 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1925 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1925 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1925 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1927 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 173:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 173: {
               //#line 1936 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1934 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1934 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1936 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 174:  AtomicStatement ::= atomic Statement
            //
            case 174: {
               //#line 1942 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1940 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1942 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 175:  WhenStatement ::= when ( Expression ) Statement
            //
            case 175: {
               //#line 1949 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1947 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1947 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1949 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 176:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 176: {
               //#line 1954 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1952 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1952 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1952 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1952 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1954 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 177:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 177: {
               //#line 1961 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1959 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1961 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 178:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 178: {
               //#line 1977 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1975 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1975 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1975 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1975 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1977 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 179:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 179: {
               //#line 1993 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1991 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1993 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 180:  FinishStatement ::= finish Statement
            //
            case 180: {
               //#line 2007 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2005 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2007 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 181:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 181: {
               //#line 2013 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2011 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 2013 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 183:  NextStatement ::= next ;
            //
            case 183: {
               //#line 2021 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2021 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 184:  AwaitStatement ::= await Expression ;
            //
            case 184: {
               //#line 2027 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2025 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2027 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Await(pos(), Expression));
                      break;
            }
    
            //
            // Rule 185:  ClockList ::= Clock
            //
            case 185: {
               //#line 2033 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2031 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 2033 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 186:  ClockList ::= ClockList , Clock
            //
            case 186: {
               //#line 2040 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2038 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 2038 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 2040 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 187:  Clock ::= Expression
            //
            case 187: {
               //#line 2048 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2046 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2048 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 188:  CastExpression ::= CastExpression as Type
            //
            case 188: {
               //#line 2062 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2060 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2060 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2062 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 190:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 190: {
               //#line 2076 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2074 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2076 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 191:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 191: {
               //#line 2083 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2081 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(1);
                //#line 2081 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2083 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 192:  TypeParameterList ::= TypeParameter
            //
            case 192: {
               //#line 2090 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2088 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2090 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 193:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 193: {
               //#line 2097 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2095 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 2095 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2097 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 194:  TypeParamWithVariance ::= Identifier
            //
            case 194: {
               //#line 2104 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2102 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2104 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 195:  TypeParamWithVariance ::= + Identifier
            //
            case 195: {
               //#line 2109 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2107 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2109 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 196:  TypeParamWithVariance ::= - Identifier
            //
            case 196: {
               //#line 2114 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2112 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2114 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 197:  TypeParameter ::= Identifier
            //
            case 197: {
               //#line 2120 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2118 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2120 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 198:  Primary ::= here
            //
            case 198: {
               //#line 2126 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2126 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 200:  RegionExpressionList ::= RegionExpression
            //
            case 200: {
               //#line 2134 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2132 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 2134 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 201:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 201: {
               //#line 2141 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2139 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 2139 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 2141 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                      break;
            }
    
            //
            // Rule 202:  Primary ::= [ ArgumentListopt ]
            //
            case 202: {
               //#line 2148 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2146 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 2148 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 203:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 203: {
               //#line 2155 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2153 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2153 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2155 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 204:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt => ClosureBody
            //
            case 204: {
               //#line 2161 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2159 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(1);
                //#line 2159 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2159 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2159 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(4);
                //#line 2159 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(5);
                //#line 2159 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 2161 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 205:  LastExpression ::= Expression
            //
            case 205: {
               //#line 2168 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2166 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2168 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 206:  ClosureBody ::= CastExpression
            //
            case 206: {
               //#line 2174 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2172 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2174 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), CastExpression, true)));
                      break;
            }
    
            //
            // Rule 207:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 207: {
               //#line 2179 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2177 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2177 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2177 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2179 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 208:  ClosureBody ::= Annotationsopt Block
            //
            case 208: {
               //#line 2189 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2187 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2187 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2189 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b.position(pos()));
                      break;
            }
    
            //
            // Rule 209:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 209: {
               //#line 2198 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2196 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2196 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2198 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 210:  AsyncExpression ::= async ClosureBody
            //
            case 210: {
               //#line 2204 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2202 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2204 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 211:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 211: {
               //#line 2209 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2207 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2207 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2209 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 212:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 212: {
               //#line 2214 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2212 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2212 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2214 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 213:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 213: {
               //#line 2219 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2217 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2217 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2217 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2219 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 214:  FinishExpression ::= finish ( Expression ) Block
            //
            case 214: {
               //#line 2226 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2224 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2224 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2226 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FinishExpr(pos(), Expression, Block));
                      break;
            }
    
            //
            // Rule 215:  FutureExpression ::= future ClosureBody
            //
            case 215: {
               //#line 2232 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2230 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2232 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 216:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 216: {
               //#line 2237 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2235 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2235 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2237 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 217:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 217: {
               //#line 2242 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2240 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2240 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2242 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 218:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 218: {
               //#line 2247 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2245 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2245 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2245 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2247 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 219:  DepParametersopt ::= $Empty
            //
            case 219:
                setResult(null);
                break;

            //
            // Rule 221:  PropertyListopt ::= $Empty
            //
            case 221: {
               //#line 2258 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2258 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 223:  WhereClauseopt ::= $Empty
            //
            case 223:
                setResult(null);
                break;

            //
            // Rule 225:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 225:
                setResult(null);
                break;

            //
            // Rule 227:  ClassModifiersopt ::= $Empty
            //
            case 227: {
               //#line 2273 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2273 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 229:  TypeDefModifiersopt ::= $Empty
            //
            case 229: {
               //#line 2279 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2279 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 231:  Unsafeopt ::= $Empty
            //
            case 231:
                setResult(null);
                break;

            //
            // Rule 232:  Unsafeopt ::= unsafe
            //
            case 232: {
               //#line 2287 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2287 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                // any value distinct from null
                setResult(this);
                      break;
            }
    
            //
            // Rule 233:  ClockedClauseopt ::= $Empty
            //
            case 233: {
               //#line 2294 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2294 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 235:  identifier ::= IDENTIFIER$ident
            //
            case 235: {
               //#line 2305 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2303 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2305 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 236:  TypeName ::= Identifier
            //
            case 236: {
               //#line 2312 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2310 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2312 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 237:  TypeName ::= TypeName . Identifier
            //
            case 237: {
               //#line 2317 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2315 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2315 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2317 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 239:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 239: {
               //#line 2329 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2327 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2329 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 240:  TypeArgumentList ::= Type
            //
            case 240: {
               //#line 2336 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2334 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2336 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 241:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 241: {
               //#line 2343 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2341 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2341 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2343 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 242:  PackageName ::= Identifier
            //
            case 242: {
               //#line 2353 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2351 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2353 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 243:  PackageName ::= PackageName . Identifier
            //
            case 243: {
               //#line 2358 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2356 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2356 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2358 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 244:  ExpressionName ::= Identifier
            //
            case 244: {
               //#line 2374 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2372 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2374 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 245:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 245: {
               //#line 2379 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2377 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2377 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2379 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 246:  MethodName ::= Identifier
            //
            case 246: {
               //#line 2389 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2387 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2389 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 247:  MethodName ::= AmbiguousName . Identifier
            //
            case 247: {
               //#line 2394 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2392 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2392 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2394 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 248:  PackageOrTypeName ::= Identifier
            //
            case 248: {
               //#line 2404 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2402 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2404 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 249:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 249: {
               //#line 2409 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2407 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2407 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2409 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 250:  AmbiguousName ::= Identifier
            //
            case 250: {
               //#line 2419 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2417 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2419 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 251:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 251: {
               //#line 2424 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2422 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2422 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2424 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 252:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 252: {
               //#line 2436 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2434 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2434 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2434 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2436 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 253:  ImportDeclarations ::= ImportDeclaration
            //
            case 253: {
               //#line 2452 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2450 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2452 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 254:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 254: {
               //#line 2459 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2457 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2457 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2459 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 255:  TypeDeclarations ::= TypeDeclaration
            //
            case 255: {
               //#line 2467 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2465 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2467 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 256:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 256: {
               //#line 2475 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2473 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2473 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2475 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 257:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 257: {
               //#line 2483 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2481 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2481 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2483 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn.position(pos()));
                      break;
            }
    
            //
            // Rule 260:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 260: {
               //#line 2497 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2495 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2497 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 261:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 261: {
               //#line 2503 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2501 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2503 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 265:  TypeDeclaration ::= ;
            //
            case 265: {
               //#line 2518 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2518 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 266:  ClassModifiers ::= ClassModifier
            //
            case 266: {
               //#line 2526 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2524 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2526 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 267:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 267: {
               //#line 2533 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2531 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2531 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2533 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassModifiers.addAll(ClassModifier);
                      break;
            }
    
            //
            // Rule 268:  ClassModifier ::= Annotation
            //
            case 268: {
               //#line 2539 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2537 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2539 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 269:  ClassModifier ::= public
            //
            case 269: {
               //#line 2544 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2544 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 270:  ClassModifier ::= protected
            //
            case 270: {
               //#line 2549 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2549 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 271:  ClassModifier ::= private
            //
            case 271: {
               //#line 2554 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2554 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 272:  ClassModifier ::= abstract
            //
            case 272: {
               //#line 2559 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2559 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 273:  ClassModifier ::= static
            //
            case 273: {
               //#line 2564 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2564 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 274:  ClassModifier ::= final
            //
            case 274: {
               //#line 2569 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2569 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 275:  ClassModifier ::= strictfp
            //
            case 275: {
               //#line 2574 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2574 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 276:  ClassModifier ::= safe
            //
            case 276: {
               //#line 2579 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2579 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 277:  TypeDefModifiers ::= TypeDefModifier
            //
            case 277: {
               //#line 2585 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2583 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2585 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 278:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 278: {
               //#line 2592 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2590 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2590 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2592 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                      break;
            }
    
            //
            // Rule 279:  TypeDefModifier ::= Annotation
            //
            case 279: {
               //#line 2598 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2596 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2598 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 280:  TypeDefModifier ::= public
            //
            case 280: {
               //#line 2603 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2603 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 281:  TypeDefModifier ::= protected
            //
            case 281: {
               //#line 2608 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2608 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 282:  TypeDefModifier ::= private
            //
            case 282: {
               //#line 2613 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2613 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 283:  TypeDefModifier ::= abstract
            //
            case 283: {
               //#line 2618 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2618 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 284:  TypeDefModifier ::= static
            //
            case 284: {
               //#line 2623 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2623 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 285:  TypeDefModifier ::= final
            //
            case 285: {
               //#line 2628 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2628 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 286:  Interfaces ::= implements InterfaceTypeList
            //
            case 286: {
               //#line 2637 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2635 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2637 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 287:  InterfaceTypeList ::= Type
            //
            case 287: {
               //#line 2643 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2641 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2643 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 288:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 288: {
               //#line 2650 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2648 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2648 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2650 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 289:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 289: {
               //#line 2660 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2658 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2660 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 291:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 291: {
               //#line 2667 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2665 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2665 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2667 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 293:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 293: {
               //#line 2689 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2687 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2689 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 295:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 295: {
               //#line 2698 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2696 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2698 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 296:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 296: {
               //#line 2705 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2703 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2705 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 297:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 297: {
               //#line 2712 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2710 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2712 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 298:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 298: {
               //#line 2719 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2717 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2719 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 299:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 299: {
               //#line 2726 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2724 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2726 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 300:  ClassMemberDeclaration ::= ;
            //
            case 300: {
               //#line 2733 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2733 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 301:  FormalDeclarators ::= FormalDeclarator
            //
            case 301: {
               //#line 2740 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2738 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2740 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 302:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 302: {
               //#line 2747 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2745 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2745 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2747 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 303:  FieldDeclarators ::= FieldDeclarator
            //
            case 303: {
               //#line 2754 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2752 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2754 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 304:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 304: {
               //#line 2761 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2759 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2759 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2761 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 305:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 305: {
               //#line 2769 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2767 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 2769 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 306:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 306: {
               //#line 2776 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2774 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 2774 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 2776 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 307:  VariableDeclarators ::= VariableDeclarator
            //
            case 307: {
               //#line 2783 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2781 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2783 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 308:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 308: {
               //#line 2790 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2788 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2788 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2790 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 310:  FieldModifiers ::= FieldModifier
            //
            case 310: {
               //#line 2799 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2797 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2799 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 311:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 311: {
               //#line 2806 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2804 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2804 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2806 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldModifiers.addAll(FieldModifier);
                      break;
            }
    
            //
            // Rule 312:  FieldModifier ::= Annotation
            //
            case 312: {
               //#line 2812 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2810 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2812 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 313:  FieldModifier ::= public
            //
            case 313: {
               //#line 2817 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2817 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 314:  FieldModifier ::= protected
            //
            case 314: {
               //#line 2822 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2822 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 315:  FieldModifier ::= private
            //
            case 315: {
               //#line 2827 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2827 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 316:  FieldModifier ::= static
            //
            case 316: {
               //#line 2832 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2832 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 317:  FieldModifier ::= transient
            //
            case 317: {
               //#line 2837 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2837 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.TRANSIENT)));
                      break;
            }
    
            //
            // Rule 318:  FieldModifier ::= volatile
            //
            case 318: {
               //#line 2842 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2842 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.VOLATILE)));
                      break;
            }
    
            //
            // Rule 319:  FieldModifier ::= global
            //
            case 319: {
               //#line 2847 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2847 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 320:  ResultType ::= : Type
            //
            case 320: {
               //#line 2853 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2851 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2853 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 321:  HasResultType ::= : Type
            //
            case 321: {
               //#line 2858 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2856 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2858 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 322:  HasResultType ::= <: Type
            //
            case 322: {
               //#line 2863 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2861 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2863 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.HasType(Type));
                      break;
            }
    
            //
            // Rule 323:  FormalParameters ::= ( FormalParameterList )
            //
            case 323: {
               //#line 2869 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2867 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2869 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterList);
                      break;
            }
    
            //
            // Rule 324:  FormalParameterList ::= FormalParameter
            //
            case 324: {
               //#line 2875 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2873 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2875 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 325:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 325: {
               //#line 2882 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2880 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2880 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2882 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 326:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 326: {
               //#line 2888 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2886 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2886 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2888 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 327:  LoopIndexDeclarator ::= ( IdentifierList ) HasResultTypeopt
            //
            case 327: {
               //#line 2893 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2891 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2891 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2893 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 328:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt
            //
            case 328: {
               //#line 2898 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2896 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2896 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2896 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2898 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 329:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 329: {
               //#line 2904 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2902 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2902 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2904 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 330:  LoopIndex ::= VariableModifiersopt VarKeyword LoopIndexDeclarator
            //
            case 330: {
               //#line 2927 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2925 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2925 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2925 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2927 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 331:  FormalParameter ::= VariableModifiersopt FormalDeclarator
            //
            case 331: {
               //#line 2951 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2949 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2949 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2951 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 332:  FormalParameter ::= VariableModifiersopt VarKeyword FormalDeclarator
            //
            case 332: {
               //#line 2975 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2973 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2973 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2973 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2975 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 333:  FormalParameter ::= Type
            //
            case 333: {
               //#line 2999 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2997 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2999 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh("id$")), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 334:  VariableModifiers ::= VariableModifier
            //
            case 334: {
               //#line 3007 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3005 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 3007 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 335:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 335: {
               //#line 3014 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3012 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 3012 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 3014 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableModifiers.addAll(VariableModifier);
                      break;
            }
    
            //
            // Rule 336:  VariableModifier ::= Annotation
            //
            case 336: {
               //#line 3020 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3018 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3020 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 337:  VariableModifier ::= shared
            //
            case 337: {
               //#line 3025 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3025 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                      break;
            }
    
            //
            // Rule 338:  MethodModifiers ::= MethodModifier
            //
            case 338: {
               //#line 3034 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3032 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 3034 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 339:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 339: {
               //#line 3041 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3039 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 3039 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 3041 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiers.addAll(MethodModifier);
                      break;
            }
    
            //
            // Rule 340:  MethodModifier ::= Annotation
            //
            case 340: {
               //#line 3047 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3045 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3047 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 341:  MethodModifier ::= public
            //
            case 341: {
               //#line 3052 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3052 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 342:  MethodModifier ::= protected
            //
            case 342: {
               //#line 3057 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3057 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 343:  MethodModifier ::= private
            //
            case 343: {
               //#line 3062 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3062 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 344:  MethodModifier ::= abstract
            //
            case 344: {
               //#line 3067 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3067 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 345:  MethodModifier ::= static
            //
            case 345: {
               //#line 3072 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3072 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 346:  MethodModifier ::= final
            //
            case 346: {
               //#line 3077 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3077 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 347:  MethodModifier ::= native
            //
            case 347: {
               //#line 3082 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3082 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 348:  MethodModifier ::= strictfp
            //
            case 348: {
               //#line 3087 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3087 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 349:  MethodModifier ::= atomic
            //
            case 349: {
               //#line 3092 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3092 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                      break;
            }
    
            //
            // Rule 350:  MethodModifier ::= extern
            //
            case 350: {
               //#line 3097 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3097 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
                      break;
            }
    
            //
            // Rule 351:  MethodModifier ::= safe
            //
            case 351: {
               //#line 3102 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3102 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 352:  MethodModifier ::= sequential
            //
            case 352: {
               //#line 3107 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3107 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                      break;
            }
    
            //
            // Rule 353:  MethodModifier ::= nonblocking
            //
            case 353: {
               //#line 3112 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3112 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                      break;
            }
    
            //
            // Rule 354:  MethodModifier ::= incomplete
            //
            case 354: {
               //#line 3117 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3117 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                      break;
            }
    
            //
            // Rule 355:  MethodModifier ::= property
            //
            case 355: {
               //#line 3122 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3122 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                      break;
            }
    
            //
            // Rule 356:  MethodModifier ::= global
            //
            case 356: {
               //#line 3127 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3127 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 357:  MethodModifier ::= proto
            //
            case 357: {
               //#line 3132 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3132 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROTO)));
                      break;
            }
    
            //
            // Rule 358:  Throws ::= throws ExceptionTypeList
            //
            case 358: {
               //#line 3139 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3137 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 3139 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 359:  Offers ::= offers Type
            //
            case 359: {
               //#line 3144 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3142 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3144 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 360:  ExceptionTypeList ::= ExceptionType
            //
            case 360: {
               //#line 3150 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3148 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3150 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 361:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 361: {
               //#line 3157 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3155 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3155 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3157 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 363:  MethodBody ::= = LastExpression ;
            //
            case 363: {
               //#line 3165 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3163 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3165 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 364:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 364: {
               //#line 3170 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3168 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3168 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(4);
                //#line 3168 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3170 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult((Block) ((X10Ext) nf.Block(pos(),l).ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 365:  MethodBody ::= = Annotationsopt Block
            //
            case 365: {
               //#line 3178 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3176 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3176 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3178 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 366:  MethodBody ::= Annotationsopt Block
            //
            case 366: {
               //#line 3183 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3181 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 3181 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3183 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 367:  MethodBody ::= ;
            //
            case 367:
                setResult(null);
                break;

            //
            // Rule 368:  SimpleTypeName ::= Identifier
            //
            case 368: {
               //#line 3203 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3201 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3203 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 369:  ConstructorModifiers ::= ConstructorModifier
            //
            case 369: {
               //#line 3209 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3207 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 3209 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 370:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 370: {
               //#line 3216 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3214 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 3214 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 3216 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                      break;
            }
    
            //
            // Rule 371:  ConstructorModifier ::= Annotation
            //
            case 371: {
               //#line 3222 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3220 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3222 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 372:  ConstructorModifier ::= public
            //
            case 372: {
               //#line 3227 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3227 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 373:  ConstructorModifier ::= protected
            //
            case 373: {
               //#line 3232 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3232 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 374:  ConstructorModifier ::= private
            //
            case 374: {
               //#line 3237 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3237 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 375:  ConstructorModifier ::= native
            //
            case 375: {
               //#line 3242 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3242 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 376:  ConstructorBody ::= = ConstructorBlock
            //
            case 376: {
               //#line 3248 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3246 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3248 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 377:  ConstructorBody ::= ConstructorBlock
            //
            case 377: {
               //#line 3253 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3251 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3253 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 378:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 378: {
               //#line 3258 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3256 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3258 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 379:  ConstructorBody ::= = AssignPropertyCall
            //
            case 379: {
               //#line 3266 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3264 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3266 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.SuperCall(pos(), Collections.EMPTY_LIST));
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 380:  ConstructorBody ::= ;
            //
            case 380:
                setResult(null);
                break;

            //
            // Rule 381:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 381: {
               //#line 3278 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3276 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3276 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3278 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 382:  Arguments ::= ( ArgumentListopt )
            //
            case 382: {
               //#line 3295 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3293 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3295 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 384:  InterfaceModifiers ::= InterfaceModifier
            //
            case 384: {
               //#line 3305 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3303 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 3305 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 385:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 385: {
               //#line 3312 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3310 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 3310 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 3312 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                      break;
            }
    
            //
            // Rule 386:  InterfaceModifier ::= Annotation
            //
            case 386: {
               //#line 3318 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3316 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3318 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 387:  InterfaceModifier ::= public
            //
            case 387: {
               //#line 3323 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3323 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 388:  InterfaceModifier ::= protected
            //
            case 388: {
               //#line 3328 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3328 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 389:  InterfaceModifier ::= private
            //
            case 389: {
               //#line 3333 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3333 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 390:  InterfaceModifier ::= abstract
            //
            case 390: {
               //#line 3338 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3338 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 391:  InterfaceModifier ::= static
            //
            case 391: {
               //#line 3343 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3343 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 392:  InterfaceModifier ::= strictfp
            //
            case 392: {
               //#line 3348 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3348 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 393:  ExtendsInterfaces ::= extends Type
            //
            case 393: {
               //#line 3354 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3352 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3354 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 394:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 394: {
               //#line 3361 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3359 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3359 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3361 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 395:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 395: {
               //#line 3370 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3368 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3370 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 397:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 397: {
               //#line 3377 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3375 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3375 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3377 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 398:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 398: {
               //#line 3384 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3382 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3384 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 399:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 399: {
               //#line 3391 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3389 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3391 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 400:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 400: {
               //#line 3398 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3396 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3398 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 401:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 401: {
               //#line 3405 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3403 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3405 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 402:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 402: {
               //#line 3412 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3410 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3412 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 403:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 403: {
               //#line 3419 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3417 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3419 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 404:  InterfaceMemberDeclaration ::= ;
            //
            case 404: {
               //#line 3426 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3426 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 405:  Annotations ::= Annotation
            //
            case 405: {
               //#line 3432 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3430 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3432 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 406:  Annotations ::= Annotations Annotation
            //
            case 406: {
               //#line 3439 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3437 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3437 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3439 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 407:  Annotation ::= @ NamedType
            //
            case 407: {
               //#line 3445 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3443 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3445 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 408:  SimpleName ::= Identifier
            //
            case 408: {
               //#line 3451 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3449 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3451 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 409:  Identifier ::= identifier
            //
            case 409: {
               //#line 3457 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3455 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3457 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 410:  VariableInitializers ::= VariableInitializer
            //
            case 410: {
               //#line 3465 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3463 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3465 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 411:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 411: {
               //#line 3472 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3470 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3470 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3472 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                      break;
            }
    
            //
            // Rule 412:  Block ::= { BlockStatementsopt }
            //
            case 412: {
               //#line 3490 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3488 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3490 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 413:  BlockStatements ::= BlockStatement
            //
            case 413: {
               //#line 3496 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3494 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3496 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 414:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 414: {
               //#line 3503 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3501 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3501 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3503 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 416:  BlockStatement ::= ClassDeclaration
            //
            case 416: {
               //#line 3511 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3509 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3511 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 417:  BlockStatement ::= TypeDefDeclaration
            //
            case 417: {
               //#line 3518 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3516 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3518 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 418:  BlockStatement ::= Statement
            //
            case 418: {
               //#line 3525 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3523 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3525 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 419:  IdentifierList ::= Identifier
            //
            case 419: {
               //#line 3533 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3531 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3533 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 420:  IdentifierList ::= IdentifierList , Identifier
            //
            case 420: {
               //#line 3540 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3538 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3538 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3540 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 421:  FormalDeclarator ::= Identifier ResultType
            //
            case 421: {
               //#line 3546 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3544 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3544 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3546 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 422:  FormalDeclarator ::= ( IdentifierList ) ResultType
            //
            case 422: {
               //#line 3551 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3549 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3549 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3551 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 423:  FormalDeclarator ::= Identifier ( IdentifierList ) ResultType
            //
            case 423: {
               //#line 3556 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3554 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3554 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3554 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3556 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 424:  FieldDeclarator ::= Identifier HasResultType
            //
            case 424: {
               //#line 3562 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3560 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3560 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3562 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultType, null });
                      break;
            }
    
            //
            // Rule 425:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 425: {
               //#line 3567 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3565 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3565 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3565 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3567 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 426:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 426: {
               //#line 3573 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3571 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3571 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3571 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3573 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 427:  VariableDeclarator ::= ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 427: {
               //#line 3578 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3576 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3576 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3576 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3578 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 428:  VariableDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 428: {
               //#line 3583 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3581 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3581 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3581 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3581 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3583 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 429:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 429: {
               //#line 3589 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3587 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3587 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3587 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3589 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 430:  VariableDeclaratorWithType ::= ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 430: {
               //#line 3594 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3592 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3592 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(4);
                //#line 3592 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3594 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 431:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 431: {
               //#line 3599 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3597 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3597 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3597 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(5);
                //#line 3597 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3599 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 433:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 433: {
               //#line 3607 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3605 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3605 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3605 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3607 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 434:  LocalVariableDeclaration ::= VariableModifiersopt VariableDeclaratorsWithType
            //
            case 434: {
               //#line 3640 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3638 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3638 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 3640 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 435:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword FormalDeclarators
            //
            case 435: {
               //#line 3674 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3672 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3672 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3672 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3674 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 437:  Primary ::= TypeName . class
            //
            case 437: {
               assert(false);
                      break;
            }
    
            //
            // Rule 438:  Primary ::= self
            //
            case 438: {
               //#line 3726 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3726 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 439:  Primary ::= this
            //
            case 439: {
               //#line 3731 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3731 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 440:  Primary ::= ClassName . this
            //
            case 440: {
               //#line 3736 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3734 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3736 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 441:  Primary ::= ( Expression )
            //
            case 441: {
               //#line 3741 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3739 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3741 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 447:  OperatorFunction ::= TypeName . +
            //
            case 447: {
               //#line 3752 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3750 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3752 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 448:  OperatorFunction ::= TypeName . -
            //
            case 448: {
               //#line 3763 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3761 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3763 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 449:  OperatorFunction ::= TypeName . *
            //
            case 449: {
               //#line 3774 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3772 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3774 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 450:  OperatorFunction ::= TypeName . /
            //
            case 450: {
               //#line 3785 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3783 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3785 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 451:  OperatorFunction ::= TypeName . %
            //
            case 451: {
               //#line 3796 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3794 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3796 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 452:  OperatorFunction ::= TypeName . &
            //
            case 452: {
               //#line 3807 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3805 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3807 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 453:  OperatorFunction ::= TypeName . |
            //
            case 453: {
               //#line 3818 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3816 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3818 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 454:  OperatorFunction ::= TypeName . ^
            //
            case 454: {
               //#line 3829 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3827 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3829 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 455:  OperatorFunction ::= TypeName . <<
            //
            case 455: {
               //#line 3840 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3838 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3840 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 456:  OperatorFunction ::= TypeName . >>
            //
            case 456: {
               //#line 3851 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3849 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3851 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 457:  OperatorFunction ::= TypeName . >>>
            //
            case 457: {
               //#line 3862 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3860 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3862 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 458:  OperatorFunction ::= TypeName . <
            //
            case 458: {
               //#line 3873 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3871 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3873 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 459:  OperatorFunction ::= TypeName . <=
            //
            case 459: {
               //#line 3884 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3882 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3884 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 460:  OperatorFunction ::= TypeName . >=
            //
            case 460: {
               //#line 3895 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3893 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3895 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 461:  OperatorFunction ::= TypeName . >
            //
            case 461: {
               //#line 3906 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3904 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3906 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 462:  OperatorFunction ::= TypeName . ==
            //
            case 462: {
               //#line 3917 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3915 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3917 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 463:  OperatorFunction ::= TypeName . !=
            //
            case 463: {
               //#line 3928 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3926 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3928 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 464:  Literal ::= IntegerLiteral$lit
            //
            case 464: {
               //#line 3941 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3939 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3941 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 465:  Literal ::= LongLiteral$lit
            //
            case 465: {
               //#line 3947 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3945 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3947 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 466:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 466: {
               //#line 3953 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3951 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3953 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 467:  Literal ::= UnsignedLongLiteral$lit
            //
            case 467: {
               //#line 3959 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3957 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3959 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 468:  Literal ::= FloatingPointLiteral$lit
            //
            case 468: {
               //#line 3965 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3963 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3965 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 469:  Literal ::= DoubleLiteral$lit
            //
            case 469: {
               //#line 3971 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3969 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3971 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 470:  Literal ::= BooleanLiteral
            //
            case 470: {
               //#line 3977 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3975 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3977 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 471:  Literal ::= CharacterLiteral$lit
            //
            case 471: {
               //#line 3982 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3980 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3982 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 472:  Literal ::= StringLiteral$str
            //
            case 472: {
               //#line 3988 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3986 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3988 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 473:  Literal ::= null
            //
            case 473: {
               //#line 3994 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3994 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 474:  BooleanLiteral ::= true$trueLiteral
            //
            case 474: {
               //#line 4000 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3998 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 4000 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 475:  BooleanLiteral ::= false$falseLiteral
            //
            case 475: {
               //#line 4005 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4003 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 4005 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 476:  ArgumentList ::= Expression
            //
            case 476: {
               //#line 4014 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4012 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 4014 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 477:  ArgumentList ::= ArgumentList , Expression
            //
            case 477: {
               //#line 4021 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4019 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 4019 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4021 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 478:  FieldAccess ::= Primary . Identifier
            //
            case 478: {
               //#line 4027 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4025 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4025 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4027 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 479:  FieldAccess ::= super . Identifier
            //
            case 479: {
               //#line 4032 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4030 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4032 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 480:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 480: {
               //#line 4037 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4035 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4035 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4035 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4037 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 481:  FieldAccess ::= Primary . class$c
            //
            case 481: {
               //#line 4042 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4040 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4040 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4042 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 482:  FieldAccess ::= super . class$c
            //
            case 482: {
               //#line 4047 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4045 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4047 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 483:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 483: {
               //#line 4052 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4050 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4050 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4050 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 4052 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 484:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 484: {
               //#line 4058 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4056 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4056 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 4056 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 4058 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 485:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 485: {
               //#line 4065 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 4063 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 4065 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 486:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 486: {
               //#line 4070 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4068 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4068 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 4068 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 4070 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 487:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 487: {
               //#line 4075 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4073 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4073 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4073 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4073 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 4073 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 4075 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 488:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 488: {
               //#line 4080 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4078 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4078 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 4078 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 4080 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 489:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 489: {
               //#line 4100 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4098 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4098 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(4);
                //#line 4100 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 490:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 490: {
               //#line 4113 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4111 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4111 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4111 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4113 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 491:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 491: {
               //#line 4125 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4123 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4123 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4125 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 492:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 492: {
               //#line 4137 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4135 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4135 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4135 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4135 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(8);
                //#line 4137 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 494:  PostfixExpression ::= ExpressionName
            //
            case 494: {
               //#line 4152 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4150 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4152 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 497:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 497: {
               //#line 4160 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4158 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4160 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 498:  PostDecrementExpression ::= PostfixExpression --
            //
            case 498: {
               //#line 4166 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4164 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4166 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 501:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 501: {
               //#line 4174 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4172 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4174 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 502:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 502: {
               //#line 4179 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4177 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4179 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 504:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 504: {
               //#line 4186 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4184 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4186 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 505:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 505: {
               //#line 4192 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4190 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4192 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 507:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 507: {
               //#line 4199 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4197 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4199 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 508:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 508: {
               //#line 4204 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4202 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4202 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4204 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e.position(pos()));
                      break;
            }
    
            //
            // Rule 509:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 509: {
               //#line 4211 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4209 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4211 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 511:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 511: {
               //#line 4218 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4216 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4216 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4218 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 512:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 512: {
               //#line 4223 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4221 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4221 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4223 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 513:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 513: {
               //#line 4228 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4226 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4226 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4228 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 515:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 515: {
               //#line 4235 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4233 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4233 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4235 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 516:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 516: {
               //#line 4240 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4238 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4238 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4240 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 518:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 518: {
               //#line 4247 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4245 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4245 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4247 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 519:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 519: {
               //#line 4252 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4250 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4250 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4252 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 520:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 520: {
               //#line 4257 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4255 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4255 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4257 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 522:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 522: {
               //#line 4264 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4262 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4262 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4264 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 525:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 525: {
               //#line 4273 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4271 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4271 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4273 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 526:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 526: {
               //#line 4278 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4276 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4276 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4278 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 527:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 527: {
               //#line 4283 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4281 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4281 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4283 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 528:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 528: {
               //#line 4288 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4286 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4286 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4288 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 529:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 529: {
               //#line 4293 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4291 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4291 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4293 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 530:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 530: {
               //#line 4298 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4296 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4296 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4298 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 532:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 532: {
               //#line 4305 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4303 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4303 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4305 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 533:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 533: {
               //#line 4310 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4308 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4308 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4310 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 534:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 534: {
               //#line 4315 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4313 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4313 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4315 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 536:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 536: {
               //#line 4322 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4320 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4320 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4322 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 538:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 538: {
               //#line 4329 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4327 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4327 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4329 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 540:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 540: {
               //#line 4336 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4334 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4334 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4336 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 542:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 542: {
               //#line 4343 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4341 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4341 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4343 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 544:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 544: {
               //#line 4350 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4348 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4348 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4350 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 551:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 551: {
               //#line 4363 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4361 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4361 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4361 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4363 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 554:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 554: {
               //#line 4372 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4370 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4370 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4370 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4372 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 555:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 555: {
               //#line 4377 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4375 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4375 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4375 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4375 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4377 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 556:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 556: {
               //#line 4382 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4380 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4380 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4380 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4380 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4382 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 557:  LeftHandSide ::= ExpressionName
            //
            case 557: {
               //#line 4388 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4386 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4388 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 559:  AssignmentOperator ::= =
            //
            case 559: {
               //#line 4395 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4395 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 560:  AssignmentOperator ::= *=
            //
            case 560: {
               //#line 4400 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4400 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 561:  AssignmentOperator ::= /=
            //
            case 561: {
               //#line 4405 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4405 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 562:  AssignmentOperator ::= %=
            //
            case 562: {
               //#line 4410 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4410 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 563:  AssignmentOperator ::= +=
            //
            case 563: {
               //#line 4415 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4415 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 564:  AssignmentOperator ::= -=
            //
            case 564: {
               //#line 4420 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4420 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 565:  AssignmentOperator ::= <<=
            //
            case 565: {
               //#line 4425 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4425 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 566:  AssignmentOperator ::= >>=
            //
            case 566: {
               //#line 4430 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4430 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 567:  AssignmentOperator ::= >>>=
            //
            case 567: {
               //#line 4435 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4435 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 568:  AssignmentOperator ::= &=
            //
            case 568: {
               //#line 4440 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4440 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 569:  AssignmentOperator ::= ^=
            //
            case 569: {
               //#line 4445 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4445 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 570:  AssignmentOperator ::= |=
            //
            case 570: {
               //#line 4450 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4450 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 573:  PrefixOp ::= +
            //
            case 573: {
               //#line 4461 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4461 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 574:  PrefixOp ::= -
            //
            case 574: {
               //#line 4466 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4466 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 575:  PrefixOp ::= !
            //
            case 575: {
               //#line 4471 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4471 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 576:  PrefixOp ::= ~
            //
            case 576: {
               //#line 4476 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4476 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 577:  BinOp ::= +
            //
            case 577: {
               //#line 4482 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4482 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 578:  BinOp ::= -
            //
            case 578: {
               //#line 4487 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4487 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 579:  BinOp ::= *
            //
            case 579: {
               //#line 4492 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4492 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 580:  BinOp ::= /
            //
            case 580: {
               //#line 4497 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4497 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 581:  BinOp ::= %
            //
            case 581: {
               //#line 4502 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4502 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 582:  BinOp ::= &
            //
            case 582: {
               //#line 4507 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4507 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 583:  BinOp ::= |
            //
            case 583: {
               //#line 4512 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4512 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 584:  BinOp ::= ^
            //
            case 584: {
               //#line 4517 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4517 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 585:  BinOp ::= &&
            //
            case 585: {
               //#line 4522 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4522 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 586:  BinOp ::= ||
            //
            case 586: {
               //#line 4527 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4527 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 587:  BinOp ::= <<
            //
            case 587: {
               //#line 4532 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4532 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 588:  BinOp ::= >>
            //
            case 588: {
               //#line 4537 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4537 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 589:  BinOp ::= >>>
            //
            case 589: {
               //#line 4542 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4542 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 590:  BinOp ::= >=
            //
            case 590: {
               //#line 4547 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4547 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 591:  BinOp ::= <=
            //
            case 591: {
               //#line 4552 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4552 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 592:  BinOp ::= >
            //
            case 592: {
               //#line 4557 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4557 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 593:  BinOp ::= <
            //
            case 593: {
               //#line 4562 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4562 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 594:  BinOp ::= ==
            //
            case 594: {
               //#line 4570 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4570 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 595:  BinOp ::= !=
            //
            case 595: {
               //#line 4575 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4575 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 596:  Catchesopt ::= $Empty
            //
            case 596: {
               //#line 4584 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4584 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                      break;
            }
    
            //
            // Rule 598:  Identifieropt ::= $Empty
            //
            case 598:
                setResult(null);
                break;

            //
            // Rule 599:  Identifieropt ::= Identifier
            //
            case 599: {
               //#line 4593 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4591 "C:/eclipsews/head5/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4593 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 600:  ForUpdateopt ::= $Empty
            //
            case 600: {
               //#line 4599 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4599 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                      break;
            }
    
            //
            // Rule 602:  Expressionopt ::= $Empty
            //
            case 602:
                setResult(null);
                break;

            //
            // Rule 604:  ForInitopt ::= $Empty
            //
            case 604: {
               //#line 4610 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4610 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 606:  SwitchLabelsopt ::= $Empty
            //
            case 606: {
               //#line 4617 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4617 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 608:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 608: {
               //#line 4624 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4624 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 610:  VariableModifiersopt ::= $Empty
            //
            case 610: {
               //#line 4631 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4631 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 612:  VariableInitializersopt ::= $Empty
            //
            case 612:
                setResult(null);
                break;

            //
            // Rule 614:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 614: {
               //#line 4642 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4642 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 616:  ExtendsInterfacesopt ::= $Empty
            //
            case 616: {
               //#line 4649 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4649 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 618:  InterfaceModifiersopt ::= $Empty
            //
            case 618: {
               //#line 4656 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4656 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 620:  ClassBodyopt ::= $Empty
            //
            case 620:
                setResult(null);
                break;

            //
            // Rule 622:  Argumentsopt ::= $Empty
            //
            case 622: {
               //#line 4667 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4667 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 624:  ArgumentListopt ::= $Empty
            //
            case 624: {
               //#line 4674 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4674 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 626:  BlockStatementsopt ::= $Empty
            //
            case 626: {
               //#line 4681 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4681 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                      break;
            }
    
            //
            // Rule 628:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 628:
                setResult(null);
                break;

            //
            // Rule 630:  ConstructorModifiersopt ::= $Empty
            //
            case 630: {
               //#line 4692 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4692 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 632:  FormalParameterListopt ::= $Empty
            //
            case 632: {
               //#line 4699 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4699 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 634:  Throwsopt ::= $Empty
            //
            case 634: {
               //#line 4706 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4706 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 636:  Offersopt ::= $Empty
            //
            case 636: {
               //#line 4712 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4712 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 638:  MethodModifiersopt ::= $Empty
            //
            case 638: {
               //#line 4719 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4719 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 640:  TypeModifieropt ::= $Empty
            //
            case 640: {
               //#line 4726 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4726 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 642:  FieldModifiersopt ::= $Empty
            //
            case 642: {
               //#line 4733 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4733 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 644:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 644: {
               //#line 4740 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4740 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 646:  Interfacesopt ::= $Empty
            //
            case 646: {
               //#line 4747 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4747 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 648:  Superopt ::= $Empty
            //
            case 648:
                setResult(null);
                break;

            //
            // Rule 650:  TypeParametersopt ::= $Empty
            //
            case 650: {
               //#line 4758 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4758 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 652:  FormalParametersopt ::= $Empty
            //
            case 652: {
               //#line 4765 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4765 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 654:  Annotationsopt ::= $Empty
            //
            case 654: {
               //#line 4772 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4772 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 656:  TypeDeclarationsopt ::= $Empty
            //
            case 656: {
               //#line 4779 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4779 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 658:  ImportDeclarationsopt ::= $Empty
            //
            case 658: {
               //#line 4786 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4786 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                      break;
            }
    
            //
            // Rule 660:  PackageDeclarationopt ::= $Empty
            //
            case 660:
                setResult(null);
                break;

            //
            // Rule 662:  ResultTypeopt ::= $Empty
            //
            case 662:
                setResult(null);
                break;

            //
            // Rule 664:  HasResultTypeopt ::= $Empty
            //
            case 664:
                setResult(null);
                break;

            //
            // Rule 666:  TypeArgumentsopt ::= $Empty
            //
            case 666: {
               //#line 4804 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4804 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 668:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 668: {
               //#line 4811 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4811 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 670:  Propertiesopt ::= $Empty
            //
            case 670: {
               //#line 4818 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4818 "C:/eclipsews/head5/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 672:  ,opt ::= $Empty
            //
            case 672:
                setResult(null);
                break;

    
            default:
                break;
        }
        return;
    }
}

