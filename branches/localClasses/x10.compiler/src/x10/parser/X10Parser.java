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

//#line 32 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
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
    

    //#line 314 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
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
        public static int PROTO       = 11;
        public static int PUBLIC      = 12;
        public static int SAFE        = 13;
        public static int SEQUENTIAL  = 14;
        public static int SHARED      = 15;
        public static int STATIC      = 16;
        public static int TRANSIENT   = 17;
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
            if (flag == PROTO)        return X10Flags.PROTO;
            if (flag == PUBLIC)       return Flags.PUBLIC;
            if (flag == SAFE)         return X10Flags.SAFE;
            if (flag == SEQUENTIAL)   return X10Flags.SEQUENTIAL;
            if (flag == SHARED)       return X10Flags.SHARED;
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
            if (flag == PROTO)        return "proto";
            if (flag == PUBLIC)       return "public";
            if (flag == SAFE)         return "safe";
            if (flag == SEQUENTIAL)   return "sequential";
            if (flag == SHARED)       return "shared";
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
            classModifiers[GLOBAL] = true;
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
             fieldModifiers[GLOBAL] = true;
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
            variableModifiers[SHARED] = true;
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
             methodModifiers[GLOBAL] = true;
            methodModifiers[INCOMPLETE] = true;
            methodModifiers[NATIVE] = true;
            methodModifiers[NON_BLOCKING] = true;
            methodModifiers[PRIVATE] = true;
            methodModifiers[PROPERTY] = true;
            methodModifiers[PROTECTED] = true;
            methodModifiers[PROTO] = true;
            methodModifiers[PUBLIC] = true;
            methodModifiers[SAFE] = true;
            methodModifiers[SEQUENTIAL] = true;
            methodModifiers[STATIC] = true;
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
               //#line 8 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 6 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 8 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 18 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 16 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 18 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 28 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 26 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 28 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 38 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 36 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 38 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 48 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 46 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 48 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 58 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 56 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 58 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 68 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 66 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 74 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 74 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 80 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 78 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 78 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 87 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 85 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 87 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 94 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 92 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 94 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 100 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 98 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                List ArgumentListopt = (List) getRhsSym(3);
                //#line 100 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 109 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 107 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 117 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 115 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                      break;
            }
    
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 122 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 120 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 120 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1188 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1188 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new LinkedList());
                      break;
            }
    
            //
            // Rule 17:  Modifiersopt ::= Modifiersopt Modifier
            //
            case 17: {
               //#line 1193 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1191 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 1191 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Modifier Modifier = (Modifier) getRhsSym(2);
                //#line 1193 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Modifiersopt.add(Modifier);
                      break;
            }
    
            //
            // Rule 18:  Modifier ::= abstract
            //
            case 18: {
               //#line 1199 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1199 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.ABSTRACT));
                      break;
            }
    
            //
            // Rule 19:  Modifier ::= Annotation
            //
            case 19: {
               //#line 1204 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1202 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 1204 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new AnnotationModifier(Annotation));
                      break;
            }
    
            //
            // Rule 20:  Modifier ::= atomic
            //
            case 20: {
               //#line 1209 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1209 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.ATOMIC));
                      break;
            }
    
            //
            // Rule 21:  Modifier ::= extern
            //
            case 21: {
               //#line 1214 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1214 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.EXTERN));
                      break;
            }
    
            //
            // Rule 22:  Modifier ::= final
            //
            case 22: {
               //#line 1219 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1219 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.FINAL));
                      break;
            }
    
            //
            // Rule 23:  Modifier ::= global
            //
            case 23: {
               //#line 1224 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1224 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.GLOBAL));
                      break;
            }
    
            //
            // Rule 24:  Modifier ::= incomplete
            //
            case 24: {
               //#line 1229 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1229 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.INCOMPLETE));
                      break;
            }
    
            //
            // Rule 25:  Modifier ::= native
            //
            case 25: {
               //#line 1234 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1234 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.NATIVE));
                      break;
            }
    
            //
            // Rule 26:  Modifier ::= nonblocking
            //
            case 26: {
               //#line 1239 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1239 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.NON_BLOCKING));
                      break;
            }
    
            //
            // Rule 27:  Modifier ::= private
            //
            case 27: {
               //#line 1244 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1244 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PRIVATE));
                      break;
            }
    
            //
            // Rule 28:  Modifier ::= protected
            //
            case 28: {
               //#line 1249 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1249 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PROTECTED));
                      break;
            }
    
            //
            // Rule 29:  Modifier ::= proto
            //
            case 29: {
               //#line 1254 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1254 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PROTO));
                      break;
            }
    
            //
            // Rule 30:  Modifier ::= public
            //
            case 30: {
               //#line 1259 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1259 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PUBLIC));
                      break;
            }
    
            //
            // Rule 31:  Modifier ::= safe
            //
            case 31: {
               //#line 1264 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1264 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.SAFE));
                      break;
            }
    
            //
            // Rule 32:  Modifier ::= sequential
            //
            case 32: {
               //#line 1269 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1269 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.SEQUENTIAL));
                      break;
            }
    
            //
            // Rule 33:  Modifier ::= shared
            //
            case 33: {
               //#line 1274 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1274 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.SHARED));
                      break;
            }
    
            //
            // Rule 34:  Modifier ::= static
            //
            case 34: {
               //#line 1279 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1279 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.STATIC));
                      break;
            }
    
            //
            // Rule 35:  Modifier ::= transient
            //
            case 35: {
               //#line 1284 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1284 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.TRANSIENT));
                      break;
            }
    
            //
            // Rule 37:  MethodModifiersopt ::= MethodModifiersopt property$property
            //
            case 37: {
               //#line 1291 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1289 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1289 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken property = (IToken) getRhsIToken(2);
                //#line 1291 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiersopt.add(new FlagModifier(pos(getRhsFirstTokenIndex(2)), FlagModifier.PROPERTY));
                      break;
            }
    
            //
            // Rule 38:  MethodModifiersopt ::= MethodModifiersopt Modifier
            //
            case 38: {
               //#line 1296 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1294 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1294 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Modifier Modifier = (Modifier) getRhsSym(2);
                //#line 1296 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiersopt.add(Modifier);
                      break;
            }
    
            //
            // Rule 39:  TypeDefDeclaration ::= Modifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
            //
            case 39: {
               //#line 1302 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1300 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 1300 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1300 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1300 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParametersopt = (List) getRhsSym(5);
                //#line 1300 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1300 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1302 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 40:  Properties ::= ( PropertyList )
            //
            case 40: {
               //#line 1316 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1314 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(2);
                //#line 1316 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
   setResult(PropertyList);
                 break;
            } 
            //
            // Rule 41:  PropertyList ::= Property
            //
            case 41: {
               //#line 1321 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1319 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 1321 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 42:  PropertyList ::= PropertyList , Property
            //
            case 42: {
               //#line 1328 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1326 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List PropertyList = (List) getRhsSym(1);
                //#line 1326 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 1328 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 43:  Property ::= Annotationsopt Identifier ResultType
            //
            case 43: {
               //#line 1335 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1333 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 1333 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1333 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 1335 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List annotations = extractAnnotations(Annotationsopt);
                PropertyDecl cd = nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), ResultType, Identifier);
                cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 44:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 44: {
               //#line 1344 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1342 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1342 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1342 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1342 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1342 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1342 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1342 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1342 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1342 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1344 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 45:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 45: {
               //#line 1377 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1375 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1375 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1375 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1375 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1375 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 1375 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 1375 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 1375 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(13);
                //#line 1375 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(14);
                //#line 1375 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(15);
                //#line 1377 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 46:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 46: {
               //#line 1401 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1399 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1399 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1399 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1399 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 1399 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 1399 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 1399 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1399 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1399 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1401 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 47:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 47: {
               //#line 1425 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1423 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1423 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1423 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 1423 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 1423 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1423 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1423 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1423 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1423 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1425 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 48:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 48: {
               //#line 1450 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1448 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1448 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1448 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1448 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1448 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1448 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1448 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1448 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1448 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1450 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 49:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 49: {
               //#line 1475 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1473 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1473 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1473 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1473 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1473 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1473 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1473 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1473 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1475 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 50:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 50: {
               //#line 1499 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1497 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1497 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1497 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1497 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1497 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1497 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1497 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1497 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1499 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 51:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 51: {
               //#line 1518 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1516 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1516 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1516 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1516 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1516 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1516 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1516 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(12);
                //#line 1516 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(13);
                //#line 1516 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 1518 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 52:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Throwsopt Offersopt MethodBody
            //
            case 52: {
               //#line 1537 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1535 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1535 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1535 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1535 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1535 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1535 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(10);
                //#line 1535 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1535 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1537 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 53:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 53: {
               //#line 1556 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1554 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1554 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1554 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1554 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1554 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1554 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(11);
                //#line 1554 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1554 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1556 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 54:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 54: {
               //#line 1575 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1573 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1573 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1573 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1573 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1573 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(8);
                //#line 1573 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(9);
                //#line 1573 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(10);
                //#line 1573 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1575 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 55:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt MethodBody
            //
            case 55: {
               //#line 1595 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1593 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1593 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1593 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(3);
                //#line 1593 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(4);
                //#line 1593 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1593 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(6);
                //#line 1593 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(7);
                //#line 1595 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 56:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier WhereClauseopt HasResultTypeopt MethodBody
            //
            case 56: {
               //#line 1612 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1610 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List MethodModifiersopt = (List) getRhsSym(1);
                //#line 1610 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1610 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(3);
                //#line 1610 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 1610 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(5);
                //#line 1612 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 57:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 57: {
               //#line 1630 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1628 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1628 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1630 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 58:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 58: {
               //#line 1635 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1633 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1633 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1635 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 59:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 59: {
               //#line 1640 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1638 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1638 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1638 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1640 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 60:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 60: {
               //#line 1645 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1643 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1643 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 1643 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 1645 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 61:  NormalInterfaceDeclaration ::= Modifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 61: {
               //#line 1651 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1649 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 1649 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1649 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1649 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1649 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1649 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfacesopt = (List) getRhsSym(7);
                //#line 1649 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1651 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 62:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 62: {
               //#line 1673 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1671 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1671 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(3);
                //#line 1671 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(5);
                //#line 1671 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1673 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 63:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 63: {
               //#line 1680 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1678 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1678 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1678 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1678 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1678 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1680 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 64:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 64: {
               //#line 1688 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1686 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1686 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1686 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(5);
                //#line 1686 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(7);
                //#line 1686 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1688 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 65:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 65: {
               //#line 1697 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1695 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 1695 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 1697 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 68:  Type ::= proto ConstrainedType
            //
            case 68: {
               //#line 1706 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1704 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode ConstrainedType = (TypeNode) getRhsSym(2);
                //#line 1706 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
        AddFlags tn = (AddFlags) ConstrainedType;
        tn.addFlags(X10Flags.PROTO);
        setResult(ConstrainedType.position(pos()));
                      break;
            }
    
            //
            // Rule 69:  FunctionType ::= TypeParametersopt ( FormalParameterListopt ) WhereClauseopt Throwsopt Offersopt => Type
            //
            case 69: {
               //#line 1714 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1712 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(1);
                //#line 1712 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(3);
                //#line 1712 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1712 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(6);
                //#line 1712 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(7);
                //#line 1712 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(9);
                //#line 1714 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeParametersopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt, Offersopt));
                      break;
            }
    
            //
            // Rule 71:  AnnotatedType ::= Type Annotations
            //
            case 71: {
               //#line 1727 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1725 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1725 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(2);
                //#line 1727 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn.position(pos()));
                      break;
            }
    
            //
            // Rule 74:  ConstrainedType ::= ( Type )
            //
            case 74: {
               //#line 1737 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1735 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1737 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 76:  SimpleNamedType ::= TypeName
            //
            case 76: {
               //#line 1751 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1749 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1751 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(TypeName.toType());
                      break;
            }
    
            //
            // Rule 77:  SimpleNamedType ::= Primary . Identifier
            //
            case 77: {
               //#line 1756 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1754 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1754 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1756 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(nf.AmbTypeNode(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 78:  SimpleNamedType ::= DepNamedType . Identifier
            //
            case 78: {
               //#line 1761 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1759 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode DepNamedType = (TypeNode) getRhsSym(1);
                //#line 1759 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1761 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(nf.AmbTypeNode(pos(), DepNamedType, Identifier));
                      break;
            }
    
            //
            // Rule 79:  DepNamedType ::= SimpleNamedType DepParameters
            //
            case 79: {
               //#line 1767 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1765 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1765 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(2);
                //#line 1767 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList(new LinkedList(), TypeNode.class, false),
                                              new TypedList(new LinkedList(), Expr.class, false),
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 80:  DepNamedType ::= SimpleNamedType Arguments
            //
            case 80: {
               //#line 1776 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1774 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1774 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Arguments = (List) getRhsSym(2);
                //#line 1776 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList(new LinkedList(), TypeNode.class, false),
                                              Arguments,
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 81:  DepNamedType ::= SimpleNamedType Arguments DepParameters
            //
            case 81: {
               //#line 1785 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1783 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1783 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Arguments = (List) getRhsSym(2);
                //#line 1783 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(3);
                //#line 1785 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList(new LinkedList(), TypeNode.class, false),
                                              Arguments,
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 82:  DepNamedType ::= SimpleNamedType TypeArguments
            //
            case 82: {
               //#line 1794 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1792 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1792 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArguments = (List) getRhsSym(2);
                //#line 1794 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              new TypedList(new LinkedList(), Expr.class, false),
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 83:  DepNamedType ::= SimpleNamedType TypeArguments DepParameters
            //
            case 83: {
               //#line 1803 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1801 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1801 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArguments = (List) getRhsSym(2);
                //#line 1801 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(3);
                //#line 1803 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              new TypedList(new LinkedList(), Expr.class, false),
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 84:  DepNamedType ::= SimpleNamedType TypeArguments Arguments
            //
            case 84: {
               //#line 1812 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1810 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1810 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArguments = (List) getRhsSym(2);
                //#line 1810 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Arguments = (List) getRhsSym(3);
                //#line 1812 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              Arguments,
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 85:  DepNamedType ::= SimpleNamedType TypeArguments Arguments DepParameters
            //
            case 85: {
               //#line 1821 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1819 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1819 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArguments = (List) getRhsSym(2);
                //#line 1819 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Arguments = (List) getRhsSym(3);
                //#line 1819 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(4);
                //#line 1821 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              Arguments,
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 88:  DepParameters ::= { ExistentialListopt Conjunctionopt }
            //
            case 88: {
               //#line 1834 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1832 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(2);
                //#line 1832 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Conjunctionopt = (List) getRhsSym(3);
                //#line 1834 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, (List) Conjunctionopt));
                      break;
            }
    
            //
            // Rule 89:  DepParameters ::= ! PlaceType
            //
            case 89: {
               //#line 1839 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1837 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1839 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 90:  DepParameters ::= !
            //
            case 90: {
               //#line 1845 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1845 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 91:  DepParameters ::= ! PlaceType { ExistentialListopt Conjunction }
            //
            case 91: {
               //#line 1851 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1849 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1849 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(4);
                //#line 1849 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(5);
                //#line 1851 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 92:  DepParameters ::= ! { ExistentialListopt Conjunction }
            //
            case 92: {
               //#line 1857 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1855 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ExistentialListopt = (List) getRhsSym(3);
                //#line 1855 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(4);
                //#line 1857 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 93:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 93: {
               //#line 1865 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1863 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(2);
                //#line 1865 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 94:  TypeParameters ::= [ TypeParameterList ]
            //
            case 94: {
               //#line 1871 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1869 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(2);
                //#line 1871 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 95:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 95: {
               //#line 1877 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1875 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(2);
                //#line 1877 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 96:  Conjunction ::= Expression
            //
            case 96: {
               //#line 1883 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1881 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1883 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 97:  Conjunction ::= Conjunction , Expression
            //
            case 97: {
               //#line 1890 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1888 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1888 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1890 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 98:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 98: {
               //#line 1896 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1894 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1894 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1896 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 99:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 99: {
               //#line 1901 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1899 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1899 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1901 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 100:  WhereClause ::= DepParameters
            //
            case 100: {
               //#line 1907 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1905 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1907 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 101:  Conjunctionopt ::= $Empty
            //
            case 101: {
               //#line 1913 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1913 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                setResult(l);
                      break;
            }
      
            //
            // Rule 102:  Conjunctionopt ::= Conjunction
            //
            case 102: {
               //#line 1919 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1917 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Conjunction = (List) getRhsSym(1);
                //#line 1919 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(Conjunction);
                      break;
            }
    
            //
            // Rule 103:  ExistentialListopt ::= $Empty
            //
            case 103: {
               //#line 1925 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1925 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList());
                      break;
            }
      
            //
            // Rule 104:  ExistentialListopt ::= ExistentialList ;
            //
            case 104: {
               //#line 1930 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1928 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1930 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 105:  ExistentialList ::= FormalParameter
            //
            case 105: {
               //#line 1936 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1934 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1936 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 106:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 106: {
               //#line 1943 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1941 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ExistentialList = (List) getRhsSym(1);
                //#line 1941 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1943 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 109:  NormalClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 109: {
               //#line 1954 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1952 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 1952 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1952 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1952 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1952 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1952 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1952 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(8);
                //#line 1952 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1954 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 110:  StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 110: {
               //#line 1971 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1969 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 1969 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1969 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParamsWithVarianceopt = (List) getRhsSym(4);
                //#line 1969 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Propertiesopt = (List) getRhsSym(5);
                //#line 1969 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1969 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Interfacesopt = (List) getRhsSym(7);
                //#line 1969 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1971 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 111:  ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt ConstructorBody
            //
            case 111: {
               //#line 1986 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1984 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 1984 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParametersopt = (List) getRhsSym(4);
                //#line 1984 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(5);
                //#line 1984 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1984 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1984 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(8);
                //#line 1984 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1984 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(10);
                //#line 1986 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 112:  Super ::= extends ClassType
            //
            case 112: {
               //#line 2004 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2002 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 2004 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 113:  FieldKeyword ::= val
            //
            case 113: {
               //#line 2010 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2010 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 114:  FieldKeyword ::= var
            //
            case 114: {
               //#line 2015 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2015 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 115:  FieldKeyword ::= const
            //
            case 115: {
               //#line 2020 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2020 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL.Static())));
                      break;
            }
    
            //
            // Rule 116:  VarKeyword ::= val
            //
            case 116: {
               //#line 2028 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2028 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 117:  VarKeyword ::= var
            //
            case 117: {
               //#line 2033 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2033 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 118:  FieldDeclaration ::= Modifiersopt FieldKeyword FieldDeclarators ;
            //
            case 118: {
               //#line 2040 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2038 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 2038 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FieldKeyword = (List) getRhsSym(2);
                //#line 2038 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(3);
                //#line 2040 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 119:  FieldDeclaration ::= Modifiersopt FieldDeclarators ;
            //
            case 119: {
               //#line 2066 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2064 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 2064 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(2);
                //#line 2066 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 122:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 122: {
               //#line 2099 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2097 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2097 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt NonExpressionStatement = (Stmt) getRhsSym(2);
                //#line 2099 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                if (NonExpressionStatement.ext() instanceof X10Ext && Annotationsopt instanceof List) {
                    NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations((List) Annotationsopt);
                }
                setResult(NonExpressionStatement.position(pos()));
                      break;
            }
    
            //
            // Rule 149:  OfferStatement ::= offer Expression ;
            //
            case 149: {
               //#line 2135 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2133 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2135 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Offer(pos(), Expression));
                      break;
            }
    
            //
            // Rule 150:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 150: {
               //#line 2141 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2139 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2139 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2141 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 151:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 151: {
               //#line 2147 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2145 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2145 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 2145 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 2147 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 152:  EmptyStatement ::= ;
            //
            case 152: {
               //#line 2153 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2153 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 153:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 153: {
               //#line 2159 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2157 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2157 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 2159 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 159:  ExpressionStatement ::= StatementExpression ;
            //
            case 159: {
               //#line 2171 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2169 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 2171 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 167:  AssertStatement ::= assert Expression ;
            //
            case 167: {
               //#line 2185 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2183 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2185 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 168:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 168: {
               //#line 2190 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2188 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 2188 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 2190 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 169:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 169: {
               //#line 2196 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2194 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2194 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlock = (List) getRhsSym(5);
                //#line 2196 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 170:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 170: {
               //#line 2202 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2200 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroupsopt = (List) getRhsSym(2);
                //#line 2200 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabelsopt = (List) getRhsSym(3);
                //#line 2202 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 172:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 172: {
               //#line 2210 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2208 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroups = (List) getRhsSym(1);
                //#line 2208 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List SwitchBlockStatementGroup = (List) getRhsSym(2);
                //#line 2210 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 173:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 173: {
               //#line 2217 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2215 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 2215 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(2);
                //#line 2217 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 174:  SwitchLabels ::= SwitchLabel
            //
            case 174: {
               //#line 2226 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2224 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 2226 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 175:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 175: {
               //#line 2233 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2231 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List SwitchLabels = (List) getRhsSym(1);
                //#line 2231 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 2233 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 176:  SwitchLabel ::= case ConstantExpression :
            //
            case 176: {
               //#line 2240 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2238 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 2240 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 177:  SwitchLabel ::= default :
            //
            case 177: {
               //#line 2245 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2245 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 178:  WhileStatement ::= while ( Expression ) Statement
            //
            case 178: {
               //#line 2251 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2249 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2249 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2251 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 179:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 179: {
               //#line 2257 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2255 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2255 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2257 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 182:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 182: {
               //#line 2266 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2264 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ForInitopt = (List) getRhsSym(3);
                //#line 2264 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 2264 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ForUpdateopt = (List) getRhsSym(7);
                //#line 2264 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 2266 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 184:  ForInit ::= LocalVariableDeclaration
            //
            case 184: {
               //#line 2273 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2271 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List LocalVariableDeclaration = (List) getRhsSym(1);
                //#line 2273 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 186:  StatementExpressionList ::= StatementExpression
            //
            case 186: {
               //#line 2283 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2281 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 2283 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 187:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 187: {
               //#line 2290 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2288 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List StatementExpressionList = (List) getRhsSym(1);
                //#line 2288 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 2290 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 188:  BreakStatement ::= break Identifieropt ;
            //
            case 188: {
               //#line 2296 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2294 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 2296 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 189:  ContinueStatement ::= continue Identifieropt ;
            //
            case 189: {
               //#line 2302 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2300 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 2302 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 190:  ReturnStatement ::= return Expressionopt ;
            //
            case 190: {
               //#line 2308 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2306 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 2308 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 191:  ThrowStatement ::= throw Expression ;
            //
            case 191: {
               //#line 2314 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2312 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2314 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 192:  TryStatement ::= try Block Catches
            //
            case 192: {
               //#line 2320 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2318 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2318 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(3);
                //#line 2320 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 193:  TryStatement ::= try Block Catchesopt Finally
            //
            case 193: {
               //#line 2325 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2323 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2323 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Catchesopt = (List) getRhsSym(3);
                //#line 2323 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 2325 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 194:  Catches ::= CatchClause
            //
            case 194: {
               //#line 2331 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2329 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 2331 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 195:  Catches ::= Catches CatchClause
            //
            case 195: {
               //#line 2338 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2336 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Catches = (List) getRhsSym(1);
                //#line 2336 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 2338 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 196:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 196: {
               //#line 2345 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2343 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2343 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2345 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 197:  Finally ::= finally Block
            //
            case 197: {
               //#line 2351 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2349 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2351 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 198:  ClockedClause ::= clocked ( ClockList )
            //
            case 198: {
               //#line 2357 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2355 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(3);
                //#line 2357 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 199:  AsyncStatement ::= async PlaceExpressionSingleListopt ClockedClauseopt Statement
            //
            case 199: {
               //#line 2363 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2361 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleListopt = (Expr) getRhsSym(2);
                //#line 2361 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(3);
                //#line 2361 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(4);
                //#line 2363 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), (PlaceExpressionSingleListopt == null
                                                                        ? nf.Here(pos(getLeftSpan()))
                                                                        : PlaceExpressionSingleListopt),
                                         ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 200:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 200: {
               //#line 2372 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2370 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2370 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2372 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 201:  AtomicStatement ::= atomic Statement
            //
            case 201: {
               //#line 2378 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2376 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2378 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 202:  WhenStatement ::= when ( Expression ) Statement
            //
            case 202: {
               //#line 2385 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2383 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2383 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2385 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 203:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 203: {
               //#line 2390 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2388 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 2388 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 2388 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 2388 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 2390 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 204:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 204: {
               //#line 2397 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2395 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2395 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2395 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 2395 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2397 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 205:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 205: {
               //#line 2413 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2411 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2411 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2411 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ClockedClauseopt = (List) getRhsSym(7);
                //#line 2411 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2413 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 206:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 206: {
               //#line 2429 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2427 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2427 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2427 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 2429 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 207:  FinishStatement ::= finish Statement
            //
            case 207: {
               //#line 2443 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2441 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2443 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement));
                      break;
            }
    
            //
            // Rule 208:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 208: {
               //#line 2449 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2447 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 2449 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 210:  NextStatement ::= next ;
            //
            case 210: {
               //#line 2457 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2457 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 211:  AwaitStatement ::= await Expression ;
            //
            case 211: {
               //#line 2463 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2461 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2463 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Await(pos(), Expression));
                      break;
            }
    
            //
            // Rule 212:  ClockList ::= Clock
            //
            case 212: {
               //#line 2469 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2467 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 2469 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 213:  ClockList ::= ClockList , Clock
            //
            case 213: {
               //#line 2476 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2474 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ClockList = (List) getRhsSym(1);
                //#line 2474 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 2476 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 214:  Clock ::= Expression
            //
            case 214: {
               //#line 2484 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2482 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2484 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
    setResult(Expression);
                      break;
            }
    
            //
            // Rule 216:  CastExpression ::= ExpressionName
            //
            case 216: {
               //#line 2498 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2496 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 2498 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 217:  CastExpression ::= CastExpression as Type
            //
            case 217: {
               //#line 2503 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2501 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2501 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2503 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 218:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 218: {
               //#line 2510 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2508 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2510 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 219:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 219: {
               //#line 2517 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2515 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParamWithVarianceList = (List) getRhsSym(1);
                //#line 2515 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2517 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 220:  TypeParameterList ::= TypeParameter
            //
            case 220: {
               //#line 2524 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2522 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2524 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 221:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 221: {
               //#line 2531 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2529 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeParameterList = (List) getRhsSym(1);
                //#line 2529 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2531 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 222:  TypeParamWithVariance ::= Identifier
            //
            case 222: {
               //#line 2538 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2536 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2538 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 223:  TypeParamWithVariance ::= + Identifier
            //
            case 223: {
               //#line 2543 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2541 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2543 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 224:  TypeParamWithVariance ::= - Identifier
            //
            case 224: {
               //#line 2548 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2546 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2548 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 225:  TypeParameter ::= Identifier
            //
            case 225: {
               //#line 2554 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2552 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2554 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 226:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 226: {
               //#line 2579 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2577 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2577 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2579 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 227:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt => ClosureBody
            //
            case 227: {
               //#line 2585 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2583 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameters = (List) getRhsSym(1);
                //#line 2583 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2583 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2583 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Throwsopt = (List) getRhsSym(4);
                //#line 2583 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(5);
                //#line 2583 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 2585 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 228:  LastExpression ::= Expression
            //
            case 228: {
               //#line 2592 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2590 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2592 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 229:  ClosureBody ::= ConditionalExpression
            //
            case 229: {
               //#line 2598 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2596 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 2598 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), ConditionalExpression, true)));
                      break;
            }
    
            //
            // Rule 230:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 230: {
               //#line 2603 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2601 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2601 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 2601 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2603 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 231:  ClosureBody ::= Annotationsopt Block
            //
            case 231: {
               //#line 2613 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2611 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2611 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2613 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b.position(pos()));
                      break;
            }
    
            //
            // Rule 232:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 232: {
               //#line 2622 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2620 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2620 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2622 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 233:  AsyncExpression ::= async ClosureBody
            //
            case 233: {
               //#line 2628 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2626 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2628 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 234:  AsyncExpression ::= async PlaceExpressionSingleList ClosureBody
            //
            case 234: {
               //#line 2633 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2631 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2631 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2633 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 235:  AsyncExpression ::= async [ Type ] ClosureBody
            //
            case 235: {
               //#line 2638 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2636 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2636 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2638 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 236:  AsyncExpression ::= async [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 236: {
               //#line 2643 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2641 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2641 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2641 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2643 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Call(pos(), nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody), nf.Id(pos(), "force")));
                      break;
            }
    
            //
            // Rule 237:  FinishExpression ::= finish ( Expression ) Block
            //
            case 237: {
               //#line 2650 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2648 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2648 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2650 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FinishExpr(pos(), Expression, Block));
                      break;
            }
    
            //
            // Rule 238:  FutureExpression ::= future ClosureBody
            //
            case 238: {
               //#line 2656 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2654 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(2);
                //#line 2656 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 239:  FutureExpression ::= future PlaceExpressionSingleList ClosureBody
            //
            case 239: {
               //#line 2661 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2659 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2659 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2661 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 240:  FutureExpression ::= future [ Type ] ClosureBody
            //
            case 240: {
               //#line 2666 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2664 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2664 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(5);
                //#line 2666 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), nf.Here(pos(getLeftSpan())), Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 241:  FutureExpression ::= future [ Type ] PlaceExpressionSingleList ClosureBody
            //
            case 241: {
               //#line 2671 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2669 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2669 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(5);
                //#line 2669 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2671 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Future(pos(), PlaceExpressionSingleList, Type, ClosureBody));
                      break;
            }
    
            //
            // Rule 242:  WhereClauseopt ::= $Empty
            //
            case 242:
                setResult(null);
                break;

            //
            // Rule 244:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 244:
                setResult(null);
                break;

            //
            // Rule 246:  ClockedClauseopt ::= $Empty
            //
            case 246: {
               //#line 2719 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2719 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 248:  identifier ::= IDENTIFIER$ident
            //
            case 248: {
               //#line 2730 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2728 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2730 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 249:  TypeName ::= Identifier
            //
            case 249: {
               //#line 2737 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2735 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2737 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 250:  TypeName ::= TypeName . Identifier
            //
            case 250: {
               //#line 2742 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2740 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2740 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2742 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 252:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 252: {
               //#line 2754 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2752 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(2);
                //#line 2754 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 253:  TypeArgumentList ::= Type
            //
            case 253: {
               //#line 2761 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2759 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2761 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 254:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 254: {
               //#line 2768 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2766 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentList = (List) getRhsSym(1);
                //#line 2766 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2768 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 255:  PackageName ::= Identifier
            //
            case 255: {
               //#line 2778 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2776 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2778 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 256:  PackageName ::= PackageName . Identifier
            //
            case 256: {
               //#line 2783 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2781 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2781 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2783 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 257:  ExpressionName ::= Identifier
            //
            case 257: {
               //#line 2799 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2797 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2799 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 258:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 258: {
               //#line 2804 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2802 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2802 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2804 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 259:  MethodName ::= Identifier
            //
            case 259: {
               //#line 2814 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2812 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2814 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 260:  MethodName ::= AmbiguousName . Identifier
            //
            case 260: {
               //#line 2819 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2817 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2817 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2819 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 261:  PackageOrTypeName ::= Identifier
            //
            case 261: {
               //#line 2829 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2827 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2829 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 262:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 262: {
               //#line 2834 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2832 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2832 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2834 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 263:  AmbiguousName ::= Identifier
            //
            case 263: {
               //#line 2844 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2842 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2844 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 264:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 264: {
               //#line 2849 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2847 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2847 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2849 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 265:  CompilationUnit ::= PackageDeclarationopt ImportDeclarationsopt TypeDeclarationsopt
            //
            case 265: {
               //#line 2861 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2859 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2859 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarationsopt = (List) getRhsSym(2);
                //#line 2859 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarationsopt = (List) getRhsSym(3);
                //#line 2861 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 266:  ImportDeclarations ::= ImportDeclaration
            //
            case 266: {
               //#line 2877 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2875 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2877 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 267:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 267: {
               //#line 2884 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2882 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ImportDeclarations = (List) getRhsSym(1);
                //#line 2882 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2884 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 268:  TypeDeclarations ::= TypeDeclaration
            //
            case 268: {
               //#line 2892 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2890 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2892 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 269:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 269: {
               //#line 2900 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2898 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeDeclarations = (List) getRhsSym(1);
                //#line 2898 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2900 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 270:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 270: {
               //#line 2908 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2906 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 2906 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2908 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn.position(pos()));
                      break;
            }
    
            //
            // Rule 273:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 273: {
               //#line 2922 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2920 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2922 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 274:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 274: {
               //#line 2928 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2926 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2928 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 278:  TypeDeclaration ::= ;
            //
            case 278: {
               //#line 2943 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2943 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 279:  Interfaces ::= implements InterfaceTypeList
            //
            case 279: {
               //#line 3060 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3058 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(2);
                //#line 3060 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 280:  InterfaceTypeList ::= Type
            //
            case 280: {
               //#line 3066 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3064 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 3066 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 281:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 281: {
               //#line 3073 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3071 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List InterfaceTypeList = (List) getRhsSym(1);
                //#line 3071 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3073 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 282:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 282: {
               //#line 3083 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3081 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarationsopt = (List) getRhsSym(2);
                //#line 3083 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 284:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 284: {
               //#line 3090 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3088 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclarations = (List) getRhsSym(1);
                //#line 3088 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ClassBodyDeclaration = (List) getRhsSym(2);
                //#line 3090 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 286:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 286: {
               //#line 3112 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3110 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 3112 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 288:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 288: {
               //#line 3121 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3119 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3121 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 289:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 289: {
               //#line 3128 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3126 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3128 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 290:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 290: {
               //#line 3135 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3133 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3135 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 291:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 291: {
               //#line 3142 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3140 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3142 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 292:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 292: {
               //#line 3149 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3147 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3149 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 293:  ClassMemberDeclaration ::= ;
            //
            case 293: {
               //#line 3156 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3156 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 294:  FormalDeclarators ::= FormalDeclarator
            //
            case 294: {
               //#line 3163 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3161 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 3163 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 295:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 295: {
               //#line 3170 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3168 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(1);
                //#line 3168 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 3170 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 296:  FieldDeclarators ::= FieldDeclarator
            //
            case 296: {
               //#line 3177 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3175 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 3177 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 297:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 297: {
               //#line 3184 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3182 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclarators = (List) getRhsSym(1);
                //#line 3182 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 3184 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 298:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 298: {
               //#line 3192 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3190 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 3192 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 299:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 299: {
               //#line 3199 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3197 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(1);
                //#line 3197 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 3199 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 300:  VariableDeclarators ::= VariableDeclarator
            //
            case 300: {
               //#line 3206 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3204 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 3206 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 301:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 301: {
               //#line 3213 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3211 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(1);
                //#line 3211 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 3213 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 303:  ResultType ::= : Type
            //
            case 303: {
               //#line 3269 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3267 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3269 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 304:  HasResultType ::= : Type
            //
            case 304: {
               //#line 3274 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3272 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3274 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 305:  HasResultType ::= <: Type
            //
            case 305: {
               //#line 3279 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3277 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3279 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.HasType(Type));
                      break;
            }
    
            //
            // Rule 306:  FormalParameterList ::= FormalParameter
            //
            case 306: {
               //#line 3294 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3292 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 3294 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 307:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 307: {
               //#line 3301 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3299 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterList = (List) getRhsSym(1);
                //#line 3299 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 3301 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 308:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 308: {
               //#line 3307 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3305 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3305 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3307 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 309:  LoopIndexDeclarator ::= ( IdentifierList ) HasResultTypeopt
            //
            case 309: {
               //#line 3312 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3310 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3310 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3312 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 310:  LoopIndexDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt
            //
            case 310: {
               //#line 3317 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3315 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3315 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3315 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3317 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 311:  LoopIndex ::= Modifiersopt LoopIndexDeclarator
            //
            case 311: {
               //#line 3323 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3321 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 3321 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 3323 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 312:  LoopIndex ::= Modifiersopt VarKeyword LoopIndexDeclarator
            //
            case 312: {
               //#line 3347 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3345 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 3345 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3345 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 3347 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 313:  FormalParameter ::= Modifiersopt FormalDeclarator
            //
            case 313: {
               //#line 3372 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3370 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 3370 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 3372 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 314:  FormalParameter ::= Modifiersopt VarKeyword FormalDeclarator
            //
            case 314: {
               //#line 3397 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3395 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 3395 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 3395 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 3397 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 315:  FormalParameter ::= Type
            //
            case 315: {
               //#line 3422 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3420 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 3422 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh("id$")), Collections.EMPTY_LIST, true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 316:  Throws ::= throws ExceptionTypeList
            //
            case 316: {
               //#line 3563 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3561 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(2);
                //#line 3563 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 317:  Offers ::= offers Type
            //
            case 317: {
               //#line 3568 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3566 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3568 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 318:  ExceptionTypeList ::= ExceptionType
            //
            case 318: {
               //#line 3574 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3572 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3574 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 319:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 319: {
               //#line 3581 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3579 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ExceptionTypeList = (List) getRhsSym(1);
                //#line 3579 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3581 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 321:  MethodBody ::= = LastExpression ;
            //
            case 321: {
               //#line 3589 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3587 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3589 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 322:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 322: {
               //#line 3594 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3592 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3592 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(4);
                //#line 3592 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3594 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new ArrayList();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult((Block) ((X10Ext) nf.Block(pos(),l).ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 323:  MethodBody ::= = Annotationsopt Block
            //
            case 323: {
               //#line 3602 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3600 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(2);
                //#line 3600 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3602 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 324:  MethodBody ::= Annotationsopt Block
            //
            case 324: {
               //#line 3607 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3605 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Annotationsopt = (List) getRhsSym(1);
                //#line 3605 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3607 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 325:  MethodBody ::= ;
            //
            case 325:
                setResult(null);
                break;

            //
            // Rule 326:  ConstructorBody ::= = ConstructorBlock
            //
            case 326: {
               //#line 3678 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3676 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3678 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 327:  ConstructorBody ::= ConstructorBlock
            //
            case 327: {
               //#line 3683 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3681 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3683 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 328:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 328: {
               //#line 3688 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3686 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3688 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 329:  ConstructorBody ::= = AssignPropertyCall
            //
            case 329: {
               //#line 3696 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3694 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3696 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l;
                l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 330:  ConstructorBody ::= ;
            //
            case 330:
                setResult(null);
                break;

            //
            // Rule 331:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 331: {
               //#line 3707 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3705 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3705 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(3);
                //#line 3707 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 332:  Arguments ::= ( ArgumentListopt )
            //
            case 332: {
               //#line 3720 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3718 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 3720 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 334:  ExtendsInterfaces ::= extends Type
            //
            case 334: {
               //#line 3777 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3775 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3777 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 335:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 335: {
               //#line 3784 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3782 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ExtendsInterfaces = (List) getRhsSym(1);
                //#line 3782 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3784 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 336:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 336: {
               //#line 3793 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3791 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarationsopt = (List) getRhsSym(2);
                //#line 3793 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 338:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 338: {
               //#line 3800 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3798 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclarations = (List) getRhsSym(1);
                //#line 3798 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List InterfaceMemberDeclaration = (List) getRhsSym(2);
                //#line 3800 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 339:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 339: {
               //#line 3807 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3805 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3807 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 340:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 340: {
               //#line 3814 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3812 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3814 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 341:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 341: {
               //#line 3821 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3819 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FieldDeclaration = (List) getRhsSym(1);
                //#line 3821 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 342:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 342: {
               //#line 3828 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3826 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3828 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 343:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 343: {
               //#line 3835 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3833 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3835 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 344:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 344: {
               //#line 3842 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3840 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3842 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 345:  InterfaceMemberDeclaration ::= ;
            //
            case 345: {
               //#line 3849 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3849 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.EMPTY_LIST);
                      break;
            }
    
            //
            // Rule 346:  Annotations ::= Annotation
            //
            case 346: {
               //#line 3855 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3853 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3855 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 347:  Annotations ::= Annotations Annotation
            //
            case 347: {
               //#line 3862 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3860 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 3860 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3862 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 348:  Annotation ::= @ NamedType
            //
            case 348: {
               //#line 3868 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3866 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3868 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 349:  Identifier ::= identifier
            //
            case 349: {
               //#line 3883 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3881 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3883 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 350:  Block ::= { BlockStatementsopt }
            //
            case 350: {
               //#line 3919 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3917 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List BlockStatementsopt = (List) getRhsSym(2);
                //#line 3919 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 351:  BlockStatements ::= BlockStatement
            //
            case 351: {
               //#line 3925 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3923 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(1);
                //#line 3925 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 352:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 352: {
               //#line 3932 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3930 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List BlockStatements = (List) getRhsSym(1);
                //#line 3930 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List BlockStatement = (List) getRhsSym(2);
                //#line 3932 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 354:  BlockStatement ::= ClassDeclaration
            //
            case 354: {
               //#line 3940 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3938 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3940 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 355:  BlockStatement ::= TypeDefDeclaration
            //
            case 355: {
               //#line 3947 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3945 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3947 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 356:  BlockStatement ::= Statement
            //
            case 356: {
               //#line 3954 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3952 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3954 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 357:  IdentifierList ::= Identifier
            //
            case 357: {
               //#line 3962 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3960 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3962 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 358:  IdentifierList ::= IdentifierList , Identifier
            //
            case 358: {
               //#line 3969 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3967 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(1);
                //#line 3967 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3969 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 359:  FormalDeclarator ::= Identifier ResultType
            //
            case 359: {
               //#line 3975 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3973 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3973 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 3975 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 360:  FormalDeclarator ::= ( IdentifierList ) ResultType
            //
            case 360: {
               //#line 3980 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3978 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 3978 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 3980 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 361:  FormalDeclarator ::= Identifier ( IdentifierList ) ResultType
            //
            case 361: {
               //#line 3985 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3983 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3983 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 3983 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 3985 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 362:  FieldDeclarator ::= Identifier HasResultType
            //
            case 362: {
               //#line 3991 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3989 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3989 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 3991 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultType, null });
                      break;
            }
    
            //
            // Rule 363:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 363: {
               //#line 3996 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3994 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3994 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3994 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 3996 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 364:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 364: {
               //#line 4002 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4000 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4000 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 4000 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 4002 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 365:  VariableDeclarator ::= ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 365: {
               //#line 4007 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4005 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 4005 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 4005 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 4007 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 366:  VariableDeclarator ::= Identifier ( IdentifierList ) HasResultTypeopt = VariableInitializer
            //
            case 366: {
               //#line 4012 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4010 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4010 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 4010 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 4010 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 4012 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 367:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 367: {
               //#line 4018 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4016 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4016 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 4016 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 4018 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.EMPTY_LIST, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 368:  VariableDeclaratorWithType ::= ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 368: {
               //#line 4023 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4021 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(2);
                //#line 4021 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(4);
                //#line 4021 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 4023 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 369:  VariableDeclaratorWithType ::= Identifier ( IdentifierList ) HasResultType = VariableInitializer
            //
            case 369: {
               //#line 4028 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4026 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4026 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List IdentifierList = (List) getRhsSym(3);
                //#line 4026 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(5);
                //#line 4026 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 4028 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 371:  LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
            //
            case 371: {
               //#line 4036 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4034 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 4034 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 4034 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclarators = (List) getRhsSym(3);
                //#line 4036 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 372:  LocalVariableDeclaration ::= Modifiersopt VariableDeclaratorsWithType
            //
            case 372: {
               //#line 4070 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4068 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 4068 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List VariableDeclaratorsWithType = (List) getRhsSym(2);
                //#line 4070 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 373:  LocalVariableDeclaration ::= Modifiersopt VarKeyword FormalDeclarators
            //
            case 373: {
               //#line 4105 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4103 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Modifiersopt = (List) getRhsSym(1);
                //#line 4103 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List VarKeyword = (List) getRhsSym(2);
                //#line 4103 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalDeclarators = (List) getRhsSym(3);
                //#line 4105 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 374:  Primary ::= here
            //
            case 374: {
               //#line 4147 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4147 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 375:  Primary ::= [ ArgumentListopt ]
            //
            case 375: {
               //#line 4153 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4151 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(2);
                //#line 4153 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 377:  Primary ::= self
            //
            case 377: {
               //#line 4161 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4161 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 378:  Primary ::= this
            //
            case 378: {
               //#line 4166 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4166 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 379:  Primary ::= ClassName . this
            //
            case 379: {
               //#line 4171 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4169 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4171 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 380:  Primary ::= ( Expression )
            //
            case 380: {
               //#line 4176 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4174 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 4176 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 386:  OperatorFunction ::= TypeName . +
            //
            case 386: {
               //#line 4187 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4185 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4187 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 387:  OperatorFunction ::= TypeName . -
            //
            case 387: {
               //#line 4198 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4196 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4198 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 388:  OperatorFunction ::= TypeName . *
            //
            case 388: {
               //#line 4209 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4207 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4209 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 389:  OperatorFunction ::= TypeName . /
            //
            case 389: {
               //#line 4220 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4218 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4220 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 390:  OperatorFunction ::= TypeName . %
            //
            case 390: {
               //#line 4231 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4229 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4231 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 391:  OperatorFunction ::= TypeName . &
            //
            case 391: {
               //#line 4242 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4240 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4242 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 392:  OperatorFunction ::= TypeName . |
            //
            case 392: {
               //#line 4253 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4251 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4253 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 393:  OperatorFunction ::= TypeName . ^
            //
            case 393: {
               //#line 4264 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4262 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4264 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 394:  OperatorFunction ::= TypeName . <<
            //
            case 394: {
               //#line 4275 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4273 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4275 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 395:  OperatorFunction ::= TypeName . >>
            //
            case 395: {
               //#line 4286 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4284 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4286 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 396:  OperatorFunction ::= TypeName . >>>
            //
            case 396: {
               //#line 4297 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4295 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4297 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 397:  OperatorFunction ::= TypeName . <
            //
            case 397: {
               //#line 4308 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4306 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4308 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 398:  OperatorFunction ::= TypeName . <=
            //
            case 398: {
               //#line 4319 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4317 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4319 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 399:  OperatorFunction ::= TypeName . >=
            //
            case 399: {
               //#line 4330 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4328 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4330 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 400:  OperatorFunction ::= TypeName . >
            //
            case 400: {
               //#line 4341 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4339 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4341 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 401:  OperatorFunction ::= TypeName . ==
            //
            case 401: {
               //#line 4352 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4350 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4352 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 402:  OperatorFunction ::= TypeName . !=
            //
            case 402: {
               //#line 4363 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4361 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4363 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 403:  Literal ::= IntegerLiteral$lit
            //
            case 403: {
               //#line 4376 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4374 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4376 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 404:  Literal ::= LongLiteral$lit
            //
            case 404: {
               //#line 4382 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4380 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4382 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 405:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 405: {
               //#line 4388 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4386 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4388 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 406:  Literal ::= UnsignedLongLiteral$lit
            //
            case 406: {
               //#line 4394 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4392 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4394 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 407:  Literal ::= FloatingPointLiteral$lit
            //
            case 407: {
               //#line 4400 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4398 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4400 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 408:  Literal ::= DoubleLiteral$lit
            //
            case 408: {
               //#line 4406 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4404 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4406 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 409:  Literal ::= BooleanLiteral
            //
            case 409: {
               //#line 4412 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4410 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 4412 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 410:  Literal ::= CharacterLiteral$lit
            //
            case 410: {
               //#line 4417 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4415 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4417 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 411:  Literal ::= StringLiteral$str
            //
            case 411: {
               //#line 4423 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4421 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 4423 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 412:  Literal ::= null
            //
            case 412: {
               //#line 4429 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4429 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 413:  BooleanLiteral ::= true$trueLiteral
            //
            case 413: {
               //#line 4435 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4433 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 4435 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 414:  BooleanLiteral ::= false$falseLiteral
            //
            case 414: {
               //#line 4440 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4438 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 4440 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 415:  ArgumentList ::= Expression
            //
            case 415: {
               //#line 4449 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4447 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 4449 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                List l = new TypedList(new LinkedList(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 416:  ArgumentList ::= ArgumentList , Expression
            //
            case 416: {
               //#line 4456 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4454 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(1);
                //#line 4454 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4456 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 417:  FieldAccess ::= Primary . Identifier
            //
            case 417: {
               //#line 4462 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4460 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4460 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4462 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 418:  FieldAccess ::= super . Identifier
            //
            case 418: {
               //#line 4467 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4465 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4467 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 419:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 419: {
               //#line 4472 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4470 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4470 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4470 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4472 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 420:  FieldAccess ::= Primary . class$c
            //
            case 420: {
               //#line 4477 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4475 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4475 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4477 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 421:  FieldAccess ::= super . class$c
            //
            case 421: {
               //#line 4482 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4480 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4482 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 422:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 422: {
               //#line 4487 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4485 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4485 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4485 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 4487 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 423:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 423: {
               //#line 4493 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4491 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4491 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 4491 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 4493 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 424:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 424: {
               //#line 4500 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4498 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4498 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4498 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 4498 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 4500 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 425:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 425: {
               //#line 4505 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4503 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4503 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(4);
                //#line 4503 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(6);
                //#line 4505 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 426:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 426: {
               //#line 4510 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4508 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4508 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4508 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4508 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(6);
                //#line 4508 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(8);
                //#line 4510 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 427:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 427: {
               //#line 4515 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4513 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4513 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List TypeArgumentsopt = (List) getRhsSym(2);
                //#line 4513 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentListopt = (List) getRhsSym(4);
                //#line 4515 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 428:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 428: {
               //#line 4535 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4533 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4533 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(4);
                //#line 4535 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 429:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 429: {
               //#line 4548 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4546 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4546 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4546 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4548 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 430:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 430: {
               //#line 4560 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4558 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4558 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(6);
                //#line 4560 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 431:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 431: {
               //#line 4572 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4570 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4570 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4570 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4570 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List FormalParameterListopt = (List) getRhsSym(8);
                //#line 4572 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 435:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 435: {
               //#line 4590 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4588 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4590 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 436:  PostDecrementExpression ::= PostfixExpression --
            //
            case 436: {
               //#line 4596 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4594 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4596 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 439:  UnannotatedUnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 439: {
               //#line 4604 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4602 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4604 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 440:  UnannotatedUnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 440: {
               //#line 4609 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4607 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4609 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 443:  UnaryExpression ::= Annotations UnannotatedUnaryExpression
            //
            case 443: {
               //#line 4617 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4615 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List Annotations = (List) getRhsSym(1);
                //#line 4615 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr UnannotatedUnaryExpression = (Expr) getRhsSym(2);
                //#line 4617 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnannotatedUnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e.position(pos()));
                      break;
            }
    
            //
            // Rule 444:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 444: {
               //#line 4625 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4623 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4625 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 445:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 445: {
               //#line 4631 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4629 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4631 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 447:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 447: {
               //#line 4638 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4636 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4638 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 448:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 448: {
               //#line 4643 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4641 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4643 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 450:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 450: {
               //#line 4650 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4648 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4648 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4650 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 451:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 451: {
               //#line 4655 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4653 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4653 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4655 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 452:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 452: {
               //#line 4660 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4658 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4658 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4660 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 454:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 454: {
               //#line 4667 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4665 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4665 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4667 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 455:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 455: {
               //#line 4672 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4670 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4670 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4672 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 457:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 457: {
               //#line 4679 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4677 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4677 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4679 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 458:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 458: {
               //#line 4684 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4682 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4682 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4684 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 459:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 459: {
               //#line 4689 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4687 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4687 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4689 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 461:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 461: {
               //#line 4696 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4694 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4694 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4696 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 464:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 464: {
               //#line 4705 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4703 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4703 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4705 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 465:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 465: {
               //#line 4710 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4708 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4708 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4710 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 466:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 466: {
               //#line 4715 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4713 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4713 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4715 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 467:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 467: {
               //#line 4720 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4718 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4718 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4720 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 468:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 468: {
               //#line 4725 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4723 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4723 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4725 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 469:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 469: {
               //#line 4730 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4728 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4728 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4730 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 471:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 471: {
               //#line 4737 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4735 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4735 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4737 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 472:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 472: {
               //#line 4742 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4740 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4740 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4742 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 473:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 473: {
               //#line 4747 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4745 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4745 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4747 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 475:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 475: {
               //#line 4754 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4752 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4752 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4754 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 477:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 477: {
               //#line 4761 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4759 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4759 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4761 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 479:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 479: {
               //#line 4768 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4766 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4766 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4768 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 481:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 481: {
               //#line 4775 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4773 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4773 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4775 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 483:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 483: {
               //#line 4782 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4780 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4780 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4782 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 490:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 490: {
               //#line 4795 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4793 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4793 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4793 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4795 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 493:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 493: {
               //#line 4804 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4802 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4802 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4802 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4804 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 494:  Assignment ::= ExpressionName$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 494: {
               //#line 4809 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4807 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4807 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4807 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4807 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4809 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 495:  Assignment ::= Primary$e1 ( ArgumentList ) AssignmentOperator AssignmentExpression
            //
            case 495: {
               //#line 4814 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4812 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4812 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                List ArgumentList = (List) getRhsSym(3);
                //#line 4812 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4812 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4814 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentList, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 496:  LeftHandSide ::= ExpressionName
            //
            case 496: {
               //#line 4820 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4818 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4820 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 498:  AssignmentOperator ::= =
            //
            case 498: {
               //#line 4827 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4827 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 499:  AssignmentOperator ::= *=
            //
            case 499: {
               //#line 4832 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4832 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 500:  AssignmentOperator ::= /=
            //
            case 500: {
               //#line 4837 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4837 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 501:  AssignmentOperator ::= %=
            //
            case 501: {
               //#line 4842 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4842 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 502:  AssignmentOperator ::= +=
            //
            case 502: {
               //#line 4847 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4847 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 503:  AssignmentOperator ::= -=
            //
            case 503: {
               //#line 4852 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4852 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 504:  AssignmentOperator ::= <<=
            //
            case 504: {
               //#line 4857 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4857 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 505:  AssignmentOperator ::= >>=
            //
            case 505: {
               //#line 4862 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4862 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 506:  AssignmentOperator ::= >>>=
            //
            case 506: {
               //#line 4867 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4867 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 507:  AssignmentOperator ::= &=
            //
            case 507: {
               //#line 4872 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4872 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 508:  AssignmentOperator ::= ^=
            //
            case 508: {
               //#line 4877 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4877 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 509:  AssignmentOperator ::= |=
            //
            case 509: {
               //#line 4882 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4882 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 512:  PrefixOp ::= +
            //
            case 512: {
               //#line 4893 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4893 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 513:  PrefixOp ::= -
            //
            case 513: {
               //#line 4898 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4898 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 514:  PrefixOp ::= !
            //
            case 514: {
               //#line 4903 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4903 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 515:  PrefixOp ::= ~
            //
            case 515: {
               //#line 4908 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4908 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 516:  BinOp ::= +
            //
            case 516: {
               //#line 4914 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4914 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 517:  BinOp ::= -
            //
            case 517: {
               //#line 4919 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4919 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 518:  BinOp ::= *
            //
            case 518: {
               //#line 4924 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4924 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 519:  BinOp ::= /
            //
            case 519: {
               //#line 4929 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4929 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 520:  BinOp ::= %
            //
            case 520: {
               //#line 4934 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4934 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 521:  BinOp ::= &
            //
            case 521: {
               //#line 4939 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4939 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 522:  BinOp ::= |
            //
            case 522: {
               //#line 4944 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4944 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 523:  BinOp ::= ^
            //
            case 523: {
               //#line 4949 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4949 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 524:  BinOp ::= &&
            //
            case 524: {
               //#line 4954 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4954 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 525:  BinOp ::= ||
            //
            case 525: {
               //#line 4959 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4959 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 526:  BinOp ::= <<
            //
            case 526: {
               //#line 4964 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4964 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 527:  BinOp ::= >>
            //
            case 527: {
               //#line 4969 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4969 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 528:  BinOp ::= >>>
            //
            case 528: {
               //#line 4974 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4974 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 529:  BinOp ::= >=
            //
            case 529: {
               //#line 4979 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4979 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 530:  BinOp ::= <=
            //
            case 530: {
               //#line 4984 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4984 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 531:  BinOp ::= >
            //
            case 531: {
               //#line 4989 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4989 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 532:  BinOp ::= <
            //
            case 532: {
               //#line 4994 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4994 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 533:  BinOp ::= ==
            //
            case 533: {
               //#line 5002 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5002 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 534:  BinOp ::= !=
            //
            case 534: {
               //#line 5007 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5007 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 535:  Catchesopt ::= $Empty
            //
            case 535: {
               //#line 5016 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5016 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Catch.class, false));
                      break;
            }
    
            //
            // Rule 537:  Identifieropt ::= $Empty
            //
            case 537:
                setResult(null);
                break;

            //
            // Rule 538:  Identifieropt ::= Identifier
            //
            case 538: {
               //#line 5025 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 5023 "C:/eclipsews/localClasses/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 5025 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 539:  ForUpdateopt ::= $Empty
            //
            case 539: {
               //#line 5031 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5031 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForUpdate.class, false));
                      break;
            }
    
            //
            // Rule 541:  Expressionopt ::= $Empty
            //
            case 541:
                setResult(null);
                break;

            //
            // Rule 543:  ForInitopt ::= $Empty
            //
            case 543: {
               //#line 5042 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5042 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 545:  SwitchLabelsopt ::= $Empty
            //
            case 545: {
               //#line 5049 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5049 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Case.class, false));
                      break;
            }
    
            //
            // Rule 547:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 547: {
               //#line 5056 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5056 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 549:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 549: {
               //#line 5080 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5080 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 551:  ExtendsInterfacesopt ::= $Empty
            //
            case 551: {
               //#line 5087 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5087 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 553:  ClassBodyopt ::= $Empty
            //
            case 553:
                setResult(null);
                break;

            //
            // Rule 555:  ArgumentListopt ::= $Empty
            //
            case 555: {
               //#line 5118 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5118 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 557:  BlockStatementsopt ::= $Empty
            //
            case 557: {
               //#line 5125 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5125 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Stmt.class, false));
                      break;
            }
    
            //
            // Rule 559:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 559:
                setResult(null);
                break;

            //
            // Rule 561:  FormalParameterListopt ::= $Empty
            //
            case 561: {
               //#line 5146 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5146 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 563:  Throwsopt ::= $Empty
            //
            case 563: {
               //#line 5153 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5153 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 565:  Offersopt ::= $Empty
            //
            case 565: {
               //#line 5159 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5159 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 567:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 567: {
               //#line 5196 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5196 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 569:  Interfacesopt ::= $Empty
            //
            case 569: {
               //#line 5203 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5203 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 571:  Superopt ::= $Empty
            //
            case 571:
                setResult(null);
                break;

            //
            // Rule 573:  TypeParametersopt ::= $Empty
            //
            case 573: {
               //#line 5214 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5214 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 575:  FormalParametersopt ::= $Empty
            //
            case 575: {
               //#line 5221 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5221 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 577:  Annotationsopt ::= $Empty
            //
            case 577: {
               //#line 5228 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5228 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 579:  TypeDeclarationsopt ::= $Empty
            //
            case 579: {
               //#line 5235 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5235 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 581:  ImportDeclarationsopt ::= $Empty
            //
            case 581: {
               //#line 5242 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5242 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), Import.class, false));
                      break;
            }
    
            //
            // Rule 583:  PackageDeclarationopt ::= $Empty
            //
            case 583:
                setResult(null);
                break;

            //
            // Rule 585:  HasResultTypeopt ::= $Empty
            //
            case 585:
                setResult(null);
                break;

            //
            // Rule 587:  TypeArgumentsopt ::= $Empty
            //
            case 587: {
               //#line 5263 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5263 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 589:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 589: {
               //#line 5270 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5270 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 591:  Propertiesopt ::= $Empty
            //
            case 591: {
               //#line 5277 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5277 "C:/eclipsews/localClasses/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList(new LinkedList(), PropertyDecl.class, false));
                      break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

