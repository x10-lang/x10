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
import polyglot.ast.AmbTypeNode;
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
    

    //#line 313 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
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

	public static String getErrorMessageFor(int errorCode,
			String[] errorInfo) {
			
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
	
		// FIXME: HACK! Prepend "Syntax error: " until we figure out how to
		// get Polyglot to do it for us.
		if (errorCode != NO_MESSAGE_CODE) {
			msg = "Syntax error: " + msg;
		}
		return msg;
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

        String msg = getErrorMessageFor(errorCode, errorInfo);
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
               //#line 919 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 917 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 917 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 917 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 917 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 917 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 917 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 919 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 932 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 930 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 932 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
   setResult(PropertyList);
                 break;
            } 
            //
            // Rule 18:  PropertyList ::= Property
            //
            case 18: {
               //#line 937 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 935 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 937 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 19:  PropertyList ::= PropertyList , Property
            //
            case 19: {
               //#line 944 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 942 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 942 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 944 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 20:  Property ::= Annotationsopt Identifier ResultType
            //
            case 20: {
               //#line 951 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 949 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 949 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 949 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 951 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 960 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 960 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 992 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(13);
                //#line 990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(14);
                //#line 990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(15);
                //#line 992 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1010 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1008 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1008 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1008 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1008 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 1008 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 1008 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 1008 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1008 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1008 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1010 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
          syntaxError("Unary operator with one parameter must be static.", md.position());
      md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(MethodModifiersopt));
      setResult(md);
                      break;
            }
    
            //
            // Rule 24:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 24: {
               //#line 1028 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 1026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 1026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1028 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1047 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1045 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1045 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1045 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1045 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1045 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1045 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1045 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1045 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1045 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1047 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1067 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1065 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1065 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1065 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1065 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1065 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1065 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1065 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1065 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1067 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1085 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1085 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1103 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1101 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1101 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1101 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1101 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1101 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1101 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1101 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(12);
                //#line 1101 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(13);
                //#line 1101 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 1103 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1121 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1119 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1119 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1119 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1119 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1119 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1119 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1119 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1119 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1121 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1139 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1139 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1157 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1155 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1155 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1155 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1155 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1155 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(8);
                //#line 1155 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(9);
                //#line 1155 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(10);
                //#line 1155 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1157 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1176 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1174 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1174 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1174 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1174 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1174 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1174 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1174 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(8);
                //#line 1176 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1192 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1190 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1190 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1190 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(4);
                //#line 1190 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 1190 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(6);
                //#line 1192 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1209 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1207 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1207 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1209 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 35:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 35: {
               //#line 1214 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1212 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1212 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1214 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 36:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 36: {
               //#line 1219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1217 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1217 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1217 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 37:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 37: {
               //#line 1224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1222 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1222 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1222 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 38:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 38: {
               //#line 1230 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1228 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 1228 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1228 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1228 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1228 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1228 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 1228 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1230 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1251 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1249 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1249 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1249 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1249 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1251 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 40:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 40: {
               //#line 1258 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1258 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1266 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1264 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1264 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1264 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1264 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1264 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1266 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1275 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1273 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1273 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1275 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 45:  Type ::= proto ConstrainedType
            //
            case 45: {
               //#line 1284 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1282 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ConstrainedType = (TypeNode) getRhsSym(2);
                //#line 1284 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
        AddFlags tn = (AddFlags) ConstrainedType;
        tn.addFlags(X10Flags.PROTO);
        setResult(ConstrainedType.position(pos()));
                      break;
            }
    
            //
            // Rule 46:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt Offersopt => Type
            //
            case 46: {
               //#line 1292 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1290 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 1290 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1290 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1290 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1290 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(7);
                //#line 1290 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(9);
                //#line 1292 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt, Offersopt));
                      break;
            }
    
            //
            // Rule 51:  AnnotatedType ::= Type Annotations
            //
            case 51: {
               //#line 1301 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1299 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1299 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1301 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn.position(pos()));
                      break;
            }
    
            //
            // Rule 54:  ConstrainedType ::= ( Type )
            //
            case 54: {
               //#line 1311 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1309 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1311 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 56:  SimpleNamedType ::= TypeName
            //
            case 56: {
               //#line 1325 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1325 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(TypeName.toType());
                      break;
            }
    
            //
            // Rule 57:  SimpleNamedType ::= Primary . Identifier
            //
            case 57: {
               //#line 1330 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1328 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1328 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1330 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(nf.AmbTypeNode(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 58:  SimpleNamedType ::= DepNamedType . Identifier
            //
            case 58: {
               //#line 1335 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1333 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode DepNamedType = (TypeNode) getRhsSym(1);
                //#line 1333 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1335 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(nf.AmbTypeNode(pos(), DepNamedType, Identifier));
                      break;
            }
    
            //
            // Rule 59:  DepNamedType ::= SimpleNamedType DepParameters
            //
            case 59: {
               //#line 1341 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1339 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1339 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(2);
                //#line 1341 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList(new LinkedList(), TypeNode.class, false),
                                              new TypedList(new LinkedList(), Expr.class, false),
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 60:  DepNamedType ::= SimpleNamedType Arguments
            //
            case 60: {
               //#line 1350 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1348 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1348 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Arguments = (List) getRhsSym(2);
                //#line 1350 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList(new LinkedList(), TypeNode.class, false),
                                              Arguments,
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 61:  DepNamedType ::= SimpleNamedType Arguments DepParameters
            //
            case 61: {
               //#line 1359 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1357 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1357 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Arguments = (List) getRhsSym(2);
                //#line 1357 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(3);
                //#line 1359 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList(new LinkedList(), TypeNode.class, false),
                                              Arguments,
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 62:  DepNamedType ::= SimpleNamedType TypeArguments
            //
            case 62: {
               //#line 1368 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1366 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1366 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArguments = (List) getRhsSym(2);
                //#line 1368 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              new TypedList(new LinkedList(), Expr.class, false),
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 63:  DepNamedType ::= SimpleNamedType TypeArguments DepParameters
            //
            case 63: {
               //#line 1377 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1375 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1375 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArguments = (List) getRhsSym(2);
                //#line 1375 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(3);
                //#line 1377 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              new TypedList(new LinkedList(), Expr.class, false),
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 64:  DepNamedType ::= SimpleNamedType TypeArguments Arguments
            //
            case 64: {
               //#line 1386 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1384 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1384 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArguments = (List) getRhsSym(2);
                //#line 1384 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Arguments = (List) getRhsSym(3);
                //#line 1386 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              Arguments,
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 65:  DepNamedType ::= SimpleNamedType TypeArguments Arguments DepParameters
            //
            case 65: {
               //#line 1395 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1393 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1393 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArguments = (List) getRhsSym(2);
                //#line 1393 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Arguments = (List) getRhsSym(3);
                //#line 1393 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(4);
                //#line 1395 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              Arguments,
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 68:  DepParameters ::= { ExistentialListopt Conjunctionopt }
            //
            case 68: {
               //#line 1408 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1406 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1406 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object Conjunctionopt = (Object) getRhsSym(3);
                //#line 1408 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, (List) Conjunctionopt));
                      break;
            }
    
            //
            // Rule 69:  DepParameters ::= ! PlaceType
            //
            case 69: {
               //#line 1413 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1411 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1413 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 70:  DepParameters ::= !
            //
            case 70: {
               //#line 1419 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1419 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 71:  DepParameters ::= ! PlaceType { ExistentialListopt Conjunction }
            //
            case 71: {
               //#line 1425 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1423 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1423 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(4);
                //#line 1423 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(5);
                //#line 1425 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 72:  DepParameters ::= ! { ExistentialListopt Conjunction }
            //
            case 72: {
               //#line 1431 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1429 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(3);
                //#line 1429 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(4);
                //#line 1431 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 73:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 73: {
               //#line 1439 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1437 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(2);
                //#line 1439 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 74:  TypeParameters ::= [ TypeParameterList ]
            //
            case 74: {
               //#line 1445 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1443 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1445 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 75:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 75: {
               //#line 1450 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1448 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1450 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 76:  Conjunction ::= Expression
            //
            case 76: {
               //#line 1456 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1454 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1456 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 77:  Conjunction ::= Conjunction , Expression
            //
            case 77: {
               //#line 1463 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1463 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 78:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 78: {
               //#line 1469 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1467 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1467 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1469 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 79:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 79: {
               //#line 1474 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1472 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1472 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1474 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 80:  WhereClause ::= DepParameters
            //
            case 80: {
               //#line 1480 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1478 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1480 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 81:  Conjunctionopt ::= $Empty
            //
            case 81: {
               //#line 1486 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1486 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                setResult(l);
                      break;
            }
      
            //
            // Rule 82:  Conjunctionopt ::= Conjunction
            //
            case 82: {
               //#line 1492 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1490 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1492 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(Conjunction);
                      break;
            }
    
            //
            // Rule 83:  ExistentialListopt ::= $Empty
            //
            case 83: {
               //#line 1498 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1498 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 84:  ExistentialListopt ::= ExistentialList ;
            //
            case 84: {
               //#line 1503 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1501 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1503 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 85:  ExistentialList ::= FormalParameter
            //
            case 85: {
               //#line 1509 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1507 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1509 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 86:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 86: {
               //#line 1516 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1514 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1514 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1516 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 89:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 89: {
               //#line 1527 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1525 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1525 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1525 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1525 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1525 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1525 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1525 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1525 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1527 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 90:  StructDeclaration ::= ClassModifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 90: {
               //#line 1543 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1541 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1541 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1541 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1541 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1541 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1541 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1541 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1543 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 91:  ConstructorDeclaration ::= ConstructorModifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt ConstructorBody
            //
            case 91: {
               //#line 1557 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(10);
                //#line 1557 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 92:  Super ::= extends ClassType
            //
            case 92: {
               //#line 1574 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1572 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1574 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 93:  FieldKeyword ::= val
            //
            case 93: {
               //#line 1580 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1580 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 94:  FieldKeyword ::= var
            //
            case 94: {
               //#line 1585 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1585 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 95:  FieldKeyword ::= const
            //
            case 95: {
               //#line 1590 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1590 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                      break;
            }
    
            //
            // Rule 96:  VarKeyword ::= val
            //
            case 96: {
               //#line 1598 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1598 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 97:  VarKeyword ::= var
            //
            case 97: {
               //#line 1603 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1603 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 98:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 98: {
               //#line 1610 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1608 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1608 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1608 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1610 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 99:  FieldDeclaration ::= FieldModifiersopt FieldDeclarators ;
            //
            case 99: {
               //#line 1635 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1633 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1633 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 1635 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 102:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 102: {
               //#line 1667 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1665 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 1665 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt NonExpressionStatement = (Stmt) getRhsSym(2);
                //#line 1667 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                if (NonExpressionStatement.ext() instanceof X10Ext && Annotationsopt instanceof List) {
                    NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations((List) Annotationsopt);
                }
                setResult(NonExpressionStatement.position(pos()));
                      break;
            }
    
            //
            // Rule 129:  OfferStatement ::= offer Expression ;
            //
            case 129: {
               //#line 1703 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1701 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1703 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Offer(pos(), Expression));
                      break;
            }
    
            //
            // Rule 130:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 130: {
               //#line 1709 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1707 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1707 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1709 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 131:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 131: {
               //#line 1715 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1713 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1713 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1713 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1715 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 132:  EmptyStatement ::= ;
            //
            case 132: {
               //#line 1721 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1721 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 133:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 133: {
               //#line 1727 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1725 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1725 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 1727 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 139:  ExpressionStatement ::= StatementExpression ;
            //
            case 139: {
               //#line 1739 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1737 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1739 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 147:  AssertStatement ::= assert Expression ;
            //
            case 147: {
               //#line 1753 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1751 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1753 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 148:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 148: {
               //#line 1758 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1756 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1756 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1758 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 149:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 149: {
               //#line 1764 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1762 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1762 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1764 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 150:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 150: {
               //#line 1770 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1768 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1768 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1770 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 152:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 152: {
               //#line 1778 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1776 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1776 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1778 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 153:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 153: {
               //#line 1785 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1783 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1783 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1785 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 154:  SwitchLabels ::= SwitchLabel
            //
            case 154: {
               //#line 1794 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1792 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1794 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 155:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 155: {
               //#line 1801 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1799 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1799 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1801 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 156:  SwitchLabel ::= case ConstantExpression :
            //
            case 156: {
               //#line 1808 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1806 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1808 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 157:  SwitchLabel ::= default :
            //
            case 157: {
               //#line 1813 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1813 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 158:  WhileStatement ::= while ( Expression ) Statement
            //
            case 158: {
               //#line 1819 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1817 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1817 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1819 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 159:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 159: {
               //#line 1825 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1823 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1823 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1825 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 162:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 162: {
               //#line 1834 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1832 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1832 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1832 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1832 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1834 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 164:  ForInit ::= LocalVariableDeclaration
            //
            case 164: {
               //#line 1841 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1839 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1841 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 166:  StatementExpressionList ::= StatementExpression
            //
            case 166: {
               //#line 1851 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1849 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1851 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 167:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 167: {
               //#line 1858 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1856 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1856 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1858 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 168:  BreakStatement ::= break Identifieropt ;
            //
            case 168: {
               //#line 1864 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1862 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1864 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 169:  ContinueStatement ::= continue Identifieropt ;
            //
            case 169: {
               //#line 1870 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1868 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1870 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 170:  ReturnStatement ::= return Expressionopt ;
            //
            case 170: {
               //#line 1876 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1874 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1876 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 171:  ThrowStatement ::= throw Expression ;
            //
            case 171: {
               //#line 1882 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1880 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1882 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 172:  TryStatement ::= try Block Catches
            //
            case 172: {
               //#line 1888 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1886 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1886 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1888 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 173:  TryStatement ::= try Block Catchesopt Finally
            //
            case 173: {
               //#line 1893 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1891 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1891 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1891 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1893 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 174:  Catches ::= CatchClause
            //
            case 174: {
               //#line 1899 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1897 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1899 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 175:  Catches ::= Catches CatchClause
            //
            case 175: {
               //#line 1906 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1904 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1904 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1906 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 176:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 176: {
               //#line 1913 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1911 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1911 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1913 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 177:  Finally ::= finally Block
            //
            case 177: {
               //#line 1919 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1917 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1919 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 178:  ClockedClause ::= clocked ( ClockList )
            //
            case 178: {
               //#line 1925 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1923 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1925 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 179:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 179: {
               //#line 1931 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1929 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1929 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1929 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1931 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 180:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 180: {
               //#line 1940 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1938 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1938 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1940 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 181:  AtomicStatement ::= atomic Statement
            //
            case 181: {
               //#line 1946 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1944 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1946 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 182:  WhenStatement ::= when ( Expression ) Statement
            //
            case 182: {
               //#line 1953 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1951 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1951 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1953 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 183:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 183: {
               //#line 1958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1956 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1956 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1956 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1956 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 184:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 184: {
               //#line 1965 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1963 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1963 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1963 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1963 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1965 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 185:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 185: {
               //#line 1981 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1979 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1979 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1979 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1979 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1981 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 186:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 186: {
               //#line 1997 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1995 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1995 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1995 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1997 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 187:  FinishStatement ::= finish Statement
            //
            case 187: {
               //#line 2011 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2009 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2011 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 188:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 188: {
               //#line 2017 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2015 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 2017 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 190:  NextStatement ::= next ;
            //
            case 190: {
               //#line 2025 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2025 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 191:  AwaitStatement ::= await Expression ;
            //
            case 191: {
               //#line 2031 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2029 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2031 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Await(pos(), Expression));
                      break;
            }
    
            //
            // Rule 192:  ClockList ::= Clock
            //
            case 192: {
               //#line 2037 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2035 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 2037 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 193:  ClockList ::= ClockList , Clock
            //
            case 193: {
               //#line 2044 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2042 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 2042 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 2044 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 194:  Clock ::= Expression
            //
            case 194: {
               //#line 2052 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2050 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2052 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 196:  CastExpression ::= ExpressionName
            //
            case 196: {
               //#line 2066 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2064 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 2066 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 197:  CastExpression ::= CastExpression as Type
            //
            case 197: {
               //#line 2071 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2069 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2069 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2071 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 198:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 198: {
               //#line 2078 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2076 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2078 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 199:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 199: {
               //#line 2085 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(1);
                //#line 2083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2085 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 200:  TypeParameterList ::= TypeParameter
            //
            case 200: {
               //#line 2092 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2090 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2092 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 201:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 201: {
               //#line 2099 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2097 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 2097 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2099 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 202:  TypeParamWithVariance ::= Identifier
            //
            case 202: {
               //#line 2106 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2104 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2106 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 203:  TypeParamWithVariance ::= + Identifier
            //
            case 203: {
               //#line 2111 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2111 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 204:  TypeParamWithVariance ::= - Identifier
            //
            case 204: {
               //#line 2116 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2114 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2116 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 205:  TypeParameter ::= Identifier
            //
            case 205: {
               //#line 2122 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2120 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2122 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 207:  RegionExpressionList ::= RegionExpression
            //
            case 207: {
               //#line 2130 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2128 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 2130 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 208:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 208: {
               //#line 2137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2135 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 2135 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 2137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                      break;
            }
    
            //
            // Rule 209:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 209: {
               //#line 2144 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2142 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2142 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2144 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 210:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt => ClosureBody
            //
            case 210: {
               //#line 2150 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2148 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(1);
                //#line 2148 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2148 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2148 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(4);
                //#line 2148 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(5);
                //#line 2148 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 2150 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 211:  LastExpression ::= Expression
            //
            case 211: {
               //#line 2157 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2155 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2157 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 212:  ClosureBody ::= ConditionalExpression
            //
            case 212: {
               //#line 2163 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2161 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 2163 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), ConditionalExpression, true)));
                      break;
            }
    
            //
            // Rule 213:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 213: {
               //#line 2168 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2166 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2166 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2166 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2168 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 214:  ClosureBody ::= Annotationsopt Block
            //
            case 214: {
               //#line 2178 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2176 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2176 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2178 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b.position(pos()));
                      break;
            }
    
            //
            // Rule 215:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 215: {
               //#line 2187 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2185 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2185 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2187 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 216:  AsyncExpression ::= async ClosureBody
            //
            case 216: {
               //#line 2193 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2191 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2193 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 217:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 217: {
               //#line 2198 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2196 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2196 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2198 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 218:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 218: {
               //#line 2203 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2201 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2201 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2203 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 219:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 219: {
               //#line 2208 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2206 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2206 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2206 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2208 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 220:  FinishExpression ::= finish ( Expression ) Block
            //
            case 220: {
               //#line 2215 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2213 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2213 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2215 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FinishExpr(pos(), Expression, Block));
                      break;
            }
    
            //
            // Rule 221:  FutureExpression ::= future ClosureBody
            //
            case 221: {
               //#line 2221 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2221 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 222:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 222: {
               //#line 2226 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2226 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 223:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 223: {
               //#line 2231 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2229 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2229 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2231 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 224:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 224: {
               //#line 2236 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2234 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2234 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2234 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2236 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 225:  DepParametersopt ::= $Empty
            //
            case 225:
                setResult(null);
                break;

            //
            // Rule 227:  PropertyListopt ::= $Empty
            //
            case 227: {
               //#line 2247 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2247 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 229:  WhereClauseopt ::= $Empty
            //
            case 229:
                setResult(null);
                break;

            //
            // Rule 231:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 231:
                setResult(null);
                break;

            //
            // Rule 233:  ClassModifiersopt ::= $Empty
            //
            case 233: {
               //#line 2262 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2262 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 235:  TypeDefModifiersopt ::= $Empty
            //
            case 235: {
               //#line 2268 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2268 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 237:  ClockedClauseopt ::= $Empty
            //
            case 237: {
               //#line 2274 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2274 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 239:  identifier ::= IDENTIFIER$ident
            //
            case 239: {
               //#line 2285 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2283 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2285 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 240:  TypeName ::= Identifier
            //
            case 240: {
               //#line 2292 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2290 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2292 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 241:  TypeName ::= TypeName . Identifier
            //
            case 241: {
               //#line 2297 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2295 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2295 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2297 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 243:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 243: {
               //#line 2309 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2307 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2309 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 244:  TypeArgumentList ::= Type
            //
            case 244: {
               //#line 2316 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2314 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2316 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 245:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 245: {
               //#line 2323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2321 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2321 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 246:  PackageName ::= Identifier
            //
            case 246: {
               //#line 2333 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2331 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2333 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 247:  PackageName ::= PackageName . Identifier
            //
            case 247: {
               //#line 2338 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2336 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2336 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2338 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 248:  ExpressionName ::= Identifier
            //
            case 248: {
               //#line 2354 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2352 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2354 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 249:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 249: {
               //#line 2359 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2357 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2357 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2359 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 250:  MethodName ::= Identifier
            //
            case 250: {
               //#line 2369 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2367 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2369 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 251:  MethodName ::= AmbiguousName . Identifier
            //
            case 251: {
               //#line 2374 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2372 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2372 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2374 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 252:  PackageOrTypeName ::= Identifier
            //
            case 252: {
               //#line 2384 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2382 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2384 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 253:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 253: {
               //#line 2389 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2387 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2387 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2389 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 254:  AmbiguousName ::= Identifier
            //
            case 254: {
               //#line 2399 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2397 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2399 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 255:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 255: {
               //#line 2404 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2402 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2402 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2404 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 256:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 256: {
               //#line 2416 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2414 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2414 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2414 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2416 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 257:  ImportDeclarations ::= ImportDeclaration
            //
            case 257: {
               //#line 2432 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2430 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2432 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 258:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 258: {
               //#line 2439 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2437 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2437 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2439 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 259:  TypeDeclarations ::= TypeDeclaration
            //
            case 259: {
               //#line 2447 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2445 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2447 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 260:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 260: {
               //#line 2455 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2453 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2453 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2455 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 261:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 261: {
               //#line 2463 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2463 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn.position(pos()));
                      break;
            }
    
            //
            // Rule 264:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 264: {
               //#line 2477 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2477 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 265:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 265: {
               //#line 2483 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2481 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2483 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 269:  TypeDeclaration ::= ;
            //
            case 269: {
               //#line 2498 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2498 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 270:  ClassModifiers ::= ClassModifier
            //
            case 270: {
               //#line 2506 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2504 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2506 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 271:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 271: {
               //#line 2513 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2511 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2511 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2513 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassModifiers.addAll(ClassModifier);
                      break;
            }
    
            //
            // Rule 272:  ClassModifier ::= Annotation
            //
            case 272: {
               //#line 2519 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2517 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2519 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 273:  ClassModifier ::= public
            //
            case 273: {
               //#line 2524 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2524 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 274:  ClassModifier ::= protected
            //
            case 274: {
               //#line 2529 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2529 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 275:  ClassModifier ::= private
            //
            case 275: {
               //#line 2534 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2534 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 276:  ClassModifier ::= abstract
            //
            case 276: {
               //#line 2539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 277:  ClassModifier ::= static
            //
            case 277: {
               //#line 2544 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2544 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 278:  ClassModifier ::= final
            //
            case 278: {
               //#line 2549 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2549 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 279:  ClassModifier ::= safe
            //
            case 279: {
               //#line 2554 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2554 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 280:  TypeDefModifiers ::= TypeDefModifier
            //
            case 280: {
               //#line 2560 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2558 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2560 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 281:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 281: {
               //#line 2567 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2565 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2565 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2567 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                      break;
            }
    
            //
            // Rule 282:  TypeDefModifier ::= Annotation
            //
            case 282: {
               //#line 2573 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2571 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2573 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 283:  TypeDefModifier ::= public
            //
            case 283: {
               //#line 2578 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2578 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 284:  TypeDefModifier ::= protected
            //
            case 284: {
               //#line 2583 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2583 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 285:  TypeDefModifier ::= private
            //
            case 285: {
               //#line 2588 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2588 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 286:  TypeDefModifier ::= abstract
            //
            case 286: {
               //#line 2593 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2593 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 287:  TypeDefModifier ::= static
            //
            case 287: {
               //#line 2598 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2598 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 288:  TypeDefModifier ::= final
            //
            case 288: {
               //#line 2603 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2603 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 289:  Interfaces ::= implements InterfaceTypeList
            //
            case 289: {
               //#line 2612 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2610 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2612 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 290:  InterfaceTypeList ::= Type
            //
            case 290: {
               //#line 2618 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2616 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2618 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 291:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 291: {
               //#line 2625 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2623 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2623 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2625 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 292:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 292: {
               //#line 2635 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2633 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2635 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 294:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 294: {
               //#line 2642 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2640 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2640 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2642 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 296:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 296: {
               //#line 2664 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2662 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2664 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 298:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 298: {
               //#line 2673 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2671 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2673 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 299:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 299: {
               //#line 2680 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2678 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2680 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 300:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 300: {
               //#line 2687 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2685 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2687 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 301:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 301: {
               //#line 2694 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2692 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2694 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 302:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 302: {
               //#line 2701 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2699 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2701 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 303:  ClassMemberDeclaration ::= ;
            //
            case 303: {
               //#line 2708 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2708 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 304:  FormalDeclarators ::= FormalDeclarator
            //
            case 304: {
               //#line 2715 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2713 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2715 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 305:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 305: {
               //#line 2722 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2720 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2720 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2722 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 306:  FieldDeclarators ::= FieldDeclarator
            //
            case 306: {
               //#line 2729 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2727 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2729 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 307:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 307: {
               //#line 2736 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2734 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2734 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2736 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 308:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 308: {
               //#line 2744 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2742 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 2744 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 309:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 309: {
               //#line 2751 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2749 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 2749 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 2751 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 310:  VariableDeclarators ::= VariableDeclarator
            //
            case 310: {
               //#line 2758 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2756 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2758 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 311:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 311: {
               //#line 2765 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2763 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2763 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2765 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 313:  FieldModifiers ::= FieldModifier
            //
            case 313: {
               //#line 2774 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2772 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2774 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 314:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 314: {
               //#line 2781 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2779 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2779 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2781 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldModifiers.addAll(FieldModifier);
                      break;
            }
    
            //
            // Rule 315:  FieldModifier ::= Annotation
            //
            case 315: {
               //#line 2787 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2785 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2787 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 316:  FieldModifier ::= public
            //
            case 316: {
               //#line 2792 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2792 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 317:  FieldModifier ::= protected
            //
            case 317: {
               //#line 2797 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2797 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 318:  FieldModifier ::= private
            //
            case 318: {
               //#line 2802 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2802 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 319:  FieldModifier ::= static
            //
            case 319: {
               //#line 2807 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2807 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 320:  FieldModifier ::= global
            //
            case 320: {
               //#line 2812 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2812 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 321:  ResultType ::= : Type
            //
            case 321: {
               //#line 2818 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2816 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2818 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 322:  HasResultType ::= : Type
            //
            case 322: {
               //#line 2823 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2821 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2823 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 323:  HasResultType ::= <: Type
            //
            case 323: {
               //#line 2828 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2826 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2828 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.HasType(Type));
                      break;
            }
    
            //
            // Rule 324:  FormalParameters ::= ( FormalParameterList )
            //
            case 324: {
               //#line 2834 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2832 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2834 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterList);
                      break;
            }
    
            //
            // Rule 325:  FormalParameterList ::= FormalParameter
            //
            case 325: {
               //#line 2840 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2838 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2840 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 326:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 326: {
               //#line 2847 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2845 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2845 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2847 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 327:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 327: {
               //#line 2853 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2851 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2851 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2853 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 328:  LoopIndexDeclarator ::= ( IdentifierList ) HasResultTypeopt
            //
            case 328: {
               //#line 2858 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2856 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2856 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2858 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 329:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt
            //
            case 329: {
               //#line 2863 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2861 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2861 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2861 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2863 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 330:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 330: {
               //#line 2869 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2867 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2867 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2869 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 331:  LoopIndex ::= VariableModifiersopt VarKeyword LoopIndexDeclarator
            //
            case 331: {
               //#line 2892 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2890 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2890 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2890 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2892 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 332:  FormalParameter ::= VariableModifiersopt FormalDeclarator
            //
            case 332: {
               //#line 2916 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2914 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2914 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2916 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 333:  FormalParameter ::= VariableModifiersopt VarKeyword FormalDeclarator
            //
            case 333: {
               //#line 2940 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2938 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2938 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2938 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2940 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 334:  FormalParameter ::= Type
            //
            case 334: {
               //#line 2964 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2962 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2964 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh("id$")), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 335:  VariableModifiers ::= VariableModifier
            //
            case 335: {
               //#line 2972 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2970 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2972 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 336:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 336: {
               //#line 2979 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2977 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2977 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2979 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableModifiers.addAll(VariableModifier);
                      break;
            }
    
            //
            // Rule 337:  VariableModifier ::= Annotation
            //
            case 337: {
               //#line 2985 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2983 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2985 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 338:  VariableModifier ::= shared
            //
            case 338: {
               //#line 2990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                      break;
            }
    
            //
            // Rule 339:  MethodModifiers ::= MethodModifier
            //
            case 339: {
               //#line 2999 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2997 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2999 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 340:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 340: {
               //#line 3006 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3004 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 3004 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 3006 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiers.addAll(MethodModifier);
                      break;
            }
    
            //
            // Rule 341:  MethodModifier ::= Annotation
            //
            case 341: {
               //#line 3012 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3010 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3012 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 342:  MethodModifier ::= public
            //
            case 342: {
               //#line 3017 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3017 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 343:  MethodModifier ::= protected
            //
            case 343: {
               //#line 3022 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3022 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 344:  MethodModifier ::= private
            //
            case 344: {
               //#line 3027 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3027 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 345:  MethodModifier ::= abstract
            //
            case 345: {
               //#line 3032 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3032 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 346:  MethodModifier ::= static
            //
            case 346: {
               //#line 3037 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3037 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 347:  MethodModifier ::= final
            //
            case 347: {
               //#line 3042 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3042 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 348:  MethodModifier ::= native
            //
            case 348: {
               //#line 3047 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3047 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 349:  MethodModifier ::= atomic
            //
            case 349: {
               //#line 3052 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3052 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                      break;
            }
    
            //
            // Rule 350:  MethodModifier ::= extern
            //
            case 350: {
               //#line 3057 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3057 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
                      break;
            }
    
            //
            // Rule 351:  MethodModifier ::= safe
            //
            case 351: {
               //#line 3062 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3062 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 352:  MethodModifier ::= sequential
            //
            case 352: {
               //#line 3067 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3067 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                      break;
            }
    
            //
            // Rule 353:  MethodModifier ::= nonblocking
            //
            case 353: {
               //#line 3072 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3072 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                      break;
            }
    
            //
            // Rule 354:  MethodModifier ::= incomplete
            //
            case 354: {
               //#line 3077 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3077 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                      break;
            }
    
            //
            // Rule 355:  MethodModifier ::= property
            //
            case 355: {
               //#line 3082 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3082 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                      break;
            }
    
            //
            // Rule 356:  MethodModifier ::= global
            //
            case 356: {
               //#line 3087 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3087 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 357:  MethodModifier ::= proto
            //
            case 357: {
               //#line 3092 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3092 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROTO)));
                      break;
            }
    
            //
            // Rule 358:  Throws ::= throws ExceptionTypeList
            //
            case 358: {
               //#line 3099 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3097 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 3099 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 359:  Offers ::= offers Type
            //
            case 359: {
               //#line 3104 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3102 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3104 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 360:  ExceptionTypeList ::= ExceptionType
            //
            case 360: {
               //#line 3110 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3108 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3110 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 361:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 361: {
               //#line 3117 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3115 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3115 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3117 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 363:  MethodBody ::= = LastExpression ;
            //
            case 363: {
               //#line 3125 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3123 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3125 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 364:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 364: {
               //#line 3130 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3128 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3128 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(4);
                //#line 3128 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3130 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3138 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3136 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3136 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3138 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 366:  MethodBody ::= Annotationsopt Block
            //
            case 366: {
               //#line 3143 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3141 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 3141 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3143 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3163 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3161 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3163 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 369:  ConstructorModifiers ::= ConstructorModifier
            //
            case 369: {
               //#line 3169 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3167 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 3169 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 370:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 370: {
               //#line 3176 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3174 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 3174 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 3176 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                      break;
            }
    
            //
            // Rule 371:  ConstructorModifier ::= Annotation
            //
            case 371: {
               //#line 3182 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3180 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3182 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 372:  ConstructorModifier ::= public
            //
            case 372: {
               //#line 3187 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3187 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 373:  ConstructorModifier ::= protected
            //
            case 373: {
               //#line 3192 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3192 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 374:  ConstructorModifier ::= private
            //
            case 374: {
               //#line 3197 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3197 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 375:  ConstructorModifier ::= native
            //
            case 375: {
               //#line 3202 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3202 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 376:  ConstructorBody ::= = ConstructorBlock
            //
            case 376: {
               //#line 3208 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3206 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3208 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 377:  ConstructorBody ::= ConstructorBlock
            //
            case 377: {
               //#line 3213 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3211 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3213 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 378:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 378: {
               //#line 3218 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3216 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3218 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3226 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3226 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
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
               //#line 3237 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3235 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3235 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3237 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 382:  Arguments ::= ( ArgumentListopt )
            //
            case 382: {
               //#line 3250 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3248 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3250 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 384:  InterfaceModifiers ::= InterfaceModifier
            //
            case 384: {
               //#line 3260 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3258 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 3260 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 385:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 385: {
               //#line 3267 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3265 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 3265 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 3267 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                      break;
            }
    
            //
            // Rule 386:  InterfaceModifier ::= Annotation
            //
            case 386: {
               //#line 3273 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3271 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3273 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 387:  InterfaceModifier ::= public
            //
            case 387: {
               //#line 3278 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3278 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 388:  InterfaceModifier ::= protected
            //
            case 388: {
               //#line 3283 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3283 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 389:  InterfaceModifier ::= private
            //
            case 389: {
               //#line 3288 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3288 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 390:  InterfaceModifier ::= abstract
            //
            case 390: {
               //#line 3293 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3293 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 391:  InterfaceModifier ::= static
            //
            case 391: {
               //#line 3298 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3298 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 392:  ExtendsInterfaces ::= extends Type
            //
            case 392: {
               //#line 3304 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3302 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3304 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 393:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 393: {
               //#line 3311 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3309 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3309 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3311 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 394:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 394: {
               //#line 3320 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3320 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 396:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 396: {
               //#line 3327 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3325 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3325 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3327 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 397:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 397: {
               //#line 3334 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3332 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3334 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 398:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 398: {
               //#line 3341 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3339 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3341 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 399:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 399: {
               //#line 3348 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3346 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3348 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 400:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 400: {
               //#line 3355 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3353 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3355 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 401:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 401: {
               //#line 3362 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3360 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3362 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 402:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 402: {
               //#line 3369 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3367 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3369 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 403:  InterfaceMemberDeclaration ::= ;
            //
            case 403: {
               //#line 3376 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3376 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 404:  Annotations ::= Annotation
            //
            case 404: {
               //#line 3382 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3380 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3382 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 405:  Annotations ::= Annotations Annotation
            //
            case 405: {
               //#line 3389 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3387 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3387 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3389 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 406:  Annotation ::= @ NamedType
            //
            case 406: {
               //#line 3395 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3393 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3395 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 407:  SimpleName ::= Identifier
            //
            case 407: {
               //#line 3401 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3399 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3401 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 408:  Identifier ::= identifier
            //
            case 408: {
               //#line 3407 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3405 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3407 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 409:  VariableInitializers ::= VariableInitializer
            //
            case 409: {
               //#line 3415 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3413 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3415 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 410:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 410: {
               //#line 3422 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3420 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3420 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3422 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                      break;
            }
    
            //
            // Rule 411:  Block ::= { BlockStatementsopt }
            //
            case 411: {
               //#line 3440 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3438 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3440 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 412:  BlockStatements ::= BlockStatement
            //
            case 412: {
               //#line 3446 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3444 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3446 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 413:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 413: {
               //#line 3453 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3451 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3451 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3453 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 415:  BlockStatement ::= ClassDeclaration
            //
            case 415: {
               //#line 3461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3459 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 416:  BlockStatement ::= TypeDefDeclaration
            //
            case 416: {
               //#line 3468 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3466 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3468 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 417:  BlockStatement ::= Statement
            //
            case 417: {
               //#line 3475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3473 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 418:  IdentifierList ::= Identifier
            //
            case 418: {
               //#line 3483 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3481 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3483 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 419:  IdentifierList ::= IdentifierList , Identifier
            //
            case 419: {
               //#line 3490 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3488 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3488 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3490 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 420:  FormalDeclarator ::= Identifier ResultType
            //
            case 420: {
               //#line 3496 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3494 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3494 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3496 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 421:  FormalDeclarator ::= ( IdentifierList ) ResultType
            //
            case 421: {
               //#line 3501 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3499 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3499 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3501 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 422:  FormalDeclarator ::= Identifier ( IdentifierList ) ResultType
            //
            case 422: {
               //#line 3506 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3504 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3504 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3504 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3506 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 423:  FieldDeclarator ::= Identifier HasResultType
            //
            case 423: {
               //#line 3512 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3510 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3510 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3512 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultType, null });
                      break;
            }
    
            //
            // Rule 424:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 424: {
               //#line 3517 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3515 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3515 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3515 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3517 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 425:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 425: {
               //#line 3523 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3521 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3521 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3521 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3523 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 426:  VariableDeclarator ::= ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 426: {
               //#line 3528 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3528 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 427:  VariableDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 427: {
               //#line 3533 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3531 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3531 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3531 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3531 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3533 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 428:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 428: {
               //#line 3539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3537 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3537 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3537 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 429:  VariableDeclaratorWithType ::= ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 429: {
               //#line 3544 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3542 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3542 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(4);
                //#line 3542 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3544 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 430:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 430: {
               //#line 3549 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3547 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3547 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3547 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(5);
                //#line 3547 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3549 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 432:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 432: {
               //#line 3557 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3557 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 433:  LocalVariableDeclaration ::= VariableModifiersopt VariableDeclaratorsWithType
            //
            case 433: {
               //#line 3590 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3588 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3588 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 3590 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 434:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword FormalDeclarators
            //
            case 434: {
               //#line 3624 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3622 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3622 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3622 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3624 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 435:  Primary ::= here
            //
            case 435: {
               //#line 3665 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3665 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 436:  Primary ::= [ ArgumentListopt ]
            //
            case 436: {
               //#line 3671 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3669 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3671 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 438:  Primary ::= self
            //
            case 438: {
               //#line 3679 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3679 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 439:  Primary ::= this
            //
            case 439: {
               //#line 3684 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3684 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 440:  Primary ::= ClassName . this
            //
            case 440: {
               //#line 3689 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3687 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3689 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 441:  Primary ::= ( Expression )
            //
            case 441: {
               //#line 3694 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3692 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3694 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 447:  OperatorFunction ::= TypeName . +
            //
            case 447: {
               //#line 3705 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3703 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3705 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3716 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3714 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3716 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3727 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3725 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3727 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3738 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3736 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3738 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3749 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3747 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3749 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3760 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3758 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3760 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3771 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3769 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3771 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3782 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3780 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3782 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3793 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3791 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3793 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3804 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3802 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3804 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3815 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3813 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3815 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3826 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3824 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3826 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3837 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3835 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3837 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3848 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3846 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3848 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3859 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3857 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3859 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3870 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3868 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3870 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3881 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3879 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3881 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3894 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3892 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3894 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 465:  Literal ::= LongLiteral$lit
            //
            case 465: {
               //#line 3900 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3898 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3900 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 466:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 466: {
               //#line 3906 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3904 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3906 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 467:  Literal ::= UnsignedLongLiteral$lit
            //
            case 467: {
               //#line 3912 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3910 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3912 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 468:  Literal ::= FloatingPointLiteral$lit
            //
            case 468: {
               //#line 3918 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3916 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3918 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 469:  Literal ::= DoubleLiteral$lit
            //
            case 469: {
               //#line 3924 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3922 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3924 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 470:  Literal ::= BooleanLiteral
            //
            case 470: {
               //#line 3930 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3928 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3930 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 471:  Literal ::= CharacterLiteral$lit
            //
            case 471: {
               //#line 3935 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3933 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3935 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 472:  Literal ::= StringLiteral$str
            //
            case 472: {
               //#line 3941 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3939 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3941 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 473:  Literal ::= null
            //
            case 473: {
               //#line 3947 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3947 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 474:  BooleanLiteral ::= true$trueLiteral
            //
            case 474: {
               //#line 3953 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3951 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3953 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 475:  BooleanLiteral ::= false$falseLiteral
            //
            case 475: {
               //#line 3958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3956 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 476:  ArgumentList ::= Expression
            //
            case 476: {
               //#line 3967 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3965 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3967 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 477:  ArgumentList ::= ArgumentList , Expression
            //
            case 477: {
               //#line 3974 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3972 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3972 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3974 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 478:  FieldAccess ::= Primary . Identifier
            //
            case 478: {
               //#line 3980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3978 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3978 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 479:  FieldAccess ::= super . Identifier
            //
            case 479: {
               //#line 3985 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3983 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3985 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 480:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 480: {
               //#line 3990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3988 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3988 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3988 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 481:  FieldAccess ::= Primary . class$c
            //
            case 481: {
               //#line 3995 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3993 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3993 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3995 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 482:  FieldAccess ::= super . class$c
            //
            case 482: {
               //#line 4000 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4000 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 483:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 483: {
               //#line 4005 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4003 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4003 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4003 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 4005 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 484:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 484: {
               //#line 4011 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4009 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4009 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 4009 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 4011 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 485:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 485: {
               //#line 4018 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 4016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 4018 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 486:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 486: {
               //#line 4023 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4021 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4021 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 4021 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 4023 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 487:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 487: {
               //#line 4028 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 4026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 4028 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 488:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 488: {
               //#line 4033 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4031 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4031 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 4031 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 4033 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4053 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4051 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4051 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(4);
                //#line 4053 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4066 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4064 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4064 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4064 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4066 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4078 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4076 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4076 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4078 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4090 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4088 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4088 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4088 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4088 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(8);
                //#line 4090 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 496:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 496: {
               //#line 4108 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4106 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4108 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 497:  PostDecrementExpression ::= PostfixExpression --
            //
            case 497: {
               //#line 4114 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4112 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4114 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 500:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 500: {
               //#line 4122 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4120 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4122 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 501:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 501: {
               //#line 4127 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4125 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4127 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 503:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 503: {
               //#line 4134 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4132 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4134 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 504:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 504: {
               //#line 4140 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4138 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4140 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 506:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 506: {
               //#line 4147 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4147 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 507:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 507: {
               //#line 4152 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4150 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4150 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4152 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e.position(pos()));
                      break;
            }
    
            //
            // Rule 508:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 508: {
               //#line 4159 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4157 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4159 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 510:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 510: {
               //#line 4166 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4166 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 511:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 511: {
               //#line 4171 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4169 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4169 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4171 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 512:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 512: {
               //#line 4176 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4174 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4174 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4176 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 514:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 514: {
               //#line 4183 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4181 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4181 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4183 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 515:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 515: {
               //#line 4188 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4186 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4186 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4188 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 517:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 517: {
               //#line 4195 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4193 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4193 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4195 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 518:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 518: {
               //#line 4200 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4198 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4198 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4200 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 519:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 519: {
               //#line 4205 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4203 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4203 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4205 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 521:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 521: {
               //#line 4212 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4210 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4210 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4212 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 524:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 524: {
               //#line 4221 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4221 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 525:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 525: {
               //#line 4226 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4226 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 526:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 526: {
               //#line 4231 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4229 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4229 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4231 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 527:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 527: {
               //#line 4236 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4234 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4234 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4236 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 528:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 528: {
               //#line 4241 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4239 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4239 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4241 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 529:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 529: {
               //#line 4246 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4244 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4244 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4246 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 531:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 531: {
               //#line 4253 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4251 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4251 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4253 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 532:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 532: {
               //#line 4258 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4258 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 533:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 533: {
               //#line 4263 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4261 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4261 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4263 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 535:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 535: {
               //#line 4270 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4268 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4268 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4270 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 537:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 537: {
               //#line 4277 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4275 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4275 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4277 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 539:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 539: {
               //#line 4284 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4282 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4282 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4284 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 541:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 541: {
               //#line 4291 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4289 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4289 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4291 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 543:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 543: {
               //#line 4298 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4296 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4296 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4298 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 550:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 550: {
               //#line 4311 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4309 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4309 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4309 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4311 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 553:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 553: {
               //#line 4320 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4320 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 554:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 554: {
               //#line 4325 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4325 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 555:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 555: {
               //#line 4330 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4328 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4328 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4328 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4328 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4330 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 556:  LeftHandSide ::= ExpressionName
            //
            case 556: {
               //#line 4336 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4334 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4336 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 558:  AssignmentOperator ::= =
            //
            case 558: {
               //#line 4343 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4343 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 559:  AssignmentOperator ::= *=
            //
            case 559: {
               //#line 4348 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4348 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 560:  AssignmentOperator ::= /=
            //
            case 560: {
               //#line 4353 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4353 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 561:  AssignmentOperator ::= %=
            //
            case 561: {
               //#line 4358 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4358 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 562:  AssignmentOperator ::= +=
            //
            case 562: {
               //#line 4363 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4363 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 563:  AssignmentOperator ::= -=
            //
            case 563: {
               //#line 4368 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4368 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 564:  AssignmentOperator ::= <<=
            //
            case 564: {
               //#line 4373 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4373 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 565:  AssignmentOperator ::= >>=
            //
            case 565: {
               //#line 4378 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4378 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 566:  AssignmentOperator ::= >>>=
            //
            case 566: {
               //#line 4383 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4383 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 567:  AssignmentOperator ::= &=
            //
            case 567: {
               //#line 4388 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4388 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 568:  AssignmentOperator ::= ^=
            //
            case 568: {
               //#line 4393 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4393 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 569:  AssignmentOperator ::= |=
            //
            case 569: {
               //#line 4398 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4398 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 572:  PrefixOp ::= +
            //
            case 572: {
               //#line 4409 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4409 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 573:  PrefixOp ::= -
            //
            case 573: {
               //#line 4414 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4414 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 574:  PrefixOp ::= !
            //
            case 574: {
               //#line 4419 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4419 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 575:  PrefixOp ::= ~
            //
            case 575: {
               //#line 4424 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4424 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 576:  BinOp ::= +
            //
            case 576: {
               //#line 4430 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4430 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 577:  BinOp ::= -
            //
            case 577: {
               //#line 4435 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4435 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 578:  BinOp ::= *
            //
            case 578: {
               //#line 4440 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4440 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 579:  BinOp ::= /
            //
            case 579: {
               //#line 4445 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4445 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 580:  BinOp ::= %
            //
            case 580: {
               //#line 4450 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4450 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 581:  BinOp ::= &
            //
            case 581: {
               //#line 4455 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4455 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 582:  BinOp ::= |
            //
            case 582: {
               //#line 4460 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4460 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 583:  BinOp ::= ^
            //
            case 583: {
               //#line 4465 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4465 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 584:  BinOp ::= &&
            //
            case 584: {
               //#line 4470 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4470 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 585:  BinOp ::= ||
            //
            case 585: {
               //#line 4475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 586:  BinOp ::= <<
            //
            case 586: {
               //#line 4480 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4480 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 587:  BinOp ::= >>
            //
            case 587: {
               //#line 4485 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4485 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 588:  BinOp ::= >>>
            //
            case 588: {
               //#line 4490 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4490 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 589:  BinOp ::= >=
            //
            case 589: {
               //#line 4495 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4495 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 590:  BinOp ::= <=
            //
            case 590: {
               //#line 4500 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4500 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 591:  BinOp ::= >
            //
            case 591: {
               //#line 4505 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4505 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 592:  BinOp ::= <
            //
            case 592: {
               //#line 4510 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4510 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 593:  BinOp ::= ==
            //
            case 593: {
               //#line 4518 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4518 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 594:  BinOp ::= !=
            //
            case 594: {
               //#line 4523 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4523 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 595:  Catchesopt ::= $Empty
            //
            case 595: {
               //#line 4532 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4532 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                      break;
            }
    
            //
            // Rule 597:  Identifieropt ::= $Empty
            //
            case 597:
                setResult(null);
                break;

            //
            // Rule 598:  Identifieropt ::= Identifier
            //
            case 598: {
               //#line 4541 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4541 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 599:  ForUpdateopt ::= $Empty
            //
            case 599: {
               //#line 4547 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4547 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                      break;
            }
    
            //
            // Rule 601:  Expressionopt ::= $Empty
            //
            case 601:
                setResult(null);
                break;

            //
            // Rule 603:  ForInitopt ::= $Empty
            //
            case 603: {
               //#line 4558 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4558 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 605:  SwitchLabelsopt ::= $Empty
            //
            case 605: {
               //#line 4565 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4565 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 607:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 607: {
               //#line 4572 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4572 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 609:  VariableModifiersopt ::= $Empty
            //
            case 609: {
               //#line 4579 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4579 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 611:  VariableInitializersopt ::= $Empty
            //
            case 611:
                setResult(null);
                break;

            //
            // Rule 613:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 613: {
               //#line 4590 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4590 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 615:  ExtendsInterfacesopt ::= $Empty
            //
            case 615: {
               //#line 4597 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4597 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 617:  InterfaceModifiersopt ::= $Empty
            //
            case 617: {
               //#line 4604 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4604 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 619:  ClassBodyopt ::= $Empty
            //
            case 619:
                setResult(null);
                break;

            //
            // Rule 621:  Argumentsopt ::= $Empty
            //
            case 621: {
               //#line 4615 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4615 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 623:  ArgumentListopt ::= $Empty
            //
            case 623: {
               //#line 4622 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4622 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 625:  BlockStatementsopt ::= $Empty
            //
            case 625: {
               //#line 4629 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4629 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                      break;
            }
    
            //
            // Rule 627:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 627:
                setResult(null);
                break;

            //
            // Rule 629:  ConstructorModifiersopt ::= $Empty
            //
            case 629: {
               //#line 4640 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4640 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 631:  FormalParameterListopt ::= $Empty
            //
            case 631: {
               //#line 4647 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4647 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 633:  Throwsopt ::= $Empty
            //
            case 633: {
               //#line 4654 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4654 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 635:  Offersopt ::= $Empty
            //
            case 635: {
               //#line 4660 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4660 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 637:  MethodModifiersopt ::= $Empty
            //
            case 637: {
               //#line 4667 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4667 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 639:  TypeModifieropt ::= $Empty
            //
            case 639: {
               //#line 4674 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4674 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 641:  FieldModifiersopt ::= $Empty
            //
            case 641: {
               //#line 4681 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4681 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 643:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 643: {
               //#line 4688 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4688 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 645:  Interfacesopt ::= $Empty
            //
            case 645: {
               //#line 4695 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4695 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 647:  Superopt ::= $Empty
            //
            case 647:
                setResult(null);
                break;

            //
            // Rule 649:  TypeParametersopt ::= $Empty
            //
            case 649: {
               //#line 4706 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4706 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 651:  FormalParametersopt ::= $Empty
            //
            case 651: {
               //#line 4713 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4713 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 653:  Annotationsopt ::= $Empty
            //
            case 653: {
               //#line 4720 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4720 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 655:  TypeDeclarationsopt ::= $Empty
            //
            case 655: {
               //#line 4727 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4727 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 657:  ImportDeclarationsopt ::= $Empty
            //
            case 657: {
               //#line 4734 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4734 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                      break;
            }
    
            //
            // Rule 659:  PackageDeclarationopt ::= $Empty
            //
            case 659:
                setResult(null);
                break;

            //
            // Rule 661:  ResultTypeopt ::= $Empty
            //
            case 661:
                setResult(null);
                break;

            //
            // Rule 663:  HasResultTypeopt ::= $Empty
            //
            case 663:
                setResult(null);
                break;

            //
            // Rule 665:  TypeArgumentsopt ::= $Empty
            //
            case 665: {
               //#line 4752 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4752 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 667:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 667: {
               //#line 4759 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4759 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 669:  Propertiesopt ::= $Empty
            //
            case 669: {
               //#line 4766 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4766 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

