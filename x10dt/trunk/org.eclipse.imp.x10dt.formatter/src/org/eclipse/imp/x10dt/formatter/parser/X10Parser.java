
// This is the grammar for parsing formatting patterns for  the X10 language.

//#line 18 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
//
// Licensed Material
// (C) Copyright IBM Corp, 2006
//

////////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2007 IBM Corporation.
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
//Contributors:
//    Philippe Charles (pcharles@us.ibm.com) - initial API and implementation

////////////////////////////////////////////////////////////////////////////////

package org.eclipse.imp.x10dt.formatter.parser;

import lpg.runtime.*;

// this is a test
 import org.eclipse.imp.parser.*;
 import x10.parser.X10ParsedName;
 import lpg.runtime.IMessageHandler;

//#line 28 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.io.File;

import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10Unary_c;
import polyglot.types.QName;
import polyglot.types.Name;
import polyglot.ast.AmbExpr;
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
import polyglot.parse.ParsedName;
import polyglot.ext.x10.ast.AnnotationNode;
import polyglot.ext.x10.ast.Closure;
import polyglot.ext.x10.ast.ClosureCall;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.Tuple;
import polyglot.ext.x10.ast.When;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10Formal_c;
import polyglot.ext.x10.ast.X10Loop;
import polyglot.ext.x10.ast.X10Call;
import polyglot.ext.x10.ast.ConstantDistMaker;
import polyglot.ext.x10.ast.TypeDecl;
import polyglot.ext.x10.ast.TypeParamNode;
import polyglot.ext.x10.ast.TypePropertyNode;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.ast.PropertyDecl;
import polyglot.ext.x10.ast.RegionMaker;
import polyglot.ext.x10.extension.X10Ext;
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
import polyglot.ext.x10.types.X10Flags;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
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
import lpg.runtime.LexStream;
import lpg.runtime.NotBacktrackParseTableException;
import lpg.runtime.NullExportedSymbolsException;
import lpg.runtime.NullTerminalSymbolsException;
import lpg.runtime.ParseTable;
import lpg.runtime.PrsStream;
import lpg.runtime.RuleAction;
import lpg.runtime.UndefinedEofSymbolException;
import lpg.runtime.UnimplementedTerminalsException;

