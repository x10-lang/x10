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
            // Rule 75:  Conjunction ::= Expression
            //
            case 75: {
               //#line 1451 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1449 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1451 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 76:  Conjunction ::= Conjunction , Expression
            //
            case 76: {
               //#line 1458 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1456 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1456 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1458 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 77:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 77: {
               //#line 1464 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1462 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1462 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1464 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 78:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 78: {
               //#line 1469 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1467 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1467 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1469 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 79:  WhereClause ::= DepParameters
            //
            case 79: {
               //#line 1475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1473 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 80:  Conjunctionopt ::= $Empty
            //
            case 80: {
               //#line 1481 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1481 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                setResult(l);
                      break;
            }
      
            //
            // Rule 81:  Conjunctionopt ::= Conjunction
            //
            case 81: {
               //#line 1487 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1485 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1487 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(Conjunction);
                      break;
            }
    
            //
            // Rule 82:  ExistentialListopt ::= $Empty
            //
            case 82: {
               //#line 1493 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1493 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 83:  ExistentialListopt ::= ExistentialList ;
            //
            case 83: {
               //#line 1498 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1496 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1498 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 84:  ExistentialList ::= FormalParameter
            //
            case 84: {
               //#line 1504 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1502 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1504 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 85:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 85: {
               //#line 1511 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1509 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1509 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1511 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 88:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 88: {
               //#line 1522 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1520 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1520 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1520 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1520 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1520 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1520 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1520 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1520 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1522 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 89:  StructDeclaration ::= ClassModifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 89: {
               //#line 1538 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1536 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1536 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1536 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1536 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1536 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1536 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1536 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1538 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 90:  ConstructorDeclaration ::= ConstructorModifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt ConstructorBody
            //
            case 90: {
               //#line 1552 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1550 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1550 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1550 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1550 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1550 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1550 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1550 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1550 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(10);
                //#line 1552 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 91:  Super ::= extends ClassType
            //
            case 91: {
               //#line 1569 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1567 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1569 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 92:  FieldKeyword ::= val
            //
            case 92: {
               //#line 1575 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1575 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 93:  FieldKeyword ::= var
            //
            case 93: {
               //#line 1580 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1580 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 94:  FieldKeyword ::= const
            //
            case 94: {
               //#line 1585 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1585 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                      break;
            }
    
            //
            // Rule 95:  VarKeyword ::= val
            //
            case 95: {
               //#line 1593 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1593 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 96:  VarKeyword ::= var
            //
            case 96: {
               //#line 1598 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1598 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 97:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 97: {
               //#line 1605 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1603 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1603 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1603 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1605 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 98:  FieldDeclaration ::= FieldModifiersopt FieldDeclarators ;
            //
            case 98: {
               //#line 1630 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1628 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1628 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 1630 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 101:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 101: {
               //#line 1662 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1660 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 1660 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt NonExpressionStatement = (Stmt) getRhsSym(2);
                //#line 1662 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                if (NonExpressionStatement.ext() instanceof X10Ext && Annotationsopt instanceof List) {
                    NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations((List) Annotationsopt);
                }
                setResult(NonExpressionStatement.position(pos()));
                      break;
            }
    
            //
            // Rule 128:  OfferStatement ::= offer Expression ;
            //
            case 128: {
               //#line 1698 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1696 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1698 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Offer(pos(), Expression));
                      break;
            }
    
            //
            // Rule 129:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 129: {
               //#line 1704 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1702 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1702 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1704 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 130:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 130: {
               //#line 1710 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1708 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1708 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1708 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1710 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 131:  EmptyStatement ::= ;
            //
            case 131: {
               //#line 1716 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1716 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 132:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 132: {
               //#line 1722 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1720 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1720 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 1722 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 138:  ExpressionStatement ::= StatementExpression ;
            //
            case 138: {
               //#line 1734 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1732 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1734 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 146:  AssertStatement ::= assert Expression ;
            //
            case 146: {
               //#line 1748 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1746 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1748 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 147:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 147: {
               //#line 1753 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1751 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1751 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1753 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 148:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 148: {
               //#line 1759 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1757 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1757 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1759 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 149:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 149: {
               //#line 1765 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1763 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1763 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1765 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 151:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 151: {
               //#line 1773 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1771 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1771 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1773 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 152:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 152: {
               //#line 1780 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1778 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1778 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1780 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 153:  SwitchLabels ::= SwitchLabel
            //
            case 153: {
               //#line 1789 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1787 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1789 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 154:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 154: {
               //#line 1796 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1794 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1794 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1796 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 155:  SwitchLabel ::= case ConstantExpression :
            //
            case 155: {
               //#line 1803 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1801 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1803 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 156:  SwitchLabel ::= default :
            //
            case 156: {
               //#line 1808 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1808 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 157:  WhileStatement ::= while ( Expression ) Statement
            //
            case 157: {
               //#line 1814 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1812 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1812 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1814 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 158:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 158: {
               //#line 1820 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1818 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1818 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1820 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 161:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 161: {
               //#line 1829 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1827 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1827 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1827 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1827 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1829 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 163:  ForInit ::= LocalVariableDeclaration
            //
            case 163: {
               //#line 1836 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1834 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1836 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 165:  StatementExpressionList ::= StatementExpression
            //
            case 165: {
               //#line 1846 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1844 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1846 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 166:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 166: {
               //#line 1853 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1851 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1851 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1853 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 167:  BreakStatement ::= break Identifieropt ;
            //
            case 167: {
               //#line 1859 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1857 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1859 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 168:  ContinueStatement ::= continue Identifieropt ;
            //
            case 168: {
               //#line 1865 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1863 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1865 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 169:  ReturnStatement ::= return Expressionopt ;
            //
            case 169: {
               //#line 1871 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1869 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1871 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 170:  ThrowStatement ::= throw Expression ;
            //
            case 170: {
               //#line 1877 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1875 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1877 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 171:  TryStatement ::= try Block Catches
            //
            case 171: {
               //#line 1883 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1881 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1881 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1883 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 172:  TryStatement ::= try Block Catchesopt Finally
            //
            case 172: {
               //#line 1888 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1886 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1886 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1886 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1888 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 173:  Catches ::= CatchClause
            //
            case 173: {
               //#line 1894 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1892 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1894 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 174:  Catches ::= Catches CatchClause
            //
            case 174: {
               //#line 1901 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1899 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1899 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1901 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 175:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 175: {
               //#line 1908 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1906 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1906 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1908 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 176:  Finally ::= finally Block
            //
            case 176: {
               //#line 1914 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1912 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1914 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 177:  ClockedClause ::= clocked ( ClockList )
            //
            case 177: {
               //#line 1920 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1918 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1920 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 178:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 178: {
               //#line 1926 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1924 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1924 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1924 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1926 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 179:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 179: {
               //#line 1935 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1933 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1933 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1935 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 180:  AtomicStatement ::= atomic Statement
            //
            case 180: {
               //#line 1941 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1939 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1941 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 181:  WhenStatement ::= when ( Expression ) Statement
            //
            case 181: {
               //#line 1948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1946 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1946 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 182:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 182: {
               //#line 1953 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1951 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1951 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1951 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1951 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1953 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 183:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 183: {
               //#line 1960 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1958 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1960 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 184:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 184: {
               //#line 1976 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1974 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1974 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1974 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1974 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1976 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 185:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 185: {
               //#line 1992 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1992 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 186:  FinishStatement ::= finish Statement
            //
            case 186: {
               //#line 2006 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2004 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2006 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 187:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 187: {
               //#line 2012 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2010 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 2012 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 189:  NextStatement ::= next ;
            //
            case 189: {
               //#line 2020 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2020 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 190:  AwaitStatement ::= await Expression ;
            //
            case 190: {
               //#line 2026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2024 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Await(pos(), Expression));
                      break;
            }
    
            //
            // Rule 191:  ClockList ::= Clock
            //
            case 191: {
               //#line 2032 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2030 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 2032 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 192:  ClockList ::= ClockList , Clock
            //
            case 192: {
               //#line 2039 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2037 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 2037 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 2039 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 193:  Clock ::= Expression
            //
            case 193: {
               //#line 2047 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2045 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2047 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 195:  CastExpression ::= ExpressionName
            //
            case 195: {
               //#line 2061 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2059 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 2061 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 196:  CastExpression ::= CastExpression as Type
            //
            case 196: {
               //#line 2066 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2064 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2064 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2066 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 197:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 197: {
               //#line 2073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2071 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 198:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 198: {
               //#line 2080 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2078 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(1);
                //#line 2078 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2080 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 199:  TypeParameterList ::= TypeParameter
            //
            case 199: {
               //#line 2087 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2085 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2087 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 200:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 200: {
               //#line 2094 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2092 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 2092 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2094 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 201:  TypeParamWithVariance ::= Identifier
            //
            case 201: {
               //#line 2101 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2099 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2101 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 202:  TypeParamWithVariance ::= + Identifier
            //
            case 202: {
               //#line 2106 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2104 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2106 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 203:  TypeParamWithVariance ::= - Identifier
            //
            case 203: {
               //#line 2111 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2111 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 204:  TypeParameter ::= Identifier
            //
            case 204: {
               //#line 2117 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2115 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2117 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 206:  RegionExpressionList ::= RegionExpression
            //
            case 206: {
               //#line 2125 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2123 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 2125 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 207:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 207: {
               //#line 2132 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2130 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 2130 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 2132 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                      break;
            }
    
            //
            // Rule 208:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 208: {
               //#line 2139 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2137 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2139 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 209:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt => ClosureBody
            //
            case 209: {
               //#line 2145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2143 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(1);
                //#line 2143 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2143 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2143 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(4);
                //#line 2143 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(5);
                //#line 2143 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 2145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 210:  LastExpression ::= Expression
            //
            case 210: {
               //#line 2152 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2150 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2152 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 211:  ClosureBody ::= ConditionalExpression
            //
            case 211: {
               //#line 2158 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2156 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 2158 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), ConditionalExpression, true)));
                      break;
            }
    
            //
            // Rule 212:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 212: {
               //#line 2163 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2161 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2161 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2161 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2163 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 213:  ClosureBody ::= Annotationsopt Block
            //
            case 213: {
               //#line 2173 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2171 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2171 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2173 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b.position(pos()));
                      break;
            }
    
            //
            // Rule 214:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 214: {
               //#line 2182 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2180 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2180 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2182 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 215:  AsyncExpression ::= async ClosureBody
            //
            case 215: {
               //#line 2188 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2186 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2188 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 216:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 216: {
               //#line 2193 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2191 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2191 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2193 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 217:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 217: {
               //#line 2198 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2196 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2196 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2198 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 218:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 218: {
               //#line 2203 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2201 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2201 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2201 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2203 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 219:  FinishExpression ::= finish ( Expression ) Block
            //
            case 219: {
               //#line 2210 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2208 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2208 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2210 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FinishExpr(pos(), Expression, Block));
                      break;
            }
    
            //
            // Rule 220:  FutureExpression ::= future ClosureBody
            //
            case 220: {
               //#line 2216 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2214 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2216 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 221:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 221: {
               //#line 2221 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2221 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 222:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 222: {
               //#line 2226 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2226 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 223:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 223: {
               //#line 2231 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2229 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2229 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2229 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2231 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 224:  DepParametersopt ::= $Empty
            //
            case 224:
                setResult(null);
                break;

            //
            // Rule 226:  PropertyListopt ::= $Empty
            //
            case 226: {
               //#line 2242 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2242 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
            //
            // Rule 228:  WhereClauseopt ::= $Empty
            //
            case 228:
                setResult(null);
                break;

            //
            // Rule 230:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 230:
                setResult(null);
                break;

            //
            // Rule 232:  ClassModifiersopt ::= $Empty
            //
            case 232: {
               //#line 2257 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2257 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 234:  TypeDefModifiersopt ::= $Empty
            //
            case 234: {
               //#line 2263 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2263 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 236:  ClockedClauseopt ::= $Empty
            //
            case 236: {
               //#line 2269 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2269 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 238:  identifier ::= IDENTIFIER$ident
            //
            case 238: {
               //#line 2280 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2278 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2280 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 239:  TypeName ::= Identifier
            //
            case 239: {
               //#line 2287 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2285 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2287 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 240:  TypeName ::= TypeName . Identifier
            //
            case 240: {
               //#line 2292 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2290 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2290 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2292 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 242:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 242: {
               //#line 2304 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2302 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2304 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 243:  TypeArgumentList ::= Type
            //
            case 243: {
               //#line 2311 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2309 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2311 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 244:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 244: {
               //#line 2318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2316 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2316 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 245:  PackageName ::= Identifier
            //
            case 245: {
               //#line 2328 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2326 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2328 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 246:  PackageName ::= PackageName . Identifier
            //
            case 246: {
               //#line 2333 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2331 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2331 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2333 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 247:  ExpressionName ::= Identifier
            //
            case 247: {
               //#line 2349 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2347 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2349 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 248:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 248: {
               //#line 2354 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2352 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2352 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2354 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 249:  MethodName ::= Identifier
            //
            case 249: {
               //#line 2364 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2362 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2364 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 250:  MethodName ::= AmbiguousName . Identifier
            //
            case 250: {
               //#line 2369 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2367 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2367 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2369 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 251:  PackageOrTypeName ::= Identifier
            //
            case 251: {
               //#line 2379 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2377 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2379 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 252:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 252: {
               //#line 2384 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2382 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2382 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2384 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 253:  AmbiguousName ::= Identifier
            //
            case 253: {
               //#line 2394 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2392 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2394 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 254:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 254: {
               //#line 2399 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2397 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2397 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2399 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 255:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 255: {
               //#line 2411 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2409 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2409 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2409 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2411 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 256:  ImportDeclarations ::= ImportDeclaration
            //
            case 256: {
               //#line 2427 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2425 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2427 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 257:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 257: {
               //#line 2434 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2432 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2432 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2434 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 258:  TypeDeclarations ::= TypeDeclaration
            //
            case 258: {
               //#line 2442 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2440 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2442 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 259:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 259: {
               //#line 2450 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2448 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2448 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2450 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 260:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 260: {
               //#line 2458 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2456 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2456 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2458 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn.position(pos()));
                      break;
            }
    
            //
            // Rule 263:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 263: {
               //#line 2472 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2470 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2472 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 264:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 264: {
               //#line 2478 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2476 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2478 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 268:  TypeDeclaration ::= ;
            //
            case 268: {
               //#line 2493 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2493 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 269:  ClassModifiers ::= ClassModifier
            //
            case 269: {
               //#line 2501 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2499 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2501 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 270:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 270: {
               //#line 2508 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2506 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2506 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2508 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassModifiers.addAll(ClassModifier);
                      break;
            }
    
            //
            // Rule 271:  ClassModifier ::= Annotation
            //
            case 271: {
               //#line 2514 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2512 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2514 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 272:  ClassModifier ::= public
            //
            case 272: {
               //#line 2519 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2519 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 273:  ClassModifier ::= protected
            //
            case 273: {
               //#line 2524 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2524 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 274:  ClassModifier ::= private
            //
            case 274: {
               //#line 2529 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2529 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 275:  ClassModifier ::= abstract
            //
            case 275: {
               //#line 2534 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2534 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 276:  ClassModifier ::= static
            //
            case 276: {
               //#line 2539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 277:  ClassModifier ::= final
            //
            case 277: {
               //#line 2544 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2544 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 278:  ClassModifier ::= safe
            //
            case 278: {
               //#line 2549 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2549 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 279:  TypeDefModifiers ::= TypeDefModifier
            //
            case 279: {
               //#line 2555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2553 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2555 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 280:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 280: {
               //#line 2562 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2560 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2560 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2562 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                      break;
            }
    
            //
            // Rule 281:  TypeDefModifier ::= Annotation
            //
            case 281: {
               //#line 2568 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2566 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2568 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 282:  TypeDefModifier ::= public
            //
            case 282: {
               //#line 2573 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2573 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 283:  TypeDefModifier ::= protected
            //
            case 283: {
               //#line 2578 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2578 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 284:  TypeDefModifier ::= private
            //
            case 284: {
               //#line 2583 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2583 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 285:  TypeDefModifier ::= abstract
            //
            case 285: {
               //#line 2588 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2588 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 286:  TypeDefModifier ::= static
            //
            case 286: {
               //#line 2593 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2593 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 287:  TypeDefModifier ::= final
            //
            case 287: {
               //#line 2598 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2598 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 288:  Interfaces ::= implements InterfaceTypeList
            //
            case 288: {
               //#line 2607 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2605 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2607 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 289:  InterfaceTypeList ::= Type
            //
            case 289: {
               //#line 2613 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2611 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2613 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 290:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 290: {
               //#line 2620 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2618 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2618 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2620 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 291:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 291: {
               //#line 2630 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2628 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2630 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 293:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 293: {
               //#line 2637 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2635 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2635 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2637 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 295:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 295: {
               //#line 2659 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2657 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2659 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 297:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 297: {
               //#line 2668 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2666 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2668 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 298:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 298: {
               //#line 2675 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2673 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2675 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 299:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 299: {
               //#line 2682 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2680 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2682 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 300:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 300: {
               //#line 2689 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2687 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2689 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 301:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 301: {
               //#line 2696 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2694 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2696 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 302:  ClassMemberDeclaration ::= ;
            //
            case 302: {
               //#line 2703 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2703 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 303:  FormalDeclarators ::= FormalDeclarator
            //
            case 303: {
               //#line 2710 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2708 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2710 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 304:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 304: {
               //#line 2717 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2715 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2715 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2717 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 305:  FieldDeclarators ::= FieldDeclarator
            //
            case 305: {
               //#line 2724 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2722 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2724 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 306:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 306: {
               //#line 2731 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2729 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2729 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2731 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 307:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 307: {
               //#line 2739 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2737 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 2739 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 308:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 308: {
               //#line 2746 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2744 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 2744 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 2746 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 309:  VariableDeclarators ::= VariableDeclarator
            //
            case 309: {
               //#line 2753 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2751 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2753 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 310:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 310: {
               //#line 2760 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2758 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2758 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2760 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 312:  FieldModifiers ::= FieldModifier
            //
            case 312: {
               //#line 2769 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2767 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2769 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 313:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 313: {
               //#line 2776 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2774 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2774 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2776 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldModifiers.addAll(FieldModifier);
                      break;
            }
    
            //
            // Rule 314:  FieldModifier ::= Annotation
            //
            case 314: {
               //#line 2782 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2780 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2782 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 315:  FieldModifier ::= public
            //
            case 315: {
               //#line 2787 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2787 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 316:  FieldModifier ::= protected
            //
            case 316: {
               //#line 2792 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2792 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 317:  FieldModifier ::= private
            //
            case 317: {
               //#line 2797 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2797 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 318:  FieldModifier ::= static
            //
            case 318: {
               //#line 2802 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2802 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 319:  FieldModifier ::= global
            //
            case 319: {
               //#line 2807 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2807 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 320:  ResultType ::= : Type
            //
            case 320: {
               //#line 2813 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2811 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2813 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 321:  HasResultType ::= : Type
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
            // Rule 322:  HasResultType ::= <: Type
            //
            case 322: {
               //#line 2823 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2821 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2823 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.HasType(Type));
                      break;
            }
    
            //
            // Rule 323:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 323: {
               //#line 2829 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2827 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 2829 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 324:  FormalParameterList ::= FormalParameter
            //
            case 324: {
               //#line 2835 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2833 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2835 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 325:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 325: {
               //#line 2842 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2840 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2840 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2842 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 326:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 326: {
               //#line 2848 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2846 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2846 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2848 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 327:  LoopIndexDeclarator ::= ( IdentifierList ) HasResultTypeopt
            //
            case 327: {
               //#line 2853 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2851 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2851 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2853 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 328:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt
            //
            case 328: {
               //#line 2858 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2856 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2856 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2856 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2858 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 329:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 329: {
               //#line 2864 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2862 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2862 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2864 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2887 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2885 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2885 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2885 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2887 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2911 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2909 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2909 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2911 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2935 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2933 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2933 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2933 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2935 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2959 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2957 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2959 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh("id$")), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 334:  VariableModifiers ::= VariableModifier
            //
            case 334: {
               //#line 2967 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2965 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2967 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 335:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 335: {
               //#line 2974 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2972 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2972 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2974 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableModifiers.addAll(VariableModifier);
                      break;
            }
    
            //
            // Rule 336:  VariableModifier ::= Annotation
            //
            case 336: {
               //#line 2980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2978 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 337:  VariableModifier ::= shared
            //
            case 337: {
               //#line 2985 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2985 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                      break;
            }
    
            //
            // Rule 338:  MethodModifiers ::= MethodModifier
            //
            case 338: {
               //#line 2994 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2992 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2994 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 339:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 339: {
               //#line 3001 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2999 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 2999 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 3001 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiers.addAll(MethodModifier);
                      break;
            }
    
            //
            // Rule 340:  MethodModifier ::= Annotation
            //
            case 340: {
               //#line 3007 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3005 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3007 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 341:  MethodModifier ::= public
            //
            case 341: {
               //#line 3012 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3012 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 342:  MethodModifier ::= protected
            //
            case 342: {
               //#line 3017 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3017 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 343:  MethodModifier ::= private
            //
            case 343: {
               //#line 3022 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3022 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 344:  MethodModifier ::= abstract
            //
            case 344: {
               //#line 3027 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3027 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 345:  MethodModifier ::= static
            //
            case 345: {
               //#line 3032 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3032 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 346:  MethodModifier ::= final
            //
            case 346: {
               //#line 3037 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3037 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 347:  MethodModifier ::= native
            //
            case 347: {
               //#line 3042 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3042 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 348:  MethodModifier ::= atomic
            //
            case 348: {
               //#line 3047 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3047 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                      break;
            }
    
            //
            // Rule 349:  MethodModifier ::= extern
            //
            case 349: {
               //#line 3052 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3052 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
                      break;
            }
    
            //
            // Rule 350:  MethodModifier ::= safe
            //
            case 350: {
               //#line 3057 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3057 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 351:  MethodModifier ::= sequential
            //
            case 351: {
               //#line 3062 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3062 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                      break;
            }
    
            //
            // Rule 352:  MethodModifier ::= nonblocking
            //
            case 352: {
               //#line 3067 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3067 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                      break;
            }
    
            //
            // Rule 353:  MethodModifier ::= incomplete
            //
            case 353: {
               //#line 3072 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3072 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                      break;
            }
    
            //
            // Rule 354:  MethodModifier ::= property
            //
            case 354: {
               //#line 3077 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3077 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                      break;
            }
    
            //
            // Rule 355:  MethodModifier ::= global
            //
            case 355: {
               //#line 3082 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3082 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 356:  MethodModifier ::= proto
            //
            case 356: {
               //#line 3087 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3087 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROTO)));
                      break;
            }
    
            //
            // Rule 357:  Throws ::= throws ExceptionTypeList
            //
            case 357: {
               //#line 3094 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3092 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 3094 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 358:  Offers ::= offers Type
            //
            case 358: {
               //#line 3099 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3097 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3099 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 359:  ExceptionTypeList ::= ExceptionType
            //
            case 359: {
               //#line 3105 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3103 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3105 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 360:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 360: {
               //#line 3112 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3110 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3110 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3112 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 362:  MethodBody ::= = LastExpression ;
            //
            case 362: {
               //#line 3120 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3118 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3120 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 363:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 363: {
               //#line 3125 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3123 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3123 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(4);
                //#line 3123 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3125 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult((Block) ((X10Ext) nf.Block(pos(),l).ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 364:  MethodBody ::= = Annotationsopt Block
            //
            case 364: {
               //#line 3133 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3131 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3131 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3133 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 365:  MethodBody ::= Annotationsopt Block
            //
            case 365: {
               //#line 3138 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3136 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 3136 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3138 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 366:  MethodBody ::= ;
            //
            case 366:
                setResult(null);
                break;

            //
            // Rule 367:  SimpleTypeName ::= Identifier
            //
            case 367: {
               //#line 3158 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3156 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3158 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 368:  ConstructorModifiers ::= ConstructorModifier
            //
            case 368: {
               //#line 3164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3162 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 3164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 369:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 369: {
               //#line 3171 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3169 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 3169 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 3171 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                      break;
            }
    
            //
            // Rule 370:  ConstructorModifier ::= Annotation
            //
            case 370: {
               //#line 3177 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3175 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3177 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 371:  ConstructorModifier ::= public
            //
            case 371: {
               //#line 3182 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3182 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 372:  ConstructorModifier ::= protected
            //
            case 372: {
               //#line 3187 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3187 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 373:  ConstructorModifier ::= private
            //
            case 373: {
               //#line 3192 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3192 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 374:  ConstructorModifier ::= native
            //
            case 374: {
               //#line 3197 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3197 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 375:  ConstructorBody ::= = ConstructorBlock
            //
            case 375: {
               //#line 3203 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3201 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3203 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 376:  ConstructorBody ::= ConstructorBlock
            //
            case 376: {
               //#line 3208 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3206 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3208 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 377:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 377: {
               //#line 3213 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3211 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3213 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3221 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3221 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
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
               //#line 3232 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3230 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3230 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3232 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 381:  Arguments ::= ( ArgumentListopt )
            //
            case 381: {
               //#line 3245 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3243 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3245 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 383:  InterfaceModifiers ::= InterfaceModifier
            //
            case 383: {
               //#line 3255 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3253 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 3255 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 384:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 384: {
               //#line 3262 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3260 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 3260 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 3262 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                      break;
            }
    
            //
            // Rule 385:  InterfaceModifier ::= Annotation
            //
            case 385: {
               //#line 3268 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3266 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3268 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 386:  InterfaceModifier ::= public
            //
            case 386: {
               //#line 3273 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3273 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 387:  InterfaceModifier ::= protected
            //
            case 387: {
               //#line 3278 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3278 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 388:  InterfaceModifier ::= private
            //
            case 388: {
               //#line 3283 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3283 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 389:  InterfaceModifier ::= abstract
            //
            case 389: {
               //#line 3288 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3288 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 390:  InterfaceModifier ::= static
            //
            case 390: {
               //#line 3293 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3293 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 391:  ExtendsInterfaces ::= extends Type
            //
            case 391: {
               //#line 3299 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3297 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3299 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 392:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 392: {
               //#line 3306 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3304 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3304 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3306 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 393:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 393: {
               //#line 3315 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3313 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3315 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 395:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 395: {
               //#line 3322 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3320 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3320 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3322 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 396:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 396: {
               //#line 3329 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3327 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3329 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 397:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 397: {
               //#line 3336 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3334 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3336 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 398:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 398: {
               //#line 3343 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3341 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3343 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 399:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 399: {
               //#line 3350 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3348 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3350 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 400:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 400: {
               //#line 3357 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3355 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3357 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 401:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 401: {
               //#line 3364 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3362 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3364 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 402:  InterfaceMemberDeclaration ::= ;
            //
            case 402: {
               //#line 3371 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3371 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 403:  Annotations ::= Annotation
            //
            case 403: {
               //#line 3377 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3375 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3377 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 404:  Annotations ::= Annotations Annotation
            //
            case 404: {
               //#line 3384 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3382 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3382 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3384 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 405:  Annotation ::= @ NamedType
            //
            case 405: {
               //#line 3390 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3388 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3390 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 406:  SimpleName ::= Identifier
            //
            case 406: {
               //#line 3396 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3394 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3396 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 407:  Identifier ::= identifier
            //
            case 407: {
               //#line 3402 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3400 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3402 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 408:  VariableInitializers ::= VariableInitializer
            //
            case 408: {
               //#line 3410 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3408 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3410 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 409:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 409: {
               //#line 3417 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3415 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3415 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3417 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                      break;
            }
    
            //
            // Rule 410:  Block ::= { BlockStatementsopt }
            //
            case 410: {
               //#line 3435 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3433 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3435 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 411:  BlockStatements ::= BlockStatement
            //
            case 411: {
               //#line 3441 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3439 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3441 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 412:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 412: {
               //#line 3448 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3446 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3446 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3448 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 414:  BlockStatement ::= ClassDeclaration
            //
            case 414: {
               //#line 3456 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3454 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3456 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 415:  BlockStatement ::= TypeDefDeclaration
            //
            case 415: {
               //#line 3463 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3461 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3463 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 416:  BlockStatement ::= Statement
            //
            case 416: {
               //#line 3470 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3468 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3470 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 417:  IdentifierList ::= Identifier
            //
            case 417: {
               //#line 3478 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3476 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3478 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 418:  IdentifierList ::= IdentifierList , Identifier
            //
            case 418: {
               //#line 3485 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3483 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3483 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3485 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 419:  FormalDeclarator ::= Identifier ResultType
            //
            case 419: {
               //#line 3491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3489 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3489 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3491 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 420:  FormalDeclarator ::= ( IdentifierList ) ResultType
            //
            case 420: {
               //#line 3496 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3494 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3494 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3496 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 421:  FormalDeclarator ::= Identifier ( IdentifierList ) ResultType
            //
            case 421: {
               //#line 3501 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3499 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3499 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3499 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3501 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 422:  FieldDeclarator ::= Identifier HasResultType
            //
            case 422: {
               //#line 3507 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3505 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3505 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3507 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultType, null });
                      break;
            }
    
            //
            // Rule 423:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 423: {
               //#line 3512 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3510 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3510 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3510 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3512 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 424:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 424: {
               //#line 3518 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3516 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3516 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3516 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3518 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 425:  VariableDeclarator ::= ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 425: {
               //#line 3523 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3521 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3521 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3521 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3523 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 426:  VariableDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 426: {
               //#line 3528 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3526 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3528 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 427:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 427: {
               //#line 3534 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3532 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3532 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3532 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3534 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 428:  VariableDeclaratorWithType ::= ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 428: {
               //#line 3539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3537 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3537 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(4);
                //#line 3537 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3539 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 429:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 429: {
               //#line 3544 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3542 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3542 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3542 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(5);
                //#line 3542 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3544 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 431:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 431: {
               //#line 3552 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3550 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3550 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3550 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3552 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 432:  LocalVariableDeclaration ::= VariableModifiersopt VariableDeclaratorsWithType
            //
            case 432: {
               //#line 3585 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3583 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3583 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 3585 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 433:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword FormalDeclarators
            //
            case 433: {
               //#line 3619 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3617 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3617 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3617 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3619 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 434:  Primary ::= here
            //
            case 434: {
               //#line 3660 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3660 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 435:  Primary ::= [ ArgumentListopt ]
            //
            case 435: {
               //#line 3666 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3664 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3666 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 437:  Primary ::= self
            //
            case 437: {
               //#line 3674 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3674 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 438:  Primary ::= this
            //
            case 438: {
               //#line 3679 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3679 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 439:  Primary ::= ClassName . this
            //
            case 439: {
               //#line 3684 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3682 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3684 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 440:  Primary ::= ( Expression )
            //
            case 440: {
               //#line 3689 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3687 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3689 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 446:  OperatorFunction ::= TypeName . +
            //
            case 446: {
               //#line 3700 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3698 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3700 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 447:  OperatorFunction ::= TypeName . -
            //
            case 447: {
               //#line 3711 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3709 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3711 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 448:  OperatorFunction ::= TypeName . *
            //
            case 448: {
               //#line 3722 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3720 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3722 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 449:  OperatorFunction ::= TypeName . /
            //
            case 449: {
               //#line 3733 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3731 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3733 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 450:  OperatorFunction ::= TypeName . %
            //
            case 450: {
               //#line 3744 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3742 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3744 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 451:  OperatorFunction ::= TypeName . &
            //
            case 451: {
               //#line 3755 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3753 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3755 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 452:  OperatorFunction ::= TypeName . |
            //
            case 452: {
               //#line 3766 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3764 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3766 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 453:  OperatorFunction ::= TypeName . ^
            //
            case 453: {
               //#line 3777 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3775 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3777 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 454:  OperatorFunction ::= TypeName . <<
            //
            case 454: {
               //#line 3788 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3786 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3788 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 455:  OperatorFunction ::= TypeName . >>
            //
            case 455: {
               //#line 3799 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3797 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3799 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 456:  OperatorFunction ::= TypeName . >>>
            //
            case 456: {
               //#line 3810 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3808 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3810 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 457:  OperatorFunction ::= TypeName . <
            //
            case 457: {
               //#line 3821 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3819 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3821 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 458:  OperatorFunction ::= TypeName . <=
            //
            case 458: {
               //#line 3832 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3830 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3832 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 459:  OperatorFunction ::= TypeName . >=
            //
            case 459: {
               //#line 3843 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3841 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3843 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 460:  OperatorFunction ::= TypeName . >
            //
            case 460: {
               //#line 3854 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3852 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3854 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 461:  OperatorFunction ::= TypeName . ==
            //
            case 461: {
               //#line 3865 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3863 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3865 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 462:  OperatorFunction ::= TypeName . !=
            //
            case 462: {
               //#line 3876 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3874 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3876 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 463:  Literal ::= IntegerLiteral$lit
            //
            case 463: {
               //#line 3889 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3887 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3889 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 464:  Literal ::= LongLiteral$lit
            //
            case 464: {
               //#line 3895 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3893 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3895 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 465:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 465: {
               //#line 3901 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3899 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3901 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 466:  Literal ::= UnsignedLongLiteral$lit
            //
            case 466: {
               //#line 3907 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3905 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3907 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 467:  Literal ::= FloatingPointLiteral$lit
            //
            case 467: {
               //#line 3913 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3911 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3913 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 468:  Literal ::= DoubleLiteral$lit
            //
            case 468: {
               //#line 3919 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3917 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3919 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 469:  Literal ::= BooleanLiteral
            //
            case 469: {
               //#line 3925 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3923 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3925 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 470:  Literal ::= CharacterLiteral$lit
            //
            case 470: {
               //#line 3930 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3928 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3930 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 471:  Literal ::= StringLiteral$str
            //
            case 471: {
               //#line 3936 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3934 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3936 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 472:  Literal ::= null
            //
            case 472: {
               //#line 3942 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3942 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 473:  BooleanLiteral ::= true$trueLiteral
            //
            case 473: {
               //#line 3948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3946 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3948 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 474:  BooleanLiteral ::= false$falseLiteral
            //
            case 474: {
               //#line 3953 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3951 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3953 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 475:  ArgumentList ::= Expression
            //
            case 475: {
               //#line 3962 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3960 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3962 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 476:  ArgumentList ::= ArgumentList , Expression
            //
            case 476: {
               //#line 3969 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3967 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3967 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3969 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 477:  FieldAccess ::= Primary . Identifier
            //
            case 477: {
               //#line 3975 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3973 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3973 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3975 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 478:  FieldAccess ::= super . Identifier
            //
            case 478: {
               //#line 3980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3978 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3980 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 479:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 479: {
               //#line 3985 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3983 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3983 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3983 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3985 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 480:  FieldAccess ::= Primary . class$c
            //
            case 480: {
               //#line 3990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3988 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3988 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3990 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 481:  FieldAccess ::= super . class$c
            //
            case 481: {
               //#line 3995 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3993 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3995 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 482:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 482: {
               //#line 4000 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3998 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 4000 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 483:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 483: {
               //#line 4006 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4004 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4004 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 4004 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 4006 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 484:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 484: {
               //#line 4013 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4011 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4011 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4011 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 4011 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 4013 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 485:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 485: {
               //#line 4018 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 4016 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 4018 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 486:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 486: {
               //#line 4023 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4021 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4021 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4021 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4021 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 4021 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 4023 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 487:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 487: {
               //#line 4028 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 4026 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 4028 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 488:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 488: {
               //#line 4048 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4046 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4046 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(4);
                //#line 4048 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 489:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 489: {
               //#line 4061 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4059 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4059 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4059 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4061 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 490:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 490: {
               //#line 4073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4071 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4071 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4073 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 491:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 491: {
               //#line 4085 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4083 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(8);
                //#line 4085 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 495:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 495: {
               //#line 4103 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4101 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4103 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 496:  PostDecrementExpression ::= PostfixExpression --
            //
            case 496: {
               //#line 4109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4107 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4109 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 499:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 499: {
               //#line 4117 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4115 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4117 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 500:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 500: {
               //#line 4122 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4120 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4122 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 502:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 502: {
               //#line 4129 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4127 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4129 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 503:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 503: {
               //#line 4135 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4133 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4135 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 505:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 505: {
               //#line 4142 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4140 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4142 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 506:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 506: {
               //#line 4147 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4145 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4147 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e.position(pos()));
                      break;
            }
    
            //
            // Rule 507:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 507: {
               //#line 4154 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4152 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4154 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 509:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 509: {
               //#line 4161 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4159 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4159 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4161 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 510:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 510: {
               //#line 4166 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4164 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4166 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 511:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 511: {
               //#line 4171 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4169 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4169 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4171 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 513:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 513: {
               //#line 4178 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4176 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4176 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4178 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 514:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 514: {
               //#line 4183 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4181 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4181 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4183 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 516:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 516: {
               //#line 4190 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4188 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4188 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4190 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 517:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 517: {
               //#line 4195 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4193 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4193 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4195 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 518:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 518: {
               //#line 4200 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4198 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4198 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4200 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 520:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 520: {
               //#line 4207 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4205 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4205 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4207 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 523:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 523: {
               //#line 4216 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4214 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4214 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4216 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 524:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 524: {
               //#line 4221 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4219 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4221 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 525:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 525: {
               //#line 4226 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4224 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4226 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 526:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 526: {
               //#line 4231 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4229 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4229 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4231 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 527:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 527: {
               //#line 4236 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4234 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4234 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4236 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 528:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 528: {
               //#line 4241 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4239 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4239 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4241 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 530:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 530: {
               //#line 4248 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4246 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4246 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4248 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 531:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 531: {
               //#line 4253 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4251 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4251 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4253 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 532:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 532: {
               //#line 4258 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4256 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4258 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 534:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 534: {
               //#line 4265 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4263 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4263 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4265 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 536:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 536: {
               //#line 4272 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4270 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4270 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4272 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 538:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 538: {
               //#line 4279 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4277 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4277 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4279 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 540:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 540: {
               //#line 4286 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4284 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4284 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4286 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 542:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 542: {
               //#line 4293 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4291 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4291 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4293 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 549:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 549: {
               //#line 4306 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4304 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4304 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4304 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4306 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 552:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 552: {
               //#line 4315 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4313 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4313 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4313 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4315 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 553:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 553: {
               //#line 4320 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4318 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4320 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 554:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 554: {
               //#line 4325 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4323 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4325 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 555:  LeftHandSide ::= ExpressionName
            //
            case 555: {
               //#line 4331 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4329 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4331 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 557:  AssignmentOperator ::= =
            //
            case 557: {
               //#line 4338 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4338 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 558:  AssignmentOperator ::= *=
            //
            case 558: {
               //#line 4343 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4343 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 559:  AssignmentOperator ::= /=
            //
            case 559: {
               //#line 4348 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4348 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 560:  AssignmentOperator ::= %=
            //
            case 560: {
               //#line 4353 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4353 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 561:  AssignmentOperator ::= +=
            //
            case 561: {
               //#line 4358 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4358 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 562:  AssignmentOperator ::= -=
            //
            case 562: {
               //#line 4363 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4363 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 563:  AssignmentOperator ::= <<=
            //
            case 563: {
               //#line 4368 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4368 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 564:  AssignmentOperator ::= >>=
            //
            case 564: {
               //#line 4373 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4373 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 565:  AssignmentOperator ::= >>>=
            //
            case 565: {
               //#line 4378 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4378 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 566:  AssignmentOperator ::= &=
            //
            case 566: {
               //#line 4383 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4383 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 567:  AssignmentOperator ::= ^=
            //
            case 567: {
               //#line 4388 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4388 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 568:  AssignmentOperator ::= |=
            //
            case 568: {
               //#line 4393 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4393 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 571:  PrefixOp ::= +
            //
            case 571: {
               //#line 4404 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4404 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 572:  PrefixOp ::= -
            //
            case 572: {
               //#line 4409 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4409 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 573:  PrefixOp ::= !
            //
            case 573: {
               //#line 4414 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4414 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 574:  PrefixOp ::= ~
            //
            case 574: {
               //#line 4419 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4419 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 575:  BinOp ::= +
            //
            case 575: {
               //#line 4425 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4425 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 576:  BinOp ::= -
            //
            case 576: {
               //#line 4430 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4430 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 577:  BinOp ::= *
            //
            case 577: {
               //#line 4435 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4435 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 578:  BinOp ::= /
            //
            case 578: {
               //#line 4440 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4440 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 579:  BinOp ::= %
            //
            case 579: {
               //#line 4445 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4445 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 580:  BinOp ::= &
            //
            case 580: {
               //#line 4450 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4450 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 581:  BinOp ::= |
            //
            case 581: {
               //#line 4455 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4455 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 582:  BinOp ::= ^
            //
            case 582: {
               //#line 4460 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4460 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 583:  BinOp ::= &&
            //
            case 583: {
               //#line 4465 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4465 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 584:  BinOp ::= ||
            //
            case 584: {
               //#line 4470 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4470 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 585:  BinOp ::= <<
            //
            case 585: {
               //#line 4475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4475 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 586:  BinOp ::= >>
            //
            case 586: {
               //#line 4480 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4480 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 587:  BinOp ::= >>>
            //
            case 587: {
               //#line 4485 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4485 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 588:  BinOp ::= >=
            //
            case 588: {
               //#line 4490 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4490 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 589:  BinOp ::= <=
            //
            case 589: {
               //#line 4495 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4495 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 590:  BinOp ::= >
            //
            case 590: {
               //#line 4500 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4500 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 591:  BinOp ::= <
            //
            case 591: {
               //#line 4505 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4505 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 592:  BinOp ::= ==
            //
            case 592: {
               //#line 4513 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4513 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 593:  BinOp ::= !=
            //
            case 593: {
               //#line 4518 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4518 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 594:  Catchesopt ::= $Empty
            //
            case 594: {
               //#line 4527 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4527 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                      break;
            }
    
            //
            // Rule 596:  Identifieropt ::= $Empty
            //
            case 596:
                setResult(null);
                break;

            //
            // Rule 597:  Identifieropt ::= Identifier
            //
            case 597: {
               //#line 4536 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4534 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4536 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 598:  ForUpdateopt ::= $Empty
            //
            case 598: {
               //#line 4542 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4542 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                      break;
            }
    
            //
            // Rule 600:  Expressionopt ::= $Empty
            //
            case 600:
                setResult(null);
                break;

            //
            // Rule 602:  ForInitopt ::= $Empty
            //
            case 602: {
               //#line 4553 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4553 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 604:  SwitchLabelsopt ::= $Empty
            //
            case 604: {
               //#line 4560 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4560 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 606:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 606: {
               //#line 4567 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4567 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 608:  VariableModifiersopt ::= $Empty
            //
            case 608: {
               //#line 4574 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4574 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 610:  VariableInitializersopt ::= $Empty
            //
            case 610:
                setResult(null);
                break;

            //
            // Rule 612:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 612: {
               //#line 4585 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4585 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 614:  ExtendsInterfacesopt ::= $Empty
            //
            case 614: {
               //#line 4592 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4592 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 616:  InterfaceModifiersopt ::= $Empty
            //
            case 616: {
               //#line 4599 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4599 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 618:  ClassBodyopt ::= $Empty
            //
            case 618:
                setResult(null);
                break;

            //
            // Rule 620:  Argumentsopt ::= $Empty
            //
            case 620: {
               //#line 4610 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4610 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 622:  ArgumentListopt ::= $Empty
            //
            case 622: {
               //#line 4617 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4617 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 624:  BlockStatementsopt ::= $Empty
            //
            case 624: {
               //#line 4624 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4624 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                      break;
            }
    
            //
            // Rule 626:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 626:
                setResult(null);
                break;

            //
            // Rule 628:  ConstructorModifiersopt ::= $Empty
            //
            case 628: {
               //#line 4635 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4635 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 630:  FormalParameterListopt ::= $Empty
            //
            case 630: {
               //#line 4642 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4642 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 632:  Throwsopt ::= $Empty
            //
            case 632: {
               //#line 4649 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4649 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 634:  Offersopt ::= $Empty
            //
            case 634: {
               //#line 4655 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4655 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 636:  MethodModifiersopt ::= $Empty
            //
            case 636: {
               //#line 4662 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4662 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 638:  TypeModifieropt ::= $Empty
            //
            case 638: {
               //#line 4669 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4669 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 640:  FieldModifiersopt ::= $Empty
            //
            case 640: {
               //#line 4676 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4676 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 642:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 642: {
               //#line 4683 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4683 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 644:  Interfacesopt ::= $Empty
            //
            case 644: {
               //#line 4690 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4690 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 646:  Superopt ::= $Empty
            //
            case 646:
                setResult(null);
                break;

            //
            // Rule 648:  TypeParametersopt ::= $Empty
            //
            case 648: {
               //#line 4701 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4701 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 650:  FormalParametersopt ::= $Empty
            //
            case 650: {
               //#line 4708 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4708 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 652:  Annotationsopt ::= $Empty
            //
            case 652: {
               //#line 4715 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4715 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 654:  TypeDeclarationsopt ::= $Empty
            //
            case 654: {
               //#line 4722 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4722 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 656:  ImportDeclarationsopt ::= $Empty
            //
            case 656: {
               //#line 4729 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4729 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                      break;
            }
    
            //
            // Rule 658:  PackageDeclarationopt ::= $Empty
            //
            case 658:
                setResult(null);
                break;

            //
            // Rule 660:  ResultTypeopt ::= $Empty
            //
            case 660:
                setResult(null);
                break;

            //
            // Rule 662:  HasResultTypeopt ::= $Empty
            //
            case 662:
                setResult(null);
                break;

            //
            // Rule 664:  TypeArgumentsopt ::= $Empty
            //
            case 664: {
               //#line 4747 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4747 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 666:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 666: {
               //#line 4754 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4754 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 668:  Propertiesopt ::= $Empty
            //
            case 668: {
               //#line 4761 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4761 "/Users/pcharles/IMP-workspace-3.6.0/x10-trunk/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

