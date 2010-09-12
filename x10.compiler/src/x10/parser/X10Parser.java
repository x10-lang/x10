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

//#line 32 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
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
import x10.ast.SettableAssign;
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
    

    //#line 314 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
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

    //
    // Temporary classes used to wrap modifiers.
    //
    private static class Modifier {
    }

    private static class FlagModifier extends Modifier {
        public static int ABSTRACT    = 0;
        public static int ATOMIC      = 1;
        public static int EXTERN      = 2;
        public static int FINAL       = 3;
        public static int GLOBAL      = 4;
        public static int INCOMPLETE  = 5;
        public static int NATIVE      = 6;
        public static int NON_BLOCKING = 7;
        public static int PRIVATE     = 8;
        public static int PROPERTY    = 9;
        public static int PROTECTED   = 10;
        public static int PUBLIC      = 11;
        public static int SAFE        = 12;
        public static int SEQUENTIAL  = 13;
        public static int CLOCKED     = 14;
        public static int STATIC      = 15;
        public static int TRANSIENT   = 16;
        public static int NUM_FLAGS   = TRANSIENT + 1;

        private JPGPosition pos;
        private int flag;

        public JPGPosition position() { return pos; }
        public int flag() { return flag; }
        public Flags flags() {
            if (flag == ABSTRACT)     return Flags.ABSTRACT;
            if (flag == ATOMIC)       return X10Flags.ATOMIC;
            if (flag == EXTERN)       return X10Flags.EXTERN;
            if (flag == FINAL)        return Flags.FINAL;
            if (flag == GLOBAL)       return X10Flags.GLOBAL;
            if (flag == INCOMPLETE)   return X10Flags.INCOMPLETE;
            if (flag == NATIVE)       return Flags.NATIVE;
            if (flag == NON_BLOCKING) return X10Flags.NON_BLOCKING;
            if (flag == PRIVATE)      return Flags.PRIVATE;
            if (flag == PROPERTY)     return X10Flags.PROPERTY;
            if (flag == PROTECTED)    return Flags.PROTECTED;
            if (flag == PUBLIC)       return Flags.PUBLIC;
            if (flag == SAFE)         return X10Flags.SAFE;
            if (flag == SEQUENTIAL)   return X10Flags.SEQUENTIAL;
            if (flag == CLOCKED)       return X10Flags.CLOCKED;
            if (flag == TRANSIENT)    return X10Flags.TRANSIENT;
            if (flag == STATIC)       return Flags.STATIC;
            assert(false);
            return null;
        }

        public String name() {
            if (flag == ABSTRACT)     return "abstract";
            if (flag == ATOMIC)       return "atomic";
            if (flag == EXTERN)       return "extern";
            if (flag == FINAL)        return "final";
            if (flag == GLOBAL)       return "global";
            if (flag == INCOMPLETE)   return "incomplete";
            if (flag == NATIVE)       return "native";
            if (flag == NON_BLOCKING) return "nonblocking";
            if (flag == PRIVATE)      return "private";
            if (flag == PROPERTY)     return "property";
            if (flag == PROTECTED)    return "protected";
            if (flag == PUBLIC)       return "public";
            if (flag == SAFE)         return "safe";
            if (flag == SEQUENTIAL)   return "sequential";
            if (flag == CLOCKED)       return "clocked";
            if (flag == STATIC)       return "static";
            if (flag == TRANSIENT)    return "transient";
            assert(false);
            return "?";
        }


        public static boolean classModifiers[] = new boolean[NUM_FLAGS];
        static {
            classModifiers[ABSTRACT] = true;
            classModifiers[FINAL] = true;
            classModifiers[PRIVATE] = true;
            classModifiers[PROTECTED] = true;
            classModifiers[PUBLIC] = true;
            classModifiers[SAFE] = true;
            classModifiers[STATIC] = true;
            classModifiers[CLOCKED] = true;
            // classModifiers[GLOBAL] = true;
        }
        public boolean isClassModifier(int flag) {
            return  classModifiers[flag];
        }

        public static boolean typeDefModifiers[] = new boolean[NUM_FLAGS];
        static {
            typeDefModifiers[ABSTRACT] = true;
            typeDefModifiers[FINAL] = true;
            typeDefModifiers[PRIVATE] = true;
            typeDefModifiers[PROTECTED] = true;
            typeDefModifiers[PUBLIC] = true;
            typeDefModifiers[STATIC] = true;
        }
        public boolean isTypeDefModifier(int flag) {
            return typeDefModifiers[flag];
        }

        public static boolean fieldModifiers[] = new boolean[NUM_FLAGS];
        static {
            fieldModifiers[TRANSIENT] = true;
            // fieldModifiers[GLOBAL] = true;
            fieldModifiers[CLOCKED] = true;
            fieldModifiers[PRIVATE] = true;
            fieldModifiers[PROTECTED] = true;
            fieldModifiers[PROPERTY] = true;
            fieldModifiers[PUBLIC] = true;
            fieldModifiers[STATIC] = true;
        }
        public boolean isFieldModifier(int flag) {
            return fieldModifiers[flag];
        }

        public static boolean variableModifiers[] = new boolean[NUM_FLAGS];
        static {
            variableModifiers[CLOCKED] = true;
        }
        public boolean isVariableModifier(int flag) {
            return variableModifiers[flag];
        }

        public static boolean methodModifiers[] = new boolean[NUM_FLAGS];
        static {
            methodModifiers[ABSTRACT] = true;
            methodModifiers[ATOMIC] = true;
            methodModifiers[EXTERN] = true;
            methodModifiers[FINAL] = true;
            // methodModifiers[GLOBAL] = true;
            methodModifiers[INCOMPLETE] = true;
            methodModifiers[NATIVE] = true;
            methodModifiers[NON_BLOCKING] = true;
            methodModifiers[PRIVATE] = true;
            methodModifiers[PROPERTY] = true;
            methodModifiers[PROTECTED] = true;
            methodModifiers[PUBLIC] = true;
            methodModifiers[SAFE] = true;
            methodModifiers[SEQUENTIAL] = true;
            methodModifiers[STATIC] = true;
            methodModifiers[CLOCKED] = true;
        }
        public boolean isMethodModifier(int flag) {
            return methodModifiers[flag];
        }

        public static boolean constructorModifiers[] = new boolean[NUM_FLAGS];
        static {
            constructorModifiers[NATIVE] = true;
            constructorModifiers[PRIVATE] = true;
            constructorModifiers[PROTECTED] = true;
            constructorModifiers[PUBLIC] = true;
        }
        public boolean isConstructorModifier(int flag) {
            return constructorModifiers[flag];
        }

        public static boolean interfaceModifiers[] = new boolean[NUM_FLAGS];
        static {
            interfaceModifiers[ABSTRACT] = true;
            interfaceModifiers[PRIVATE] = true;
            interfaceModifiers[PROTECTED] = true;
            interfaceModifiers[PUBLIC] = true;
            interfaceModifiers[STATIC] = true;
            interfaceModifiers[CLOCKED] = true;

        }
        public boolean isInterfaceModifier(int flag) {
            return interfaceModifiers[flag];
        }

        public FlagModifier(JPGPosition pos, int flag) {
            this.pos = pos;
            this.flag = flag;
        }
    }

    private static class AnnotationModifier extends Modifier {
        private AnnotationNode annotation;

        public AnnotationNode annotation() { return annotation; }
        
        public AnnotationModifier(AnnotationNode annotation) {
            this.annotation = annotation;
        }
    }

    //    
    // TODO: Say something!
    //    
    private List checkModifiers(String kind, List modifiers, boolean legal_flags[]) {
        List l = new LinkedList();

        assert(modifiers.size() > 0);

        boolean flags[] = new boolean[FlagModifier.NUM_FLAGS]; // initialized to false
        for (int i = 0; i < modifiers.size(); i++) {
            Object element = modifiers.get(i);
            if (element instanceof FlagModifier) {
                FlagModifier modifier = (FlagModifier) element;
                l.addAll(Collections.singletonList(nf.FlagsNode(modifier.position(), modifier.flags())));

                if (! flags[modifier.flag()]) {
                    flags[modifier.flag()] = true;
                }
                else {
                    syntaxError("Duplicate specification of modifier: " + modifier.name(), modifier.position());
                 }

                if (! legal_flags[modifier.flag()]) {
                    syntaxError("\"" + modifier.name() + "\" is not a valid " + kind + " modifier", modifier.position());
                }
            }
            else {
                AnnotationModifier modifier = (AnnotationModifier) element;
                l.addAll(Collections.singletonList(modifier.annotation()));
            }
        }

        return l;
    }

    private List checkClassModifiers(List modifiers) {
        return (modifiers.size() == 0
                 ? Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE)))
                 : checkModifiers("class", modifiers, FlagModifier.classModifiers));
    }

    private List checkTypeDefModifiers(List modifiers) {
        return (modifiers.size() == 0
                 ? Collections.singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE)))
                 : checkModifiers("typedef",  modifiers, FlagModifier.typeDefModifiers));
    }

    private List checkFieldModifiers(List modifiers) {
        return (modifiers.size() == 0
                 ? Collections.EMPTY_LIST
                 : checkModifiers("field",  modifiers, FlagModifier.fieldModifiers));
    }

    private List checkVariableModifiers(List modifiers) {
        return (modifiers.size() == 0
                 ? Collections.EMPTY_LIST
                 : checkModifiers("variable",  modifiers, FlagModifier.variableModifiers));
    }

    private List checkMethodModifiers(List modifiers) {
        return (modifiers.size() == 0
                 ? Collections.EMPTY_LIST
                 : checkModifiers("method",  modifiers, FlagModifier.methodModifiers));
    }

    private List checkConstructorModifiers(List modifiers) {
        return (modifiers.size() == 0
                 ? Collections.EMPTY_LIST
                 : checkModifiers("constructor",  modifiers, FlagModifier.constructorModifiers));
    }

    private List checkInterfaceModifiers(List modifiers) {
        return (modifiers.size() == 0
                 ? Collections.EMPTY_LIST
                 : checkModifiers("interface",  modifiers, FlagModifier.interfaceModifiers));
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
               //#line 8 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 6 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 8 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 18 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 16 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 18 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 28 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 26 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 28 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 38 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 36 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 38 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 48 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 46 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 48 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 58 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 56 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 58 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 68 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 66 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 74 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 74 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 80 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 78 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 78 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 87 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 85 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 87 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 94 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 92 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 94 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 100 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 98 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 100 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 109 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 107 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 117 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 115 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                      break;
            }
    
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 122 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 120 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 120 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "C:/eclipsews/head1/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Object[] a = new Object[3];
                a[0] = ClassName;
                a[1] = pos(getRhsFirstTokenIndex(3));
                a[2] = id(getRhsFirstTokenIndex(5));
                setResult(a);
                      break;
            }
    
            //
            // Rule 16:  Modifiersopt ::= $Empty
            //
            case 16: {
               //#line 1189 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1189 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new LinkedList());
                      break;
            }
    
            //
            // Rule 17:  Modifiersopt ::= Modifiersopt Modifier
            //
            case 17: {
               //#line 1194 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1192 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 1192 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Modifier Modifier = (Modifier) getRhsSym(2);
                //#line 1194 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Modifiersopt.add(Modifier);
                      break;
            }
    
            //
            // Rule 18:  Modifier ::= abstract
            //
            case 18: {
               //#line 1200 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1200 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.ABSTRACT));
                      break;
            }
    
            //
            // Rule 19:  Modifier ::= Annotation
            //
            case 19: {
               //#line 1205 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1203 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 1205 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new AnnotationModifier(Annotation));
                      break;
            }
    
            //
            // Rule 20:  Modifier ::= atomic
            //
            case 20: {
               //#line 1210 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1210 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.ATOMIC));
                      break;
            }
    
            //
            // Rule 21:  Modifier ::= extern
            //
            case 21: {
               //#line 1215 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1215 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.EXTERN));
                      break;
            }
    
            //
            // Rule 22:  Modifier ::= final
            //
            case 22: {
               //#line 1220 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1220 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.FINAL));
                      break;
            }
    
            //
            // Rule 23:  Modifier ::= global
            //
            case 23: {
               //#line 1225 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1225 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.GLOBAL));
                      break;
            }
    
            //
            // Rule 24:  Modifier ::= incomplete
            //
            case 24: {
               //#line 1230 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1230 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.INCOMPLETE));
                      break;
            }
    
            //
            // Rule 25:  Modifier ::= native
            //
            case 25: {
               //#line 1235 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1235 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.NATIVE));
                      break;
            }
    
            //
            // Rule 26:  Modifier ::= nonblocking
            //
            case 26: {
               //#line 1240 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1240 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.NON_BLOCKING));
                      break;
            }
    
            //
            // Rule 27:  Modifier ::= private
            //
            case 27: {
               //#line 1245 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1245 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PRIVATE));
                      break;
            }
    
            //
            // Rule 28:  Modifier ::= protected
            //
            case 28: {
               //#line 1250 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1250 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PROTECTED));
                      break;
            }
    
            //
            // Rule 29:  Modifier ::= public
            //
            case 29: {
               //#line 1255 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1255 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PUBLIC));
                      break;
            }
    
            //
            // Rule 30:  Modifier ::= safe
            //
            case 30: {
               //#line 1260 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1260 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.SAFE));
                      break;
            }
    
            //
            // Rule 31:  Modifier ::= sequential
            //
            case 31: {
               //#line 1265 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1265 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.SEQUENTIAL));
                      break;
            }
    
            //
            // Rule 32:  Modifier ::= static
            //
            case 32: {
               //#line 1270 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1270 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.STATIC));
                      break;
            }
    
            //
            // Rule 33:  Modifier ::= transient
            //
            case 33: {
               //#line 1275 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1275 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.TRANSIENT));
                      break;
            }
    
            //
            // Rule 34:  Modifier ::= clocked
            //
            case 34: {
               //#line 1280 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1280 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.CLOCKED));
                      break;
            }
    
            //
            // Rule 36:  MethodModifiersopt ::= MethodModifiersopt property$property
            //
            case 36: {
               //#line 1287 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1285 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1285 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken property = (IToken) getRhsIToken(2);
                //#line 1287 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiersopt.add(new FlagModifier(pos(getRhsFirstTokenIndex(2)), FlagModifier.PROPERTY));
                      break;
            }
    
            //
            // Rule 37:  MethodModifiersopt ::= MethodModifiersopt Modifier
            //
            case 37: {
               //#line 1292 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1290 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1290 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Modifier Modifier = (Modifier) getRhsSym(2);
                //#line 1292 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiersopt.add(Modifier);
                      break;
            }
    
            //
            // Rule 38:  TypeDefDeclaration ::= Modifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
            //
            case 38: {
               //#line 1298 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1296 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 1296 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1296 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1296 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 1296 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1296 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1298 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Modifiersopt = checkTypeDefModifiers(Modifiersopt);
                FlagsNode f = extractFlags(Modifiersopt);
                List annotations = extractAnnotations(Modifiersopt);
                for (Formal v : (List<Formal>) FormalParametersopt) {
                    if (!v.flags().flags().isFinal()) syntaxError("Type definition parameters must be final.", v.position());
                }
                TypeDecl cd = nf.TypeDecl(pos(), f, Identifier, TypeParametersopt, FormalParametersopt, WhereClauseopt, Type);
                cd = (TypeDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 39:  Properties ::= ( PropertyList )
            //
            case 39: {
               //#line 1312 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1310 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 1312 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
   setResult(PropertyList);
                 break;
            } 
            //
            // Rule 40:  PropertyList ::= Property
            //
            case 40: {
               //#line 1317 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1315 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 1317 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 41:  PropertyList ::= PropertyList , Property
            //
            case 41: {
               //#line 1324 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1322 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 1322 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 1324 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 42:  Property ::= Annotationsopt Identifier ResultType
            //
            case 42: {
               //#line 1331 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1329 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 1329 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1329 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 1331 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List annotations = extractAnnotations(Annotationsopt);
                PropertyDecl cd = nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), ResultType, Identifier);
                cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 43:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 43: {
               //#line 1340 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1338 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1338 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1338 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1338 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1338 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1338 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1338 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1338 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1338 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1340 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
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
            // Rule 44:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 44: {
               //#line 1373 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 1371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 1371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 1371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(13);
                //#line 1371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(14);
                //#line 1371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(15);
                //#line 1373 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
       Name opName = X10Binary_c.binaryMethodName(BinOp);
       if (opName == null) {
           syntaxError("Cannot override binary operator '"+BinOp+"'.", pos());
           opName = Name.make("invalid operator");
       }
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(7)), opName),
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
            // Rule 45:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 45: {
               //#line 1397 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1395 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1395 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1395 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1395 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 1395 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 1395 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 1395 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1395 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1395 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1397 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
       Name opName = X10Unary_c.unaryMethodName(PrefixOp);
       if (opName == null) {
           syntaxError("Cannot override unary operator '"+PrefixOp+"'.", pos());
           opName = Name.make("invalid operator");
       }
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(4)), opName),
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
            // Rule 46:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 46: {
               //#line 1421 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 1419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 1419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1421 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
       Name opName = X10Binary_c.binaryMethodName(BinOp);
       if (opName == null) {
           syntaxError("Cannot override binary operator '"+BinOp+"'.", pos());
           opName = Name.make("invalid operator");
       }
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(5)), opName),
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
            // Rule 47:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 47: {
               //#line 1446 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1444 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1444 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1444 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1444 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1444 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1444 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1444 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1444 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1444 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1446 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
       Name opName = X10Binary_c.invBinaryMethodName(BinOp);
       if (opName == null) {
           syntaxError("Cannot override binary operator '"+BinOp+"'.", pos());
           opName = Name.make("invalid operator");
       }
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(7)), opName),
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
            // Rule 48:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 48: {
               //#line 1471 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1469 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1469 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1469 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1469 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1469 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1469 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1469 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1469 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1471 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
       Name opName = X10Unary_c.unaryMethodName(PrefixOp);
       if (opName == null) {
           syntaxError("Cannot override unary operator '"+PrefixOp+"'.", pos());
           opName = Name.make("invalid operator");
       }
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(getRhsFirstTokenIndex(4)), opName),
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
            // Rule 49:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 49: {
               //#line 1495 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1495 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(), ClosureCall.APPLY),
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
            // Rule 50:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 50: {
               //#line 1514 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1512 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1512 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1512 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1512 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1512 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1512 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1512 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(12);
                //#line 1512 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(13);
                //#line 1512 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 1514 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
       MethodDecl md = nf.X10MethodDecl(pos(),
          extractFlags(MethodModifiersopt),
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
          nf.Id(pos(), SettableAssign.SET),
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
            // Rule 51:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Throwsopt Offersopt MethodBody
            //
            case 51: {
               //#line 1533 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1533 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
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
            // Rule 52:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 52: {
               //#line 1552 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1550 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1550 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1550 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1550 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1550 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1550 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1550 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1550 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1552 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
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
            // Rule 53:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 53: {
               //#line 1571 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1569 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1569 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1569 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1569 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1569 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(8);
                //#line 1569 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(9);
                //#line 1569 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(10);
                //#line 1569 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1571 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
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
            // Rule 54:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt MethodBody
            //
            case 54: {
               //#line 1591 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1589 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1589 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1589 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1589 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(4);
                //#line 1589 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1589 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(6);
                //#line 1589 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(7);
                //#line 1591 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
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
            // Rule 55:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier WhereClauseopt HasResultTypeopt MethodBody
            //
            case 55: {
               //#line 1608 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1606 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1606 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1606 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(3);
                //#line 1606 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 1606 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(5);
                //#line 1608 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
       MethodModifiersopt = checkMethodModifiers(MethodModifiersopt);
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
            // Rule 56:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 56: {
               //#line 1626 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1624 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1624 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1626 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 57:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 57: {
               //#line 1631 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1629 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1629 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1631 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 58:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 58: {
               //#line 1636 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1634 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1634 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1634 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1636 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 59:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 59: {
               //#line 1641 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1639 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1639 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1639 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1641 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 60:  NormalInterfaceDeclaration ::= Modifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 60: {
               //#line 1647 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1645 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 1645 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1645 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1645 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1645 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1645 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 1645 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1647 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
      Modifiersopt = checkInterfaceModifiers(Modifiersopt);
      checkTypeName(Identifier);
      List TypeParametersopt = TypeParamsWithVarianceopt;
      List/*<PropertyDecl>*/ props = Propertiesopt;
      DepParameterExpr ci = WhereClauseopt;
      FlagsNode fn = extractFlags(Modifiersopt, Flags.INTERFACE);
      ClassDecl cd = nf.X10ClassDecl(pos(),
                   fn,
                   Identifier,
                   TypeParametersopt,
                   props,
                   ci,
                   null,
                   ExtendsInterfacesopt,
                   InterfaceBody);
      cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(Modifiersopt));
      setResult(cd);
                      break;
            }
    
            //
            // Rule 61:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 61: {
               //#line 1669 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1667 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1667 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1667 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1667 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1669 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 62:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 62: {
               //#line 1676 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1674 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1674 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1674 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1674 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1674 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1676 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 63:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 63: {
               //#line 1684 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1682 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1682 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1682 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1682 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1682 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1684 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 64:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 64: {
               //#line 1693 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1691 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1691 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1693 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 67:  FunctionType ::= TypeParametersopt ( FormalParameterListopt ) WhereClauseopt Throwsopt Offersopt => Type
            //
            case 67: {
               //#line 1703 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1701 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(1);
                //#line 1701 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1701 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1701 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1701 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(7);
                //#line 1701 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(9);
                //#line 1703 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeParametersopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt, Offersopt));
                      break;
            }
    
            //
            // Rule 69:  AnnotatedType ::= Type Annotations
            //
            case 69: {
               //#line 1716 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1714 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1714 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1716 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn.position(pos()));
                      break;
            }
    
            //
            // Rule 72:  ConstrainedType ::= ( Type )
            //
            case 72: {
               //#line 1726 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1724 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1726 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 74:  SimpleNamedType ::= TypeName
            //
            case 74: {
               //#line 1740 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1738 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1740 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(TypeName.toType());
                      break;
            }
    
            //
            // Rule 75:  SimpleNamedType ::= Primary . Identifier
            //
            case 75: {
               //#line 1745 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1743 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1743 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1745 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(nf.AmbTypeNode(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 76:  SimpleNamedType ::= DepNamedType . Identifier
            //
            case 76: {
               //#line 1750 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1748 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode DepNamedType = (TypeNode) getRhsSym(1);
                //#line 1748 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1750 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(nf.AmbTypeNode(pos(), DepNamedType, Identifier));
                      break;
            }
    
            //
            // Rule 77:  DepNamedType ::= SimpleNamedType DepParameters
            //
            case 77: {
               //#line 1756 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1754 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1754 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(2);
                //#line 1756 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList(new LinkedList(), TypeNode.class, false),
                                              new TypedList(new LinkedList(), Expr.class, false),
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 78:  DepNamedType ::= SimpleNamedType Arguments
            //
            case 78: {
               //#line 1765 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1763 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1763 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Arguments = (List) getRhsSym(2);
                //#line 1765 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList(new LinkedList(), TypeNode.class, false),
                                              Arguments,
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 79:  DepNamedType ::= SimpleNamedType Arguments DepParameters
            //
            case 79: {
               //#line 1774 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1772 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1772 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Arguments = (List) getRhsSym(2);
                //#line 1772 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(3);
                //#line 1774 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList(new LinkedList(), TypeNode.class, false),
                                              Arguments,
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 80:  DepNamedType ::= SimpleNamedType TypeArguments
            //
            case 80: {
               //#line 1783 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1781 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1781 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArguments = (List) getRhsSym(2);
                //#line 1783 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              new TypedList(new LinkedList(), Expr.class, false),
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 81:  DepNamedType ::= SimpleNamedType TypeArguments DepParameters
            //
            case 81: {
               //#line 1792 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1790 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1790 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArguments = (List) getRhsSym(2);
                //#line 1790 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(3);
                //#line 1792 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              new TypedList(new LinkedList(), Expr.class, false),
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 82:  DepNamedType ::= SimpleNamedType TypeArguments Arguments
            //
            case 82: {
               //#line 1801 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1799 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1799 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArguments = (List) getRhsSym(2);
                //#line 1799 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Arguments = (List) getRhsSym(3);
                //#line 1801 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              Arguments,
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 83:  DepNamedType ::= SimpleNamedType TypeArguments Arguments DepParameters
            //
            case 83: {
               //#line 1810 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1808 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1808 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArguments = (List) getRhsSym(2);
                //#line 1808 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Arguments = (List) getRhsSym(3);
                //#line 1808 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(4);
                //#line 1810 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              Arguments,
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 86:  DepParameters ::= { ExistentialListopt Conjunctionopt }
            //
            case 86: {
               //#line 1823 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1821 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1821 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Conjunctionopt = (List) getRhsSym(3);
                //#line 1823 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, (List) Conjunctionopt));
                      break;
            }
    
            //
            // Rule 87:  DepParameters ::= ! PlaceType
            //
            case 87: {
               //#line 1828 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1826 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1828 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 88:  DepParameters ::= !
            //
            case 88: {
               //#line 1834 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1834 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 89:  DepParameters ::= ! PlaceType { ExistentialListopt Conjunction }
            //
            case 89: {
               //#line 1840 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1838 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1838 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(4);
                //#line 1838 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(5);
                //#line 1840 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 90:  DepParameters ::= ! { ExistentialListopt Conjunction }
            //
            case 90: {
               //#line 1846 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1844 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(3);
                //#line 1844 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(4);
                //#line 1846 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 91:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 91: {
               //#line 1854 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1852 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(2);
                //#line 1854 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 92:  TypeParameters ::= [ TypeParameterList ]
            //
            case 92: {
               //#line 1860 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1858 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1860 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 93:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 93: {
               //#line 1866 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1864 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1866 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 94:  Conjunction ::= Expression
            //
            case 94: {
               //#line 1872 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1870 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1872 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 95:  Conjunction ::= Conjunction , Expression
            //
            case 95: {
               //#line 1879 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1877 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1877 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1879 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 96:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 96: {
               //#line 1885 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1883 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1883 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1885 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 97:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 97: {
               //#line 1890 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1888 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1888 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1890 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 98:  WhereClause ::= DepParameters
            //
            case 98: {
               //#line 1896 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1894 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1896 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 99:  Conjunctionopt ::= $Empty
            //
            case 99: {
               //#line 1902 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1902 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                setResult(l);
                      break;
            }
      
            //
            // Rule 100:  Conjunctionopt ::= Conjunction
            //
            case 100: {
               //#line 1908 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1906 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1908 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(Conjunction);
                      break;
            }
    
            //
            // Rule 101:  ExistentialListopt ::= $Empty
            //
            case 101: {
               //#line 1914 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1914 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 102:  ExistentialListopt ::= ExistentialList ;
            //
            case 102: {
               //#line 1919 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1917 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1919 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 103:  ExistentialList ::= FormalParameter
            //
            case 103: {
               //#line 1925 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1923 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1925 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 104:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 104: {
               //#line 1932 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1930 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1930 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1932 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 107:  NormalClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 107: {
               //#line 1943 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 1941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1943 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
      Modifiersopt = checkClassModifiers(Modifiersopt);
      checkTypeName(Identifier);
                List TypeParametersopt = TypeParamsWithVarianceopt;
      List/*<PropertyDecl>*/ props = Propertiesopt;
      DepParameterExpr ci = WhereClauseopt;
      FlagsNode f = extractFlags(Modifiersopt);
      List annotations = extractAnnotations(Modifiersopt);
      ClassDecl cd = nf.X10ClassDecl(pos(),
              f, Identifier, TypeParametersopt, props, ci, Superopt, Interfacesopt, ClassBody);
      cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(annotations);
      setResult(cd);
                      break;
            }
    
            //
            // Rule 108:  StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 108: {
               //#line 1960 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1958 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 1958 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1958 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1958 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1958 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1958 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1958 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1960 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
    Modifiersopt = checkClassModifiers(Modifiersopt);
    checkTypeName(Identifier);
                List TypeParametersopt = TypeParamsWithVarianceopt;
    List props = Propertiesopt;
    DepParameterExpr ci = WhereClauseopt;
    ClassDecl cd = (nf.X10ClassDecl(pos(getLeftSpan(), getRightSpan()),
    extractFlags(Modifiersopt, X10Flags.STRUCT), Identifier,  TypeParametersopt,
    props, ci, null, Interfacesopt, ClassBody));
    cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(Modifiersopt));
    setResult(cd);
                      break;
            }
    
            //
            // Rule 109:  ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt ConstructorBody
            //
            case 109: {
               //#line 1975 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 1973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(10);
                //#line 1975 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
     Modifiersopt = checkConstructorModifiers(Modifiersopt);
     ConstructorDecl cd = nf.X10ConstructorDecl(pos(),
                                             extractFlags(Modifiersopt),
                                             nf.Id(pos(getRhsFirstTokenIndex(3)), "this"),
                                             HasResultTypeopt,
                                             TypeParametersopt,
                                             FormalParameters,
                                             WhereClauseopt,
                                             Throwsopt,
                                             Offersopt,
                                             ConstructorBody);
     cd = (ConstructorDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(Modifiersopt));
     setResult(cd);
                     break;
            }
   
            //
            // Rule 110:  Super ::= extends ClassType
            //
            case 110: {
               //#line 1993 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1991 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1993 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 111:  FieldKeyword ::= val
            //
            case 111: {
               //#line 1999 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1999 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 112:  FieldKeyword ::= var
            //
            case 112: {
               //#line 2004 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2004 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 113:  VarKeyword ::= val
            //
            case 113: {
               //#line 2012 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2012 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 114:  VarKeyword ::= var
            //
            case 114: {
               //#line 2017 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2017 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 115:  FieldDeclaration ::= Modifiersopt FieldKeyword FieldDeclarators ;
            //
            case 115: {
               //#line 2024 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2022 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 2022 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 2022 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 2024 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Modifiersopt = checkFieldModifiers(Modifiersopt);
                FlagsNode fn = extractFlags(Modifiersopt, FieldKeyword);
    
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
                        fd = (FieldDecl) ((X10Ext) fd.ext()).annotations(extractAnnotations(Modifiersopt));
                        fd = (FieldDecl) ((X10Ext) fd.ext()).setComment(comment(getRhsFirstTokenIndex(1)));
                        l.add(fd);
                    }
                setResult(l);
                      break;
            }
    
            //
            // Rule 116:  FieldDeclaration ::= Modifiersopt FieldDeclarators ;
            //
            case 116: {
               //#line 2050 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2048 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 2048 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 2050 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Modifiersopt = checkFieldModifiers(Modifiersopt);
                List FieldKeyword = Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL));
                FlagsNode fn = extractFlags(Modifiersopt, FieldKeyword);
    
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
                        fd = (FieldDecl) ((X10Ext) fd.ext()).annotations(extractAnnotations(Modifiersopt));
                        fd = (FieldDecl) ((X10Ext) fd.ext()).setComment(comment(getRhsFirstTokenIndex(1)));
                        l.add(fd);
                    }
                setResult(l);
                      break;
            }
    
            //
            // Rule 119:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 119: {
               //#line 2083 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2081 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2081 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt NonExpressionStatement = (Stmt) getRhsSym(2);
                //#line 2083 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                if (NonExpressionStatement.ext() instanceof X10Ext && Annotationsopt instanceof List) {
                    NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations((List) Annotationsopt);
                }
                setResult(NonExpressionStatement.position(pos()));
                      break;
            }
    
            //
            // Rule 146:  OfferStatement ::= offer Expression ;
            //
            case 146: {
               //#line 2119 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2117 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2119 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Offer(pos(), Expression));
                      break;
            }
    
            //
            // Rule 147:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 147: {
               //#line 2125 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2123 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2123 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2125 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 148:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 148: {
               //#line 2131 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2129 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2129 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 2129 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 2131 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 149:  EmptyStatement ::= ;
            //
            case 149: {
               //#line 2137 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2137 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 150:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 150: {
               //#line 2143 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2141 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2141 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 2143 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 156:  ExpressionStatement ::= StatementExpression ;
            //
            case 156: {
               //#line 2155 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2153 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 2155 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 164:  AssertStatement ::= assert Expression ;
            //
            case 164: {
               //#line 2169 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2167 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2169 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 165:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 165: {
               //#line 2174 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2172 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 2172 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 2174 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 166:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 166: {
               //#line 2180 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2178 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2178 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 2180 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 167:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 167: {
               //#line 2186 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2184 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 2184 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 2186 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 169:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 169: {
               //#line 2194 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2192 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 2192 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 2194 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 170:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 170: {
               //#line 2201 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2199 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 2199 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 2201 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 171:  SwitchLabels ::= SwitchLabel
            //
            case 171: {
               //#line 2210 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2208 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 2210 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 172:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 172: {
               //#line 2217 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2215 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 2215 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 2217 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 173:  SwitchLabel ::= case ConstantExpression :
            //
            case 173: {
               //#line 2224 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2222 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 2224 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 174:  SwitchLabel ::= default :
            //
            case 174: {
               //#line 2229 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2229 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 175:  WhileStatement ::= while ( Expression ) Statement
            //
            case 175: {
               //#line 2235 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2233 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2233 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2235 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 176:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 176: {
               //#line 2241 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2239 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2239 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2241 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 179:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 179: {
               //#line 2250 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2248 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 2248 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 2248 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 2248 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 2250 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 181:  ForInit ::= LocalVariableDeclaration
            //
            case 181: {
               //#line 2257 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2255 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 2257 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 183:  StatementExpressionList ::= StatementExpression
            //
            case 183: {
               //#line 2267 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2265 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 2267 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 184:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 184: {
               //#line 2274 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2272 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 2272 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 2274 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 185:  BreakStatement ::= break Identifieropt ;
            //
            case 185: {
               //#line 2280 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2278 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 2280 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 186:  ContinueStatement ::= continue Identifieropt ;
            //
            case 186: {
               //#line 2286 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2284 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 2286 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 187:  ReturnStatement ::= return Expressionopt ;
            //
            case 187: {
               //#line 2292 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2290 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 2292 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 188:  ThrowStatement ::= throw Expression ;
            //
            case 188: {
               //#line 2298 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2296 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2298 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 189:  TryStatement ::= try Block Catches
            //
            case 189: {
               //#line 2304 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2302 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2302 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 2304 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 190:  TryStatement ::= try Block Catchesopt Finally
            //
            case 190: {
               //#line 2309 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2307 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2307 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 2307 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 2309 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 191:  Catches ::= CatchClause
            //
            case 191: {
               //#line 2315 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2313 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 2315 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 192:  Catches ::= Catches CatchClause
            //
            case 192: {
               //#line 2322 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2320 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 2320 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 2322 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 193:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 193: {
               //#line 2329 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2327 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2327 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2329 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 194:  Finally ::= finally Block
            //
            case 194: {
               //#line 2335 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2333 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2335 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 195:  ClockedClause ::= clocked ( ClockList )
            //
            case 195: {
               //#line 2341 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2339 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 2341 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 196:  AsyncStatement ::= async ClockedClauseopt Statement
            //
            case 196: {
               //#line 2348 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2346 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(2);
                //#line 2346 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2348 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 197:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 197: {
               //#line 2355 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2353 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2353 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2355 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 198:  AtomicStatement ::= atomic Statement
            //
            case 198: {
               //#line 2361 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2359 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2361 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 199:  WhenStatement ::= when ( Expression ) Statement
            //
            case 199: {
               //#line 2368 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2366 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2366 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2368 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 200:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 200: {
               //#line 2373 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 2371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 2371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 2371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 2373 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 201:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 201: {
               //#line 2380 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 2378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2380 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 202:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 202: {
               //#line 2396 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2394 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2394 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2394 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 2394 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2396 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 203:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 203: {
               //#line 2412 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2410 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2410 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2410 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 2412 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 204:  FinishStatement ::= finish Statement
            //
            case 204: {
               //#line 2426 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2424 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2426 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 205:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 205: {
               //#line 2432 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2430 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 2432 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 207:  NextStatement ::= next ;
            //
            case 207: {
               //#line 2440 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2440 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 208:  ClockList ::= Clock
            //
            case 208: {
               //#line 2446 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2444 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 2446 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 209:  ClockList ::= ClockList , Clock
            //
            case 209: {
               //#line 2453 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2451 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 2451 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 2453 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 210:  Clock ::= Expression
            //
            case 210: {
               //#line 2461 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2459 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2461 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 212:  CastExpression ::= ExpressionName
            //
            case 212: {
               //#line 2474 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2472 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 2474 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 213:  CastExpression ::= CastExpression as Type
            //
            case 213: {
               //#line 2479 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2477 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2477 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2479 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 214:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 214: {
               //#line 2486 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2484 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2486 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 215:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 215: {
               //#line 2493 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2491 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(1);
                //#line 2491 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2493 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 216:  TypeParameterList ::= TypeParameter
            //
            case 216: {
               //#line 2500 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2498 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2500 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 217:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 217: {
               //#line 2507 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2505 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 2505 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2507 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 218:  TypeParamWithVariance ::= Identifier
            //
            case 218: {
               //#line 2514 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2512 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2514 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 219:  TypeParamWithVariance ::= + Identifier
            //
            case 219: {
               //#line 2519 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2517 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2519 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 220:  TypeParamWithVariance ::= - Identifier
            //
            case 220: {
               //#line 2524 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2522 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2524 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 221:  TypeParameter ::= Identifier
            //
            case 221: {
               //#line 2530 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2528 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2530 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 222:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 222: {
               //#line 2555 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2553 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2553 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2555 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 223:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt => ClosureBody
            //
            case 223: {
               //#line 2561 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(1);
                //#line 2559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(4);
                //#line 2559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(5);
                //#line 2559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 2561 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 224:  LastExpression ::= Expression
            //
            case 224: {
               //#line 2568 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2566 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2568 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 225:  ClosureBody ::= ConditionalExpression
            //
            case 225: {
               //#line 2574 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2572 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 2574 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), ConditionalExpression, true)));
                      break;
            }
    
            //
            // Rule 226:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 226: {
               //#line 2579 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2577 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2577 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2577 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2579 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 227:  ClosureBody ::= Annotationsopt Block
            //
            case 227: {
               //#line 2589 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2587 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2587 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2589 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b.position(pos()));
                      break;
            }
    
            //
            // Rule 228:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 228: {
               //#line 2598 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2596 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2596 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2598 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 229:  AsyncExpression ::= async ClosureBody
            //
            case 229: {
               //#line 2604 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2602 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2604 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 230:  FinishExpression ::= finish ( Expression ) Block
            //
            case 230: {
               //#line 2610 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2608 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2608 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2610 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FinishExpr(pos(), Expression, Block));
                      break;
            }
    
            //
            // Rule 231:  WhereClauseopt ::= $Empty
            //
            case 231:
                setResult(null);
                break;

            //
            // Rule 233:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 233:
                setResult(null);
                break;

            //
            // Rule 235:  ClockedClauseopt ::= $Empty
            //
            case 235: {
               //#line 2658 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2658 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 237:  identifier ::= IDENTIFIER$ident
            //
            case 237: {
               //#line 2669 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2667 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2669 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 238:  TypeName ::= Identifier
            //
            case 238: {
               //#line 2676 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2674 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2676 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 239:  TypeName ::= TypeName . Identifier
            //
            case 239: {
               //#line 2681 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2679 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2679 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2681 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 241:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 241: {
               //#line 2693 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2691 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2693 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 242:  TypeArgumentList ::= Type
            //
            case 242: {
               //#line 2700 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2698 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2700 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 243:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 243: {
               //#line 2707 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2705 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2705 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2707 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 244:  PackageName ::= Identifier
            //
            case 244: {
               //#line 2717 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2715 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2717 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 245:  PackageName ::= PackageName . Identifier
            //
            case 245: {
               //#line 2722 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2720 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2720 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2722 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 246:  ExpressionName ::= Identifier
            //
            case 246: {
               //#line 2738 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2736 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2738 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 247:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 247: {
               //#line 2743 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2741 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2741 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2743 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 248:  MethodName ::= Identifier
            //
            case 248: {
               //#line 2753 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2751 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2753 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 249:  MethodName ::= AmbiguousName . Identifier
            //
            case 249: {
               //#line 2758 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2756 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2756 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2758 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 250:  PackageOrTypeName ::= Identifier
            //
            case 250: {
               //#line 2768 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2766 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2768 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 251:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 251: {
               //#line 2773 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2771 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2771 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2773 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 252:  AmbiguousName ::= Identifier
            //
            case 252: {
               //#line 2783 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2781 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2783 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 253:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 253: {
               //#line 2788 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2786 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2786 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2788 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 254:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 254: {
               //#line 2800 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2798 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2798 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2798 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2800 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 255:  ImportDeclarations ::= ImportDeclaration
            //
            case 255: {
               //#line 2816 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2814 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2816 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 256:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 256: {
               //#line 2823 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2821 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2821 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2823 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 257:  TypeDeclarations ::= TypeDeclaration
            //
            case 257: {
               //#line 2831 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2829 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2831 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 258:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 258: {
               //#line 2839 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2837 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2837 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2839 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 259:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 259: {
               //#line 2847 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2845 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2845 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2847 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn.position(pos()));
                      break;
            }
    
            //
            // Rule 262:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 262: {
               //#line 2861 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2859 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2861 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 263:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 263: {
               //#line 2867 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2865 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2867 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 267:  TypeDeclaration ::= ;
            //
            case 267: {
               //#line 2882 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2882 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 268:  Interfaces ::= implements InterfaceTypeList
            //
            case 268: {
               //#line 2999 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2997 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 2999 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 269:  InterfaceTypeList ::= Type
            //
            case 269: {
               //#line 3005 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3003 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 3005 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 270:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 270: {
               //#line 3012 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3010 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 3010 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3012 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 271:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 271: {
               //#line 3022 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3020 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 3022 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 273:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 273: {
               //#line 3029 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3027 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 3027 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 3029 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 275:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 275: {
               //#line 3051 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3049 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 3051 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 277:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 277: {
               //#line 3060 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3058 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3060 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 278:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 278: {
               //#line 3067 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3065 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3067 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 279:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 279: {
               //#line 3074 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3072 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3074 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 280:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 280: {
               //#line 3081 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3079 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3081 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 281:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 281: {
               //#line 3088 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3086 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3088 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 282:  ClassMemberDeclaration ::= ;
            //
            case 282: {
               //#line 3095 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3095 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 283:  FormalDeclarators ::= FormalDeclarator
            //
            case 283: {
               //#line 3102 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3100 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 3102 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 284:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 284: {
               //#line 3109 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3107 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 3107 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 3109 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 285:  FieldDeclarators ::= FieldDeclarator
            //
            case 285: {
               //#line 3116 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3114 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 3116 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 286:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 286: {
               //#line 3123 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3121 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 3121 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 3123 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 287:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 287: {
               //#line 3131 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3129 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 3131 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 288:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 288: {
               //#line 3138 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3136 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 3136 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 3138 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 289:  VariableDeclarators ::= VariableDeclarator
            //
            case 289: {
               //#line 3145 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3143 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 3145 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 290:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 290: {
               //#line 3152 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3150 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 3150 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 3152 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 292:  ResultType ::= : Type
            //
            case 292: {
               //#line 3208 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3206 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3208 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 293:  HasResultType ::= : Type
            //
            case 293: {
               //#line 3213 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3211 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3213 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 294:  HasResultType ::= <: Type
            //
            case 294: {
               //#line 3218 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3216 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3218 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.HasType(Type));
                      break;
            }
    
            //
            // Rule 295:  FormalParameterList ::= FormalParameter
            //
            case 295: {
               //#line 3233 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3231 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 3233 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 296:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 296: {
               //#line 3240 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3238 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 3238 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 3240 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 297:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 297: {
               //#line 3246 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3244 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3244 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3246 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 298:  LoopIndexDeclarator ::= ( IdentifierList ) HasResultTypeopt
            //
            case 298: {
               //#line 3251 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3249 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3249 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3251 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 299:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt
            //
            case 299: {
               //#line 3256 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3254 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3254 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3254 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3256 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 300:  LoopIndex ::= Modifiersopt LoopIndexDeclarator
            //
            case 300: {
               //#line 3262 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3260 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 3260 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 3262 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            Modifiersopt = checkVariableModifiers(Modifiersopt);
            Formal f;
            FlagsNode fn = extractFlags(Modifiersopt, Flags.FINAL);
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
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(Modifiersopt));
            setResult(f);
                      break;
            }
    
            //
            // Rule 301:  LoopIndex ::= Modifiersopt VarKeyword LoopIndexDeclarator
            //
            case 301: {
               //#line 3286 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3284 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 3284 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3284 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 3286 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            Modifiersopt = checkVariableModifiers(Modifiersopt);
            Formal f;
            FlagsNode fn = extractFlags(Modifiersopt, VarKeyword);
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
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(Modifiersopt));
            setResult(f);
                      break;
            }
    
            //
            // Rule 302:  FormalParameter ::= Modifiersopt FormalDeclarator
            //
            case 302: {
               //#line 3311 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3309 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 3309 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 3311 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            Modifiersopt = checkVariableModifiers(Modifiersopt);
            Formal f;
            FlagsNode fn = extractFlags(Modifiersopt, Flags.FINAL);
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
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(Modifiersopt));
            setResult(f);
                      break;
            }
    
            //
            // Rule 303:  FormalParameter ::= Modifiersopt VarKeyword FormalDeclarator
            //
            case 303: {
               //#line 3336 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3334 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 3334 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3334 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 3336 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            Modifiersopt = checkVariableModifiers(Modifiersopt);
            Formal f;
            FlagsNode fn = extractFlags(Modifiersopt, VarKeyword);
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
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(Modifiersopt));
            setResult(f);
                      break;
            }
    
            //
            // Rule 304:  FormalParameter ::= Type
            //
            case 304: {
               //#line 3361 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3359 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 3361 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh("id$")), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 305:  Throws ::= throws ExceptionTypeList
            //
            case 305: {
               //#line 3502 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3500 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 3502 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 306:  Offers ::= offers Type
            //
            case 306: {
               //#line 3507 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3505 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3507 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 307:  ExceptionTypeList ::= ExceptionType
            //
            case 307: {
               //#line 3513 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3511 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3513 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 308:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 308: {
               //#line 3520 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3518 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3518 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3520 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 310:  MethodBody ::= = LastExpression ;
            //
            case 310: {
               //#line 3528 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3526 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3528 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 311:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 311: {
               //#line 3533 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(4);
                //#line 3531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3533 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult((Block) ((X10Ext) nf.Block(pos(),l).ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 312:  MethodBody ::= = Annotationsopt Block
            //
            case 312: {
               //#line 3541 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3539 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3539 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3541 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 313:  MethodBody ::= Annotationsopt Block
            //
            case 313: {
               //#line 3546 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3544 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 3544 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3546 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 314:  MethodBody ::= ;
            //
            case 314:
                setResult(null);
                break;

            //
            // Rule 315:  ConstructorBody ::= = ConstructorBlock
            //
            case 315: {
               //#line 3617 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3615 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3617 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 316:  ConstructorBody ::= ConstructorBlock
            //
            case 316: {
               //#line 3622 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3620 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3622 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 317:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 317: {
               //#line 3627 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3625 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3627 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 318:  ConstructorBody ::= = AssignPropertyCall
            //
            case 318: {
               //#line 3635 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3633 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3635 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 319:  ConstructorBody ::= ;
            //
            case 319:
                setResult(null);
                break;

            //
            // Rule 320:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 320: {
               //#line 3646 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3644 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3644 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3646 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 321:  Arguments ::= ( ArgumentListopt )
            //
            case 321: {
               //#line 3659 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3657 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3659 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 323:  ExtendsInterfaces ::= extends Type
            //
            case 323: {
               //#line 3716 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3714 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3716 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 324:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 324: {
               //#line 3723 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3721 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3721 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3723 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 325:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 325: {
               //#line 3732 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3730 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3732 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 327:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 327: {
               //#line 3739 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3737 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3737 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3739 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 328:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 328: {
               //#line 3746 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3744 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3746 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 329:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 329: {
               //#line 3753 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3751 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3753 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 330:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 330: {
               //#line 3760 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3758 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3760 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 331:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 331: {
               //#line 3767 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3765 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3767 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 332:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 332: {
               //#line 3774 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3772 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3774 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 333:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 333: {
               //#line 3781 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3779 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3781 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 334:  InterfaceMemberDeclaration ::= ;
            //
            case 334: {
               //#line 3788 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3788 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 335:  Annotations ::= Annotation
            //
            case 335: {
               //#line 3794 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3792 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3794 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 336:  Annotations ::= Annotations Annotation
            //
            case 336: {
               //#line 3801 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3799 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3799 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3801 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 337:  Annotation ::= @ NamedType
            //
            case 337: {
               //#line 3807 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3805 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3807 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 338:  Identifier ::= identifier
            //
            case 338: {
               //#line 3822 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3820 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3822 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 339:  Block ::= { BlockStatementsopt }
            //
            case 339: {
               //#line 3858 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3856 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3858 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 340:  BlockStatements ::= BlockStatement
            //
            case 340: {
               //#line 3864 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3862 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3864 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 341:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 341: {
               //#line 3871 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3869 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3869 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3871 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 343:  BlockStatement ::= ClassDeclaration
            //
            case 343: {
               //#line 3879 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3877 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3879 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 344:  BlockStatement ::= TypeDefDeclaration
            //
            case 344: {
               //#line 3886 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3884 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3886 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 345:  BlockStatement ::= Statement
            //
            case 345: {
               //#line 3893 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3891 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3893 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 346:  IdentifierList ::= Identifier
            //
            case 346: {
               //#line 3901 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3899 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3901 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 347:  IdentifierList ::= IdentifierList , Identifier
            //
            case 347: {
               //#line 3908 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3906 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3906 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3908 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 348:  FormalDeclarator ::= Identifier ResultType
            //
            case 348: {
               //#line 3914 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3912 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3912 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3914 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 349:  FormalDeclarator ::= ( IdentifierList ) ResultType
            //
            case 349: {
               //#line 3919 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3917 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3917 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3919 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 350:  FormalDeclarator ::= Identifier ( IdentifierList ) ResultType
            //
            case 350: {
               //#line 3924 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3922 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3922 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3922 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3924 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 351:  FieldDeclarator ::= Identifier HasResultType
            //
            case 351: {
               //#line 3930 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3928 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3928 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3930 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultType, null });
                      break;
            }
    
            //
            // Rule 352:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 352: {
               //#line 3935 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3933 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3933 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3933 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3935 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 353:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 353: {
               //#line 3941 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3939 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3939 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3939 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3941 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 354:  VariableDeclarator ::= ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 354: {
               //#line 3946 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3944 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3944 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3944 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3946 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 355:  VariableDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 355: {
               //#line 3951 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3949 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3949 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3949 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3949 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3951 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 356:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 356: {
               //#line 3957 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3955 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3955 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3955 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3957 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 357:  VariableDeclaratorWithType ::= ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 357: {
               //#line 3962 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3960 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3960 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(4);
                //#line 3960 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 3962 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 358:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 358: {
               //#line 3967 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3965 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3965 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3965 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(5);
                //#line 3965 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 3967 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 360:  LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
            //
            case 360: {
               //#line 3975 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 3973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 3975 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Modifiersopt = checkVariableModifiers(Modifiersopt);
                FlagsNode fn = extractFlags(Modifiersopt, VarKeyword);
    
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
                        ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(Modifiersopt));
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
            // Rule 361:  LocalVariableDeclaration ::= Modifiersopt VariableDeclaratorsWithType
            //
            case 361: {
               //#line 4009 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4007 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 4007 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 4009 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Modifiersopt = checkVariableModifiers(Modifiersopt);
                FlagsNode fn = extractFlags(Modifiersopt, Flags.FINAL);
    
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
                        ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(Modifiersopt));
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
            // Rule 362:  LocalVariableDeclaration ::= Modifiersopt VarKeyword FormalDeclarators
            //
            case 362: {
               //#line 4044 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4042 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 4042 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 4042 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 4044 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Modifiersopt = checkVariableModifiers(Modifiersopt);
                FlagsNode fn = extractFlags(Modifiersopt, VarKeyword);
    
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
                        ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(Modifiersopt));
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
            // Rule 363:  Primary ::= here
            //
            case 363: {
               //#line 4086 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4086 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 364:  Primary ::= [ ArgumentListopt ]
            //
            case 364: {
               //#line 4092 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4090 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 4092 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 366:  Primary ::= self
            //
            case 366: {
               //#line 4100 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4100 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 367:  Primary ::= this
            //
            case 367: {
               //#line 4105 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4105 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 368:  Primary ::= ClassName . this
            //
            case 368: {
               //#line 4110 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4108 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4110 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 369:  Primary ::= ( Expression )
            //
            case 369: {
               //#line 4115 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4113 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 4115 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 375:  OperatorFunction ::= TypeName . +
            //
            case 375: {
               //#line 4126 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4124 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4126 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 376:  OperatorFunction ::= TypeName . -
            //
            case 376: {
               //#line 4137 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4135 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4137 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 377:  OperatorFunction ::= TypeName . *
            //
            case 377: {
               //#line 4148 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4146 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4148 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 378:  OperatorFunction ::= TypeName . /
            //
            case 378: {
               //#line 4159 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4157 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4159 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 379:  OperatorFunction ::= TypeName . %
            //
            case 379: {
               //#line 4170 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4168 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4170 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 380:  OperatorFunction ::= TypeName . &
            //
            case 380: {
               //#line 4181 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4179 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4181 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 381:  OperatorFunction ::= TypeName . |
            //
            case 381: {
               //#line 4192 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4190 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4192 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 382:  OperatorFunction ::= TypeName . ^
            //
            case 382: {
               //#line 4203 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4201 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4203 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 383:  OperatorFunction ::= TypeName . <<
            //
            case 383: {
               //#line 4214 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4212 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4214 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 384:  OperatorFunction ::= TypeName . >>
            //
            case 384: {
               //#line 4225 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4223 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4225 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 385:  OperatorFunction ::= TypeName . >>>
            //
            case 385: {
               //#line 4236 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4234 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4236 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 386:  OperatorFunction ::= TypeName . <
            //
            case 386: {
               //#line 4247 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4245 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4247 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 387:  OperatorFunction ::= TypeName . <=
            //
            case 387: {
               //#line 4258 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4256 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4258 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 388:  OperatorFunction ::= TypeName . >=
            //
            case 388: {
               //#line 4269 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4267 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4269 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 389:  OperatorFunction ::= TypeName . >
            //
            case 389: {
               //#line 4280 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4278 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4280 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 390:  OperatorFunction ::= TypeName . ==
            //
            case 390: {
               //#line 4291 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4289 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4291 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 391:  OperatorFunction ::= TypeName . !=
            //
            case 391: {
               //#line 4302 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4300 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4302 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 392:  Literal ::= IntegerLiteral$lit
            //
            case 392: {
               //#line 4315 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4313 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4315 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 393:  Literal ::= LongLiteral$lit
            //
            case 393: {
               //#line 4321 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4319 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4321 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 394:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 394: {
               //#line 4327 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4325 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4327 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 395:  Literal ::= UnsignedLongLiteral$lit
            //
            case 395: {
               //#line 4333 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4331 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4333 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 396:  Literal ::= FloatingPointLiteral$lit
            //
            case 396: {
               //#line 4339 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4337 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4339 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 397:  Literal ::= DoubleLiteral$lit
            //
            case 397: {
               //#line 4345 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4343 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4345 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 398:  Literal ::= BooleanLiteral
            //
            case 398: {
               //#line 4351 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4349 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 4351 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 399:  Literal ::= CharacterLiteral$lit
            //
            case 399: {
               //#line 4356 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4354 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4356 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 400:  Literal ::= StringLiteral$str
            //
            case 400: {
               //#line 4362 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4360 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 4362 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 401:  Literal ::= null
            //
            case 401: {
               //#line 4368 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4368 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 402:  BooleanLiteral ::= true$trueLiteral
            //
            case 402: {
               //#line 4374 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4372 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 4374 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 403:  BooleanLiteral ::= false$falseLiteral
            //
            case 403: {
               //#line 4379 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4377 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 4379 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 404:  ArgumentList ::= Expression
            //
            case 404: {
               //#line 4388 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4386 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 4388 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 405:  ArgumentList ::= ArgumentList , Expression
            //
            case 405: {
               //#line 4395 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4393 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 4393 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4395 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 406:  FieldAccess ::= Primary . Identifier
            //
            case 406: {
               //#line 4401 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4399 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4399 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4401 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 407:  FieldAccess ::= super . Identifier
            //
            case 407: {
               //#line 4406 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4404 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4406 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 408:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 408: {
               //#line 4411 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4409 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4409 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4409 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4411 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 409:  FieldAccess ::= Primary . class$c
            //
            case 409: {
               //#line 4416 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4414 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4414 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4416 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 410:  FieldAccess ::= super . class$c
            //
            case 410: {
               //#line 4421 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4421 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 411:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 411: {
               //#line 4426 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4424 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4424 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4424 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 4426 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 412:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 412: {
               //#line 4432 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4430 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4430 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 4430 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 4432 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 413:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 413: {
               //#line 4439 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4437 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4437 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4437 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 4437 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 4439 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 414:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 414: {
               //#line 4444 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4442 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4442 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 4442 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 4444 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 415:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 415: {
               //#line 4449 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4447 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4447 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4447 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4447 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 4447 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 4449 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 416:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 416: {
               //#line 4454 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4452 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4452 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 4452 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 4454 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 417:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 417: {
               //#line 4474 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4472 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4472 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(4);
                //#line 4474 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 418:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 418: {
               //#line 4487 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4485 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4485 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4485 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4487 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 419:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 419: {
               //#line 4499 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4497 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4497 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4499 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 420:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 420: {
               //#line 4511 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4509 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4509 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4509 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4509 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(8);
                //#line 4511 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 424:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 424: {
               //#line 4529 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4527 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4529 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 425:  PostDecrementExpression ::= PostfixExpression --
            //
            case 425: {
               //#line 4535 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4533 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4535 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 428:  UnannotatedUnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 428: {
               //#line 4543 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4541 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4543 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 429:  UnannotatedUnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 429: {
               //#line 4548 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4546 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4548 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 432:  UnaryExpression ::= Annotations UnannotatedUnaryExpression
            //
            case 432: {
               //#line 4556 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4554 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4554 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnannotatedUnaryExpression = (Expr) getRhsSym(2);
                //#line 4556 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnannotatedUnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e.position(pos()));
                      break;
            }
    
            //
            // Rule 433:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 433: {
               //#line 4564 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4562 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4564 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 434:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 434: {
               //#line 4570 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4568 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4570 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 436:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 436: {
               //#line 4577 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4575 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4577 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 437:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 437: {
               //#line 4582 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4580 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4582 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 439:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 439: {
               //#line 4589 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4587 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4587 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4589 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 440:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 440: {
               //#line 4594 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4592 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4592 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4594 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 441:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 441: {
               //#line 4599 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4597 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4597 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4599 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 443:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 443: {
               //#line 4606 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4604 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4604 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4606 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 444:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 444: {
               //#line 4611 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4609 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4609 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4611 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 446:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 446: {
               //#line 4618 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4616 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4616 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4618 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 447:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 447: {
               //#line 4623 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4621 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4621 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4623 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 448:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 448: {
               //#line 4628 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4626 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4626 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4628 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 450:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 450: {
               //#line 4635 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4633 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4633 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4635 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 453:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 453: {
               //#line 4644 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4642 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4642 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4644 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 454:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 454: {
               //#line 4649 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4647 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4647 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4649 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 455:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 455: {
               //#line 4654 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4652 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4652 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4654 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 456:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 456: {
               //#line 4659 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4657 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4657 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4659 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 457:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 457: {
               //#line 4664 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4662 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4662 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4664 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 458:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 458: {
               //#line 4669 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4667 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4667 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4669 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 460:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 460: {
               //#line 4676 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4674 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4674 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4676 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 461:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 461: {
               //#line 4681 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4679 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4679 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4681 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 462:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 462: {
               //#line 4686 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4684 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4684 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4686 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 464:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 464: {
               //#line 4693 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4691 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4691 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4693 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 466:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 466: {
               //#line 4700 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4698 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4698 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4700 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 468:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 468: {
               //#line 4707 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4705 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4705 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4707 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 470:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 470: {
               //#line 4714 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4712 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4712 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4714 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 472:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 472: {
               //#line 4721 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4719 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4719 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4721 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 479:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 479: {
               //#line 4734 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4732 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4732 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4732 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4734 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 482:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 482: {
               //#line 4743 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4741 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4741 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4741 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4743 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 483:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 483: {
               //#line 4748 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4746 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4746 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4746 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4746 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4748 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 484:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 484: {
               //#line 4753 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4751 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4751 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4751 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4751 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4753 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 485:  LeftHandSide ::= ExpressionName
            //
            case 485: {
               //#line 4759 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4757 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4759 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 487:  AssignmentOperator ::= =
            //
            case 487: {
               //#line 4766 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4766 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 488:  AssignmentOperator ::= *=
            //
            case 488: {
               //#line 4771 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4771 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 489:  AssignmentOperator ::= /=
            //
            case 489: {
               //#line 4776 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4776 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 490:  AssignmentOperator ::= %=
            //
            case 490: {
               //#line 4781 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4781 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 491:  AssignmentOperator ::= +=
            //
            case 491: {
               //#line 4786 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4786 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 492:  AssignmentOperator ::= -=
            //
            case 492: {
               //#line 4791 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4791 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 493:  AssignmentOperator ::= <<=
            //
            case 493: {
               //#line 4796 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4796 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 494:  AssignmentOperator ::= >>=
            //
            case 494: {
               //#line 4801 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4801 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 495:  AssignmentOperator ::= >>>=
            //
            case 495: {
               //#line 4806 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4806 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 496:  AssignmentOperator ::= &=
            //
            case 496: {
               //#line 4811 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4811 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 497:  AssignmentOperator ::= ^=
            //
            case 497: {
               //#line 4816 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4816 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 498:  AssignmentOperator ::= |=
            //
            case 498: {
               //#line 4821 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4821 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 501:  PrefixOp ::= +
            //
            case 501: {
               //#line 4832 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4832 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 502:  PrefixOp ::= -
            //
            case 502: {
               //#line 4837 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4837 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 503:  PrefixOp ::= !
            //
            case 503: {
               //#line 4842 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4842 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 504:  PrefixOp ::= ~
            //
            case 504: {
               //#line 4847 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4847 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 505:  BinOp ::= +
            //
            case 505: {
               //#line 4853 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4853 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 506:  BinOp ::= -
            //
            case 506: {
               //#line 4858 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4858 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 507:  BinOp ::= *
            //
            case 507: {
               //#line 4863 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4863 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 508:  BinOp ::= /
            //
            case 508: {
               //#line 4868 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4868 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 509:  BinOp ::= %
            //
            case 509: {
               //#line 4873 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4873 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 510:  BinOp ::= &
            //
            case 510: {
               //#line 4878 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4878 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 511:  BinOp ::= |
            //
            case 511: {
               //#line 4883 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4883 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 512:  BinOp ::= ^
            //
            case 512: {
               //#line 4888 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4888 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 513:  BinOp ::= &&
            //
            case 513: {
               //#line 4893 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4893 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 514:  BinOp ::= ||
            //
            case 514: {
               //#line 4898 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4898 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 515:  BinOp ::= <<
            //
            case 515: {
               //#line 4903 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4903 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 516:  BinOp ::= >>
            //
            case 516: {
               //#line 4908 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4908 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 517:  BinOp ::= >>>
            //
            case 517: {
               //#line 4913 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4913 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 518:  BinOp ::= >=
            //
            case 518: {
               //#line 4918 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4918 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 519:  BinOp ::= <=
            //
            case 519: {
               //#line 4923 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4923 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 520:  BinOp ::= >
            //
            case 520: {
               //#line 4928 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4928 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 521:  BinOp ::= <
            //
            case 521: {
               //#line 4933 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4933 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 522:  BinOp ::= ==
            //
            case 522: {
               //#line 4941 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4941 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 523:  BinOp ::= !=
            //
            case 523: {
               //#line 4946 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4946 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 524:  Catchesopt ::= $Empty
            //
            case 524: {
               //#line 4955 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4955 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                      break;
            }
    
            //
            // Rule 526:  Identifieropt ::= $Empty
            //
            case 526:
                setResult(null);
                break;

            //
            // Rule 527:  Identifieropt ::= Identifier
            //
            case 527: {
               //#line 4964 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4962 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4964 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 528:  ForUpdateopt ::= $Empty
            //
            case 528: {
               //#line 4970 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4970 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                      break;
            }
    
            //
            // Rule 530:  Expressionopt ::= $Empty
            //
            case 530:
                setResult(null);
                break;

            //
            // Rule 532:  ForInitopt ::= $Empty
            //
            case 532: {
               //#line 4981 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4981 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 534:  SwitchLabelsopt ::= $Empty
            //
            case 534: {
               //#line 4988 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4988 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 536:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 536: {
               //#line 4995 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4995 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 538:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 538: {
               //#line 5019 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5019 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 540:  ExtendsInterfacesopt ::= $Empty
            //
            case 540: {
               //#line 5026 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5026 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 542:  ClassBodyopt ::= $Empty
            //
            case 542:
                setResult(null);
                break;

            //
            // Rule 544:  ArgumentListopt ::= $Empty
            //
            case 544: {
               //#line 5057 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5057 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 546:  BlockStatementsopt ::= $Empty
            //
            case 546: {
               //#line 5064 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5064 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                      break;
            }
    
            //
            // Rule 548:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 548:
                setResult(null);
                break;

            //
            // Rule 550:  FormalParameterListopt ::= $Empty
            //
            case 550: {
               //#line 5085 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5085 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 552:  Throwsopt ::= $Empty
            //
            case 552: {
               //#line 5092 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5092 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 554:  Offersopt ::= $Empty
            //
            case 554: {
               //#line 5098 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5098 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 556:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 556: {
               //#line 5135 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5135 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 558:  Interfacesopt ::= $Empty
            //
            case 558: {
               //#line 5142 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5142 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 560:  Superopt ::= $Empty
            //
            case 560:
                setResult(null);
                break;

            //
            // Rule 562:  TypeParametersopt ::= $Empty
            //
            case 562: {
               //#line 5153 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5153 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 564:  FormalParametersopt ::= $Empty
            //
            case 564: {
               //#line 5160 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5160 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 566:  Annotationsopt ::= $Empty
            //
            case 566: {
               //#line 5167 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5167 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 568:  TypeDeclarationsopt ::= $Empty
            //
            case 568: {
               //#line 5174 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5174 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 570:  ImportDeclarationsopt ::= $Empty
            //
            case 570: {
               //#line 5181 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5181 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                      break;
            }
    
            //
            // Rule 572:  PackageDeclarationopt ::= $Empty
            //
            case 572:
                setResult(null);
                break;

            //
            // Rule 574:  HasResultTypeopt ::= $Empty
            //
            case 574:
                setResult(null);
                break;

            //
            // Rule 576:  TypeArgumentsopt ::= $Empty
            //
            case 576: {
               //#line 5202 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5202 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 578:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 578: {
               //#line 5209 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5209 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 580:  Propertiesopt ::= $Empty
            //
            case 580: {
               //#line 5216 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5216 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