public class X10Parser implements RuleAction , IParser, ParseErrorCodes 
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
    

    //#line 314 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
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

    Position pos = new Position(file.getPath(),
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
        return new Position(null, prsStream.getFileName(),
               prsStream.getLine(lefttok), prsStream.getColumn(lefttok),
               prsStream.getEndLine(righttok), prsStream.getEndColumn(righttok));
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

        public IToken getLeftIToken() { return leftIToken; }
        public IToken getRightIToken() { return rightIToken; }

        public String toText()
        {
            if (leftIToken == null) return "...";
            IPrsStream prsStream = leftIToken.getPrsStream();
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
                eq.enqueue(ErrorInfo.SYNTAX_ERROR, "Unable to parse " + source.name() + ".", new Position(null, file(), 1, 1, 1, 1));
            }   
        }
        catch (RuntimeException e) {
            // Let the Compiler catch and report it.
            throw e;
        }
        catch (Exception e) {
            // Used by cup to indicate a non-recoverable error.
            eq.enqueue(ErrorInfo.SYNTAX_ERROR, e.getMessage(), new Position(null, file(), 1, 1, 1, 1));
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
        String s = prsStream.getName(i);
        if (s != null && s.startsWith("/**") && s.endsWith("*/")) {
            return s +"\n";
        }
        return null;
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
                pos = pos == null ? fn.position() : new Position(pos, fn.position());
                Flags f = fn.flags();
                if (f instanceof X10Flags) {
                    xf = xf.setX((X10Flags) f);
                }
                else {
                    xf = X10Flags.toX10Flags(xf.set(f));
                }
            }
        }
        return nf.FlagsNode(pos == null ? Position.COMPILER_GENERATED : pos, xf);
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

    private polyglot.lex.LongLiteral int_lit(int i, int radix)
    {
        long x = parseLong(prsStream.getName(i), radix);
        return new LongLiteral(pos(i),  x, X10Parsersym.TK_IntegerLiteral);
    }

    private polyglot.lex.LongLiteral int_lit(int i)
    {
        long x = parseLong(prsStream.getName(i));
        return new LongLiteral(pos(i),  x, X10Parsersym.TK_IntegerLiteral);
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
               //#line 8 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 6 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 8 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 18 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 16 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 18 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 28 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 26 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 28 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 38 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 36 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 38 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 48 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 46 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 48 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 58 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 56 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 58 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 68 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 66 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 74 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 74 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 80 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 78 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 78 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 87 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 85 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 87 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 94 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 92 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 94 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 100 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 98 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 100 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 109 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 107 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 117 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 115 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                      break;
            }
    
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 122 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 120 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 120 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 877 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 875 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 875 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 875 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 875 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 875 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 875 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 877 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode f = extractFlags(TypeDefModifiersopt);
                List annotations = extractAnnotations(TypeDefModifiersopt);
                for (Formal v : (List<Formal>) FormalParametersopt) {
                    if (!v.flags().flags().isFinal()) throw new InternalCompilerError("Type definition parameters must be final.", v.position()); 
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
               //#line 889 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 887 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 887 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 887 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 887 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 887 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 889 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode f = extractFlags(TypeDefModifiersopt);
                List annotations = extractAnnotations(TypeDefModifiersopt);
                for (Formal v : (List<Formal>) FormalParametersopt) {
                    if (!v.flags().flags().isFinal()) throw new InternalCompilerError("Type definition parameters must be final.", v.position()); 
                }
                TypeDecl cd = nf.TypeDecl(pos(), f, Identifier, TypeParametersopt, FormalParametersopt, WhereClauseopt, null);
                cd = (TypeDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 18:  Properties ::= ( PropertyList )
            //
            case 18: {
               //#line 902 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 900 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 902 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
   setResult(PropertyList);
                 break;
            } 
            //
            // Rule 19:  PropertyList ::= Property
            //
            case 19: {
               //#line 907 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 905 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 907 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 20:  PropertyList ::= PropertyList , Property
            //
            case 20: {
               //#line 914 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 912 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 912 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 914 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 21:  Property ::= Annotationsopt Identifier : Type
            //
            case 21: {
               //#line 921 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 919 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 919 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 919 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(4);
                //#line 921 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List annotations = extractAnnotations(Annotationsopt);
                PropertyDecl cd = nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), Type, Identifier);
                cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 22:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 22: {
               //#line 930 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 928 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 928 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 928 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 928 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 928 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 928 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 928 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 928 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 930 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
       if (Identifier.id().toString().equals("this")) {
                   ConstructorDecl cd = nf.X10ConstructorDecl(pos(),
                                             extractFlags(MethodModifiersopt),
                                             nf.Id(pos(3), "this"),
                                             ResultTypeopt,
                                             TypeParametersopt,
                                             FormalParameters,
                                             WhereClauseopt,
                                             Throwsopt,
                                             MethodBody);
     cd = (ConstructorDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(MethodModifiersopt));
     setResult(cd);
          }
          else {
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(9)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          Identifier,
          TypeParametersopt,
          FormalParameters,
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
      }
                      break;
            }
    
            //
            // Rule 23:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 23: {
               //#line 960 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 958 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 958 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 958 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 958 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 958 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 958 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 958 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 958 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(13);
                //#line 958 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 960 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(14)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(7)), X10Binary_c.binaryMethodName(BinOp)),
          TypeParametersopt,
          Arrays.<Formal>asList(fp1, fp2),
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (! md.flags().flags().isStatic())
          syntaxError("Binary operator with two parameters must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 24:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 24: {
               //#line 977 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 975 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 975 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 975 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 975 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 975 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 975 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 975 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 975 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 977 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(11)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(4)), X10Unary_c.unaryMethodName(PrefixOp)),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp2),
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (! md.flags().flags().isStatic())
          syntaxError("Unary operator with two parameters must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 25:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 25: {
               //#line 994 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 992 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 992 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 992 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 992 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 992 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 992 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 992 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 992 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 994 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(12)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(5)), X10Binary_c.binaryMethodName(BinOp)),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp2),
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Binary operator with this parameter cannot be static.", md.position());
          
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 26:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 26: {
               //#line 1012 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1010 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1010 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1010 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1010 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1010 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1010 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1010 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1010 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1012 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
       Name op = X10Binary_c.invBinaryMethodName(BinOp);
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(12)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(7)), X10Binary_c.invBinaryMethodName(BinOp)),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp1),
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Binary operator with this parameter cannot be static.", md.position());
          
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 27:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 27: {
               //#line 1031 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1029 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1029 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1029 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1029 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1029 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1029 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1029 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1031 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(9)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(4)), X10Unary_c.unaryMethodName(PrefixOp)),
          TypeParametersopt,
          Collections.EMPTY_LIST,
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Unary operator with this parameter cannot be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 28:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 28: {
               //#line 1048 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1046 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1046 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1046 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1046 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1046 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1046 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1046 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1048 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(9)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(), Name.make("apply")),
          TypeParametersopt,
          FormalParameters,
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Apply operator cannot be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 29:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 29: {
               //#line 1065 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1063 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1063 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1063 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1063 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1063 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1063 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1063 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(12);
                //#line 1063 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1065 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(13)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(), Name.make("set")),
          TypeParametersopt,
          CollectionUtil.append(Collections.singletonList(fp2), FormalParameters),
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (md.flags().flags().isStatic())
          syntaxError("Set operator cannot be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 30:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Throwsopt MethodBody
            //
            case 30: {
               //#line 1082 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1080 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1080 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1080 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1080 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1080 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1080 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1080 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1082 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(11)),
          extractFlags(MethodModifiersopt),
          Type,
          nf.Id(pos(), Name.make("$convert")),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp1),
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (! md.flags().flags().isStatic())
          syntaxError("Conversion operator must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 31:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 31: {
               //#line 1099 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1097 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1097 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1097 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1097 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1097 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1097 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1097 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1099 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(12)),
          extractFlags(MethodModifiersopt),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          nf.Id(pos(), Name.make("")),
          TypeParametersopt,
          Collections.<Formal>singletonList(fp1),
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      if (! md.flags().flags().isStatic())
          syntaxError("Conversion operator must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 32:  PropertyMethodDeclaration ::= MethodModifiersopt property Identifier TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt MethodBody
            //
            case 32: {
               //#line 1118 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1116 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1116 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1116 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1116 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1116 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1116 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1116 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1116 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1118 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(9)),
          extractFlags(MethodModifiersopt, X10Flags.PROPERTY),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          Identifier,
          TypeParametersopt,
          FormalParameters,
          WhereClauseopt,
          Throwsopt,
          MethodBody);
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 33:  PropertyMethodDeclaration ::= MethodModifiersopt property Identifier WhereClauseopt ResultTypeopt MethodBody
            //
            case 33: {
               //#line 1133 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1131 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1131 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1131 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(4);
                //#line 1131 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 1131 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(6);
                //#line 1133 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodDecl md = nf.X10MethodDecl(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(6)),
          extractFlags(MethodModifiersopt, X10Flags.PROPERTY),
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt,
          Identifier,
          Collections.EMPTY_LIST,
          Collections.EMPTY_LIST,
          WhereClauseopt,
          Collections.EMPTY_LIST,
          MethodBody);
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 34:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 34: {
               //#line 1149 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1147 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1147 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1149 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 35:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 35: {
               //#line 1154 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1152 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1152 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1154 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 36:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 36: {
               //#line 1159 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1157 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1157 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1157 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1159 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 37:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 37: {
               //#line 1164 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1162 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1162 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1162 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1164 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 38:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypePropertiesopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 38: {
               //#line 1170 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1168 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 1168 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1168 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(4);
                //#line 1168 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1168 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1168 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 1168 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1170 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
      checkTypeName(Identifier);
      List TypeParametersopt = Collections.EMPTY_LIST;
      List/*<PropertyDecl>*/ props = Propertiesopt;
      DepParameterExpr ci = WhereClauseopt;
      FlagsNode fn = extractFlags(InterfaceModifiersopt, Flags.INTERFACE);
      ClassDecl cd = nf.X10ClassDecl(pos(),
                   fn,
                   Identifier,
                   TypeParametersopt,
                   TypePropertiesopt,
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
               //#line 1192 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1190 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1190 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1190 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1190 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1192 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 40:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 40: {
               //#line 1199 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1197 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1197 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1197 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1197 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1197 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1199 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1207 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1205 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1205 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1205 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1205 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1205 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1207 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1216 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1214 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1214 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1216 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 45:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt => Type
            //
            case 45: {
               //#line 1228 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1226 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 1226 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1226 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1226 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1226 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1228 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt));
                      break;
            }
    
            //
            // Rule 50:  AnnotatedType ::= Type Annotations
            //
            case 50: {
               //#line 1237 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1235 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1235 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1237 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn);
                      break;
            }
    
            //
            // Rule 54:  ConstrainedType ::= ( Type )
            //
            case 54: {
               //#line 1248 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1246 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1248 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 55:  PlaceType ::= any
            //
            case 55: {
               //#line 1254 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1254 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 56:  PlaceType ::= current
            //
            case 56: {
               //#line 1259 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1259 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(),
                                    nf.Field(pos(), nf.This(pos()), nf.Id(pos(), "loc")), Binary.EQ,
                                    nf.Field(pos(), nf.Self(pos()), nf.Id(pos(), "$current"))));
                      break;
            }
    
            //
            // Rule 57:  PlaceType ::= PlaceExpression
            //
            case 57: {
               //#line 1266 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1264 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(1);
                //#line 1266 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(),
                                    nf.Field(pos(), nf.This(pos()), nf.Id(pos(), "loc")), Binary.EQ,
                                    PlaceExpression));
                      break;
            }
    
            //
            // Rule 58:  NamedType ::= Primary . Identifier TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 58: {
               //#line 1274 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1272 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1272 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1272 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1272 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1272 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1274 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.X10AmbTypeNode(pos(), Primary, Identifier);
            // TODO: place constraint
            if (DepParametersopt != null || (TypeArgumentsopt != null && ! TypeArgumentsopt.isEmpty()) || (Argumentsopt != null && ! Argumentsopt.isEmpty())) {
                type = nf.AmbDepTypeNode(pos(), Primary, Identifier, TypeArgumentsopt, Argumentsopt, DepParametersopt);
            }
            setResult(type);
                      break;
            }
    
            //
            // Rule 59:  NamedType ::= TypeName TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 59: {
               //#line 1285 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1283 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1283 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1283 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(3);
                //#line 1283 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                //#line 1285 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 60:  DepParameters ::= { ExistentialListopt Conjunction }
            //
            case 60: {
               //#line 1311 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1309 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1309 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(3);
                //#line 1311 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
                      break;
            }
    
            //
            // Rule 61:  DepParameters ::= { ExistentialListopt Conjunction } !
            //
            case 61: {
               //#line 1316 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1314 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1314 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(3);
                //#line 1316 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
                      break;
            }
    
            //
            // Rule 62:  DepParameters ::= { ExistentialListopt Conjunction } ! PlaceType
            //
            case 62: {
               //#line 1321 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1319 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1319 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(3);
                //#line 1319 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(6);
                //#line 1321 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                if (PlaceType != null)
                    setResult(nf.DepParameterExpr(pos(), ExistentialListopt, nf.Binary(pos(), Conjunction, Binary.COND_AND, PlaceType)));
            else
		setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunction));
                      break;
            }
    
            //
            // Rule 63:  TypeProperties ::= [ TypePropertyList ]
            //
            case 63: {
               //#line 1330 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1328 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypePropertyList = (List) getRhsSym(2);
                //#line 1330 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypePropertyList);
                      break;
            }
    
            //
            // Rule 64:  TypeParameters ::= [ TypeParameterList ]
            //
            case 64: {
               //#line 1336 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1334 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1336 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 65:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 65: {
               //#line 1341 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1339 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1341 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 66:  Conjunction ::= Expression
            //
            case 66: {
               //#line 1347 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1345 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1347 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Expression);
                      break;
            }
    
            //
            // Rule 67:  Conjunction ::= Conjunction , Expression
            //
            case 67: {
               //#line 1352 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1350 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Conjunction = (Expr) getRhsSym(1);
                //#line 1350 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1352 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), Conjunction, Binary.COND_AND, Expression));
                      break;
            }
    
            //
            // Rule 68:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 68: {
               //#line 1358 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1356 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1356 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1358 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 69:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 69: {
               //#line 1363 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1361 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1361 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1363 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 70:  WhereClause ::= DepParameters
            //
            case 70: {
               //#line 1369 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1367 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1369 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 71:  ExistentialListopt ::= $Empty
            //
            case 71: {
               //#line 1375 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1375 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 72:  ExistentialListopt ::= ExistentialList ;
            //
            case 72: {
               //#line 1380 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1378 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1380 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 73:  ExistentialList ::= FormalParameter
            //
            case 73: {
               //#line 1386 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1384 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1386 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 74:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 74: {
               //#line 1393 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1391 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1391 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1393 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 77:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypePropertiesopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 77: {
               //#line 1404 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1402 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1402 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1402 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(4);
                //#line 1402 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1402 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1402 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1402 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1402 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1404 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
      checkTypeName(Identifier);
                List TypeParametersopt = Collections.EMPTY_LIST;
      List/*<PropertyDecl>*/ props = Propertiesopt;
      DepParameterExpr ci = WhereClauseopt;
      FlagsNode f = extractFlags(ClassModifiersopt);
      List annotations = extractAnnotations(ClassModifiersopt);
      ClassDecl cd = nf.X10ClassDecl(pos(),
              f, Identifier, TypeParametersopt, TypePropertiesopt, props, ci, Superopt, Interfacesopt, ClassBody);
      cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(annotations);
      setResult(cd);
                      break;
            }
    
            //
            // Rule 78:  ValueClassDeclaration ::= ClassModifiersopt value Identifier TypePropertiesopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 78: {
               //#line 1419 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1417 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1417 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1417 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypePropertiesopt = (List) getRhsSym(4);
                //#line 1417 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1417 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1417 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1417 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1417 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1419 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
    checkTypeName(Identifier);
                List TypeParametersopt = Collections.EMPTY_LIST;
    List props = Propertiesopt;
    DepParameterExpr ci = WhereClauseopt;
    ClassDecl cd = (nf.X10ClassDecl(pos(getLeftSpan(), getRightSpan()),
    extractFlags(ClassModifiersopt, X10Flags.VALUE), Identifier,  TypeParametersopt,
    TypePropertiesopt, props, ci, Superopt, Interfacesopt, ClassBody));
    cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ClassModifiersopt));
    setResult(cd);
                      break;
            }
    
            //
            // Rule 79:  ConstructorDeclaration ::= ConstructorModifiersopt def this TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt ConstructorBody
            //
            case 79: {
               //#line 1433 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1431 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1431 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1431 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1431 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1431 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1431 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1431 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(9);
                //#line 1433 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
     ConstructorDecl cd = nf.X10ConstructorDecl(pos(),
                                             extractFlags(ConstructorModifiersopt),
                                             nf.Id(pos(3), "this"),
                                             ResultTypeopt,
                                             TypeParametersopt,
                                             FormalParameters,
                                             WhereClauseopt,
                                             Throwsopt,
                                             ConstructorBody);
     cd = (ConstructorDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(ConstructorModifiersopt));
     setResult(cd);
                     break;
            }
   
            //
            // Rule 80:  Super ::= extends ClassType
            //
            case 80: {
               //#line 1449 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1447 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1449 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 81:  FieldKeyword ::= val
            //
            case 81: {
               //#line 1455 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1455 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 82:  FieldKeyword ::= var
            //
            case 82: {
               //#line 1460 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1460 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 83:  FieldKeyword ::= const
            //
            case 83: {
               //#line 1465 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1465 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                      break;
            }
    
            //
            // Rule 84:  VarKeyword ::= val
            //
            case 84: {
               //#line 1473 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1473 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 85:  VarKeyword ::= var
            //
            case 85: {
               //#line 1478 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1478 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 86:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 86: {
               //#line 1485 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1483 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1483 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1483 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1485 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
                        FieldDecl ld = nf.FieldDecl(pos, fn,
                                           type, name, init);
                        ld = (FieldDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(FieldModifiersopt));
                        l.add(ld);
                    }
                setResult(l);
                      break;
            }
    
            //
            // Rule 87:  FieldDeclaration ::= FieldModifiersopt FieldDeclarators ;
            //
            case 87: {
               //#line 1510 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1508 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1508 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 1510 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
                        FieldDecl ld = nf.FieldDecl(pos, fn,
                                           type, name, init);
                        ld = (FieldDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(FieldModifiersopt));
                        l.add(ld);
                    }
                setResult(l);
                      break;
            }
    
            //
            // Rule 118:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 118: {
               //#line 1569 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1567 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1567 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1569 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 119:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 119: {
               //#line 1575 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1573 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1573 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1573 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1575 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 120:  EmptyStatement ::= ;
            //
            case 120: {
               //#line 1581 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1581 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 121:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 121: {
               //#line 1587 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1585 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1585 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 1587 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 127:  ExpressionStatement ::= StatementExpression ;
            //
            case 127: {
               //#line 1599 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1597 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1599 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                boolean eval = true;
                if (StatementExpression instanceof X10Call) {
                    X10Call c = (X10Call) StatementExpression;
                    if (c.name().id().toString().equals("property") && c.target() == null) {
                        setResult(nf.AssignPropertyCall(c.position(),c.typeArguments(), c.arguments()));
                        eval = false;
                    }
                    if (c.name().id().toString().equals("super") && c.target() instanceof Expr) {
                        setResult(nf.X10SuperCall(c.position(), (Expr) c.target(), c.typeArguments(), c.arguments()));
                        eval = false;
                   }
                   if (c.name().id().toString().equals("this") && c.target() instanceof Expr) {
                        setResult(nf.X10ThisCall(c.position(), (Expr) c.target(), c.typeArguments(), c.arguments()));
                        eval = false;
                   }
                }
                    
                setResult(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 135:  AssertStatement ::= assert Expression ;
            //
            case 135: {
               //#line 1630 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1628 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1630 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 136:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 136: {
               //#line 1635 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1633 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1633 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1635 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 137:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 137: {
               //#line 1641 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1639 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1639 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1641 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 138:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 138: {
               //#line 1647 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1645 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1645 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1647 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 140:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 140: {
               //#line 1655 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1653 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1653 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1655 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 141:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 141: {
               //#line 1662 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1660 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1660 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1662 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 142:  SwitchLabels ::= SwitchLabel
            //
            case 142: {
               //#line 1671 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1669 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1671 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 143:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 143: {
               //#line 1678 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1676 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1676 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1678 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 144:  SwitchLabel ::= case ConstantExpression :
            //
            case 144: {
               //#line 1685 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1683 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1685 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 145:  SwitchLabel ::= default :
            //
            case 145: {
               //#line 1690 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1690 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 146:  WhileStatement ::= while ( Expression ) Statement
            //
            case 146: {
               //#line 1696 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1694 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1694 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1696 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 147:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 147: {
               //#line 1702 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1700 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1700 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1702 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 150:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 150: {
               //#line 1711 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1709 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1709 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1709 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1709 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1711 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 152:  ForInit ::= LocalVariableDeclaration
            //
            case 152: {
               //#line 1718 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1716 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1718 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 154:  StatementExpressionList ::= StatementExpression
            //
            case 154: {
               //#line 1728 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1726 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1728 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 155:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 155: {
               //#line 1735 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1733 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1733 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1735 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 156:  BreakStatement ::= break Identifieropt ;
            //
            case 156: {
               //#line 1741 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1739 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1741 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 157:  ContinueStatement ::= continue Identifieropt ;
            //
            case 157: {
               //#line 1747 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1745 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1747 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 158:  ReturnStatement ::= return Expressionopt ;
            //
            case 158: {
               //#line 1753 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1751 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1753 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 159:  ThrowStatement ::= throw Expression ;
            //
            case 159: {
               //#line 1759 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1757 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1759 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 160:  TryStatement ::= try Block Catches
            //
            case 160: {
               //#line 1765 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1763 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1763 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1765 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 161:  TryStatement ::= try Block Catchesopt Finally
            //
            case 161: {
               //#line 1770 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1768 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1768 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1768 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1770 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 162:  Catches ::= CatchClause
            //
            case 162: {
               //#line 1776 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1774 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1776 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 163:  Catches ::= Catches CatchClause
            //
            case 163: {
               //#line 1783 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1781 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1781 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1783 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 164:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 164: {
               //#line 1790 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1788 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1788 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1790 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 165:  Finally ::= finally Block
            //
            case 165: {
               //#line 1796 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1794 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1796 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 166:  NowStatement ::= now ( Clock ) Statement
            //
            case 166: {
               //#line 1802 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1800 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1800 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1802 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Now(pos(), Clock, Statement));
                      break;
            }
    
            //
            // Rule 167:  ClockedClause ::= clocked ( ClockList )
            //
            case 167: {
               //#line 1808 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1806 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1808 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 168:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 168: {
               //#line 1814 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1812 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1812 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1812 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1814 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 169:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 169: {
               //#line 1823 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1821 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1821 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1823 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 170:  AtomicStatement ::= atomic Statement
            //
            case 170: {
               //#line 1829 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1827 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1829 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 171:  WhenStatement ::= when ( Expression ) Statement
            //
            case 171: {
               //#line 1836 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1834 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1834 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1836 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 172:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 172: {
               //#line 1841 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1839 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1839 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1839 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1839 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1841 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 173:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 173: {
               //#line 1848 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1846 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1846 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1846 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1846 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1848 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                Flags f = fn.flags();
                f = f.Final();
                fn = fn.flags(f);
                setResult(nf.ForEach(pos(),
                              LoopIndex.flags(fn),
                              Expression,
                              ClockedClauseopt,
                              Statement));
                      break;
            }
    
            //
            // Rule 174:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 174: {
               //#line 1862 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1860 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1860 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1860 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1860 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1862 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                Flags f = fn.flags();
                f = f.Final();
                fn = fn.flags(f);
                setResult(nf.AtEach(pos(),
                             LoopIndex.flags(fn),
                             Expression,
                             ClockedClauseopt,
                             Statement));
                      break;
            }
    
            //
            // Rule 175:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 175: {
               //#line 1876 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1874 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1874 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1874 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1876 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                Flags f = fn.flags();
                f = f.Final();
                fn = fn.flags(f);
                setResult(nf.ForLoop(pos(),
                        LoopIndex.flags(fn),
                        Expression,
                        Statement));
                      break;
            }
    
            //
            // Rule 176:  FinishStatement ::= finish Statement
            //
            case 176: {
               //#line 1889 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1887 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1889 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 177:  AnnotationStatement ::= Annotations Statement
            //
            case 177: {
               //#line 1896 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1894 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 1894 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1896 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                if (Statement.ext() instanceof X10Ext && Annotations instanceof List) {
                    Statement = (Stmt) ((X10Ext) Statement.ext()).annotations((List) Annotations);
                }
                setResult(Statement);
                      break;
            }
    
            //
            // Rule 178:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 178: {
               //#line 1905 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1903 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1905 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 180:  NextStatement ::= next ;
            //
            case 180: {
               //#line 1913 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1913 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 181:  AwaitStatement ::= await Expression ;
            //
            case 181: {
               //#line 1919 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1917 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1919 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Await(pos(), Expression));
                      break;
            }
    
            //
            // Rule 182:  ClockList ::= Clock
            //
            case 182: {
               //#line 1925 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1923 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1925 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 183:  ClockList ::= ClockList , Clock
            //
            case 183: {
               //#line 1932 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1930 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1930 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1932 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 184:  Clock ::= Expression
            //
            case 184: {
               //#line 1940 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1938 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1940 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 185:  CastExpression ::= CastExpression as Type
            //
            case 185: {
               //#line 1954 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1952 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 1952 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 1954 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 186:  CastExpression ::= ConditionalExpression ! Expression
            //
            case 186: {
               //#line 1959 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1957 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 1957 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1959 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.PlaceCast(pos(), Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 188:  TypePropertyList ::= TypeProperty
            //
            case 188: {
               //#line 1968 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1966 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypePropertyNode TypeProperty = (TypePropertyNode) getRhsSym(1);
                //#line 1968 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypePropertyNode.class, false);
                l.add(TypeProperty);
                setResult(l);
                      break;
            }
    
            //
            // Rule 189:  TypePropertyList ::= TypePropertyList , TypeProperty
            //
            case 189: {
               //#line 1975 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1973 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypePropertyList = (List) getRhsSym(1);
                //#line 1973 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypePropertyNode TypeProperty = (TypePropertyNode) getRhsSym(3);
                //#line 1975 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                TypePropertyList.add(TypeProperty);
                setResult(TypePropertyList);
                      break;
            }
    
            //
            // Rule 190:  TypeParameterList ::= TypeParameter
            //
            case 190: {
               //#line 1982 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1980 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 1982 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 191:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 191: {
               //#line 1989 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1987 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 1987 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 1989 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 192:  TypeProperty ::= Identifier
            //
            case 192: {
               //#line 1996 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1994 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1996 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 193:  TypeProperty ::= + Identifier
            //
            case 193: {
               //#line 2001 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1999 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2001 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 194:  TypeProperty ::= - Identifier
            //
            case 194: {
               //#line 2006 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2004 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2006 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypePropertyNode(pos(), Identifier, TypeProperty.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 195:  TypeParameter ::= Identifier
            //
            case 195: {
               //#line 2012 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2010 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2012 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 196:  Primary ::= here
            //
            case 196: {
               //#line 2018 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2018 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 198:  RegionExpressionList ::= RegionExpression
            //
            case 198: {
               //#line 2026 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2024 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 2026 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 199:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 199: {
               //#line 2033 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2031 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 2031 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 2033 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                      break;
            }
    
            //
            // Rule 200:  Primary ::= [ ArgumentListopt ]
            //
            case 200: {
               //#line 2040 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2038 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 2040 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 201:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 201: {
               //#line 2047 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2045 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2045 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2047 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 202:  ClosureExpression ::= TypeParametersopt FormalParameters WhereClauseopt ResultTypeopt Throwsopt => ClosureBody
            //
            case 202: {
               //#line 2054 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2052 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(1);
                //#line 2052 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameters = (List) getRhsSym(2);
                //#line 2052 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(3);
                //#line 2052 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2052 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Throwsopt = (List) getRhsSym(5);
                //#line 2052 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 2054 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), TypeParametersopt, FormalParameters, WhereClauseopt, 
          ResultTypeopt == null ? nf.UnknownTypeNode(pos()) : ResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 203:  LastExpression ::= Expression
            //
            case 203: {
               //#line 2061 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2059 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2061 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 204:  ClosureBody ::= CastExpression
            //
            case 204: {
               //#line 2067 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2065 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2067 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), CastExpression, true)));
                      break;
            }
    
            //
            // Rule 205:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 205: {
               //#line 2072 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2070 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2070 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2070 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2072 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 206:  ClosureBody ::= Annotationsopt Block
            //
            case 206: {
               //#line 2082 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2080 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2080 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2082 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 207:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 207: {
               //#line 2091 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2089 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2089 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2091 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 208:  AsyncExpression ::= async ClosureBody
            //
            case 208: {
               //#line 2097 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2095 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2097 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 209:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 209: {
               //#line 2102 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2100 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2100 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2102 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 210:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 210: {
               //#line 2107 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2105 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2105 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2107 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 211:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 211: {
               //#line 2112 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2110 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2110 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2110 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2112 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 212:  FutureExpression ::= future ClosureBody
            //
            case 212: {
               //#line 2118 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2116 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2118 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 213:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 213: {
               //#line 2123 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2121 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2121 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2123 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 214:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 214: {
               //#line 2128 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2126 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2126 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2128 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 215:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 215: {
               //#line 2133 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2131 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2131 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2131 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2133 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2144 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2144 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 222:  ObjectKindopt ::= $Empty
            //
            case 222:
                setResult(null);
                break;

            //
            // Rule 224:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 224:
                setResult(null);
                break;

            //
            // Rule 226:  ClassModifiersopt ::= $Empty
            //
            case 226: {
               //#line 2163 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2163 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 228:  TypeDefModifiersopt ::= $Empty
            //
            case 228: {
               //#line 2169 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2169 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 230:  Unsafeopt ::= $Empty
            //
            case 230:
                setResult(null);
                break;

            //
            // Rule 231:  Unsafeopt ::= unsafe
            //
            case 231: {
               //#line 2177 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2177 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                // any value distinct from null
                setResult(this);
                      break;
            }
    
            //
            // Rule 232:  ClockedClauseopt ::= $Empty
            //
            case 232: {
               //#line 2184 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2184 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 234:  identifier ::= IDENTIFIER$ident
            //
            case 234: {
               //#line 2195 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2193 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2195 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 235:  TypeName ::= Identifier
            //
            case 235: {
               //#line 2202 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2200 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2202 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 236:  TypeName ::= TypeName . Identifier
            //
            case 236: {
               //#line 2207 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2205 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2205 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2207 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 238:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 238: {
               //#line 2219 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2217 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2219 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 239:  TypeArgumentList ::= Type
            //
            case 239: {
               //#line 2226 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2224 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2226 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 240:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 240: {
               //#line 2233 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2231 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2231 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2233 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 241:  PackageName ::= Identifier
            //
            case 241: {
               //#line 2243 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2241 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2243 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 242:  PackageName ::= PackageName . Identifier
            //
            case 242: {
               //#line 2248 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2246 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2246 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2248 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 243:  ExpressionName ::= Identifier
            //
            case 243: {
               //#line 2264 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2262 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2264 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 244:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 244: {
               //#line 2269 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2267 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2267 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2269 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 245:  MethodName ::= Identifier
            //
            case 245: {
               //#line 2279 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2277 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2279 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 246:  MethodName ::= AmbiguousName . Identifier
            //
            case 246: {
               //#line 2284 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2282 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2282 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2284 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 247:  PackageOrTypeName ::= Identifier
            //
            case 247: {
               //#line 2294 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2292 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2294 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 248:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 248: {
               //#line 2299 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2297 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2297 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2299 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 249:  AmbiguousName ::= Identifier
            //
            case 249: {
               //#line 2309 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2307 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2309 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 250:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 250: {
               //#line 2314 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2312 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2312 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2314 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 251:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 251: {
               //#line 2326 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2324 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2324 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2324 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2326 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 252:  ImportDeclarations ::= ImportDeclaration
            //
            case 252: {
               //#line 2342 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2340 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2342 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 253:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 253: {
               //#line 2349 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2347 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2347 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2349 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 254:  TypeDeclarations ::= TypeDeclaration
            //
            case 254: {
               //#line 2357 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2355 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2357 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 255:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 255: {
               //#line 2365 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2363 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2363 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2365 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 256:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 256: {
               //#line 2373 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2371 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2371 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2373 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn);
                      break;
            }
    
            //
            // Rule 259:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 259: {
               //#line 2387 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2385 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2387 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 260:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 260: {
               //#line 2393 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2391 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2393 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 264:  TypeDeclaration ::= ;
            //
            case 264: {
               //#line 2408 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2408 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 265:  ClassModifiers ::= ClassModifier
            //
            case 265: {
               //#line 2416 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2414 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2416 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 266:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 266: {
               //#line 2423 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2421 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2421 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2423 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassModifiers.addAll(ClassModifier);
                      break;
            }
    
            //
            // Rule 267:  ClassModifier ::= Annotation
            //
            case 267: {
               //#line 2429 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2427 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2429 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 268:  ClassModifier ::= public
            //
            case 268: {
               //#line 2434 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2434 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 269:  ClassModifier ::= protected
            //
            case 269: {
               //#line 2439 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2439 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 270:  ClassModifier ::= private
            //
            case 270: {
               //#line 2444 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2444 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 271:  ClassModifier ::= abstract
            //
            case 271: {
               //#line 2449 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2449 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 272:  ClassModifier ::= static
            //
            case 272: {
               //#line 2454 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2454 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 273:  ClassModifier ::= final
            //
            case 273: {
               //#line 2459 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2459 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 274:  ClassModifier ::= strictfp
            //
            case 274: {
               //#line 2464 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2464 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 275:  ClassModifier ::= safe
            //
            case 275: {
               //#line 2469 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2469 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 276:  ClassModifier ::= value
            //
            case 276: {
               //#line 2474 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2474 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.VALUE)));
                      break;
            }
    
            //
            // Rule 277:  TypeDefModifiers ::= TypeDefModifier
            //
            case 277: {
               //#line 2480 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2478 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2480 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 278:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 278: {
               //#line 2487 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2485 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2485 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2487 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                      break;
            }
    
            //
            // Rule 279:  TypeDefModifier ::= Annotation
            //
            case 279: {
               //#line 2493 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2491 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2493 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 280:  TypeDefModifier ::= public
            //
            case 280: {
               //#line 2498 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2498 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 281:  TypeDefModifier ::= protected
            //
            case 281: {
               //#line 2503 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2503 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 282:  TypeDefModifier ::= private
            //
            case 282: {
               //#line 2508 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2508 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 283:  TypeDefModifier ::= abstract
            //
            case 283: {
               //#line 2513 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2513 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 284:  TypeDefModifier ::= static
            //
            case 284: {
               //#line 2518 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2518 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 285:  TypeDefModifier ::= final
            //
            case 285: {
               //#line 2523 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2523 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 286:  Interfaces ::= implements InterfaceTypeList
            //
            case 286: {
               //#line 2532 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2530 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2532 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 287:  InterfaceTypeList ::= Type
            //
            case 287: {
               //#line 2538 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2536 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2538 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 288:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 288: {
               //#line 2545 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2543 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2543 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2545 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 289:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 289: {
               //#line 2555 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2553 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2555 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 291:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 291: {
               //#line 2562 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2560 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2560 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2562 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 293:  ClassBodyDeclaration ::= InstanceInitializer
            //
            case 293: {
               //#line 2570 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2568 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Initializer InstanceInitializer = (Initializer) getRhsSym(1);
                //#line 2570 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InstanceInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 294:  ClassBodyDeclaration ::= StaticInitializer
            //
            case 294: {
               //#line 2577 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2575 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Initializer StaticInitializer = (Initializer) getRhsSym(1);
                //#line 2577 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(StaticInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 295:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 295: {
               //#line 2584 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2582 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2584 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 297:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 297: {
               //#line 2593 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2591 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2593 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 298:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 298: {
               //#line 2600 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2598 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2600 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 299:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 299: {
               //#line 2607 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2605 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2607 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 300:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 300: {
               //#line 2614 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2612 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2614 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 301:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 301: {
               //#line 2621 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2619 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2621 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 302:  ClassMemberDeclaration ::= ;
            //
            case 302: {
               //#line 2628 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2628 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 303:  FormalDeclarators ::= FormalDeclarator
            //
            case 303: {
               //#line 2635 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2633 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2635 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 304:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 304: {
               //#line 2642 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2640 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2640 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2642 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 305:  FieldDeclarators ::= FieldDeclarator
            //
            case 305: {
               //#line 2649 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2647 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2649 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 306:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 306: {
               //#line 2656 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2654 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2654 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2656 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 307:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 307: {
               //#line 2664 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2662 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 2664 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 308:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 308: {
               //#line 2671 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2669 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 2669 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 2671 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 309:  VariableDeclarators ::= VariableDeclarator
            //
            case 309: {
               //#line 2678 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2676 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2678 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 310:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 310: {
               //#line 2685 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2683 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2683 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2685 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 312:  FieldModifiers ::= FieldModifier
            //
            case 312: {
               //#line 2694 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2692 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2694 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 313:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 313: {
               //#line 2701 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2699 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2699 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2701 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldModifiers.addAll(FieldModifier);
                      break;
            }
    
            //
            // Rule 314:  FieldModifier ::= Annotation
            //
            case 314: {
               //#line 2707 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2705 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2707 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 315:  FieldModifier ::= public
            //
            case 315: {
               //#line 2712 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2712 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 316:  FieldModifier ::= protected
            //
            case 316: {
               //#line 2717 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2717 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 317:  FieldModifier ::= private
            //
            case 317: {
               //#line 2722 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2722 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 318:  FieldModifier ::= static
            //
            case 318: {
               //#line 2727 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2727 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 319:  FieldModifier ::= transient
            //
            case 319: {
               //#line 2732 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2732 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.TRANSIENT)));
                      break;
            }
    
            //
            // Rule 320:  FieldModifier ::= volatile
            //
            case 320: {
               //#line 2737 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2737 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.VOLATILE)));
                      break;
            }
    
            //
            // Rule 321:  ResultType ::= : Type
            //
            case 321: {
               //#line 2743 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2741 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2743 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 322:  FormalParameters ::= ( FormalParameterList )
            //
            case 322: {
               //#line 2749 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2747 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2749 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterList);
                      break;
            }
    
            //
            // Rule 323:  FormalParameterList ::= FormalParameter
            //
            case 323: {
               //#line 2755 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2753 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2755 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 324:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 324: {
               //#line 2762 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2760 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2760 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2762 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 325:  LoopIndexDeclarator ::= Identifier ResultTypeopt
            //
            case 325: {
               //#line 2768 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2766 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2766 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2768 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 326:  LoopIndexDeclarator ::= ( IdentifierList ) ResultTypeopt
            //
            case 326: {
               //#line 2773 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2771 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2771 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2773 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 327:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt
            //
            case 327: {
               //#line 2778 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2776 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2776 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2776 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2778 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 328:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 328: {
               //#line 2784 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2782 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2782 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2784 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 329:  LoopIndex ::= VariableModifiersopt VarKeyword LoopIndexDeclarator
            //
            case 329: {
               //#line 2807 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2805 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2805 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2805 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2807 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 330:  FormalParameter ::= VariableModifiersopt FormalDeclarator
            //
            case 330: {
               //#line 2831 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2829 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2829 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2831 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 331:  FormalParameter ::= VariableModifiersopt VarKeyword FormalDeclarator
            //
            case 331: {
               //#line 2855 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2853 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2853 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2853 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2855 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 332:  FormalParameter ::= Type
            //
            case 332: {
               //#line 2879 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2877 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2879 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh()), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 333:  VariableModifiers ::= VariableModifier
            //
            case 333: {
               //#line 2887 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2885 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2887 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 334:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 334: {
               //#line 2894 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2892 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2892 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2894 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableModifiers.addAll(VariableModifier);
                      break;
            }
    
            //
            // Rule 335:  VariableModifier ::= Annotation
            //
            case 335: {
               //#line 2900 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2898 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2900 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 336:  VariableModifier ::= shared
            //
            case 336: {
               //#line 2905 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2905 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                      break;
            }
    
            //
            // Rule 337:  MethodModifiers ::= MethodModifier
            //
            case 337: {
               //#line 2914 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2912 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2914 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 338:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 338: {
               //#line 2921 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2919 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 2919 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 2921 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiers.addAll(MethodModifier);
                      break;
            }
    
            //
            // Rule 339:  MethodModifier ::= Annotation
            //
            case 339: {
               //#line 2927 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2925 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2927 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 340:  MethodModifier ::= public
            //
            case 340: {
               //#line 2932 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2932 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 341:  MethodModifier ::= protected
            //
            case 341: {
               //#line 2937 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2937 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 342:  MethodModifier ::= private
            //
            case 342: {
               //#line 2942 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2942 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 343:  MethodModifier ::= abstract
            //
            case 343: {
               //#line 2947 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2947 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 344:  MethodModifier ::= static
            //
            case 344: {
               //#line 2952 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2952 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 345:  MethodModifier ::= final
            //
            case 345: {
               //#line 2957 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2957 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 346:  MethodModifier ::= native
            //
            case 346: {
               //#line 2962 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2962 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 347:  MethodModifier ::= strictfp
            //
            case 347: {
               //#line 2967 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2967 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 348:  MethodModifier ::= atomic
            //
            case 348: {
               //#line 2972 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2972 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                      break;
            }
    
            //
            // Rule 349:  MethodModifier ::= extern
            //
            case 349: {
               //#line 2977 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2977 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
                      break;
            }
    
            //
            // Rule 350:  MethodModifier ::= safe
            //
            case 350: {
               //#line 2982 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2982 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 351:  MethodModifier ::= sequential
            //
            case 351: {
               //#line 2987 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2987 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                      break;
            }
    
            //
            // Rule 352:  MethodModifier ::= local
            //
            case 352: {
               //#line 2992 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2992 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.LOCAL)));
                      break;
            }
    
            //
            // Rule 353:  MethodModifier ::= nonblocking
            //
            case 353: {
               //#line 2997 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2997 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                      break;
            }
    
            //
            // Rule 354:  MethodModifier ::= incomplete
            //
            case 354: {
               //#line 3002 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3002 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                      break;
            }
    
            //
            // Rule 355:  MethodModifier ::= property
            //
            case 355: {
               //#line 3007 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3007 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                      break;
            }
    
            //
            // Rule 356:  Throws ::= throws ExceptionTypeList
            //
            case 356: {
               //#line 3014 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3012 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 3014 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 357:  ExceptionTypeList ::= ExceptionType
            //
            case 357: {
               //#line 3020 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3018 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3020 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 358:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 358: {
               //#line 3027 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3025 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3025 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3027 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 360:  MethodBody ::= = LastExpression ;
            //
            case 360: {
               //#line 3035 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3033 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3035 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 361:  MethodBody ::= = { BlockStatementsopt LastExpression }
            //
            case 361: {
               //#line 3040 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3038 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3038 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 3040 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 362:  MethodBody ::= = Block
            //
            case 362: {
               //#line 3048 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3046 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3048 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 363:  MethodBody ::= Block
            //
            case 363: {
               //#line 3053 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3051 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block Block = (Block) getRhsSym(1);
                //#line 3053 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 364:  MethodBody ::= ;
            //
            case 364:
                setResult(null);
                break;

            //
            // Rule 365:  InstanceInitializer ::= Block
            //
            case 365: {
               //#line 3061 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3059 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block Block = (Block) getRhsSym(1);
                //#line 3061 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Initializer(pos(), nf.FlagsNode(pos(), Flags.NONE), Block));
                      break;
            }
    
            //
            // Rule 366:  StaticInitializer ::= static Block
            //
            case 366: {
               //#line 3067 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3065 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3067 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Initializer(pos(), nf.FlagsNode(pos(getLeftSpan()), Flags.STATIC), Block));
                      break;
            }
    
            //
            // Rule 367:  SimpleTypeName ::= Identifier
            //
            case 367: {
               //#line 3073 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3071 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3073 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 368:  ConstructorModifiers ::= ConstructorModifier
            //
            case 368: {
               //#line 3079 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3077 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 3079 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 369:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 369: {
               //#line 3086 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3084 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 3084 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 3086 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                      break;
            }
    
            //
            // Rule 370:  ConstructorModifier ::= Annotation
            //
            case 370: {
               //#line 3092 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3090 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3092 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 371:  ConstructorModifier ::= public
            //
            case 371: {
               //#line 3097 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3097 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 372:  ConstructorModifier ::= protected
            //
            case 372: {
               //#line 3102 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3102 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 373:  ConstructorModifier ::= private
            //
            case 373: {
               //#line 3107 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3107 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 374:  ConstructorModifier ::= native
            //
            case 374: {
               //#line 3112 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3112 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 375:  ConstructorBody ::= = ConstructorBlock
            //
            case 375: {
               //#line 3118 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3116 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3118 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 376:  ConstructorBody ::= ConstructorBlock
            //
            case 376: {
               //#line 3123 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3121 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3123 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 377:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 377: {
               //#line 3128 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3126 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3128 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 378:  ConstructorBody ::= = AssignPropertyCall
            //
            case 378: {
               //#line 3136 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3134 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3136 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.SuperCall(pos(), Collections.EMPTY_LIST));
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 379:  ConstructorBody ::= ;
            //
            case 379:
                setResult(null);
                break;

            //
            // Rule 380:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 380: {
               //#line 3148 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3146 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3146 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3148 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 381:  Arguments ::= ( ArgumentListopt )
            //
            case 381: {
               //#line 3165 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3163 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3165 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 383:  InterfaceModifiers ::= InterfaceModifier
            //
            case 383: {
               //#line 3175 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3173 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 3175 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 384:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 384: {
               //#line 3182 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3180 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 3180 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 3182 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                      break;
            }
    
            //
            // Rule 385:  InterfaceModifier ::= Annotation
            //
            case 385: {
               //#line 3188 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3186 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3188 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 386:  InterfaceModifier ::= public
            //
            case 386: {
               //#line 3193 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3193 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 387:  InterfaceModifier ::= protected
            //
            case 387: {
               //#line 3198 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3198 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 388:  InterfaceModifier ::= private
            //
            case 388: {
               //#line 3203 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3203 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 389:  InterfaceModifier ::= abstract
            //
            case 389: {
               //#line 3208 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3208 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 390:  InterfaceModifier ::= static
            //
            case 390: {
               //#line 3213 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3213 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 391:  InterfaceModifier ::= strictfp
            //
            case 391: {
               //#line 3218 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3218 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STRICTFP)));
                      break;
            }
    
            //
            // Rule 392:  ExtendsInterfaces ::= extends Type
            //
            case 392: {
               //#line 3224 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3222 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3224 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 393:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 393: {
               //#line 3231 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3229 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3229 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3231 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 394:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 394: {
               //#line 3240 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3238 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3240 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 396:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 396: {
               //#line 3247 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3245 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3245 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3247 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 397:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 397: {
               //#line 3254 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3252 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3254 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 398:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 398: {
               //#line 3261 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3259 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3261 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 399:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 399: {
               //#line 3268 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3266 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3268 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 400:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 400: {
               //#line 3275 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3273 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3275 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 401:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 401: {
               //#line 3282 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3280 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3282 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 402:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 402: {
               //#line 3289 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3287 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3289 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 403:  InterfaceMemberDeclaration ::= ;
            //
            case 403: {
               //#line 3296 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3296 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 404:  Annotations ::= Annotation
            //
            case 404: {
               //#line 3302 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3300 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3302 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 405:  Annotations ::= Annotations Annotation
            //
            case 405: {
               //#line 3309 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3307 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3307 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3309 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 406:  Annotation ::= @ NamedType
            //
            case 406: {
               //#line 3315 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3313 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3315 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 407:  SimpleName ::= Identifier
            //
            case 407: {
               //#line 3321 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3319 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3321 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 408:  Identifier ::= identifier
            //
            case 408: {
               //#line 3327 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3325 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3327 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 409:  ArrayInitializer ::= [ VariableInitializersopt ,opt$opt ]
            //
            case 409: {
               //#line 3335 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3333 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableInitializersopt = (List) getRhsSym(2);
                //#line 3333 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Object opt = (Object) getRhsSym(3);
                //#line 3335 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                if (VariableInitializersopt == null)
                     setResult(nf.ArrayInit(pos()));
                else setResult(nf.ArrayInit(pos(), VariableInitializersopt));
                      break;
            }
    
            //
            // Rule 410:  VariableInitializers ::= VariableInitializer
            //
            case 410: {
               //#line 3343 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3341 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3343 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 411:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 411: {
               //#line 3350 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3348 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3348 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3350 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                      break;
            }
    
            //
            // Rule 412:  Block ::= { BlockStatementsopt }
            //
            case 412: {
               //#line 3368 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3366 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3368 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 413:  BlockStatements ::= BlockStatement
            //
            case 413: {
               //#line 3374 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3372 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3374 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 414:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 414: {
               //#line 3381 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3379 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3379 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3381 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 416:  BlockStatement ::= ClassDeclaration
            //
            case 416: {
               //#line 3389 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3387 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3389 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 417:  BlockStatement ::= TypeDefDeclaration
            //
            case 417: {
               //#line 3396 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3394 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3396 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 418:  BlockStatement ::= Statement
            //
            case 418: {
               //#line 3403 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3401 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3403 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 419:  IdentifierList ::= Identifier
            //
            case 419: {
               //#line 3411 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3409 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3411 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 420:  IdentifierList ::= IdentifierList , Identifier
            //
            case 420: {
               //#line 3418 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3416 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3416 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3418 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 421:  FormalDeclarator ::= Identifier : Type
            //
            case 421: {
               //#line 3424 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3422 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3422 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3424 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, Type, null });
                      break;
            }
    
            //
            // Rule 422:  FormalDeclarator ::= ( IdentifierList ) : Type
            //
            case 422: {
               //#line 3429 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3427 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3427 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(5);
                //#line 3429 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, Type, null });
                      break;
            }
    
            //
            // Rule 423:  FormalDeclarator ::= Identifier ( IdentifierList ) : Type
            //
            case 423: {
               //#line 3434 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3432 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3432 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3432 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(6);
                //#line 3434 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, Type, null });
                      break;
            }
    
            //
            // Rule 424:  FieldDeclarator ::= Identifier : Type
            //
            case 424: {
               //#line 3440 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3438 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3438 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3440 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, Type, null });
                      break;
            }
    
            //
            // Rule 425:  FieldDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 425: {
               //#line 3445 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3443 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3443 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3443 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3445 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 426:  VariableDeclarator ::= Identifier ResultTypeopt = VariableInitializer
            //
            case 426: {
               //#line 3451 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3449 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3449 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3449 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3451 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 427:  VariableDeclarator ::= ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 427: {
               //#line 3456 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3454 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3454 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3454 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3456 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 428:  VariableDeclarator ::= Identifier ( IdentifierList ) ResultTypeopt = VariableInitializer
            //
            case 428: {
               //#line 3461 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3459 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3459 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3459 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3459 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3461 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 429:  VariableDeclaratorWithType ::= Identifier ResultType = VariableInitializer
            //
            case 429: {
               //#line 3467 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3465 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3465 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3465 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3467 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 430:  VariableDeclaratorWithType ::= ( IdentifierList ) ResultType = VariableInitializer
            //
            case 430: {
               //#line 3472 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3470 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3470 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3470 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3472 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 431:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) ResultType = VariableInitializer
            //
            case 431: {
               //#line 3477 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3475 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3475 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3475 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3475 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3477 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 433:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 433: {
               //#line 3485 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3483 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3483 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3483 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3485 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
                        	l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(init.position(), nf.Local(init.position(), name), Collections.EMPTY_LIST, Collections.<Expr>singletonList(nf.IntLit(init.position(), IntLit.INT, index))) : null));
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
               //#line 3518 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3516 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3516 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 3518 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
                        	l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(init.position(), nf.Local(init.position(), name), Collections.EMPTY_LIST, Collections.<Expr>singletonList(nf.IntLit(init.position(), IntLit.INT, index))) : null));
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
               //#line 3552 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3550 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3550 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3550 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3552 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
                        	l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(init.position(), nf.Local(init.position(), name), Collections.EMPTY_LIST, Collections.<Expr>singletonList(nf.IntLit(init.position(), IntLit.INT, index))) : null));
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
               //#line 3593 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3591 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3593 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeName instanceof ParsedName)
                {
                    ParsedName a = (ParsedName) TypeName;
                    setResult(nf.ClassLit(pos(), a.toType()));
                }
                else assert(false);
                      break;
            }
    
            //
            // Rule 438:  Primary ::= self
            //
            case 438: {
               //#line 3603 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3603 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 439:  Primary ::= this
            //
            case 439: {
               //#line 3608 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3608 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 440:  Primary ::= ClassName . this
            //
            case 440: {
               //#line 3613 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3611 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3613 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 441:  Primary ::= ( Expression )
            //
            case 441: {
               //#line 3618 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3616 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3618 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 447:  OperatorFunction ::= TypeName . +
            //
            case 447: {
               //#line 3629 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3627 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3629 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.ADD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 448:  OperatorFunction ::= TypeName . -
            //
            case 448: {
               //#line 3640 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3638 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3640 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SUB, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 449:  OperatorFunction ::= TypeName . *
            //
            case 449: {
               //#line 3651 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3649 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3651 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MUL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 450:  OperatorFunction ::= TypeName . /
            //
            case 450: {
               //#line 3662 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3660 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3662 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.DIV, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 451:  OperatorFunction ::= TypeName . %
            //
            case 451: {
               //#line 3673 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3671 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3673 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MOD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 452:  OperatorFunction ::= TypeName . &
            //
            case 452: {
               //#line 3684 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3682 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3684 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_AND, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 453:  OperatorFunction ::= TypeName . |
            //
            case 453: {
               //#line 3695 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3693 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3695 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_OR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 454:  OperatorFunction ::= TypeName . ^
            //
            case 454: {
               //#line 3706 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3704 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3706 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_XOR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 455:  OperatorFunction ::= TypeName . <<
            //
            case 455: {
               //#line 3717 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3715 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3717 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 456:  OperatorFunction ::= TypeName . >>
            //
            case 456: {
               //#line 3728 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3726 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3728 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 457:  OperatorFunction ::= TypeName . >>>
            //
            case 457: {
               //#line 3739 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3737 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3739 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.USHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 458:  OperatorFunction ::= TypeName . <
            //
            case 458: {
               //#line 3750 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3748 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3750 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 459:  OperatorFunction ::= TypeName . <=
            //
            case 459: {
               //#line 3761 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3759 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3761 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 460:  OperatorFunction ::= TypeName . >=
            //
            case 460: {
               //#line 3772 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3770 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3772 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 461:  OperatorFunction ::= TypeName . >
            //
            case 461: {
               //#line 3783 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3781 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3783 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 462:  OperatorFunction ::= TypeName . ==
            //
            case 462: {
               //#line 3794 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3792 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3794 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.EQ, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 463:  OperatorFunction ::= TypeName . !=
            //
            case 463: {
               //#line 3805 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3803 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3805 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(), Collections.EMPTY_LIST, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.NE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 464:  Literal ::= IntegerLiteral$IntegerLiteral
            //
            case 464: {
               //#line 3818 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3816 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken IntegerLiteral = (IToken) getRhsIToken(1);
                //#line 3818 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 465:  Literal ::= LongLiteral$LongLiteral
            //
            case 465: {
               //#line 3824 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3822 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken LongLiteral = (IToken) getRhsIToken(1);
                //#line 3824 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 466:  Literal ::= FloatingPointLiteral$FloatLiteral
            //
            case 466: {
               //#line 3830 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3828 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken FloatLiteral = (IToken) getRhsIToken(1);
                //#line 3830 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 467:  Literal ::= DoubleLiteral$DoubleLiteral
            //
            case 467: {
               //#line 3836 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3834 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken DoubleLiteral = (IToken) getRhsIToken(1);
                //#line 3836 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 468:  Literal ::= BooleanLiteral
            //
            case 468: {
               //#line 3842 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3840 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3842 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 469:  Literal ::= CharacterLiteral$CharacterLiteral
            //
            case 469: {
               //#line 3847 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3845 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken CharacterLiteral = (IToken) getRhsIToken(1);
                //#line 3847 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 470:  Literal ::= StringLiteral$str
            //
            case 470: {
               //#line 3853 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3851 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3853 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 471:  Literal ::= null
            //
            case 471: {
               //#line 3859 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3859 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 472:  BooleanLiteral ::= true$trueLiteral
            //
            case 472: {
               //#line 3865 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3863 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3865 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 473:  BooleanLiteral ::= false$falseLiteral
            //
            case 473: {
               //#line 3870 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3868 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3870 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 474:  ArgumentList ::= Expression
            //
            case 474: {
               //#line 3879 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3877 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3879 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 475:  ArgumentList ::= ArgumentList , Expression
            //
            case 475: {
               //#line 3886 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3884 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3884 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3886 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 476:  FieldAccess ::= Primary . Identifier
            //
            case 476: {
               //#line 3892 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3890 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3890 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3892 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 477:  FieldAccess ::= super . Identifier
            //
            case 477: {
               //#line 3897 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3895 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3897 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 478:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 478: {
               //#line 3902 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3900 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3900 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3900 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3902 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 479:  FieldAccess ::= Primary . class$c
            //
            case 479: {
               //#line 3907 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3905 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3905 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3907 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 480:  FieldAccess ::= super . class$c
            //
            case 480: {
               //#line 3912 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3910 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3912 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 481:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 481: {
               //#line 3917 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3915 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3915 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3915 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 3917 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 482:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 482: {
               //#line 3923 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3921 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3921 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3921 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3923 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 483:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 483: {
               //#line 3930 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3928 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3928 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3928 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3928 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3930 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 484:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 484: {
               //#line 3935 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3933 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3933 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3933 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3935 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 485:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 485: {
               //#line 3940 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3938 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3938 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3938 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3938 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 3938 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 3940 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 486:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 486: {
               //#line 3945 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3943 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3943 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3943 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3945 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 487:  MethodSelection ::= MethodName . TypeParametersopt ( FormalParameterListopt )
            //
            case 487: {
               //#line 3965 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3963 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3963 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 3963 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(5);
                //#line 3965 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, typeArgs, actuals), true))));
                      break;
            }
    
            //
            // Rule 488:  MethodSelection ::= Primary . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 488: {
               //#line 3978 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3976 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3976 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3976 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(5);
                //#line 3976 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(7);
                //#line 3978 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), Primary, Identifier, typeArgs, actuals), true))));
                      break;
            }
    
            //
            // Rule 489:  MethodSelection ::= super . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 489: {
               //#line 3990 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3988 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3988 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(5);
                //#line 3988 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(7);
                //#line 3990 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, typeArgs, actuals), true))));
                      break;
            }
    
            //
            // Rule 490:  MethodSelection ::= ClassName . super$sup . Identifier . TypeParametersopt ( FormalParameterListopt )
            //
            case 490: {
               //#line 4002 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4000 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4000 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4000 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4000 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(7);
                //#line 4000 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(9);
                //#line 4002 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
                List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), typeParams, formals, (DepParameterExpr) null, tn, Collections.EMPTY_LIST, nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, typeArgs, actuals), true))));
                      break;
            }
    
            //
            // Rule 493:  PostfixExpression ::= ExpressionName
            //
            case 493: {
               //#line 4017 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4015 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4017 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 496:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 496: {
               //#line 4025 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4023 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4025 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 497:  PostDecrementExpression ::= PostfixExpression --
            //
            case 497: {
               //#line 4031 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4029 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4031 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 500:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 500: {
               //#line 4039 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4037 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4039 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 501:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 501: {
               //#line 4044 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4042 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4044 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 503:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 503: {
               //#line 4051 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4049 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4051 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 504:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 504: {
               //#line 4057 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4055 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4057 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 506:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 506: {
               //#line 4064 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4062 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4064 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 507:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 507: {
               //#line 4069 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4067 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4067 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4069 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e);
                      break;
            }
    
            //
            // Rule 508:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 508: {
               //#line 4076 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4074 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4076 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 510:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 510: {
               //#line 4083 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4081 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4081 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4083 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 511:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 511: {
               //#line 4088 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4086 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4086 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4088 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 512:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 512: {
               //#line 4093 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4091 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4091 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4093 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 514:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 514: {
               //#line 4100 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4098 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4098 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4100 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 515:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 515: {
               //#line 4105 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4103 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4103 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4105 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 517:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 517: {
               //#line 4112 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4110 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4110 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4112 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 518:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 518: {
               //#line 4117 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4115 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4115 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4117 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 519:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 519: {
               //#line 4122 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4120 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4120 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4122 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 521:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 521: {
               //#line 4129 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4127 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4127 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4129 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 524:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 524: {
               //#line 4138 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4136 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4136 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4138 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 525:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 525: {
               //#line 4143 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4141 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4141 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4143 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 526:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 526: {
               //#line 4148 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4146 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4146 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4148 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 527:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 527: {
               //#line 4153 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4151 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4151 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4153 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 528:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 528: {
               //#line 4158 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4156 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4156 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4158 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 529:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 529: {
               //#line 4163 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4161 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4161 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4163 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 531:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 531: {
               //#line 4170 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4168 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4168 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4170 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 532:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 532: {
               //#line 4175 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4173 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4173 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4175 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 533:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 533: {
               //#line 4180 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4178 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4178 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4180 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 535:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 535: {
               //#line 4187 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4185 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4185 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4187 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 537:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 537: {
               //#line 4194 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4192 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4192 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4194 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 539:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 539: {
               //#line 4201 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4199 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4199 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4201 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 541:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 541: {
               //#line 4208 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4206 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4206 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4208 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 543:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 543: {
               //#line 4215 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4213 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4213 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4215 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 549:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 549: {
               //#line 4227 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4225 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4225 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4225 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4227 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 552:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 552: {
               //#line 4236 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4234 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4234 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4234 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4236 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 553:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 553: {
               //#line 4241 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4239 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4239 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4239 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4239 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4241 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 554:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 554: {
               //#line 4246 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4244 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4244 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4244 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4244 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4246 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 555:  LeftHandSide ::= ExpressionName
            //
            case 555: {
               //#line 4252 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4250 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4252 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 557:  AssignmentOperator ::= =
            //
            case 557: {
               //#line 4259 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4259 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 558:  AssignmentOperator ::= *=
            //
            case 558: {
               //#line 4264 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4264 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 559:  AssignmentOperator ::= /=
            //
            case 559: {
               //#line 4269 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4269 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 560:  AssignmentOperator ::= %=
            //
            case 560: {
               //#line 4274 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4274 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 561:  AssignmentOperator ::= +=
            //
            case 561: {
               //#line 4279 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4279 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 562:  AssignmentOperator ::= -=
            //
            case 562: {
               //#line 4284 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4284 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 563:  AssignmentOperator ::= <<=
            //
            case 563: {
               //#line 4289 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4289 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 564:  AssignmentOperator ::= >>=
            //
            case 564: {
               //#line 4294 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4294 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 565:  AssignmentOperator ::= >>>=
            //
            case 565: {
               //#line 4299 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4299 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 566:  AssignmentOperator ::= &=
            //
            case 566: {
               //#line 4304 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4304 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 567:  AssignmentOperator ::= ^=
            //
            case 567: {
               //#line 4309 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4309 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 568:  AssignmentOperator ::= |=
            //
            case 568: {
               //#line 4314 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4314 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 571:  Catchesopt ::= $Empty
            //
            case 571: {
               //#line 4327 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4327 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                      break;
            }
    
            //
            // Rule 573:  Identifieropt ::= $Empty
            //
            case 573:
                setResult(null);
                break;

            //
            // Rule 574:  Identifieropt ::= Identifier
            //
            case 574: {
               //#line 4336 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4334 "/Users/beth/ews/x10dt17-apr-34/org.eclipse.imp.x10dt.formatter/src/org/eclipse/imp/x10dt/formatter/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4336 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 575:  ForUpdateopt ::= $Empty
            //
            case 575: {
               //#line 4342 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4342 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                      break;
            }
    
            //
            // Rule 577:  Expressionopt ::= $Empty
            //
            case 577:
                setResult(null);
                break;

            //
            // Rule 579:  ForInitopt ::= $Empty
            //
            case 579: {
               //#line 4353 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4353 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 581:  SwitchLabelsopt ::= $Empty
            //
            case 581: {
               //#line 4360 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4360 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 583:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 583: {
               //#line 4367 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4367 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 585:  VariableModifiersopt ::= $Empty
            //
            case 585: {
               //#line 4374 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4374 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 587:  VariableInitializersopt ::= $Empty
            //
            case 587:
                setResult(null);
                break;

            //
            // Rule 589:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 589: {
               //#line 4385 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4385 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 591:  ExtendsInterfacesopt ::= $Empty
            //
            case 591: {
               //#line 4392 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4392 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 593:  InterfaceModifiersopt ::= $Empty
            //
            case 593: {
               //#line 4399 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4399 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 595:  ClassBodyopt ::= $Empty
            //
            case 595:
                setResult(null);
                break;

            //
            // Rule 597:  Argumentsopt ::= $Empty
            //
            case 597: {
               //#line 4410 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4410 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 599:  ArgumentListopt ::= $Empty
            //
            case 599: {
               //#line 4417 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4417 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 601:  BlockStatementsopt ::= $Empty
            //
            case 601: {
               //#line 4424 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4424 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                      break;
            }
    
            //
            // Rule 603:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 603:
                setResult(null);
                break;

            //
            // Rule 605:  ConstructorModifiersopt ::= $Empty
            //
            case 605: {
               //#line 4435 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4435 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 607:  FormalParameterListopt ::= $Empty
            //
            case 607: {
               //#line 4442 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4442 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 609:  Throwsopt ::= $Empty
            //
            case 609: {
               //#line 4449 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4449 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 611:  MethodModifiersopt ::= $Empty
            //
            case 611: {
               //#line 4456 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4456 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 613:  FieldModifiersopt ::= $Empty
            //
            case 613: {
               //#line 4463 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4463 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 615:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 615: {
               //#line 4470 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4470 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 617:  Interfacesopt ::= $Empty
            //
            case 617: {
               //#line 4477 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4477 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 619:  Superopt ::= $Empty
            //
            case 619:
                setResult(null);
                break;

            //
            // Rule 621:  TypeParametersopt ::= $Empty
            //
            case 621: {
               //#line 4488 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4488 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 623:  FormalParametersopt ::= $Empty
            //
            case 623: {
               //#line 4495 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4495 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 625:  Annotationsopt ::= $Empty
            //
            case 625: {
               //#line 4502 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4502 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 627:  TypeDeclarationsopt ::= $Empty
            //
            case 627: {
               //#line 4509 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4509 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 629:  ImportDeclarationsopt ::= $Empty
            //
            case 629: {
               //#line 4516 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4516 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                      break;
            }
    
            //
            // Rule 631:  PackageDeclarationopt ::= $Empty
            //
            case 631:
                setResult(null);
                break;

            //
            // Rule 633:  ResultTypeopt ::= $Empty
            //
            case 633:
                setResult(null);
                break;

            //
            // Rule 635:  TypeArgumentsopt ::= $Empty
            //
            case 635: {
               //#line 4531 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4531 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 637:  TypePropertiesopt ::= $Empty
            //
            case 637: {
               //#line 4538 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4538 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypePropertyNode.class, false));
                      break;
            }
    
            //
            // Rule 639:  Propertiesopt ::= $Empty
            //
            case 639: {
               //#line 4545 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4545 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 641:  ,opt ::= $Empty
            //
            case 641:
                setResult(null);
                break;

            //
            // Rule 643:  PrefixOp ::= +
            //
            case 643: {
               //#line 4556 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4556 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 644:  PrefixOp ::= -
            //
            case 644: {
               //#line 4561 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4561 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 645:  PrefixOp ::= !
            //
            case 645: {
               //#line 4566 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4566 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 646:  PrefixOp ::= ~
            //
            case 646: {
               //#line 4571 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4571 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 647:  BinOp ::= +
            //
            case 647: {
               //#line 4577 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4577 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 648:  BinOp ::= -
            //
            case 648: {
               //#line 4582 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4582 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 649:  BinOp ::= *
            //
            case 649: {
               //#line 4587 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4587 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 650:  BinOp ::= /
            //
            case 650: {
               //#line 4592 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4592 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 651:  BinOp ::= %
            //
            case 651: {
               //#line 4597 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4597 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 652:  BinOp ::= &
            //
            case 652: {
               //#line 4602 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4602 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 653:  BinOp ::= |
            //
            case 653: {
               //#line 4607 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4607 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 654:  BinOp ::= ^
            //
            case 654: {
               //#line 4612 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4612 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 655:  BinOp ::= &&
            //
            case 655: {
               //#line 4617 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4617 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 656:  BinOp ::= ||
            //
            case 656: {
               //#line 4622 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4622 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 657:  BinOp ::= <<
            //
            case 657: {
               //#line 4627 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4627 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 658:  BinOp ::= >>
            //
            case 658: {
               //#line 4632 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4632 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 659:  BinOp ::= >>>
            //
            case 659: {
               //#line 4637 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4637 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 660:  BinOp ::= >=
            //
            case 660: {
               //#line 4642 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4642 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 661:  BinOp ::= <=
            //
            case 661: {
               //#line 4647 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4647 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 662:  BinOp ::= >
            //
            case 662: {
               //#line 4652 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4652 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 663:  BinOp ::= <
            //
            case 663: {
               //#line 4657 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4657 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 664:  BinOp ::= ==
            //
            case 664: {
               //#line 4665 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4665 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 665:  BinOp ::= !=
            //
            case 665: {
               //#line 4670 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4670 "/Users/beth/ews/x10dt17-apr-34/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

