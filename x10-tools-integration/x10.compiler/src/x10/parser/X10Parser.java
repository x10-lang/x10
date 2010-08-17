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

//#line 32 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
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
    

    //#line 312 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
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
               //#line 8 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 6 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 8 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 18 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 16 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 18 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 28 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 26 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 28 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 38 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 36 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 38 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 48 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 46 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 48 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 58 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 56 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 58 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 68 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 66 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 74 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 74 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 80 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 78 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 78 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 87 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 85 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 87 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 94 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 92 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 94 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 100 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 98 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 100 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 109 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 107 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 117 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 115 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                      break;
            }
    
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 122 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 120 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 120 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 918 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 916 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiersopt = (List) getRhsSym(1);
                //#line 916 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 916 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 916 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 916 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 916 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 918 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 931 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 929 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 931 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
   setResult(PropertyList);
                 break;
            } 
            //
            // Rule 18:  PropertyList ::= Property
            //
            case 18: {
               //#line 936 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 934 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 936 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 19:  PropertyList ::= PropertyList , Property
            //
            case 19: {
               //#line 943 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 941 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 941 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 943 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 20:  Property ::= Annotationsopt Identifier ResultType
            //
            case 20: {
               //#line 950 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 948 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 948 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 948 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 950 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 959 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 957 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 957 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 957 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 957 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 957 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 957 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 957 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 957 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 957 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 959 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 991 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 989 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 989 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 989 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 989 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 989 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 989 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 989 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 989 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(13);
                //#line 989 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(14);
                //#line 989 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(15);
                //#line 991 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1009 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1007 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1007 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1007 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1007 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 1007 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 1007 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 1007 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1007 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1007 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1009 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1027 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1025 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1025 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1025 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 1025 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 1025 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1025 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1025 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1025 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1025 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1027 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1046 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1044 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1044 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1044 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1044 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1044 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1044 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1044 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1044 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1044 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1046 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1066 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1064 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1064 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1064 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1064 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1064 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1064 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1064 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1064 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1066 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1084 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1082 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1082 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1082 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1082 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1082 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1082 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1082 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1082 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1084 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1102 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1100 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1100 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1100 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1100 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1100 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1100 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1100 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(12);
                //#line 1100 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(13);
                //#line 1100 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 1102 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1120 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1118 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1118 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1118 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1118 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1118 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1118 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1118 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1118 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1120 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1138 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1136 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1136 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1136 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1136 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1136 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1136 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1136 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1136 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1138 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1156 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1154 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1154 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1154 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1154 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1154 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(8);
                //#line 1154 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(9);
                //#line 1154 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(10);
                //#line 1154 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1156 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1175 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1173 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1173 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1173 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1173 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1173 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1173 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1173 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(8);
                //#line 1175 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1191 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1189 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1189 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1189 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(4);
                //#line 1189 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 1189 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(6);
                //#line 1191 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1208 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1206 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1206 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1208 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 35:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 35: {
               //#line 1213 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1211 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1211 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1213 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 36:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 36: {
               //#line 1218 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1216 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1216 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1216 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1218 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 37:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 37: {
               //#line 1223 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1221 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1221 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1221 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1223 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 38:  NormalInterfaceDeclaration ::= InterfaceModifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 38: {
               //#line 1229 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1227 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiersopt = (List) getRhsSym(1);
                //#line 1227 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1227 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1227 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1227 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1227 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 1227 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1229 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1250 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1248 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1248 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1248 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1248 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1250 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 40:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 40: {
               //#line 1257 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1255 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1255 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1255 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1255 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1255 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1257 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1265 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1263 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1263 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1263 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1263 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1263 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1265 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1274 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1272 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1272 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1274 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 45:  Type ::= proto ConstrainedType
            //
            case 45: {
               //#line 1283 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1281 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode ConstrainedType = (TypeNode) getRhsSym(2);
                //#line 1283 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
        AddFlags tn = (AddFlags) ConstrainedType;
        tn.addFlags(X10Flags.PROTO);
        setResult(ConstrainedType.position(pos()));
                      break;
            }
    
            //
            // Rule 46:  FunctionType ::= TypeArgumentsopt ( FormalParameterListopt ) WhereClauseopt Throwsopt Offersopt => Type
            //
            case 46: {
               //#line 1291 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1289 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(1);
                //#line 1289 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1289 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1289 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1289 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(7);
                //#line 1289 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(9);
                //#line 1291 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeArgumentsopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt, Offersopt));
                      break;
            }
    
            //
            // Rule 51:  AnnotatedType ::= Type Annotations
            //
            case 51: {
               //#line 1300 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1298 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1298 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1300 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn.position(pos()));
                      break;
            }
    
            //
            // Rule 54:  ConstrainedType ::= ( Type )
            //
            case 54: {
               //#line 1310 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1308 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1310 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 56:  NamedType ::= Primary . Identifier TypeArgumentsopt Argumentsopt DepParametersopt
            //
            case 56: {
               //#line 1324 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1322 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1322 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1322 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1322 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(5);
                //#line 1322 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(6);
                //#line 1324 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1335 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1333 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1333 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1333 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Argumentsopt = (List) getRhsSym(3);
                //#line 1333 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParametersopt = (DepParameterExpr) getRhsSym(4);
                //#line 1335 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1353 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1351 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1351 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object Conjunctionopt = (Object) getRhsSym(3);
                //#line 1353 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, (List) Conjunctionopt));
                      break;
            }
    
            //
            // Rule 59:  DepParameters ::= ! PlaceType
            //
            case 59: {
               //#line 1358 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1356 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1358 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 60:  DepParameters ::= !
            //
            case 60: {
               //#line 1364 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1364 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 61:  DepParameters ::= ! PlaceType { ExistentialListopt Conjunction }
            //
            case 61: {
               //#line 1370 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1368 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1368 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(4);
                //#line 1368 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(5);
                //#line 1370 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 62:  DepParameters ::= ! { ExistentialListopt Conjunction }
            //
            case 62: {
               //#line 1376 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1374 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(3);
                //#line 1374 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(4);
                //#line 1376 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 63:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 63: {
               //#line 1384 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1382 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(2);
                //#line 1384 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 64:  TypeParameters ::= [ TypeParameterList ]
            //
            case 64: {
               //#line 1390 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1388 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1390 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 65:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 65: {
               //#line 1395 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1393 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1395 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 66:  Conjunction ::= Expression
            //
            case 66: {
               //#line 1401 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1399 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1401 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 67:  Conjunction ::= Conjunction , Expression
            //
            case 67: {
               //#line 1408 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1406 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1406 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1408 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 68:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 68: {
               //#line 1414 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1412 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1412 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1414 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 69:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 69: {
               //#line 1419 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1417 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1417 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1419 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 70:  WhereClause ::= DepParameters
            //
            case 70: {
               //#line 1425 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1423 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1425 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 71:  Conjunctionopt ::= $Empty
            //
            case 71: {
               //#line 1431 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1431 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                setResult(l);
                      break;
            }
      
            //
            // Rule 72:  Conjunctionopt ::= Conjunction
            //
            case 72: {
               //#line 1437 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1435 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1437 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(Conjunction);
                      break;
            }
    
            //
            // Rule 73:  ExistentialListopt ::= $Empty
            //
            case 73: {
               //#line 1443 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1443 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 74:  ExistentialListopt ::= ExistentialList ;
            //
            case 74: {
               //#line 1448 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1446 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1448 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 75:  ExistentialList ::= FormalParameter
            //
            case 75: {
               //#line 1454 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1452 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1454 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 76:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 76: {
               //#line 1461 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1459 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1459 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1461 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 79:  NormalClassDeclaration ::= ClassModifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 79: {
               //#line 1472 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1470 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1470 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1470 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1470 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1470 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1470 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1470 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1470 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1472 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1488 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1486 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiersopt = (List) getRhsSym(1);
                //#line 1486 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1486 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1486 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1486 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1486 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1486 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1488 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1502 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1500 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiersopt = (List) getRhsSym(1);
                //#line 1500 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1500 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1500 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1500 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1500 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1500 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1500 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(10);
                //#line 1502 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1519 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1517 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1519 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 83:  FieldKeyword ::= val
            //
            case 83: {
               //#line 1525 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1525 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 84:  FieldKeyword ::= var
            //
            case 84: {
               //#line 1530 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1530 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 85:  FieldKeyword ::= const
            //
            case 85: {
               //#line 1535 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1535 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                      break;
            }
    
            //
            // Rule 86:  VarKeyword ::= val
            //
            case 86: {
               //#line 1543 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1543 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 87:  VarKeyword ::= var
            //
            case 87: {
               //#line 1548 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1548 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 88:  FieldDeclaration ::= FieldModifiersopt FieldKeyword FieldDeclarators ;
            //
            case 88: {
               //#line 1555 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1553 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1553 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 1553 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 1555 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1580 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1578 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiersopt = (List) getRhsSym(1);
                //#line 1578 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 1580 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1612 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1610 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 1610 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt NonExpressionStatement = (Stmt) getRhsSym(2);
                //#line 1612 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1648 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1646 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1648 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Offer(pos(), Expression));
                      break;
            }
    
            //
            // Rule 120:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 120: {
               //#line 1654 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1652 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1652 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1654 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 121:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 121: {
               //#line 1660 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1658 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1658 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 1658 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 1660 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 122:  EmptyStatement ::= ;
            //
            case 122: {
               //#line 1666 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1666 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 123:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 123: {
               //#line 1672 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1670 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 1670 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 1672 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 129:  ExpressionStatement ::= StatementExpression ;
            //
            case 129: {
               //#line 1684 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1682 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1684 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 137:  AssertStatement ::= assert Expression ;
            //
            case 137: {
               //#line 1698 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1696 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1698 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 138:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 138: {
               //#line 1703 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1701 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 1701 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 1703 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 139:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 139: {
               //#line 1709 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1707 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1707 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 1709 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 140:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 140: {
               //#line 1715 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1713 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 1713 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 1715 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 142:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 142: {
               //#line 1723 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1721 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 1721 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 1723 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 143:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 143: {
               //#line 1730 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1728 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1728 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 1730 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1739 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1737 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 1739 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 145:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 145: {
               //#line 1746 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1744 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 1744 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 1746 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 146:  SwitchLabel ::= case ConstantExpression :
            //
            case 146: {
               //#line 1753 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1751 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 1753 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 147:  SwitchLabel ::= default :
            //
            case 147: {
               //#line 1758 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1758 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 148:  WhileStatement ::= while ( Expression ) Statement
            //
            case 148: {
               //#line 1764 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1762 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1762 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1764 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 149:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 149: {
               //#line 1770 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1768 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1768 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1770 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 152:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 152: {
               //#line 1779 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1777 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 1777 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 1777 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 1777 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 1779 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 154:  ForInit ::= LocalVariableDeclaration
            //
            case 154: {
               //#line 1786 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1784 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 1786 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 156:  StatementExpressionList ::= StatementExpression
            //
            case 156: {
               //#line 1796 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1794 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 1796 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 157:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 157: {
               //#line 1803 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1801 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 1801 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 1803 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 158:  BreakStatement ::= break Identifieropt ;
            //
            case 158: {
               //#line 1809 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1807 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1809 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 159:  ContinueStatement ::= continue Identifieropt ;
            //
            case 159: {
               //#line 1815 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1813 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 1815 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 160:  ReturnStatement ::= return Expressionopt ;
            //
            case 160: {
               //#line 1821 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1819 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 1821 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 161:  ThrowStatement ::= throw Expression ;
            //
            case 161: {
               //#line 1827 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1825 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1827 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 162:  TryStatement ::= try Block Catches
            //
            case 162: {
               //#line 1833 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1831 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1831 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 1833 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 163:  TryStatement ::= try Block Catchesopt Finally
            //
            case 163: {
               //#line 1838 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1836 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1836 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 1836 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 1838 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 164:  Catches ::= CatchClause
            //
            case 164: {
               //#line 1844 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1842 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 1844 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 165:  Catches ::= Catches CatchClause
            //
            case 165: {
               //#line 1851 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1849 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 1849 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 1851 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 166:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 166: {
               //#line 1858 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1856 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1856 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 1858 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 167:  Finally ::= finally Block
            //
            case 167: {
               //#line 1864 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1862 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 1864 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 168:  ClockedClause ::= clocked ( ClockList )
            //
            case 168: {
               //#line 1870 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1868 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 1870 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 169:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 169: {
               //#line 1876 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1874 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 1874 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 1874 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 1876 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1885 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1883 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 1883 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 1885 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 171:  AtomicStatement ::= atomic Statement
            //
            case 171: {
               //#line 1891 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1889 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1891 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 172:  WhenStatement ::= when ( Expression ) Statement
            //
            case 172: {
               //#line 1898 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1896 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1896 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 1898 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 173:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 173: {
               //#line 1903 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1901 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 1901 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 1901 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 1901 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 1903 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 174:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 174: {
               //#line 1910 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1908 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1908 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1908 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1908 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1910 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1926 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1924 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1924 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1924 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 1924 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 1926 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1942 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1940 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 1940 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 1940 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 1942 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1956 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1954 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 1956 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 178:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 178: {
               //#line 1962 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1960 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 1962 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 180:  NextStatement ::= next ;
            //
            case 180: {
               //#line 1970 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1970 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 181:  AwaitStatement ::= await Expression ;
            //
            case 181: {
               //#line 1976 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1974 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 1976 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Await(pos(), Expression));
                      break;
            }
    
            //
            // Rule 182:  ClockList ::= Clock
            //
            case 182: {
               //#line 1982 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1980 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 1982 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 183:  ClockList ::= ClockList , Clock
            //
            case 183: {
               //#line 1989 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1987 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 1987 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 1989 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 184:  Clock ::= Expression
            //
            case 184: {
               //#line 1997 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1995 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1997 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 186:  CastExpression ::= ExpressionName
            //
            case 186: {
               //#line 2011 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2009 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 2011 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 187:  CastExpression ::= CastExpression as Type
            //
            case 187: {
               //#line 2016 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2014 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2014 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2016 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 188:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 188: {
               //#line 2023 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2021 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2023 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 189:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 189: {
               //#line 2030 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2028 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(1);
                //#line 2028 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2030 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 190:  TypeParameterList ::= TypeParameter
            //
            case 190: {
               //#line 2037 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2035 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2037 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 191:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 191: {
               //#line 2044 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2042 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 2042 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2044 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 192:  TypeParamWithVariance ::= Identifier
            //
            case 192: {
               //#line 2051 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2049 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2051 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 193:  TypeParamWithVariance ::= + Identifier
            //
            case 193: {
               //#line 2056 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2054 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2056 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 194:  TypeParamWithVariance ::= - Identifier
            //
            case 194: {
               //#line 2061 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2059 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2061 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 195:  TypeParameter ::= Identifier
            //
            case 195: {
               //#line 2067 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2065 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2067 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 197:  RegionExpressionList ::= RegionExpression
            //
            case 197: {
               //#line 2075 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2073 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(1);
                //#line 2075 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(RegionExpression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 198:  RegionExpressionList ::= RegionExpressionList , RegionExpression
            //
            case 198: {
               //#line 2082 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2080 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List RegionExpressionList = (List) getRhsSym(1);
                //#line 2080 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RegionExpression = (Expr) getRhsSym(3);
                //#line 2082 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                RegionExpressionList.add(RegionExpression);
                //setResult(RegionExpressionList);
                      break;
            }
    
            //
            // Rule 199:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 199: {
               //#line 2089 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2087 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2087 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2089 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 200:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt => ClosureBody
            //
            case 200: {
               //#line 2095 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2093 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(1);
                //#line 2093 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2093 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2093 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(4);
                //#line 2093 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(5);
                //#line 2093 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 2095 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 201:  LastExpression ::= Expression
            //
            case 201: {
               //#line 2102 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2100 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2102 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 202:  ClosureBody ::= ConditionalExpression
            //
            case 202: {
               //#line 2108 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2106 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 2108 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), ConditionalExpression, true)));
                      break;
            }
    
            //
            // Rule 203:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 203: {
               //#line 2113 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2111 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2111 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2111 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2113 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2123 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2121 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2121 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2123 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b.position(pos()));
                      break;
            }
    
            //
            // Rule 205:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 205: {
               //#line 2132 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2130 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2130 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2132 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 206:  AsyncExpression ::= async ClosureBody
            //
            case 206: {
               //#line 2138 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2136 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2138 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 207:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 207: {
               //#line 2143 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2141 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2141 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2143 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 208:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 208: {
               //#line 2148 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2146 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2146 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2148 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 209:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 209: {
               //#line 2153 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2151 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2151 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2151 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2153 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 210:  FinishExpression ::= finish ( Expression ) Block
            //
            case 210: {
               //#line 2160 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2158 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2158 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2160 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FinishExpr(pos(), Expression, Block));
                      break;
            }
    
            //
            // Rule 211:  FutureExpression ::= future ClosureBody
            //
            case 211: {
               //#line 2166 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2164 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2166 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 212:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 212: {
               //#line 2171 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2169 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2169 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2171 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 213:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 213: {
               //#line 2176 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2174 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2174 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2176 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 214:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 214: {
               //#line 2181 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2179 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2179 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2179 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2181 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2192 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2192 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2207 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2207 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 225:  TypeDefModifiersopt ::= $Empty
            //
            case 225: {
               //#line 2213 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2213 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
         setResult(Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE))));
                      break;
            } 
            //
            // Rule 227:  ClockedClauseopt ::= $Empty
            //
            case 227: {
               //#line 2219 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2219 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 229:  identifier ::= IDENTIFIER$ident
            //
            case 229: {
               //#line 2230 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2228 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2230 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 230:  TypeName ::= Identifier
            //
            case 230: {
               //#line 2237 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2235 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2237 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 231:  TypeName ::= TypeName . Identifier
            //
            case 231: {
               //#line 2242 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2240 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2240 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2242 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2254 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2252 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2254 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 234:  TypeArgumentList ::= Type
            //
            case 234: {
               //#line 2261 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2259 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2261 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 235:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 235: {
               //#line 2268 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2266 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2266 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2268 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 236:  PackageName ::= Identifier
            //
            case 236: {
               //#line 2278 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2276 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2278 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 237:  PackageName ::= PackageName . Identifier
            //
            case 237: {
               //#line 2283 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2281 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2281 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2283 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2299 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2297 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2299 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 239:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 239: {
               //#line 2304 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2302 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2302 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2304 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2314 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2312 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2314 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 241:  MethodName ::= AmbiguousName . Identifier
            //
            case 241: {
               //#line 2319 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2317 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2317 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2319 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2329 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2327 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2329 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 243:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 243: {
               //#line 2334 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2332 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2332 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2334 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2344 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2342 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2344 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 245:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 245: {
               //#line 2349 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2347 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2347 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2349 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2361 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2359 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2359 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2359 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2361 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2377 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2375 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2377 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 248:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 248: {
               //#line 2384 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2382 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2382 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2384 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 249:  TypeDeclarations ::= TypeDeclaration
            //
            case 249: {
               //#line 2392 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2390 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2392 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2400 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2398 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2398 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2400 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 251:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 251: {
               //#line 2408 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2406 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2406 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2408 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn.position(pos()));
                      break;
            }
    
            //
            // Rule 254:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 254: {
               //#line 2422 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2420 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2422 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 255:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 255: {
               //#line 2428 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2426 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2428 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 259:  TypeDeclaration ::= ;
            //
            case 259: {
               //#line 2443 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2443 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 260:  ClassModifiers ::= ClassModifier
            //
            case 260: {
               //#line 2451 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2449 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(1);
                //#line 2451 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ClassModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 261:  ClassModifiers ::= ClassModifiers ClassModifier
            //
            case 261: {
               //#line 2458 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2456 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClassModifiers = (List) getRhsSym(1);
                //#line 2456 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClassModifier = (List) getRhsSym(2);
                //#line 2458 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassModifiers.addAll(ClassModifier);
                      break;
            }
    
            //
            // Rule 262:  ClassModifier ::= Annotation
            //
            case 262: {
               //#line 2464 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2462 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2464 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 263:  ClassModifier ::= public
            //
            case 263: {
               //#line 2469 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2469 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 264:  ClassModifier ::= protected
            //
            case 264: {
               //#line 2474 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2474 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 265:  ClassModifier ::= private
            //
            case 265: {
               //#line 2479 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2479 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 266:  ClassModifier ::= abstract
            //
            case 266: {
               //#line 2484 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2484 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 267:  ClassModifier ::= static
            //
            case 267: {
               //#line 2489 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2489 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 268:  ClassModifier ::= final
            //
            case 268: {
               //#line 2494 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2494 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 269:  ClassModifier ::= safe
            //
            case 269: {
               //#line 2499 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2499 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 270:  TypeDefModifiers ::= TypeDefModifier
            //
            case 270: {
               //#line 2505 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2503 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(1);
                //#line 2505 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(TypeDefModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 271:  TypeDefModifiers ::= TypeDefModifiers TypeDefModifier
            //
            case 271: {
               //#line 2512 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2510 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifiers = (List) getRhsSym(1);
                //#line 2510 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeDefModifier = (List) getRhsSym(2);
                //#line 2512 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeDefModifiers.addAll(TypeDefModifier);
                      break;
            }
    
            //
            // Rule 272:  TypeDefModifier ::= Annotation
            //
            case 272: {
               //#line 2518 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2516 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2518 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 273:  TypeDefModifier ::= public
            //
            case 273: {
               //#line 2523 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2523 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 274:  TypeDefModifier ::= protected
            //
            case 274: {
               //#line 2528 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2528 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 275:  TypeDefModifier ::= private
            //
            case 275: {
               //#line 2533 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2533 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 276:  TypeDefModifier ::= abstract
            //
            case 276: {
               //#line 2538 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2538 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 277:  TypeDefModifier ::= static
            //
            case 277: {
               //#line 2543 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2543 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 278:  TypeDefModifier ::= final
            //
            case 278: {
               //#line 2548 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2548 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 279:  Interfaces ::= implements InterfaceTypeList
            //
            case 279: {
               //#line 2557 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2555 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2557 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 280:  InterfaceTypeList ::= Type
            //
            case 280: {
               //#line 2563 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2561 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2563 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 281:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 281: {
               //#line 2570 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2568 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 2568 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2570 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 282:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 282: {
               //#line 2580 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2578 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 2580 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 284:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 284: {
               //#line 2587 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2585 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 2585 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 2587 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 286:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 286: {
               //#line 2609 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2607 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 2609 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 288:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 288: {
               //#line 2618 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2616 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2618 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 289:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 289: {
               //#line 2625 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2623 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 2625 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 290:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 290: {
               //#line 2632 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2630 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 2632 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 291:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 291: {
               //#line 2639 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2637 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2639 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 292:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 292: {
               //#line 2646 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2644 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 2646 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 293:  ClassMemberDeclaration ::= ;
            //
            case 293: {
               //#line 2653 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2653 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 294:  FormalDeclarators ::= FormalDeclarator
            //
            case 294: {
               //#line 2660 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2658 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 2660 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 295:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 295: {
               //#line 2667 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2665 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 2665 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2667 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 296:  FieldDeclarators ::= FieldDeclarator
            //
            case 296: {
               //#line 2674 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2672 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 2674 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 297:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 297: {
               //#line 2681 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2679 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 2679 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 2681 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 298:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 298: {
               //#line 2689 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2687 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 2689 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 299:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 299: {
               //#line 2696 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2694 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 2694 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 2696 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 300:  VariableDeclarators ::= VariableDeclarator
            //
            case 300: {
               //#line 2703 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2701 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 2703 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 301:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 301: {
               //#line 2710 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2708 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 2708 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 2710 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 303:  FieldModifiers ::= FieldModifier
            //
            case 303: {
               //#line 2719 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2717 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(1);
                //#line 2719 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(FieldModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 304:  FieldModifiers ::= FieldModifiers FieldModifier
            //
            case 304: {
               //#line 2726 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2724 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FieldModifiers = (List) getRhsSym(1);
                //#line 2724 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FieldModifier = (List) getRhsSym(2);
                //#line 2726 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldModifiers.addAll(FieldModifier);
                      break;
            }
    
            //
            // Rule 305:  FieldModifier ::= Annotation
            //
            case 305: {
               //#line 2732 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2730 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2732 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 306:  FieldModifier ::= public
            //
            case 306: {
               //#line 2737 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2737 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 307:  FieldModifier ::= protected
            //
            case 307: {
               //#line 2742 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2742 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 308:  FieldModifier ::= private
            //
            case 308: {
               //#line 2747 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2747 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 309:  FieldModifier ::= static
            //
            case 309: {
               //#line 2752 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2752 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 310:  FieldModifier ::= global
            //
            case 310: {
               //#line 2757 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2757 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 311:  ResultType ::= : Type
            //
            case 311: {
               //#line 2763 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2761 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2763 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 312:  HasResultType ::= : Type
            //
            case 312: {
               //#line 2768 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2766 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2768 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 313:  HasResultType ::= <: Type
            //
            case 313: {
               //#line 2773 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2771 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 2773 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.HasType(Type));
                      break;
            }
    
            //
            // Rule 314:  FormalParameters ::= ( FormalParameterList )
            //
            case 314: {
               //#line 2779 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2777 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(2);
                //#line 2779 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterList);
                      break;
            }
    
            //
            // Rule 315:  FormalParameterList ::= FormalParameter
            //
            case 315: {
               //#line 2785 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2783 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 2785 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 316:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 316: {
               //#line 2792 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2790 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 2790 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2792 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 317:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 317: {
               //#line 2798 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2796 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2796 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 2798 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 318:  LoopIndexDeclarator ::= ( IdentifierList ) HasResultTypeopt
            //
            case 318: {
               //#line 2803 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2801 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 2801 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 2803 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 319:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt
            //
            case 319: {
               //#line 2808 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2806 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2806 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 2806 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 2808 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 320:  LoopIndex ::= VariableModifiersopt LoopIndexDeclarator
            //
            case 320: {
               //#line 2814 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2812 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2812 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 2814 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2837 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2835 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2835 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2835 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 2837 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2861 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2859 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2859 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 2861 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2885 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2883 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 2883 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 2883 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 2885 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2909 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2907 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2909 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh("id$")), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 325:  VariableModifiers ::= VariableModifier
            //
            case 325: {
               //#line 2917 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2915 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(1);
                //#line 2917 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(VariableModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 326:  VariableModifiers ::= VariableModifiers VariableModifier
            //
            case 326: {
               //#line 2924 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2922 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiers = (List) getRhsSym(1);
                //#line 2922 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableModifier = (List) getRhsSym(2);
                //#line 2924 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableModifiers.addAll(VariableModifier);
                      break;
            }
    
            //
            // Rule 327:  VariableModifier ::= Annotation
            //
            case 327: {
               //#line 2930 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2928 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2930 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 328:  VariableModifier ::= shared
            //
            case 328: {
               //#line 2935 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2935 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SHARED)));
                      break;
            }
    
            //
            // Rule 329:  MethodModifiers ::= MethodModifier
            //
            case 329: {
               //#line 2944 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2942 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(1);
                //#line 2944 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(MethodModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 330:  MethodModifiers ::= MethodModifiers MethodModifier
            //
            case 330: {
               //#line 2951 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2949 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiers = (List) getRhsSym(1);
                //#line 2949 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List MethodModifier = (List) getRhsSym(2);
                //#line 2951 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiers.addAll(MethodModifier);
                      break;
            }
    
            //
            // Rule 331:  MethodModifier ::= Annotation
            //
            case 331: {
               //#line 2957 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2955 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 2957 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 332:  MethodModifier ::= public
            //
            case 332: {
               //#line 2962 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2962 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 333:  MethodModifier ::= protected
            //
            case 333: {
               //#line 2967 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2967 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 334:  MethodModifier ::= private
            //
            case 334: {
               //#line 2972 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2972 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 335:  MethodModifier ::= abstract
            //
            case 335: {
               //#line 2977 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2977 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 336:  MethodModifier ::= static
            //
            case 336: {
               //#line 2982 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2982 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 337:  MethodModifier ::= final
            //
            case 337: {
               //#line 2987 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2987 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 338:  MethodModifier ::= native
            //
            case 338: {
               //#line 2992 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2992 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 339:  MethodModifier ::= atomic
            //
            case 339: {
               //#line 2997 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2997 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.ATOMIC)));
                      break;
            }
    
            //
            // Rule 340:  MethodModifier ::= extern
            //
            case 340: {
               //#line 3002 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3002 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.EXTERN)));
                      break;
            }
    
            //
            // Rule 341:  MethodModifier ::= safe
            //
            case 341: {
               //#line 3007 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3007 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SAFE)));
                      break;
            }
    
            //
            // Rule 342:  MethodModifier ::= sequential
            //
            case 342: {
               //#line 3012 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3012 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.SEQUENTIAL)));
                      break;
            }
    
            //
            // Rule 343:  MethodModifier ::= nonblocking
            //
            case 343: {
               //#line 3017 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3017 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.NON_BLOCKING)));
                      break;
            }
    
            //
            // Rule 344:  MethodModifier ::= incomplete
            //
            case 344: {
               //#line 3022 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3022 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.INCOMPLETE)));
                      break;
            }
    
            //
            // Rule 345:  MethodModifier ::= property
            //
            case 345: {
               //#line 3027 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3027 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROPERTY)));
                      break;
            }
    
            //
            // Rule 346:  MethodModifier ::= global
            //
            case 346: {
               //#line 3032 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3032 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.GLOBAL)));
                      break;
            }
    
            //
            // Rule 347:  MethodModifier ::= proto
            //
            case 347: {
               //#line 3037 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3037 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), X10Flags.PROTO)));
                      break;
            }
    
            //
            // Rule 348:  Throws ::= throws ExceptionTypeList
            //
            case 348: {
               //#line 3044 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3042 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 3044 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 349:  Offers ::= offers Type
            //
            case 349: {
               //#line 3049 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3047 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3049 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 350:  ExceptionTypeList ::= ExceptionType
            //
            case 350: {
               //#line 3055 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3053 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3055 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 351:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 351: {
               //#line 3062 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3060 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3060 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3062 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 353:  MethodBody ::= = LastExpression ;
            //
            case 353: {
               //#line 3070 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3068 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3070 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 354:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 354: {
               //#line 3075 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3073 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3073 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(4);
                //#line 3073 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3075 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3083 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3081 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3081 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3083 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 356:  MethodBody ::= Annotationsopt Block
            //
            case 356: {
               //#line 3088 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3086 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 3086 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3088 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3108 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3106 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3108 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 359:  ConstructorModifiers ::= ConstructorModifier
            //
            case 359: {
               //#line 3114 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3112 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(1);
                //#line 3114 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(ConstructorModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 360:  ConstructorModifiers ::= ConstructorModifiers ConstructorModifier
            //
            case 360: {
               //#line 3121 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3119 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifiers = (List) getRhsSym(1);
                //#line 3119 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ConstructorModifier = (List) getRhsSym(2);
                //#line 3121 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                ConstructorModifiers.addAll(ConstructorModifier);
                      break;
            }
    
            //
            // Rule 361:  ConstructorModifier ::= Annotation
            //
            case 361: {
               //#line 3127 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3125 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3127 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 362:  ConstructorModifier ::= public
            //
            case 362: {
               //#line 3132 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3132 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 363:  ConstructorModifier ::= protected
            //
            case 363: {
               //#line 3137 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3137 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 364:  ConstructorModifier ::= private
            //
            case 364: {
               //#line 3142 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3142 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 365:  ConstructorModifier ::= native
            //
            case 365: {
               //#line 3147 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3147 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NATIVE)));
                      break;
            }
    
            //
            // Rule 366:  ConstructorBody ::= = ConstructorBlock
            //
            case 366: {
               //#line 3153 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3151 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3153 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 367:  ConstructorBody ::= ConstructorBlock
            //
            case 367: {
               //#line 3158 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3156 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3158 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 368:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 368: {
               //#line 3163 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3161 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3163 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3171 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3169 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3171 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3182 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3180 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3180 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3182 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3195 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3193 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3195 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 374:  InterfaceModifiers ::= InterfaceModifier
            //
            case 374: {
               //#line 3205 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3203 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(1);
                //#line 3205 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new LinkedList();
                l.addAll(InterfaceModifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 375:  InterfaceModifiers ::= InterfaceModifiers InterfaceModifier
            //
            case 375: {
               //#line 3212 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3210 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifiers = (List) getRhsSym(1);
                //#line 3210 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List InterfaceModifier = (List) getRhsSym(2);
                //#line 3212 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceModifiers.addAll(InterfaceModifier);
                      break;
            }
    
            //
            // Rule 376:  InterfaceModifier ::= Annotation
            //
            case 376: {
               //#line 3218 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3216 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3218 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(Annotation));
                      break;
            }
    
            //
            // Rule 377:  InterfaceModifier ::= public
            //
            case 377: {
               //#line 3223 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3223 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PUBLIC)));
                      break;
            }
    
            //
            // Rule 378:  InterfaceModifier ::= protected
            //
            case 378: {
               //#line 3228 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3228 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PROTECTED)));
                      break;
            }
    
            //
            // Rule 379:  InterfaceModifier ::= private
            //
            case 379: {
               //#line 3233 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3233 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.PRIVATE)));
                      break;
            }
    
            //
            // Rule 380:  InterfaceModifier ::= abstract
            //
            case 380: {
               //#line 3238 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3238 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.ABSTRACT)));
                      break;
            }
    
            //
            // Rule 381:  InterfaceModifier ::= static
            //
            case 381: {
               //#line 3243 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3243 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.STATIC)));
                      break;
            }
    
            //
            // Rule 382:  ExtendsInterfaces ::= extends Type
            //
            case 382: {
               //#line 3249 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3247 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3249 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 383:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 383: {
               //#line 3256 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3254 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3254 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3256 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 384:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 384: {
               //#line 3265 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3263 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3265 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 386:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 386: {
               //#line 3272 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3270 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3270 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3272 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 387:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 387: {
               //#line 3279 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3277 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3279 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 388:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 388: {
               //#line 3286 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3284 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3286 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 389:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 389: {
               //#line 3293 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3291 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3293 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 390:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 390: {
               //#line 3300 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3298 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3300 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 391:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 391: {
               //#line 3307 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3305 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3307 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 392:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 392: {
               //#line 3314 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3312 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3314 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 393:  InterfaceMemberDeclaration ::= ;
            //
            case 393: {
               //#line 3321 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3321 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 394:  Annotations ::= Annotation
            //
            case 394: {
               //#line 3327 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3325 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3327 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 395:  Annotations ::= Annotations Annotation
            //
            case 395: {
               //#line 3334 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3332 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3332 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3334 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 396:  Annotation ::= @ NamedType
            //
            case 396: {
               //#line 3340 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3338 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3340 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 397:  SimpleName ::= Identifier
            //
            case 397: {
               //#line 3346 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3344 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3346 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 398:  Identifier ::= identifier
            //
            case 398: {
               //#line 3352 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3350 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3352 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 399:  VariableInitializers ::= VariableInitializer
            //
            case 399: {
               //#line 3360 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3358 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(1);
                //#line 3360 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(VariableInitializer);
                setResult(l);
                      break;
            }
    
            //
            // Rule 400:  VariableInitializers ::= VariableInitializers , VariableInitializer
            //
            case 400: {
               //#line 3367 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3365 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableInitializers = (List) getRhsSym(1);
                //#line 3365 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(3);
                //#line 3367 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableInitializers.add(VariableInitializer);
                //setResult(VariableInitializers);
                      break;
            }
    
            //
            // Rule 401:  Block ::= { BlockStatementsopt }
            //
            case 401: {
               //#line 3385 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3383 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3385 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 402:  BlockStatements ::= BlockStatement
            //
            case 402: {
               //#line 3391 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3389 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3391 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 403:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 403: {
               //#line 3398 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3396 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3396 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3398 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 405:  BlockStatement ::= ClassDeclaration
            //
            case 405: {
               //#line 3406 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3404 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3406 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 406:  BlockStatement ::= TypeDefDeclaration
            //
            case 406: {
               //#line 3413 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3411 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3413 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 407:  BlockStatement ::= Statement
            //
            case 407: {
               //#line 3420 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3418 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3420 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 408:  IdentifierList ::= Identifier
            //
            case 408: {
               //#line 3428 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3426 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3428 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 409:  IdentifierList ::= IdentifierList , Identifier
            //
            case 409: {
               //#line 3435 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3433 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3433 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3435 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 410:  FormalDeclarator ::= Identifier ResultType
            //
            case 410: {
               //#line 3441 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3439 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3439 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3441 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 411:  FormalDeclarator ::= ( IdentifierList ) ResultType
            //
            case 411: {
               //#line 3446 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3444 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3444 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3446 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 412:  FormalDeclarator ::= Identifier ( IdentifierList ) ResultType
            //
            case 412: {
               //#line 3451 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3449 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3449 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3449 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3451 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 413:  FieldDeclarator ::= Identifier HasResultType
            //
            case 413: {
               //#line 3457 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3455 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3455 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3457 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultType, null });
                      break;
            }
    
            //
            // Rule 414:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 414: {
               //#line 3462 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3460 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3460 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3460 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3462 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 415:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 415: {
               //#line 3468 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3466 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3466 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3466 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3468 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 416:  VariableDeclarator ::= ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 416: {
               //#line 3473 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3471 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3471 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3471 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3473 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 417:  VariableDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 417: {
               //#line 3478 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3476 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3476 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3476 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3476 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3478 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 418:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 418: {
               //#line 3484 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3482 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3482 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3482 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3484 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 419:  VariableDeclaratorWithType ::= ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 419: {
               //#line 3489 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3487 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3487 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(4);
                //#line 3487 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3489 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 420:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 420: {
               //#line 3494 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3492 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3492 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3492 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(5);
                //#line 3492 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3494 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 422:  LocalVariableDeclaration ::= VariableModifiersopt VarKeyword VariableDeclarators
            //
            case 422: {
               //#line 3502 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3500 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3500 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3500 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3502 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3535 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3533 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3533 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 3535 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3569 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3567 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VariableModifiersopt = (List) getRhsSym(1);
                //#line 3567 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3567 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 3569 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3610 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3610 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 426:  Primary ::= [ ArgumentListopt ]
            //
            case 426: {
               //#line 3616 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3614 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3616 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 428:  Primary ::= self
            //
            case 428: {
               //#line 3624 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3624 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 429:  Primary ::= this
            //
            case 429: {
               //#line 3629 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3629 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 430:  Primary ::= ClassName . this
            //
            case 430: {
               //#line 3634 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3632 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3634 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 431:  Primary ::= ( Expression )
            //
            case 431: {
               //#line 3639 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3637 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 3639 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 437:  OperatorFunction ::= TypeName . +
            //
            case 437: {
               //#line 3650 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3648 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3650 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3661 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3659 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3661 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3672 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3670 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3672 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3683 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3681 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3683 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3694 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3692 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3694 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3705 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3703 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3705 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3716 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3714 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3716 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3727 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3725 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3727 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3738 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3736 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3738 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3749 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3747 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3749 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3760 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3758 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3760 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3771 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3769 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3771 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3782 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3780 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3782 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3793 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3791 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3793 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3804 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3802 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3804 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3815 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3813 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3815 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3826 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3824 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 3826 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3839 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3837 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3839 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 455:  Literal ::= LongLiteral$lit
            //
            case 455: {
               //#line 3845 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3843 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3845 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 456:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 456: {
               //#line 3851 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3849 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3851 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 457:  Literal ::= UnsignedLongLiteral$lit
            //
            case 457: {
               //#line 3857 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3855 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3857 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 458:  Literal ::= FloatingPointLiteral$lit
            //
            case 458: {
               //#line 3863 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3861 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3863 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 459:  Literal ::= DoubleLiteral$lit
            //
            case 459: {
               //#line 3869 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3867 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3869 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 460:  Literal ::= BooleanLiteral
            //
            case 460: {
               //#line 3875 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3873 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 3875 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 461:  Literal ::= CharacterLiteral$lit
            //
            case 461: {
               //#line 3880 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3878 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 3880 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 462:  Literal ::= StringLiteral$str
            //
            case 462: {
               //#line 3886 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3884 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 3886 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 463:  Literal ::= null
            //
            case 463: {
               //#line 3892 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3892 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 464:  BooleanLiteral ::= true$trueLiteral
            //
            case 464: {
               //#line 3898 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3896 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 3898 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 465:  BooleanLiteral ::= false$falseLiteral
            //
            case 465: {
               //#line 3903 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3901 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 3903 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 466:  ArgumentList ::= Expression
            //
            case 466: {
               //#line 3912 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3910 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 3912 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 467:  ArgumentList ::= ArgumentList , Expression
            //
            case 467: {
               //#line 3919 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3917 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 3917 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 3919 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 468:  FieldAccess ::= Primary . Identifier
            //
            case 468: {
               //#line 3925 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3923 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3923 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3925 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 469:  FieldAccess ::= super . Identifier
            //
            case 469: {
               //#line 3930 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3928 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3930 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 470:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 470: {
               //#line 3935 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3933 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3933 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3933 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3935 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 471:  FieldAccess ::= Primary . class$c
            //
            case 471: {
               //#line 3940 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3938 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3938 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3940 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 472:  FieldAccess ::= super . class$c
            //
            case 472: {
               //#line 3945 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3943 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 3945 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 473:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 473: {
               //#line 3950 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3948 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3948 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3948 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 3950 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 474:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 474: {
               //#line 3956 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3954 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3954 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3954 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3956 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 475:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 475: {
               //#line 3963 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3961 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3961 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3961 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3961 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3963 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 476:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 476: {
               //#line 3968 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3966 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3966 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 3966 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 3968 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 477:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 477: {
               //#line 3973 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3971 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 3971 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 3971 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 3971 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 3971 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 3973 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 478:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 478: {
               //#line 3978 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3976 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 3976 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 3976 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 3978 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3998 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3996 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 3996 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(4);
                //#line 3998 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4011 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4009 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4009 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4009 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4011 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4023 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4021 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4021 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4023 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4035 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4033 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4033 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4033 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4033 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(8);
                //#line 4035 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4053 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4051 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4053 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 487:  PostDecrementExpression ::= PostfixExpression --
            //
            case 487: {
               //#line 4059 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4057 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4059 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 490:  UnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 490: {
               //#line 4067 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4065 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4067 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 491:  UnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 491: {
               //#line 4072 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4070 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4072 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 493:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 493: {
               //#line 4079 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4077 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4079 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 494:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 494: {
               //#line 4085 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4083 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4085 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 496:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 496: {
               //#line 4092 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4090 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4092 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 497:  UnaryExpressionNotPlusMinus ::= Annotations UnaryExpression
            //
            case 497: {
               //#line 4097 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4095 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4095 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4097 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e.position(pos()));
                      break;
            }
    
            //
            // Rule 498:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 498: {
               //#line 4104 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4102 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4104 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 500:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 500: {
               //#line 4111 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4109 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4109 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4111 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 501:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 501: {
               //#line 4116 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4114 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4114 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4116 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 502:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 502: {
               //#line 4121 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4119 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4119 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4121 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 504:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 504: {
               //#line 4128 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4126 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4126 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4128 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 505:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 505: {
               //#line 4133 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4131 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4131 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4133 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 507:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 507: {
               //#line 4140 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4138 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4138 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4140 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 508:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 508: {
               //#line 4145 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4143 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4143 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4145 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 509:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 509: {
               //#line 4150 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4148 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4148 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4150 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 511:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 511: {
               //#line 4157 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4155 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4155 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4157 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 514:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 514: {
               //#line 4166 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4164 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4164 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4166 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 515:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 515: {
               //#line 4171 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4169 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4169 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4171 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 516:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 516: {
               //#line 4176 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4174 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4174 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4176 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 517:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 517: {
               //#line 4181 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4179 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4179 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4181 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 518:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 518: {
               //#line 4186 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4184 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4184 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4186 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 519:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 519: {
               //#line 4191 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4189 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4189 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4191 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 521:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 521: {
               //#line 4198 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4196 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4196 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4198 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 522:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 522: {
               //#line 4203 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4201 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4201 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4203 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 523:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 523: {
               //#line 4208 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4206 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4206 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4208 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 525:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 525: {
               //#line 4215 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4213 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4213 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4215 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 527:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 527: {
               //#line 4222 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4220 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4220 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4222 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 529:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 529: {
               //#line 4229 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4227 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4227 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4229 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 531:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 531: {
               //#line 4236 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4234 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4234 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4236 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 533:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 533: {
               //#line 4243 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4241 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4241 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4243 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 540:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 540: {
               //#line 4256 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4254 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4254 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4254 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4256 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 543:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 543: {
               //#line 4265 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4263 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4263 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4263 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4265 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 544:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 544: {
               //#line 4270 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4268 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4268 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4268 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4268 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4270 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 545:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 545: {
               //#line 4275 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4273 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4273 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4273 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4273 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4275 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 546:  LeftHandSide ::= ExpressionName
            //
            case 546: {
               //#line 4281 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4279 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4281 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 548:  AssignmentOperator ::= =
            //
            case 548: {
               //#line 4288 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4288 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 549:  AssignmentOperator ::= *=
            //
            case 549: {
               //#line 4293 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4293 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 550:  AssignmentOperator ::= /=
            //
            case 550: {
               //#line 4298 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4298 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 551:  AssignmentOperator ::= %=
            //
            case 551: {
               //#line 4303 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4303 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 552:  AssignmentOperator ::= +=
            //
            case 552: {
               //#line 4308 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4308 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 553:  AssignmentOperator ::= -=
            //
            case 553: {
               //#line 4313 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4313 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 554:  AssignmentOperator ::= <<=
            //
            case 554: {
               //#line 4318 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4318 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 555:  AssignmentOperator ::= >>=
            //
            case 555: {
               //#line 4323 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4323 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 556:  AssignmentOperator ::= >>>=
            //
            case 556: {
               //#line 4328 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4328 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 557:  AssignmentOperator ::= &=
            //
            case 557: {
               //#line 4333 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4333 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 558:  AssignmentOperator ::= ^=
            //
            case 558: {
               //#line 4338 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4338 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 559:  AssignmentOperator ::= |=
            //
            case 559: {
               //#line 4343 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4343 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 562:  PrefixOp ::= +
            //
            case 562: {
               //#line 4354 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4354 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 563:  PrefixOp ::= -
            //
            case 563: {
               //#line 4359 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4359 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 564:  PrefixOp ::= !
            //
            case 564: {
               //#line 4364 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4364 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 565:  PrefixOp ::= ~
            //
            case 565: {
               //#line 4369 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4369 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 566:  BinOp ::= +
            //
            case 566: {
               //#line 4375 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4375 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 567:  BinOp ::= -
            //
            case 567: {
               //#line 4380 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4380 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 568:  BinOp ::= *
            //
            case 568: {
               //#line 4385 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4385 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 569:  BinOp ::= /
            //
            case 569: {
               //#line 4390 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4390 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 570:  BinOp ::= %
            //
            case 570: {
               //#line 4395 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4395 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 571:  BinOp ::= &
            //
            case 571: {
               //#line 4400 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4400 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 572:  BinOp ::= |
            //
            case 572: {
               //#line 4405 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4405 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 573:  BinOp ::= ^
            //
            case 573: {
               //#line 4410 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4410 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 574:  BinOp ::= &&
            //
            case 574: {
               //#line 4415 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4415 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 575:  BinOp ::= ||
            //
            case 575: {
               //#line 4420 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4420 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 576:  BinOp ::= <<
            //
            case 576: {
               //#line 4425 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4425 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 577:  BinOp ::= >>
            //
            case 577: {
               //#line 4430 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4430 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 578:  BinOp ::= >>>
            //
            case 578: {
               //#line 4435 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4435 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 579:  BinOp ::= >=
            //
            case 579: {
               //#line 4440 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4440 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 580:  BinOp ::= <=
            //
            case 580: {
               //#line 4445 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4445 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 581:  BinOp ::= >
            //
            case 581: {
               //#line 4450 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4450 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 582:  BinOp ::= <
            //
            case 582: {
               //#line 4455 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4455 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 583:  BinOp ::= ==
            //
            case 583: {
               //#line 4463 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4463 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 584:  BinOp ::= !=
            //
            case 584: {
               //#line 4468 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4468 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 585:  Catchesopt ::= $Empty
            //
            case 585: {
               //#line 4477 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4477 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4486 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4484 "C:/eclipse/workspace/3.5.2/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4486 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 589:  ForUpdateopt ::= $Empty
            //
            case 589: {
               //#line 4492 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4492 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4503 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4503 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 595:  SwitchLabelsopt ::= $Empty
            //
            case 595: {
               //#line 4510 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4510 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 597:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 597: {
               //#line 4517 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4517 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 599:  VariableModifiersopt ::= $Empty
            //
            case 599: {
               //#line 4524 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4524 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4535 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4535 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 605:  ExtendsInterfacesopt ::= $Empty
            //
            case 605: {
               //#line 4542 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4542 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 607:  InterfaceModifiersopt ::= $Empty
            //
            case 607: {
               //#line 4549 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4549 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4560 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4560 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 613:  ArgumentListopt ::= $Empty
            //
            case 613: {
               //#line 4567 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4567 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 615:  BlockStatementsopt ::= $Empty
            //
            case 615: {
               //#line 4574 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4574 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4585 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4585 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 621:  FormalParameterListopt ::= $Empty
            //
            case 621: {
               //#line 4592 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4592 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 623:  Throwsopt ::= $Empty
            //
            case 623: {
               //#line 4599 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4599 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 625:  Offersopt ::= $Empty
            //
            case 625: {
               //#line 4605 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4605 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 627:  MethodModifiersopt ::= $Empty
            //
            case 627: {
               //#line 4612 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4612 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 629:  TypeModifieropt ::= $Empty
            //
            case 629: {
               //#line 4619 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4619 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 631:  FieldModifiersopt ::= $Empty
            //
            case 631: {
               //#line 4626 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4626 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 633:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 633: {
               //#line 4633 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4633 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 635:  Interfacesopt ::= $Empty
            //
            case 635: {
               //#line 4640 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4640 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4651 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4651 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 641:  FormalParametersopt ::= $Empty
            //
            case 641: {
               //#line 4658 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4658 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 643:  Annotationsopt ::= $Empty
            //
            case 643: {
               //#line 4665 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4665 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 645:  TypeDeclarationsopt ::= $Empty
            //
            case 645: {
               //#line 4672 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4672 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 647:  ImportDeclarationsopt ::= $Empty
            //
            case 647: {
               //#line 4679 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4679 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 4697 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4697 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 657:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 657: {
               //#line 4704 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4704 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 659:  Propertiesopt ::= $Empty
            //
            case 659: {
               //#line 4711 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4711 "C:/eclipse/workspace/3.5.2/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

