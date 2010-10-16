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

//#line 32 "x10/parser/x10.g"
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.io.File;

import polyglot.parse.ParsedName;
import x10.ast.AddFlags;
import x10.ast.AmbExpr;
import x10.ast.AmbTypeNode;
import x10.ast.AnnotationNode;
import x10.ast.Assign;
import x10.ast.Binary;
import x10.ast.Block;
import x10.ast.Case;
import x10.ast.Catch;
import x10.ast.ClassBody;
import x10.ast.ClassDecl;
import x10.ast.ClassMember;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.ConstructorCall;
import x10.ast.ConstructorDecl;
import x10.ast.Eval;
import x10.ast.Expr;
import x10.ast.Field;
import x10.ast.FieldDecl;
import x10.ast.FlagsNode;
import x10.ast.FloatLit;
import x10.ast.ForInit;
import x10.ast.ForUpdate;
import x10.ast.Formal;
import x10.ast.Id;
import x10.ast.Import;
import x10.ast.IntLit;
import x10.ast.LocalDecl;
import x10.ast.MethodDecl;
import x10.ast.Node;
import x10.ast.NodeFactory;
import x10.ast.PackageNode;
import x10.ast.ProcedureDecl;
import x10.ast.SettableAssign;
import x10.ast.Here;
import x10.ast.DepParameterExpr;
import x10.ast.SourceFile;
import x10.ast.Stmt;
import x10.ast.SwitchElement;
import x10.ast.TopLevelDecl;
import x10.ast.Tuple;
import x10.ast.TypeNode;
import x10.ast.Unary;
import x10.ast.When;
import x10.ast.X10Formal;
import x10.ast.X10Formal_c;
import x10.ast.X10Loop;
import x10.ast.X10Call;
import x10.ast.ConstantDistMaker;
import x10.ast.TypeDecl;
import x10.ast.TypeParamNode;
import x10.ast.NodeFactory;
import x10.types.Flags;
import x10.types.Name;
import x10.types.ParameterType;
import x10.types.QName;
import x10.types.TypeSystem;
import x10.types.TypeSystem;
import x10.types.TypeSystem_c;
import x10.ast.PropertyDecl;
import x10.ast.RegionMaker;
import x10.ast.X10Binary_c;
import x10.ast.X10Unary_c;
import x10.ast.X10IntLit_c;
import x10.ast.NodeFactory_c;
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
import x10.types.X10Flags;
import x10.types.checker.Converter;
import x10.errors.Errors;
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
        catch (NullExportedSymbolsException e) {
        }
        catch (NullTerminalSymbolsException e) {
        }
        catch (UnimplementedTerminalsException e)
        {
            if (unimplementedSymbolsWarning) {
                java.util.ArrayList<Integer> unimplemented_symbols = e.getSymbols();
                System.out.println("The Lexer will not scan the following token(s):");
                for (int i = 0; i < unimplemented_symbols.size(); i++)
                {
                    Integer id = unimplemented_symbols.get(i);
                    System.out.println("    " + X10Parsersym.orderedTerminalSymbols[id.intValue()]);               
                }
                System.out.println();
            }
        }
        catch (UndefinedEofSymbolException e)
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

    public x10.ast.Node parser()
    {
        return parser(null, 0);
    }
    
    public x10.ast.Node parser(Monitor monitor)
    {
        return parser(monitor, 0);
    }
    
    public x10.ast.Node parser(int error_repair_count)
    {
        return parser(null, error_repair_count);
    }

    public x10.ast.Node parser(Monitor monitor, int error_repair_count)
    {
        btParser.setMonitor(monitor);
        
        try
        {
            return (x10.ast.Node) btParser.fuzzyParse(error_repair_count);
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
    

    //#line 315 "x10/parser/x10.g"
    private ErrorQueue eq;
    private TypeSystem ts;
    private NodeFactory nf;
    private FileSource source;
    private boolean unrecoverableSyntaxError = false;

    public void initialize(TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q)
    {
        this.ts = (TypeSystem) t;
        this.nf = (NodeFactory) n;
        this.source = source;
        this.eq = q;
    }
    
    public X10Parser(ILexStream lexStream, TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q)
    {
        this(lexStream);
        initialize((TypeSystem) t,
                   (NodeFactory) n,
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
    
            Position pos = new JPGPosition("",
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
       // public static int EXTERN      = 2;
        public static int FINAL       = 3;
        //public static int GLOBAL      = 4;
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
          //  if (flag == EXTERN)       return X10Flags.EXTERN;
            if (flag == FINAL)        return Flags.FINAL;
           // if (flag == GLOBAL)       return X10Flags.GLOBAL;
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
            //if (flag == EXTERN)       return "extern";
            if (flag == FINAL)        return "final";
            //if (flag == GLOBAL)       return "global";
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
           // methodModifiers[EXTERN] = true;
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

    public x10.ast.Node parse() {
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


    @SuppressWarnings("unchecked") // Casting Object to various generic types
    public void ruleAction(int ruleNumber)
    {
        switch (ruleNumber)
        {

            //
            // Rule 1:  TypeName ::= TypeName . ErrorId
            //
            case 1: {
               //#line 8 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 6 "x10/parser/MissingId.gi"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 8 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 18 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 16 "x10/parser/MissingId.gi"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 18 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 28 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 26 "x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 28 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 38 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 36 "x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 38 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 48 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 46 "x10/parser/MissingId.gi"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 48 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 58 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 56 "x10/parser/MissingId.gi"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 58 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 68 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 66 "x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 68 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary,
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 74 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 74 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getLeftSpan())),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 80 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 78 "x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 78 "x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 80 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(getRightSpan()), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()),
                                      nf.Id(pos(getRightSpan()), "*")));
                      break;
            }
    
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 87 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 85 "x10/parser/MissingId.gi"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 85 "x10/parser/MissingId.gi"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(3);
                //#line 87 "lpg.generator/templates/java/btParserTemplateF.gi"
                Expr Primary = (Expr) ((Object[]) MethodPrimaryPrefix)[0];
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ((Object[]) MethodPrimaryPrefix)[1];
                setResult(nf.Call(pos(), Primary, nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 94 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 92 "x10/parser/MissingId.gi"
                polyglot.lex.Identifier MethodSuperPrefix = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 92 "x10/parser/MissingId.gi"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(3);
                //#line 94 "lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.Identifier identifier = MethodSuperPrefix;
                setResult(nf.Call(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(), identifier.getIdentifier()), ArgumentListopt));
                      break;
            }
    
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 100 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 98 "x10/parser/MissingId.gi"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 98 "x10/parser/MissingId.gi"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(3);
                //#line 100 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 109 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 107 "x10/parser/MissingId.gi"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 107 "x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 109 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 117 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 115 "x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 117 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(id(getRhsFirstTokenIndex(3)));
                      break;
            }
    
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 122 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 120 "x10/parser/MissingId.gi"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 120 "x10/parser/MissingId.gi"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 120 "x10/parser/MissingId.gi"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 122 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1190 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1190 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new LinkedList<Modifier>());
                      break;
            }
    
            //
            // Rule 17:  Modifiersopt ::= Modifiersopt Modifier
            //
            case 17: {
               //#line 1195 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1193 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1193 "x10/parser/x10.g"
                Modifier Modifier = (Modifier) getRhsSym(2);
                //#line 1195 "lpg.generator/templates/java/btParserTemplateF.gi"
                Modifiersopt.add(Modifier);
                      break;
            }
    
            //
            // Rule 18:  Modifier ::= abstract
            //
            case 18: {
               //#line 1201 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1201 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.ABSTRACT));
                      break;
            }
    
            //
            // Rule 19:  Modifier ::= Annotation
            //
            case 19: {
               //#line 1206 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1204 "x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 1206 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new AnnotationModifier(Annotation));
                      break;
            }
    
            //
            // Rule 20:  Modifier ::= atomic
            //
            case 20: {
               //#line 1211 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1211 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.ATOMIC));
                      break;
            }
    
            //
            // Rule 21:  Modifier ::= final
            //
            case 21: {
               //#line 1221 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1221 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.FINAL));
                      break;
            }
    
            //
            // Rule 22:  Modifier ::= native
            //
            case 22: {
               //#line 1231 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1231 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.NATIVE));
                      break;
            }
    
            //
            // Rule 23:  Modifier ::= private
            //
            case 23: {
               //#line 1236 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1236 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PRIVATE));
                      break;
            }
    
            //
            // Rule 24:  Modifier ::= protected
            //
            case 24: {
               //#line 1241 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1241 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PROTECTED));
                      break;
            }
    
            //
            // Rule 25:  Modifier ::= public
            //
            case 25: {
               //#line 1246 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1246 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.PUBLIC));
                      break;
            }
    
            //
            // Rule 26:  Modifier ::= static
            //
            case 26: {
               //#line 1251 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1251 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.STATIC));
                      break;
            }
    
            //
            // Rule 27:  Modifier ::= transient
            //
            case 27: {
               //#line 1256 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1256 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.TRANSIENT));
                      break;
            }
    
            //
            // Rule 28:  Modifier ::= clocked
            //
            case 28: {
               //#line 1261 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1261 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new FlagModifier(pos(), FlagModifier.CLOCKED));
                      break;
            }
    
            //
            // Rule 30:  MethodModifiersopt ::= MethodModifiersopt property$property
            //
            case 30: {
               //#line 1268 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1266 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1266 "x10/parser/x10.g"
                IToken property = (IToken) getRhsIToken(2);
                //#line 1268 "lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiersopt.add(new FlagModifier(pos(getRhsFirstTokenIndex(2)), FlagModifier.PROPERTY));
                      break;
            }
    
            //
            // Rule 31:  MethodModifiersopt ::= MethodModifiersopt Modifier
            //
            case 31: {
               //#line 1273 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1271 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1271 "x10/parser/x10.g"
                Modifier Modifier = (Modifier) getRhsSym(2);
                //#line 1273 "lpg.generator/templates/java/btParserTemplateF.gi"
                MethodModifiersopt.add(Modifier);
                      break;
            }
    
            //
            // Rule 32:  TypeDefDeclaration ::= Modifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
            //
            case 32: {
               //#line 1279 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1277 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1277 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1277 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1277 "x10/parser/x10.g"
                List<Formal> FormalParametersopt = (List<Formal>) getRhsSym(5);
                //#line 1277 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1277 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1279 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 33:  Properties ::= ( PropertyList )
            //
            case 33: {
               //#line 1299 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1297 "x10/parser/x10.g"
                List<PropertyDecl> PropertyList = (List<PropertyDecl>) getRhsSym(2);
                //#line 1299 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(PropertyList);
                 break;
            } 
            //
            // Rule 34:  PropertyList ::= Property
            //
            case 34: {
               //#line 1304 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1302 "x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(1);
                //#line 1304 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<PropertyDecl> l = new TypedList<PropertyDecl>(new LinkedList<PropertyDecl>(), PropertyDecl.class, false);
                l.add(Property);
                setResult(l);
                      break;
            }
    
            //
            // Rule 35:  PropertyList ::= PropertyList , Property
            //
            case 35: {
               //#line 1311 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1309 "x10/parser/x10.g"
                List<PropertyDecl> PropertyList = (List<PropertyDecl>) getRhsSym(1);
                //#line 1309 "x10/parser/x10.g"
                PropertyDecl Property = (PropertyDecl) getRhsSym(3);
                //#line 1311 "lpg.generator/templates/java/btParserTemplateF.gi"
                PropertyList.add(Property);
                      break;
            }
    
            //
            // Rule 36:  Property ::= Annotationsopt Identifier ResultType
            //
            case 36: {
               //#line 1318 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1316 "x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 1316 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1316 "x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(3);
                //#line 1318 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<AnnotationNode> annotations = extractAnnotations(Annotationsopt);
                PropertyDecl cd = nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), ResultType, Identifier);
                cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
                setResult(cd);
                      break;
            }
    
            //
            // Rule 37:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 37: {
               //#line 1327 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1325 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1325 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1325 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1325 "x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(5);
                //#line 1325 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1325 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1325 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(8);
                //#line 1325 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1327 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 38:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 38: {
               //#line 1360 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1358 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1358 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1358 "x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1358 "x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1358 "x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(9);
                //#line 1358 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(11);
                //#line 1358 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(12);
                //#line 1358 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(13);
                //#line 1358 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(14);
                //#line 1360 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 39:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 39: {
               //#line 1387 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1385 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1385 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1385 "x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1385 "x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(6);
                //#line 1385 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(8);
                //#line 1385 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(9);
                //#line 1385 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(10);
                //#line 1385 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1387 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 40:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 40: {
               //#line 1414 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1412 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1412 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1412 "x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(5);
                //#line 1412 "x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(7);
                //#line 1412 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1412 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1412 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1412 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1414 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 41:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 41: {
               //#line 1441 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1439 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1439 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1439 "x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1439 "x10/parser/x10.g"
                Binary.Operator BinOp = (Binary.Operator) getRhsSym(7);
                //#line 1439 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1439 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1439 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1439 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1441 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 42:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 42: {
               //#line 1468 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1466 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1466 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1466 "x10/parser/x10.g"
                Unary.Operator PrefixOp = (Unary.Operator) getRhsSym(4);
                //#line 1466 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1466 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1466 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(8);
                //#line 1466 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1468 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 43:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 43: {
               //#line 1495 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1493 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1493 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1493 "x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(5);
                //#line 1493 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1493 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1493 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(8);
                //#line 1493 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(9);
                //#line 1495 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 44:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 44: {
               //#line 1517 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1515 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1515 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1515 "x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(5);
                //#line 1515 "x10/parser/x10.g"
                X10Formal fp2 = (X10Formal) getRhsSym(8);
                //#line 1515 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(10);
                //#line 1515 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(11);
                //#line 1515 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(12);
                //#line 1515 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(13);
                //#line 1517 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 45:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Offersopt MethodBody
            //
            case 45: {
               //#line 1539 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1537 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1537 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1537 "x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1537 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1537 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1537 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(10);
                //#line 1537 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(11);
                //#line 1539 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 46:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 46: {
               //#line 1561 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1559 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1559 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1559 "x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1559 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(9);
                //#line 1559 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(10);
                //#line 1559 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(11);
                //#line 1559 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(12);
                //#line 1561 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 47:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 47: {
               //#line 1583 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1581 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1581 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1581 "x10/parser/x10.g"
                X10Formal fp1 = (X10Formal) getRhsSym(5);
                //#line 1581 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(7);
                //#line 1581 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(8);
                //#line 1581 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(9);
                //#line 1581 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(10);
                //#line 1583 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 48:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt MethodBody
            //
            case 48: {
               //#line 1606 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1604 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1604 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1604 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(3);
                //#line 1604 "x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(4);
                //#line 1604 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1604 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(6);
                //#line 1604 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(7);
                //#line 1606 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 49:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier WhereClauseopt HasResultTypeopt MethodBody
            //
            case 49: {
               //#line 1623 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1621 "x10/parser/x10.g"
                List<Modifier> MethodModifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1621 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 1621 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(3);
                //#line 1621 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 1621 "x10/parser/x10.g"
                Block MethodBody = (Block) getRhsSym(5);
                //#line 1623 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 50:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 50: {
               //#line 1641 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1639 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 1639 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 1641 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 51:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 51: {
               //#line 1646 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1644 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 1644 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 1646 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 52:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 52: {
               //#line 1651 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1649 "x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1649 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(4);
                //#line 1649 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(6);
                //#line 1651 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 53:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 53: {
               //#line 1656 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1654 "x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1654 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(4);
                //#line 1654 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(6);
                //#line 1656 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 54:  NormalInterfaceDeclaration ::= Modifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 54: {
               //#line 1662 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1660 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1660 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1660 "x10/parser/x10.g"
                List<TypeParamNode> TypeParamsWithVarianceopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1660 "x10/parser/x10.g"
                List<PropertyDecl> Propertiesopt = (List<PropertyDecl>) getRhsSym(5);
                //#line 1660 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1660 "x10/parser/x10.g"
                List<TypeNode> ExtendsInterfacesopt = (List<TypeNode>) getRhsSym(7);
                //#line 1660 "x10/parser/x10.g"
                ClassBody InterfaceBody = (ClassBody) getRhsSym(8);
                //#line 1662 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 55:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 55: {
               //#line 1684 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1682 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1682 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(3);
                //#line 1682 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(5);
                //#line 1682 "x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(7);
                //#line 1684 "lpg.generator/templates/java/btParserTemplateF.gi"
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt)) ;
                      break;
            }
    
            //
            // Rule 56:  ClassInstanceCreationExpression ::= new TypeName [ Type ] [ ArgumentListopt ]
            //
            case 56: {
               //#line 1691 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1689 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 1689 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(4);
                //#line 1689 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(7);
                //#line 1691 "lpg.generator/templates/java/btParserTemplateF.gi"
                String arrayTypeName = TypeName.name.id().toString();
                if (! (arrayTypeName.equals("x10.array.Array") || arrayTypeName.equals("Array")))
                    syntaxError(new Errors.ArrayLiteralMustBeOfArrayType(arrayTypeName, TypeName.pos).getMessage(),TypeName.pos);
                setResult(nf.Tuple(pos(), Type, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 57:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 57: {
               //#line 1699 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1697 "x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1697 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1697 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(5);
                //#line 1697 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(7);
                //#line 1697 "x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1699 "lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 58:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 58: {
               //#line 1707 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1705 "x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 1705 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(4);
                //#line 1705 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(5);
                //#line 1705 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(7);
                //#line 1705 "x10/parser/x10.g"
                ClassBody ClassBodyopt = (ClassBody) getRhsSym(9);
                //#line 1707 "lpg.generator/templates/java/btParserTemplateF.gi"
                ParsedName b = new X10ParsedName(nf, ts, pos(), Identifier);
                if (ClassBodyopt == null)
                     setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
                else setResult(nf.X10New(pos(), AmbiguousName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
                      break;
            }
    
            //
            // Rule 59:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 59: {
               //#line 1716 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1714 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 1714 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 1716 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 62:  FunctionType ::= TypeParametersopt ( FormalParameterListopt ) WhereClauseopt Offersopt => Type
            //
            case 62: {
               //#line 1726 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1724 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(1);
                //#line 1724 "x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(3);
                //#line 1724 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(5);
                //#line 1724 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(6);
                //#line 1724 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(8);
                //#line 1726 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FunctionTypeNode(pos(), TypeParametersopt, FormalParameterListopt, WhereClauseopt, Type,  Offersopt));
                      break;
            }
    
            //
            // Rule 64:  AnnotatedType ::= Type Annotations
            //
            case 64: {
               //#line 1739 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1737 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 1737 "x10/parser/x10.g"
                List<AnnotationNode> Annotations = (List<AnnotationNode>) getRhsSym(2);
                //#line 1739 "lpg.generator/templates/java/btParserTemplateF.gi"
                TypeNode tn = Type;
                tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
                setResult(tn.position(pos()));
                      break;
            }
    
            //
            // Rule 67:  ConstrainedType ::= ( Type )
            //
            case 67: {
               //#line 1749 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1747 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 1749 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 69:  SimpleNamedType ::= TypeName
            //
            case 69: {
               //#line 1763 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1761 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 1763 "lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(TypeName.toType());
                      break;
            }
    
            //
            // Rule 70:  SimpleNamedType ::= Primary . Identifier
            //
            case 70: {
               //#line 1768 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1766 "x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 1766 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1768 "lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(nf.AmbTypeNode(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 71:  SimpleNamedType ::= DepNamedType . Identifier
            //
            case 71: {
               //#line 1773 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1771 "x10/parser/x10.g"
                TypeNode DepNamedType = (TypeNode) getRhsSym(1);
                //#line 1771 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1773 "lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(nf.AmbTypeNode(pos(), DepNamedType, Identifier));
                      break;
            }
    
            //
            // Rule 72:  DepNamedType ::= SimpleNamedType DepParameters
            //
            case 72: {
               //#line 1779 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1777 "x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1777 "x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(2);
                //#line 1779 "lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false),
                                              new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 73:  DepNamedType ::= SimpleNamedType Arguments
            //
            case 73: {
               //#line 1788 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1786 "x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1786 "x10/parser/x10.g"
                List<Expr> Arguments = (List<Expr>) getRhsSym(2);
                //#line 1788 "lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false),
                                              Arguments,
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 74:  DepNamedType ::= SimpleNamedType Arguments DepParameters
            //
            case 74: {
               //#line 1797 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1795 "x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1795 "x10/parser/x10.g"
                List<Expr> Arguments = (List<Expr>) getRhsSym(2);
                //#line 1795 "x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(3);
                //#line 1797 "lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false),
                                              Arguments,
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 75:  DepNamedType ::= SimpleNamedType TypeArguments
            //
            case 75: {
               //#line 1806 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1804 "x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1804 "x10/parser/x10.g"
                List<TypeNode> TypeArguments = (List<TypeNode>) getRhsSym(2);
                //#line 1806 "lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 76:  DepNamedType ::= SimpleNamedType TypeArguments DepParameters
            //
            case 76: {
               //#line 1815 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1813 "x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1813 "x10/parser/x10.g"
                List<TypeNode> TypeArguments = (List<TypeNode>) getRhsSym(2);
                //#line 1813 "x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(3);
                //#line 1815 "lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 77:  DepNamedType ::= SimpleNamedType TypeArguments Arguments
            //
            case 77: {
               //#line 1824 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1822 "x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1822 "x10/parser/x10.g"
                List<TypeNode> TypeArguments = (List<TypeNode>) getRhsSym(2);
                //#line 1822 "x10/parser/x10.g"
                List<Expr> Arguments = (List<Expr>) getRhsSym(3);
                //#line 1824 "lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              Arguments,
                                              null);
            setResult(type);
                      break;
            }
    
            //
            // Rule 78:  DepNamedType ::= SimpleNamedType TypeArguments Arguments DepParameters
            //
            case 78: {
               //#line 1833 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1831 "x10/parser/x10.g"
                TypeNode SimpleNamedType = (TypeNode) getRhsSym(1);
                //#line 1831 "x10/parser/x10.g"
                List<TypeNode> TypeArguments = (List<TypeNode>) getRhsSym(2);
                //#line 1831 "x10/parser/x10.g"
                List<Expr> Arguments = (List<Expr>) getRhsSym(3);
                //#line 1831 "x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(4);
                //#line 1833 "lpg.generator/templates/java/btParserTemplateF.gi"
            TypeNode type = nf.AmbDepTypeNode(pos(), ((AmbTypeNode) SimpleNamedType).prefix(), ((AmbTypeNode) SimpleNamedType).name(),
                                              TypeArguments,
                                              Arguments,
                                              DepParameters);
            setResult(type);
                      break;
            }
    
            //
            // Rule 81:  DepParameters ::= { ExistentialListopt Conjunctionopt }
            //
            case 81: {
               //#line 1846 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1844 "x10/parser/x10.g"
                List<Formal> ExistentialListopt = (List<Formal>) getRhsSym(2);
                //#line 1844 "x10/parser/x10.g"
                List<Expr> Conjunctionopt = (List<Expr>) getRhsSym(3);
                //#line 1846 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.DepParameterExpr(pos(), ExistentialListopt, Conjunctionopt));
                      break;
            }
    
            //
            // Rule 82:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 82: {
               //#line 1853 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1851 "x10/parser/x10.g"
                List<TypeParamNode> TypeParamWithVarianceList = (List<TypeParamNode>) getRhsSym(2);
                //#line 1853 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 83:  TypeParameters ::= [ TypeParameterList ]
            //
            case 83: {
               //#line 1859 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1857 "x10/parser/x10.g"
                List<TypeParamNode> TypeParameterList = (List<TypeParamNode>) getRhsSym(2);
                //#line 1859 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 84:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 84: {
               //#line 1865 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1863 "x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(2);
                //#line 1865 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(FormalParameterListopt);
                      break;
            }
    
            //
            // Rule 85:  Conjunction ::= Expression
            //
            case 85: {
               //#line 1871 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1869 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 1871 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Expr> l = new ArrayList<Expr>();
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 86:  Conjunction ::= Conjunction , Expression
            //
            case 86: {
               //#line 1878 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1876 "x10/parser/x10.g"
                List<Expr> Conjunction = (List<Expr>) getRhsSym(1);
                //#line 1876 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 1878 "lpg.generator/templates/java/btParserTemplateF.gi"
                Conjunction.add(Expression);
                      break;
            }
    
            //
            // Rule 87:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 87: {
               //#line 1884 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1882 "x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1882 "x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1884 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, false));
                      break;
            }
    
            //
            // Rule 88:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 88: {
               //#line 1889 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1887 "x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 1887 "x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 1889 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t2, t1, false));
                      break;
            }
    
            //
            // Rule 89:  WhereClause ::= DepParameters
            //
            case 89: {
               //#line 1895 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1893 "x10/parser/x10.g"
                DepParameterExpr DepParameters = (DepParameterExpr) getRhsSym(1);
                //#line 1895 "lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(DepParameters);
                      break;
            }
      
            //
            // Rule 90:  Conjunctionopt ::= $Empty
            //
            case 90: {
               //#line 1901 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1901 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Expr> l = new ArrayList<Expr>();
                setResult(l);
                      break;
            }
      
            //
            // Rule 91:  Conjunctionopt ::= Conjunction
            //
            case 91: {
               //#line 1907 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1905 "x10/parser/x10.g"
                List<Expr> Conjunction = (List<Expr>) getRhsSym(1);
                //#line 1907 "lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(Conjunction);
                      break;
            }
    
            //
            // Rule 92:  ExistentialListopt ::= $Empty
            //
            case 92: {
               //#line 1913 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1913 "lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(new ArrayList<Formal>());
                      break;
            }
      
            //
            // Rule 93:  ExistentialListopt ::= ExistentialList ;
            //
            case 93: {
               //#line 1918 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1916 "x10/parser/x10.g"
                List<Formal> ExistentialList = (List<Formal>) getRhsSym(1);
                //#line 1918 "lpg.generator/templates/java/btParserTemplateF.gi"
            setResult(ExistentialList);
                      break;
            }
    
            //
            // Rule 94:  ExistentialList ::= FormalParameter
            //
            case 94: {
               //#line 1924 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1922 "x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 1924 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
                l.add(FormalParameter.flags(nf.FlagsNode(NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                setResult(l);
                      break;
            }
    
            //
            // Rule 95:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 95: {
               //#line 1931 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1929 "x10/parser/x10.g"
                List<Formal> ExistentialList = (List<Formal>) getRhsSym(1);
                //#line 1929 "x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 1931 "lpg.generator/templates/java/btParserTemplateF.gi"
                ExistentialList.add(FormalParameter.flags(nf.FlagsNode(NodeFactory_c.compilerGenerated(FormalParameter), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 98:  NormalClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 98: {
               //#line 1942 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1940 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1940 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1940 "x10/parser/x10.g"
                List<TypeParamNode> TypeParamsWithVarianceopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1940 "x10/parser/x10.g"
                List<PropertyDecl> Propertiesopt = (List<PropertyDecl>) getRhsSym(5);
                //#line 1940 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1940 "x10/parser/x10.g"
                TypeNode Superopt = (TypeNode) getRhsSym(7);
                //#line 1940 "x10/parser/x10.g"
                List<TypeNode> Interfacesopt = (List<TypeNode>) getRhsSym(8);
                //#line 1940 "x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(9);
                //#line 1942 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 99:  StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 99: {
               //#line 1960 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1958 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1958 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 1958 "x10/parser/x10.g"
                List<TypeParamNode> TypeParamsWithVarianceopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1958 "x10/parser/x10.g"
                List<PropertyDecl> Propertiesopt = (List<PropertyDecl>) getRhsSym(5);
                //#line 1958 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1958 "x10/parser/x10.g"
                List<TypeNode> Interfacesopt = (List<TypeNode>) getRhsSym(7);
                //#line 1958 "x10/parser/x10.g"
                ClassBody ClassBody = (ClassBody) getRhsSym(8);
                //#line 1960 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 100:  ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt ConstructorBody
            //
            case 100: {
               //#line 1975 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1973 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 1973 "x10/parser/x10.g"
                List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) getRhsSym(4);
                //#line 1973 "x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(5);
                //#line 1973 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(6);
                //#line 1973 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(7);
                //#line 1973 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(8);
                //#line 1973 "x10/parser/x10.g"
                Block ConstructorBody = (Block) getRhsSym(9);
                //#line 1975 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 101:  Super ::= extends ClassType
            //
            case 101: {
               //#line 1993 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1991 "x10/parser/x10.g"
                TypeNode ClassType = (TypeNode) getRhsSym(2);
                //#line 1993 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClassType);
                      break;
            }
    
            //
            // Rule 102:  FieldKeyword ::= val
            //
            case 102: {
               //#line 1999 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1999 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 103:  FieldKeyword ::= var
            //
            case 103: {
               //#line 2004 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2004 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 104:  VarKeyword ::= val
            //
            case 104: {
               //#line 2012 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2012 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
                      break;
            }
    
            //
            // Rule 105:  VarKeyword ::= var
            //
            case 105: {
               //#line 2017 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2017 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
                      break;
            }
    
            //
            // Rule 106:  FieldDeclaration ::= Modifiersopt FieldKeyword FieldDeclarators ;
            //
            case 106: {
               //#line 2024 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2022 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 2022 "x10/parser/x10.g"
                List<FlagsNode> FieldKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 2022 "x10/parser/x10.g"
                List<Object[]> FieldDeclarators = (List<Object[]>) getRhsSym(3);
                //#line 2024 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 107:  FieldDeclaration ::= Modifiersopt FieldDeclarators ;
            //
            case 107: {
               //#line 2049 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2047 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 2047 "x10/parser/x10.g"
                List<Object[]> FieldDeclarators = (List<Object[]>) getRhsSym(2);
                //#line 2049 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 110:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 110: {
               //#line 2081 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2079 "x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 2079 "x10/parser/x10.g"
                Stmt NonExpressionStatement = (Stmt) getRhsSym(2);
                //#line 2081 "lpg.generator/templates/java/btParserTemplateF.gi"
                if (NonExpressionStatement.ext() instanceof X10Ext) {
                    NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations(Annotationsopt);
                }
                setResult(NonExpressionStatement.position(pos()));
                      break;
            }
    
            //
            // Rule 136:  OfferStatement ::= offer Expression ;
            //
            case 136: {
               //#line 2118 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2116 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2118 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Offer(pos(), Expression));
                      break;
            }
    
            //
            // Rule 137:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 137: {
               //#line 2124 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2122 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2122 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2124 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 138:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 138: {
               //#line 2130 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2128 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2128 "x10/parser/x10.g"
                Stmt s1 = (Stmt) getRhsSym(5);
                //#line 2128 "x10/parser/x10.g"
                Stmt s2 = (Stmt) getRhsSym(7);
                //#line 2130 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.If(pos(), Expression, s1, s2));
                      break;
            }
    
            //
            // Rule 139:  EmptyStatement ::= ;
            //
            case 139: {
               //#line 2136 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2136 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Empty(pos()));
                      break;
            }
    
            //
            // Rule 140:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 140: {
               //#line 2142 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2140 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2140 "x10/parser/x10.g"
                Stmt LoopStatement = (Stmt) getRhsSym(3);
                //#line 2142 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Labeled(pos(), Identifier, LoopStatement));
                      break;
            }
    
            //
            // Rule 145:  ExpressionStatement ::= StatementExpression ;
            //
            case 145: {
               //#line 2154 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2152 "x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 2154 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 153:  AssertStatement ::= assert Expression ;
            //
            case 153: {
               //#line 2168 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2166 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2168 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), Expression));
                      break;
            }
    
            //
            // Rule 154:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 154: {
               //#line 2173 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2171 "x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(2);
                //#line 2171 "x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(4);
                //#line 2173 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assert(pos(), expr1, expr2));
                      break;
            }
    
            //
            // Rule 155:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 155: {
               //#line 2179 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2177 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2177 "x10/parser/x10.g"
                List<SwitchElement> SwitchBlock = (List<SwitchElement>) getRhsSym(5);
                //#line 2179 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Switch(pos(), Expression, SwitchBlock));
                      break;
            }
    
            //
            // Rule 156:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 156: {
               //#line 2185 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2183 "x10/parser/x10.g"
                List<Stmt> SwitchBlockStatementGroupsopt = (List<Stmt>) getRhsSym(2);
                //#line 2183 "x10/parser/x10.g"
                List<Case> SwitchLabelsopt = (List<Case>) getRhsSym(3);
                //#line 2185 "lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
                setResult(SwitchBlockStatementGroupsopt);
                      break;
            }
    
            //
            // Rule 158:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 158: {
               //#line 2193 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2191 "x10/parser/x10.g"
                List<SwitchElement> SwitchBlockStatementGroups = (List<SwitchElement>) getRhsSym(1);
                //#line 2191 "x10/parser/x10.g"
                List<SwitchElement> SwitchBlockStatementGroup = (List<SwitchElement>) getRhsSym(2);
                //#line 2193 "lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
                // setResult(SwitchBlockStatementGroups);
                      break;
            }
    
            //
            // Rule 159:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 159: {
               //#line 2200 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2198 "x10/parser/x10.g"
                List<SwitchElement> SwitchLabels = (List<SwitchElement>) getRhsSym(1);
                //#line 2198 "x10/parser/x10.g"
                List<Stmt> BlockStatements = (List<Stmt>) getRhsSym(2);
                //#line 2200 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<SwitchElement> l = new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false);
                l.addAll(SwitchLabels);
                l.add(nf.SwitchBlock(pos(), BlockStatements));
                setResult(l);
                      break;
            }
    
            //
            // Rule 160:  SwitchLabels ::= SwitchLabel
            //
            case 160: {
               //#line 2209 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2207 "x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(1);
                //#line 2209 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Case> l = new TypedList<Case>(new LinkedList<Case>(), Case.class, false);
                l.add(SwitchLabel);
                setResult(l);
                      break;
            }
    
            //
            // Rule 161:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 161: {
               //#line 2216 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2214 "x10/parser/x10.g"
                List<SwitchElement> SwitchLabels = (List<SwitchElement>) getRhsSym(1);
                //#line 2214 "x10/parser/x10.g"
                Case SwitchLabel = (Case) getRhsSym(2);
                //#line 2216 "lpg.generator/templates/java/btParserTemplateF.gi"
                SwitchLabels.add(SwitchLabel);
                //setResult(SwitchLabels);
                      break;
            }
    
            //
            // Rule 162:  SwitchLabel ::= case ConstantExpression :
            //
            case 162: {
               //#line 2223 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2221 "x10/parser/x10.g"
                Expr ConstantExpression = (Expr) getRhsSym(2);
                //#line 2223 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Case(pos(), ConstantExpression));
                      break;
            }
    
            //
            // Rule 163:  SwitchLabel ::= default :
            //
            case 163: {
               //#line 2228 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2228 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Default(pos()));
                      break;
            }
    
            //
            // Rule 164:  WhileStatement ::= while ( Expression ) Statement
            //
            case 164: {
               //#line 2234 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2232 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2232 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2234 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.While(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 165:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 165: {
               //#line 2240 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2238 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2238 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2240 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Do(pos(), Statement, Expression));
                      break;
            }
    
            //
            // Rule 168:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 168: {
               //#line 2249 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2247 "x10/parser/x10.g"
                List<ForInit> ForInitopt = (List<ForInit>) getRhsSym(3);
                //#line 2247 "x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(5);
                //#line 2247 "x10/parser/x10.g"
                List<ForUpdate> ForUpdateopt = (List<ForUpdate>) getRhsSym(7);
                //#line 2247 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(9);
                //#line 2249 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
                      break;
            }
    
            //
            // Rule 170:  ForInit ::= LocalVariableDeclaration
            //
            case 170: {
               //#line 2256 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2254 "x10/parser/x10.g"
                List<LocalDecl> LocalVariableDeclaration = (List<LocalDecl>) getRhsSym(1);
                //#line 2256 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ForInit> l = new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false);
                l.addAll(LocalVariableDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 172:  StatementExpressionList ::= StatementExpression
            //
            case 172: {
               //#line 2266 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2264 "x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(1);
                //#line 2266 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Eval> l = new TypedList<Eval>(new LinkedList<Eval>(), Eval.class, false);
                l.add(nf.Eval(pos(), StatementExpression));
                setResult(l);
                      break;
            }
    
            //
            // Rule 173:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 173: {
               //#line 2273 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2271 "x10/parser/x10.g"
                List<Eval> StatementExpressionList = (List<Eval>) getRhsSym(1);
                //#line 2271 "x10/parser/x10.g"
                Expr StatementExpression = (Expr) getRhsSym(3);
                //#line 2273 "lpg.generator/templates/java/btParserTemplateF.gi"
                StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
                      break;
            }
    
            //
            // Rule 174:  BreakStatement ::= break Identifieropt ;
            //
            case 174: {
               //#line 2279 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2277 "x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 2279 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Break(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 175:  ContinueStatement ::= continue Identifieropt ;
            //
            case 175: {
               //#line 2285 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2283 "x10/parser/x10.g"
                Id Identifieropt = (Id) getRhsSym(2);
                //#line 2285 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Continue(pos(), Identifieropt));
                      break;
            }
    
            //
            // Rule 176:  ReturnStatement ::= return Expressionopt ;
            //
            case 176: {
               //#line 2291 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2289 "x10/parser/x10.g"
                Expr Expressionopt = (Expr) getRhsSym(2);
                //#line 2291 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Return(pos(), Expressionopt));
                      break;
            }
    
            //
            // Rule 177:  ThrowStatement ::= throw Expression ;
            //
            case 177: {
               //#line 2297 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2295 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 2297 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Throw(pos(), Expression));
                      break;
            }
    
            //
            // Rule 178:  TryStatement ::= try Block Catches
            //
            case 178: {
               //#line 2303 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2301 "x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2301 "x10/parser/x10.g"
                List<Catch> Catches = (List<Catch>) getRhsSym(3);
                //#line 2303 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catches));
                      break;
            }
    
            //
            // Rule 179:  TryStatement ::= try Block Catchesopt Finally
            //
            case 179: {
               //#line 2308 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2306 "x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2306 "x10/parser/x10.g"
                List<Catch> Catchesopt = (List<Catch>) getRhsSym(3);
                //#line 2306 "x10/parser/x10.g"
                Block Finally = (Block) getRhsSym(4);
                //#line 2308 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Try(pos(), Block, Catchesopt, Finally));
                      break;
            }
    
            //
            // Rule 180:  Catches ::= CatchClause
            //
            case 180: {
               //#line 2314 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2312 "x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(1);
                //#line 2314 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Catch> l = new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false);
                l.add(CatchClause);
                setResult(l);
                      break;
            }
    
            //
            // Rule 181:  Catches ::= Catches CatchClause
            //
            case 181: {
               //#line 2321 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2319 "x10/parser/x10.g"
                List<Catch> Catches = (List<Catch>) getRhsSym(1);
                //#line 2319 "x10/parser/x10.g"
                Catch CatchClause = (Catch) getRhsSym(2);
                //#line 2321 "lpg.generator/templates/java/btParserTemplateF.gi"
                Catches.add(CatchClause);
                //setResult(Catches);
                      break;
            }
    
            //
            // Rule 182:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 182: {
               //#line 2328 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2326 "x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 2326 "x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2328 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Catch(pos(), FormalParameter, Block));
                      break;
            }
    
            //
            // Rule 183:  Finally ::= finally Block
            //
            case 183: {
               //#line 2334 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2332 "x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2334 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Block);
                      break;
            }
    
            //
            // Rule 184:  ClockedClause ::= clocked ( ClockList )
            //
            case 184: {
               //#line 2340 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2338 "x10/parser/x10.g"
                List<Expr> ClockList = (List<Expr>) getRhsSym(3);
                //#line 2340 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 185:  AsyncStatement ::= async ClockedClauseopt Statement
            //
            case 185: {
               //#line 2347 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2345 "x10/parser/x10.g"
                List<Expr> ClockedClauseopt = (List<Expr>) getRhsSym(2);
                //#line 2345 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2347 "lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), ClockedClauseopt, Statement));
                      break;
            }
    
            //
            // Rule 186:  AsyncStatement ::= clocked async Statement
            //
            case 186: {
               //#line 2352 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2350 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2352 "lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Async(pos(), Statement, true));
                      break;
            }
    
            //
            // Rule 187:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 187: {
               //#line 2359 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2357 "x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2357 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2359 "lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.AtStmt(pos(), PlaceExpressionSingleList, Statement));
                      break;
            }
    
            //
            // Rule 188:  AtomicStatement ::= atomic Statement
            //
            case 188: {
               //#line 2365 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2363 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2365 "lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
                      break;
            }
    
            //
            // Rule 189:  WhenStatement ::= when ( Expression ) Statement
            //
            case 189: {
               //#line 2372 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2370 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2370 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2372 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.When(pos(), Expression, Statement));
                      break;
            }
    
            //
            // Rule 190:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 190: {
               //#line 2435 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2433 "x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2433 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2433 "x10/parser/x10.g"
                List<Expr> ClockedClauseopt = (List<Expr>) getRhsSym(7);
                //#line 2433 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(8);
                //#line 2435 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 191:  AtEachStatement ::= ateach ( Expression ) Statement
            //
            case 191: {
               //#line 2450 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2448 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2448 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2450 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 192:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 192: {
               //#line 2461 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2459 "x10/parser/x10.g"
                X10Formal LoopIndex = (X10Formal) getRhsSym(3);
                //#line 2459 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(5);
                //#line 2459 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(7);
                //#line 2461 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 193:  EnhancedForStatement ::= for ( Expression ) Statement
            //
            case 193: {
               //#line 2475 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2473 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2473 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(5);
                //#line 2475 "lpg.generator/templates/java/btParserTemplateF.gi"
                Id name = nf.Id(pos(), Name.makeFresh());
                TypeNode type = nf.UnknownTypeNode(pos());
                setResult(nf.ForLoop(pos(),
                        nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
                        Expression,
                        Statement));
                      break;
            }
    
            //
            // Rule 194:  FinishStatement ::= finish Statement
            //
            case 194: {
               //#line 2487 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2485 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(2);
                //#line 2487 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement, false));
                      break;
            }
    
            //
            // Rule 195:  FinishStatement ::= clocked finish Statement
            //
            case 195: {
               //#line 2492 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2490 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(3);
                //#line 2492 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Finish(pos(),  Statement, true));
                      break;
            }
    
            //
            // Rule 196:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 196: {
               //#line 2497 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2495 "x10/parser/x10.g"
                Expr PlaceExpression = (Expr) getRhsSym(2);
                //#line 2497 "lpg.generator/templates/java/btParserTemplateF.gi"
              setResult(PlaceExpression);
                      break;
            }
    
            //
            // Rule 198:  NextStatement ::= next ;
            //
            case 198: {
               //#line 2505 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2505 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Next(pos()));
                      break;
            }
    
            //
            // Rule 199:  ResumeStatement ::= resume ;
            //
            case 199: {
               //#line 2511 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2511 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Resume(pos()));
                      break;
            }
    
            //
            // Rule 200:  ClockList ::= Clock
            //
            case 200: {
               //#line 2517 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2515 "x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(1);
                //#line 2517 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
                l.add(Clock);
                setResult(l);
                      break;
            }
    
            //
            // Rule 201:  ClockList ::= ClockList , Clock
            //
            case 201: {
               //#line 2524 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2522 "x10/parser/x10.g"
                List<Expr> ClockList = (List<Expr>) getRhsSym(1);
                //#line 2522 "x10/parser/x10.g"
                Expr Clock = (Expr) getRhsSym(3);
                //#line 2524 "lpg.generator/templates/java/btParserTemplateF.gi"
                ClockList.add(Clock);
                setResult(ClockList);
                      break;
            }
    
            //
            // Rule 202:  Clock ::= Expression
            //
            case 202: {
               //#line 2532 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2530 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2532 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Expression);
                      break;
            }
    
            //
            // Rule 204:  CastExpression ::= ExpressionName
            //
            case 204: {
               //#line 2545 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2543 "x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 2545 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 205:  CastExpression ::= CastExpression as Type
            //
            case 205: {
               //#line 2550 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2548 "x10/parser/x10.g"
                Expr CastExpression = (Expr) getRhsSym(1);
                //#line 2548 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2550 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Cast(pos(), Type, CastExpression));
                      break;
            }
    
            //
            // Rule 206:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 206: {
               //#line 2557 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2555 "x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(1);
                //#line 2557 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
                l.add(TypeParamWithVariance);
                setResult(l);
                      break;
            }
    
            //
            // Rule 207:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 207: {
               //#line 2564 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2562 "x10/parser/x10.g"
                List<TypeParamNode> TypeParamWithVarianceList = (List<TypeParamNode>) getRhsSym(1);
                //#line 2562 "x10/parser/x10.g"
                TypeParamNode TypeParamWithVariance = (TypeParamNode) getRhsSym(3);
                //#line 2564 "lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParamWithVarianceList.add(TypeParamWithVariance);
                setResult(TypeParamWithVarianceList);
                      break;
            }
    
            //
            // Rule 208:  TypeParameterList ::= TypeParameter
            //
            case 208: {
               //#line 2571 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2569 "x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(1);
                //#line 2571 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
                l.add(TypeParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 209:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 209: {
               //#line 2578 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2576 "x10/parser/x10.g"
                List<TypeParamNode> TypeParameterList = (List<TypeParamNode>) getRhsSym(1);
                //#line 2576 "x10/parser/x10.g"
                TypeParamNode TypeParameter = (TypeParamNode) getRhsSym(3);
                //#line 2578 "lpg.generator/templates/java/btParserTemplateF.gi"
                TypeParameterList.add(TypeParameter);
                setResult(TypeParameterList);
                      break;
            }
    
            //
            // Rule 210:  TypeParamWithVariance ::= Identifier
            //
            case 210: {
               //#line 2585 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2583 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2585 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.INVARIANT));
                      break;
            }
    
            //
            // Rule 211:  TypeParamWithVariance ::= + Identifier
            //
            case 211: {
               //#line 2590 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2588 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2590 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.COVARIANT));
                      break;
            }
    
            //
            // Rule 212:  TypeParamWithVariance ::= - Identifier
            //
            case 212: {
               //#line 2595 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2593 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(2);
                //#line 2595 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier, ParameterType.Variance.CONTRAVARIANT));
                      break;
            }
    
            //
            // Rule 213:  TypeParameter ::= Identifier
            //
            case 213: {
               //#line 2601 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2599 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2601 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.TypeParamNode(pos(), Identifier));
                      break;
            }
    
            //
            // Rule 214:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 214: {
               //#line 2626 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2624 "x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 2624 "x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 2626 "lpg.generator/templates/java/btParserTemplateF.gi"
                Expr call = nf.ConstantDistMaker(pos(), expr1, expr2);
                setResult(call);
                      break;
            }
    
            //
            // Rule 215:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Offersopt => ClosureBody
            //
            case 215: {
               //#line 2632 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2630 "x10/parser/x10.g"
                List<Formal> FormalParameters = (List<Formal>) getRhsSym(1);
                //#line 2630 "x10/parser/x10.g"
                DepParameterExpr WhereClauseopt = (DepParameterExpr) getRhsSym(2);
                //#line 2630 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(3);
                //#line 2630 "x10/parser/x10.g"
                TypeNode Offersopt = (TypeNode) getRhsSym(4);
                //#line 2630 "x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(6);
                //#line 2632 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt, 
          HasResultTypeopt == null ? nf.UnknownTypeNode(pos()) : HasResultTypeopt,  ClosureBody));
                      break;
            }
    
            //
            // Rule 216:  LastExpression ::= Expression
            //
            case 216: {
               //#line 2639 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2637 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 2639 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Return(pos(), Expression, true));
                      break;
            }
    
            //
            // Rule 217:  ClosureBody ::= ConditionalExpression
            //
            case 217: {
               //#line 2645 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2643 "x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(1);
                //#line 2645 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), nf.X10Return(pos(), ConditionalExpression, true)));
                      break;
            }
    
            //
            // Rule 218:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 218: {
               //#line 2650 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2648 "x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 2648 "x10/parser/x10.g"
                List<Stmt> BlockStatementsopt = (List<Stmt>) getRhsSym(3);
                //#line 2648 "x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(4);
                //#line 2650 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                Block b = nf.Block(pos(), l);
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b);
                      break;
            }
    
            //
            // Rule 219:  ClosureBody ::= Annotationsopt Block
            //
            case 219: {
               //#line 2660 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2658 "x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 2658 "x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 2660 "lpg.generator/templates/java/btParserTemplateF.gi"
                Block b = Block;
                b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
                setResult(b.position(pos()));
                      break;
            }
    
            //
            // Rule 220:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 220: {
               //#line 2669 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2667 "x10/parser/x10.g"
                Expr PlaceExpressionSingleList = (Expr) getRhsSym(2);
                //#line 2667 "x10/parser/x10.g"
                Block ClosureBody = (Block) getRhsSym(3);
                //#line 2669 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AtExpr(pos(), PlaceExpressionSingleList, nf.UnknownTypeNode(pos()), ClosureBody));
                      break;
            }
    
            //
            // Rule 221:  FinishExpression ::= finish ( Expression ) Block
            //
            case 221: {
               //#line 2675 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2673 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 2673 "x10/parser/x10.g"
                Block Block = (Block) getRhsSym(5);
                //#line 2675 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.FinishExpr(pos(), Expression, Block));
                      break;
            }
    
            //
            // Rule 222:  WhereClauseopt ::= $Empty
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
            // Rule 226:  ClockedClauseopt ::= $Empty
            //
            case 226: {
               //#line 2723 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2723 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 228:  identifier ::= IDENTIFIER$ident
            //
            case 228: {
               //#line 2734 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2732 "x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2734 "lpg.generator/templates/java/btParserTemplateF.gi"
                ident.setKind(X10Parsersym.TK_IDENTIFIER);
                setResult(id(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 229:  TypeName ::= Identifier
            //
            case 229: {
               //#line 2741 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2739 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2741 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 230:  TypeName ::= TypeName . Identifier
            //
            case 230: {
               //#line 2746 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2744 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 2744 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2746 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  TypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 232:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 232: {
               //#line 2758 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2756 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentList = (List<TypeNode>) getRhsSym(2);
                //#line 2758 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(TypeArgumentList);
                      break;
            }
    
            //
            // Rule 233:  TypeArgumentList ::= Type
            //
            case 233: {
               //#line 2765 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2763 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 2765 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> l = new ArrayList<TypeNode>();
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 234:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 234: {
               //#line 2772 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2770 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentList = (List<TypeNode>) getRhsSym(1);
                //#line 2770 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 2772 "lpg.generator/templates/java/btParserTemplateF.gi"
                TypeArgumentList.add(Type);
                      break;
            }
    
            //
            // Rule 235:  PackageName ::= Identifier
            //
            case 235: {
               //#line 2782 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2780 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2782 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 236:  PackageName ::= PackageName . Identifier
            //
            case 236: {
               //#line 2787 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2785 "x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(1);
                //#line 2785 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2787 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 237:  ExpressionName ::= Identifier
            //
            case 237: {
               //#line 2803 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2801 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2803 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 238:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 238: {
               //#line 2808 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2806 "x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2806 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2808 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 239:  MethodName ::= Identifier
            //
            case 239: {
               //#line 2818 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2816 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2818 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 240:  MethodName ::= AmbiguousName . Identifier
            //
            case 240: {
               //#line 2823 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2821 "x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2821 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2823 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 241:  PackageOrTypeName ::= Identifier
            //
            case 241: {
               //#line 2833 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2831 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2833 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 242:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 242: {
               //#line 2838 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2836 "x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(1);
                //#line 2836 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2838 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  PackageOrTypeName,
                                  Identifier));
                      break;
            }
    
            //
            // Rule 243:  AmbiguousName ::= Identifier
            //
            case 243: {
               //#line 2848 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2846 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 2848 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf, ts, pos(), Identifier));
                      break;
            }
    
            //
            // Rule 244:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 244: {
               //#line 2853 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2851 "x10/parser/x10.g"
                ParsedName AmbiguousName = (ParsedName) getRhsSym(1);
                //#line 2851 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 2853 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new X10ParsedName(nf,
                                  ts,
                                  pos(getLeftSpan(), getRightSpan()),
                                  AmbiguousName,
                                  Identifier));
                     break;
            }
    
            //
            // Rule 245:  CompilationUnit ::= PackageDeclarationopt TypeDeclarationsopt
            //
            case 245: {
               //#line 2865 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2863 "x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2863 "x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) getRhsSym(2);
                //#line 2865 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 246:  CompilationUnit ::= PackageDeclarationopt ImportDeclarations TypeDeclarationsopt
            //
            case 246: {
               //#line 2883 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2881 "x10/parser/x10.g"
                PackageNode PackageDeclarationopt = (PackageNode) getRhsSym(1);
                //#line 2881 "x10/parser/x10.g"
                List<Import> ImportDeclarations = (List<Import>) getRhsSym(2);
                //#line 2881 "x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) getRhsSym(3);
                //#line 2883 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                                        PackageDeclarationopt,
                                        ImportDeclarations,
                                        TypeDeclarationsopt));
                      break;
            }
    
            //
            // Rule 247:  CompilationUnit ::= ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 247: {
               //#line 2891 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2889 "x10/parser/x10.g"
                List<Import> ImportDeclarations = (List<Import>) getRhsSym(1);
                //#line 2889 "x10/parser/x10.g"
                PackageNode misplacedPackageDeclaration = (PackageNode) getRhsSym(2);
                //#line 2889 "x10/parser/x10.g"
                List<Import> misplacedImportDeclarations = (List<Import>) getRhsSym(3);
                //#line 2889 "x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) getRhsSym(4);
                //#line 2891 "lpg.generator/templates/java/btParserTemplateF.gi"
                syntaxError("Misplaced package declaration", misplacedPackageDeclaration.position());
                ImportDeclarations.addAll(misplacedImportDeclarations); // merge the two import lists
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                                        misplacedPackageDeclaration,
                                        ImportDeclarations,
                                        TypeDeclarationsopt));
                      break;
            }
    
            //
            // Rule 248:  CompilationUnit ::= PackageDeclaration ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 248: {
               //#line 2901 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2899 "x10/parser/x10.g"
                PackageNode PackageDeclaration = (PackageNode) getRhsSym(1);
                //#line 2899 "x10/parser/x10.g"
                List<Import> ImportDeclarations = (List<Import>) getRhsSym(2);
                //#line 2899 "x10/parser/x10.g"
                PackageNode misplacedPackageDeclaration = (PackageNode) getRhsSym(3);
                //#line 2899 "x10/parser/x10.g"
                List<Import> misplacedImportDeclarations = (List<Import>) getRhsSym(4);
                //#line 2899 "x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) getRhsSym(5);
                //#line 2901 "lpg.generator/templates/java/btParserTemplateF.gi"
                syntaxError("Misplaced package declaration, ignoring", misplacedPackageDeclaration.position());
                ImportDeclarations.addAll(misplacedImportDeclarations); // merge the two import lists
                setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                                        PackageDeclaration,
                                        ImportDeclarations,
                                        TypeDeclarationsopt));
                      break;
            }
    
            //
            // Rule 249:  ImportDeclarations ::= ImportDeclaration
            //
            case 249: {
               //#line 2912 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2910 "x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(1);
                //#line 2912 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Import> l = new TypedList<Import>(new LinkedList<Import>(), Import.class, false);
                l.add(ImportDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 250:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 250: {
               //#line 2919 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2917 "x10/parser/x10.g"
                List<Import> ImportDeclarations = (List<Import>) getRhsSym(1);
                //#line 2917 "x10/parser/x10.g"
                Import ImportDeclaration = (Import) getRhsSym(2);
                //#line 2919 "lpg.generator/templates/java/btParserTemplateF.gi"
                if (ImportDeclaration != null)
                    ImportDeclarations.add(ImportDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 251:  TypeDeclarations ::= TypeDeclaration
            //
            case 251: {
               //#line 2927 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2925 "x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(1);
                //#line 2927 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<TopLevelDecl> l = new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false);
                if (TypeDeclaration != null)
                    l.add(TypeDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 252:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 252: {
               //#line 2935 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2933 "x10/parser/x10.g"
                List<TopLevelDecl> TypeDeclarations = (List<TopLevelDecl>) getRhsSym(1);
                //#line 2933 "x10/parser/x10.g"
                TopLevelDecl TypeDeclaration = (TopLevelDecl) getRhsSym(2);
                //#line 2935 "lpg.generator/templates/java/btParserTemplateF.gi"
                if (TypeDeclaration != null)
                    TypeDeclarations.add(TypeDeclaration);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 253:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 253: {
               //#line 2943 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2941 "x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 2941 "x10/parser/x10.g"
                ParsedName PackageName = (ParsedName) getRhsSym(3);
                //#line 2943 "lpg.generator/templates/java/btParserTemplateF.gi"
                PackageNode pn = PackageName.toPackage();
                pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
                setResult(pn.position(pos()));
                      break;
            }
    
            //
            // Rule 256:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 256: {
               //#line 2957 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2955 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(2);
                //#line 2957 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
                      break;
            }
    
            //
            // Rule 257:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 257: {
               //#line 2963 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2961 "x10/parser/x10.g"
                ParsedName PackageOrTypeName = (ParsedName) getRhsSym(2);
                //#line 2963 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
                      break;
            }
    
            //
            // Rule 261:  TypeDeclaration ::= ;
            //
            case 261: {
               //#line 2978 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2978 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 262:  Interfaces ::= implements InterfaceTypeList
            //
            case 262: {
               //#line 3095 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3093 "x10/parser/x10.g"
                List<TypeNode> InterfaceTypeList = (List<TypeNode>) getRhsSym(2);
                //#line 3095 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 263:  InterfaceTypeList ::= Type
            //
            case 263: {
               //#line 3101 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3099 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 3101 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 264:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 264: {
               //#line 3108 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3106 "x10/parser/x10.g"
                List<TypeNode> InterfaceTypeList = (List<TypeNode>) getRhsSym(1);
                //#line 3106 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3108 "lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceTypeList.add(Type);
                setResult(InterfaceTypeList);
                      break;
            }
    
            //
            // Rule 265:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 265: {
               //#line 3118 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3116 "x10/parser/x10.g"
                List<ClassMember> ClassBodyDeclarationsopt = (List<ClassMember>) getRhsSym(2);
                //#line 3118 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassBodyDeclarationsopt));
                      break;
            }
    
            //
            // Rule 267:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 267: {
               //#line 3125 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3123 "x10/parser/x10.g"
                List<ClassMember> ClassBodyDeclarations = (List<ClassMember>) getRhsSym(1);
                //#line 3123 "x10/parser/x10.g"
                List<ClassMember> ClassBodyDeclaration = (List<ClassMember>) getRhsSym(2);
                //#line 3125 "lpg.generator/templates/java/btParserTemplateF.gi"
                ClassBodyDeclarations.addAll(ClassBodyDeclaration);
                // setResult(a);
                      break;
            }
    
            //
            // Rule 269:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 269: {
               //#line 3147 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3145 "x10/parser/x10.g"
                ConstructorDecl ConstructorDeclaration = (ConstructorDecl) getRhsSym(1);
                //#line 3147 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(ConstructorDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 271:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 271: {
               //#line 3156 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3154 "x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3156 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 272:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 272: {
               //#line 3163 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3161 "x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3163 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 273:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 273: {
               //#line 3170 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3168 "x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3170 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 274:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 274: {
               //#line 3177 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3175 "x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3177 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 275:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 275: {
               //#line 3184 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3182 "x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3184 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 276:  ClassMemberDeclaration ::= ;
            //
            case 276: {
               //#line 3191 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3191 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                setResult(l);
                      break;
            }
    
            //
            // Rule 277:  FormalDeclarators ::= FormalDeclarator
            //
            case 277: {
               //#line 3198 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3196 "x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(1);
                //#line 3198 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
                l.add(FormalDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 278:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 278: {
               //#line 3205 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3203 "x10/parser/x10.g"
                List<Object[]> FormalDeclarators = (List<Object[]>) getRhsSym(1);
                //#line 3203 "x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 3205 "lpg.generator/templates/java/btParserTemplateF.gi"
                FormalDeclarators.add(FormalDeclarator);
                      break;
            }
    
            //
            // Rule 279:  FieldDeclarators ::= FieldDeclarator
            //
            case 279: {
               //#line 3212 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3210 "x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(1);
                //#line 3212 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
                l.add(FieldDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 280:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 280: {
               //#line 3219 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3217 "x10/parser/x10.g"
                List<Object[]> FieldDeclarators = (List<Object[]>) getRhsSym(1);
                //#line 3217 "x10/parser/x10.g"
                Object[] FieldDeclarator = (Object[]) getRhsSym(3);
                //#line 3219 "lpg.generator/templates/java/btParserTemplateF.gi"
                FieldDeclarators.add(FieldDeclarator);
                // setResult(FieldDeclarators);
                      break;
            }
    
            //
            // Rule 281:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 281: {
               //#line 3227 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3225 "x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(1);
                //#line 3227 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
                l.add(VariableDeclaratorWithType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 282:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 282: {
               //#line 3234 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3232 "x10/parser/x10.g"
                List<Object[]> VariableDeclaratorsWithType = (List<Object[]>) getRhsSym(1);
                //#line 3232 "x10/parser/x10.g"
                Object[] VariableDeclaratorWithType = (Object[]) getRhsSym(3);
                //#line 3234 "lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
                // setResult(VariableDeclaratorsWithType);
                      break;
            }
    
            //
            // Rule 283:  VariableDeclarators ::= VariableDeclarator
            //
            case 283: {
               //#line 3241 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3239 "x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(1);
                //#line 3241 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
                l.add(VariableDeclarator);
                setResult(l);
                      break;
            }
    
            //
            // Rule 284:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 284: {
               //#line 3248 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3246 "x10/parser/x10.g"
                List<Object[]> VariableDeclarators = (List<Object[]>) getRhsSym(1);
                //#line 3246 "x10/parser/x10.g"
                Object[] VariableDeclarator = (Object[]) getRhsSym(3);
                //#line 3248 "lpg.generator/templates/java/btParserTemplateF.gi"
                VariableDeclarators.add(VariableDeclarator);
                // setResult(VariableDeclarators);
                      break;
            }
    
            //
            // Rule 286:  ResultType ::= : Type
            //
            case 286: {
               //#line 3304 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3302 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3304 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 287:  HasResultType ::= : Type
            //
            case 287: {
               //#line 3309 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3307 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3309 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 288:  HasResultType ::= <: Type
            //
            case 288: {
               //#line 3314 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3312 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3314 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.HasType(Type));
                      break;
            }
    
            //
            // Rule 289:  FormalParameterList ::= FormalParameter
            //
            case 289: {
               //#line 3329 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3327 "x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(1);
                //#line 3329 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
                l.add(FormalParameter);
                setResult(l);
                      break;
            }
    
            //
            // Rule 290:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 290: {
               //#line 3336 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3334 "x10/parser/x10.g"
                List<Formal> FormalParameterList = (List<Formal>) getRhsSym(1);
                //#line 3334 "x10/parser/x10.g"
                X10Formal FormalParameter = (X10Formal) getRhsSym(3);
                //#line 3336 "lpg.generator/templates/java/btParserTemplateF.gi"
                FormalParameterList.add(FormalParameter);
                      break;
            }
    
            //
            // Rule 291:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 291: {
               //#line 3342 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3340 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3340 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 3342 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 292:  LoopIndexDeclarator ::= [ IdentifierList ] HasResultTypeopt
            //
            case 292: {
               //#line 3347 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3345 "x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(2);
                //#line 3345 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 3347 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 293:  LoopIndexDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt
            //
            case 293: {
               //#line 3352 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3350 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3350 "x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(3);
                //#line 3350 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 3352 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, null });
                      break;
            }
    
            //
            // Rule 294:  LoopIndex ::= Modifiersopt LoopIndexDeclarator
            //
            case 294: {
               //#line 3358 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3356 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 3356 "x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(2);
                //#line 3358 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 295:  LoopIndex ::= Modifiersopt VarKeyword LoopIndexDeclarator
            //
            case 295: {
               //#line 3381 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3379 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 3379 "x10/parser/x10.g"
                List<FlagsNode> VarKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 3379 "x10/parser/x10.g"
                Object[] LoopIndexDeclarator = (Object[]) getRhsSym(3);
                //#line 3381 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 296:  FormalParameter ::= Modifiersopt FormalDeclarator
            //
            case 296: {
               //#line 3405 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3403 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 3403 "x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(2);
                //#line 3405 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 297:  FormalParameter ::= Modifiersopt VarKeyword FormalDeclarator
            //
            case 297: {
               //#line 3429 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3427 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 3427 "x10/parser/x10.g"
                List<FlagsNode> VarKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 3427 "x10/parser/x10.g"
                Object[] FormalDeclarator = (Object[]) getRhsSym(3);
                //#line 3429 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 298:  FormalParameter ::= Type
            //
            case 298: {
               //#line 3453 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3451 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(1);
                //#line 3453 "lpg.generator/templates/java/btParserTemplateF.gi"
            Formal f;
            f = nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), Type, nf.Id(pos(), Name.makeFresh("id$")), Collections.<Formal>emptyList(), true);
            setResult(f);
                      break;
            }
    
            //
            // Rule 299:  Offers ::= offers Type
            //
            case 299: {
               //#line 3599 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3597 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3599 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Type);
                      break;
            }
    
            //
            // Rule 300:  ExceptionTypeList ::= ExceptionType
            //
            case 300: {
               //#line 3605 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3603 "x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(1);
                //#line 3605 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
                l.add(ExceptionType);
                setResult(l);
                      break;
            }
    
            //
            // Rule 301:  ExceptionTypeList ::= ExceptionTypeList , ExceptionType
            //
            case 301: {
               //#line 3612 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3610 "x10/parser/x10.g"
                List<TypeNode> ExceptionTypeList = (List<TypeNode>) getRhsSym(1);
                //#line 3610 "x10/parser/x10.g"
                TypeNode ExceptionType = (TypeNode) getRhsSym(3);
                //#line 3612 "lpg.generator/templates/java/btParserTemplateF.gi"
                ExceptionTypeList.add(ExceptionType);
                      break;
            }
    
            //
            // Rule 303:  MethodBody ::= = LastExpression ;
            //
            case 303: {
               //#line 3620 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3618 "x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(2);
                //#line 3620 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), LastExpression));
                      break;
            }
    
            //
            // Rule 304:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 304: {
               //#line 3625 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3623 "x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(2);
                //#line 3623 "x10/parser/x10.g"
                List<Stmt> BlockStatementsopt = (List<Stmt>) getRhsSym(4);
                //#line 3623 "x10/parser/x10.g"
                Stmt LastExpression = (Stmt) getRhsSym(5);
                //#line 3625 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new ArrayList<Stmt>();
                l.addAll(BlockStatementsopt);
                l.add(LastExpression);
                setResult((Block) ((X10Ext) nf.Block(pos(),l).ext()).annotations(Annotationsopt));
                      break;
            }
    
            //
            // Rule 305:  MethodBody ::= = Annotationsopt Block
            //
            case 305: {
               //#line 3633 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3631 "x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(2);
                //#line 3631 "x10/parser/x10.g"
                Block Block = (Block) getRhsSym(3);
                //#line 3633 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 306:  MethodBody ::= Annotationsopt Block
            //
            case 306: {
               //#line 3638 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3636 "x10/parser/x10.g"
                List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) getRhsSym(1);
                //#line 3636 "x10/parser/x10.g"
                Block Block = (Block) getRhsSym(2);
                //#line 3638 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
                      break;
            }
    
            //
            // Rule 307:  MethodBody ::= ;
            //
            case 307:
                setResult(null);
                break;

            //
            // Rule 308:  ConstructorBody ::= = ConstructorBlock
            //
            case 308: {
               //#line 3709 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3707 "x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(2);
                //#line 3709 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 309:  ConstructorBody ::= ConstructorBlock
            //
            case 309: {
               //#line 3714 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3712 "x10/parser/x10.g"
                Block ConstructorBlock = (Block) getRhsSym(1);
                //#line 3714 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ConstructorBlock);
                      break;
            }
    
            //
            // Rule 310:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 310: {
               //#line 3719 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3717 "x10/parser/x10.g"
                ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) getRhsSym(2);
                //#line 3719 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(ExplicitConstructorInvocation);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 311:  ConstructorBody ::= = AssignPropertyCall
            //
            case 311: {
               //#line 3726 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3724 "x10/parser/x10.g"
                Stmt AssignPropertyCall = (Stmt) getRhsSym(2);
                //#line 3726 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(AssignPropertyCall);
                setResult(nf.Block(pos(), l));
                      break;
            }
    
            //
            // Rule 312:  ConstructorBody ::= ;
            //
            case 312:
                setResult(null);
                break;

            //
            // Rule 313:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 313: {
               //#line 3736 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3734 "x10/parser/x10.g"
                Stmt ExplicitConstructorInvocationopt = (Stmt) getRhsSym(2);
                //#line 3734 "x10/parser/x10.g"
                List<Stmt> BlockStatementsopt = (List<Stmt>) getRhsSym(3);
                //#line 3736 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 314:  Arguments ::= ( ArgumentListopt )
            //
            case 314: {
               //#line 3748 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3746 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(2);
                //#line 3748 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ArgumentListopt);
                      break;
            }
    
            //
            // Rule 316:  ExtendsInterfaces ::= extends Type
            //
            case 316: {
               //#line 3805 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3803 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(2);
                //#line 3805 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
                l.add(Type);
                setResult(l);
                      break;
            }
    
            //
            // Rule 317:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 317: {
               //#line 3812 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3810 "x10/parser/x10.g"
                List<TypeNode> ExtendsInterfaces = (List<TypeNode>) getRhsSym(1);
                //#line 3810 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 3812 "lpg.generator/templates/java/btParserTemplateF.gi"
                ExtendsInterfaces.add(Type);
                      break;
            }
    
            //
            // Rule 318:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 318: {
               //#line 3821 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3819 "x10/parser/x10.g"
                List<ClassMember> InterfaceMemberDeclarationsopt = (List<ClassMember>) getRhsSym(2);
                //#line 3821 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
                      break;
            }
    
            //
            // Rule 320:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 320: {
               //#line 3828 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3826 "x10/parser/x10.g"
                List<ClassMember> InterfaceMemberDeclarations = (List<ClassMember>) getRhsSym(1);
                //#line 3826 "x10/parser/x10.g"
                List<ClassMember> InterfaceMemberDeclaration = (List<ClassMember>) getRhsSym(2);
                //#line 3828 "lpg.generator/templates/java/btParserTemplateF.gi"
                InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
                // setResult(l);
                      break;
            }
    
            //
            // Rule 321:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 321: {
               //#line 3835 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3833 "x10/parser/x10.g"
                ClassMember MethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3835 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(MethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 322:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 322: {
               //#line 3842 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3840 "x10/parser/x10.g"
                ClassMember PropertyMethodDeclaration = (ClassMember) getRhsSym(1);
                //#line 3842 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(PropertyMethodDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 323:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 323: {
               //#line 3849 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3847 "x10/parser/x10.g"
                List<ClassMember> FieldDeclaration = (List<ClassMember>) getRhsSym(1);
                //#line 3849 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.addAll(FieldDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 324:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 324: {
               //#line 3856 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3854 "x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3856 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(ClassDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 325:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 325: {
               //#line 3863 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3861 "x10/parser/x10.g"
                ClassDecl InterfaceDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3863 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(InterfaceDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 326:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 326: {
               //#line 3870 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3868 "x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3870 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
                l.add(TypeDefDeclaration);
                setResult(l);
                      break;
            }
    
            //
            // Rule 327:  InterfaceMemberDeclaration ::= ;
            //
            case 327: {
               //#line 3877 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3877 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Collections.<ClassMember>emptyList());
                      break;
            }
    
            //
            // Rule 328:  Annotations ::= Annotation
            //
            case 328: {
               //#line 3883 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3881 "x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(1);
                //#line 3883 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<AnnotationNode> l = new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false);
                l.add(Annotation);
                setResult(l);
                      break;
            }
    
            //
            // Rule 329:  Annotations ::= Annotations Annotation
            //
            case 329: {
               //#line 3890 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3888 "x10/parser/x10.g"
                List<AnnotationNode> Annotations = (List<AnnotationNode>) getRhsSym(1);
                //#line 3888 "x10/parser/x10.g"
                AnnotationNode Annotation = (AnnotationNode) getRhsSym(2);
                //#line 3890 "lpg.generator/templates/java/btParserTemplateF.gi"
                Annotations.add(Annotation);
                      break;
            }
    
            //
            // Rule 330:  Annotation ::= @ NamedType
            //
            case 330: {
               //#line 3896 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3894 "x10/parser/x10.g"
                TypeNode NamedType = (TypeNode) getRhsSym(2);
                //#line 3896 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.AnnotationNode(pos(), NamedType));
                      break;
            }
    
            //
            // Rule 331:  Identifier ::= identifier
            //
            case 331: {
               //#line 3911 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3909 "x10/parser/x10.g"
                polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) getRhsSym(1);
                //#line 3911 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult( nf.Id(identifier.getPosition(), identifier.getIdentifier()));
                      break;
            }
    
            //
            // Rule 332:  Block ::= { BlockStatementsopt }
            //
            case 332: {
               //#line 3947 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3945 "x10/parser/x10.g"
                List<Stmt> BlockStatementsopt = (List<Stmt>) getRhsSym(2);
                //#line 3947 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Block(pos(), BlockStatementsopt));
                      break;
            }
    
            //
            // Rule 333:  BlockStatements ::= BlockStatement
            //
            case 333: {
               //#line 3953 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3951 "x10/parser/x10.g"
                List<Stmt> BlockStatement = (List<Stmt>) getRhsSym(1);
                //#line 3953 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.addAll(BlockStatement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 334:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 334: {
               //#line 3960 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3958 "x10/parser/x10.g"
                List<Stmt> BlockStatements = (List<Stmt>) getRhsSym(1);
                //#line 3958 "x10/parser/x10.g"
                List<Stmt> BlockStatement = (List<Stmt>) getRhsSym(2);
                //#line 3960 "lpg.generator/templates/java/btParserTemplateF.gi"
                BlockStatements.addAll(BlockStatement);
                //setResult(l);
                      break;
            }
    
            //
            // Rule 336:  BlockStatement ::= ClassDeclaration
            //
            case 336: {
               //#line 3968 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3966 "x10/parser/x10.g"
                ClassDecl ClassDeclaration = (ClassDecl) getRhsSym(1);
                //#line 3968 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 337:  BlockStatement ::= TypeDefDeclaration
            //
            case 337: {
               //#line 3975 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3973 "x10/parser/x10.g"
                TypeDecl TypeDefDeclaration = (TypeDecl) getRhsSym(1);
                //#line 3975 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
                setResult(l);
                      break;
            }
    
            //
            // Rule 338:  BlockStatement ::= Statement
            //
            case 338: {
               //#line 3982 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3980 "x10/parser/x10.g"
                Stmt Statement = (Stmt) getRhsSym(1);
                //#line 3982 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
                l.add(Statement);
                setResult(l);
                      break;
            }
    
            //
            // Rule 339:  IdentifierList ::= Identifier
            //
            case 339: {
               //#line 3990 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3988 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 3990 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Id> l = new TypedList<Id>(new LinkedList<Id>(), Id.class, false);
                l.add(Identifier);
                setResult(l);
                      break;
            }
    
            //
            // Rule 340:  IdentifierList ::= IdentifierList , Identifier
            //
            case 340: {
               //#line 3997 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 3995 "x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(1);
                //#line 3995 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 3997 "lpg.generator/templates/java/btParserTemplateF.gi"
                IdentifierList.add(Identifier);
                      break;
            }
    
            //
            // Rule 341:  FormalDeclarator ::= Identifier ResultType
            //
            case 341: {
               //#line 4003 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4001 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4001 "x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(2);
                //#line 4003 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, ResultType, null });
                      break;
            }
    
            //
            // Rule 342:  FormalDeclarator ::= [ IdentifierList ] ResultType
            //
            case 342: {
               //#line 4008 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4006 "x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(2);
                //#line 4006 "x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(4);
                //#line 4008 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 343:  FormalDeclarator ::= Identifier [ IdentifierList ] ResultType
            //
            case 343: {
               //#line 4013 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4011 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4011 "x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(3);
                //#line 4011 "x10/parser/x10.g"
                TypeNode ResultType = (TypeNode) getRhsSym(5);
                //#line 4013 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
                      break;
            }
    
            //
            // Rule 344:  FieldDeclarator ::= Identifier HasResultType
            //
            case 344: {
               //#line 4019 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4017 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4017 "x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 4019 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), HasResultType, null });
                      break;
            }
    
            //
            // Rule 345:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 345: {
               //#line 4024 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4022 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4022 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 4022 "x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 4024 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 346:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 346: {
               //#line 4030 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4028 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4028 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(2);
                //#line 4028 "x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 4030 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 347:  VariableDeclarator ::= [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 347: {
               //#line 4035 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4033 "x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(2);
                //#line 4033 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(4);
                //#line 4033 "x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 4035 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 348:  VariableDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 348: {
               //#line 4040 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4038 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4038 "x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(3);
                //#line 4038 "x10/parser/x10.g"
                TypeNode HasResultTypeopt = (TypeNode) getRhsSym(5);
                //#line 4038 "x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 4040 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer });
                      break;
            }
    
            //
            // Rule 349:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 349: {
               //#line 4046 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4044 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4044 "x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(2);
                //#line 4044 "x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(4);
                //#line 4046 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 350:  VariableDeclaratorWithType ::= [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 350: {
               //#line 4051 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4049 "x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(2);
                //#line 4049 "x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(4);
                //#line 4049 "x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(6);
                //#line 4051 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), null, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 351:  VariableDeclaratorWithType ::= Identifier [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 351: {
               //#line 4056 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4054 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 4054 "x10/parser/x10.g"
                List<Id> IdentifierList = (List<Id>) getRhsSym(3);
                //#line 4054 "x10/parser/x10.g"
                TypeNode HasResultType = (TypeNode) getRhsSym(5);
                //#line 4054 "x10/parser/x10.g"
                Expr VariableInitializer = (Expr) getRhsSym(7);
                //#line 4056 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultType, VariableInitializer });
                      break;
            }
    
            //
            // Rule 353:  LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
            //
            case 353: {
               //#line 4064 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4062 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 4062 "x10/parser/x10.g"
                List<FlagsNode> VarKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 4062 "x10/parser/x10.g"
                List<Object[]> VariableDeclarators = (List<Object[]>) getRhsSym(3);
                //#line 4064 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 354:  LocalVariableDeclaration ::= Modifiersopt VariableDeclaratorsWithType
            //
            case 354: {
               //#line 4094 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4092 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 4092 "x10/parser/x10.g"
                List<Object[]> VariableDeclaratorsWithType = (List<Object[]>) getRhsSym(2);
                //#line 4094 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 355:  LocalVariableDeclaration ::= Modifiersopt VarKeyword FormalDeclarators
            //
            case 355: {
               //#line 4125 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4123 "x10/parser/x10.g"
                List<Modifier> Modifiersopt = (List<Modifier>) getRhsSym(1);
                //#line 4123 "x10/parser/x10.g"
                List<FlagsNode> VarKeyword = (List<FlagsNode>) getRhsSym(2);
                //#line 4123 "x10/parser/x10.g"
                List<Object[]> FormalDeclarators = (List<Object[]>) getRhsSym(3);
                //#line 4125 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 356:  Primary ::= here
            //
            case 356: {
               //#line 4163 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4163 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(((NodeFactory) nf).Here(pos()));
                      break;
            }
    
            //
            // Rule 357:  Primary ::= [ ArgumentListopt ]
            //
            case 357: {
               //#line 4168 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4166 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(2);
                //#line 4168 "lpg.generator/templates/java/btParserTemplateF.gi"
                Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
                setResult(tuple);
                      break;
            }
    
            //
            // Rule 359:  Primary ::= self
            //
            case 359: {
               //#line 4176 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4176 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Self(pos()));
                      break;
            }
    
            //
            // Rule 360:  Primary ::= this
            //
            case 360: {
               //#line 4181 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4181 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos()));
                      break;
            }
    
            //
            // Rule 361:  Primary ::= ClassName . this
            //
            case 361: {
               //#line 4186 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4184 "x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4186 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.This(pos(), ClassName.toType()));
                      break;
            }
    
            //
            // Rule 362:  Primary ::= ( Expression )
            //
            case 362: {
               //#line 4191 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4189 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(2);
                //#line 4191 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.ParExpr(pos(), Expression));
                      break;
            }
    
            //
            // Rule 368:  OperatorFunction ::= TypeName . +
            //
            case 368: {
               //#line 4202 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4200 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4202 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 369:  OperatorFunction ::= TypeName . -
            //
            case 369: {
               //#line 4213 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4211 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4213 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 370:  OperatorFunction ::= TypeName . *
            //
            case 370: {
               //#line 4224 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4222 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4224 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 371:  OperatorFunction ::= TypeName . /
            //
            case 371: {
               //#line 4235 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4233 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4235 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 372:  OperatorFunction ::= TypeName . %
            //
            case 372: {
               //#line 4246 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4244 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4246 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 373:  OperatorFunction ::= TypeName . &
            //
            case 373: {
               //#line 4257 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4255 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4257 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 374:  OperatorFunction ::= TypeName . |
            //
            case 374: {
               //#line 4268 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4266 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4268 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 375:  OperatorFunction ::= TypeName . ^
            //
            case 375: {
               //#line 4279 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4277 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4279 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 376:  OperatorFunction ::= TypeName . <<
            //
            case 376: {
               //#line 4290 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4288 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4290 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 377:  OperatorFunction ::= TypeName . >>
            //
            case 377: {
               //#line 4301 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4299 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4301 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 378:  OperatorFunction ::= TypeName . >>>
            //
            case 378: {
               //#line 4312 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4310 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4312 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 379:  OperatorFunction ::= TypeName . <
            //
            case 379: {
               //#line 4323 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4321 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4323 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 380:  OperatorFunction ::= TypeName . <=
            //
            case 380: {
               //#line 4334 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4332 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4334 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 381:  OperatorFunction ::= TypeName . >=
            //
            case 381: {
               //#line 4345 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4343 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4345 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 382:  OperatorFunction ::= TypeName . >
            //
            case 382: {
               //#line 4356 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4354 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4356 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 383:  OperatorFunction ::= TypeName . ==
            //
            case 383: {
               //#line 4367 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4365 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4367 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 384:  OperatorFunction ::= TypeName . !=
            //
            case 384: {
               //#line 4378 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4376 "x10/parser/x10.g"
                ParsedName TypeName = (ParsedName) getRhsSym(1);
                //#line 4378 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 385:  Literal ::= IntegerLiteral$lit
            //
            case 385: {
               //#line 4391 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4389 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4391 "lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = int_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.INT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 386:  Literal ::= LongLiteral$lit
            //
            case 386: {
               //#line 4397 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4395 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4397 "lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = long_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), IntLit.LONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 387:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 387: {
               //#line 4403 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4401 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4403 "lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = uint_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.UINT, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 388:  Literal ::= UnsignedLongLiteral$lit
            //
            case 388: {
               //#line 4409 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4407 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4409 "lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.LongLiteral a = ulong_lit(getRhsFirstTokenIndex(1));
                setResult(nf.IntLit(pos(), X10IntLit_c.ULONG, a.getValue().longValue()));
                      break;
            }
    
            //
            // Rule 389:  Literal ::= FloatingPointLiteral$lit
            //
            case 389: {
               //#line 4415 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4413 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4415 "lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
                      break;
            }
    
            //
            // Rule 390:  Literal ::= DoubleLiteral$lit
            //
            case 390: {
               //#line 4421 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4419 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4421 "lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
                setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
                      break;
            }
    
            //
            // Rule 391:  Literal ::= BooleanLiteral
            //
            case 391: {
               //#line 4427 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4425 "x10/parser/x10.g"
                polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) getRhsSym(1);
                //#line 4427 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
                      break;
            }
    
            //
            // Rule 392:  Literal ::= CharacterLiteral$lit
            //
            case 392: {
               //#line 4432 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4430 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 4432 "lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1));
                setResult(nf.CharLit(pos(), a.getValue().charValue()));
                      break;
            }
    
            //
            // Rule 393:  Literal ::= StringLiteral$str
            //
            case 393: {
               //#line 4438 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4436 "x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 4438 "lpg.generator/templates/java/btParserTemplateF.gi"
                polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1));
                setResult(nf.StringLit(pos(), a.getValue()));
                      break;
            }
    
            //
            // Rule 394:  Literal ::= null
            //
            case 394: {
               //#line 4444 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4444 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.NullLit(pos()));
                      break;
            }
    
            //
            // Rule 395:  BooleanLiteral ::= true$trueLiteral
            //
            case 395: {
               //#line 4450 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4448 "x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 4450 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 396:  BooleanLiteral ::= false$falseLiteral
            //
            case 396: {
               //#line 4455 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4453 "x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 4455 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(boolean_lit(getRhsFirstTokenIndex(1)));
                      break;
            }
    
            //
            // Rule 397:  ArgumentList ::= Expression
            //
            case 397: {
               //#line 4464 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4462 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(1);
                //#line 4464 "lpg.generator/templates/java/btParserTemplateF.gi"
                List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
                l.add(Expression);
                setResult(l);
                      break;
            }
    
            //
            // Rule 398:  ArgumentList ::= ArgumentList , Expression
            //
            case 398: {
               //#line 4471 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4469 "x10/parser/x10.g"
                List<Expr> ArgumentList = (List<Expr>) getRhsSym(1);
                //#line 4469 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4471 "lpg.generator/templates/java/btParserTemplateF.gi"
                ArgumentList.add(Expression);
                      break;
            }
    
            //
            // Rule 399:  FieldAccess ::= Primary . Identifier
            //
            case 399: {
               //#line 4477 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4475 "x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4475 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4477 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, Identifier));
                      break;
            }
    
            //
            // Rule 400:  FieldAccess ::= super . Identifier
            //
            case 400: {
               //#line 4482 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4480 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4482 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), Identifier));
                      break;
            }
    
            //
            // Rule 401:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 401: {
               //#line 4487 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4485 "x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4485 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4485 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4487 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier));
                      break;
            }
    
            //
            // Rule 402:  FieldAccess ::= Primary . class$c
            //
            case 402: {
               //#line 4492 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4490 "x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4490 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4492 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 403:  FieldAccess ::= super . class$c
            //
            case 403: {
               //#line 4497 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4495 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 4497 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3)), "class")));
                      break;
            }
    
            //
            // Rule 404:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 404: {
               //#line 4502 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4500 "x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4500 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4500 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 4502 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan(),getRhsFirstTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5)), "class")));
                      break;
            }
    
            //
            // Rule 405:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 405: {
               //#line 4508 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4506 "x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4506 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 4506 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 4508 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), MethodName.prefix == null
                                                             ? null
                                                             : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 406:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 406: {
               //#line 4515 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4513 "x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4513 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4513 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(4);
                //#line 4513 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(6);
                //#line 4515 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 407:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 407: {
               //#line 4520 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4518 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4518 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(4);
                //#line 4518 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(6);
                //#line 4520 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 408:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 408: {
               //#line 4525 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4523 "x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4523 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4523 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4523 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(6);
                //#line 4523 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(8);
                //#line 4525 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
                      break;
            }
    
            //
            // Rule 409:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 409: {
               //#line 4530 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4528 "x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4528 "x10/parser/x10.g"
                List<TypeNode> TypeArgumentsopt = (List<TypeNode>) getRhsSym(2);
                //#line 4528 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(4);
                //#line 4530 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 410:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 410: {
               //#line 4550 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4548 "x10/parser/x10.g"
                ParsedName MethodName = (ParsedName) getRhsSym(1);
                //#line 4548 "x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(4);
                //#line 4550 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 411:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 411: {
               //#line 4563 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4561 "x10/parser/x10.g"
                Expr Primary = (Expr) getRhsSym(1);
                //#line 4561 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4561 "x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(6);
                //#line 4563 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 412:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 412: {
               //#line 4575 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4573 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(3);
                //#line 4573 "x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(6);
                //#line 4575 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 413:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 413: {
               //#line 4588 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4586 "x10/parser/x10.g"
                ParsedName ClassName = (ParsedName) getRhsSym(1);
                //#line 4586 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 4586 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(5);
                //#line 4586 "x10/parser/x10.g"
                List<Formal> FormalParameterListopt = (List<Formal>) getRhsSym(8);
                //#line 4588 "lpg.generator/templates/java/btParserTemplateF.gi"
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
            // Rule 417:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 417: {
               //#line 4606 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4604 "x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4606 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
                      break;
            }
    
            //
            // Rule 418:  PostDecrementExpression ::= PostfixExpression --
            //
            case 418: {
               //#line 4612 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4610 "x10/parser/x10.g"
                Expr PostfixExpression = (Expr) getRhsSym(1);
                //#line 4612 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
                      break;
            }
    
            //
            // Rule 421:  UnannotatedUnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 421: {
               //#line 4620 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4618 "x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4620 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 422:  UnannotatedUnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 422: {
               //#line 4625 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4623 "x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4625 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 425:  UnaryExpression ::= Annotations UnannotatedUnaryExpression
            //
            case 425: {
               //#line 4633 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4631 "x10/parser/x10.g"
                List<AnnotationNode> Annotations = (List<AnnotationNode>) getRhsSym(1);
                //#line 4631 "x10/parser/x10.g"
                Expr UnannotatedUnaryExpression = (Expr) getRhsSym(2);
                //#line 4633 "lpg.generator/templates/java/btParserTemplateF.gi"
                Expr e = UnannotatedUnaryExpression;
                e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
                setResult(e.position(pos()));
                      break;
            }
    
            //
            // Rule 426:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 426: {
               //#line 4641 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4639 "x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4641 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 427:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 427: {
               //#line 4647 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4645 "x10/parser/x10.g"
                Expr UnaryExpressionNotPlusMinus = (Expr) getRhsSym(2);
                //#line 4647 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
                      break;
            }
    
            //
            // Rule 429:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 429: {
               //#line 4654 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4652 "x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4654 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 430:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 430: {
               //#line 4659 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4657 "x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(2);
                //#line 4659 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
                      break;
            }
    
            //
            // Rule 432:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 432: {
               //#line 4666 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4664 "x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4664 "x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4666 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, UnaryExpression));
                      break;
            }
    
            //
            // Rule 433:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 433: {
               //#line 4671 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4669 "x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4669 "x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4671 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, UnaryExpression));
                      break;
            }
    
            //
            // Rule 434:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 434: {
               //#line 4676 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4674 "x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(1);
                //#line 4674 "x10/parser/x10.g"
                Expr UnaryExpression = (Expr) getRhsSym(3);
                //#line 4676 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, UnaryExpression));
                      break;
            }
    
            //
            // Rule 436:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 436: {
               //#line 4683 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4681 "x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4681 "x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4683 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 437:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 437: {
               //#line 4688 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4686 "x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(1);
                //#line 4686 "x10/parser/x10.g"
                Expr MultiplicativeExpression = (Expr) getRhsSym(3);
                //#line 4688 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
                      break;
            }
    
            //
            // Rule 439:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 439: {
               //#line 4695 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4693 "x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4693 "x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4695 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 440:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 440: {
               //#line 4700 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4698 "x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4698 "x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4700 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 441:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 441: {
               //#line 4705 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4703 "x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(1);
                //#line 4703 "x10/parser/x10.g"
                Expr AdditiveExpression = (Expr) getRhsSym(3);
                //#line 4705 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
                      break;
            }
    
            //
            // Rule 443:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 443: {
               //#line 4712 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4710 "x10/parser/x10.g"
                Expr expr1 = (Expr) getRhsSym(1);
                //#line 4710 "x10/parser/x10.g"
                Expr expr2 = (Expr) getRhsSym(3);
                //#line 4712 "lpg.generator/templates/java/btParserTemplateF.gi"
                Expr regionCall = nf.RegionMaker(pos(), expr1, expr2);
                setResult(regionCall);
                      break;
            }
    
            //
            // Rule 446:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 446: {
               //#line 4721 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4719 "x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4719 "x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4721 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, RangeExpression));
                      break;
            }
    
            //
            // Rule 447:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 447: {
               //#line 4726 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4724 "x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4724 "x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4726 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, RangeExpression));
                      break;
            }
    
            //
            // Rule 448:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 448: {
               //#line 4731 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4729 "x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4729 "x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4731 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, RangeExpression));
                      break;
            }
    
            //
            // Rule 449:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 449: {
               //#line 4736 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4734 "x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4734 "x10/parser/x10.g"
                Expr RangeExpression = (Expr) getRhsSym(3);
                //#line 4736 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, RangeExpression));
                      break;
            }
    
            //
            // Rule 450:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 450: {
               //#line 4741 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4739 "x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4739 "x10/parser/x10.g"
                TypeNode Type = (TypeNode) getRhsSym(3);
                //#line 4741 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Instanceof(pos(), RelationalExpression, Type));
                      break;
            }
    
            //
            // Rule 451:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 451: {
               //#line 4746 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4744 "x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(1);
                //#line 4744 "x10/parser/x10.g"
                Expr ShiftExpression = (Expr) getRhsSym(3);
                //#line 4746 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Contains(pos(), RelationalExpression, ShiftExpression));
                      break;
            }
    
            //
            // Rule 453:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 453: {
               //#line 4753 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4751 "x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4751 "x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4753 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
                      break;
            }
    
            //
            // Rule 454:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 454: {
               //#line 4758 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4756 "x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(1);
                //#line 4756 "x10/parser/x10.g"
                Expr RelationalExpression = (Expr) getRhsSym(3);
                //#line 4758 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
                      break;
            }
    
            //
            // Rule 455:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 455: {
               //#line 4763 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4761 "x10/parser/x10.g"
                TypeNode t1 = (TypeNode) getRhsSym(1);
                //#line 4761 "x10/parser/x10.g"
                TypeNode t2 = (TypeNode) getRhsSym(3);
                //#line 4763 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SubtypeTest(pos(), t1, t2, true));
                      break;
            }
    
            //
            // Rule 457:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 457: {
               //#line 4770 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4768 "x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(1);
                //#line 4768 "x10/parser/x10.g"
                Expr EqualityExpression = (Expr) getRhsSym(3);
                //#line 4770 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
                      break;
            }
    
            //
            // Rule 459:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 459: {
               //#line 4777 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4775 "x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4775 "x10/parser/x10.g"
                Expr AndExpression = (Expr) getRhsSym(3);
                //#line 4777 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
                      break;
            }
    
            //
            // Rule 461:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 461: {
               //#line 4784 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4782 "x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(1);
                //#line 4782 "x10/parser/x10.g"
                Expr ExclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4784 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 463:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 463: {
               //#line 4791 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4789 "x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(1);
                //#line 4789 "x10/parser/x10.g"
                Expr InclusiveOrExpression = (Expr) getRhsSym(3);
                //#line 4791 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
                      break;
            }
    
            //
            // Rule 465:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 465: {
               //#line 4798 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4796 "x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4796 "x10/parser/x10.g"
                Expr ConditionalAndExpression = (Expr) getRhsSym(3);
                //#line 4798 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
                      break;
            }
    
            //
            // Rule 470:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 470: {
               //#line 4809 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4807 "x10/parser/x10.g"
                Expr ConditionalOrExpression = (Expr) getRhsSym(1);
                //#line 4807 "x10/parser/x10.g"
                Expr Expression = (Expr) getRhsSym(3);
                //#line 4807 "x10/parser/x10.g"
                Expr ConditionalExpression = (Expr) getRhsSym(5);
                //#line 4809 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
                      break;
            }
    
            //
            // Rule 473:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 473: {
               //#line 4818 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4816 "x10/parser/x10.g"
                Expr LeftHandSide = (Expr) getRhsSym(1);
                //#line 4816 "x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(2);
                //#line 4816 "x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(3);
                //#line 4818 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 474:  Assignment ::= ExpressionName$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 474: {
               //#line 4823 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4821 "x10/parser/x10.g"
                ParsedName e1 = (ParsedName) getRhsSym(1);
                //#line 4821 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(3);
                //#line 4821 "x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4821 "x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4823 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentListopt, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 475:  Assignment ::= Primary$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 475: {
               //#line 4828 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4826 "x10/parser/x10.g"
                Expr e1 = (Expr) getRhsSym(1);
                //#line 4826 "x10/parser/x10.g"
                List<Expr> ArgumentListopt = (List<Expr>) getRhsSym(3);
                //#line 4826 "x10/parser/x10.g"
                Assign.Operator AssignmentOperator = (Assign.Operator) getRhsSym(5);
                //#line 4826 "x10/parser/x10.g"
                Expr AssignmentExpression = (Expr) getRhsSym(6);
                //#line 4828 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(nf.SettableAssign(pos(), e1, ArgumentListopt, AssignmentOperator, AssignmentExpression));
                      break;
            }
    
            //
            // Rule 476:  LeftHandSide ::= ExpressionName
            //
            case 476: {
               //#line 4834 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 4832 "x10/parser/x10.g"
                ParsedName ExpressionName = (ParsedName) getRhsSym(1);
                //#line 4834 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(ExpressionName.toExpr());
                      break;
            }
    
            //
            // Rule 478:  AssignmentOperator ::= =
            //
            case 478: {
               //#line 4841 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4841 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ASSIGN);
                      break;
            }
    
            //
            // Rule 479:  AssignmentOperator ::= *=
            //
            case 479: {
               //#line 4846 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4846 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MUL_ASSIGN);
                      break;
            }
    
            //
            // Rule 480:  AssignmentOperator ::= /=
            //
            case 480: {
               //#line 4851 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4851 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.DIV_ASSIGN);
                      break;
            }
    
            //
            // Rule 481:  AssignmentOperator ::= %=
            //
            case 481: {
               //#line 4856 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4856 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.MOD_ASSIGN);
                      break;
            }
    
            //
            // Rule 482:  AssignmentOperator ::= +=
            //
            case 482: {
               //#line 4861 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4861 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.ADD_ASSIGN);
                      break;
            }
    
            //
            // Rule 483:  AssignmentOperator ::= -=
            //
            case 483: {
               //#line 4866 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4866 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SUB_ASSIGN);
                      break;
            }
    
            //
            // Rule 484:  AssignmentOperator ::= <<=
            //
            case 484: {
               //#line 4871 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4871 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHL_ASSIGN);
                      break;
            }
    
            //
            // Rule 485:  AssignmentOperator ::= >>=
            //
            case 485: {
               //#line 4876 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4876 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.SHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 486:  AssignmentOperator ::= >>>=
            //
            case 486: {
               //#line 4881 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4881 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.USHR_ASSIGN);
                      break;
            }
    
            //
            // Rule 487:  AssignmentOperator ::= &=
            //
            case 487: {
               //#line 4886 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4886 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_AND_ASSIGN);
                      break;
            }
    
            //
            // Rule 488:  AssignmentOperator ::= ^=
            //
            case 488: {
               //#line 4891 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4891 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_XOR_ASSIGN);
                      break;
            }
    
            //
            // Rule 489:  AssignmentOperator ::= |=
            //
            case 489: {
               //#line 4896 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4896 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Assign.BIT_OR_ASSIGN);
                      break;
            }
    
            //
            // Rule 492:  PrefixOp ::= +
            //
            case 492: {
               //#line 4907 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4907 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.POS);
                      break;
            }
    
            //
            // Rule 493:  PrefixOp ::= -
            //
            case 493: {
               //#line 4912 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4912 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NEG);
                      break;
            }
    
            //
            // Rule 494:  PrefixOp ::= !
            //
            case 494: {
               //#line 4917 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4917 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.NOT);
                      break;
            }
    
            //
            // Rule 495:  PrefixOp ::= ~
            //
            case 495: {
               //#line 4922 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4922 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Unary.BIT_NOT);
                      break;
            }
    
            //
            // Rule 496:  BinOp ::= +
            //
            case 496: {
               //#line 4928 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4928 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.ADD);
                      break;
            }
    
            //
            // Rule 497:  BinOp ::= -
            //
            case 497: {
               //#line 4933 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4933 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SUB);
                      break;
            }
    
            //
            // Rule 498:  BinOp ::= *
            //
            case 498: {
               //#line 4938 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4938 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MUL);
                      break;
            }
    
            //
            // Rule 499:  BinOp ::= /
            //
            case 499: {
               //#line 4943 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4943 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.DIV);
                      break;
            }
    
            //
            // Rule 500:  BinOp ::= %
            //
            case 500: {
               //#line 4948 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4948 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.MOD);
                      break;
            }
    
            //
            // Rule 501:  BinOp ::= &
            //
            case 501: {
               //#line 4953 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4953 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_AND);
                      break;
            }
    
            //
            // Rule 502:  BinOp ::= |
            //
            case 502: {
               //#line 4958 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4958 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_OR);
                      break;
            }
    
            //
            // Rule 503:  BinOp ::= ^
            //
            case 503: {
               //#line 4963 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4963 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.BIT_XOR);
                      break;
            }
    
            //
            // Rule 504:  BinOp ::= &&
            //
            case 504: {
               //#line 4968 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4968 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_AND);
                      break;
            }
    
            //
            // Rule 505:  BinOp ::= ||
            //
            case 505: {
               //#line 4973 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4973 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.COND_OR);
                      break;
            }
    
            //
            // Rule 506:  BinOp ::= <<
            //
            case 506: {
               //#line 4978 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4978 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHL);
                      break;
            }
    
            //
            // Rule 507:  BinOp ::= >>
            //
            case 507: {
               //#line 4983 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4983 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.SHR);
                      break;
            }
    
            //
            // Rule 508:  BinOp ::= >>>
            //
            case 508: {
               //#line 4988 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4988 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.USHR);
                      break;
            }
    
            //
            // Rule 509:  BinOp ::= >=
            //
            case 509: {
               //#line 4993 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4993 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GE);
                      break;
            }
    
            //
            // Rule 510:  BinOp ::= <=
            //
            case 510: {
               //#line 4998 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 4998 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LE);
                      break;
            }
    
            //
            // Rule 511:  BinOp ::= >
            //
            case 511: {
               //#line 5003 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5003 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.GT);
                      break;
            }
    
            //
            // Rule 512:  BinOp ::= <
            //
            case 512: {
               //#line 5008 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5008 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.LT);
                      break;
            }
    
            //
            // Rule 513:  BinOp ::= ==
            //
            case 513: {
               //#line 5016 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5016 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.EQ);
                      break;
            }
    
            //
            // Rule 514:  BinOp ::= !=
            //
            case 514: {
               //#line 5021 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5021 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Binary.NE);
                      break;
            }
    
            //
            // Rule 515:  Catchesopt ::= $Empty
            //
            case 515: {
               //#line 5030 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5030 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false));
                      break;
            }
    
            //
            // Rule 517:  Identifieropt ::= $Empty
            //
            case 517:
                setResult(null);
                break;

            //
            // Rule 518:  Identifieropt ::= Identifier
            //
            case 518: {
               //#line 5039 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 5037 "x10/parser/x10.g"
                Id Identifier = (Id) getRhsSym(1);
                //#line 5039 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(Identifier);
                      break;
            }
    
            //
            // Rule 519:  ForUpdateopt ::= $Empty
            //
            case 519: {
               //#line 5045 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5045 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<ForUpdate>(new LinkedList<ForUpdate>(), ForUpdate.class, false));
                      break;
            }
    
            //
            // Rule 521:  Expressionopt ::= $Empty
            //
            case 521:
                setResult(null);
                break;

            //
            // Rule 523:  ForInitopt ::= $Empty
            //
            case 523: {
               //#line 5056 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5056 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false));
                      break;
            }
    
            //
            // Rule 525:  SwitchLabelsopt ::= $Empty
            //
            case 525: {
               //#line 5063 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5063 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Case>(new LinkedList<Case>(), Case.class, false));
                      break;
            }
    
            //
            // Rule 527:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 527: {
               //#line 5070 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5070 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false));
                      break;
            }
    
            //
            // Rule 529:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 529: {
               //#line 5094 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5094 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 531:  ExtendsInterfacesopt ::= $Empty
            //
            case 531: {
               //#line 5101 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5101 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 533:  ClassBodyopt ::= $Empty
            //
            case 533:
                setResult(null);
                break;

            //
            // Rule 535:  ArgumentListopt ::= $Empty
            //
            case 535: {
               //#line 5132 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5132 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false));
                      break;
            }
    
            //
            // Rule 537:  BlockStatementsopt ::= $Empty
            //
            case 537: {
               //#line 5139 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5139 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false));
                      break;
            }
    
            //
            // Rule 539:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 539:
                setResult(null);
                break;

            //
            // Rule 541:  FormalParameterListopt ::= $Empty
            //
            case 541: {
               //#line 5160 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5160 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 543:  Offersopt ::= $Empty
            //
            case 543: {
               //#line 5173 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5173 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(null);
                      break;
            }
    
            //
            // Rule 545:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 545: {
               //#line 5210 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5210 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false));
                      break;
            }
    
            //
            // Rule 547:  Interfacesopt ::= $Empty
            //
            case 547: {
               //#line 5217 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5217 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 549:  Superopt ::= $Empty
            //
            case 549:
                setResult(null);
                break;

            //
            // Rule 551:  TypeParametersopt ::= $Empty
            //
            case 551: {
               //#line 5228 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5228 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 553:  FormalParametersopt ::= $Empty
            //
            case 553: {
               //#line 5235 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5235 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false));
                      break;
            }
    
            //
            // Rule 555:  Annotationsopt ::= $Empty
            //
            case 555: {
               //#line 5242 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5242 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false));
                      break;
            }
    
            //
            // Rule 557:  TypeDeclarationsopt ::= $Empty
            //
            case 557: {
               //#line 5249 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5249 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false));
                      break;
            }
    
            //
            // Rule 559:  ImportDeclarationsopt ::= $Empty
            //
            case 559: {
               //#line 5256 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5256 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<Import>(new LinkedList<Import>(), Import.class, false));
                      break;
            }
    
            //
            // Rule 561:  PackageDeclarationopt ::= $Empty
            //
            case 561:
                setResult(null);
                break;

            //
            // Rule 563:  HasResultTypeopt ::= $Empty
            //
            case 563:
                setResult(null);
                break;

            //
            // Rule 565:  TypeArgumentsopt ::= $Empty
            //
            case 565: {
               //#line 5277 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5277 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
                      break;
            }
    
            //
            // Rule 567:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 567: {
               //#line 5284 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5284 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false));
                      break;
            }
    
            //
            // Rule 569:  Propertiesopt ::= $Empty
            //
            case 569: {
               //#line 5291 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 5291 "lpg.generator/templates/java/btParserTemplateF.gi"
                setResult(new TypedList<PropertyDecl>(new LinkedList<PropertyDecl>(), PropertyDecl.class, false));
                      break;
            }
    
    
            default:
                break;
        }
        return;
    }
}

