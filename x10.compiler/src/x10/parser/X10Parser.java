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
import polyglot.ast.Node;
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
    

    //#line 315 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
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

        public static String getErrorMessageFor(int errorCode, String[] errorInfo) {

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
                                  String[] errorInfo)
        {
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
    private List<Node> checkModifiers(String kind, List<Modifier> modifiers, boolean legal_flags[]) {
        List<Node> l = new LinkedList<Node>();

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

    private List<Node> checkClassModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0
                 ? Collections.<Node>singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE)))
                 : checkModifiers("class", modifiers, FlagModifier.classModifiers));
    }

    private List<Node> checkTypeDefModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0
                 ? Collections.<Node>singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, X10Flags.toX10Flags(Flags.NONE)))
                 : checkModifiers("typedef", modifiers, FlagModifier.typeDefModifiers));
    }

    private List<Node> checkFieldModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0
                 ? Collections.<Node>emptyList()
                 : checkModifiers("field", modifiers, FlagModifier.fieldModifiers));
    }

    private List<Node> checkVariableModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0
                 ? Collections.<Node>emptyList()
                 : checkModifiers("variable", modifiers, FlagModifier.variableModifiers));
    }

    private List<Node> checkMethodModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0
                 ? Collections.<Node>emptyList()
                 : checkModifiers("method", modifiers, FlagModifier.methodModifiers));
    }

    private List<Node> checkConstructorModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0
                 ? Collections.<Node>emptyList()
                 : checkModifiers("constructor", modifiers, FlagModifier.constructorModifiers));
    }

    private List<Node> checkInterfaceModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0
                 ? Collections.<Node>emptyList()
                 : checkModifiers("interface", modifiers, FlagModifier.interfaceModifiers));
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
        syntaxError(msg, pos, false);
    }

    public void syntaxError(String msg, Position pos, boolean unrecoverable) {
        unrecoverableSyntaxError = unrecoverable;
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

            
    private List<AnnotationNode> extractAnnotations(List<? extends Node> l) {
        List<AnnotationNode> l2 = new LinkedList<AnnotationNode>();
        for (Node n : l) {
            if (n instanceof AnnotationNode) {
                l2.add((AnnotationNode) n);
            }
        }
        return l2;
    }

    private FlagsNode extractFlags(List<? extends Node> l, Flags f) {
        FlagsNode fn = extractFlags(l);
        fn = fn.flags(fn.flags().set(f));
        return fn;
    }
    
    private FlagsNode extractFlags(List<? extends Node> l1, List<? extends Node> l2) {
        List<Node> l = new ArrayList<Node>();
        l.addAll(l1);
        l.addAll(l2);
        return extractFlags(l);
    }
    
    private FlagsNode extractFlags(List<? extends Node> l) {
        Position pos = null;
        X10Flags xf = X10Flags.toX10Flags(Flags.NONE);
        for (Node n : l) {
            if (n instanceof FlagsNode) {
                FlagsNode fn = (FlagsNode) n;
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
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(3);
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
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(3);
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
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(3);
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
               //#line 1190 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1190 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new LinkedList<Modifier>());
                      break;
            }
    
            //
            // Rule 17:  Modifiersopt ::= Modifiersopt Modifier
            //
            case 17: {
               //#line 1195 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1193 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1193 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Modifier Modifier = (Modifier) getRhsSym(2);
                //#line 1195 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Modifiersopt.add(Modifier);
                      break;
            }
    
            //
            // Rule 18:  Modifier ::= abstract
            //
            case 18: {
               //#line 1201 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1201 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.ABSTRACT));
                      break;
            }
    
            //
            // Rule 19:  Modifier ::= Annotation
            //
            case 19: {
               //#line 1206 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1204 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 1206 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new AnnotationModifier(Annotation));
                      break;
            }
    
            //
            // Rule 20:  Modifier ::= atomic
            //
            case 20: {
               //#line 1211 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1211 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.ATOMIC));
                      break;
            }
    
            //
            // Rule 21:  Modifier ::= extern
            //
            case 21: {
               //#line 1216 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1216 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.EXTERN));
                      break;
            }
    
            //
            // Rule 22:  Modifier ::= final
            //
            case 22: {
               //#line 1221 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1221 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.FINAL));
                      break;
            }
    
            //
            // Rule 23:  Modifier ::= global
            //
            case 23: {
               //#line 1226 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1226 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.GLOBAL));
                      break;
            }
    
            //
            // Rule 24:  Modifier ::= incomplete
            //
            case 24: {
               //#line 1231 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1231 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.INCOMPLETE));
                      break;
            }
    
            //
            // Rule 25:  Modifier ::= native
            //
            case 25: {
               //#line 1236 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1236 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.NATIVE));
                      break;
            }
    
            //
            // Rule 26:  Modifier ::= nonblocking
            //
            case 26: {
               //#line 1241 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1241 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.NON_BLOCKING));
                      break;
            }
    
            //
            // Rule 27:  Modifier ::= private
            //
            case 27: {
               //#line 1246 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1246 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PRIVATE));
                      break;
            }
    
            //
            // Rule 28:  Modifier ::= protected
            //
            case 28: {
               //#line 1251 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1251 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PROTECTED));
                      break;
            }
    
            //
            // Rule 29:  Modifier ::= public
            //
            case 29: {
               //#line 1256 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1256 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PUBLIC));
                      break;
            }
    
            //
            // Rule 30:  Modifier ::= safe
            //
            case 30: {
               //#line 1261 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1261 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.SAFE));
                      break;
            }
    
            //
            // Rule 31:  Modifier ::= sequential
            //
            case 31: {
               //#line 1266 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1266 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.SEQUENTIAL));
                      break;
            }
    
            //
            // Rule 32:  Modifier ::= static
            //
            case 32: {
               //#line 1271 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1271 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.STATIC));
                      break;
            }
    
            //
            // Rule 33:  Modifier ::= transient
            //
            case 33: {
               //#line 1276 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1276 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.TRANSIENT));
                      break;
            }
    
            //
            // Rule 34:  Modifier ::= clocked
            //
            case 34: {
               //#line 1281 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1281 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.CLOCKED));
                      break;
            }
    
            //
            // Rule 36:  MethodModifiersopt ::= MethodModifiersopt property$property
            //
            case 36: {
               //#line 1288 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1286 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1286 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken property = (IToken) getRhsIToken(2);
                //#line 1288 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiersopt.add(new FlagModifier(pos(getRhsFirstTokenIndex(2)), FlagModifier.PROPERTY));
                      break;
            }
    
            //
            // Rule 37:  MethodModifiersopt ::= MethodModifiersopt Modifier
            //
            case 37: {
               //#line 1293 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1291 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1291 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Modifier Modifier = (Modifier) getRhsSym(2);
                //#line 1293 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiersopt.add(Modifier);
                      break;
            }
    
            //
            // Rule 38:  TypeDefDeclaration ::= Modifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
            //
            case 38: {
               //#line 1299 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1297 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1297 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1297 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1297 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParametersopt = (List<Formal>) getRhsSym(5);
                //#line 1297 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1297 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1299 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkTypeDefModifiers(Modifiersopt);
                FlagsNode f = extractFlags(modifiers);
                List<AnnotationNode> annotations = extractAnnotations(modifiers);
                List<Formal> formals = new ArrayList<Formal>();
                for (Formal v : FormalParametersopt) {
                    FlagsNode flags = v.flags();
                    if (!flags.flags().isFinal()) {
                        syntaxError("Type definition parameters must be final.", v.position());
                        v = v.flags(flags.flags(flags.flags().Final()));
                    }
                    formals.add(v);
                }
                TypeDecl cd = nf.TypeDecl(pos(), f, Identifier, TypeParametersopt, formals, WhereClauseopt, Type);
                cd = (TypeDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 39:  Properties ::= ( PropertyList )
            //
            case 39: {
               //#line 1319 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1317 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<PropertyDecl> PropertyList = (List<PropertyDecl>) getRhsSym(2);
                //#line 1319 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(PropertyList);
                 break;
            } 
            //
            // Rule 40:  PropertyList ::= Property
            //
            case 40: {
               //#line 1324 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1322 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 1324 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<PropertyDecl> l = new TypedList<PropertyDecl>(new LinkedList<PropertyDecl>(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 41:  PropertyList ::= PropertyList , Property
            //
            case 41: {
               //#line 1331 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1329 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<PropertyDecl> PropertyList = (List<PropertyDecl>) getRhsSym(1);
                //#line 1329 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 1331 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 42:  Property ::= Annotationsopt Identifier ResultType
            //
            case 42: {
               //#line 1338 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1336 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 1336 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1336 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 1338 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<AnnotationNode> annotations = extractAnnotations(Annotationsopt);
                PropertyDecl cd = nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), ResultType, Identifier);
                cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 43:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 43: {
               //#line 1347 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1345 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1345 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1345 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1345 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(5);
                //#line 1345 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1345 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1345 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(8);
                //#line 1345 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1345 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1347 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                ProcedureDecl pd;
                if (Identifier.id().toString().equals("this")) {
                    pd = nf.X10ConstructorDecl(pos(),
                                               extractFlags(modifiers),
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
                                          extractFlags(modifiers),
                                          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                          Identifier,
                                          TypeParametersopt,
                                          FormalParameters,
                                          WhereClauseopt,
                                          Throwsopt,
                                          Offersopt,
                                          MethodBody);
                }
                pd = (ProcedureDecl) ((X10Ext) pd.ext()).annotations(extractAnnotations(modifiers));
                setResult(pd);
                      break;
            }
    
            //
            // Rule 44:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 44: {
               //#line 1380 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 1378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 1378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 1378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(13);
                //#line 1378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(14);
                //#line 1378 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(15);
                //#line 1380 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                Name opName = X10Binary_c.binaryMethodName(BinOp);
                if (opName == null) {
                    syntaxError("Cannot override binary operator '"+BinOp+"'.", pos());
                    opName = Name.make("invalid operator");
                }
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(getRhsFirstTokenIndex(7)), opName),
                                                 TypeParametersopt,
                                                 Arrays.<Formal>asList(fp1, fp2),
                                                 WhereClauseopt,
                                                 Throwsopt,
                                                 Offersopt,
                                                 MethodBody);
                FlagsNode flags = md.flags();
                if (! flags.flags().isStatic()) {
                    syntaxError("Binary operator with two parameters must be static.", md.position());
                    md = md.flags(flags.flags(flags.flags().Static()));
                }
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 45:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 45: {
               //#line 1407 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1405 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1405 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1405 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1405 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 1405 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 1405 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 1405 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(10);
                //#line 1405 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1405 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1407 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                Name opName = X10Unary_c.unaryMethodName(PrefixOp);
                if (opName == null) {
                    syntaxError("Cannot override unary operator '"+PrefixOp+"'.", pos());
                    opName = Name.make("invalid operator");
                }
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(getRhsFirstTokenIndex(4)), opName),
                                                 TypeParametersopt,
                                                 Collections.<Formal>singletonList(fp2),
                                                 WhereClauseopt,
                                                 Throwsopt,
                                                 Offersopt,
                                                 MethodBody);
                FlagsNode flags = md.flags();
                if (! flags.flags().isStatic()) {
                    syntaxError("Unary operator with one parameter must be static.", md.position());
                    md = md.flags(flags.flags(flags.flags().Static()));
                }
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 46:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 46: {
               //#line 1434 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1432 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1432 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1432 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 1432 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 1432 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1432 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1432 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(11);
                //#line 1432 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1432 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1434 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                Name opName = X10Binary_c.binaryMethodName(BinOp);
                if (opName == null) {
                    syntaxError("Cannot override binary operator '"+BinOp+"'.", pos());
                    opName = Name.make("invalid operator");
                }
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(getRhsFirstTokenIndex(5)), opName),
                                                 TypeParametersopt,
                                                 Collections.<Formal>singletonList(fp2),
                                                 WhereClauseopt,
                                                 Throwsopt,
                                                 Offersopt,
                                                 MethodBody);
                FlagsNode flags = md.flags();
                if (flags.flags().isStatic()) {
                    syntaxError("Binary operator with this parameter cannot be static.", md.position());
                    md = md.flags(flags.flags(flags.flags().clearStatic()));
                }
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 47:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 47: {
               //#line 1461 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1459 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1459 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1459 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1459 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1459 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1459 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1459 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(11);
                //#line 1459 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1459 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1461 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                Name opName = X10Binary_c.invBinaryMethodName(BinOp);
                if (opName == null) {
                    syntaxError("Cannot override binary operator '"+BinOp+"'.", pos());
                    opName = Name.make("invalid operator");
                }
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(getRhsFirstTokenIndex(7)), opName),
                                                 TypeParametersopt,
                                                 Collections.<Formal>singletonList(fp1),
                                                 WhereClauseopt,
                                                 Throwsopt,
                                                 Offersopt,
                                                 MethodBody);
                FlagsNode flags = md.flags();
                if (flags.flags().isStatic()) {
                    syntaxError("Binary operator with this parameter cannot be static.", md.position());
                    md = md.flags(flags.flags(flags.flags().clearStatic()));
                }
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 48:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 48: {
               //#line 1488 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1486 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1486 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1486 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1486 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1486 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1486 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(8);
                //#line 1486 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1486 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1488 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                Name opName = X10Unary_c.unaryMethodName(PrefixOp);
                if (opName == null) {
                    syntaxError("Cannot override unary operator '"+PrefixOp+"'.", pos());
                    opName = Name.make("invalid operator");
                }
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(getRhsFirstTokenIndex(4)), opName),
                                                 TypeParametersopt,
                                                 Collections.<Formal>emptyList(),
                                                 WhereClauseopt,
                                                 Throwsopt,
                                                 Offersopt,
                                                 MethodBody);
                FlagsNode flags = md.flags();
                if (flags.flags().isStatic()) {
                    syntaxError("Unary operator with this parameter cannot be static.", md.position());
                    md = md.flags(flags.flags(flags.flags().clearStatic()));
                }
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 49:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 49: {
               //#line 1515 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1513 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1513 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1513 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(5);
                //#line 1513 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1513 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1513 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(8);
                //#line 1513 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1513 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1515 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(), ClosureCall.APPLY),
                                                 TypeParametersopt,
                                                 FormalParameters,
                                                 WhereClauseopt,
                                                 Throwsopt,
                                                 Offersopt,
                                                 MethodBody);
                FlagsNode flags = md.flags();
                if (flags.flags().isStatic()) {
                    syntaxError("Apply operator cannot be static.", md.position());
                    md = md.flags(flags.flags(flags.flags().clearStatic()));
                }
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 50:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 50: {
               //#line 1537 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1535 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1535 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1535 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(5);
                //#line 1535 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1535 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1535 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1535 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(12);
                //#line 1535 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(13);
                //#line 1535 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 1537 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(), SettableAssign.SET),
                                                 TypeParametersopt,
                                                 CollectionUtil.append(Collections.singletonList(fp2), FormalParameters),
                                                 WhereClauseopt,
                                                 Throwsopt,
                                                 Offersopt,
                                                 MethodBody);
                FlagsNode flags = md.flags();
                if (flags.flags().isStatic()) {
                    syntaxError("Set operator cannot be static.", md.position());
                    md = md.flags(flags.flags(flags.flags().clearStatic()));
                }
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 51:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Throwsopt Offersopt MethodBody
            //
            case 51: {
               //#line 1559 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1557 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1557 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1557 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1557 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1557 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1557 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(10);
                //#line 1557 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1557 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1559 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 Type,
                                                 nf.Id(pos(), Converter.operator_as),
                                                 TypeParametersopt,
                                                 Collections.<Formal>singletonList(fp1),
                                                 WhereClauseopt,
                                                 Throwsopt,
                                                 Offersopt, 
                                                 MethodBody);
                FlagsNode flags = md.flags();
                if (! flags.flags().isStatic()) {
                    syntaxError("Conversion operator must be static.", md.position());
                    md = md.flags(flags.flags(flags.flags().Static()));
                }
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 52:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 52: {
               //#line 1581 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1579 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1579 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1579 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1579 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1579 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1579 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(11);
                //#line 1579 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1579 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1581 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(), Converter.operator_as),
                                                 TypeParametersopt,
                                                 Collections.<Formal>singletonList(fp1),
                                                 WhereClauseopt,
                                                 Throwsopt,
                                                 Offersopt, 
                                                 MethodBody);
                FlagsNode flags = md.flags();
                if (! flags.flags().isStatic()) {
                    syntaxError("Conversion operator must be static.", md.position());
                    md = md.flags(flags.flags(flags.flags().Static()));
                }
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 53:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt HasResultTypeopt Throwsopt Offersopt MethodBody
            //
            case 53: {
               //#line 1603 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1601 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1601 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1601 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1601 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1601 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(8);
                //#line 1601 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(9);
                //#line 1601 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(10);
                //#line 1601 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1603 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(), Converter.implicit_operator_as),
                                                 TypeParametersopt,
                                                 Collections.<Formal>singletonList(fp1),
                                                 WhereClauseopt,
                                                 Throwsopt,
                                                 Offersopt,
                                                 MethodBody);
                FlagsNode flags = md.flags();
                if (! flags.flags().isStatic()) {
                    syntaxError("Conversion operator must be static.", md.position());
                    md = md.flags(flags.flags(flags.flags().Static()));
                }
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 54:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt MethodBody
            //
            case 54: {
               //#line 1626 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1624 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1624 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1624 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1624 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(4);
                //#line 1624 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1624 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(6);
                //#line 1624 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(7);
                //#line 1626 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers, X10Flags.PROPERTY),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 Identifier,
                                                 TypeParametersopt,
                                                 FormalParameters,
                                                 WhereClauseopt,
                                                 Collections.<TypeNode>emptyList(),
                                                 null, // offersOpt
                                                 MethodBody);
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 55:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier WhereClauseopt HasResultTypeopt MethodBody
            //
            case 55: {
               //#line 1643 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1641 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1641 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1641 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(3);
                //#line 1641 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 1641 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(5);
                //#line 1643 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers, X10Flags.PROPERTY),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 Identifier,
                                                 Collections.<TypeParamNode>emptyList(),
                                                 Collections.<Formal>emptyList(),
                                                 WhereClauseopt,
                                                 Collections.<TypeNode>emptyList(),
                                                 null, // offersOpt
                                                 MethodBody);
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 56:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 56: {
               //#line 1661 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1659 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 1659 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 1661 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 57:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 57: {
               //#line 1666 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1664 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 1664 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 1666 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 58:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 58: {
               //#line 1671 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1669 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1669 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(4);
                //#line 1669 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(6);
                //#line 1671 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 59:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 59: {
               //#line 1676 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1674 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1674 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(4);
                //#line 1674 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(6);
                //#line 1676 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 60:  NormalInterfaceDeclaration ::= Modifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 60: {
               //#line 1682 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1680 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1680 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1680 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParamsWithVarianceopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1680 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<PropertyDecl> Propertiesopt = (List<PropertyDecl>) getRhsSym(5);
                //#line 1680 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1680 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> ExtendsInterfacesopt = (List<TypeNode>) getRhsSym(7);
                //#line 1680 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1682 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkInterfaceModifiers(Modifiersopt);
                checkTypeName(Identifier);
                List<TypeParamNode> TypeParametersopt = TypeParamsWithVarianceopt;
                List<PropertyDecl> props = Propertiesopt;
                DepParameterExpr ci = WhereClauseopt;
                FlagsNode fn = extractFlags(modifiers, Flags.INTERFACE);
                ClassDecl cd = nf.X10ClassDecl(pos(),
                                               fn,
                                               Identifier,
                                               TypeParametersopt,
                                               props,
                                               ci,
                                               null,
                                               ExtendsInterfacesopt,
                                               InterfaceBody);
                cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(modifiers));
                setResult(cd);
                      break;
            }
    
            //
            // Rule 61:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 61: {
               //#line 1704 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1702 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1702 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(3);
                //#line 1702 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(5);
                //#line 1702 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1704 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 62:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 62: {
               //#line 1711 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1709 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1709 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1709 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(5);
                //#line 1709 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(7);
                //#line 1709 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1711 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1719 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1717 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1717 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1717 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(5);
                //#line 1717 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(7);
                //#line 1717 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1719 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1728 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1726 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 1726 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 1728 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 67:  FunctionType ::= TypeParametersopt ( FormalParameterListopt ) WhereClauseopt Throwsopt Offersopt => Type
            //
            case 67: {
               //#line 1738 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1736 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(1);
                //#line 1736 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(3);
                //#line 1736 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1736 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(6);
                //#line 1736 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(7);
                //#line 1736 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(9);
                //#line 1738 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeParametersopt, FormalParameterListopt, WhereClauseopt, Type, Throwsopt, Offersopt));
                      break;
            }
    
            //
            // Rule 69:  AnnotatedType ::= Type Annotations
            //
            case 69: {
               //#line 1751 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1749 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1749 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotations = (List<AnnotationNode>) getRhsSym(2);
                //#line 1751 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn.position(pos()));
                      break;
            }
    
            //
            // Rule 72:  ConstrainedType ::= ( Type )
            //
            case 72: {
               //#line 1761 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1759 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1761 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 74:  SimpleNamedType ::= TypeName
            //
            case 74: {
               //#line 1775 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1773 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1775 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(TypeName.toType());
                      break;
            }
    
            //
            // Rule 75:  SimpleNamedType ::= Primary . Identifier
            //
            case 75: {
               //#line 1780 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1778 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1778 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1780 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(nf.AmbTypeNode(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 76:  SimpleNamedType ::= DepNamedType . Identifier
            //
            case 76: {
               //#line 1785 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1783 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode DepNamedType = (TypeNode) getRhsSym(1);
                //#line 1783 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1785 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(nf.AmbTypeNode(pos(), DepNamedType, Identifier));
                      break;
            }
    
            //
            // Rule 77:  DepNamedType ::= SimpleNamedType DepParameters
            //
            case 77: {
               //#line 1791 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1789 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1789 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(2);
                //#line 1791 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false),
                                              new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 78:  DepNamedType ::= SimpleNamedType Arguments
            //
            case 78: {
               //#line 1800 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1798 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1798 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Arguments = (List<Expr>) getRhsSym(2);
                //#line 1800 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false),
                                              Arguments,
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 79:  DepNamedType ::= SimpleNamedType Arguments DepParameters
            //
            case 79: {
               //#line 1809 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1807 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1807 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Arguments = (List<Expr>) getRhsSym(2);
                //#line 1807 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(3);
                //#line 1809 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false),
                                              Arguments,
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 80:  DepNamedType ::= SimpleNamedType TypeArguments
            //
            case 80: {
               //#line 1818 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1816 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1816 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArguments = (List<TypeNode>) getRhsSym(2);
                //#line 1818 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 81:  DepNamedType ::= SimpleNamedType TypeArguments DepParameters
            //
            case 81: {
               //#line 1827 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1825 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1825 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArguments = (List<TypeNode>) getRhsSym(2);
                //#line 1825 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(3);
                //#line 1827 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 82:  DepNamedType ::= SimpleNamedType TypeArguments Arguments
            //
            case 82: {
               //#line 1836 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1834 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1834 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArguments = (List<TypeNode>) getRhsSym(2);
                //#line 1834 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Arguments = (List<Expr>) getRhsSym(3);
                //#line 1836 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1845 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1843 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1843 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArguments = (List<TypeNode>) getRhsSym(2);
                //#line 1843 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Arguments = (List<Expr>) getRhsSym(3);
                //#line 1843 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(4);
                //#line 1845 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1858 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1856 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> ExistentialListopt = (List<Formal>) getRhsSym(2);
                //#line 1856 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Conjunctionopt = (List<Expr>) getRhsSym(3);
                //#line 1858 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunctionopt));
                      break;
            }
    
            //
            // Rule 87:  DepParameters ::= ! PlaceType
            //
            case 87: {
               //#line 1863 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1861 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1863 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 88:  DepParameters ::= !
            //
            case 88: {
               //#line 1869 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1869 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 89:  DepParameters ::= ! PlaceType { ExistentialListopt Conjunction }
            //
            case 89: {
               //#line 1875 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1873 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1873 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> ExistentialListopt = (List<Formal>) getRhsSym(4);
                //#line 1873 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Conjunction = (List<Expr>) getRhsSym(5);
                //#line 1875 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 90:  DepParameters ::= ! { ExistentialListopt Conjunction }
            //
            case 90: {
               //#line 1881 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1879 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> ExistentialListopt = (List<Formal>) getRhsSym(3);
                //#line 1879 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Conjunction = (List<Expr>) getRhsSym(4);
                //#line 1881 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 91:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 91: {
               //#line 1889 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1887 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParamWithVarianceList = (List<TypeParamNode>) getRhsSym(2);
                //#line 1889 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 92:  TypeParameters ::= [ TypeParameterList ]
            //
            case 92: {
               //#line 1895 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1893 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParameterList = (List<TypeParamNode>) getRhsSym(2);
                //#line 1895 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 93:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 93: {
               //#line 1901 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1899 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(2);
                //#line 1901 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 94:  Conjunction ::= Expression
            //
            case 94: {
               //#line 1907 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1905 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1907 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Expr> l = new ArrayList<Expr>();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 95:  Conjunction ::= Conjunction , Expression
            //
            case 95: {
               //#line 1914 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1912 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Conjunction = (List<Expr>) getRhsSym(1);
                //#line 1912 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1914 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 96:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 96: {
               //#line 1920 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1918 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1918 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1920 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 97:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 97: {
               //#line 1925 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1923 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1923 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1925 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 98:  WhereClause ::= DepParameters
            //
            case 98: {
               //#line 1931 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1929 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1931 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 99:  Conjunctionopt ::= $Empty
            //
            case 99: {
               //#line 1937 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1937 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Expr> l = new ArrayList<Expr>();
                setResult(l);
                      break;
            }
      
            //
            // Rule 100:  Conjunctionopt ::= Conjunction
            //
            case 100: {
               //#line 1943 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Conjunction = (List<Expr>) getRhsSym(1);
                //#line 1943 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(Conjunction);
                      break;
            }
    
            //
            // Rule 101:  ExistentialListopt ::= $Empty
            //
            case 101: {
               //#line 1949 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1949 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList<Formal>());
                      break;
            }
      
            //
            // Rule 102:  ExistentialListopt ::= ExistentialList ;
            //
            case 102: {
               //#line 1954 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1952 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> ExistentialList = (List<Formal>) getRhsSym(1);
                //#line 1954 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 103:  ExistentialList ::= FormalParameter
            //
            case 103: {
               //#line 1960 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1958 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1960 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 104:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 104: {
               //#line 1967 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1965 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> ExistentialList = (List<Formal>) getRhsSym(1);
                //#line 1965 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1967 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 107:  NormalClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 107: {
               //#line 1978 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1976 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1976 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1976 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParamsWithVarianceopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1976 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<PropertyDecl> Propertiesopt = (List<PropertyDecl>) getRhsSym(5);
                //#line 1976 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1976 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1976 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Interfacesopt = (List<TypeNode>) getRhsSym(8);
                //#line 1976 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1978 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkClassModifiers(Modifiersopt);
                checkTypeName(Identifier);
                List<TypeParamNode> TypeParametersopt = TypeParamsWithVarianceopt;
                List<PropertyDecl> props = Propertiesopt;
                DepParameterExpr ci = WhereClauseopt;
                FlagsNode f = extractFlags(modifiers);
                List<AnnotationNode> annotations = extractAnnotations(modifiers);
                ClassDecl cd = nf.X10ClassDecl(pos(),
                                               f, Identifier, TypeParametersopt, props, ci,
                                               Superopt, Interfacesopt, ClassBody);
                cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 108:  StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 108: {
               //#line 1996 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1994 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1994 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1994 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParamsWithVarianceopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1994 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<PropertyDecl> Propertiesopt = (List<PropertyDecl>) getRhsSym(5);
                //#line 1994 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1994 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Interfacesopt = (List<TypeNode>) getRhsSym(7);
                //#line 1994 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1996 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkClassModifiers(Modifiersopt);
                checkTypeName(Identifier);
                List<TypeParamNode> TypeParametersopt = TypeParamsWithVarianceopt;
                List<PropertyDecl> props = Propertiesopt;
                DepParameterExpr ci = WhereClauseopt;
                ClassDecl cd = nf.X10ClassDecl(pos(getLeftSpan(), getRightSpan()),
                                               extractFlags(modifiers, X10Flags.STRUCT), Identifier,
                                               TypeParametersopt, props, ci, null, Interfacesopt, ClassBody);
                cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(modifiers));
                setResult(cd);
                      break;
            }
    
            //
            // Rule 109:  ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt ConstructorBody
            //
            case 109: {
               //#line 2011 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2009 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 2009 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 2009 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(5);
                //#line 2009 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 2009 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 2009 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(8);
                //#line 2009 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 2009 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(10);
                //#line 2011 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkConstructorModifiers(Modifiersopt);
                ConstructorDecl cd = nf.X10ConstructorDecl(pos(),
                                                           extractFlags(modifiers),
                                                           nf.Id(pos(getRhsFirstTokenIndex(3)), "this"),
                                                           HasResultTypeopt,
                                                           TypeParametersopt,
                                                           FormalParameters,
                                                           WhereClauseopt,
                                                           Throwsopt,
                                                           Offersopt,
                                                           ConstructorBody);
                cd = (ConstructorDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(modifiers));
                setResult(cd);
                     break;
            }
    
            //
            // Rule 110:  Super ::= extends ClassType
            //
            case 110: {
               //#line 2029 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2027 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 2029 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 111:  FieldKeyword ::= val
            //
            case 111: {
               //#line 2035 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2035 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 112:  FieldKeyword ::= var
            //
            case 112: {
               //#line 2040 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2040 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 113:  VarKeyword ::= val
            //
            case 113: {
               //#line 2048 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2048 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 114:  VarKeyword ::= var
            //
            case 114: {
               //#line 2053 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2053 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 115:  FieldDeclaration ::= Modifiersopt FieldKeyword FieldDeclarators ;
            //
            case 115: {
               //#line 2060 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2058 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 2058 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<FlagsNode> FieldKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 2058 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> FieldDeclarators = (List<Object[]>) getRhsSym(3);
                //#line 2060 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkFieldModifiers(Modifiersopt);
                FlagsNode fn = extractFlags(modifiers, FieldKeyword);
    
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                    for (Object[] o : FieldDeclarators)
                    {
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        if (name == null) name = nf.Id(pos, Name.makeFresh());
                        List<Id> exploded = (List<Id>) o[2];
                        TypeNode type = (TypeNode) o[3];
                        if (type == null) type = nf.UnknownTypeNode(name.position());
                        Expr init = (Expr) o[4];
                        FieldDecl fd = nf.FieldDecl(pos, fn,
                                           type, name, init);
                        fd = (FieldDecl) ((X10Ext) fd.ext()).annotations(extractAnnotations(modifiers));
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
               //#line 2085 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2083 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 2083 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> FieldDeclarators = (List<Object[]>) getRhsSym(2);
                //#line 2085 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkFieldModifiers(Modifiersopt);
                List<FlagsNode> FieldKeyword = Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL));
                FlagsNode fn = extractFlags(modifiers, FieldKeyword);
    
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                    for (Object[] o : FieldDeclarators)
                    {
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        if (name == null) name = nf.Id(pos, Name.makeFresh());
                        List<Id> exploded = (List<Id>) o[2];
                        TypeNode type = (TypeNode) o[3];
                        if (type == null) type = nf.UnknownTypeNode(name.position());
                        Expr init = (Expr) o[4];
                        FieldDecl fd = nf.FieldDecl(pos, fn,
                                           type, name, init);
                        fd = (FieldDecl) ((X10Ext) fd.ext()).annotations(extractAnnotations(modifiers));
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
               //#line 2117 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2115 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 2115 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt NonExpressionStatement = (Stmt) getRhsSym(2);
                //#line 2117 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                if (NonExpressionStatement.ext() instanceof X10Ext) {
                    NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations(Annotationsopt);
                }
                setResult(NonExpressionStatement.position(pos()));
                      break;
            }
    
            //
            // Rule 147:  OfferStatement ::= offer Expression ;
            //
            case 147: {
               //#line 2154 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2152 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2154 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Offer(pos(), Expression));
                      break;
            }
    
            //
            // Rule 148:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 148: {
               //#line 2160 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2158 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2158 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2160 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 149:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 149: {
               //#line 2166 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2164 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2164 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 2164 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 2166 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 150:  EmptyStatement ::= ;
            //
            case 150: {
               //#line 2172 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2172 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 151:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 151: {
               //#line 2178 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2176 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2176 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 2178 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 157:  ExpressionStatement ::= StatementExpression ;
            //
            case 157: {
               //#line 2190 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2188 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 2190 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 165:  AssertStatement ::= assert Expression ;
            //
            case 165: {
               //#line 2204 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2202 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2204 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 166:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 166: {
               //#line 2209 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2207 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 2207 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 2209 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 167:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 167: {
               //#line 2215 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2213 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2213 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<SwitchElement> SwitchBlock = (List<SwitchElement>) getRhsSym(5);
                //#line 2215 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 168:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 168: {
               //#line 2221 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2219 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> SwitchBlockStatementGroupsopt = (List<Stmt>) getRhsSym(2);
                //#line 2219 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Case> SwitchLabelsopt = (List<Case>) getRhsSym(3);
                //#line 2221 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 170:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 170: {
               //#line 2229 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2227 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<SwitchElement> SwitchBlockStatementGroups = (List<SwitchElement>) getRhsSym(1);
                //#line 2227 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<SwitchElement> SwitchBlockStatementGroup = (List<SwitchElement>) getRhsSym(2);
                //#line 2229 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 171:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 171: {
               //#line 2236 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2234 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<SwitchElement> SwitchLabels = (List<SwitchElement>) getRhsSym(1);
                //#line 2234 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatements = (List<Stmt>) getRhsSym(2);
                //#line 2236 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<SwitchElement> l = new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 172:  SwitchLabels ::= SwitchLabel
            //
            case 172: {
               //#line 2245 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2243 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 2245 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Case> l = new TypedList<Case>(new LinkedList<Case>(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 173:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 173: {
               //#line 2252 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2250 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<SwitchElement> SwitchLabels = (List<SwitchElement>) getRhsSym(1);
                //#line 2250 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 2252 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 174:  SwitchLabel ::= case ConstantExpression :
            //
            case 174: {
               //#line 2259 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2257 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 2259 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 175:  SwitchLabel ::= default :
            //
            case 175: {
               //#line 2264 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2264 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 176:  WhileStatement ::= while ( Expression ) Statement
            //
            case 176: {
               //#line 2270 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2268 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2268 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2270 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 177:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 177: {
               //#line 2276 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2274 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2274 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2276 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 180:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 180: {
               //#line 2285 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2283 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ForInit> ForInitopt = (List<ForInit>) getRhsSym(3);
                //#line 2283 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 2283 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ForUpdate> ForUpdateopt = (List<ForUpdate>) getRhsSym(7);
                //#line 2283 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 2285 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 182:  ForInit ::= LocalVariableDeclaration
            //
            case 182: {
               //#line 2292 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2290 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<LocalDecl> LocalVariableDeclaration = (List<LocalDecl>) getRhsSym(1);
                //#line 2292 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ForInit> l = new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 184:  StatementExpressionList ::= StatementExpression
            //
            case 184: {
               //#line 2302 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2300 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 2302 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Eval> l = new TypedList<Eval>(new LinkedList<Eval>(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 185:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 185: {
               //#line 2309 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2307 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Eval> StatementExpressionList = (List<Eval>) getRhsSym(1);
                //#line 2307 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 2309 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 186:  BreakStatement ::= break Identifieropt ;
            //
            case 186: {
               //#line 2315 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2313 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 2315 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 187:  ContinueStatement ::= continue Identifieropt ;
            //
            case 187: {
               //#line 2321 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2319 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 2321 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 188:  ReturnStatement ::= return Expressionopt ;
            //
            case 188: {
               //#line 2327 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2325 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 2327 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 189:  ThrowStatement ::= throw Expression ;
            //
            case 189: {
               //#line 2333 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2331 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2333 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 190:  TryStatement ::= try Block Catches
            //
            case 190: {
               //#line 2339 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2337 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2337 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Catch> Catches = (List<Catch>) getRhsSym(3);
                //#line 2339 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 191:  TryStatement ::= try Block Catchesopt Finally
            //
            case 191: {
               //#line 2344 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2342 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2342 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Catch> Catchesopt = (List<Catch>) getRhsSym(3);
                //#line 2342 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 2344 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 192:  Catches ::= CatchClause
            //
            case 192: {
               //#line 2350 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2348 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 2350 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Catch> l = new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 193:  Catches ::= Catches CatchClause
            //
            case 193: {
               //#line 2357 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2355 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Catch> Catches = (List<Catch>) getRhsSym(1);
                //#line 2355 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 2357 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 194:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 194: {
               //#line 2364 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2362 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2362 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2364 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 195:  Finally ::= finally Block
            //
            case 195: {
               //#line 2370 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2368 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2370 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 196:  ClockedClause ::= clocked ( ClockList )
            //
            case 196: {
               //#line 2376 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2374 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ClockList = (List<Expr>) getRhsSym(3);
                //#line 2376 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 197:  AsyncStatement ::= async ClockedClauseopt Statement
            //
            case 197: {
               //#line 2383 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2381 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ClockedClauseopt = (List<Expr>) getRhsSym(2);
                //#line 2381 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2383 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 198:  AsyncStatement ::= clocked async Statement
            //
            case 198: {
               //#line 2388 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2386 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2388 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), Statement, true));
                      break;
            }
    
            //
            // Rule 199:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 199: {
               //#line 2395 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2393 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2393 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2395 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 200:  AtomicStatement ::= atomic Statement
            //
            case 200: {
               //#line 2401 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2399 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2401 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 201:  WhenStatement ::= when ( Expression ) Statement
            //
            case 201: {
               //#line 2408 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2406 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2406 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2408 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 202:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 202: {
               //#line 2413 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2411 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 2411 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 2411 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 2411 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 2413 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 203:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 203: {
               //#line 2420 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2418 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2418 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2418 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ClockedClauseopt = (List<Expr>) getRhsSym(7);
                //#line 2418 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2420 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                if (! fn.flags().isFinal()) {
                    syntaxError("Enhanced foreach loop may not have var loop index" + LoopIndex, LoopIndex.position());
                    fn = fn.flags(fn.flags().Final());
                    LoopIndex = LoopIndex.flags(fn);
                }
                setResult(nf.ForEach(pos(),
                              LoopIndex,
                              Expression,
                              ClockedClauseopt,
                              Statement));
                      break;
            }
    
            //
            // Rule 204:  ForEachStatement ::= clocked foreach ( LoopIndex in Expression ) Statement
            //
            case 204: {
               //#line 2435 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2433 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(4);
                //#line 2433 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(6);
                //#line 2433 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2435 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                if (! fn.flags().isFinal()) {
                    syntaxError("Enhanced foreach loop cannot have var loop index" + LoopIndex, LoopIndex.position());
                    fn = fn.flags(fn.flags().Final());
                    LoopIndex = LoopIndex.flags(fn);
                }
                setResult(nf.ForEach(pos(),
                              LoopIndex,
                              Expression,
                              Statement));
                      break;
            }
    
            //
            // Rule 205:  ForEachStatement ::= foreach ( Expression ) Statement
            //
            case 205: {
               //#line 2449 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2447 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2447 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2449 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Id name = nf.Id(pos(), Name.makeFresh());
                TypeNode type = nf.UnknownTypeNode(pos());
                setResult(nf.ForEach(pos(),
                        nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
                        Expression,
                        new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                        Statement));
                      break;
            }
    
            //
            // Rule 206:  ForEachStatement ::= clocked foreach ( Expression ) Statement
            //
            case 206: {
               //#line 2460 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2458 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 2458 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 2460 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Id name = nf.Id(pos(), Name.makeFresh());
                TypeNode type = nf.UnknownTypeNode(pos());
                setResult(nf.ForEach(pos(),
                        nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
                        Expression,
                        Statement));
                      break;
            }
    
            //
            // Rule 207:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 207: {
               //#line 2471 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2469 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2469 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2469 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ClockedClauseopt = (List<Expr>) getRhsSym(7);
                //#line 2469 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2471 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                if (! fn.flags().isFinal()) {
                    syntaxError("Enhanced ateach loop may not have var loop index" + LoopIndex, LoopIndex.position());
                    fn = fn.flags(fn.flags().Final());
                    LoopIndex = LoopIndex.flags(fn);
                }
                setResult(nf.AtEach(pos(),
                             LoopIndex,
                             Expression,
                             ClockedClauseopt,
                             Statement));
                      break;
            }
    
            //
            // Rule 208:  AtEachStatement ::= clocked ateach ( LoopIndex in Expression ) Statement
            //
            case 208: {
               //#line 2486 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2484 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(4);
                //#line 2484 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(6);
                //#line 2484 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2486 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                if (! fn.flags().isFinal()) {
                    syntaxError("Enhanced ateach loop may not have var loop index" + LoopIndex, LoopIndex.position());
                    fn = fn.flags(fn.flags().Final());
                    LoopIndex = LoopIndex.flags(fn);
                }
                setResult(nf.AtEach(pos(),
                             LoopIndex,
                             Expression,
                             Statement));
                      break;
            }
    
            //
            // Rule 209:  AtEachStatement ::= ateach ( Expression ) Statement
            //
            case 209: {
               //#line 2500 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2498 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2498 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2500 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Id name = nf.Id(pos(), Name.makeFresh());
                TypeNode type = nf.UnknownTypeNode(pos());
                setResult(nf.AtEach(pos(),
                        nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
                        Expression,
                        new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                        Statement));
                      break;
            }
    
            //
            // Rule 210:  AtEachStatement ::= clocked ateach ( Expression ) Statement
            //
            case 210: {
               //#line 2511 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2509 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 2509 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 2511 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Id name = nf.Id(pos(), Name.makeFresh());
                TypeNode type = nf.UnknownTypeNode(pos());
                setResult(nf.AtEach(pos(),
                        nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
                        Expression,
                        Statement));
                      break;
            }
    
            //
            // Rule 211:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 211: {
               //#line 2521 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2519 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2519 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2519 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 2521 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FlagsNode fn = LoopIndex.flags();
                if (! fn.flags().isFinal()) {
                    syntaxError("Enhanced for loop may not have var loop index" + LoopIndex, LoopIndex.position());
                    fn = fn.flags(fn.flags().Final());
                    LoopIndex = LoopIndex.flags(fn);
                }
                setResult(nf.ForLoop(pos(),
                        LoopIndex,
                        Expression,
                        Statement));
                      break;
            }
    
            //
            // Rule 212:  EnhancedForStatement ::= for ( Expression ) Statement
            //
            case 212: {
               //#line 2535 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2533 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2533 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2535 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Id name = nf.Id(pos(), Name.makeFresh());
                TypeNode type = nf.UnknownTypeNode(pos());
                setResult(nf.ForLoop(pos(),
                        nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
                        Expression,
                        Statement));
                      break;
            }
    
            //
            // Rule 213:  FinishStatement ::= finish Statement
            //
            case 213: {
               //#line 2547 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2545 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2547 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement, false));
                      break;
            }
    
            //
            // Rule 214:  FinishStatement ::= clocked finish Statement
            //
            case 214: {
               //#line 2552 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2550 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2552 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement, true));
                      break;
            }
    
            //
            // Rule 215:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 215: {
               //#line 2557 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2555 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 2557 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 217:  NextStatement ::= next ;
            //
            case 217: {
               //#line 2565 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2565 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 218:  ResumeStatement ::= resume ;
            //
            case 218: {
               //#line 2571 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2571 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Resume(pos()));
                      break;
            }
    
            //
            // Rule 219:  ClockList ::= Clock
            //
            case 219: {
               //#line 2577 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2575 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 2577 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 220:  ClockList ::= ClockList , Clock
            //
            case 220: {
               //#line 2584 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2582 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ClockList = (List<Expr>) getRhsSym(1);
                //#line 2582 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 2584 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 221:  Clock ::= Expression
            //
            case 221: {
               //#line 2592 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2590 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2592 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Expression);
                      break;
            }
    
            //
            // Rule 223:  CastExpression ::= ExpressionName
            //
            case 223: {
               //#line 2605 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2603 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 2605 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 224:  CastExpression ::= CastExpression as Type
            //
            case 224: {
               //#line 2610 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2608 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2608 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2610 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 225:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 225: {
               //#line 2617 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2615 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2617 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 226:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 226: {
               //#line 2624 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2622 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParamWithVarianceList = (List<TypeParamNode>) getRhsSym(1);
                //#line 2622 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2624 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 227:  TypeParameterList ::= TypeParameter
            //
            case 227: {
               //#line 2631 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2629 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2631 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 228:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 228: {
               //#line 2638 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2636 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParameterList = (List<TypeParamNode>) getRhsSym(1);
                //#line 2636 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2638 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 229:  TypeParamWithVariance ::= Identifier
            //
            case 229: {
               //#line 2645 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2643 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2645 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 230:  TypeParamWithVariance ::= + Identifier
            //
            case 230: {
               //#line 2650 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2648 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2650 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 231:  TypeParamWithVariance ::= - Identifier
            //
            case 231: {
               //#line 2655 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2653 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2655 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 232:  TypeParameter ::= Identifier
            //
            case 232: {
               //#line 2661 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2659 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2661 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 233:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 233: {
               //#line 2686 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2684 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2684 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2686 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 234:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Throwsopt Offersopt => ClosureBody
            //
            case 234: {
               //#line 2692 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2690 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(1);
                //#line 2690 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2690 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2690 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Throwsopt = (List<TypeNode>) getRhsSym(4);
                //#line 2690 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(5);
                //#line 2690 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(7);
                //#line 2692 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt, Throwsopt, ClosureBody));
                      break;
            }
    
            //
            // Rule 235:  LastExpression ::= Expression
            //
            case 235: {
               //#line 2699 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2697 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2699 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 236:  ClosureBody ::= ConditionalExpression
            //
            case 236: {
               //#line 2705 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2703 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 2705 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), ConditionalExpression, true)));
                      break;
            }
    
            //
            // Rule 237:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 237: {
               //#line 2710 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2708 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 2708 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatementsopt = (List<Stmt>) getRhsSym(3);
                //#line 2708 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2710 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 238:  ClosureBody ::= Annotationsopt Block
            //
            case 238: {
               //#line 2720 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2718 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 2718 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2720 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b.position(pos()));
                      break;
            }
    
            //
            // Rule 239:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 239: {
               //#line 2729 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2727 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2727 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2729 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 240:  FinishExpression ::= finish ( Expression ) Block
            //
            case 240: {
               //#line 2735 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2733 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2733 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2735 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FinishExpr(pos(), Expression, Block));
                      break;
            }
    
            //
            // Rule 241:  WhereClauseopt ::= $Empty
            //
            case 241:
                setResult(null);
                break;

            //
            // Rule 243:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 243:
                setResult(null);
                break;

            //
            // Rule 245:  ClockedClauseopt ::= $Empty
            //
            case 245: {
               //#line 2783 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2783 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 247:  identifier ::= IDENTIFIER$ident
            //
            case 247: {
               //#line 2794 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2792 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2794 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 248:  TypeName ::= Identifier
            //
            case 248: {
               //#line 2801 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2799 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2801 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 249:  TypeName ::= TypeName . Identifier
            //
            case 249: {
               //#line 2806 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2804 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2804 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2806 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 251:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 251: {
               //#line 2818 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2816 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentList = (List<TypeNode>) getRhsSym(2);
                //#line 2818 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 252:  TypeArgumentList ::= Type
            //
            case 252: {
               //#line 2825 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2823 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2825 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> l = new ArrayList<TypeNode>();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 253:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 253: {
               //#line 2832 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2830 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentList = (List<TypeNode>) getRhsSym(1);
                //#line 2830 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2832 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 254:  PackageName ::= Identifier
            //
            case 254: {
               //#line 2842 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2840 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2842 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 255:  PackageName ::= PackageName . Identifier
            //
            case 255: {
               //#line 2847 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2845 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2845 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2847 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 256:  ExpressionName ::= Identifier
            //
            case 256: {
               //#line 2863 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2861 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2863 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 257:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 257: {
               //#line 2868 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2866 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2866 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2868 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 258:  MethodName ::= Identifier
            //
            case 258: {
               //#line 2878 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2876 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2878 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 259:  MethodName ::= AmbiguousName . Identifier
            //
            case 259: {
               //#line 2883 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2881 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2881 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2883 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 260:  PackageOrTypeName ::= Identifier
            //
            case 260: {
               //#line 2893 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2891 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2893 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 261:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 261: {
               //#line 2898 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2896 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2896 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2898 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 262:  AmbiguousName ::= Identifier
            //
            case 262: {
               //#line 2908 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2906 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2908 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 263:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 263: {
               //#line 2913 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2911 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2911 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2913 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 264:  CompilationUnit ::= PackageDeclarationopt TypeDeclarationsopt
            //
            case 264: {
               //#line 2925 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2923 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2923 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) getRhsSym(2);
                //#line 2925 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                // Add import x10.lang.* by default.
//                    int token_pos = (ImportDeclarationsopt.size() == 0
//                                       ? TypeDeclarationsopt.size() == 0
//                                               ? prsStream.getSize() - 1
//                                               : prsStream.getPrevious(getRhsFirstTokenIndex(2))
//                                     : getRhsLastTokenIndex($ImportDeclarationsopt)
//                                );
//                    Import x10LangImport = 
//                    nf.Import(pos(token_pos), Import.PACKAGE, QName.make("x10.lang"));
//                    ImportDeclarationsopt.add(x10LangImport);
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                                        PackageDeclarationopt,
                                        new TypedList<Import>(new LinkedList<Import>(), Import.class, false),
                                        TypeDeclarationsopt));
                      break;
            }
    
            //
            // Rule 265:  CompilationUnit ::= PackageDeclarationopt ImportDeclarations TypeDeclarationsopt
            //
            case 265: {
               //#line 2943 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Import> ImportDeclarations = (List<Import>) getRhsSym(2);
                //#line 2941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) getRhsSym(3);
                //#line 2943 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                                        PackageDeclarationopt,
                                        ImportDeclarations,
                                        TypeDeclarationsopt));
                      break;
            }
    
            //
            // Rule 266:  CompilationUnit ::= ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 266: {
               //#line 2951 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2949 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Import> ImportDeclarations = (List<Import>) getRhsSym(1);
                //#line 2949 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PackageNode misplacedPackageDeclaration = (PackageNode) getRhsSym(2);
                //#line 2949 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Import> misplacedImportDeclarations = (List<Import>) getRhsSym(3);
                //#line 2949 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) getRhsSym(4);
                //#line 2951 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                syntaxError("Misplaced package declaration", misplacedPackageDeclaration.position());
                ImportDeclarations.addAll(misplacedImportDeclarations); // merge the two import lists
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                                        misplacedPackageDeclaration,
                                        ImportDeclarations,
                                        TypeDeclarationsopt));
                      break;
            }
    
            //
            // Rule 267:  CompilationUnit ::= PackageDeclaration ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 267: {
               //#line 2961 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2959 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclaration = (PackageNode) getRhsSym(1);
                //#line 2959 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Import> ImportDeclarations = (List<Import>) getRhsSym(2);
                //#line 2959 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PackageNode misplacedPackageDeclaration = (PackageNode) getRhsSym(3);
                //#line 2959 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Import> misplacedImportDeclarations = (List<Import>) getRhsSym(4);
                //#line 2959 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) getRhsSym(5);
                //#line 2961 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                syntaxError("Misplaced package declaration, ignoring", misplacedPackageDeclaration.position());
                ImportDeclarations.addAll(misplacedImportDeclarations); // merge the two import lists
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                                        PackageDeclaration,
                                        ImportDeclarations,
                                        TypeDeclarationsopt));
                      break;
            }
    
            //
            // Rule 268:  ImportDeclarations ::= ImportDeclaration
            //
            case 268: {
               //#line 2972 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2970 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2972 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Import> l = new TypedList<Import>(new LinkedList<Import>(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 269:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 269: {
               //#line 2979 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2977 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Import> ImportDeclarations = (List<Import>) getRhsSym(1);
                //#line 2977 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2979 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 270:  TypeDeclarations ::= TypeDeclaration
            //
            case 270: {
               //#line 2987 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2985 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2987 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TopLevelDecl> l = new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 271:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 271: {
               //#line 2995 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2993 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarations = (List<TopLevelDecl>) getRhsSym(1);
                //#line 2993 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2995 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 272:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 272: {
               //#line 3003 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3001 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 3001 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 3003 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn.position(pos()));
                      break;
            }
    
            //
            // Rule 275:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 275: {
               //#line 3017 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3015 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 3017 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 276:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 276: {
               //#line 3023 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3021 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 3023 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 280:  TypeDeclaration ::= ;
            //
            case 280: {
               //#line 3038 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3038 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 281:  Interfaces ::= implements InterfaceTypeList
            //
            case 281: {
               //#line 3155 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3153 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> InterfaceTypeList = (List<TypeNode>) getRhsSym(2);
                //#line 3155 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 282:  InterfaceTypeList ::= Type
            //
            case 282: {
               //#line 3161 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3159 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 3161 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 283:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 283: {
               //#line 3168 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3166 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> InterfaceTypeList = (List<TypeNode>) getRhsSym(1);
                //#line 3166 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3168 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 284:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 284: {
               //#line 3178 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3176 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> ClassBodyDeclarationsopt = (List<ClassMember>) getRhsSym(2);
                //#line 3178 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 286:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 286: {
               //#line 3185 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3183 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> ClassBodyDeclarations = (List<ClassMember>) getRhsSym(1);
                //#line 3183 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> ClassBodyDeclaration = (List<ClassMember>) getRhsSym(2);
                //#line 3185 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 288:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 288: {
               //#line 3207 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3205 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 3207 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 290:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 290: {
               //#line 3216 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3214 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3216 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 291:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 291: {
               //#line 3223 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3221 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3223 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 292:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 292: {
               //#line 3230 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3228 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3230 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 293:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 293: {
               //#line 3237 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3235 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3237 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 294:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 294: {
               //#line 3244 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3242 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3244 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 295:  ClassMemberDeclaration ::= ;
            //
            case 295: {
               //#line 3251 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3251 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 296:  FormalDeclarators ::= FormalDeclarator
            //
            case 296: {
               //#line 3258 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3256 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 3258 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 297:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 297: {
               //#line 3265 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3263 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> FormalDeclarators = (List<Object[]>) getRhsSym(1);
                //#line 3263 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 3265 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 298:  FieldDeclarators ::= FieldDeclarator
            //
            case 298: {
               //#line 3272 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3270 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 3272 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 299:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 299: {
               //#line 3279 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3277 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> FieldDeclarators = (List<Object[]>) getRhsSym(1);
                //#line 3277 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 3279 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 300:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 300: {
               //#line 3287 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3285 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 3287 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 301:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 301: {
               //#line 3294 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3292 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> VariableDeclaratorsWithType = (List<Object[]>) getRhsSym(1);
                //#line 3292 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 3294 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 302:  VariableDeclarators ::= VariableDeclarator
            //
            case 302: {
               //#line 3301 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3299 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 3301 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 303:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 303: {
               //#line 3308 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3306 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> VariableDeclarators = (List<Object[]>) getRhsSym(1);
                //#line 3306 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 3308 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 305:  ResultType ::= : Type
            //
            case 305: {
               //#line 3364 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3362 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3364 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 306:  HasResultType ::= : Type
            //
            case 306: {
               //#line 3369 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3367 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3369 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 307:  HasResultType ::= <: Type
            //
            case 307: {
               //#line 3374 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3372 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3374 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.HasType(Type));
                      break;
            }
    
            //
            // Rule 308:  FormalParameterList ::= FormalParameter
            //
            case 308: {
               //#line 3389 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3387 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 3389 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 309:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 309: {
               //#line 3396 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3394 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterList = (List<Formal>) getRhsSym(1);
                //#line 3394 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 3396 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 310:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 310: {
               //#line 3402 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3400 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3400 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3402 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 311:  LoopIndexDeclarator ::= [ IdentifierList ] HasResultTypeopt
            //
            case 311: {
               //#line 3407 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3405 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(2);
                //#line 3405 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3407 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 312:  LoopIndexDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt
            //
            case 312: {
               //#line 3412 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3410 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3410 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(3);
                //#line 3410 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3412 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 313:  LoopIndex ::= Modifiersopt LoopIndexDeclarator
            //
            case 313: {
               //#line 3418 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3416 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 3416 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 3418 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            List<Node> modifiers = checkVariableModifiers(Modifiersopt);
            Formal f;
            FlagsNode fn = extractFlags(modifiers, Flags.FINAL);
            Object[] o = LoopIndexDeclarator;
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
            boolean unnamed = name == null;
            if (name == null) name = nf.Id(pos, Name.makeFresh());
            List<Id> exploded = (List<Id>) o[2];
            DepParameterExpr guard = (DepParameterExpr) o[3];
            TypeNode type = (TypeNode) o[4];
            if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
            List<Formal> explodedFormals = new ArrayList<Formal>();
            for (Id id : exploded) {
                explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(id.position()), id));
            }
            f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
            setResult(f);
                      break;
            }
    
            //
            // Rule 314:  LoopIndex ::= Modifiersopt VarKeyword LoopIndexDeclarator
            //
            case 314: {
               //#line 3441 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3439 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 3439 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<FlagsNode> VarKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 3439 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 3441 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            List<Node> modifiers = checkVariableModifiers(Modifiersopt);
            Formal f;
            FlagsNode fn = extractFlags(modifiers, VarKeyword);
            Object[] o = LoopIndexDeclarator;
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
            boolean unnamed = name == null;
            if (name == null) name = nf.Id(pos, Name.makeFresh());
            List<Id> exploded = (List<Id>) o[2];
            DepParameterExpr guard = (DepParameterExpr) o[3];
            TypeNode type = (TypeNode) o[4];
            if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
            List<Formal> explodedFormals = new ArrayList<Formal>();
            for (Id id : exploded) {
                explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(id.position()), id));
            }
            f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
            setResult(f);
                      break;
            }
    
            //
            // Rule 315:  FormalParameter ::= Modifiersopt FormalDeclarator
            //
            case 315: {
               //#line 3465 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3463 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 3463 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 3465 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            List<Node> modifiers = checkVariableModifiers(Modifiersopt);
            Formal f;
            FlagsNode fn = extractFlags(modifiers, Flags.FINAL);
            Object[] o = FormalDeclarator;
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
            boolean unnamed = name == null;
            if (name == null) name = nf.Id(pos, Name.makeFresh());
            List<Id> exploded = (List<Id>) o[2];
            DepParameterExpr guard = (DepParameterExpr) o[3];
            TypeNode type = (TypeNode) o[4];
            if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
            Expr init = (Expr) o[5];
            List<Formal> explodedFormals = new ArrayList<Formal>();
            for (Id id : exploded) {
                explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(id.position()), id));
            }
            f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
            setResult(f);
                      break;
            }
    
            //
            // Rule 316:  FormalParameter ::= Modifiersopt VarKeyword FormalDeclarator
            //
            case 316: {
               //#line 3489 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3487 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 3487 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<FlagsNode> VarKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 3487 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 3489 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            List<Node> modifiers = checkVariableModifiers(Modifiersopt);
            Formal f;
            FlagsNode fn = extractFlags(modifiers, VarKeyword);
            Object[] o = FormalDeclarator;
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
            boolean unnamed = name == null;
            if (name == null) name = nf.Id(pos, Name.makeFresh());
            List<Id> exploded = (List<Id>) o[2];
            DepParameterExpr guard = (DepParameterExpr) o[3];
            TypeNode type = (TypeNode) o[4];
            if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
            Expr init = (Expr) o[5];
            List<Formal> explodedFormals = new ArrayList<Formal>();
            for (Id id : exploded) {
                explodedFormals.add(nf.Formal(id.position(), fn, nf.UnknownTypeNode(id.position()), id));
            }
            f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
            f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
            setResult(f);
                      break;
            }
    
            //
            // Rule 317:  FormalParameter ::= Type
            //
            case 317: {
               //#line 3513 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3511 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 3513 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh("id$")), Collections.<Formal>emptyList(), true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 318:  Throws ::= throws ExceptionTypeList
            //
            case 318: {
               //#line 3654 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3652 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> ExceptionTypeList = (List<TypeNode>) getRhsSym(2);
                //#line 3654 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExceptionTypeList);
                      break;
            }
    
            //
            // Rule 319:  Offers ::= offers Type
            //
            case 319: {
               //#line 3659 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3657 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3659 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 320:  ExceptionTypeList ::= ExceptionType
            //
            case 320: {
               //#line 3665 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3663 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3665 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 321:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 321: {
               //#line 3672 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3670 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> ExceptionTypeList = (List<TypeNode>) getRhsSym(1);
                //#line 3670 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3672 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 323:  MethodBody ::= = LastExpression ;
            //
            case 323: {
               //#line 3680 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3678 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3680 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 324:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 324: {
               //#line 3685 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3683 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(2);
                //#line 3683 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatementsopt = (List<Stmt>) getRhsSym(4);
                //#line 3683 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3685 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult((Block) ((X10Ext) nf.Block(pos(),l).ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 325:  MethodBody ::= = Annotationsopt Block
            //
            case 325: {
               //#line 3693 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3691 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(2);
                //#line 3691 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3693 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 326:  MethodBody ::= Annotationsopt Block
            //
            case 326: {
               //#line 3698 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3696 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 3696 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3698 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 327:  MethodBody ::= ;
            //
            case 327:
                setResult(null);
                break;

            //
            // Rule 328:  ConstructorBody ::= = ConstructorBlock
            //
            case 328: {
               //#line 3769 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3767 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3769 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 329:  ConstructorBody ::= ConstructorBlock
            //
            case 329: {
               //#line 3774 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3772 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3774 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 330:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 330: {
               //#line 3779 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3777 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3779 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 331:  ConstructorBody ::= = AssignPropertyCall
            //
            case 331: {
               //#line 3786 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3784 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3786 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 332:  ConstructorBody ::= ;
            //
            case 332:
                setResult(null);
                break;

            //
            // Rule 333:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 333: {
               //#line 3796 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3794 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3794 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatementsopt = (List<Stmt>) getRhsSym(3);
                //#line 3796 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                if (ExplicitConstructorInvocationopt != null)
                {
                    l.add(ExplicitConstructorInvocationopt);
                }
                l.addAll(BlockStatementsopt);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 334:  Arguments ::= ( ArgumentListopt )
            //
            case 334: {
               //#line 3808 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3806 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(2);
                //#line 3808 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 336:  ExtendsInterfaces ::= extends Type
            //
            case 336: {
               //#line 3865 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3863 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3865 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 337:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 337: {
               //#line 3872 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3870 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> ExtendsInterfaces = (List<TypeNode>) getRhsSym(1);
                //#line 3870 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3872 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 338:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 338: {
               //#line 3881 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3879 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> InterfaceMemberDeclarationsopt = (List<ClassMember>) getRhsSym(2);
                //#line 3881 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 340:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 340: {
               //#line 3888 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3886 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> InterfaceMemberDeclarations = (List<ClassMember>) getRhsSym(1);
                //#line 3886 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> InterfaceMemberDeclaration = (List<ClassMember>) getRhsSym(2);
                //#line 3888 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 341:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 341: {
               //#line 3895 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3893 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3895 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 342:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 342: {
               //#line 3902 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3900 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3902 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 343:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 343: {
               //#line 3909 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3907 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> FieldDeclaration = (List<ClassMember>) getRhsSym(1);
                //#line 3909 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 344:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 344: {
               //#line 3916 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3914 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3916 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 345:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 345: {
               //#line 3923 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3921 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3923 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 346:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 346: {
               //#line 3930 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3928 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3930 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 347:  InterfaceMemberDeclaration ::= ;
            //
            case 347: {
               //#line 3937 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3937 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.<ClassMember>emptyList());
                      break;
            }
    
            //
            // Rule 348:  Annotations ::= Annotation
            //
            case 348: {
               //#line 3943 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3941 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3943 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<AnnotationNode> l = new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 349:  Annotations ::= Annotations Annotation
            //
            case 349: {
               //#line 3950 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3948 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotations = (List<AnnotationNode>) getRhsSym(1);
                //#line 3948 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3950 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 350:  Annotation ::= @ NamedType
            //
            case 350: {
               //#line 3956 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3954 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3956 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 351:  Identifier ::= identifier
            //
            case 351: {
               //#line 3971 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3969 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3971 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 352:  Block ::= { BlockStatementsopt }
            //
            case 352: {
               //#line 4007 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4005 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatementsopt = (List<Stmt>) getRhsSym(2);
                //#line 4007 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 353:  BlockStatements ::= BlockStatement
            //
            case 353: {
               //#line 4013 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4011 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatement = (List<Stmt>) getRhsSym(1);
                //#line 4013 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 354:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 354: {
               //#line 4020 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4018 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatements = (List<Stmt>) getRhsSym(1);
                //#line 4018 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatement = (List<Stmt>) getRhsSym(2);
                //#line 4020 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 356:  BlockStatement ::= ClassDeclaration
            //
            case 356: {
               //#line 4028 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4026 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 4028 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 357:  BlockStatement ::= TypeDefDeclaration
            //
            case 357: {
               //#line 4035 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4033 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 4035 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 358:  BlockStatement ::= Statement
            //
            case 358: {
               //#line 4042 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4040 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 4042 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 359:  IdentifierList ::= Identifier
            //
            case 359: {
               //#line 4050 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4048 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4050 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Id> l = new TypedList<Id>(new LinkedList<Id>(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 360:  IdentifierList ::= IdentifierList , Identifier
            //
            case 360: {
               //#line 4057 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4055 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(1);
                //#line 4055 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4057 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 361:  FormalDeclarator ::= Identifier ResultType
            //
            case 361: {
               //#line 4063 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4061 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4061 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 4063 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, ResultType, null });
                      break;
            }
    
            //
            // Rule 362:  FormalDeclarator ::= [ IdentifierList ] ResultType
            //
            case 362: {
               //#line 4068 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4066 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(2);
                //#line 4066 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 4068 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 363:  FormalDeclarator ::= Identifier [ IdentifierList ] ResultType
            //
            case 363: {
               //#line 4073 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4071 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4071 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(3);
                //#line 4071 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 4073 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 364:  FieldDeclarator ::= Identifier HasResultType
            //
            case 364: {
               //#line 4079 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4077 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4077 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 4079 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), HasResultType, null });
                      break;
            }
    
            //
            // Rule 365:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 365: {
               //#line 4084 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4082 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4082 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 4082 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 4084 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 366:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 366: {
               //#line 4090 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4088 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4088 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 4088 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 4090 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 367:  VariableDeclarator ::= [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 367: {
               //#line 4095 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4093 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(2);
                //#line 4093 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 4093 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 4095 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 368:  VariableDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 368: {
               //#line 4100 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4098 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4098 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(3);
                //#line 4098 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 4098 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 4100 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 369:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 369: {
               //#line 4106 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4104 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4104 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 4104 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 4106 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 370:  VariableDeclaratorWithType ::= [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 370: {
               //#line 4111 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4109 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(2);
                //#line 4109 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(4);
                //#line 4109 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 4111 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 371:  VariableDeclaratorWithType ::= Identifier [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 371: {
               //#line 4116 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4114 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4114 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(3);
                //#line 4114 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(5);
                //#line 4114 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 4116 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 373:  LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
            //
            case 373: {
               //#line 4124 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4122 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 4122 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<FlagsNode> VarKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 4122 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> VariableDeclarators = (List<Object[]>) getRhsSym(3);
                //#line 4124 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkVariableModifiers(Modifiersopt);
                FlagsNode fn = extractFlags(modifiers, VarKeyword);
    
                List<LocalDecl> l = new TypedList<LocalDecl>(new LinkedList<LocalDecl>(), LocalDecl.class, false);
                    for (Object[] o : VariableDeclarators)
                    {
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        if (name == null) name = nf.Id(pos, Name.makeFresh());
                        List<Id> exploded = (List<Id>) o[2];
                        DepParameterExpr guard = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                        if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                        Expr init = (Expr) o[5];
                        LocalDecl ld = nf.LocalDecl(pos, fn,
                                           type, name, init);
                        ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(modifiers));
                        int index = 0;
                        l.add(ld);
                        for (Id id : exploded) {
                            TypeNode tni = nf.UnknownTypeNode(id.position());
                            l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(JPGPosition.COMPILER_GENERATED, nf.Local(JPGPosition.COMPILER_GENERATED, name),  Collections.<Expr>singletonList(nf.IntLit(JPGPosition.COMPILER_GENERATED, IntLit.INT, index))) : null));
                            index++;
                        }
                    }
                setResult(l);
                      break;
            }
    
            //
            // Rule 374:  LocalVariableDeclaration ::= Modifiersopt VariableDeclaratorsWithType
            //
            case 374: {
               //#line 4154 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4152 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 4152 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> VariableDeclaratorsWithType = (List<Object[]>) getRhsSym(2);
                //#line 4154 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkVariableModifiers(Modifiersopt);
                FlagsNode fn = extractFlags(modifiers, Flags.FINAL);
    
                List<LocalDecl> l = new TypedList<LocalDecl>(new LinkedList<LocalDecl>(), LocalDecl.class, false);
                    for (Object[] o : VariableDeclaratorsWithType)
                    {
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        if (name == null) name = nf.Id(pos, Name.makeFresh());
                        List<Id> exploded = (List<Id>) o[2];
                        DepParameterExpr guard = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                        if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                        Expr init = (Expr) o[5];
                        LocalDecl ld = nf.LocalDecl(pos, fn,
                                           type, name, init);
                        ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(modifiers));
                        int index = 0;
                        l.add(ld);
                        for (Id id : exploded) {
                            // HACK: if the local is non-final, assume the type is point and the component is int
                            TypeNode tni = nf.UnknownTypeNode(id.position());
                            l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(JPGPosition.COMPILER_GENERATED, nf.Local(JPGPosition.COMPILER_GENERATED, name),  Collections.<Expr>singletonList(nf.IntLit(JPGPosition.COMPILER_GENERATED, IntLit.INT, index))) : null));
                            index++;
                        }
                    }
                setResult(l);
                      break;
            }
    
            //
            // Rule 375:  LocalVariableDeclaration ::= Modifiersopt VarKeyword FormalDeclarators
            //
            case 375: {
               //#line 4185 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4183 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 4183 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<FlagsNode> VarKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 4183 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> FormalDeclarators = (List<Object[]>) getRhsSym(3);
                //#line 4185 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkVariableModifiers(Modifiersopt);
                FlagsNode fn = extractFlags(modifiers, VarKeyword);
    
                List<LocalDecl> l = new TypedList<LocalDecl>(new LinkedList<LocalDecl>(), LocalDecl.class, false);
                    for (Object[] o : FormalDeclarators)
                    {
                        Position pos = (Position) o[0];
                        Id name = (Id) o[1];
                        if (name == null) name = nf.Id(pos, Name.makeFresh());
                        List<Id> exploded = (List<Id>) o[2];
                        DepParameterExpr guard = (DepParameterExpr) o[3];
                        TypeNode type = (TypeNode) o[4];
                                                    if (type == null) type = nf.UnknownTypeNode(name != null ? name.position() : pos);
                        Expr init = (Expr) o[5];
                        LocalDecl ld = nf.LocalDecl(pos, fn,
                                           type, name, init);
                        ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(modifiers));
                        int index = 0;
                        l.add(ld);
                        for (Id id : exploded) {
                            // HACK: if the local is non-final, assume the type is point and the component is int
                            TypeNode tni = nf.UnknownTypeNode(id.position());
                            // todo: fixme: do this desugaring after type-checking, and remove this code duplication 
                            l.add(nf.LocalDecl(id.position(), fn, tni, id, init != null ? nf.ClosureCall(JPGPosition.COMPILER_GENERATED, nf.Local(JPGPosition.COMPILER_GENERATED, name),  Collections.<Expr>singletonList(nf.IntLit(JPGPosition.COMPILER_GENERATED, IntLit.INT, index))) : null));
                            index++;
                        }
                    }
                setResult(l);
                      break;
            }
    
            //
            // Rule 376:  Primary ::= here
            //
            case 376: {
               //#line 4223 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4223 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 377:  Primary ::= [ ArgumentListopt ]
            //
            case 377: {
               //#line 4229 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4227 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(2);
                //#line 4229 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 379:  Primary ::= self
            //
            case 379: {
               //#line 4237 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4237 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 380:  Primary ::= this
            //
            case 380: {
               //#line 4242 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4242 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 381:  Primary ::= ClassName . this
            //
            case 381: {
               //#line 4247 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4245 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4247 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 382:  Primary ::= ( Expression )
            //
            case 382: {
               //#line 4252 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4250 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 4252 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 388:  OperatorFunction ::= TypeName . +
            //
            case 388: {
               //#line 4263 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4261 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4263 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.ADD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 389:  OperatorFunction ::= TypeName . -
            //
            case 389: {
               //#line 4274 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4272 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4274 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SUB, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 390:  OperatorFunction ::= TypeName . *
            //
            case 390: {
               //#line 4285 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4283 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4285 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MUL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 391:  OperatorFunction ::= TypeName . /
            //
            case 391: {
               //#line 4296 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4294 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4296 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.DIV, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 392:  OperatorFunction ::= TypeName . %
            //
            case 392: {
               //#line 4307 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4305 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4307 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MOD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 393:  OperatorFunction ::= TypeName . &
            //
            case 393: {
               //#line 4318 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4316 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4318 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_AND, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 394:  OperatorFunction ::= TypeName . |
            //
            case 394: {
               //#line 4329 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4327 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4329 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_OR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 395:  OperatorFunction ::= TypeName . ^
            //
            case 395: {
               //#line 4340 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4338 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4340 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_XOR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 396:  OperatorFunction ::= TypeName . <<
            //
            case 396: {
               //#line 4351 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4349 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4351 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 397:  OperatorFunction ::= TypeName . >>
            //
            case 397: {
               //#line 4362 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4360 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4362 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 398:  OperatorFunction ::= TypeName . >>>
            //
            case 398: {
               //#line 4373 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4371 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4373 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(),  nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.USHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 399:  OperatorFunction ::= TypeName . <
            //
            case 399: {
               //#line 4384 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4382 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4384 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 400:  OperatorFunction ::= TypeName . <=
            //
            case 400: {
               //#line 4395 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4393 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4395 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 401:  OperatorFunction ::= TypeName . >=
            //
            case 401: {
               //#line 4406 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4404 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4406 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 402:  OperatorFunction ::= TypeName . >
            //
            case 402: {
               //#line 4417 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4415 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4417 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 403:  OperatorFunction ::= TypeName . ==
            //
            case 403: {
               //#line 4428 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4426 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4428 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.EQ, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 404:  OperatorFunction ::= TypeName . !=
            //
            case 404: {
               //#line 4439 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4437 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4439 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.NE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 405:  Literal ::= IntegerLiteral$lit
            //
            case 405: {
               //#line 4452 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4450 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4452 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 406:  Literal ::= LongLiteral$lit
            //
            case 406: {
               //#line 4458 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4456 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4458 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 407:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 407: {
               //#line 4464 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4462 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4464 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 408:  Literal ::= UnsignedLongLiteral$lit
            //
            case 408: {
               //#line 4470 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4468 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4470 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 409:  Literal ::= FloatingPointLiteral$lit
            //
            case 409: {
               //#line 4476 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4474 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4476 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 410:  Literal ::= DoubleLiteral$lit
            //
            case 410: {
               //#line 4482 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4480 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4482 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 411:  Literal ::= BooleanLiteral
            //
            case 411: {
               //#line 4488 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4486 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 4488 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 412:  Literal ::= CharacterLiteral$lit
            //
            case 412: {
               //#line 4493 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4491 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4493 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 413:  Literal ::= StringLiteral$str
            //
            case 413: {
               //#line 4499 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4497 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 4499 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 414:  Literal ::= null
            //
            case 414: {
               //#line 4505 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4505 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 415:  BooleanLiteral ::= true$trueLiteral
            //
            case 415: {
               //#line 4511 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4509 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 4511 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 416:  BooleanLiteral ::= false$falseLiteral
            //
            case 416: {
               //#line 4516 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4514 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 4516 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 417:  ArgumentList ::= Expression
            //
            case 417: {
               //#line 4525 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4523 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 4525 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 418:  ArgumentList ::= ArgumentList , Expression
            //
            case 418: {
               //#line 4532 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4530 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentList = (List<Expr>) getRhsSym(1);
                //#line 4530 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4532 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 419:  FieldAccess ::= Primary . Identifier
            //
            case 419: {
               //#line 4538 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4536 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4536 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4538 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 420:  FieldAccess ::= super . Identifier
            //
            case 420: {
               //#line 4543 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4541 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4543 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 421:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 421: {
               //#line 4548 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4546 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4546 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4546 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4548 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 422:  FieldAccess ::= Primary . class$c
            //
            case 422: {
               //#line 4553 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4551 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4551 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4553 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 423:  FieldAccess ::= super . class$c
            //
            case 423: {
               //#line 4558 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4556 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4558 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 424:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 424: {
               //#line 4563 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4561 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4561 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4561 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 4563 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 425:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 425: {
               //#line 4569 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4567 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4567 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 4567 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 4569 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 426:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 426: {
               //#line 4576 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4574 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4574 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4574 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(4);
                //#line 4574 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(6);
                //#line 4576 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 427:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 427: {
               //#line 4581 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4579 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4579 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(4);
                //#line 4579 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(6);
                //#line 4581 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 428:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 428: {
               //#line 4586 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4584 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4584 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4584 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4584 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(6);
                //#line 4584 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(8);
                //#line 4586 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 429:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 429: {
               //#line 4591 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4589 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4589 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 4589 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 4591 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 430:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 430: {
               //#line 4611 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4609 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4609 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(4);
                //#line 4611 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(), nf.X10Call(pos(),
                                                             MethodName.prefix == null ? null : MethodName.prefix.toReceiver(),
                                                             MethodName.name, Collections.<TypeNode>emptyList(), actuals), true))));
                      break;
            }
    
            //
            // Rule 431:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 431: {
               //#line 4624 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4622 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4622 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4622 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(6);
                //#line 4624 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), Primary, Identifier, Collections.<TypeNode>emptyList(), actuals), true))));
                      break;
            }
    
            //
            // Rule 432:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 432: {
               //#line 4636 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4634 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4634 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(6);
                //#line 4636 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier,
                                                          Collections.<TypeNode>emptyList(), actuals), true))));
                      break;
            }
    
            //
            // Rule 433:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 433: {
               //#line 4649 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4647 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4647 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4647 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4647 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(8);
                //#line 4649 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, Collections.<TypeNode>emptyList(), nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, 
                                                          Collections.<TypeNode>emptyList(), actuals), true))));
                      break;
            }
    
            //
            // Rule 437:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 437: {
               //#line 4667 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4665 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4667 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 438:  PostDecrementExpression ::= PostfixExpression --
            //
            case 438: {
               //#line 4673 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4671 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4673 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 441:  UnannotatedUnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 441: {
               //#line 4681 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4679 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4681 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 442:  UnannotatedUnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 442: {
               //#line 4686 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4684 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4686 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 445:  UnaryExpression ::= Annotations UnannotatedUnaryExpression
            //
            case 445: {
               //#line 4694 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4692 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotations = (List<AnnotationNode>) getRhsSym(1);
                //#line 4692 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnannotatedUnaryExpression = (Expr) getRhsSym(2);
                //#line 4694 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnannotatedUnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e.position(pos()));
                      break;
            }
    
            //
            // Rule 446:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 446: {
               //#line 4702 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4700 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4702 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 447:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 447: {
               //#line 4708 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4706 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4708 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 449:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 449: {
               //#line 4715 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4713 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4715 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 450:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 450: {
               //#line 4720 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4718 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4720 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 452:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 452: {
               //#line 4727 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4725 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4725 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4727 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 453:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 453: {
               //#line 4732 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4730 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4730 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4732 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 454:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 454: {
               //#line 4737 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4735 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4735 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4737 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 456:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 456: {
               //#line 4744 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4742 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4742 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4744 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 457:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 457: {
               //#line 4749 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4747 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4747 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4749 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 459:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 459: {
               //#line 4756 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4754 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4754 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4756 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 460:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 460: {
               //#line 4761 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4759 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4759 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4761 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 461:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 461: {
               //#line 4766 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4764 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4764 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4766 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 463:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 463: {
               //#line 4773 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4771 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4771 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4773 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 466:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 466: {
               //#line 4782 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4780 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4780 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4782 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 467:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 467: {
               //#line 4787 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4785 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4785 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4787 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 468:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 468: {
               //#line 4792 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4790 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4790 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4792 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 469:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 469: {
               //#line 4797 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4795 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4795 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4797 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 470:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 470: {
               //#line 4802 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4800 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4800 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4802 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 471:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 471: {
               //#line 4807 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4805 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4805 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4807 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 473:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 473: {
               //#line 4814 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4812 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4812 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4814 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 474:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 474: {
               //#line 4819 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4817 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4817 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4819 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 475:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 475: {
               //#line 4824 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4822 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4822 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4824 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 477:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 477: {
               //#line 4831 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4829 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4829 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4831 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 479:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 479: {
               //#line 4838 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4836 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4836 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4838 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 481:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 481: {
               //#line 4845 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4843 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4843 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4845 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 483:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 483: {
               //#line 4852 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4850 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4850 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4852 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 485:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 485: {
               //#line 4859 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4857 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4857 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4859 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 490:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 490: {
               //#line 4870 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4868 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4868 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4868 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4870 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 493:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 493: {
               //#line 4879 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4877 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4877 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4877 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4879 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 494:  Assignment ::= ExpressionName$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 494: {
               //#line 4884 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4882 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4882 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(3);
                //#line 4882 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4882 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4884 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentListopt, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 495:  Assignment ::= Primary$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 495: {
               //#line 4889 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4887 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4887 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(3);
                //#line 4887 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4887 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4889 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentListopt, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 496:  LeftHandSide ::= ExpressionName
            //
            case 496: {
               //#line 4895 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4893 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4895 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 498:  AssignmentOperator ::= =
            //
            case 498: {
               //#line 4902 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4902 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 499:  AssignmentOperator ::= *=
            //
            case 499: {
               //#line 4907 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4907 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 500:  AssignmentOperator ::= /=
            //
            case 500: {
               //#line 4912 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4912 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 501:  AssignmentOperator ::= %=
            //
            case 501: {
               //#line 4917 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4917 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 502:  AssignmentOperator ::= +=
            //
            case 502: {
               //#line 4922 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4922 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 503:  AssignmentOperator ::= -=
            //
            case 503: {
               //#line 4927 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4927 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 504:  AssignmentOperator ::= <<=
            //
            case 504: {
               //#line 4932 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4932 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 505:  AssignmentOperator ::= >>=
            //
            case 505: {
               //#line 4937 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4937 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 506:  AssignmentOperator ::= >>>=
            //
            case 506: {
               //#line 4942 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4942 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 507:  AssignmentOperator ::= &=
            //
            case 507: {
               //#line 4947 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4947 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 508:  AssignmentOperator ::= ^=
            //
            case 508: {
               //#line 4952 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4952 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 509:  AssignmentOperator ::= |=
            //
            case 509: {
               //#line 4957 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4957 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 512:  PrefixOp ::= +
            //
            case 512: {
               //#line 4968 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4968 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 513:  PrefixOp ::= -
            //
            case 513: {
               //#line 4973 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4973 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 514:  PrefixOp ::= !
            //
            case 514: {
               //#line 4978 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4978 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 515:  PrefixOp ::= ~
            //
            case 515: {
               //#line 4983 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4983 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 516:  BinOp ::= +
            //
            case 516: {
               //#line 4989 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4989 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 517:  BinOp ::= -
            //
            case 517: {
               //#line 4994 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4994 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 518:  BinOp ::= *
            //
            case 518: {
               //#line 4999 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4999 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 519:  BinOp ::= /
            //
            case 519: {
               //#line 5004 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5004 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 520:  BinOp ::= %
            //
            case 520: {
               //#line 5009 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5009 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 521:  BinOp ::= &
            //
            case 521: {
               //#line 5014 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5014 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 522:  BinOp ::= |
            //
            case 522: {
               //#line 5019 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5019 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 523:  BinOp ::= ^
            //
            case 523: {
               //#line 5024 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5024 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 524:  BinOp ::= &&
            //
            case 524: {
               //#line 5029 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5029 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 525:  BinOp ::= ||
            //
            case 525: {
               //#line 5034 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5034 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 526:  BinOp ::= <<
            //
            case 526: {
               //#line 5039 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5039 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 527:  BinOp ::= >>
            //
            case 527: {
               //#line 5044 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5044 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 528:  BinOp ::= >>>
            //
            case 528: {
               //#line 5049 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5049 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 529:  BinOp ::= >=
            //
            case 529: {
               //#line 5054 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5054 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 530:  BinOp ::= <=
            //
            case 530: {
               //#line 5059 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5059 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 531:  BinOp ::= >
            //
            case 531: {
               //#line 5064 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5064 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 532:  BinOp ::= <
            //
            case 532: {
               //#line 5069 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5069 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 533:  BinOp ::= ==
            //
            case 533: {
               //#line 5077 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5077 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 534:  BinOp ::= !=
            //
            case 534: {
               //#line 5082 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5082 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 535:  Catchesopt ::= $Empty
            //
            case 535: {
               //#line 5091 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5091 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false));
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
               //#line 5100 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 5098 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 5100 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 539:  ForUpdateopt ::= $Empty
            //
            case 539: {
               //#line 5106 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5106 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<ForUpdate>(new LinkedList<ForUpdate>(), ForUpdate.class, false));
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
               //#line 5117 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5117 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 545:  SwitchLabelsopt ::= $Empty
            //
            case 545: {
               //#line 5124 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5124 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Case>(new LinkedList<Case>(), Case.class, false));
                      break;
            }
    
            //
            // Rule 547:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 547: {
               //#line 5131 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5131 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 549:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 549: {
               //#line 5155 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5155 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 551:  ExtendsInterfacesopt ::= $Empty
            //
            case 551: {
               //#line 5162 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5162 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
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
               //#line 5193 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5193 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 557:  BlockStatementsopt ::= $Empty
            //
            case 557: {
               //#line 5200 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5200 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false));
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
               //#line 5221 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5221 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 563:  Throwsopt ::= $Empty
            //
            case 563: {
               //#line 5228 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5228 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 565:  Offersopt ::= $Empty
            //
            case 565: {
               //#line 5234 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5234 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 567:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 567: {
               //#line 5271 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5271 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 569:  Interfacesopt ::= $Empty
            //
            case 569: {
               //#line 5278 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5278 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
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
               //#line 5289 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5289 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 575:  FormalParametersopt ::= $Empty
            //
            case 575: {
               //#line 5296 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5296 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 577:  Annotationsopt ::= $Empty
            //
            case 577: {
               //#line 5303 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5303 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 579:  TypeDeclarationsopt ::= $Empty
            //
            case 579: {
               //#line 5310 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5310 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 581:  ImportDeclarationsopt ::= $Empty
            //
            case 581: {
               //#line 5317 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5317 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Import>(new LinkedList<Import>(), Import.class, false));
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
               //#line 5338 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5338 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 589:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 589: {
               //#line 5345 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5345 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 591:  Propertiesopt ::= $Empty
            //
            case 591: {
               //#line 5352 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5352 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<PropertyDecl>(new LinkedList<PropertyDecl>(), PropertyDecl.class, false));
                      break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

