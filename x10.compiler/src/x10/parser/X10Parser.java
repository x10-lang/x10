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
        //public static int INCOMPLETE  = 5;
        public static int NATIVE      = 6;
        //public static int NON_BLOCKING = 7;
        public static int PRIVATE     = 8;
        public static int PROPERTY    = 9;
        public static int PROTECTED   = 10;
        public static int PUBLIC      = 11;
        //public static int SAFE        = 12;
        //public static int SEQUENTIAL  = 13;
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
            //if (flag == INCOMPLETE)   return X10Flags.INCOMPLETE;
            if (flag == NATIVE)       return Flags.NATIVE;
            //if (flag == NON_BLOCKING) return X10Flags.NON_BLOCKING;
            if (flag == PRIVATE)      return Flags.PRIVATE;
            if (flag == PROPERTY)     return X10Flags.PROPERTY;
            if (flag == PROTECTED)    return Flags.PROTECTED;
            if (flag == PUBLIC)       return Flags.PUBLIC;
            //if (flag == SAFE)         return X10Flags.SAFE;
            //if (flag == SEQUENTIAL)   return X10Flags.SEQUENTIAL;
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
            //if (flag == INCOMPLETE)   return "incomplete";
            if (flag == NATIVE)       return "native";
            //if (flag == NON_BLOCKING) return "nonblocking";
            if (flag == PRIVATE)      return "private";
            if (flag == PROPERTY)     return "property";
            if (flag == PROTECTED)    return "protected";
            if (flag == PUBLIC)       return "public";
            //if (flag == SAFE)         return "safe";
            //if (flag == SEQUENTIAL)   return "sequential";
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
            //classModifiers[SAFE] = true;
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
            //methodModifiers[INCOMPLETE] = true;
            methodModifiers[NATIVE] = true;
            //methodModifiers[NON_BLOCKING] = true;
            methodModifiers[PRIVATE] = true;
            methodModifiers[PROPERTY] = true;
            methodModifiers[PROTECTED] = true;
            methodModifiers[PUBLIC] = true;
            //methodModifiers[SAFE] = true;
            //methodModifiers[SEQUENTIAL] = true;
            methodModifiers[STATIC] = true;
            //methodModifiers[CLOCKED] = true;
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
            // Rule 24:  Modifier ::= native
            //
            case 24: {
               //#line 1231 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1231 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.NATIVE));
                      break;
            }
    
            //
            // Rule 25:  Modifier ::= private
            //
            case 25: {
               //#line 1236 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1236 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PRIVATE));
                      break;
            }
    
            //
            // Rule 26:  Modifier ::= protected
            //
            case 26: {
               //#line 1241 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1241 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PROTECTED));
                      break;
            }
    
            //
            // Rule 27:  Modifier ::= public
            //
            case 27: {
               //#line 1246 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1246 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PUBLIC));
                      break;
            }
    
            //
            // Rule 28:  Modifier ::= static
            //
            case 28: {
               //#line 1251 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1251 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.STATIC));
                      break;
            }
    
            //
            // Rule 29:  Modifier ::= transient
            //
            case 29: {
               //#line 1256 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1256 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.TRANSIENT));
                      break;
            }
    
            //
            // Rule 30:  Modifier ::= clocked
            //
            case 30: {
               //#line 1261 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1261 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.CLOCKED));
                      break;
            }
    
            //
            // Rule 32:  MethodModifiersopt ::= MethodModifiersopt property$property
            //
            case 32: {
               //#line 1268 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1266 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1266 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken property = (IToken) getRhsIToken(2);
                //#line 1268 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiersopt.add(new FlagModifier(pos(getRhsFirstTokenIndex(2)), FlagModifier.PROPERTY));
                      break;
            }
    
            //
            // Rule 33:  MethodModifiersopt ::= MethodModifiersopt Modifier
            //
            case 33: {
               //#line 1273 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1271 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1271 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Modifier Modifier = (Modifier) getRhsSym(2);
                //#line 1273 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiersopt.add(Modifier);
                      break;
            }
    
            //
            // Rule 34:  TypeDefDeclaration ::= Modifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
            //
            case 34: {
               //#line 1279 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1277 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1277 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1277 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1277 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParametersopt = (List<Formal>) getRhsSym(5);
                //#line 1277 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1277 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1279 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 35:  Properties ::= ( PropertyList )
            //
            case 35: {
               //#line 1299 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1297 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<PropertyDecl> PropertyList = (List<PropertyDecl>) getRhsSym(2);
                //#line 1299 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(PropertyList);
                 break;
            } 
            //
            // Rule 36:  PropertyList ::= Property
            //
            case 36: {
               //#line 1304 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1302 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 1304 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<PropertyDecl> l = new TypedList<PropertyDecl>(new LinkedList<PropertyDecl>(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 37:  PropertyList ::= PropertyList , Property
            //
            case 37: {
               //#line 1311 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1309 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<PropertyDecl> PropertyList = (List<PropertyDecl>) getRhsSym(1);
                //#line 1309 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 1311 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 38:  Property ::= Annotationsopt Identifier ResultType
            //
            case 38: {
               //#line 1318 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1316 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 1316 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1316 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 1318 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<AnnotationNode> annotations = extractAnnotations(Annotationsopt);
                PropertyDecl cd = nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), ResultType, Identifier);
                cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 39:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 39: {
               //#line 1327 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1325 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1325 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1325 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1325 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(5);
                //#line 1325 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1325 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1325 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(8);
                //#line 1325 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1327 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
                                        
                                          Offersopt,
                                          MethodBody);
                }
                pd = (ProcedureDecl) ((X10Ext) pd.ext()).annotations(extractAnnotations(modifiers));
                setResult(pd);
                      break;
            }
    
            //
            // Rule 40:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 40: {
               //#line 1360 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1358 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1358 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1358 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1358 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1358 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 1358 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 1358 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 1358 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(13);
                //#line 1358 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 1360 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 41:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 41: {
               //#line 1387 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1385 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1385 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1385 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1385 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 1385 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 1385 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 1385 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(10);
                //#line 1385 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1387 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 42:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 42: {
               //#line 1414 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1412 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1412 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1412 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 1412 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 1412 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1412 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1412 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1412 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1414 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 43:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 43: {
               //#line 1441 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1439 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1439 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1439 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1439 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1439 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1439 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1439 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1439 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1441 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 44:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 44: {
               //#line 1468 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1466 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1466 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1466 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1466 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1466 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1466 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(8);
                //#line 1466 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1468 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 45:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 45: {
               //#line 1495 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(5);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(8);
                //#line 1493 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1495 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(), ClosureCall.APPLY),
                                                 TypeParametersopt,
                                                 FormalParameters,
                                                 WhereClauseopt,
                                              
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
            // Rule 46:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 46: {
               //#line 1517 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1515 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1515 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1515 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(5);
                //#line 1515 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1515 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1515 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1515 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1515 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1517 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(), SettableAssign.SET),
                                                 TypeParametersopt,
                                                 CollectionUtil.append(Collections.singletonList(fp2), FormalParameters),
                                                 WhereClauseopt,
                                                 
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
            // Rule 47:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Offersopt MethodBody
            //
            case 47: {
               //#line 1539 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1537 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1537 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1537 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1537 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1537 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1537 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(10);
                //#line 1537 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1539 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 Type,
                                                 nf.Id(pos(), Converter.operator_as),
                                                 TypeParametersopt,
                                                 Collections.<Formal>singletonList(fp1),
                                                 WhereClauseopt,
                                                 
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
            // Rule 48:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 48: {
               //#line 1561 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1561 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(), Converter.operator_as),
                                                 TypeParametersopt,
                                                 Collections.<Formal>singletonList(fp1),
                                                 WhereClauseopt,
                                                 
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
            // Rule 49:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 49: {
               //#line 1583 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1581 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1581 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1581 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1581 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1581 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(8);
                //#line 1581 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1581 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1583 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 nf.Id(pos(), Converter.implicit_operator_as),
                                                 TypeParametersopt,
                                                 Collections.<Formal>singletonList(fp1),
                                                 WhereClauseopt,
                                                 
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
            // Rule 50:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt MethodBody
            //
            case 50: {
               //#line 1606 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1604 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1604 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1604 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1604 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(4);
                //#line 1604 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1604 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(6);
                //#line 1604 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(7);
                //#line 1606 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers, X10Flags.PROPERTY),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 Identifier,
                                                 TypeParametersopt,
                                                 FormalParameters,
                                                 WhereClauseopt,
                                              
                                                 null, // offersOpt
                                                 MethodBody);
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 51:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier WhereClauseopt HasResultTypeopt MethodBody
            //
            case 51: {
               //#line 1623 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1621 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1621 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1621 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(3);
                //#line 1621 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 1621 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(5);
                //#line 1623 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
                MethodDecl md = nf.X10MethodDecl(pos(),
                                                 extractFlags(modifiers, X10Flags.PROPERTY),
                                                 HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,
                                                 Identifier,
                                                 Collections.<TypeParamNode>emptyList(),
                                                 Collections.<Formal>emptyList(),
                                                 WhereClauseopt,
                                             
                                                 null, // offersOpt
                                                 MethodBody);
                md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
                setResult(md);
                      break;
            }
    
            //
            // Rule 52:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 52: {
               //#line 1641 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1639 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 1639 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 1641 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 53:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 53: {
               //#line 1646 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1644 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 1644 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 1646 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 54:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 54: {
               //#line 1651 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1649 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1649 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(4);
                //#line 1649 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(6);
                //#line 1651 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 55:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 55: {
               //#line 1656 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1654 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1654 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(4);
                //#line 1654 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(6);
                //#line 1656 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 56:  NormalInterfaceDeclaration ::= Modifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 56: {
               //#line 1662 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1660 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1660 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1660 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParamsWithVarianceopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1660 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<PropertyDecl> Propertiesopt = (List<PropertyDecl>) getRhsSym(5);
                //#line 1660 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1660 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> ExtendsInterfacesopt = (List<TypeNode>) getRhsSym(7);
                //#line 1660 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1662 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 57:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 57: {
               //#line 1684 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1682 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1682 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(3);
                //#line 1682 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(5);
                //#line 1682 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1684 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 58:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 58: {
               //#line 1691 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1689 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1689 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1689 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(5);
                //#line 1689 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(7);
                //#line 1689 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1691 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 59:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 59: {
               //#line 1699 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1697 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1697 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1697 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(5);
                //#line 1697 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(7);
                //#line 1697 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1699 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 60:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 60: {
               //#line 1708 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1706 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 1706 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 1708 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 63:  FunctionType ::= TypeParametersopt ( FormalParameterListopt ) WhereClauseopt Offersopt => Type
            //
            case 63: {
               //#line 1718 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1716 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(1);
                //#line 1716 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(3);
                //#line 1716 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1716 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(6);
                //#line 1716 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1718 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeParametersopt, FormalParameterListopt, WhereClauseopt, Type,  Offersopt));
                      break;
            }
    
            //
            // Rule 65:  AnnotatedType ::= Type Annotations
            //
            case 65: {
               //#line 1731 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1729 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1729 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotations = (List<AnnotationNode>) getRhsSym(2);
                //#line 1731 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn.position(pos()));
                      break;
            }
    
            //
            // Rule 68:  ConstrainedType ::= ( Type )
            //
            case 68: {
               //#line 1741 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1739 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1741 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 70:  SimpleNamedType ::= TypeName
            //
            case 70: {
               //#line 1755 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1753 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1755 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(TypeName.toType());
                      break;
            }
    
            //
            // Rule 71:  SimpleNamedType ::= Primary . Identifier
            //
            case 71: {
               //#line 1760 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1758 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1758 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1760 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(nf.AmbTypeNode(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 72:  SimpleNamedType ::= DepNamedType . Identifier
            //
            case 72: {
               //#line 1765 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1763 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode DepNamedType = (TypeNode) getRhsSym(1);
                //#line 1763 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1765 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(nf.AmbTypeNode(pos(), DepNamedType, Identifier));
                      break;
            }
    
            //
            // Rule 73:  DepNamedType ::= SimpleNamedType DepParameters
            //
            case 73: {
               //#line 1771 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1769 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1769 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(2);
                //#line 1771 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false),
                                              new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 74:  DepNamedType ::= SimpleNamedType Arguments
            //
            case 74: {
               //#line 1780 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1778 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1778 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Arguments = (List<Expr>) getRhsSym(2);
                //#line 1780 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false),
                                              Arguments,
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 75:  DepNamedType ::= SimpleNamedType Arguments DepParameters
            //
            case 75: {
               //#line 1789 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1787 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1787 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Arguments = (List<Expr>) getRhsSym(2);
                //#line 1787 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(3);
                //#line 1789 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false),
                                              Arguments,
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 76:  DepNamedType ::= SimpleNamedType TypeArguments
            //
            case 76: {
               //#line 1798 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1796 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1796 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArguments = (List<TypeNode>) getRhsSym(2);
                //#line 1798 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 77:  DepNamedType ::= SimpleNamedType TypeArguments DepParameters
            //
            case 77: {
               //#line 1807 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1805 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1805 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArguments = (List<TypeNode>) getRhsSym(2);
                //#line 1805 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(3);
                //#line 1807 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 78:  DepNamedType ::= SimpleNamedType TypeArguments Arguments
            //
            case 78: {
               //#line 1816 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1814 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1814 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArguments = (List<TypeNode>) getRhsSym(2);
                //#line 1814 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Arguments = (List<Expr>) getRhsSym(3);
                //#line 1816 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              Arguments,
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 79:  DepNamedType ::= SimpleNamedType TypeArguments Arguments DepParameters
            //
            case 79: {
               //#line 1825 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1823 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1823 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArguments = (List<TypeNode>) getRhsSym(2);
                //#line 1823 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Arguments = (List<Expr>) getRhsSym(3);
                //#line 1823 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(4);
                //#line 1825 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              Arguments,
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 82:  DepParameters ::= { ExistentialListopt Conjunctionopt }
            //
            case 82: {
               //#line 1838 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1836 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> ExistentialListopt = (List<Formal>) getRhsSym(2);
                //#line 1836 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Conjunctionopt = (List<Expr>) getRhsSym(3);
                //#line 1838 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunctionopt));
                      break;
            }
    
            //
            // Rule 83:  DepParameters ::= ! PlaceType
            //
            case 83: {
               //#line 1843 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1841 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1843 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 84:  DepParameters ::= !
            //
            case 84: {
               //#line 1849 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1849 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), null, Collections.singletonList(placeClause)));
                      break;
            }
    
            //
            // Rule 85:  DepParameters ::= ! PlaceType { ExistentialListopt Conjunction }
            //
            case 85: {
               //#line 1855 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1853 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceType = (Expr) getRhsSym(2);
                //#line 1853 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> ExistentialListopt = (List<Formal>) getRhsSym(4);
                //#line 1853 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Conjunction = (List<Expr>) getRhsSym(5);
                //#line 1855 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), PlaceType);
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 86:  DepParameters ::= ! { ExistentialListopt Conjunction }
            //
            case 86: {
               //#line 1861 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1859 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> ExistentialListopt = (List<Formal>) getRhsSym(3);
                //#line 1859 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Conjunction = (List<Expr>) getRhsSym(4);
                //#line 1861 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr placeClause = nf.Call(pos(), nf.Self(pos()), nf.Id(pos(), "at"), nf.AmbHereThis(pos()));
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, CollectionUtil.append(Conjunction, Collections.singletonList(placeClause))));
                      break;
            }
    
            //
            // Rule 87:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 87: {
               //#line 1869 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1867 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParamWithVarianceList = (List<TypeParamNode>) getRhsSym(2);
                //#line 1869 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 88:  TypeParameters ::= [ TypeParameterList ]
            //
            case 88: {
               //#line 1875 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1873 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParameterList = (List<TypeParamNode>) getRhsSym(2);
                //#line 1875 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 89:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 89: {
               //#line 1881 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1879 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(2);
                //#line 1881 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 90:  Conjunction ::= Expression
            //
            case 90: {
               //#line 1887 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1885 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1887 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Expr> l = new ArrayList<Expr>();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 91:  Conjunction ::= Conjunction , Expression
            //
            case 91: {
               //#line 1894 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1892 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Conjunction = (List<Expr>) getRhsSym(1);
                //#line 1892 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1894 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 92:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 92: {
               //#line 1900 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1898 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1898 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1900 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 93:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 93: {
               //#line 1905 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1903 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1903 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1905 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 94:  WhereClause ::= DepParameters
            //
            case 94: {
               //#line 1911 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1909 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1911 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 95:  Conjunctionopt ::= $Empty
            //
            case 95: {
               //#line 1917 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1917 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Expr> l = new ArrayList<Expr>();
                setResult(l);
                      break;
            }
      
            //
            // Rule 96:  Conjunctionopt ::= Conjunction
            //
            case 96: {
               //#line 1923 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1921 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> Conjunction = (List<Expr>) getRhsSym(1);
                //#line 1923 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(Conjunction);
                      break;
            }
    
            //
            // Rule 97:  ExistentialListopt ::= $Empty
            //
            case 97: {
               //#line 1929 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1929 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList<Formal>());
                      break;
            }
      
            //
            // Rule 98:  ExistentialListopt ::= ExistentialList ;
            //
            case 98: {
               //#line 1934 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1932 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> ExistentialList = (List<Formal>) getRhsSym(1);
                //#line 1934 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 99:  ExistentialList ::= FormalParameter
            //
            case 99: {
               //#line 1940 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1938 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1940 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 100:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 100: {
               //#line 1947 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1945 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> ExistentialList = (List<Formal>) getRhsSym(1);
                //#line 1945 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1947 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(X10NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 103:  NormalClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 103: {
               //#line 1958 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1956 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1956 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1956 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParamsWithVarianceopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1956 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<PropertyDecl> Propertiesopt = (List<PropertyDecl>) getRhsSym(5);
                //#line 1956 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1956 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1956 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Interfacesopt = (List<TypeNode>) getRhsSym(8);
                //#line 1956 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1958 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 104:  StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 104: {
               //#line 1976 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1974 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1974 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1974 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParamsWithVarianceopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1974 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<PropertyDecl> Propertiesopt = (List<PropertyDecl>) getRhsSym(5);
                //#line 1974 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1974 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> Interfacesopt = (List<TypeNode>) getRhsSym(7);
                //#line 1974 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1976 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 105:  ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt ConstructorBody
            //
            case 105: {
               //#line 1991 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1989 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1989 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1989 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(5);
                //#line 1989 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1989 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1989 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(8);
                //#line 1989 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(9);
                //#line 1991 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Node> modifiers = checkConstructorModifiers(Modifiersopt);
                ConstructorDecl cd = nf.X10ConstructorDecl(pos(),
                                                           extractFlags(modifiers),
                                                           nf.Id(pos(getRhsFirstTokenIndex(3)), "this"),
                                                           HasResultTypeopt,
                                                           TypeParametersopt,
                                                           FormalParameters,
                                                           WhereClauseopt,
                                                           
                                                           Offersopt,
                                                           ConstructorBody);
                cd = (ConstructorDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(modifiers));
                setResult(cd);
                     break;
            }
    
            //
            // Rule 106:  Super ::= extends ClassType
            //
            case 106: {
               //#line 2009 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2007 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 2009 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 107:  FieldKeyword ::= val
            //
            case 107: {
               //#line 2015 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2015 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 108:  FieldKeyword ::= var
            //
            case 108: {
               //#line 2020 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2020 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 109:  VarKeyword ::= val
            //
            case 109: {
               //#line 2028 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2028 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 110:  VarKeyword ::= var
            //
            case 110: {
               //#line 2033 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2033 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 111:  FieldDeclaration ::= Modifiersopt FieldKeyword FieldDeclarators ;
            //
            case 111: {
               //#line 2040 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2038 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 2038 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<FlagsNode> FieldKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 2038 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> FieldDeclarators = (List<Object[]>) getRhsSym(3);
                //#line 2040 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 112:  FieldDeclaration ::= Modifiersopt FieldDeclarators ;
            //
            case 112: {
               //#line 2065 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2063 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 2063 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> FieldDeclarators = (List<Object[]>) getRhsSym(2);
                //#line 2065 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 115:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 115: {
               //#line 2097 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2095 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 2095 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt NonExpressionStatement = (Stmt) getRhsSym(2);
                //#line 2097 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                if (NonExpressionStatement.ext() instanceof X10Ext) {
                    NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations(Annotationsopt);
                }
                setResult(NonExpressionStatement.position(pos()));
                      break;
            }
    
            //
            // Rule 143:  OfferStatement ::= offer Expression ;
            //
            case 143: {
               //#line 2134 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2132 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2134 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Offer(pos(), Expression));
                      break;
            }
    
            //
            // Rule 144:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 144: {
               //#line 2140 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2138 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2138 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2140 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 145:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 145: {
               //#line 2146 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2144 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2144 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 2144 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 2146 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 146:  EmptyStatement ::= ;
            //
            case 146: {
               //#line 2152 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2152 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 147:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 147: {
               //#line 2158 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2156 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2156 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 2158 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 153:  ExpressionStatement ::= StatementExpression ;
            //
            case 153: {
               //#line 2170 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2168 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 2170 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 161:  AssertStatement ::= assert Expression ;
            //
            case 161: {
               //#line 2184 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2182 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2184 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 162:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 162: {
               //#line 2189 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2187 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 2187 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 2189 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 163:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 163: {
               //#line 2195 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2193 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2193 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<SwitchElement> SwitchBlock = (List<SwitchElement>) getRhsSym(5);
                //#line 2195 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 164:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 164: {
               //#line 2201 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2199 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> SwitchBlockStatementGroupsopt = (List<Stmt>) getRhsSym(2);
                //#line 2199 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Case> SwitchLabelsopt = (List<Case>) getRhsSym(3);
                //#line 2201 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 166:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 166: {
               //#line 2209 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2207 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<SwitchElement> SwitchBlockStatementGroups = (List<SwitchElement>) getRhsSym(1);
                //#line 2207 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<SwitchElement> SwitchBlockStatementGroup = (List<SwitchElement>) getRhsSym(2);
                //#line 2209 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 167:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 167: {
               //#line 2216 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2214 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<SwitchElement> SwitchLabels = (List<SwitchElement>) getRhsSym(1);
                //#line 2214 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatements = (List<Stmt>) getRhsSym(2);
                //#line 2216 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<SwitchElement> l = new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 168:  SwitchLabels ::= SwitchLabel
            //
            case 168: {
               //#line 2225 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2223 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 2225 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Case> l = new TypedList<Case>(new LinkedList<Case>(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 169:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 169: {
               //#line 2232 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2230 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<SwitchElement> SwitchLabels = (List<SwitchElement>) getRhsSym(1);
                //#line 2230 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 2232 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 170:  SwitchLabel ::= case ConstantExpression :
            //
            case 170: {
               //#line 2239 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2237 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 2239 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 171:  SwitchLabel ::= default :
            //
            case 171: {
               //#line 2244 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2244 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 172:  WhileStatement ::= while ( Expression ) Statement
            //
            case 172: {
               //#line 2250 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2248 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2248 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2250 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 173:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 173: {
               //#line 2256 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2254 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2254 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2256 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 176:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 176: {
               //#line 2265 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2263 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ForInit> ForInitopt = (List<ForInit>) getRhsSym(3);
                //#line 2263 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 2263 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ForUpdate> ForUpdateopt = (List<ForUpdate>) getRhsSym(7);
                //#line 2263 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 2265 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 178:  ForInit ::= LocalVariableDeclaration
            //
            case 178: {
               //#line 2272 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2270 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<LocalDecl> LocalVariableDeclaration = (List<LocalDecl>) getRhsSym(1);
                //#line 2272 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ForInit> l = new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 180:  StatementExpressionList ::= StatementExpression
            //
            case 180: {
               //#line 2282 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2280 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 2282 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Eval> l = new TypedList<Eval>(new LinkedList<Eval>(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 181:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 181: {
               //#line 2289 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2287 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Eval> StatementExpressionList = (List<Eval>) getRhsSym(1);
                //#line 2287 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 2289 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 182:  BreakStatement ::= break Identifieropt ;
            //
            case 182: {
               //#line 2295 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2293 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 2295 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 183:  ContinueStatement ::= continue Identifieropt ;
            //
            case 183: {
               //#line 2301 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2299 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 2301 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 184:  ReturnStatement ::= return Expressionopt ;
            //
            case 184: {
               //#line 2307 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2305 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 2307 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 185:  ThrowStatement ::= throw Expression ;
            //
            case 185: {
               //#line 2313 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2311 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2313 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 186:  TryStatement ::= try Block Catches
            //
            case 186: {
               //#line 2319 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2317 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2317 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Catch> Catches = (List<Catch>) getRhsSym(3);
                //#line 2319 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 187:  TryStatement ::= try Block Catchesopt Finally
            //
            case 187: {
               //#line 2324 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2322 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2322 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Catch> Catchesopt = (List<Catch>) getRhsSym(3);
                //#line 2322 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 2324 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 188:  Catches ::= CatchClause
            //
            case 188: {
               //#line 2330 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2328 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 2330 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Catch> l = new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 189:  Catches ::= Catches CatchClause
            //
            case 189: {
               //#line 2337 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2335 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Catch> Catches = (List<Catch>) getRhsSym(1);
                //#line 2335 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 2337 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 190:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 190: {
               //#line 2344 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2342 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2342 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2344 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 191:  Finally ::= finally Block
            //
            case 191: {
               //#line 2350 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2348 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2350 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 192:  ClockedClause ::= clocked ( ClockList )
            //
            case 192: {
               //#line 2356 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2354 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ClockList = (List<Expr>) getRhsSym(3);
                //#line 2356 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 193:  AsyncStatement ::= async ClockedClauseopt Statement
            //
            case 193: {
               //#line 2363 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2361 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ClockedClauseopt = (List<Expr>) getRhsSym(2);
                //#line 2361 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2363 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 194:  AsyncStatement ::= clocked async Statement
            //
            case 194: {
               //#line 2368 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2366 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2368 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), Statement, true));
                      break;
            }
    
            //
            // Rule 195:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 195: {
               //#line 2375 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2373 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2373 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2375 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 196:  AtomicStatement ::= atomic Statement
            //
            case 196: {
               //#line 2381 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2379 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2381 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 197:  WhenStatement ::= when ( Expression ) Statement
            //
            case 197: {
               //#line 2388 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2386 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2386 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2388 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 198:  WhenStatement ::= WhenStatement or$or ( Expression ) Statement
            //
            case 198: {
               //#line 2393 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2391 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                When WhenStatement = (When) getRhsSym(1);
                //#line 2391 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken or = (IToken) getRhsIToken(2);
                //#line 2391 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 2391 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 2393 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              WhenStatement.addBranch(pos(getRhsFirstTokenIndex(2), getRightSpan()), Expression, Statement);
              setResult(WhenStatement);
                      break;
            }
    
            //
            // Rule 199:  ForEachStatement ::= foreach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 199: {
               //#line 2400 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2398 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2398 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2398 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ClockedClauseopt = (List<Expr>) getRhsSym(7);
                //#line 2398 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2400 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 200:  ForEachStatement ::= clocked foreach ( LoopIndex in Expression ) Statement
            //
            case 200: {
               //#line 2415 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2413 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(4);
                //#line 2413 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(6);
                //#line 2413 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2415 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 201:  ForEachStatement ::= foreach ( Expression ) Statement
            //
            case 201: {
               //#line 2429 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2427 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2427 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2429 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 202:  ForEachStatement ::= clocked foreach ( Expression ) Statement
            //
            case 202: {
               //#line 2440 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2438 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 2438 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 2440 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Id name = nf.Id(pos(), Name.makeFresh());
                TypeNode type = nf.UnknownTypeNode(pos());
                setResult(nf.ForEach(pos(),
                        nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
                        Expression,
                        Statement));
                      break;
            }
    
            //
            // Rule 203:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 203: {
               //#line 2451 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2449 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2449 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2449 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ClockedClauseopt = (List<Expr>) getRhsSym(7);
                //#line 2449 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2451 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 204:  AtEachStatement ::= clocked ateach ( LoopIndex in Expression ) Statement
            //
            case 204: {
               //#line 2466 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2464 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(4);
                //#line 2464 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(6);
                //#line 2464 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2466 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 205:  AtEachStatement ::= ateach ( Expression ) Statement
            //
            case 205: {
               //#line 2480 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2478 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2478 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2480 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 206:  AtEachStatement ::= clocked ateach ( Expression ) Statement
            //
            case 206: {
               //#line 2491 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2489 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(4);
                //#line 2489 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(6);
                //#line 2491 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Id name = nf.Id(pos(), Name.makeFresh());
                TypeNode type = nf.UnknownTypeNode(pos());
                setResult(nf.AtEach(pos(),
                        nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
                        Expression,
                        Statement));
                      break;
            }
    
            //
            // Rule 207:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 207: {
               //#line 2501 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2499 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2499 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2499 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 2501 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 208:  EnhancedForStatement ::= for ( Expression ) Statement
            //
            case 208: {
               //#line 2515 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2513 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2513 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2515 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Id name = nf.Id(pos(), Name.makeFresh());
                TypeNode type = nf.UnknownTypeNode(pos());
                setResult(nf.ForLoop(pos(),
                        nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
                        Expression,
                        Statement));
                      break;
            }
    
            //
            // Rule 209:  FinishStatement ::= finish Statement
            //
            case 209: {
               //#line 2527 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2525 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2527 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement, false));
                      break;
            }
    
            //
            // Rule 210:  FinishStatement ::= clocked finish Statement
            //
            case 210: {
               //#line 2532 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2530 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2532 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement, true));
                      break;
            }
    
            //
            // Rule 211:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 211: {
               //#line 2537 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2535 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 2537 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 213:  NextStatement ::= next ;
            //
            case 213: {
               //#line 2545 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2545 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 214:  ResumeStatement ::= resume ;
            //
            case 214: {
               //#line 2551 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2551 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Resume(pos()));
                      break;
            }
    
            //
            // Rule 215:  ClockList ::= Clock
            //
            case 215: {
               //#line 2557 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2555 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 2557 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 216:  ClockList ::= ClockList , Clock
            //
            case 216: {
               //#line 2564 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2562 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ClockList = (List<Expr>) getRhsSym(1);
                //#line 2562 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 2564 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 217:  Clock ::= Expression
            //
            case 217: {
               //#line 2572 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2570 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2572 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Expression);
                      break;
            }
    
            //
            // Rule 219:  CastExpression ::= ExpressionName
            //
            case 219: {
               //#line 2585 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2583 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 2585 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 220:  CastExpression ::= CastExpression as Type
            //
            case 220: {
               //#line 2590 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2588 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2588 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2590 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 221:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 221: {
               //#line 2597 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2595 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2597 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 222:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 222: {
               //#line 2604 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2602 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParamWithVarianceList = (List<TypeParamNode>) getRhsSym(1);
                //#line 2602 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2604 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 223:  TypeParameterList ::= TypeParameter
            //
            case 223: {
               //#line 2611 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2609 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2611 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 224:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 224: {
               //#line 2618 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2616 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeParamNode> TypeParameterList = (List<TypeParamNode>) getRhsSym(1);
                //#line 2616 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2618 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 225:  TypeParamWithVariance ::= Identifier
            //
            case 225: {
               //#line 2625 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2623 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2625 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 226:  TypeParamWithVariance ::= + Identifier
            //
            case 226: {
               //#line 2630 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2628 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2630 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 227:  TypeParamWithVariance ::= - Identifier
            //
            case 227: {
               //#line 2635 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2633 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2635 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 228:  TypeParameter ::= Identifier
            //
            case 228: {
               //#line 2641 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2639 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2641 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 229:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 229: {
               //#line 2666 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2664 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2664 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2666 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 230:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Offersopt => ClosureBody
            //
            case 230: {
               //#line 2672 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2670 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(1);
                //#line 2670 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2670 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2670 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(4);
                //#line 2670 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2672 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,  ClosureBody));
                      break;
            }
    
            //
            // Rule 231:  LastExpression ::= Expression
            //
            case 231: {
               //#line 2679 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2677 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2679 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 232:  ClosureBody ::= ConditionalExpression
            //
            case 232: {
               //#line 2685 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2683 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 2685 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), ConditionalExpression, true)));
                      break;
            }
    
            //
            // Rule 233:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 233: {
               //#line 2690 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2688 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 2688 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatementsopt = (List<Stmt>) getRhsSym(3);
                //#line 2688 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2690 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 234:  ClosureBody ::= Annotationsopt Block
            //
            case 234: {
               //#line 2700 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2698 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 2698 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2700 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b.position(pos()));
                      break;
            }
    
            //
            // Rule 235:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 235: {
               //#line 2709 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2707 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2707 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2709 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 236:  FinishExpression ::= finish ( Expression ) Block
            //
            case 236: {
               //#line 2715 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2713 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2713 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2715 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FinishExpr(pos(), Expression, Block));
                      break;
            }
    
            //
            // Rule 237:  WhereClauseopt ::= $Empty
            //
            case 237:
                setResult(null);
                break;

            //
            // Rule 239:  PlaceExpressionSingleListopt ::= $Empty
            //
            case 239:
                setResult(null);
                break;

            //
            // Rule 241:  ClockedClauseopt ::= $Empty
            //
            case 241: {
               //#line 2763 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2763 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 243:  identifier ::= IDENTIFIER$ident
            //
            case 243: {
               //#line 2774 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2772 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2774 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 244:  TypeName ::= Identifier
            //
            case 244: {
               //#line 2781 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2779 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2781 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 245:  TypeName ::= TypeName . Identifier
            //
            case 245: {
               //#line 2786 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2784 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2784 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2786 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 247:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 247: {
               //#line 2798 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2796 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentList = (List<TypeNode>) getRhsSym(2);
                //#line 2798 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 248:  TypeArgumentList ::= Type
            //
            case 248: {
               //#line 2805 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2803 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2805 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> l = new ArrayList<TypeNode>();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 249:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 249: {
               //#line 2812 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2810 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentList = (List<TypeNode>) getRhsSym(1);
                //#line 2810 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2812 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 250:  PackageName ::= Identifier
            //
            case 250: {
               //#line 2822 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2820 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2822 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 251:  PackageName ::= PackageName . Identifier
            //
            case 251: {
               //#line 2827 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2825 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2825 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2827 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 252:  ExpressionName ::= Identifier
            //
            case 252: {
               //#line 2843 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2841 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2843 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 253:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 253: {
               //#line 2848 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2846 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2846 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2848 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 254:  MethodName ::= Identifier
            //
            case 254: {
               //#line 2858 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2856 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2858 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 255:  MethodName ::= AmbiguousName . Identifier
            //
            case 255: {
               //#line 2863 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2861 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2861 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2863 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 256:  PackageOrTypeName ::= Identifier
            //
            case 256: {
               //#line 2873 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2871 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2873 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 257:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 257: {
               //#line 2878 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2876 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2876 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2878 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 258:  AmbiguousName ::= Identifier
            //
            case 258: {
               //#line 2888 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2886 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2888 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 259:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 259: {
               //#line 2893 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2891 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2891 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2893 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 260:  CompilationUnit ::= PackageDeclarationopt TypeDeclarationsopt
            //
            case 260: {
               //#line 2905 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2903 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2903 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) getRhsSym(2);
                //#line 2905 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 261:  CompilationUnit ::= PackageDeclarationopt ImportDeclarations TypeDeclarationsopt
            //
            case 261: {
               //#line 2923 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2921 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2921 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Import> ImportDeclarations = (List<Import>) getRhsSym(2);
                //#line 2921 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) getRhsSym(3);
                //#line 2923 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                                        PackageDeclarationopt,
                                        ImportDeclarations,
                                        TypeDeclarationsopt));
                      break;
            }
    
            //
            // Rule 262:  CompilationUnit ::= ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 262: {
               //#line 2931 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2929 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Import> ImportDeclarations = (List<Import>) getRhsSym(1);
                //#line 2929 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PackageNode misplacedPackageDeclaration = (PackageNode) getRhsSym(2);
                //#line 2929 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Import> misplacedImportDeclarations = (List<Import>) getRhsSym(3);
                //#line 2929 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) getRhsSym(4);
                //#line 2931 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                syntaxError("Misplaced package declaration", misplacedPackageDeclaration.position());
                ImportDeclarations.addAll(misplacedImportDeclarations); // merge the two import lists
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                                        misplacedPackageDeclaration,
                                        ImportDeclarations,
                                        TypeDeclarationsopt));
                      break;
            }
    
            //
            // Rule 263:  CompilationUnit ::= PackageDeclaration ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 263: {
               //#line 2941 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2939 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PackageNode PackageDeclaration = (PackageNode) getRhsSym(1);
                //#line 2939 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Import> ImportDeclarations = (List<Import>) getRhsSym(2);
                //#line 2939 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                PackageNode misplacedPackageDeclaration = (PackageNode) getRhsSym(3);
                //#line 2939 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Import> misplacedImportDeclarations = (List<Import>) getRhsSym(4);
                //#line 2939 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) getRhsSym(5);
                //#line 2941 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                syntaxError("Misplaced package declaration, ignoring", misplacedPackageDeclaration.position());
                ImportDeclarations.addAll(misplacedImportDeclarations); // merge the two import lists
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                                        PackageDeclaration,
                                        ImportDeclarations,
                                        TypeDeclarationsopt));
                      break;
            }
    
            //
            // Rule 264:  ImportDeclarations ::= ImportDeclaration
            //
            case 264: {
               //#line 2952 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2950 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2952 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Import> l = new TypedList<Import>(new LinkedList<Import>(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 265:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 265: {
               //#line 2959 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2957 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Import> ImportDeclarations = (List<Import>) getRhsSym(1);
                //#line 2957 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2959 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 266:  TypeDeclarations ::= TypeDeclaration
            //
            case 266: {
               //#line 2967 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2965 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2967 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TopLevelDecl> l = new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 267:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 267: {
               //#line 2975 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarations = (List<TopLevelDecl>) getRhsSym(1);
                //#line 2973 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2975 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 268:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 268: {
               //#line 2983 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2981 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 2981 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2983 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn.position(pos()));
                      break;
            }
    
            //
            // Rule 271:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 271: {
               //#line 2997 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2995 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2997 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 272:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 272: {
               //#line 3003 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3001 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 3003 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 276:  TypeDeclaration ::= ;
            //
            case 276: {
               //#line 3018 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3018 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 277:  Interfaces ::= implements InterfaceTypeList
            //
            case 277: {
               //#line 3135 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3133 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> InterfaceTypeList = (List<TypeNode>) getRhsSym(2);
                //#line 3135 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 278:  InterfaceTypeList ::= Type
            //
            case 278: {
               //#line 3141 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3139 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 3141 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 279:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 279: {
               //#line 3148 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3146 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> InterfaceTypeList = (List<TypeNode>) getRhsSym(1);
                //#line 3146 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3148 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 280:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 280: {
               //#line 3158 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3156 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> ClassBodyDeclarationsopt = (List<ClassMember>) getRhsSym(2);
                //#line 3158 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 282:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 282: {
               //#line 3165 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3163 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> ClassBodyDeclarations = (List<ClassMember>) getRhsSym(1);
                //#line 3163 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> ClassBodyDeclaration = (List<ClassMember>) getRhsSym(2);
                //#line 3165 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 284:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 284: {
               //#line 3187 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3185 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 3187 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 286:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 286: {
               //#line 3196 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3194 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3196 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 287:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 287: {
               //#line 3203 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3201 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3203 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 288:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 288: {
               //#line 3210 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3208 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3210 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 289:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 289: {
               //#line 3217 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3215 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3217 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 290:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 290: {
               //#line 3224 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3222 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3224 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 291:  ClassMemberDeclaration ::= ;
            //
            case 291: {
               //#line 3231 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3231 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 292:  FormalDeclarators ::= FormalDeclarator
            //
            case 292: {
               //#line 3238 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3236 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 3238 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 293:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 293: {
               //#line 3245 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3243 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> FormalDeclarators = (List<Object[]>) getRhsSym(1);
                //#line 3243 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 3245 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 294:  FieldDeclarators ::= FieldDeclarator
            //
            case 294: {
               //#line 3252 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3250 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 3252 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 295:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 295: {
               //#line 3259 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3257 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> FieldDeclarators = (List<Object[]>) getRhsSym(1);
                //#line 3257 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 3259 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 296:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 296: {
               //#line 3267 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3265 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 3267 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 297:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 297: {
               //#line 3274 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3272 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> VariableDeclaratorsWithType = (List<Object[]>) getRhsSym(1);
                //#line 3272 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 3274 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 298:  VariableDeclarators ::= VariableDeclarator
            //
            case 298: {
               //#line 3281 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3279 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 3281 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 299:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 299: {
               //#line 3288 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3286 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> VariableDeclarators = (List<Object[]>) getRhsSym(1);
                //#line 3286 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 3288 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 301:  ResultType ::= : Type
            //
            case 301: {
               //#line 3344 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3342 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3344 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 302:  HasResultType ::= : Type
            //
            case 302: {
               //#line 3349 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3347 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3349 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 303:  HasResultType ::= <: Type
            //
            case 303: {
               //#line 3354 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3352 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3354 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.HasType(Type));
                      break;
            }
    
            //
            // Rule 304:  FormalParameterList ::= FormalParameter
            //
            case 304: {
               //#line 3369 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3367 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 3369 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 305:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 305: {
               //#line 3376 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3374 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterList = (List<Formal>) getRhsSym(1);
                //#line 3374 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 3376 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 306:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 306: {
               //#line 3382 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3380 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3380 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3382 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 307:  LoopIndexDeclarator ::= [ IdentifierList ] HasResultTypeopt
            //
            case 307: {
               //#line 3387 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3385 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(2);
                //#line 3385 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3387 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 308:  LoopIndexDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt
            //
            case 308: {
               //#line 3392 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3390 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3390 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(3);
                //#line 3390 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3392 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 309:  LoopIndex ::= Modifiersopt LoopIndexDeclarator
            //
            case 309: {
               //#line 3398 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3396 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 3396 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 3398 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 310:  LoopIndex ::= Modifiersopt VarKeyword LoopIndexDeclarator
            //
            case 310: {
               //#line 3421 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 3419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<FlagsNode> VarKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 3419 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 3421 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 311:  FormalParameter ::= Modifiersopt FormalDeclarator
            //
            case 311: {
               //#line 3445 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3443 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 3443 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 3445 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 312:  FormalParameter ::= Modifiersopt VarKeyword FormalDeclarator
            //
            case 312: {
               //#line 3469 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3467 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 3467 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<FlagsNode> VarKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 3467 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 3469 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 313:  FormalParameter ::= Type
            //
            case 313: {
               //#line 3493 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3491 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 3493 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh("id$")), Collections.<Formal>emptyList(), true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 314:  Offers ::= offers Type
            //
            case 314: {
               //#line 3639 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3637 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3639 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 315:  ExceptionTypeList ::= ExceptionType
            //
            case 315: {
               //#line 3645 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3643 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3645 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 316:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 316: {
               //#line 3652 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3650 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> ExceptionTypeList = (List<TypeNode>) getRhsSym(1);
                //#line 3650 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3652 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 318:  MethodBody ::= = LastExpression ;
            //
            case 318: {
               //#line 3660 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3658 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3660 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 319:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 319: {
               //#line 3665 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3663 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(2);
                //#line 3663 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatementsopt = (List<Stmt>) getRhsSym(4);
                //#line 3663 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3665 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult((Block) ((X10Ext) nf.Block(pos(),l).ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 320:  MethodBody ::= = Annotationsopt Block
            //
            case 320: {
               //#line 3673 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3671 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(2);
                //#line 3671 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3673 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 321:  MethodBody ::= Annotationsopt Block
            //
            case 321: {
               //#line 3678 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3676 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 3676 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3678 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 322:  MethodBody ::= ;
            //
            case 322:
                setResult(null);
                break;

            //
            // Rule 323:  ConstructorBody ::= = ConstructorBlock
            //
            case 323: {
               //#line 3749 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3747 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3749 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 324:  ConstructorBody ::= ConstructorBlock
            //
            case 324: {
               //#line 3754 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3752 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3754 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 325:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 325: {
               //#line 3759 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3757 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3759 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 326:  ConstructorBody ::= = AssignPropertyCall
            //
            case 326: {
               //#line 3766 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3764 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3766 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 327:  ConstructorBody ::= ;
            //
            case 327:
                setResult(null);
                break;

            //
            // Rule 328:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 328: {
               //#line 3776 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3774 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3774 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatementsopt = (List<Stmt>) getRhsSym(3);
                //#line 3776 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 329:  Arguments ::= ( ArgumentListopt )
            //
            case 329: {
               //#line 3788 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3786 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(2);
                //#line 3788 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 331:  ExtendsInterfaces ::= extends Type
            //
            case 331: {
               //#line 3845 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3843 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3845 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 332:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 332: {
               //#line 3852 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3850 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> ExtendsInterfaces = (List<TypeNode>) getRhsSym(1);
                //#line 3850 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3852 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 333:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 333: {
               //#line 3861 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3859 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> InterfaceMemberDeclarationsopt = (List<ClassMember>) getRhsSym(2);
                //#line 3861 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 335:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 335: {
               //#line 3868 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3866 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> InterfaceMemberDeclarations = (List<ClassMember>) getRhsSym(1);
                //#line 3866 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> InterfaceMemberDeclaration = (List<ClassMember>) getRhsSym(2);
                //#line 3868 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 336:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 336: {
               //#line 3875 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3873 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3875 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 337:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 337: {
               //#line 3882 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3880 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3882 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 338:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 338: {
               //#line 3889 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3887 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<ClassMember> FieldDeclaration = (List<ClassMember>) getRhsSym(1);
                //#line 3889 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 339:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 339: {
               //#line 3896 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3894 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3896 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 340:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 340: {
               //#line 3903 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3901 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3903 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 341:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 341: {
               //#line 3910 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3908 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3910 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 342:  InterfaceMemberDeclaration ::= ;
            //
            case 342: {
               //#line 3917 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3917 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.<ClassMember>emptyList());
                      break;
            }
    
            //
            // Rule 343:  Annotations ::= Annotation
            //
            case 343: {
               //#line 3923 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3921 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3923 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<AnnotationNode> l = new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 344:  Annotations ::= Annotations Annotation
            //
            case 344: {
               //#line 3930 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3928 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotations = (List<AnnotationNode>) getRhsSym(1);
                //#line 3928 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3930 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 345:  Annotation ::= @ NamedType
            //
            case 345: {
               //#line 3936 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3934 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3936 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 346:  Identifier ::= identifier
            //
            case 346: {
               //#line 3951 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3949 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3951 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 347:  Block ::= { BlockStatementsopt }
            //
            case 347: {
               //#line 3987 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3985 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatementsopt = (List<Stmt>) getRhsSym(2);
                //#line 3987 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 348:  BlockStatements ::= BlockStatement
            //
            case 348: {
               //#line 3993 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3991 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatement = (List<Stmt>) getRhsSym(1);
                //#line 3993 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 349:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 349: {
               //#line 4000 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3998 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatements = (List<Stmt>) getRhsSym(1);
                //#line 3998 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Stmt> BlockStatement = (List<Stmt>) getRhsSym(2);
                //#line 4000 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 351:  BlockStatement ::= ClassDeclaration
            //
            case 351: {
               //#line 4008 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4006 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 4008 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 352:  BlockStatement ::= TypeDefDeclaration
            //
            case 352: {
               //#line 4015 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4013 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 4015 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 353:  BlockStatement ::= Statement
            //
            case 353: {
               //#line 4022 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4020 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 4022 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 354:  IdentifierList ::= Identifier
            //
            case 354: {
               //#line 4030 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4028 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4030 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Id> l = new TypedList<Id>(new LinkedList<Id>(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 355:  IdentifierList ::= IdentifierList , Identifier
            //
            case 355: {
               //#line 4037 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4035 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(1);
                //#line 4035 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4037 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 356:  FormalDeclarator ::= Identifier ResultType
            //
            case 356: {
               //#line 4043 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4041 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4041 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 4043 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, ResultType, null });
                      break;
            }
    
            //
            // Rule 357:  FormalDeclarator ::= [ IdentifierList ] ResultType
            //
            case 357: {
               //#line 4048 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4046 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(2);
                //#line 4046 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 4048 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 358:  FormalDeclarator ::= Identifier [ IdentifierList ] ResultType
            //
            case 358: {
               //#line 4053 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4051 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4051 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(3);
                //#line 4051 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 4053 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 359:  FieldDeclarator ::= Identifier HasResultType
            //
            case 359: {
               //#line 4059 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4057 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4057 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 4059 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), HasResultType, null });
                      break;
            }
    
            //
            // Rule 360:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 360: {
               //#line 4064 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4062 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4062 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 4062 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 4064 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 361:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 361: {
               //#line 4070 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4068 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4068 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 4068 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 4070 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 362:  VariableDeclarator ::= [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 362: {
               //#line 4075 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4073 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(2);
                //#line 4073 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 4073 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 4075 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 363:  VariableDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 363: {
               //#line 4080 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4078 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4078 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(3);
                //#line 4078 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 4078 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 4080 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 364:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 364: {
               //#line 4086 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4084 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4084 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 4084 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 4086 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 365:  VariableDeclaratorWithType ::= [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 365: {
               //#line 4091 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4089 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(2);
                //#line 4089 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(4);
                //#line 4089 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 4091 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 366:  VariableDeclaratorWithType ::= Identifier [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 366: {
               //#line 4096 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4094 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4094 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(3);
                //#line 4094 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(5);
                //#line 4094 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 4096 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 368:  LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
            //
            case 368: {
               //#line 4104 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4102 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 4102 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<FlagsNode> VarKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 4102 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> VariableDeclarators = (List<Object[]>) getRhsSym(3);
                //#line 4104 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 369:  LocalVariableDeclaration ::= Modifiersopt VariableDeclaratorsWithType
            //
            case 369: {
               //#line 4134 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4132 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 4132 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> VariableDeclaratorsWithType = (List<Object[]>) getRhsSym(2);
                //#line 4134 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 370:  LocalVariableDeclaration ::= Modifiersopt VarKeyword FormalDeclarators
            //
            case 370: {
               //#line 4165 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4163 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 4163 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<FlagsNode> VarKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 4163 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Object[]> FormalDeclarators = (List<Object[]>) getRhsSym(3);
                //#line 4165 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 371:  Primary ::= here
            //
            case 371: {
               //#line 4203 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4203 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((X10NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 372:  Primary ::= [ ArgumentListopt ]
            //
            case 372: {
               //#line 4209 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4207 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(2);
                //#line 4209 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 374:  Primary ::= self
            //
            case 374: {
               //#line 4217 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4217 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 375:  Primary ::= this
            //
            case 375: {
               //#line 4222 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4222 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 376:  Primary ::= ClassName . this
            //
            case 376: {
               //#line 4227 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4225 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4227 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 377:  Primary ::= ( Expression )
            //
            case 377: {
               //#line 4232 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4230 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 4232 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 383:  OperatorFunction ::= TypeName . +
            //
            case 383: {
               //#line 4243 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4241 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4243 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,  nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.ADD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 384:  OperatorFunction ::= TypeName . -
            //
            case 384: {
               //#line 4254 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4252 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4254 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SUB, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 385:  OperatorFunction ::= TypeName . *
            //
            case 385: {
               //#line 4265 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4263 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4265 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,   nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MUL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 386:  OperatorFunction ::= TypeName . /
            //
            case 386: {
               //#line 4276 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4274 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4276 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,   nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.DIV, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 387:  OperatorFunction ::= TypeName . %
            //
            case 387: {
               //#line 4287 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4285 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4287 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,   nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.MOD, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 388:  OperatorFunction ::= TypeName . &
            //
            case 388: {
               //#line 4298 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4296 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4298 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,   nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_AND, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 389:  OperatorFunction ::= TypeName . |
            //
            case 389: {
               //#line 4309 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4307 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4309 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,   nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_OR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 390:  OperatorFunction ::= TypeName . ^
            //
            case 390: {
               //#line 4320 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4318 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4320 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.BIT_XOR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 391:  OperatorFunction ::= TypeName . <<
            //
            case 391: {
               //#line 4331 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4329 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4331 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,  nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHL, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 392:  OperatorFunction ::= TypeName . >>
            //
            case 392: {
               //#line 4342 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4340 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4342 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,  nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.SHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 393:  OperatorFunction ::= TypeName . >>>
            //
            case 393: {
               //#line 4353 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4351 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4353 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,   nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.USHR, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 394:  OperatorFunction ::= TypeName . <
            //
            case 394: {
               //#line 4364 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4362 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4364 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,  nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 395:  OperatorFunction ::= TypeName . <=
            //
            case 395: {
               //#line 4375 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4373 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4375 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,  nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.LE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 396:  OperatorFunction ::= TypeName . >=
            //
            case 396: {
               //#line 4386 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4384 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4386 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn, nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 397:  OperatorFunction ::= TypeName . >
            //
            case 397: {
               //#line 4397 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4395 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4397 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,  nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.GT, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 398:  OperatorFunction ::= TypeName . ==
            //
            case 398: {
               //#line 4408 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4406 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4408 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,  nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.EQ, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 399:  OperatorFunction ::= TypeName . !=
            //
            case 399: {
               //#line 4419 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4417 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4419 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> formals = new ArrayList<Formal>();
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "x")));
                formals.add(nf.Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), TypeName.toType(), nf.Id(pos(), "y")));
                TypeNode tn = nf.CanonicalTypeNode(pos(), ts.Boolean());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,  nf.Block(pos(),
                                     nf.X10Return(pos(), nf.Binary(pos(), nf.Local(pos(), nf.Id(pos(), "x")),
                                                           Binary.NE, nf.Local(pos(), nf.Id(pos(), "y"))), true))));
                      break;
            }
    
            //
            // Rule 400:  Literal ::= IntegerLiteral$lit
            //
            case 400: {
               //#line 4432 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4430 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4432 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 401:  Literal ::= LongLiteral$lit
            //
            case 401: {
               //#line 4438 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4436 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4438 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 402:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 402: {
               //#line 4444 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4442 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4444 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 403:  Literal ::= UnsignedLongLiteral$lit
            //
            case 403: {
               //#line 4450 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4448 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4450 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 404:  Literal ::= FloatingPointLiteral$lit
            //
            case 404: {
               //#line 4456 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4454 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4456 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 405:  Literal ::= DoubleLiteral$lit
            //
            case 405: {
               //#line 4462 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4460 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4462 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 406:  Literal ::= BooleanLiteral
            //
            case 406: {
               //#line 4468 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4466 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 4468 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 407:  Literal ::= CharacterLiteral$lit
            //
            case 407: {
               //#line 4473 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4471 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4473 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 408:  Literal ::= StringLiteral$str
            //
            case 408: {
               //#line 4479 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4477 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 4479 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 409:  Literal ::= null
            //
            case 409: {
               //#line 4485 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4485 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 410:  BooleanLiteral ::= true$trueLiteral
            //
            case 410: {
               //#line 4491 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4489 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 4491 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 411:  BooleanLiteral ::= false$falseLiteral
            //
            case 411: {
               //#line 4496 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4494 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 4496 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 412:  ArgumentList ::= Expression
            //
            case 412: {
               //#line 4505 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4503 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 4505 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 413:  ArgumentList ::= ArgumentList , Expression
            //
            case 413: {
               //#line 4512 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4510 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentList = (List<Expr>) getRhsSym(1);
                //#line 4510 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4512 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 414:  FieldAccess ::= Primary . Identifier
            //
            case 414: {
               //#line 4518 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4516 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4516 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4518 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 415:  FieldAccess ::= super . Identifier
            //
            case 415: {
               //#line 4523 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4521 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4523 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 416:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 416: {
               //#line 4528 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4526 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4526 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4526 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4528 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 417:  FieldAccess ::= Primary . class$c
            //
            case 417: {
               //#line 4533 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4531 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4533 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 418:  FieldAccess ::= super . class$c
            //
            case 418: {
               //#line 4538 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4536 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4538 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 419:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 419: {
               //#line 4543 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4541 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4541 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4541 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 4543 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 420:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 420: {
               //#line 4549 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4547 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4547 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 4547 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 4549 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 421:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 421: {
               //#line 4556 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4554 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4554 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4554 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(4);
                //#line 4554 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(6);
                //#line 4556 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 422:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 422: {
               //#line 4561 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(4);
                //#line 4559 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(6);
                //#line 4561 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 423:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 423: {
               //#line 4566 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4564 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4564 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4564 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4564 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(6);
                //#line 4564 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(8);
                //#line 4566 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 424:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 424: {
               //#line 4571 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4569 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4569 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 4569 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 4571 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 425:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 425: {
               //#line 4591 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4589 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4589 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(4);
                //#line 4591 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), formals, (DepParameterExpr) null, tn,  nf.Block(pos(),
                                     nf.X10Return(pos(), nf.X10Call(pos(),
                                                             MethodName.prefix == null ? null : MethodName.prefix.toReceiver(),
                                                             MethodName.name, Collections.<TypeNode>emptyList(), actuals), true))));
                      break;
            }
    
            //
            // Rule 426:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 426: {
               //#line 4604 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4602 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4602 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4602 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(6);
                //#line 4604 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(), formals, (DepParameterExpr) null, tn,  nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), Primary, Identifier, Collections.<TypeNode>emptyList(), actuals), true))));
                      break;
            }
    
            //
            // Rule 427:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 427: {
               //#line 4616 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4614 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4614 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(6);
                //#line 4616 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,  nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier,
                                                     Collections.<TypeNode>emptyList(),    actuals), true))));
                      break;
            }
    
            //
            // Rule 428:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 428: {
               //#line 4629 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4627 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4627 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4627 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4627 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(8);
                //#line 4629 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
//                    List<TypeNode> typeArgs = toTypeArgs(TypeParametersopt);
//                    List<TypeParamNode> typeParams = toTypeParams(TypeParametersopt);
                List<Formal> formals = toFormals(FormalParameterListopt);
                List<Expr> actuals = toActuals(FormalParameterListopt);
                TypeNode tn = nf.UnknownTypeNode(pos());
                setResult(nf.Closure(pos(),  formals, (DepParameterExpr) null, tn,  nf.Block(pos(),
                                     nf.X10Return(pos(),
                                               nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, 
                                                          Collections.<TypeNode>emptyList(), actuals), true))));
                      break;
            }
    
            //
            // Rule 432:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 432: {
               //#line 4647 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4645 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4647 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 433:  PostDecrementExpression ::= PostfixExpression --
            //
            case 433: {
               //#line 4653 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4651 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4653 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 436:  UnannotatedUnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 436: {
               //#line 4661 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4659 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4661 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 437:  UnannotatedUnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 437: {
               //#line 4666 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4664 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4666 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 440:  UnaryExpression ::= Annotations UnannotatedUnaryExpression
            //
            case 440: {
               //#line 4674 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4672 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<AnnotationNode> Annotations = (List<AnnotationNode>) getRhsSym(1);
                //#line 4672 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnannotatedUnaryExpression = (Expr) getRhsSym(2);
                //#line 4674 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnannotatedUnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e.position(pos()));
                      break;
            }
    
            //
            // Rule 441:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 441: {
               //#line 4682 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4680 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4682 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 442:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 442: {
               //#line 4688 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4686 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4688 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 444:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 444: {
               //#line 4695 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4693 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4695 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 445:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 445: {
               //#line 4700 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4698 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4700 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 447:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 447: {
               //#line 4707 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4705 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4705 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4707 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 448:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 448: {
               //#line 4712 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4710 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4710 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4712 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 449:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 449: {
               //#line 4717 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4715 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4715 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4717 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 451:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 451: {
               //#line 4724 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4722 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4722 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4724 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 452:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 452: {
               //#line 4729 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4727 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4727 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4729 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 454:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 454: {
               //#line 4736 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4734 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4734 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4736 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 455:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 455: {
               //#line 4741 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4739 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4739 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4741 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 456:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 456: {
               //#line 4746 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4744 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4744 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4746 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 458:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 458: {
               //#line 4753 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4751 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4751 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4753 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 461:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 461: {
               //#line 4762 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4760 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4760 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4762 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 462:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 462: {
               //#line 4767 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4765 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4765 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4767 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 463:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 463: {
               //#line 4772 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4770 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4770 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4772 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 464:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 464: {
               //#line 4777 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4775 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4775 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4777 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 465:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 465: {
               //#line 4782 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4780 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4780 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4782 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 466:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 466: {
               //#line 4787 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4785 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4785 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4787 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 468:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 468: {
               //#line 4794 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4792 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4792 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4794 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 469:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 469: {
               //#line 4799 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4797 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4797 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4799 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 470:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 470: {
               //#line 4804 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4802 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4802 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4804 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 472:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 472: {
               //#line 4811 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4809 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4809 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4811 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 474:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 474: {
               //#line 4818 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4816 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4816 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4818 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 476:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 476: {
               //#line 4825 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4823 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4823 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4825 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 478:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 478: {
               //#line 4832 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4830 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4830 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4832 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 480:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 480: {
               //#line 4839 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4837 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4837 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4839 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 485:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 485: {
               //#line 4850 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4848 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4848 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4848 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4850 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 488:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 488: {
               //#line 4859 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4857 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4857 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4857 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4859 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 489:  Assignment ::= ExpressionName$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 489: {
               //#line 4864 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4862 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4862 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(3);
                //#line 4862 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4862 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4864 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentListopt, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 490:  Assignment ::= Primary$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 490: {
               //#line 4869 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4867 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4867 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(3);
                //#line 4867 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4867 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4869 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentListopt, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 491:  LeftHandSide ::= ExpressionName
            //
            case 491: {
               //#line 4875 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4873 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4875 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 493:  AssignmentOperator ::= =
            //
            case 493: {
               //#line 4882 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4882 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 494:  AssignmentOperator ::= *=
            //
            case 494: {
               //#line 4887 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4887 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 495:  AssignmentOperator ::= /=
            //
            case 495: {
               //#line 4892 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4892 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 496:  AssignmentOperator ::= %=
            //
            case 496: {
               //#line 4897 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4897 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 497:  AssignmentOperator ::= +=
            //
            case 497: {
               //#line 4902 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4902 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 498:  AssignmentOperator ::= -=
            //
            case 498: {
               //#line 4907 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4907 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 499:  AssignmentOperator ::= <<=
            //
            case 499: {
               //#line 4912 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4912 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 500:  AssignmentOperator ::= >>=
            //
            case 500: {
               //#line 4917 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4917 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 501:  AssignmentOperator ::= >>>=
            //
            case 501: {
               //#line 4922 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4922 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 502:  AssignmentOperator ::= &=
            //
            case 502: {
               //#line 4927 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4927 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 503:  AssignmentOperator ::= ^=
            //
            case 503: {
               //#line 4932 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4932 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 504:  AssignmentOperator ::= |=
            //
            case 504: {
               //#line 4937 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4937 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 507:  PrefixOp ::= +
            //
            case 507: {
               //#line 4948 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4948 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 508:  PrefixOp ::= -
            //
            case 508: {
               //#line 4953 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4953 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 509:  PrefixOp ::= !
            //
            case 509: {
               //#line 4958 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4958 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 510:  PrefixOp ::= ~
            //
            case 510: {
               //#line 4963 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4963 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 511:  BinOp ::= +
            //
            case 511: {
               //#line 4969 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4969 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 512:  BinOp ::= -
            //
            case 512: {
               //#line 4974 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4974 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 513:  BinOp ::= *
            //
            case 513: {
               //#line 4979 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4979 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 514:  BinOp ::= /
            //
            case 514: {
               //#line 4984 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4984 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 515:  BinOp ::= %
            //
            case 515: {
               //#line 4989 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4989 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 516:  BinOp ::= &
            //
            case 516: {
               //#line 4994 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4994 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 517:  BinOp ::= |
            //
            case 517: {
               //#line 4999 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4999 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 518:  BinOp ::= ^
            //
            case 518: {
               //#line 5004 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5004 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 519:  BinOp ::= &&
            //
            case 519: {
               //#line 5009 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5009 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 520:  BinOp ::= ||
            //
            case 520: {
               //#line 5014 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5014 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 521:  BinOp ::= <<
            //
            case 521: {
               //#line 5019 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5019 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 522:  BinOp ::= >>
            //
            case 522: {
               //#line 5024 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5024 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 523:  BinOp ::= >>>
            //
            case 523: {
               //#line 5029 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5029 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 524:  BinOp ::= >=
            //
            case 524: {
               //#line 5034 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5034 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 525:  BinOp ::= <=
            //
            case 525: {
               //#line 5039 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5039 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 526:  BinOp ::= >
            //
            case 526: {
               //#line 5044 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5044 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 527:  BinOp ::= <
            //
            case 527: {
               //#line 5049 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5049 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 528:  BinOp ::= ==
            //
            case 528: {
               //#line 5057 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5057 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 529:  BinOp ::= !=
            //
            case 529: {
               //#line 5062 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5062 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 530:  Catchesopt ::= $Empty
            //
            case 530: {
               //#line 5071 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5071 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false));
                      break;
            }
    
            //
            // Rule 532:  Identifieropt ::= $Empty
            //
            case 532:
                setResult(null);
                break;

            //
            // Rule 533:  Identifieropt ::= Identifier
            //
            case 533: {
               //#line 5080 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 5078 "C:/eclipsews/head1/x10.compiler/src/x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 5080 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 534:  ForUpdateopt ::= $Empty
            //
            case 534: {
               //#line 5086 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5086 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<ForUpdate>(new LinkedList<ForUpdate>(), ForUpdate.class, false));
                      break;
            }
    
            //
            // Rule 536:  Expressionopt ::= $Empty
            //
            case 536:
                setResult(null);
                break;

            //
            // Rule 538:  ForInitopt ::= $Empty
            //
            case 538: {
               //#line 5097 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5097 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 540:  SwitchLabelsopt ::= $Empty
            //
            case 540: {
               //#line 5104 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5104 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Case>(new LinkedList<Case>(), Case.class, false));
                      break;
            }
    
            //
            // Rule 542:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 542: {
               //#line 5111 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5111 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 544:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 544: {
               //#line 5135 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5135 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 546:  ExtendsInterfacesopt ::= $Empty
            //
            case 546: {
               //#line 5142 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5142 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 548:  ClassBodyopt ::= $Empty
            //
            case 548:
                setResult(null);
                break;

            //
            // Rule 550:  ArgumentListopt ::= $Empty
            //
            case 550: {
               //#line 5173 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5173 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 552:  BlockStatementsopt ::= $Empty
            //
            case 552: {
               //#line 5180 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5180 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false));
                      break;
            }
    
            //
            // Rule 554:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 554:
                setResult(null);
                break;

            //
            // Rule 556:  FormalParameterListopt ::= $Empty
            //
            case 556: {
               //#line 5201 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5201 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 558:  Offersopt ::= $Empty
            //
            case 558: {
               //#line 5214 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5214 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 560:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 560: {
               //#line 5251 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5251 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 562:  Interfacesopt ::= $Empty
            //
            case 562: {
               //#line 5258 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5258 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 564:  Superopt ::= $Empty
            //
            case 564:
                setResult(null);
                break;

            //
            // Rule 566:  TypeParametersopt ::= $Empty
            //
            case 566: {
               //#line 5269 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5269 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 568:  FormalParametersopt ::= $Empty
            //
            case 568: {
               //#line 5276 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5276 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 570:  Annotationsopt ::= $Empty
            //
            case 570: {
               //#line 5283 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5283 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 572:  TypeDeclarationsopt ::= $Empty
            //
            case 572: {
               //#line 5290 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5290 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 574:  ImportDeclarationsopt ::= $Empty
            //
            case 574: {
               //#line 5297 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5297 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Import>(new LinkedList<Import>(), Import.class, false));
                      break;
            }
    
            //
            // Rule 576:  PackageDeclarationopt ::= $Empty
            //
            case 576:
                setResult(null);
                break;

            //
            // Rule 578:  HasResultTypeopt ::= $Empty
            //
            case 578:
                setResult(null);
                break;

            //
            // Rule 580:  TypeArgumentsopt ::= $Empty
            //
            case 580: {
               //#line 5318 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5318 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 582:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 582: {
               //#line 5325 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5325 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 584:  Propertiesopt ::= $Empty
            //
            case 584: {
               //#line 5332 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5332 "C:/eclipsews/head1/lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<PropertyDecl>(new LinkedList<PropertyDecl>(), PropertyDecl.class, false));
                      break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

