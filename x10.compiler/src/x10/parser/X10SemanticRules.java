/* 
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.parser;

import lpg.runtime.*;

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
import x10.ast.AmbMacroTypeNode;
import x10.ast.AnnotationNode;
import x10.ast.AtExpr;
import x10.ast.ClosureCall;
import x10.ast.SettableAssign;
import x10.ast.DepParameterExpr;
import x10.ast.Tuple;
import x10.ast.X10Formal;
import x10.ast.TypeDecl;
import x10.ast.TypeParamNode;
import x10.types.ParameterType;
import polyglot.types.TypeSystem;
import x10.ast.PropertyDecl;
import x10.ast.X10Binary_c;
import x10.ast.X10Unary_c;
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
import polyglot.types.Flags;
import x10.types.checker.Converter;
import x10.errors.Errors;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;

import lpg.runtime.BacktrackingParser;
import lpg.runtime.IToken;
import lpg.runtime.ParseTable;

public class X10SemanticRules implements Parser, ParseErrorCodes
{
    public ParseTable getParseTable() { return p.getParseTable(); }
    public BacktrackingParser getParser() { return p.getParser(); }
    public X10Parser getX10Parser() { return p; }
    public X10Lexer getX10Lexer() { return lexer; }

    //private Object setResult(Object o) { return o; } // todo: refactor it out
    private void setResult(Object object) { getParser().setSym1(object); }
    public Object getRhsSym(int i) { return p.getRhsSym(i); }

    public int getRhsTokenIndex(int i) { return p.getRhsTokenIndex(i); }
    public IToken getRhsIToken(int i) { return p.getRhsIToken(i); }

    public int getRhsFirstTokenIndex(int i) { return p.getRhsFirstTokenIndex(i); }
    public IToken getRhsFirstIToken(int i) { return p.getRhsFirstIToken(i); }

    public int getRhsLastTokenIndex(int i) { return p.getRhsLastTokenIndex(i); }
    public IToken getRhsLastIToken(int i) { return p.getRhsLastIToken(i); }

    public int getLeftSpan() { return p.getLeftSpan(); }
    public IToken getLeftIToken()  { return p.getLeftIToken(); }

    public int getRightSpan() { return p.getRightSpan(); }
    public IToken getRightIToken() { return p.getRightIToken(); }

    public int getRhsErrorTokenIndex(int i) { return p.getRhsErrorTokenIndex(i); }
    public ErrorToken getRhsErrorIToken(int i) { return p.getRhsErrorIToken(i); }
    public polyglot.ast.Node parser() { return p.parser(); }

//    public void reset(ILexStream lexStream) { return p.(i); }
//    public int numTokenKinds() { return X10Parsersym.numTokenKinds; }
//    public String[] orderedTerminalSymbols() { return X10Parsersym.orderedTerminalSymbols; }
//    public String getTokenKindName(int kind) { return X10Parsersym.orderedTerminalSymbols[kind]; }
    //    public int getEOFTokenKind() { return prsTable.getEoftSymbol(); }
    public IPrsStream getIPrsStream() { return p.getIPrsStream(); }


    private final X10Parser p;
    private final X10Lexer lexer;
    private IPrsStream prsStream;

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

    public X10SemanticRules(X10Lexer lexer, TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q)
    {
        this.lexer = lexer;
        ILexStream lexStream = this.lexer.getILexStream();
        p = new X10Parser(lexStream);
        p.r = this;
        initialize((TypeSystem) t,
                (NodeFactory) n,
                source,
                q);
        prsStream = p.getIPrsStream();
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
            if (flag == ATOMIC)       return Flags.ATOMIC;
            //  if (flag == EXTERN)       return X10Flags.EXTERN;
            if (flag == FINAL)        return Flags.FINAL;
            // if (flag == GLOBAL)       return X10Flags.GLOBAL;
            //if (flag == INCOMPLETE)   return X10Flags.INCOMPLETE;
            if (flag == NATIVE)       return Flags.NATIVE;
            //if (flag == NON_BLOCKING) return X10Flags.NON_BLOCKING;
            if (flag == PRIVATE)      return Flags.PRIVATE;
            if (flag == PROPERTY)     return Flags.PROPERTY;
            if (flag == PROTECTED)    return Flags.PROTECTED;
            if (flag == PUBLIC)       return Flags.PUBLIC;
            //if (flag == SAFE)         return X10Flags.SAFE;
            //if (flag == SEQUENTIAL)   return X10Flags.SEQUENTIAL;
            if (flag == CLOCKED)       return Flags.CLOCKED;
            if (flag == TRANSIENT)    return Flags.TRANSIENT;
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
                ? Collections.<Node>singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, Flags.NONE))
                : checkModifiers("class", modifiers, FlagModifier.classModifiers));
    }

    private List<Node> checkTypeDefModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0
                ? Collections.<Node>singletonList(nf.FlagsNode(JPGPosition.COMPILER_GENERATED, Flags.NONE))
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
        Flags xf = Flags.NONE;
        for (Node n : l) {
            if (n instanceof FlagsNode) {
                FlagsNode fn = (FlagsNode) n;
                pos = pos == null ? fn.position() : new JPGPosition(pos, fn.position());
                Flags f = fn.flags();
                xf = xf.set(f);
            }
        }
        return nf.FlagsNode(pos == null ? JPGPosition.COMPILER_GENERATED : pos, xf);
    }

    /* Roll our own integer parser.  We can't use Long.parseLong because
     * it doesn't handle numbers greater than 0x7fffffffffffffff correctly.
     */
    private long parseLong(String s, int radix, Position pos)
    {
        long x = 0L;

        s = s.toLowerCase();

        boolean reportedError = false;
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i);

            if (c < '0' || c > '9') {
                c = c - 'a' + 10;
            }
            else {
                c = c - '0';
            }

            if (c >= radix) {
                if (!reportedError) {
                    syntaxError("Invalid digit: '"+s.charAt(i)+"'",pos);
                    reportedError = true;
                }
            }

            x *= radix;
            x += c;
        }

        return x;
    }

    private long parseLong(String s, Position pos)
    {
        int radix;
        int start_index;
        int end_index;

        end_index = s.length();

        boolean isUnsigned = false;
        boolean isLong = true;
        long min = Long.MIN_VALUE;
        while (end_index > 0) {
            char lastCh = s.charAt(end_index - 1);
            if (lastCh == 'u' || lastCh == 'U') isUnsigned = true;
            // todo: long need special treatment cause we have overflows
            // for signed values that start with 0, we need to make them negative if they are above max value
            if (lastCh == 'n' || lastCh == 'N') { isLong = false; min = Integer.MIN_VALUE; }
            if (lastCh == 'y' || lastCh == 'Y') { isLong = false; min = Byte.MIN_VALUE; }
            if (lastCh == 's' || lastCh == 'S') { isLong = false; min = Short.MIN_VALUE; }
            if (lastCh != 'y' && lastCh != 'Y' && lastCh != 's' && lastCh != 'S' && lastCh != 'l' && lastCh != 'L' && lastCh != 'n' && lastCh != 'N' && lastCh != 'u' && lastCh != 'U') {
                break;
            }
            end_index--;
        }
        long max = -min;

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

        final long res = parseLong(s.substring(start_index, end_index), radix, pos);
        if (!isUnsigned && !isLong && radix!=10 && res>=max) {
            // need to make this value negative
            // e.g., 0xffUY == 255, 0xffY== 255-256 = -1  , 0xfeYU==254, 0xfeY== 254-256 = -2
            return res+min*2;
        }
        return res;
    }

    private void setIntLit(IntLit.Kind k)
    {
        setResult(nf.IntLit(pos(), k, parseLong(prsStream.getName(getRhsFirstTokenIndex(1)), pos())));
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

    private polyglot.lex.CharacterLiteral char_lit(int i, Position pos)
    {
        char x;
        String s = prsStream.getName(i);
        if (s.charAt(1) == '\\') {
            switch(s.charAt(2)) {
                case 'u':
                    x = (char) parseLong(s.substring(3, s.length() - 1), 16, pos);
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
                    x = (char) parseLong(s.substring(2, s.length() - 1), 8, pos);
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

    private polyglot.lex.StringLiteral string_lit(int i, Position pos)
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
                        x[k++] = (char) parseLong(s.substring(j + 2, j + 6), 16, pos);
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
                    case '`':
                        x[k++] = '`';
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
                        char c = (char) parseLong(s.substring(j + 1, n), 8, pos);
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




    // Production: ExpressionStatement ::= StatementExpression ';'
    void rule_ExpressionStatement0(Object _StatementExpression) {
        Expr StatementExpression = (Expr) _StatementExpression;
        setResult(nf.Eval(pos(), StatementExpression));
    }
    // Production: ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt '=>' ClosureBody
    void rule_ClosureExpression0(Object _FormalParameters, Object _WhereClauseopt, Object _HasResultTypeopt, Object _OBSOLETE_Offersopt, Object _ClosureBody) {
        List<Formal> FormalParameters = (List<Formal>) _FormalParameters;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        Block ClosureBody = (Block) _ClosureBody;
        setResult(nf.Closure(pos(), FormalParameters, WhereClauseopt,
        		HasResultTypeopt == null ? nf.UnknownTypeNode(JPGPosition.COMPILER_GENERATED) : HasResultTypeopt,  ClosureBody));
    }
    // Production: PackageOrTypeName ::= PackageOrTypeName '.' ErrorId
    void rule_PackageOrTypeName0(Object _PackageOrTypeName) {
        ParsedName PackageOrTypeName = (ParsedName) _PackageOrTypeName;
        setResult(new ParsedName(nf,
                ts,
                pos(getLeftSpan(), getRightSpan()),
                PackageOrTypeName,
                nf.Id(pos(getRightSpan()), "*")));
    }
    // Production: PackageOrTypeName ::= Identifier
    void rule_PackageOrTypeName1(Object _Identifier) {
        Id Identifier = (Id) _Identifier;
        setResult(new ParsedName(nf, ts, pos(), Identifier));
    }
    // Production: PackageOrTypeName ::= PackageOrTypeName '.' Identifier
    void rule_PackageOrTypeName2(Object _PackageOrTypeName, Object _Identifier) {
        ParsedName PackageOrTypeName = (ParsedName) _PackageOrTypeName;
        Id Identifier = (Id) _Identifier;
        setResult(new ParsedName(nf,
                ts,
                pos(getLeftSpan(), getRightSpan()),
                PackageOrTypeName,
                Identifier));
    }
    // Production: Property ::= Annotationsopt Identifier ResultType
    void rule_Property0(Object _Annotationsopt, Object _Identifier, Object _ResultType) {
        List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) _Annotationsopt;
        Id Identifier = (Id) _Identifier;
        TypeNode ResultType = (TypeNode) _ResultType;
        List<AnnotationNode> annotations = extractAnnotations(Annotationsopt);
        PropertyDecl cd = nf.PropertyDecl(pos(), nf.FlagsNode(pos(), Flags.PUBLIC.Final()), ResultType, Identifier);
        cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
        setResult(cd);
    }
    // Production: CastExpression ::= ExpressionName
    void rule_CastExpression1(Object _ExpressionName) {
        ParsedName ExpressionName = (ParsedName) _ExpressionName;
        setResult(ExpressionName.toExpr());
    }
    // Production: CastExpression ::= CastExpression as Type
    void rule_CastExpression2(Object _CastExpression, Object _Type) {
        Expr CastExpression = (Expr) _CastExpression;
        TypeNode Type = (TypeNode) _Type;
        setResult(nf.X10Cast(pos(), Type, CastExpression));
    }
    // Production: TypeParameter ::= Identifier
    void rule_TypeParameter0(Object _Identifier) {
        Id Identifier = (Id) _Identifier;
        setResult(nf.TypeParamNode(pos(), Identifier));
    }
    // Production: FieldDeclarator ::= Identifier HasResultType
    void rule_FieldDeclarator0(Object _Identifier, Object _HasResultType) {
        Id Identifier = (Id) _Identifier;
        TypeNode HasResultType = (TypeNode) _HasResultType;
        setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), HasResultType, null });
    }
    // Production: FieldDeclarator ::= Identifier HasResultTypeopt '=' VariableInitializer
    void rule_FieldDeclarator1(Object _Identifier, Object _HasResultTypeopt, Object _VariableInitializer) {
        Id Identifier = (Id) _Identifier;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        Expr VariableInitializer = (Expr) _VariableInitializer;
        setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), HasResultTypeopt, VariableInitializer });
    }
    // Production: AtCaptureDeclarator ::= Modifiersopt VarKeywordopt VariableDeclarator
    void rule_AtCaptureDeclarator0(Object _Modifiersopt, Object _VarKeywordopt, Object _VariableDeclarator) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        List<FlagsNode> VarKeywordopt = (List<FlagsNode>) _VarKeywordopt;
        Object[] VariableDeclarator = (Object[]) _VariableDeclarator;
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        FlagsNode fn = VarKeywordopt==null ? extractFlags(modifiers, Flags.FINAL) : extractFlags(modifiers, VarKeywordopt);
        Object[] o = VariableDeclarator;
        Position pos = (Position) o[0];
        Id name = (Id) o[1];
        if (name == null) name = nf.Id(pos, Name.makeFresh());
        List<Id> exploded = (List<Id>) o[2];
        DepParameterExpr guard = (DepParameterExpr) o[3];
        TypeNode type = (TypeNode) o[4];
        if (type == null) type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        Expr init = (Expr) o[5];
        LocalDecl ld = nf.LocalDecl(pos, fn, type, name, init, exploded);
        ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(modifiers));
        if (exploded.size()>0 && init==null) {
            syntaxError("An exploded point must have an initializer.",pos);
        }
        setResult(ld);
    }
    // Production: AtCaptureDeclarator ::= Identifier
    void rule_AtCaptureDeclarator1(Object _Identifier) {
        Id Identifier = (Id) _Identifier;
        setResult(Identifier);
    }
    // Production: AtCaptureDeclarator ::= this
    void rule_AtCaptureDeclarator2() {
        setResult(nf.This(pos()));
    }
    // Production: HomeVariable ::= Identifier
    void rule_HomeVariable0(Object _Identifier) {
        Id Identifier = (Id) _Identifier;
        setResult(nf.Local(pos(), Identifier));
    }
    // Production: HomeVariable ::= this
    void rule_HomeVariable1() {
        setResult(nf.This(pos()));
    }
    // Production: FullyQualifiedName ::= FullyQualifiedName '.' ErrorId
    void rule_FullyQualifiedName0(Object _FullyQualifiedName) {
        ParsedName FullyQualifiedName = (ParsedName) _FullyQualifiedName;
        setResult(new ParsedName(nf,
                ts,
                pos(getLeftSpan(), getRightSpan()),
                FullyQualifiedName,
                nf.Id(pos(getRightSpan()), "*")));
    }
    // Production: FullyQualifiedName ::= Identifier
    void rule_FullyQualifiedName1(Object _Identifier) {
        Id Identifier = (Id) _Identifier;
        setResult(new ParsedName(nf, ts, pos(), Identifier));
    }
    // Production: FullyQualifiedName ::= FullyQualifiedName '.' Identifier
    void rule_FullyQualifiedName2(Object _FullyQualifiedName, Object _Identifier) {
        ParsedName FullyQualifiedName = (ParsedName) _FullyQualifiedName;
        Id Identifier = (Id) _Identifier;
        setResult(new ParsedName(nf,
                ts,
                pos(getLeftSpan(), getRightSpan()),
                FullyQualifiedName,
                Identifier));
    }
    // Production: VariableDeclaratorWithType ::= Identifier HasResultType '=' VariableInitializer
    void rule_VariableDeclaratorWithType0(Object _Identifier, Object _HasResultType, Object _VariableInitializer) {
        Id Identifier = (Id) _Identifier;
        TypeNode HasResultType = (TypeNode) _HasResultType;
        Expr VariableInitializer = (Expr) _VariableInitializer;
        setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, HasResultType, VariableInitializer });
    }
    // Production: VariableDeclaratorWithType ::= '[' IdentifierList ']' HasResultType '=' VariableInitializer
    void rule_VariableDeclaratorWithType1(Object _IdentifierList, Object _HasResultType, Object _VariableInitializer) {
        List<Id> IdentifierList = (List<Id>) _IdentifierList;
        TypeNode HasResultType = (TypeNode) _HasResultType;
        Expr VariableInitializer = (Expr) _VariableInitializer;
        setResult(new Object[] { pos(), null, IdentifierList, null, HasResultType, VariableInitializer });
    }
    // Production: VariableDeclaratorWithType ::= Identifier '[' IdentifierList ']' HasResultType '=' VariableInitializer
    void rule_VariableDeclaratorWithType2(Object _Identifier, Object _IdentifierList, Object _HasResultType, Object _VariableInitializer) {
        Id Identifier = (Id) _Identifier;
        List<Id> IdentifierList = (List<Id>) _IdentifierList;
        TypeNode HasResultType = (TypeNode) _HasResultType;
        Expr VariableInitializer = (Expr) _VariableInitializer;
        setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultType, VariableInitializer });
    }
    // Production: Finally ::= finally Block
    void rule_Finally0(Object _Block) {
        Block Block = (Block) _Block;
        setResult(Block);
    }
    // Production: AnnotationStatement ::= Annotationsopt NonExpressionStatement
    void rule_AnnotationStatement0(Object _Annotationsopt, Object _NonExpressionStatement) {
        List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) _Annotationsopt;
        Stmt NonExpressionStatement = (Stmt) _NonExpressionStatement;
        if (NonExpressionStatement.ext() instanceof X10Ext) {
            NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations(Annotationsopt);
        }
        setResult(NonExpressionStatement.position(pos()));
    }
    // Production: TypeDeclarations ::= TypeDeclaration
    void rule_TypeDeclarations0(Object _TypeDeclaration) {
        TopLevelDecl TypeDeclaration = (TopLevelDecl) _TypeDeclaration;
        List<TopLevelDecl> l = new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false);
        if (TypeDeclaration != null)
            l.add(TypeDeclaration);
        setResult(l);
    }
    // Production: TypeDeclarations ::= TypeDeclarations TypeDeclaration
    void rule_TypeDeclarations1(Object _TypeDeclarations, Object _TypeDeclaration) {
        List<TopLevelDecl> TypeDeclarations = (List<TopLevelDecl>) _TypeDeclarations;
        TopLevelDecl TypeDeclaration = (TopLevelDecl) _TypeDeclaration;
        if (TypeDeclaration != null)
            TypeDeclarations.add(TypeDeclaration);
        //setResult(l);
    }
    // Production: IdentifierList ::= Identifier
    void rule_IdentifierList0(Object _Identifier) {
        Id Identifier = (Id) _Identifier;
        List<Id> l = new TypedList<Id>(new LinkedList<Id>(), Id.class, false);
        l.add(Identifier);
        setResult(l);
    }
    // Production: IdentifierList ::= IdentifierList ',' Identifier
    void rule_IdentifierList1(Object _IdentifierList, Object _Identifier) {
        List<Id> IdentifierList = (List<Id>) _IdentifierList;
        Id Identifier = (Id) _Identifier;
        IdentifierList.add(Identifier);
    }
    // Production: TypeImportOnDemandDeclaration ::= import PackageOrTypeName '.' '*' ';'
    void rule_TypeImportOnDemandDeclaration0(Object _PackageOrTypeName) {
        ParsedName PackageOrTypeName = (ParsedName) _PackageOrTypeName;
        setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.PACKAGE, QName.make(PackageOrTypeName.toString())));
    }
    // Production: BreakStatement ::= break Identifieropt ';'
    void rule_BreakStatement0(Object _Identifieropt) {
        Id Identifieropt = (Id) _Identifieropt;
        setResult(nf.Break(pos(), Identifieropt));
    }
    // Production: ConditionalOrExpression ::= ConditionalOrExpression '||' ConditionalAndExpression
    void rule_ConditionalOrExpression1(Object _ConditionalOrExpression, Object _ConditionalAndExpression) {
        Expr ConditionalOrExpression = (Expr) _ConditionalOrExpression;
        Expr ConditionalAndExpression = (Expr) _ConditionalAndExpression;
        setResult(nf.Binary(pos(), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression));
    }
    // Production: LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
    void rule_LocalVariableDeclaration0(Object _Modifiersopt, Object _VarKeyword, Object _VariableDeclarators) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        List<FlagsNode> VarKeyword = (List<FlagsNode>) _VarKeyword;
        List<Object[]> VariableDeclarators = (List<Object[]>) _VariableDeclarators;
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        FlagsNode fn = VarKeyword == null ? extractFlags(modifiers, Flags.FINAL) : extractFlags(modifiers, VarKeyword);
        List<LocalDecl> l = new TypedList<LocalDecl>(new LinkedList<LocalDecl>(), LocalDecl.class, false);
        for (Object[] o : VariableDeclarators) {
            Position pos = (Position) o[0];
            Position compilerGen = pos.markCompilerGenerated();
            Id name = (Id) o[1];
            if (name == null) name = nf.Id(pos, Name.makeFresh());
            List<Id> exploded = (List<Id>) o[2];
            DepParameterExpr guard = (DepParameterExpr) o[3];
            TypeNode type = (TypeNode) o[4];
            if (type == null) type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
            Expr init = (Expr) o[5];
            LocalDecl ld = nf.LocalDecl(pos, fn, type, name, init, exploded);
            ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(modifiers));
            int index = 0;
            l.add(ld);
            if (exploded.size()>0 && init==null) {
                syntaxError("An exploded point must have an initializer.",pos);
            }
            FlagsNode efn = extractFlags(modifiers, Flags.FINAL); // exploded vars are always final
            for (Id id : exploded) {
                TypeNode tni =
                        init==null ? nf.CanonicalTypeNode(compilerGen, ts.Int()) : // we infer the type of the exploded components, however if there is no init, then we just assume Int to avoid cascading errors.
                        explodedType(id.position()); // UnknownType
                l.add(nf.LocalDecl(id.position(), efn, tni, id, init != null ? nf.ClosureCall(compilerGen, nf.Local(compilerGen, name),  Collections.<Expr>singletonList(nf.IntLit(compilerGen, IntLit.INT, index))) : null));
                index++;
            }
        }
        setResult(l);
    }
    // Production: LocalVariableDeclaration ::= Modifiersopt VariableDeclaratorsWithType
    void rule_LocalVariableDeclaration1(Object _Modifiersopt, Object _VariableDeclaratorsWithType) {
        rule_LocalVariableDeclaration0(_Modifiersopt, null, _VariableDeclaratorsWithType);
    }
    // Production: LocalVariableDeclaration ::= Modifiersopt VarKeyword FormalDeclarators
    void rule_LocalVariableDeclaration2(Object _Modifiersopt, Object _VarKeyword, Object _FormalDeclarators) {
        rule_LocalVariableDeclaration0(_Modifiersopt, _VarKeyword, _FormalDeclarators);
    }
    // Production: InterfaceMemberDeclarationsopt ::= %Empty
    void rule_InterfaceMemberDeclarationsopt0() {
        setResult(new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false));
    }
    // Production: InterfaceTypeList ::= Type
    void rule_InterfaceTypeList0(Object _Type) {
        TypeNode Type = (TypeNode) _Type;
        List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
        l.add(Type);
        setResult(l);
    }
    // Production: InterfaceTypeList ::= InterfaceTypeList ',' Type
    void rule_InterfaceTypeList1(Object _InterfaceTypeList, Object _Type) {
        List<TypeNode> InterfaceTypeList = (List<TypeNode>) _InterfaceTypeList;
        TypeNode Type = (TypeNode) _Type;
        InterfaceTypeList.add(Type);
        setResult(InterfaceTypeList);
    }
    // Production: AtomicStatement ::= atomic Statement
    void rule_AtomicStatement0(Object _Statement) {
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.Atomic(pos(), nf.Here(pos(getLeftSpan())), Statement));
    }
    // Production: PackageName ::= PackageName '.' ErrorId
    void rule_PackageName0(Object _PackageName) {
        ParsedName PackageName = (ParsedName) _PackageName;
        setResult(new ParsedName(nf,
                ts,
                pos(getLeftSpan(), getRightSpan()),
                PackageName,
                nf.Id(pos(getRightSpan()), "*")));
    }
    // Production: PackageName ::= Identifier
    void rule_PackageName1(Object _Identifier) {
        Id Identifier = (Id) _Identifier;
        setResult(new ParsedName(nf, ts, pos(), Identifier));
    }
    // Production: PackageName ::= PackageName '.' Identifier
    void rule_PackageName2(Object _PackageName, Object _Identifier) {
        ParsedName PackageName = (ParsedName) _PackageName;
        Id Identifier = (Id) _Identifier;
        setResult(new ParsedName(nf,
                ts,
                pos(getLeftSpan(), getRightSpan()),
                PackageName,
                Identifier));
    }
    // Production: RelationalExpression ::= RelationalExpression '<' ShiftExpression
    void rule_RelationalExpression3(Object _RelationalExpression, Object _ShiftExpression) {
        Expr RelationalExpression = (Expr) _RelationalExpression;
        Expr ShiftExpression = (Expr) _ShiftExpression;
        setResult(nf.Binary(pos(), RelationalExpression, Binary.LT, ShiftExpression));
    }
    // Production: RelationalExpression ::= RelationalExpression '>' ShiftExpression
    void rule_RelationalExpression4(Object _RelationalExpression, Object _ShiftExpression) {
        Expr RelationalExpression = (Expr) _RelationalExpression;
        Expr ShiftExpression = (Expr) _ShiftExpression;
        setResult(nf.Binary(pos(), RelationalExpression, Binary.GT, ShiftExpression));
    }
    // Production: RelationalExpression ::= RelationalExpression '<=' ShiftExpression
    void rule_RelationalExpression5(Object _RelationalExpression, Object _ShiftExpression) {
        Expr RelationalExpression = (Expr) _RelationalExpression;
        Expr ShiftExpression = (Expr) _ShiftExpression;
        setResult(nf.Binary(pos(), RelationalExpression, Binary.LE, ShiftExpression));
    }
    // Production: RelationalExpression ::= RelationalExpression '>=' ShiftExpression
    void rule_RelationalExpression6(Object _RelationalExpression, Object _ShiftExpression) {
        Expr RelationalExpression = (Expr) _RelationalExpression;
        Expr ShiftExpression = (Expr) _ShiftExpression;
        setResult(nf.Binary(pos(), RelationalExpression, Binary.GE, ShiftExpression));
    }
    // Production: RelationalExpression ::= RelationalExpression instanceof Type
    void rule_RelationalExpression7(Object _RelationalExpression, Object _Type) {
        Expr RelationalExpression = (Expr) _RelationalExpression;
        TypeNode Type = (TypeNode) _Type;
        setResult(nf.Instanceof(pos(), RelationalExpression, Type));
    }
    // Production: BlockInteriorStatement ::= ClassDeclaration
    void rule_BlockInteriorStatement1(Object _ClassDeclaration) {
        ClassDecl ClassDeclaration = (ClassDecl) _ClassDeclaration;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(nf.LocalClassDecl(pos(), ClassDeclaration));
        setResult(l);
    }
    // Production: BlockInteriorStatement ::= StructDeclaration
    void rule_BlockInteriorStatement2(Object _StructDeclaration) {
        ClassDecl StructDeclaration = (ClassDecl) _StructDeclaration;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(nf.LocalClassDecl(pos(), StructDeclaration));
        setResult(l);
    }
    // Production: BlockInteriorStatement ::= TypeDefDeclaration
    void rule_BlockInteriorStatement3(Object _TypeDefDeclaration) {
        TypeDecl TypeDefDeclaration = (TypeDecl) _TypeDefDeclaration;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(nf.LocalTypeDef(pos(), TypeDefDeclaration));
        setResult(l);
    }
    // Production: BlockInteriorStatement ::= Statement
    void rule_BlockInteriorStatement4(Object _Statement) {
        Stmt Statement = (Stmt) _Statement;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(Statement);
        setResult(l);
    }
    // Production: UnaryExpression ::= Annotations UnannotatedUnaryExpression
    void rule_UnaryExpression1(Object _Annotations, Object _UnannotatedUnaryExpression) {
        List<AnnotationNode> Annotations = (List<AnnotationNode>) _Annotations;
        Expr UnannotatedUnaryExpression = (Expr) _UnannotatedUnaryExpression;
        Expr e = UnannotatedUnaryExpression;
        e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
        setResult(e.position(pos()));
    }
    // Production: ExclusiveOrExpression ::= ExclusiveOrExpression '^' AndExpression
    void rule_ExclusiveOrExpression1(Object _ExclusiveOrExpression, Object _AndExpression) {
        Expr ExclusiveOrExpression = (Expr) _ExclusiveOrExpression;
        Expr AndExpression = (Expr) _AndExpression;
        setResult(nf.Binary(pos(), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression));
    }
    // Production: ClockedClauseopt ::= %Empty
    void rule_ClockedClauseopt0() {
        setResult(new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false));
    }
    // Production: AdditiveExpression ::= AdditiveExpression '+' MultiplicativeExpression
    void rule_AdditiveExpression1(Object _AdditiveExpression, Object _MultiplicativeExpression) {
        Expr AdditiveExpression = (Expr) _AdditiveExpression;
        Expr MultiplicativeExpression = (Expr) _MultiplicativeExpression;
        setResult(nf.Binary(pos(), AdditiveExpression, Binary.ADD, MultiplicativeExpression));
    }
    // Production: AdditiveExpression ::= AdditiveExpression '-' MultiplicativeExpression
    void rule_AdditiveExpression2(Object _AdditiveExpression, Object _MultiplicativeExpression) {
        Expr AdditiveExpression = (Expr) _AdditiveExpression;
        Expr MultiplicativeExpression = (Expr) _MultiplicativeExpression;
        setResult(nf.Binary(pos(), AdditiveExpression, Binary.SUB, MultiplicativeExpression));
    }
    // Production: AssignPropertyCall ::= property TypeArgumentsopt '(' ArgumentListopt ')' ';'
    void rule_AssignPropertyCall0(Object _TypeArgumentsopt, Object _ArgumentListopt) {
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        setResult(nf.AssignPropertyCall(pos(), TypeArgumentsopt, ArgumentListopt));
    }
    // Production: ClosureBody ::= ConditionalExpression
    void rule_ClosureBody0(Object _ConditionalExpression) {
        Expr ConditionalExpression = (Expr) _ConditionalExpression;
        setResult(nf.Block(pos(), nf.X10Return(pos(), ConditionalExpression, true)));
    }
    // Production: ClosureBody ::= Annotationsopt '{' BlockStatementsopt LastExpression '}'
    void rule_ClosureBody1(Object _Annotationsopt, Object _BlockStatementsopt, Object _LastExpression) {
        List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) _Annotationsopt;
        List<Stmt> BlockStatementsopt = (List<Stmt>) _BlockStatementsopt;
        Stmt LastExpression = (Stmt) _LastExpression;
        List<Stmt> l = new ArrayList<Stmt>();
        l.addAll(BlockStatementsopt);
        l.add(LastExpression);
        Block b = nf.Block(pos(), l);
        b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
        setResult(b);
    }
    // Production: ClosureBody ::= Annotationsopt Block
    void rule_ClosureBody2(Object _Annotationsopt, Object _Block) {
        List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) _Annotationsopt;
        Block Block = (Block) _Block;
        Block b = Block;
        b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
        setResult(b.position(pos()));
    }
    // Production: MultiplicativeExpression ::= MultiplicativeExpression '*' RangeExpression
    void rule_MultiplicativeExpression1(Object _MultiplicativeExpression, Object _RangeExpression) {
        Expr MultiplicativeExpression = (Expr) _MultiplicativeExpression;
        Expr RangeExpression = (Expr) _RangeExpression;
        setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MUL, RangeExpression));
    }
    // Production: MultiplicativeExpression ::= MultiplicativeExpression '/' RangeExpression
    void rule_MultiplicativeExpression2(Object _MultiplicativeExpression, Object _RangeExpression) {
        Expr MultiplicativeExpression = (Expr) _MultiplicativeExpression;
        Expr RangeExpression = (Expr) _RangeExpression;
        setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.DIV, RangeExpression));
    }
    // Production: MultiplicativeExpression ::= MultiplicativeExpression '%' RangeExpression
    void rule_MultiplicativeExpression3(Object _MultiplicativeExpression, Object _RangeExpression) {
        Expr MultiplicativeExpression = (Expr) _MultiplicativeExpression;
        Expr RangeExpression = (Expr) _RangeExpression;
        setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.MOD, RangeExpression));
    }
    // Production: MultiplicativeExpression ::= MultiplicativeExpression '**' RangeExpression
    void rule_MultiplicativeExpression4(Object _MultiplicativeExpression, Object _RangeExpression) {
        Expr MultiplicativeExpression = (Expr) _MultiplicativeExpression;
        Expr RangeExpression = (Expr) _RangeExpression;
        setResult(nf.Binary(pos(), MultiplicativeExpression, Binary.STARSTAR, RangeExpression));
    }
    // Production: TryStatement ::= try Block Catches
    void rule_TryStatement0(Object _Block, Object _Catches) {
        Block Block = (Block) _Block;
        List<Catch> Catches = (List<Catch>) _Catches;
        setResult(nf.Try(pos(), Block, Catches));
    }
    // Production: TryStatement ::= try Block Catchesopt Finally
    void rule_TryStatement1(Object _Block, Object _Catchesopt, Object _Finally) {
        Block Block = (Block) _Block;
        List<Catch> Catchesopt = (List<Catch>) _Catchesopt;
        Block Finally = (Block) _Finally;
        setResult(nf.Try(pos(), Block, Catchesopt, Finally));
    }
    // Production: FormalParameterList ::= FormalParameter
    void rule_FormalParameterList0(Object _FormalParameter) {
        X10Formal FormalParameter = (X10Formal) _FormalParameter;
        List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
        l.add(FormalParameter);
        setResult(l);
    }
    // Production: FormalParameterList ::= FormalParameterList ',' FormalParameter
    void rule_FormalParameterList1(Object _FormalParameterList, Object _FormalParameter) {
        List<Formal> FormalParameterList = (List<Formal>) _FormalParameterList;
        X10Formal FormalParameter = (X10Formal) _FormalParameter;
        FormalParameterList.add(FormalParameter);
    }
    // Production: SwitchBlock ::= '{' SwitchBlockStatementGroupsopt SwitchLabelsopt '}'
    void rule_SwitchBlock0(Object _SwitchBlockStatementGroupsopt, Object _SwitchLabelsopt) {
        List<Stmt> SwitchBlockStatementGroupsopt = (List<Stmt>) _SwitchBlockStatementGroupsopt;
        List<Case> SwitchLabelsopt = (List<Case>) _SwitchLabelsopt;
        SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
        setResult(SwitchBlockStatementGroupsopt);
    }
    // Production: UnannotatedUnaryExpression ::= '+' UnaryExpressionNotPlusMinus
    void rule_UnannotatedUnaryExpression2(Object _UnaryExpressionNotPlusMinus) {
        Expr UnaryExpressionNotPlusMinus = (Expr) _UnaryExpressionNotPlusMinus;
        setResult(nf.Unary(pos(), Unary.POS, UnaryExpressionNotPlusMinus));
    }
    // Production: UnannotatedUnaryExpression ::= '-' UnaryExpressionNotPlusMinus
    void rule_UnannotatedUnaryExpression3(Object _UnaryExpressionNotPlusMinus) {
        Expr UnaryExpressionNotPlusMinus = (Expr) _UnaryExpressionNotPlusMinus;
        setResult(nf.Unary(pos(), Unary.NEG, UnaryExpressionNotPlusMinus));
    }
    // Production: VariableDeclarator ::= Identifier HasResultTypeopt '=' VariableInitializer
    void rule_VariableDeclarator0(Object _Identifier, Object _HasResultTypeopt, Object _VariableInitializer) {
        Id Identifier = (Id) _Identifier;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        Expr VariableInitializer = (Expr) _VariableInitializer;
        setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, HasResultTypeopt, VariableInitializer });
    }
    // Production: VariableDeclarator ::= '[' IdentifierList ']' HasResultTypeopt '=' VariableInitializer
    void rule_VariableDeclarator1(Object _IdentifierList, Object _HasResultTypeopt, Object _VariableInitializer) {
        List<Id> IdentifierList = (List<Id>) _IdentifierList;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        Expr VariableInitializer = (Expr) _VariableInitializer;
        setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, VariableInitializer });
    }
    // Production: VariableDeclarator ::= Identifier '[' IdentifierList ']' HasResultTypeopt '=' VariableInitializer
    void rule_VariableDeclarator2(Object _Identifier, Object _IdentifierList, Object _HasResultTypeopt, Object _VariableInitializer) {
        Id Identifier = (Id) _Identifier;
        List<Id> IdentifierList = (List<Id>) _IdentifierList;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        Expr VariableInitializer = (Expr) _VariableInitializer;
        setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer });
    }
    // Production: TypeParamWithVarianceList ::= TypeParameter
    void rule_TypeParamWithVarianceList0(Object _TypeParameter) {
        TypeParamNode TypeParameter = (TypeParamNode) _TypeParameter;
        List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        l.add(TypeParameter);
        setResult(l);
    }
    // Production: TypeParamWithVarianceList ::= OBSOLETE_TypeParamWithVariance
    void rule_TypeParamWithVarianceList1(Object _OBSOLETE_TypeParamWithVariance) {
        TypeParamNode OBSOLETE_TypeParamWithVariance = (TypeParamNode) _OBSOLETE_TypeParamWithVariance;
        List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        l.add(OBSOLETE_TypeParamWithVariance);
        setResult(l);
    }
    // Production: TypeParamWithVarianceList ::= TypeParamWithVarianceList ',' TypeParameter
    void rule_TypeParamWithVarianceList2(Object _TypeParamWithVarianceList, Object _TypeParameter) {
        List<TypeParamNode> TypeParamWithVarianceList = (List<TypeParamNode>) _TypeParamWithVarianceList;
        TypeParamNode TypeParameter = (TypeParamNode) _TypeParameter;
        TypeParamWithVarianceList.add(TypeParameter);
        setResult(TypeParamWithVarianceList);
    }
    // Production: TypeParamWithVarianceList ::= TypeParamWithVarianceList ',' OBSOLETE_TypeParamWithVariance
    void rule_TypeParamWithVarianceList3(Object _TypeParamWithVarianceList, Object _OBSOLETE_TypeParamWithVariance) {
        List<TypeParamNode> TypeParamWithVarianceList = (List<TypeParamNode>) _TypeParamWithVarianceList;
        TypeParamNode OBSOLETE_TypeParamWithVariance = (TypeParamNode) _OBSOLETE_TypeParamWithVariance;
        TypeParamWithVarianceList.add(OBSOLETE_TypeParamWithVariance);
        setResult(TypeParamWithVarianceList);
    }
    // Production: UnaryExpressionNotPlusMinus ::= '~' UnaryExpression
    void rule_UnaryExpressionNotPlusMinus1(Object _UnaryExpression) {
        Expr UnaryExpression = (Expr) _UnaryExpression;
        setResult(nf.Unary(pos(), Unary.BIT_NOT, UnaryExpression));
    }
    // Production: UnaryExpressionNotPlusMinus ::= '!' UnaryExpression
    void rule_UnaryExpressionNotPlusMinus2(Object _UnaryExpression) {
        Expr UnaryExpression = (Expr) _UnaryExpression;
        setResult(nf.Unary(pos(), Unary.NOT, UnaryExpression));
    }
    // Production: UnaryExpressionNotPlusMinus ::= '^' UnaryExpression
    void rule_UnaryExpressionNotPlusMinus3(Object _UnaryExpression) {
        Expr UnaryExpression = (Expr) _UnaryExpression;
        setResult(nf.Unary(pos(), Unary.CARET, UnaryExpression));
    }
    // Production: UnaryExpressionNotPlusMinus ::= '|' UnaryExpression
    void rule_UnaryExpressionNotPlusMinus4(Object _UnaryExpression) {
        Expr UnaryExpression = (Expr) _UnaryExpression;
        setResult(nf.Unary(pos(), Unary.BAR, UnaryExpression));
    }
    // Production: UnaryExpressionNotPlusMinus ::= '&' UnaryExpression
    void rule_UnaryExpressionNotPlusMinus5(Object _UnaryExpression) {
        Expr UnaryExpression = (Expr) _UnaryExpression;
        setResult(nf.Unary(pos(), Unary.AMPERSAND, UnaryExpression));
    }
    // Production: UnaryExpressionNotPlusMinus ::= '*' UnaryExpression
    void rule_UnaryExpressionNotPlusMinus6(Object _UnaryExpression) {
        Expr UnaryExpression = (Expr) _UnaryExpression;
        setResult(nf.Unary(pos(), Unary.STAR, UnaryExpression));
    }
    // Production: UnaryExpressionNotPlusMinus ::= '/' UnaryExpression
    void rule_UnaryExpressionNotPlusMinus7(Object _UnaryExpression) {
        Expr UnaryExpression = (Expr) _UnaryExpression;
        setResult(nf.Unary(pos(), Unary.SLASH, UnaryExpression));
    }
    // Production: UnaryExpressionNotPlusMinus ::= '%' UnaryExpression
    void rule_UnaryExpressionNotPlusMinus8(Object _UnaryExpression) {
        Expr UnaryExpression = (Expr) _UnaryExpression;
        setResult(nf.Unary(pos(), Unary.PERCENT, UnaryExpression));
    }
    // Production: Interfacesopt ::= %Empty
    void rule_Interfacesopt0() {
        setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
    }
    // Production: ConditionalExpression ::= ConditionalOrExpression '?' Expression ':' ConditionalExpression
    void rule_ConditionalExpression4(Object _ConditionalOrExpression, Object _Expression, Object _ConditionalExpression) {
        Expr ConditionalOrExpression = (Expr) _ConditionalOrExpression;
        Expr Expression = (Expr) _Expression;
        Expr ConditionalExpression = (Expr) _ConditionalExpression;
        setResult(nf.Conditional(pos(), ConditionalOrExpression, Expression, ConditionalExpression));
    }
    // Production: SwitchLabel ::= case ConstantExpression ':'
    void rule_SwitchLabel0(Object _ConstantExpression) {
        Expr ConstantExpression = (Expr) _ConstantExpression;
        setResult(nf.Case(pos(), ConstantExpression));
    }
    // Production: SwitchLabel ::= default ':'
    void rule_SwitchLabel1() {
        setResult(nf.Default(pos()));
    }
    // Production: VariableDeclarators ::= VariableDeclarator
    void rule_VariableDeclarators0(Object _VariableDeclarator) {
        Object[] VariableDeclarator = (Object[]) _VariableDeclarator;
        List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
        l.add(VariableDeclarator);
        setResult(l);
    }
    // Production: VariableDeclarators ::= VariableDeclarators ',' VariableDeclarator
    void rule_VariableDeclarators1(Object _VariableDeclarators, Object _VariableDeclarator) {
        List<Object[]> VariableDeclarators = (List<Object[]>) _VariableDeclarators;
        Object[] VariableDeclarator = (Object[]) _VariableDeclarator;
        VariableDeclarators.add(VariableDeclarator);
        // setResult(VariableDeclarators);
    }
    // Production: AtCaptureDeclarators ::= AtCaptureDeclarator
    void rule_AtCaptureDeclarators0(Object _AtCaptureDeclarator) {
        Node AtCaptureDeclarator = (Node) _AtCaptureDeclarator;
        List<Node> l = new TypedList<Node>(new LinkedList<Node>(), Node.class, false);
        l.add(AtCaptureDeclarator);
        setResult(l);
    }
    // Production: AtCaptureDeclarators ::= AtCaptureDeclarators ',' AtCaptureDeclarator
    void rule_AtCaptureDeclarators1(Object _AtCaptureDeclarators, Object _AtCaptureDeclarator) {
        List<Node> AtCaptureDeclarators = (List<Node>) _AtCaptureDeclarators;
        Node AtCaptureDeclarator = (Node) _AtCaptureDeclarator;
        AtCaptureDeclarators.add(AtCaptureDeclarator);
        // setResult(AtCaptureDeclarators);
    }
    // Production: HomeVariableList ::= HomeVariable
    void rule_HomeVariableList0(Object _HomeVariable) {
        Node HomeVariable = (Node) _HomeVariable;
        List<Node> l = new TypedList<Node>(new LinkedList<Node>(), Node.class, false);
        l.add(HomeVariable);
        setResult(l);
    }
    // Production: HomeVariableList ::= HomeVariableList ',' HomeVariable
    void rule_HomeVariableList1(Object _HomeVariableList, Object _HomeVariable) {
        List<Node> HomeVariableList = (List<Node>) _HomeVariableList;
        Node HomeVariable = (Node) _HomeVariable;
        HomeVariableList.add(HomeVariable);
        // setResult(HomeVariableList);
    }
    // Production: BlockStatementsopt ::= %Empty
    void rule_BlockStatementsopt0() {
        setResult(new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false));
    }
    // Production: BlockStatements ::= BlockInteriorStatement
    void rule_BlockStatements0(Object _BlockInteriorStatement) {
        List<Stmt> BlockInteriorStatement = (List<Stmt>) _BlockInteriorStatement;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.addAll(BlockInteriorStatement);
        setResult(l);
    }
    // Production: BlockStatements ::= BlockStatements BlockInteriorStatement
    void rule_BlockStatements1(Object _BlockStatements, Object _BlockInteriorStatement) {
        List<Stmt> BlockStatements = (List<Stmt>) _BlockStatements;
        List<Stmt> BlockInteriorStatement = (List<Stmt>) _BlockInteriorStatement;
        BlockStatements.addAll(BlockInteriorStatement);
        //setResult(l);
    }
    // Production: TypeParameterList ::= TypeParameter
    void rule_TypeParameterList0(Object _TypeParameter) {
        TypeParamNode TypeParameter = (TypeParamNode) _TypeParameter;
        List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        l.add(TypeParameter);
        setResult(l);
    }
    // Production: TypeParameterList ::= TypeParameterList ',' TypeParameter
    void rule_TypeParameterList1(Object _TypeParameterList, Object _TypeParameter) {
        List<TypeParamNode> TypeParameterList = (List<TypeParamNode>) _TypeParameterList;
        TypeParamNode TypeParameter = (TypeParamNode) _TypeParameter;
        TypeParameterList.add(TypeParameter);
        setResult(TypeParameterList);
    }
    // Production: OBSOLETE_TypeParamWithVariance ::= '+' TypeParameter
    void rule_OBSOLETE_TypeParamWithVariance0(Object _TypeParameter) {
        TypeParamNode TypeParameter = (TypeParamNode) _TypeParameter;
        syntaxError("Covariance is no longer supported.",pos());
        setResult(TypeParameter.variance(ParameterType.Variance.COVARIANT).position(pos()));
    }
    // Production: OBSOLETE_TypeParamWithVariance ::= '-' TypeParameter
    void rule_OBSOLETE_TypeParamWithVariance1(Object _TypeParameter) {
        TypeParamNode TypeParameter = (TypeParamNode) _TypeParameter;
        syntaxError("Contravariance is no longer supported.",pos());
        setResult(TypeParameter.variance(ParameterType.Variance.CONTRAVARIANT).position(pos()));
    }
    // Production: VariableDeclaratorsWithType ::= VariableDeclaratorWithType
    void rule_VariableDeclaratorsWithType0(Object _VariableDeclaratorWithType) {
        Object[] VariableDeclaratorWithType = (Object[]) _VariableDeclaratorWithType;
        List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
        l.add(VariableDeclaratorWithType);
        setResult(l);
    }
    // Production: VariableDeclaratorsWithType ::= VariableDeclaratorsWithType ',' VariableDeclaratorWithType
    void rule_VariableDeclaratorsWithType1(Object _VariableDeclaratorsWithType, Object _VariableDeclaratorWithType) {
        List<Object[]> VariableDeclaratorsWithType = (List<Object[]>) _VariableDeclaratorsWithType;
        Object[] VariableDeclaratorWithType = (Object[]) _VariableDeclaratorWithType;
        VariableDeclaratorsWithType.add(VariableDeclaratorWithType);
        // setResult(VariableDeclaratorsWithType);
    }
    // Production: Block ::= '{' BlockStatementsopt '}'
    void rule_Block0(Object _BlockStatementsopt) {
        List<Stmt> BlockStatementsopt = (List<Stmt>) _BlockStatementsopt;
        setResult(nf.Block(pos(), BlockStatementsopt));
    }
    // Production: FunctionType ::= TypeParametersopt '(' FormalParameterListopt ')' WhereClauseopt OBSOLETE_Offersopt '=>' Type
    void rule_FunctionType0(Object _TypeParametersopt, Object _FormalParameterListopt, Object _WhereClauseopt, Object _OBSOLETE_Offersopt, Object _Type) {
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        List<Formal> FormalParameterListopt = (List<Formal>) _FormalParameterListopt;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        TypeNode Type = (TypeNode) _Type;
        setResult(nf.FunctionTypeNode(pos(), TypeParametersopt, FormalParameterListopt, WhereClauseopt, Type,  OBSOLETE_Offersopt));
    }
    // Production: ConstraintConjunction ::= Expression
    void rule_ConstraintConjunction0(Object _Expression) {
        Expr Expression = (Expr) _Expression;
        List<Expr> l = new ArrayList<Expr>();
        l.add(Expression);
        setResult(l);
    }
    // Production: ConstraintConjunction ::= ConstraintConjunction ',' Expression
    void rule_ConstraintConjunction1(Object _ConstraintConjunction, Object _Expression) {
        List<Expr> ConstraintConjunction = (List<Expr>) _ConstraintConjunction;
        Expr Expression = (Expr) _Expression;
        ConstraintConjunction.add(Expression);
    }
    // Production: TypeParamsWithVariance ::= '[' TypeParamWithVarianceList ']'
    void rule_TypeParamsWithVariance0(Object _TypeParamWithVarianceList) {
        List<TypeParamNode> TypeParamWithVarianceList = (List<TypeParamNode>) _TypeParamWithVarianceList;
        setResult(TypeParamWithVarianceList);
    }
    // Production: HasZeroConstraint ::= Type haszero
    void rule_HasZeroConstraint0(Object _t1) {
        TypeNode t1 = (TypeNode) _t1;
        setResult(nf.HasZeroTest(pos(), t1));
    }
    // Production: IsRefConstraint ::= Type isref
    void rule_IsRefConstraint0(Object _t1) {
        TypeNode t1 = (TypeNode) _t1;
        setResult(nf.IsRefTest(pos(), t1));
    }
    // Production: FUTURE_ExistentialListopt ::= %Empty
    void rule_FUTURE_ExistentialListopt0() {
        setResult(new ArrayList<Formal>());
    }
    // Production: FUTURE_ExistentialListopt ::= FUTURE_ExistentialList ';'
    void rule_FUTURE_ExistentialListopt1(Object _FUTURE_ExistentialList) {
        List<Formal> FUTURE_ExistentialList = (List<Formal>) _FUTURE_ExistentialList;
        setResult(FUTURE_ExistentialList);
    }
    // Production: Annotation ::= '@' NamedTypeNoConstraints
    void rule_Annotation0(Object _NamedTypeNoConstraints) {
        TypeNode NamedTypeNoConstraints = (TypeNode) _NamedTypeNoConstraints;
        setResult(nf.AnnotationNode(pos(), NamedTypeNoConstraints));
    }
    // Production: BinOp ::= '+'
    void rule_BinOp0() {
        setResult(Binary.ADD);
    }
    // Production: BinOp ::= '-'
    void rule_BinOp1() {
        setResult(Binary.SUB);
    }
    // Production: BinOp ::= '*'
    void rule_BinOp2() {
        setResult(Binary.MUL);
    }
    // Production: BinOp ::= '/'
    void rule_BinOp3() {
        setResult(Binary.DIV);
    }
    // Production: BinOp ::= '%'
    void rule_BinOp4() {
        setResult(Binary.MOD);
    }
    // Production: BinOp ::= '&'
    void rule_BinOp5() {
        setResult(Binary.BIT_AND);
    }
    // Production: BinOp ::= '|'
    void rule_BinOp6() {
        setResult(Binary.BIT_OR);
    }
    // Production: BinOp ::= '^'
    void rule_BinOp7() {
        setResult(Binary.BIT_XOR);
    }
    // Production: BinOp ::= '&&'
    void rule_BinOp8() {
        setResult(Binary.COND_AND);
    }
    // Production: BinOp ::= '||'
    void rule_BinOp9() {
        setResult(Binary.COND_OR);
    }
    // Production: BinOp ::= '<<'
    void rule_BinOp10() {
        setResult(Binary.SHL);
    }
    // Production: BinOp ::= '>>'
    void rule_BinOp11() {
        setResult(Binary.SHR);
    }
    // Production: BinOp ::= '>>>'
    void rule_BinOp12() {
        setResult(Binary.USHR);
    }
    // Production: BinOp ::= '>='
    void rule_BinOp13() {
        setResult(Binary.GE);
    }
    // Production: BinOp ::= '<='
    void rule_BinOp14() {
        setResult(Binary.LE);
    }
    // Production: BinOp ::= '>'
    void rule_BinOp15() {
        setResult(Binary.GT);
    }
    // Production: BinOp ::= '<'
    void rule_BinOp16() {
        setResult(Binary.LT);
    }
    // Production: BinOp ::= '=='
    void rule_BinOp17() {
        setResult(Binary.EQ);
    }
    // Production: BinOp ::= '!='
    void rule_BinOp18() {
        setResult(Binary.NE);
    }
    // Production: BinOp ::= '..'
    void rule_BinOp19() {
        setResult(Binary.DOT_DOT);
    }
    // Production: BinOp ::= '->'
    void rule_BinOp20() {
        setResult(Binary.ARROW);
    }
    // Production: BinOp ::= '<-'
    void rule_BinOp21() {
        setResult(Binary.LARROW);
    }
    // Production: BinOp ::= '-<'
    void rule_BinOp22() {
        setResult(Binary.FUNNEL);
    }
    // Production: BinOp ::= '>-'
    void rule_BinOp23() {
        setResult(Binary.LFUNNEL);
    }
    // Production: BinOp ::= '**'
    void rule_BinOp24() {
        setResult(Binary.STARSTAR);
    }
    // Production: BinOp ::= '~'
    void rule_BinOp25() {
        setResult(Binary.TWIDDLE);
    }
    // Production: BinOp ::= '!~'
    void rule_BinOp26() {
        setResult(Binary.NTWIDDLE);
    }
    // Production: BinOp ::= '!'
    void rule_BinOp27() {
        setResult(Binary.BANG);
    }
    // Production: BinOp ::= '<>'
    void rule_BinOp28() {
        setResult(Binary.DIAMOND);
    }
    // Production: BinOp ::= '><'
    void rule_BinOp29() {
        setResult(Binary.BOWTIE);
    }
    // Production: EqualityExpression ::= EqualityExpression '==' RelationalExpression
    void rule_EqualityExpression1(Object _EqualityExpression, Object _RelationalExpression) {
        Expr EqualityExpression = (Expr) _EqualityExpression;
        Expr RelationalExpression = (Expr) _RelationalExpression;
        setResult(nf.Binary(pos(), EqualityExpression, Binary.EQ, RelationalExpression));
    }
    // Production: EqualityExpression ::= EqualityExpression '!=' RelationalExpression
    void rule_EqualityExpression2(Object _EqualityExpression, Object _RelationalExpression) {
        Expr EqualityExpression = (Expr) _EqualityExpression;
        Expr RelationalExpression = (Expr) _RelationalExpression;
        setResult(nf.Binary(pos(), EqualityExpression, Binary.NE, RelationalExpression));
    }
    // Production: EqualityExpression ::= Type '==' Type
    void rule_EqualityExpression3(Object _t1, Object _t2) {
        TypeNode t1 = (TypeNode) _t1;
        TypeNode t2 = (TypeNode) _t2;
        setResult(nf.SubtypeTest(pos(), t1, t2, true));
    }
    // Production: EqualityExpression ::= EqualityExpression '~' RelationalExpression
    void rule_EqualityExpression4(Object _EqualityExpression, Object _RelationalExpression) {
        Expr EqualityExpression = (Expr) _EqualityExpression;
        Expr RelationalExpression = (Expr) _RelationalExpression;
        setResult(nf.Binary(pos(), EqualityExpression, Binary.TWIDDLE, RelationalExpression));
    }
    // Production: EqualityExpression ::= EqualityExpression '!~' RelationalExpression
    void rule_EqualityExpression5(Object _EqualityExpression, Object _RelationalExpression) {
        Expr EqualityExpression = (Expr) _EqualityExpression;
        Expr RelationalExpression = (Expr) _RelationalExpression;
        setResult(nf.Binary(pos(), EqualityExpression, Binary.NTWIDDLE, RelationalExpression));
    }
    // Production: Modifiersopt ::= %Empty
    void rule_Modifiersopt0() {
        setResult(new LinkedList<Modifier>());
    }
    // Production: Modifiersopt ::= Modifiersopt Modifier
    void rule_Modifiersopt1(Object _Modifiersopt, Object _Modifier) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        Modifier Modifier = (Modifier) _Modifier;
        Modifiersopt.add(Modifier);
    }
    // Production: BooleanLiteral ::= true
    void rule_BooleanLiteral0() {
        setResult(boolean_lit(getRhsFirstTokenIndex(1)));
    }
    // Production: BooleanLiteral ::= false
    void rule_BooleanLiteral1() {
        setResult(boolean_lit(getRhsFirstTokenIndex(1)));
    }
    // Production: ArgumentList ::= Expression
    void rule_ArgumentList0(Object _Expression) {
        Expr Expression = (Expr) _Expression;
        List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        l.add(Expression);
        setResult(l);
    }
    // Production: ArgumentList ::= ArgumentList ',' Expression
    void rule_ArgumentList1(Object _ArgumentList, Object _Expression) {
        List<Expr> ArgumentList = (List<Expr>) _ArgumentList;
        Expr Expression = (Expr) _Expression;
        ArgumentList.add(Expression);
    }
    // Production: FormalParametersopt ::= %Empty
    void rule_FormalParametersopt0() {
        setResult(new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false));
    }
    // Production: ExtendsInterfacesopt ::= %Empty
    void rule_ExtendsInterfacesopt0() {
        setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
    }
    // Production: Primary ::= here
    void rule_Primary0() {
        setResult(((NodeFactory) nf).Here(pos()));
    }
    // Production: Primary ::= '[' ArgumentListopt ']'
    void rule_Primary1(Object _ArgumentListopt) {
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        Tuple tuple = nf.Tuple(pos(), ArgumentListopt);
        setResult(tuple);
    }
    // Production: Primary ::= self
    void rule_Primary3() {
        setResult(nf.Self(pos()));
    }
    // Production: Primary ::= this
    void rule_Primary4() {
        setResult(nf.This(pos()));
    }
    // Production: Primary ::= ClassName '.' this
    void rule_Primary5(Object _ClassName) {
        ParsedName ClassName = (ParsedName) _ClassName;
        setResult(nf.This(pos(), ClassName.toType()));
    }
    // Production: Primary ::= '(' Expression ')'
    void rule_Primary6(Object _Expression) {
        Expr Expression = (Expr) _Expression;
        setResult(nf.ParExpr(pos(), Expression));
    }
    // Production: FormalDeclarators ::= FormalDeclarator
    void rule_FormalDeclarators0(Object _FormalDeclarator) {
        Object[] FormalDeclarator = (Object[]) _FormalDeclarator;
        List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
        l.add(FormalDeclarator);
        setResult(l);
    }
    // Production: FormalDeclarators ::= FormalDeclarators ',' FormalDeclarator
    void rule_FormalDeclarators1(Object _FormalDeclarators, Object _FormalDeclarator) {
        List<Object[]> FormalDeclarators = (List<Object[]>) _FormalDeclarators;
        Object[] FormalDeclarator = (Object[]) _FormalDeclarator;
        FormalDeclarators.add(FormalDeclarator);
    }
    // Production: SingleTypeImportDeclaration ::= import TypeName ';'
    void rule_SingleTypeImportDeclaration0(Object _TypeName) {
        ParsedName TypeName = (ParsedName) _TypeName;
        setResult(nf.Import(pos(getLeftSpan(), getRightSpan()), Import.CLASS, QName.make(TypeName.toString())));
    }
    // Production: RangeExpression ::= RangeExpression '..' UnaryExpression
    void rule_RangeExpression1(Object _RangeExpression, Object _UnaryExpression) {
        Expr RangeExpression = (Expr) _RangeExpression;
        Expr UnaryExpression = (Expr) _UnaryExpression;
        Expr regionCall = nf.Binary(pos(), RangeExpression, Binary.DOT_DOT, UnaryExpression);
        setResult(regionCall);
    }
    // Production: ParameterizedNamedType ::= SimpleNamedType Arguments
    void rule_ParameterizedNamedType0(Object _SimpleNamedType, Object _Arguments) {
        AmbTypeNode SimpleNamedType = (AmbTypeNode) _SimpleNamedType;
        List<Expr> Arguments = (List<Expr>) _Arguments;
        TypeNode type = nf.AmbMacroTypeNode(pos(), SimpleNamedType.prefix(), SimpleNamedType.name(),
                new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false),
                Arguments);
        setResult(type);
    }
    // Production: ParameterizedNamedType ::= SimpleNamedType TypeArguments
    void rule_ParameterizedNamedType1(Object _SimpleNamedType, Object _TypeArguments) {
        AmbTypeNode SimpleNamedType = (AmbTypeNode) _SimpleNamedType;
        List<TypeNode> TypeArguments = (List<TypeNode>) _TypeArguments;
        TypeNode type = nf.AmbMacroTypeNode(pos(), SimpleNamedType.prefix(), SimpleNamedType.name(),
                TypeArguments,
                new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false));
        setResult(type);
    }
    // Production: ParameterizedNamedType ::= SimpleNamedType TypeArguments Arguments
    void rule_ParameterizedNamedType2(Object _SimpleNamedType, Object _TypeArguments, Object _Arguments) {
        AmbTypeNode SimpleNamedType = (AmbTypeNode) _SimpleNamedType;
        List<TypeNode> TypeArguments = (List<TypeNode>) _TypeArguments;
        List<Expr> Arguments = (List<Expr>) _Arguments;
        TypeNode type = nf.AmbMacroTypeNode(pos(), SimpleNamedType.prefix(), SimpleNamedType.name(),
                TypeArguments,
                Arguments);
        setResult(type);
    }
    // Production: DepNamedType ::= SimpleNamedType DepParameters
    void rule_DepNamedType0(Object _SimpleNamedType, Object _DepParameters) {
        AmbTypeNode SimpleNamedType = (AmbTypeNode) _SimpleNamedType;
        DepParameterExpr DepParameters = (DepParameterExpr) _DepParameters;
        TypeNode type = nf.AmbDepTypeNode(pos(), SimpleNamedType.prefix(), SimpleNamedType.name(),
                new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false),
                new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                DepParameters);
        setResult(type);
    }
    // Production: DepNamedType ::= ParameterizedNamedType DepParameters
    void rule_DepNamedType1(Object _ParameterizedNamedType, Object _DepParameters) {
        AmbMacroTypeNode ParameterizedNamedType = (AmbMacroTypeNode) _ParameterizedNamedType;
        DepParameterExpr DepParameters = (DepParameterExpr) _DepParameters;
        TypeNode type = nf.AmbDepTypeNode(pos(), ParameterizedNamedType, DepParameters);
        setResult(type);
    }
    // Production: ClassMemberDeclaration ::= ConstructorDeclaration
    void rule_ClassMemberDeclaration1(Object _ConstructorDeclaration) {
        ConstructorDecl ConstructorDeclaration = (ConstructorDecl) _ConstructorDeclaration;
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        l.add(ConstructorDeclaration);
        setResult(l);
    }
    // Production: InterfaceBody ::= '{' InterfaceMemberDeclarationsopt '}'
    void rule_InterfaceBody0(Object _InterfaceMemberDeclarationsopt) {
        List<ClassMember> InterfaceMemberDeclarationsopt = (List<ClassMember>) _InterfaceMemberDeclarationsopt;
        setResult(nf.ClassBody(pos(), InterfaceMemberDeclarationsopt));
    }
    // Production: LabeledStatement ::= Identifier ':' LoopStatement
    void rule_LabeledStatement0(Object _Identifier, Object _LoopStatement) {
        Id Identifier = (Id) _Identifier;
        Stmt LoopStatement = (Stmt) _LoopStatement;
        setResult(nf.Labeled(pos(), Identifier, LoopStatement));
    }
    // Production: TypeArgumentList ::= Type
    void rule_TypeArgumentList0(Object _Type) {
        TypeNode Type = (TypeNode) _Type;
        List<TypeNode> l = new ArrayList<TypeNode>();
        l.add(Type);
        setResult(l);
    }
    // Production: TypeArgumentList ::= TypeArgumentList ',' Type
    void rule_TypeArgumentList1(Object _TypeArgumentList, Object _Type) {
        List<TypeNode> TypeArgumentList = (List<TypeNode>) _TypeArgumentList;
        TypeNode Type = (TypeNode) _Type;
        TypeArgumentList.add(Type);
    }
    // Production: NormalClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
    void rule_NormalClassDeclaration0(Object _Modifiersopt, Object _Identifier, Object _TypeParamsWithVarianceopt, Object _Propertiesopt, Object _WhereClauseopt, Object _Superopt, Object _Interfacesopt, Object _ClassBody) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        Id Identifier = (Id) _Identifier;
        List<TypeParamNode> TypeParamsWithVarianceopt = (List<TypeParamNode>) _TypeParamsWithVarianceopt;
        List<PropertyDecl> Propertiesopt = (List<PropertyDecl>) _Propertiesopt;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode Superopt = (TypeNode) _Superopt;
        List<TypeNode> Interfacesopt = (List<TypeNode>) _Interfacesopt;
        ClassBody ClassBody = (ClassBody) _ClassBody;
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
    }
    // Production: SimpleNamedType ::= TypeName
    void rule_SimpleNamedType0(Object _TypeName) {
        ParsedName TypeName = (ParsedName) _TypeName;
        setResult(TypeName.toType());
    }
    // Production: SimpleNamedType ::= Primary '.' Identifier
    void rule_SimpleNamedType1(Object _Primary, Object _Identifier) {
        Expr Primary = (Expr) _Primary;
        Id Identifier = (Id) _Identifier;
        setResult(nf.AmbTypeNode(pos(), Primary, Identifier));
    }
    // Production: SimpleNamedType ::= ParameterizedNamedType '.' Identifier
    void rule_SimpleNamedType2(Object _ParameterizedNamedType, Object _Identifier) {
        TypeNode ParameterizedNamedType = (TypeNode) _ParameterizedNamedType;
        Id Identifier = (Id) _Identifier;
        setResult(nf.AmbTypeNode(pos(), ParameterizedNamedType, Identifier));
    }
    // Production: SimpleNamedType ::= DepNamedType '.' Identifier
    void rule_SimpleNamedType3(Object _DepNamedType, Object _Identifier) {
        TypeNode DepNamedType = (TypeNode) _DepNamedType;
        Id Identifier = (Id) _Identifier;
        setResult(nf.AmbTypeNode(pos(), DepNamedType, Identifier));
    }
    // Production: Void ::= void
    void rule_Void0() {
        setResult(nf.CanonicalTypeNode(pos(), ts.Void()));
    }
    // Production: PreIncrementExpression ::= '++' UnaryExpressionNotPlusMinus
    void rule_PreIncrementExpression0(Object _UnaryExpressionNotPlusMinus) {
        Expr UnaryExpressionNotPlusMinus = (Expr) _UnaryExpressionNotPlusMinus;
        setResult(nf.Unary(pos(), Unary.PRE_INC, UnaryExpressionNotPlusMinus));
    }

    TypeNode explodedType(Position p) {
        return nf.UnknownTypeNode(p);// exploded formals/locals are either Int or T (when exploding Array[T]).  nf.TypeNodeFromQualifiedName(p,QName.make("x10.lang.Int"));  

    }
    List<Formal> createExplodedFormals(List<Id> exploded) {
        List<Formal> explodedFormals = new ArrayList<Formal>();
        for (Id id : exploded) {
            // exploded formals are always final (VAL)
            explodedFormals.add(nf.Formal(id.position(), nf.FlagsNode(id.position(), Flags.FINAL), explodedType(id.position()), id));
        }
        return explodedFormals;
    }

    // Production: LoopIndex ::= Modifiersopt LoopIndexDeclarator
    void rule_LoopIndex0(Object _Modifiersopt, Object _LoopIndexDeclarator) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        Object[] LoopIndexDeclarator = (Object[]) _LoopIndexDeclarator;
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
        if (type == null) type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        List<Formal> explodedFormals = createExplodedFormals(exploded);
        f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
        f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
        setResult(f);
    }
    // Production: LoopIndex ::= Modifiersopt VarKeyword LoopIndexDeclarator
    void rule_LoopIndex1(Object _Modifiersopt, Object _VarKeyword, Object _LoopIndexDeclarator) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        List<FlagsNode> VarKeyword = (List<FlagsNode>) _VarKeyword;
        Object[] LoopIndexDeclarator = (Object[]) _LoopIndexDeclarator;
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
        if (type == null) type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        List<Formal> explodedFormals = createExplodedFormals(exploded);
        f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
        f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
        setResult(f);
    }
    // Production: Arguments ::= '(' ArgumentList ')'
    void rule_Arguments0(Object _ArgumentList) {
        List<Expr> ArgumentList = (List<Expr>) _ArgumentList;
        setResult(ArgumentList);
    }
    // Production: Literal ::= ByteLiteral
    void rule_LiteralByte() {
        setIntLit(IntLit.BYTE);
    }
    // Production: Literal ::= UByteLiteral
    void rule_LiteralUByte() {
        setIntLit(IntLit.UBYTE);
    }
    // Production: Literal ::= ShortLiteral
    void rule_LiteralShort() {
        setIntLit(IntLit.SHORT);
    }
    // Production: Literal ::= UShortLiteral
    void rule_LiteralUShort() {
        setIntLit(IntLit.USHORT);
    }
    // Production: Literal ::= IntLiteral
    void rule_Literal0() {
        setIntLit(IntLit.INT);
    }
    // Production: Literal ::= LongLiteral
    void rule_Literal1() {
        setIntLit(IntLit.LONG);
    }
    // Production: Literal ::= UnsignedIntLiteral
    void rule_Literal2() {
        setIntLit(IntLit.UINT);
    }
    // Production: Literal ::= UnsignedLongLiteral
    void rule_Literal3() {
        setIntLit(IntLit.ULONG);
    }
    // Production: Literal ::= FloatingPointLiteral
    void rule_Literal4() {
        polyglot.lex.FloatLiteral a = float_lit(getRhsFirstTokenIndex(1));
        setResult(nf.FloatLit(pos(), FloatLit.FLOAT, a.getValue().floatValue()));
    }
    // Production: Literal ::= DoubleLiteral
    void rule_Literal5() {
        polyglot.lex.DoubleLiteral a = double_lit(getRhsFirstTokenIndex(1));
        setResult(nf.FloatLit(pos(), FloatLit.DOUBLE, a.getValue().doubleValue()));
    }
    // Production: Literal ::= BooleanLiteral
    void rule_Literal6(Object _BooleanLiteral) {
        polyglot.lex.BooleanLiteral BooleanLiteral = (polyglot.lex.BooleanLiteral) _BooleanLiteral;
        setResult(nf.BooleanLit(pos(), BooleanLiteral.getValue().booleanValue()));
    }
    // Production: Literal ::= CharacterLiteral
    void rule_Literal7() {
        polyglot.lex.CharacterLiteral a = char_lit(getRhsFirstTokenIndex(1), pos());
        setResult(nf.CharLit(pos(), a.getValue().charValue()));
    }
    // Production: Literal ::= StringLiteral
    void rule_Literal8() {
        polyglot.lex.StringLiteral a = string_lit(getRhsFirstTokenIndex(1), pos());
        setResult(nf.StringLit(pos(), a.getValue()));
    }
    // Production: Literal ::= null
    void rule_Literal9() {
        setResult(nf.NullLit(pos()));
    }
    // Production: ArgumentListopt ::= %Empty
    void rule_ArgumentListopt0() {
        setResult(new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false));
    }
    // Production: TypeDeclaration ::= ';'
    void rule_TypeDeclaration3() {
        setResult(null);
    }
    // Production: TypeArguments ::= '[' TypeArgumentList ']'
    void rule_TypeArguments0(Object _TypeArgumentList) {
        List<TypeNode> TypeArgumentList = (List<TypeNode>) _TypeArgumentList;
        setResult(TypeArgumentList);
    }
    // Production: ClassMemberDeclarationsopt ::= %Empty
    void rule_ClassMemberDeclarationsopt0() {
        setResult(new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false));
    }
    // Production: LeftHandSide ::= ExpressionName
    void rule_LeftHandSide0(Object _ExpressionName) {
        ParsedName ExpressionName = (ParsedName) _ExpressionName;
        setResult(ExpressionName.toExpr());
    }
    // Production: TypeName ::= TypeName '.' ErrorId
    void rule_TypeName0(Object _TypeName) {
        ParsedName TypeName = (ParsedName) _TypeName;
        setResult(new ParsedName(nf,
                ts,
                pos(getLeftSpan(), getRightSpan()),
                TypeName,
                nf.Id(pos(getRightSpan()), "*")));
    }
    // Production: TypeName ::= Identifier
    void rule_TypeName1(Object _Identifier) {
        Id Identifier = (Id) _Identifier;
        setResult(new ParsedName(nf, ts, pos(), Identifier));
    }
    // Production: TypeName ::= TypeName '.' Identifier
    void rule_TypeName2(Object _TypeName, Object _Identifier) {
        ParsedName TypeName = (ParsedName) _TypeName;
        Id Identifier = (Id) _Identifier;
        setResult(new ParsedName(nf,
                ts,
                pos(getLeftSpan(), getRightSpan()),
                TypeName,
                Identifier));
    }
    // Production: OBSOLETE_Offers ::= offers Type
    void rule_OBSOLETE_Offers0(Object _Type) {
        TypeNode Type = (TypeNode) _Type;
        setResult(Type);
    }
    // Production: Super ::= extends ClassType
    void rule_Super0(Object _ClassType) {
        TypeNode ClassType = (TypeNode) _ClassType;
        setResult(ClassType);
    }
    // Production: InterfaceDeclaration ::= Modifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
    void rule_InterfaceDeclaration0(Object _Modifiersopt, Object _Identifier, Object _TypeParamsWithVarianceopt, Object _Propertiesopt, Object _WhereClauseopt, Object _ExtendsInterfacesopt, Object _InterfaceBody) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        Id Identifier = (Id) _Identifier;
        List<TypeParamNode> TypeParamsWithVarianceopt = (List<TypeParamNode>) _TypeParamsWithVarianceopt;
        List<PropertyDecl> Propertiesopt = (List<PropertyDecl>) _Propertiesopt;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        List<TypeNode> ExtendsInterfacesopt = (List<TypeNode>) _ExtendsInterfacesopt;
        ClassBody InterfaceBody = (ClassBody) _InterfaceBody;
        List<Node> modifiers = checkInterfaceModifiers(Modifiersopt);
        checkTypeName(Identifier);
        List<TypeParamNode> TypeParametersopt = TypeParamsWithVarianceopt;
        List<PropertyDecl> props = Propertiesopt;
        // we use the property syntax for annotation-interfaces:
        // public interface Pragma(pragma:Int) extends StatementAnnotation { ... }
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
    }
    // Production: Propertiesopt ::= %Empty
    void rule_Propertiesopt0() {
        setResult(new TypedList<PropertyDecl>(new LinkedList<PropertyDecl>(), PropertyDecl.class, false));
    }
    // Production: AtCaptureDeclaratorsopt ::= %Empty
    void rule_AtCaptureDeclaratorsopt0() {
        setResult(new TypedList<Node>(new LinkedList<Node>(), Node.class, false));
    }
    // Production: SwitchLabelsopt ::= %Empty
    void rule_SwitchLabelsopt0() {
        setResult(new TypedList<Case>(new LinkedList<Case>(), Case.class, false));
    }
    // Production: MethodName ::= FullyQualifiedName '.' ErrorId
    void rule_MethodName0(Object _FullyQualifiedName) {
        ParsedName FullyQualifiedName = (ParsedName) _FullyQualifiedName;
        setResult(new ParsedName(nf,
                ts,
                pos(getLeftSpan(), getRightSpan()),
                FullyQualifiedName,
                nf.Id(pos(getRightSpan()), "*")));
    }
    // Production: MethodName ::= Identifier
    void rule_MethodName1(Object _Identifier) {
        Id Identifier = (Id) _Identifier;
        setResult(new ParsedName(nf, ts, pos(), Identifier));
    }
    // Production: MethodName ::= FullyQualifiedName '.' Identifier
    void rule_MethodName2(Object _FullyQualifiedName, Object _Identifier) {
        ParsedName FullyQualifiedName = (ParsedName) _FullyQualifiedName;
        Id Identifier = (Id) _Identifier;
        setResult(new ParsedName(nf,
                ts,
                pos(getLeftSpan(), getRightSpan()),
                FullyQualifiedName,
                Identifier));
    }
    // Production: FieldAccess ::= ErrorPrimaryPrefix
    void rule_FieldAccess0(Object _ErrorPrimaryPrefix) {
        Object[] ErrorPrimaryPrefix = (Object[]) _ErrorPrimaryPrefix;
        Expr Primary = (Expr) ErrorPrimaryPrefix[0];
        polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ErrorPrimaryPrefix[1];
        setResult(nf.Field(pos(), Primary, nf.Id(identifier.getPosition(), identifier.getIdentifier())));
    }
    // Production: FieldAccess ::= ErrorSuperPrefix
    void rule_FieldAccess1(Object _ErrorSuperPrefix) {
        Object[] ErrorSuperPrefix = (Object[]) _ErrorSuperPrefix;
        JPGPosition super_pos = (JPGPosition) ErrorSuperPrefix[0];
        polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ErrorSuperPrefix[1];
        setResult(nf.Field(pos(getRightSpan()), nf.Super(super_pos),
                nf.Id(identifier.getPosition(), identifier.getIdentifier())));
    }
    // Production: FieldAccess ::= ErrorClassNameSuperPrefix
    void rule_FieldAccess2(Object _ErrorClassNameSuperPrefix) {
        Object[] ErrorClassNameSuperPrefix = (Object[]) _ErrorClassNameSuperPrefix;
        ParsedName ClassName = (ParsedName) ErrorClassNameSuperPrefix[0];
        JPGPosition super_pos = (JPGPosition) ErrorClassNameSuperPrefix[1];
        polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ErrorClassNameSuperPrefix[2];
        setResult(nf.Field(pos(getRightSpan()), nf.Super(super_pos, ClassName.toType()),
                nf.Id(identifier.getPosition(), identifier.getIdentifier())));
    }
    // Production: FieldAccess ::= Primary '.' Identifier
    void rule_FieldAccess3(Object _Primary, Object _Identifier) {
        Expr Primary = (Expr) _Primary;
        Id Identifier = (Id) _Identifier;
        setResult(nf.Field(pos(), Primary, Identifier));
    }
    // Production: FieldAccess ::= super '.' Identifier
    void rule_FieldAccess4(Object _Identifier) {
        Id Identifier = (Id) _Identifier;
        setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), Identifier));
    }
    // Production: FieldAccess ::= ClassName '.' super '.' Identifier
    void rule_FieldAccess5(Object _ClassName, Object _Identifier) {
        ParsedName ClassName = (ParsedName) _ClassName;
        Id Identifier = (Id) _Identifier;
        setResult(nf.Field(pos(), nf.Super(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(3)), ClassName.toType()), Identifier));
    }
    // Production: ForInit ::= LocalVariableDeclaration
    void rule_ForInit1(Object _LocalVariableDeclaration) {
        List<LocalDecl> LocalVariableDeclaration = (List<LocalDecl>) _LocalVariableDeclaration;
        List<ForInit> l = new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false);
        l.addAll(LocalVariableDeclaration);
        setResult(l);
    }
    // Production: OBSOLETE_OfferStatement ::= offer Expression ';'
    void rule_OBSOLETE_OfferStatement0(Object _Expression) {
        Expr Expression = (Expr) _Expression;
        setResult(nf.Offer(pos(), Expression));
    }
    // Production: AtEachStatement ::= ateach '(' LoopIndex in Expression ')' ClockedClauseopt Statement
    void rule_AtEachStatement0(Object _LoopIndex, Object _Expression, Object _ClockedClauseopt, Object _Statement) {
        X10Formal LoopIndex = (X10Formal) _LoopIndex;
        Expr Expression = (Expr) _Expression;
        List<Expr> ClockedClauseopt = (List<Expr>) _ClockedClauseopt;
        Stmt Statement = (Stmt) _Statement;
        FlagsNode fn = LoopIndex.flags();
        if (! fn.flags().isFinal()) {
            syntaxError("Enhanced ateach loop may not have var loop index. " + LoopIndex, LoopIndex.position());
            fn = fn.flags(fn.flags().Final());
            LoopIndex = LoopIndex.flags(fn);
        }
        setResult(nf.AtEach(pos(),
                LoopIndex,
                Expression,
                ClockedClauseopt,
                Statement));
    }
    // Production: AtEachStatement ::= ateach '(' Expression ')' Statement
    void rule_AtEachStatement1(Object _Expression, Object _Statement) {
        Expr Expression = (Expr) _Expression;
        Stmt Statement = (Stmt) _Statement;
        Id name = nf.Id(pos(), Name.makeFresh());
        TypeNode type = nf.UnknownTypeNode(pos().markCompilerGenerated());
        setResult(nf.AtEach(pos(),
                nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
                Expression,
                new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false),
                Statement));
    }
    // Production: OBSOLETE_Offersopt ::= %Empty
    void rule_OBSOLETE_Offersopt0() {
        setResult(null);
    }
    // Production: TypeDeclarationsopt ::= %Empty
    void rule_TypeDeclarationsopt0() {
        setResult(new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false));
    }
    // Production: ClassMemberDeclarations ::= ClassMemberDeclaration
    void rule_ClassMemberDeclarations0(Object _ClassMemberDeclaration) {
        List<ClassMember> ClassMemberDeclaration = (List<ClassMember>) _ClassMemberDeclaration;
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        if (ClassMemberDeclaration != null) {
            l.addAll(ClassMemberDeclaration);
        }
        setResult(l);
    }
    // Production: ClassMemberDeclarations ::= ClassMemberDeclarations ClassMemberDeclaration
    void rule_ClassMemberDeclarations1(Object _ClassMemberDeclarations, Object _ClassMemberDeclaration) {
        List<ClassMember> ClassMemberDeclarations = (List<ClassMember>) _ClassMemberDeclarations;
        List<ClassMember> ClassMemberDeclaration = (List<ClassMember>) _ClassMemberDeclaration;
        if (ClassMemberDeclaration != null) {
            ClassMemberDeclarations.addAll(ClassMemberDeclaration);
        }
        setResult(ClassMemberDeclarations);
    }
    // Production: WhereClause ::= DepParameters
    void rule_WhereClause0(Object _DepParameters) {
        DepParameterExpr DepParameters = (DepParameterExpr) _DepParameters;
        setResult(DepParameters);
    }
    // Production: InterfaceMemberDeclaration ::= MethodDeclaration
    void rule_InterfaceMemberDeclaration0(Object _MethodDeclaration) {
        ClassMember MethodDeclaration = (ClassMember) _MethodDeclaration;
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        l.add(MethodDeclaration);
        setResult(l);
    }
    // Production: InterfaceMemberDeclaration ::= PropertyMethodDeclaration
    void rule_InterfaceMemberDeclaration1(Object _PropertyMethodDeclaration) {
        ClassMember PropertyMethodDeclaration = (ClassMember) _PropertyMethodDeclaration;
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        l.add(PropertyMethodDeclaration);
        setResult(l);
    }
    // Production: InterfaceMemberDeclaration ::= FieldDeclaration
    void rule_InterfaceMemberDeclaration2(Object _FieldDeclaration) {
        List<ClassMember> FieldDeclaration = (List<ClassMember>) _FieldDeclaration;
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        l.addAll(FieldDeclaration);
        setResult(l);
    }
    // Production: InterfaceMemberDeclaration ::= TypeDeclaration
    void rule_InterfaceMemberDeclaration3(Object _TypeDeclaration) {
        ClassMember TypeDeclaration = (ClassMember) _TypeDeclaration;
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        if (TypeDeclaration != null) {
            l.add(TypeDeclaration);
        }
        setResult(l);
    }
    // Production: PackageDeclaration ::= Annotationsopt package PackageName ';'
    void rule_PackageDeclaration0(Object _Annotationsopt, Object _PackageName) {
        List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) _Annotationsopt;
        ParsedName PackageName = (ParsedName) _PackageName;
        PackageNode pn = PackageName.toPackage();
        pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
        setResult(pn.position(pos()));
    }
    // Production: InterfaceMemberDeclarations ::= InterfaceMemberDeclaration
    void rule_InterfaceMemberDeclarations0(Object _InterfaceMemberDeclaration) {
        List<ClassMember> InterfaceMemberDeclaration = (List<ClassMember>) _InterfaceMemberDeclaration;
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        if (InterfaceMemberDeclaration != null) {
            l.addAll(InterfaceMemberDeclaration);
        }
        setResult(l);
    }
    // Production: InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
    void rule_InterfaceMemberDeclarations1(Object _InterfaceMemberDeclarations, Object _InterfaceMemberDeclaration) {
        List<ClassMember> InterfaceMemberDeclarations = (List<ClassMember>) _InterfaceMemberDeclarations;
        List<ClassMember> InterfaceMemberDeclaration = (List<ClassMember>) _InterfaceMemberDeclaration;
        if (InterfaceMemberDeclaration != null) {
            InterfaceMemberDeclarations.addAll(InterfaceMemberDeclaration);
        }
        setResult(InterfaceMemberDeclarations);
    }
    // Production: MethodInvocation ::= ErrorPrimaryPrefix '(' ArgumentListopt ')'
    void rule_MethodInvocation0(Object _ErrorPrimaryPrefix, Object _ArgumentListopt) {
        Object[] ErrorPrimaryPrefix = (Object[]) _ErrorPrimaryPrefix;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        Expr Primary = (Expr) ErrorPrimaryPrefix[0];
        polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ErrorPrimaryPrefix[1];
        setResult(nf.Call(pos(), Primary,
              nf.Id(identifier.getPosition(), identifier.getIdentifier()), ArgumentListopt));
    }
    // Production: MethodInvocation ::= ErrorSuperPrefix '(' ArgumentListopt ')'
    void rule_MethodInvocation1(Object _ErrorSuperPrefix, Object _ArgumentListopt) {
        Object[] ErrorSuperPrefix = (Object[]) _ErrorSuperPrefix;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        JPGPosition super_pos = (JPGPosition) ErrorSuperPrefix[0];
        polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ErrorSuperPrefix[1];
        setResult(nf.Call(pos(), nf.Super(super_pos),
              nf.Id(identifier.getPosition(), identifier.getIdentifier()), ArgumentListopt));
    }
    // Production: MethodInvocation ::= ErrorClassNameSuperPrefix '(' ArgumentListopt ')'
    void rule_MethodInvocation2(Object _ErrorClassNameSuperPrefix, Object _ArgumentListopt) {
        Object[] ErrorClassNameSuperPrefix = (Object[]) _ErrorClassNameSuperPrefix;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        ParsedName ClassName = (ParsedName) ErrorClassNameSuperPrefix[0];
        JPGPosition super_pos = (JPGPosition) ErrorClassNameSuperPrefix[1];
        polyglot.lex.Identifier identifier = (polyglot.lex.Identifier) ErrorClassNameSuperPrefix[2];
        setResult(nf.Call(pos(), nf.Super(super_pos, ClassName.toType()),
              nf.Id(identifier.getPosition(), identifier.getIdentifier()), ArgumentListopt));
    }
    // Production: MethodInvocation ::= MethodName TypeArgumentsopt '(' ArgumentListopt ')'
    void rule_MethodInvocation3(Object _MethodName, Object _TypeArgumentsopt, Object _ArgumentListopt) {
        ParsedName MethodName = (ParsedName) _MethodName;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        setResult(nf.X10Call(pos(), MethodName.prefix == null
                ? null
                : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt));
    }
    // Production: MethodInvocation ::= Primary '.' Identifier TypeArgumentsopt '(' ArgumentListopt ')'
    void rule_MethodInvocation4(Object _Primary, Object _Identifier, Object _TypeArgumentsopt, Object _ArgumentListopt) {
        Expr Primary = (Expr) _Primary;
        Id Identifier = (Id) _Identifier;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        setResult(nf.X10Call(pos(), Primary, Identifier, TypeArgumentsopt, ArgumentListopt));
    }
    // Production: MethodInvocation ::= super '.' Identifier TypeArgumentsopt '(' ArgumentListopt ')'
    void rule_MethodInvocation5(Object _Identifier, Object _TypeArgumentsopt, Object _ArgumentListopt) {
        Id Identifier = (Id) _Identifier;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        setResult(nf.X10Call(pos(), nf.Super(pos(getLeftSpan())), Identifier, TypeArgumentsopt, ArgumentListopt));
    }
    // Production: MethodInvocation ::= ClassName '.' super '.' Identifier TypeArgumentsopt '(' ArgumentListopt ')'
    void rule_MethodInvocation6(Object _ClassName, Object _Identifier, Object _TypeArgumentsopt, Object _ArgumentListopt) {
        ParsedName ClassName = (ParsedName) _ClassName;
        Id Identifier = (Id) _Identifier;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        setResult(nf.X10Call(pos(), nf.Super(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(3)), ClassName.toType()), Identifier, TypeArgumentsopt, ArgumentListopt));
    }
    // Production: MethodInvocation ::= Primary TypeArgumentsopt '(' ArgumentListopt ')'
    void rule_MethodInvocation7(Object _Primary, Object _TypeArgumentsopt, Object _ArgumentListopt) {
        Expr Primary = (Expr) _Primary;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        if (Primary instanceof Field) {
            Field f = (Field) Primary;
            setResult(nf.X10Call(pos(), f.target(), f.name(), TypeArgumentsopt, ArgumentListopt));
        }
        else if (Primary instanceof AmbExpr) {
            AmbExpr f = (AmbExpr) Primary;
            setResult(nf.X10Call(pos(), null, f.name(), TypeArgumentsopt, ArgumentListopt));
        }
        else {
            setResult(nf.ClosureCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
        }
    }
    // Production: MethodInvocation ::= OperatorPrefix TypeArgumentsopt '(' ArgumentListopt ')'
    void rule_MethodInvocation8(Object _OperatorPrefix, Object _TypeArgumentsopt, Object _ArgumentListopt) {
        Expr OperatorPrefix = (Expr) _OperatorPrefix;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        if (OperatorPrefix instanceof Field) {
            Field f = (Field) OperatorPrefix;
            setResult(nf.X10Call(pos(), f.target(), f.name(), TypeArgumentsopt, ArgumentListopt));
        }
        else if (OperatorPrefix instanceof AmbExpr) {
            AmbExpr f = (AmbExpr) OperatorPrefix;
            setResult(nf.X10Call(pos(), null, f.name(), TypeArgumentsopt, ArgumentListopt));
        }
        else {
            throw new InternalCompilerError("Invalid operator prefix", OperatorPrefix.position());
        }
    }
    // Production: MethodInvocation ::= ClassName '.' operator as '[' Type ']' TypeArgumentsopt '(' ArgumentListopt ')'
    void rule_OperatorPrefix25(Object _ClassName, Object _Type, Object _TypeArgumentsopt, Object _ArgumentListopt) {
        ParsedName ClassName = (ParsedName) _ClassName;
        TypeNode Type = (TypeNode) _Type;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        Name opName = Converter.operator_as;
        setResult(nf.X10ConversionCall(pos(), ClassName.toType(), nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(5)), opName), Type, TypeArgumentsopt, ArgumentListopt));
    }
    // Production: MethodInvocation ::= ClassName '.' operator '[' Type ']' TypeArgumentsopt '(' ArgumentListopt ')'
    void rule_OperatorPrefix26(Object _ClassName, Object _Type, Object _TypeArgumentsopt, Object _ArgumentListopt) {
        ParsedName ClassName = (ParsedName) _ClassName;
        TypeNode Type = (TypeNode) _Type;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        Name opName = Converter.implicit_operator_as;
        setResult(nf.X10ConversionCall(pos(), ClassName.toType(), nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(4)), opName), Type, TypeArgumentsopt, ArgumentListopt));
    }
    // Production: OperatorPrefix ::= operator BinOp
    void rule_OperatorPrefix0(Object _BinOp) {
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), null, nf.Id(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(2)), opName)));
    }
    // Production: OperatorPrefix ::= FullyQualifiedName '.' operator BinOp
    void rule_OperatorPrefix1(Object _FullyQualifiedName, Object _BinOp) {
        ParsedName FullyQualifiedName = (ParsedName) _FullyQualifiedName;
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), FullyQualifiedName.toReceiver(), nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(4)), opName)));
    }
    // Production: OperatorPrefix ::= Primary '.' operator BinOp
    void rule_OperatorPrefix2(Object _Primary, Object _BinOp) {
        Expr Primary = (Expr) _Primary;
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(4)), opName)));
    }
    // Production: OperatorPrefix ::= super '.' operator BinOp
    void rule_OperatorPrefix3(Object _BinOp) {
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(4)), opName)));
    }
    // Production: OperatorPrefix ::= ClassName '.' super '.' operator BinOp
    void rule_OperatorPrefix4(Object _ClassName, Object _BinOp) {
        ParsedName ClassName = (ParsedName) _ClassName;
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), nf.Super(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(6)), opName)));
    }
    // Production: OperatorPrefix ::= operator '(' ')' BinOp
    void rule_OperatorPrefix5(Object _BinOp) {
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), null, nf.Id(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(4)), opName)));
    }
    // Production: OperatorPrefix ::= FullyQualifiedName '.' operator '(' ')' BinOp
    void rule_OperatorPrefix6(Object _FullyQualifiedName, Object _BinOp) {
        ParsedName FullyQualifiedName = (ParsedName) _FullyQualifiedName;
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), FullyQualifiedName.toReceiver(), nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(6)), opName)));
    }
    // Production: OperatorPrefix ::= Primary '.' operator '(' ')' BinOp
    void rule_OperatorPrefix7(Object _Primary, Object _BinOp) {
        Expr Primary = (Expr) _Primary;
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(6)), opName)));
    }
    // Production: OperatorPrefix ::= super '.' operator '(' ')' BinOp
    void rule_OperatorPrefix8(Object _BinOp) {
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(6)), opName)));
    }
    // Production: OperatorPrefix ::= ClassName '.' super '.' operator '(' ')' BinOp
    void rule_OperatorPrefix9(Object _ClassName, Object _BinOp) {
        ParsedName ClassName = (ParsedName) _ClassName;
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), nf.Super(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(8)), opName)));
    }
    // Production: OperatorPrefix ::= operator PrefixOp
    void rule_OperatorPrefix10(Object _PrefixOp) {
        Unary.Operator PrefixOp = (Unary.Operator) _PrefixOp;
        Name opName = X10Unary_c.unaryMethodName(PrefixOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+PrefixOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), null, nf.Id(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(2)), opName)));
    }
    // Production: OperatorPrefix ::= FullyQualifiedName '.' operator PrefixOp
    void rule_OperatorPrefix11(Object _FullyQualifiedName, Object _PrefixOp) {
        ParsedName FullyQualifiedName = (ParsedName) _FullyQualifiedName;
        Unary.Operator PrefixOp = (Unary.Operator) _PrefixOp;
        Name opName = X10Unary_c.unaryMethodName(PrefixOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+PrefixOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), FullyQualifiedName.toReceiver(), nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(4)), opName)));
    }
    // Production: OperatorPrefix ::= Primary '.' operator PrefixOp
    void rule_OperatorPrefix12(Object _Primary, Object _PrefixOp) {
        Expr Primary = (Expr) _Primary;
        Unary.Operator PrefixOp = (Unary.Operator) _PrefixOp;
        Name opName = X10Unary_c.unaryMethodName(PrefixOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+PrefixOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(4)), opName)));
    }
    // Production: OperatorPrefix ::= super '.' operator PrefixOp
    void rule_OperatorPrefix13(Object _PrefixOp) {
        Unary.Operator PrefixOp = (Unary.Operator) _PrefixOp;
        Name opName = X10Unary_c.unaryMethodName(PrefixOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+PrefixOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(4)), opName)));
    }
    // Production: OperatorPrefix ::= ClassName '.' super '.' operator PrefixOp
    void rule_OperatorPrefix14(Object _ClassName, Object _PrefixOp) {
        ParsedName ClassName = (ParsedName) _ClassName;
        Unary.Operator PrefixOp = (Unary.Operator) _PrefixOp;
        Name opName = X10Unary_c.unaryMethodName(PrefixOp);
        if (opName == null) {
            syntaxError("Cannot invoke binary operator '"+PrefixOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        setResult(nf.Field(pos(), nf.Super(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(6)), opName)));
    }
    // Production: OperatorPrefix ::= operator '(' ')'
    void rule_OperatorPrefix15() {
        Name opName = ClosureCall.APPLY;
        setResult(nf.Field(pos(), null, nf.Id(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(3)), opName)));
    }
    // Production: OperatorPrefix ::= FullyQualifiedName '.' operator '(' ')'
    void rule_OperatorPrefix16(Object _FullyQualifiedName) {
        ParsedName FullyQualifiedName = (ParsedName) _FullyQualifiedName;
        Name opName = ClosureCall.APPLY;
        setResult(nf.Field(pos(), FullyQualifiedName.toReceiver(), nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(5)), opName)));
    }
    // Production: OperatorPrefix ::= Primary '.' operator '(' ')'
    void rule_OperatorPrefix17(Object _Primary) {
        Expr Primary = (Expr) _Primary;
        Name opName = ClosureCall.APPLY;
        setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(5)), opName)));
    }
    // Production: OperatorPrefix ::= super '.' operator '(' ')'
    void rule_OperatorPrefix18() {
        Name opName = ClosureCall.APPLY;
        setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(5)), opName)));
    }
    // Production: OperatorPrefix ::= ClassName '.' super '.' operator '(' ')'
    void rule_OperatorPrefix19(Object _ClassName) {
        ParsedName ClassName = (ParsedName) _ClassName;
        Name opName = ClosureCall.APPLY;
        setResult(nf.Field(pos(), nf.Super(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(7)), opName)));
    }
    // Production: OperatorPrefix ::= operator '(' ')' '='
    void rule_OperatorPrefix20() {
        Name opName = ClosureCall.APPLY;
        setResult(nf.Field(pos(), null, nf.Id(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(4)), opName)));
    }
    // Production: OperatorPrefix ::= FullyQualifiedName '.' operator '(' ')' '='
    void rule_OperatorPrefix21(Object _FullyQualifiedName) {
        ParsedName FullyQualifiedName = (ParsedName) _FullyQualifiedName;
        Name opName = SettableAssign.SET;
        setResult(nf.Field(pos(), FullyQualifiedName.toReceiver(), nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(6)), opName)));
    }
    // Production: OperatorPrefix ::= Primary '.' operator '(' ')' '='
    void rule_OperatorPrefix22(Object _Primary) {
        Expr Primary = (Expr) _Primary;
        Name opName = SettableAssign.SET;
        setResult(nf.Field(pos(), Primary, nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(6)), opName)));
    }
    // Production: OperatorPrefix ::= super '.' operator '(' ')' '='
    void rule_OperatorPrefix23() {
        Name opName = SettableAssign.SET;
        setResult(nf.Field(pos(), nf.Super(pos(getLeftSpan())), nf.Id(pos(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(6)), opName)));
    }
    // Production: OperatorPrefix ::= ClassName '.' super '.' operator '(' ')' '='
    void rule_OperatorPrefix24(Object _ClassName) {
        ParsedName ClassName = (ParsedName) _ClassName;
        Name opName = SettableAssign.SET;
        setResult(nf.Field(pos(), nf.Super(pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(3)), ClassName.toType()), nf.Id(pos(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(8)), opName)));
    }
    // Production: PrefixOp ::= '+'
    void rule_PrefixOp0() {
        setResult(Unary.POS);
    }
    // Production: PrefixOp ::= '-'
    void rule_PrefixOp1() {
        setResult(Unary.NEG);
    }
    // Production: PrefixOp ::= '!'
    void rule_PrefixOp2() {
        setResult(Unary.NOT);
    }
    // Production: PrefixOp ::= '~'
    void rule_PrefixOp3() {
        setResult(Unary.BIT_NOT);
    }
    // Production: PrefixOp ::= '^'
    void rule_PrefixOp4() {
        setResult(Unary.CARET);
    }
    // Production: PrefixOp ::= '|'
    void rule_PrefixOp5() {
        setResult(Unary.BAR);
    }
    // Production: PrefixOp ::= '&'
    void rule_PrefixOp6() {
        setResult(Unary.AMPERSAND);
    }
    // Production: PrefixOp ::= '*'
    void rule_PrefixOp7() {
        setResult(Unary.STAR);
    }
    // Production: PrefixOp ::= '/'
    void rule_PrefixOp8() {
        setResult(Unary.SLASH);
    }
    // Production: PrefixOp ::= '%'
    void rule_PrefixOp9() {
        setResult(Unary.PERCENT);
    }
    // Production: PreDecrementExpression ::= '--' UnaryExpressionNotPlusMinus
    void rule_PreDecrementExpression0(Object _UnaryExpressionNotPlusMinus) {
        Expr UnaryExpressionNotPlusMinus = (Expr) _UnaryExpressionNotPlusMinus;
        setResult(nf.Unary(pos(), Unary.PRE_DEC, UnaryExpressionNotPlusMinus));
    }
    // Production: WhileStatement ::= while '(' Expression ')' Statement
    void rule_WhileStatement0(Object _Expression, Object _Statement) {
        Expr Expression = (Expr) _Expression;
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.While(pos(), Expression, Statement));
    }
    // Production: Modifier ::= abstract
    void rule_Modifier0() {
        setResult(new FlagModifier(pos(), FlagModifier.ABSTRACT));
    }
    // Production: Modifier ::= Annotation
    void rule_Modifier1(Object _Annotation) {
        AnnotationNode Annotation = (AnnotationNode) _Annotation;
        setResult(new AnnotationModifier(Annotation));
    }
    // Production: Modifier ::= atomic
    void rule_Modifier2() {
        setResult(new FlagModifier(pos(), FlagModifier.ATOMIC));
    }
    // Production: Modifier ::= final
    void rule_Modifier3() {
        setResult(new FlagModifier(pos(), FlagModifier.FINAL));
    }
    // Production: Modifier ::= native
    void rule_Modifier4() {
        setResult(new FlagModifier(pos(), FlagModifier.NATIVE));
    }
    // Production: Modifier ::= private
    void rule_Modifier5() {
        setResult(new FlagModifier(pos(), FlagModifier.PRIVATE));
    }
    // Production: Modifier ::= protected
    void rule_Modifier6() {
        setResult(new FlagModifier(pos(), FlagModifier.PROTECTED));
    }
    // Production: Modifier ::= public
    void rule_Modifier7() {
        setResult(new FlagModifier(pos(), FlagModifier.PUBLIC));
    }
    // Production: Modifier ::= static
    void rule_Modifier8() {
        setResult(new FlagModifier(pos(), FlagModifier.STATIC));
    }
    // Production: Modifier ::= transient
    void rule_Modifier9() {
        setResult(new FlagModifier(pos(), FlagModifier.TRANSIENT));
    }
    // Production: Modifier ::= clocked
    void rule_Modifier10() {
        setResult(new FlagModifier(pos(), FlagModifier.CLOCKED));
    }
    // Production: ExpressionName ::= FullyQualifiedName '.' ErrorId
    void rule_ExpressionName0(Object _FullyQualifiedName) {
        ParsedName FullyQualifiedName = (ParsedName) _FullyQualifiedName;
        setResult(new ParsedName(nf,
                ts,
                pos(getLeftSpan(), getRightSpan()),
                FullyQualifiedName,
                nf.Id(pos(getRightSpan()), "*")));
    }
    // Production: ExpressionName ::= Identifier
    void rule_ExpressionName1(Object _Identifier) {
        Id Identifier = (Id) _Identifier;
        setResult(new ParsedName(nf, ts, pos(), Identifier));
    }
    // Production: ExpressionName ::= FullyQualifiedName '.' Identifier
    void rule_ExpressionName2(Object _FullyQualifiedName, Object _Identifier) {
        ParsedName FullyQualifiedName = (ParsedName) _FullyQualifiedName;
        Id Identifier = (Id) _Identifier;
        setResult(new ParsedName(nf,
                ts,
                pos(getLeftSpan(), getRightSpan()),
                FullyQualifiedName,
                Identifier));
    }
    // Production: TypeParamsWithVarianceopt ::= %Empty
    void rule_TypeParamsWithVarianceopt0() {
        setResult(new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false));
    }
    // Production: FormalParameterListopt ::= %Empty
    void rule_FormalParameterListopt0() {
        setResult(new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false));
    }
    // Production: ConstraintConjunctionopt ::= %Empty
    void rule_ConstraintConjunctionopt0() {
        List<Expr> l = new ArrayList<Expr>();
        setResult(l);
    }
    // Production: ConstraintConjunctionopt ::= ConstraintConjunction
    void rule_ConstraintConjunctionopt1(Object _ConstraintConjunction) {
        List<Expr> ConstraintConjunction = (List<Expr>) _ConstraintConjunction;
        setResult(ConstraintConjunction);
    }
    // Production: ClassBody ::= '{' ClassMemberDeclarationsopt '}'
    void rule_ClassBody0(Object _ClassMemberDeclarationsopt) {
        List<ClassMember> ClassMemberDeclarationsopt = (List<ClassMember>) _ClassMemberDeclarationsopt;
        setResult(nf.ClassBody(pos(getLeftSpan(), getRightSpan()), ClassMemberDeclarationsopt));
    }
    // Production: Identifier ::= IDENTIFIER
    void rule_Identifier0() {
        // TODO: [IP] Transform escape sequences in quoted identifiers
        setResult( nf.Id(pos(), prsStream.getName(getRhsFirstTokenIndex(1))));
    }
    // Production: AssignmentOperator ::= '='
    void rule_AssignmentOperator0() {
        setResult(Assign.ASSIGN);
    }
    // Production: AssignmentOperator ::= '*='
    void rule_AssignmentOperator1() {
        setResult(Assign.MUL_ASSIGN);
    }
    // Production: AssignmentOperator ::= '/='
    void rule_AssignmentOperator2() {
        setResult(Assign.DIV_ASSIGN);
    }
    // Production: AssignmentOperator ::= '%='
    void rule_AssignmentOperator3() {
        setResult(Assign.MOD_ASSIGN);
    }
    // Production: AssignmentOperator ::= '+='
    void rule_AssignmentOperator4() {
        setResult(Assign.ADD_ASSIGN);
    }
    // Production: AssignmentOperator ::= '-='
    void rule_AssignmentOperator5() {
        setResult(Assign.SUB_ASSIGN);
    }
    // Production: AssignmentOperator ::= '<<='
    void rule_AssignmentOperator6() {
        setResult(Assign.SHL_ASSIGN);
    }
    // Production: AssignmentOperator ::= '>>='
    void rule_AssignmentOperator7() {
        setResult(Assign.SHR_ASSIGN);
    }
    // Production: AssignmentOperator ::= '>>>='
    void rule_AssignmentOperator8() {
        setResult(Assign.USHR_ASSIGN);
    }
    // Production: AssignmentOperator ::= '&='
    void rule_AssignmentOperator9() {
        setResult(Assign.BIT_AND_ASSIGN);
    }
    // Production: AssignmentOperator ::= '^='
    void rule_AssignmentOperator10() {
        setResult(Assign.BIT_XOR_ASSIGN);
    }
    // Production: AssignmentOperator ::= '|='
    void rule_AssignmentOperator11() {
        setResult(Assign.BIT_OR_ASSIGN);
    }
    // Production: AssignmentOperator ::= '..='
    void rule_AssignmentOperator12() {
        setResult(Assign.DOT_DOT_ASSIGN);
    }
    // Production: AssignmentOperator ::= '->='
    void rule_AssignmentOperator13() {
        setResult(Assign.ARROW_ASSIGN);
    }
    // Production: AssignmentOperator ::= '<-='
    void rule_AssignmentOperator14() {
        setResult(Assign.LARROW_ASSIGN);
    }
    // Production: AssignmentOperator ::= '-<='
    void rule_AssignmentOperator15() {
        setResult(Assign.FUNNEL_ASSIGN);
    }
    // Production: AssignmentOperator ::= '>-='
    void rule_AssignmentOperator16() {
        setResult(Assign.LFUNNEL_ASSIGN);
    }
    // Production: AssignmentOperator ::= '**='
    void rule_AssignmentOperator17() {
        setResult(Assign.STARSTAR_ASSIGN);
    }
    // Production: AssignmentOperator ::= '<>='
    void rule_AssignmentOperator18() {
        setResult(Assign.DIAMOND_ASSIGN);
    }
    // Production: AssignmentOperator ::= '><='
    void rule_AssignmentOperator19() {
        setResult(Assign.BOWTIE_ASSIGN);
    }
    // Production: AssignmentOperator ::= '~='
    void rule_AssignmentOperator20() {
        setResult(Assign.TWIDDLE_ASSIGN);
    }
    // Production: ForUpdateopt ::= %Empty
    void rule_ForUpdateopt0() {
        setResult(new TypedList<ForUpdate>(new LinkedList<ForUpdate>(), ForUpdate.class, false));
    }
    // Production: AndExpression ::= AndExpression '&' EqualityExpression
    void rule_AndExpression1(Object _AndExpression, Object _EqualityExpression) {
        Expr AndExpression = (Expr) _AndExpression;
        Expr EqualityExpression = (Expr) _EqualityExpression;
        setResult(nf.Binary(pos(), AndExpression, Binary.BIT_AND, EqualityExpression));
    }
    // Production: OBSOLETE_FinishExpression ::= finish '(' Expression ')' Block
    void rule_OBSOLETE_FinishExpression0(Object _Expression, Object _Block) {
        Expr Expression = (Expr) _Expression;
        Block Block = (Block) _Block;
        setResult(nf.FinishExpr(pos(), Expression, Block));
    }
    // Production: ReturnStatement ::= return Expressionopt ';'
    void rule_ReturnStatement0(Object _Expressionopt) {
        Expr Expressionopt = (Expr) _Expressionopt;
        setResult(nf.Return(pos(), Expressionopt));
    }
    // Production: SubtypeConstraint ::= Type '<:' Type
    void rule_SubtypeConstraint0(Object _t1, Object _t2) {
        TypeNode t1 = (TypeNode) _t1;
        TypeNode t2 = (TypeNode) _t2;
        setResult(nf.SubtypeTest(pos(), t1, t2, false));
    }
    // Production: SubtypeConstraint ::= Type ':>' Type
    void rule_SubtypeConstraint1(Object _t1, Object _t2) {
        TypeNode t1 = (TypeNode) _t1;
        TypeNode t2 = (TypeNode) _t2;
        setResult(nf.SubtypeTest(pos(), t2, t1, false));
    }
    // Production: Catchesopt ::= %Empty
    void rule_Catchesopt0() {
        setResult(new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false));
    }
    // Production: MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt MethodBody
    void rule_MethodDeclaration0(Object _MethodModifiersopt, Object _Identifier, Object _TypeParametersopt, Object _FormalParameters, Object _WhereClauseopt, Object _HasResultTypeopt, Object _OBSOLETE_Offersopt, Object _Throwsopt, Object _MethodBody) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        Id Identifier = (Id) _Identifier;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        List<Formal> FormalParameters = (List<Formal>) _FormalParameters;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        List<TypeNode> Throwsopt = (List<TypeNode>) _Throwsopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos().endOf() : MethodBody.position().startOf();
        ProcedureDecl pd = nf.X10MethodDecl(pos(),
                    extractFlags(modifiers),
                    HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                    Identifier,
                    TypeParametersopt,
                    FormalParameters,
                    WhereClauseopt,
                    OBSOLETE_Offersopt,
                    Throwsopt,
                    MethodBody);
        pd = (ProcedureDecl) ((X10Ext) pd.ext()).annotations(extractAnnotations(modifiers));
        setResult(pd);
    }
    // Production: BinaryOperatorDeclaration ::= MethodModifiersopt operator TypeParametersopt '(' FormalParameter ')' BinOp '(' FormalParameter ')' WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt MethodBody
    void rule_MethodDeclaration1(Object _MethodModifiersopt, Object _TypeParametersopt, Object _fp1, Object _BinOp, Object _fp2, Object _WhereClauseopt, Object _HasResultTypeopt, Object _OBSOLETE_Offersopt, Object _Throwsopt, Object _MethodBody) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        X10Formal fp1 = (X10Formal) _fp1;
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        X10Formal fp2 = (X10Formal) _fp2;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        List<TypeNode> throwsopt = (List<TypeNode>) _Throwsopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            syntaxError("Cannot override binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos().endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(),
                extractFlags(modifiers),
                HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(getRhsFirstTokenIndex(7)), opName),
                TypeParametersopt,
                Arrays.<Formal>asList(fp1, fp2),
                WhereClauseopt,
                OBSOLETE_Offersopt,
                throwsopt,
                MethodBody);
        FlagsNode flags = md.flags();
        if (! flags.flags().isStatic()) {
            syntaxError("Binary operator with two parameters must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        setResult(md);
    }
    // Production: PrefixOperatorDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp '(' FormalParameter ')' WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt MethodBody
    void rule_MethodDeclaration2(Object _MethodModifiersopt, Object _TypeParametersopt, Object _PrefixOp, Object _fp2, Object _WhereClauseopt, Object _HasResultTypeopt, Object _OBSOLETE_Offersopt, Object _Throwsopt, Object _MethodBody) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        Unary.Operator PrefixOp = (Unary.Operator) _PrefixOp;
        X10Formal fp2 = (X10Formal) _fp2;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        List<TypeNode> throwsopt = (List<TypeNode>) _Throwsopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Unary_c.unaryMethodName(PrefixOp);
        if (opName == null) {
            syntaxError("Cannot override unary operator '"+PrefixOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos().endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(),
                extractFlags(modifiers),
                HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(getRhsFirstTokenIndex(4)), opName),
                TypeParametersopt,
                Collections.<Formal>singletonList(fp2),
                WhereClauseopt,
                OBSOLETE_Offersopt,
                throwsopt,
                MethodBody);
        FlagsNode flags = md.flags();
        if (! flags.flags().isStatic()) {
            syntaxError("Unary operator with one parameter must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        setResult(md);
    }
    // Production: BinaryOperatorDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp '(' FormalParameter ')' WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt MethodBody
    void rule_MethodDeclaration3(Object _MethodModifiersopt, Object _TypeParametersopt, Object _BinOp, Object _fp2, Object _WhereClauseopt, Object _HasResultTypeopt, Object _OBSOLETE_Offersopt, Object _Throwsopt, Object _MethodBody) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        X10Formal fp2 = (X10Formal) _fp2;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        List<TypeNode> throwsopt = (List<TypeNode>) _Throwsopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            syntaxError("Cannot override binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos().endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(),
                extractFlags(modifiers),
                HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(getRhsFirstTokenIndex(5)), opName),
                TypeParametersopt,
                Collections.<Formal>singletonList(fp2),
                WhereClauseopt,
                OBSOLETE_Offersopt,
                throwsopt,
                MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            syntaxError("Binary operator with this parameter cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        setResult(md);
    }
    // Production: BinaryOperatorDeclaration ::= MethodModifiersopt operator TypeParametersopt '(' FormalParameter ')' BinOp this WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt MethodBody
    void rule_MethodDeclaration4(Object _MethodModifiersopt, Object _TypeParametersopt, Object _fp1, Object _BinOp, Object _WhereClauseopt, Object _HasResultTypeopt, Object _OBSOLETE_Offersopt, Object _Throwsopt, Object _MethodBody) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        X10Formal fp1 = (X10Formal) _fp1;
        Binary.Operator BinOp = (Binary.Operator) _BinOp;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        List<TypeNode> throwsopt = (List<TypeNode>) _Throwsopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
        	// [DC] doesn't look like this can ever happen?
            syntaxError("Cannot override binary operator '"+BinOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos().endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(),
                extractFlags(modifiers),
                HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(getRhsFirstTokenIndex(7)), opName),
                TypeParametersopt,
                Collections.<Formal>singletonList(fp1),
                WhereClauseopt,
                OBSOLETE_Offersopt,
                throwsopt,
                MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            syntaxError("Binary operator with this parameter cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        setResult(md);
    }
    // Production: PrefixOperatorDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt MethodBody
    void rule_MethodDeclaration5(Object _MethodModifiersopt, Object _TypeParametersopt, Object _PrefixOp, Object _WhereClauseopt, Object _HasResultTypeopt, Object _OBSOLETE_Offersopt, Object _Throwsopt, Object _MethodBody) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        Unary.Operator PrefixOp = (Unary.Operator) _PrefixOp;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        List<TypeNode> throwsopt = (List<TypeNode>) _Throwsopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Unary_c.unaryMethodName(PrefixOp);
        if (opName == null) {
            syntaxError("Cannot override unary operator '"+PrefixOp+"'.", pos());
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos().endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(),
                extractFlags(modifiers),
                HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(getRhsFirstTokenIndex(4)), opName),
                TypeParametersopt,
                Collections.<Formal>emptyList(),
                WhereClauseopt,
                OBSOLETE_Offersopt,
                throwsopt,
                MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            syntaxError("Unary operator with this parameter cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        setResult(md);
    }
    // Production: ApplyOperatorDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt MethodBody
    void rule_MethodDeclaration6(Object _MethodModifiersopt, Object _TypeParametersopt, Object _FormalParameters, Object _WhereClauseopt, Object _HasResultTypeopt, Object _OBSOLETE_Offersopt, Object _Throwsopt, Object _MethodBody) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        List<Formal> FormalParameters = (List<Formal>) _FormalParameters;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        List<TypeNode> throwsopt = (List<TypeNode>) _Throwsopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos().endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(),
                extractFlags(modifiers),
                HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(), ClosureCall.APPLY),
                TypeParametersopt,
                FormalParameters,
                WhereClauseopt,
                OBSOLETE_Offersopt,
                throwsopt,
                MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            syntaxError("operator() cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        setResult(md);
    }
    // Production: SetOperatorDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters '=' '(' FormalParameter ')' WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt MethodBody
    void rule_MethodDeclaration7(Object _MethodModifiersopt, Object _TypeParametersopt, Object _FormalParameters, Object _fp2, Object _WhereClauseopt, Object _HasResultTypeopt, Object _OBSOLETE_Offersopt, Object _Throwsopt, Object _MethodBody) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        List<Formal> FormalParameters = (List<Formal>) _FormalParameters;
        X10Formal fp2 = (X10Formal) _fp2;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        List<TypeNode> throwsopt = (List<TypeNode>) _Throwsopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos().endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(),
                extractFlags(modifiers),
                HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(), SettableAssign.SET),
                TypeParametersopt,
                CollectionUtil.append(FormalParameters, Collections.singletonList(fp2)),
                WhereClauseopt,
                OBSOLETE_Offersopt,
                throwsopt,
                MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            syntaxError("Set operator cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        setResult(md);
    }
    // Production: ExplicitConversionOperatorDeclaration ::= MethodModifiersopt operator TypeParametersopt '(' FormalParameter ')' as Type WhereClauseopt OBSOLETE_Offersopt MethodBody
    void rule_MethodDeclaration8(Object _MethodModifiersopt, Object _TypeParametersopt, Object _fp1, Object _Type, Object _WhereClauseopt, Object _OBSOLETE_Offersopt, Object _Throwsopt, Object _MethodBody) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        X10Formal fp1 = (X10Formal) _fp1;
        TypeNode Type = (TypeNode) _Type;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        List<TypeNode> throwsopt = (List<TypeNode>) _Throwsopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        MethodDecl md = nf.X10MethodDecl(pos(),
                extractFlags(modifiers),
                Type,
                nf.Id(pos(), Converter.operator_as),
                TypeParametersopt,
                Collections.<Formal>singletonList(fp1),
                WhereClauseopt,
                OBSOLETE_Offersopt,
                throwsopt,
                MethodBody);
        FlagsNode flags = md.flags();
        if (! flags.flags().isStatic()) {
            syntaxError("Conversion operator must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        setResult(md);
    }
    // Production: ExplicitConversionOperatorDeclaration ::= MethodModifiersopt operator TypeParametersopt '(' FormalParameter ')' as '?' WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt MethodBody
    void rule_MethodDeclaration9(Object _MethodModifiersopt, Object _TypeParametersopt, Object _fp1, Object _WhereClauseopt, Object _HasResultTypeopt, Object _OBSOLETE_Offersopt, Object _Throwsopt, Object _MethodBody) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        X10Formal fp1 = (X10Formal) _fp1;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        List<TypeNode> throwsopt = (List<TypeNode>) _Throwsopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos().endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(),
                extractFlags(modifiers),
                HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(), Converter.operator_as),
                TypeParametersopt,
                Collections.<Formal>singletonList(fp1),
                WhereClauseopt,
                OBSOLETE_Offersopt,
                throwsopt,
                MethodBody);
        FlagsNode flags = md.flags();
        if (! flags.flags().isStatic()) {
            syntaxError("Conversion operator must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        setResult(md);
    }
    // Production: ImplicitConversionOperatorDeclaration ::= MethodModifiersopt operator TypeParametersopt '(' FormalParameter ')' WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt MethodBody
    void rule_MethodDeclaration10(Object _MethodModifiersopt, Object _TypeParametersopt, Object _fp1, Object _WhereClauseopt, Object _HasResultTypeopt, Object _OBSOLETE_Offersopt, Object _Throwsopt, Object _MethodBody) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        X10Formal fp1 = (X10Formal) _fp1;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        List<TypeNode> throwsopt = (List<TypeNode>) _Throwsopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos().endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(),
                extractFlags(modifiers),
                HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(), Converter.implicit_operator_as),
                TypeParametersopt,
                Collections.<Formal>singletonList(fp1),
                WhereClauseopt,
                OBSOLETE_Offersopt,
                throwsopt,
                MethodBody);
        FlagsNode flags = md.flags();
        if (! flags.flags().isStatic()) {
            syntaxError("Conversion operator must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        setResult(md);
    }
    // Production: AssertStatement ::= assert Expression ';'
    void rule_AssertStatement0(Object _Expression) {
        Expr Expression = (Expr) _Expression;
        setResult(nf.Assert(pos(), Expression));
    }
    // Production: AssertStatement ::= assert Expression ':' Expression ';'
    void rule_AssertStatement1(Object _expr1, Object _expr2) {
        Expr expr1 = (Expr) _expr1;
        Expr expr2 = (Expr) _expr2;
        setResult(nf.Assert(pos(), expr1, expr2));
    }
    // Production: DepParameters ::= '{' FUTURE_ExistentialListopt ConstraintConjunctionopt '}'
    void rule_DepParameters0(Object _FUTURE_ExistentialListopt, Object _ConstraintConjunctionopt) {
        List<Formal> FUTURE_ExistentialListopt = (List<Formal>) _FUTURE_ExistentialListopt;
        List<Expr> ConstraintConjunctionopt = (List<Expr>) _ConstraintConjunctionopt;
        setResult(nf.DepParameterExpr(pos(), FUTURE_ExistentialListopt, ConstraintConjunctionopt));
    }
    // Production: DoStatement ::= do Statement while '(' Expression ')' ';'
    void rule_DoStatement0(Object _Statement, Object _Expression) {
        Stmt Statement = (Stmt) _Statement;
        Expr Expression = (Expr) _Expression;
        setResult(nf.Do(pos(), Statement, Expression));
    }
    // Production: PostDecrementExpression ::= PostfixExpression '--'
    void rule_PostDecrementExpression0(Object _PostfixExpression) {
        Expr PostfixExpression = (Expr) _PostfixExpression;
        setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_DEC));
    }
    // Production: ShiftExpression ::= ShiftExpression '->' AdditiveExpression
    void rule_ShiftExpression4(Object _expr1, Object _expr2) {
        Expr expr1 = (Expr) _expr1;
        Expr expr2 = (Expr) _expr2;
        Expr call = nf.Binary(pos(), expr1, Binary.ARROW, expr2);
        setResult(call);
    }
    // Production: ShiftExpression ::= ShiftExpression '<-' AdditiveExpression
    void rule_ShiftExpression5(Object _expr1, Object _expr2) {
        Expr expr1 = (Expr) _expr1;
        Expr expr2 = (Expr) _expr2;
        Expr call = nf.Binary(pos(), expr1, Binary.LARROW, expr2);
        setResult(call);
    }
    // Production: ShiftExpression ::= ShiftExpression '-<' AdditiveExpression
    void rule_ShiftExpression6(Object _expr1, Object _expr2) {
        Expr expr1 = (Expr) _expr1;
        Expr expr2 = (Expr) _expr2;
        Expr call = nf.Binary(pos(), expr1, Binary.FUNNEL, expr2);
        setResult(call);
    }
    // Production: ShiftExpression ::= ShiftExpression '>-' AdditiveExpression
    void rule_ShiftExpression7(Object _expr1, Object _expr2) {
        Expr expr1 = (Expr) _expr1;
        Expr expr2 = (Expr) _expr2;
        Expr call = nf.Binary(pos(), expr1, Binary.LFUNNEL, expr2);
        setResult(call);
    }
    // Production: ShiftExpression ::= ShiftExpression '!' AdditiveExpression
    void rule_ShiftExpression8(Object _expr1, Object _expr2) {
        Expr expr1 = (Expr) _expr1;
        Expr expr2 = (Expr) _expr2;
        Expr call = nf.Binary(pos(), expr1, Binary.BANG, expr2);
        setResult(call);
    }
    // Production: ShiftExpression ::= ShiftExpression '<>' AdditiveExpression
    void rule_ShiftExpression9(Object _expr1, Object _expr2) {
        Expr expr1 = (Expr) _expr1;
        Expr expr2 = (Expr) _expr2;
        Expr call = nf.Binary(pos(), expr1, Binary.DIAMOND, expr2);
        setResult(call);
    }
    // Production: ShiftExpression ::= ShiftExpression '><' AdditiveExpression
    void rule_ShiftExpression10(Object _expr1, Object _expr2) {
        Expr expr1 = (Expr) _expr1;
        Expr expr2 = (Expr) _expr2;
        Expr call = nf.Binary(pos(), expr1, Binary.BOWTIE, expr2);
        setResult(call);
    }
    // Production: ExplicitConstructorInvocation ::= this TypeArgumentsopt '(' ArgumentListopt ')' ';'
    void rule_ExplicitConstructorInvocation0(Object _TypeArgumentsopt, Object _ArgumentListopt) {
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        setResult(nf.X10ThisCall(pos(), TypeArgumentsopt, ArgumentListopt));
    }
    // Production: ExplicitConstructorInvocation ::= super TypeArgumentsopt '(' ArgumentListopt ')' ';'
    void rule_ExplicitConstructorInvocation1(Object _TypeArgumentsopt, Object _ArgumentListopt) {
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        setResult(nf.X10SuperCall(pos(), TypeArgumentsopt, ArgumentListopt));
    }
    // Production: ExplicitConstructorInvocation ::= Primary '.' this TypeArgumentsopt '(' ArgumentListopt ')' ';'
    void rule_ExplicitConstructorInvocation2(Object _Primary, Object _TypeArgumentsopt, Object _ArgumentListopt) {
        Expr Primary = (Expr) _Primary;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        setResult(nf.X10ThisCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
    }
    // Production: ExplicitConstructorInvocation ::= Primary '.' super TypeArgumentsopt '(' ArgumentListopt ')' ';'
    void rule_ExplicitConstructorInvocation3(Object _Primary, Object _TypeArgumentsopt, Object _ArgumentListopt) {
        Expr Primary = (Expr) _Primary;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        setResult(nf.X10SuperCall(pos(), Primary, TypeArgumentsopt, ArgumentListopt));
    }
    // Production: FormalParameter ::= Modifiersopt FormalDeclarator
    void rule_FormalParameter0(Object _Modifiersopt, Object _FormalDeclarator) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        Object[] FormalDeclarator = (Object[]) _FormalDeclarator;
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        Formal f;
        FlagsNode fn = extractFlags(modifiers, Flags.FINAL);
        Object[] o = FormalDeclarator;
        Position pos = (Position) o[0];
        Id name = (Id) o[1];
        boolean unnamed = name == null;
        if (name == null) name = nf.Id(pos.markCompilerGenerated(), Name.makeFresh());
        List<Id> exploded = (List<Id>) o[2];
        DepParameterExpr guard = (DepParameterExpr) o[3];
        TypeNode type = (TypeNode) o[4];
        if (type == null) type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        Expr init = (Expr) o[5];
        List<Formal> explodedFormals = createExplodedFormals(exploded);
        f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
        f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
        setResult(f);
    }
    // Production: FormalParameter ::= Modifiersopt VarKeyword FormalDeclarator
    void rule_FormalParameter1(Object _Modifiersopt, Object _VarKeyword, Object _FormalDeclarator) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        List<FlagsNode> VarKeyword = (List<FlagsNode>) _VarKeyword;
        Object[] FormalDeclarator = (Object[]) _FormalDeclarator;
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        Formal f;
        FlagsNode fn = extractFlags(modifiers, VarKeyword);
        Object[] o = FormalDeclarator;
        Position pos = (Position) o[0];
        Id name = (Id) o[1];
        boolean unnamed = name == null;
        if (name == null) name = nf.Id(pos.markCompilerGenerated(), Name.makeFresh());
        List<Id> exploded = (List<Id>) o[2];
        DepParameterExpr guard = (DepParameterExpr) o[3];
        TypeNode type = (TypeNode) o[4];
        if (type == null) type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        Expr init = (Expr) o[5];
        List<Formal> explodedFormals = createExplodedFormals(exploded);
        f = nf.X10Formal(pos(), fn, type, name, explodedFormals, unnamed);
        f = (Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
        setResult(f);
    }
    // Production: FormalParameter ::= Type
    void rule_FormalParameter2(Object _Type) {
        TypeNode Type = (TypeNode) _Type;
        Formal f;
        f = nf.X10Formal(pos().markCompilerGenerated(), nf.FlagsNode(pos().markCompilerGenerated(), Flags.FINAL), Type, nf.Id(pos().markCompilerGenerated(), Name.makeFresh("id")), Collections.<Formal>emptyList(), true);
        setResult(f);
    }
    // Production: BasicForStatement ::= for '(' ForInitopt ';' Expressionopt ';' ForUpdateopt ')' Statement
    void rule_BasicForStatement0(Object _ForInitopt, Object _Expressionopt, Object _ForUpdateopt, Object _Statement) {
        List<ForInit> ForInitopt = (List<ForInit>) _ForInitopt;
        Expr Expressionopt = (Expr) _Expressionopt;
        List<ForUpdate> ForUpdateopt = (List<ForUpdate>) _ForUpdateopt;
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.For(pos(), ForInitopt, Expressionopt, ForUpdateopt, Statement));
    }
    // Production: Properties ::= '(' PropertyList ')'
    void rule_Properties0(Object _PropertyList) {
        List<PropertyDecl> PropertyList = (List<PropertyDecl>) _PropertyList;
        setResult(PropertyList);
    }
    // Production: SwitchStatement ::= switch '(' Expression ')' SwitchBlock
    void rule_SwitchStatement0(Object _Expression, Object _SwitchBlock) {
        Expr Expression = (Expr) _Expression;
        List<SwitchElement> SwitchBlock = (List<SwitchElement>) _SwitchBlock;
        setResult(nf.Switch(pos(), Expression, SwitchBlock));
    }
    // Production: ThrowStatement ::= throw Expression ';'
    void rule_ThrowStatement0(Object _Expression) {
        Expr Expression = (Expr) _Expression;
        setResult(nf.Throw(pos(), Expression));
    }
    // Production: ContinueStatement ::= continue Identifieropt ';'
    void rule_ContinueStatement0(Object _Identifieropt) {
        Id Identifieropt = (Id) _Identifieropt;
        setResult(nf.Continue(pos(), Identifieropt));
    }
    // Production: StatementExpressionList ::= StatementExpression
    void rule_StatementExpressionList0(Object _StatementExpression) {
        Expr StatementExpression = (Expr) _StatementExpression;
        List<Eval> l = new TypedList<Eval>(new LinkedList<Eval>(), Eval.class, false);
        l.add(nf.Eval(pos(), StatementExpression));
        setResult(l);
    }
    // Production: StatementExpressionList ::= StatementExpressionList ',' StatementExpression
    void rule_StatementExpressionList1(Object _StatementExpressionList, Object _StatementExpression) {
        List<Eval> StatementExpressionList = (List<Eval>) _StatementExpressionList;
        Expr StatementExpression = (Expr) _StatementExpression;
        StatementExpressionList.add(nf.Eval(pos(), StatementExpression));
    }
    // Production: SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
    void rule_SwitchBlockStatementGroups1(Object _SwitchBlockStatementGroups, Object _SwitchBlockStatementGroup) {
        List<SwitchElement> SwitchBlockStatementGroups = (List<SwitchElement>) _SwitchBlockStatementGroups;
        List<SwitchElement> SwitchBlockStatementGroup = (List<SwitchElement>) _SwitchBlockStatementGroup;
        SwitchBlockStatementGroups.addAll(SwitchBlockStatementGroup);
        // setResult(SwitchBlockStatementGroups);
    }
    // Production: TypeDefDeclaration ::= Modifiersopt type Identifier TypeParametersopt WhereClauseopt '=' Type ';'
    void rule_TypeDefDeclaration0(Object _Modifiersopt, Object _Identifier, Object _TypeParametersopt, Object _WhereClauseopt, Object _Type) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        Id Identifier = (Id) _Identifier;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode Type = (TypeNode) _Type;
        List<Node> modifiers = checkTypeDefModifiers(Modifiersopt);
        FlagsNode f = extractFlags(modifiers);
        List<AnnotationNode> annotations = extractAnnotations(modifiers);
        List<Formal> formals = new ArrayList<Formal>();
        TypeDecl cd = nf.TypeDecl(pos(), f, Identifier, TypeParametersopt, formals, WhereClauseopt, Type);
        cd = (TypeDecl) ((X10Ext) cd.ext()).annotations(annotations);
        setResult(cd);
    }
    // Production: TypeDefDeclaration ::= Modifiersopt type Identifier TypeParametersopt ( FormalParameterList ) WhereClauseopt '=' Type ';'
    void rule_TypeDefDeclaration1(Object _Modifiersopt, Object _Identifier, Object _TypeParametersopt, Object _FormalParameterList, Object _WhereClauseopt, Object _Type) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        Id Identifier = (Id) _Identifier;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        List<Formal> FormalParameterList = (List<Formal>) _FormalParameterList;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode Type = (TypeNode) _Type;
        List<Node> modifiers = checkTypeDefModifiers(Modifiersopt);
        FlagsNode f = extractFlags(modifiers);
        List<AnnotationNode> annotations = extractAnnotations(modifiers);
        List<Formal> formals = new ArrayList<Formal>();
        for (Formal v : FormalParameterList) {
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
    }
    // Production: PropertyMethodDeclaration ::= MethodModifiersopt Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt MethodBody
    void rule_PropertyMethodDeclaration0(Object _MethodModifiersopt, Object _Identifier, Object _TypeParametersopt, Object _FormalParameters, Object _WhereClauseopt, Object _HasResultTypeopt, Object _MethodBody) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        Id Identifier = (Id) _Identifier;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        List<Formal> FormalParameters = (List<Formal>) _FormalParameters;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        MethodDecl md = nf.X10MethodDecl(pos(),
                extractFlags(modifiers, Flags.PROPERTY),
                HasResultTypeopt == null ? nf.UnknownTypeNode(pos().markCompilerGenerated()) : HasResultTypeopt,
                Identifier,
                TypeParametersopt,
                FormalParameters,
                WhereClauseopt,
                null, // offersOpt
                Collections.<TypeNode>emptyList(),
                MethodBody);
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        setResult(md);
    }
    // Production: PropertyMethodDeclaration ::= MethodModifiersopt Identifier WhereClauseopt HasResultTypeopt MethodBody
    void rule_PropertyMethodDeclaration1(Object _MethodModifiersopt, Object _Identifier, Object _WhereClauseopt, Object _HasResultTypeopt, Object _MethodBody) {
        syntaxError("This syntax is no longer supported. You must supply the property method formals, and if there are none, you can use an empty parenthesis '()'.",pos());
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        Id Identifier = (Id) _Identifier;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        Block MethodBody = (Block) _MethodBody;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        MethodDecl md = nf.X10MethodDecl(pos(),
                extractFlags(modifiers, Flags.PROPERTY),
                HasResultTypeopt == null ? nf.UnknownTypeNode(pos().markCompilerGenerated()) : HasResultTypeopt,
                Identifier,
                Collections.<TypeParamNode>emptyList(),
                Collections.<Formal>emptyList(),
                WhereClauseopt,
                null, // offersOpt
                Collections.<TypeNode>emptyList(),
                MethodBody);
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        setResult(md);
    }
    // Production: ExtendsInterfaces ::= extends Type
    void rule_ExtendsInterfaces0(Object _Type) {
        TypeNode Type = (TypeNode) _Type;
        List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
        l.add(Type);
        setResult(l);
    }
    // Production: ExtendsInterfaces ::= ExtendsInterfaces ',' Type
    void rule_ExtendsInterfaces1(Object _ExtendsInterfaces, Object _Type) {
        List<TypeNode> ExtendsInterfaces = (List<TypeNode>) _ExtendsInterfaces;
        TypeNode Type = (TypeNode) _Type;
        ExtendsInterfaces.add(Type);
    }
    // Production: SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
    void rule_SwitchBlockStatementGroup0(Object _SwitchLabels, Object _BlockStatements) {
        List<SwitchElement> SwitchLabels = (List<SwitchElement>) _SwitchLabels;
        List<Stmt> BlockStatements = (List<Stmt>) _BlockStatements;
        List<SwitchElement> l = new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false);
        l.addAll(SwitchLabels);
        l.add(nf.SwitchBlock(pos(), BlockStatements));
        setResult(l);
    }
    // Production: TypeParametersopt ::= %Empty
    void rule_TypeParametersopt0() {
        setResult(new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false));
    }
    // Production: AtStatement ::= at ( Expression ) Statement
    // Production: AtStatement ::= at ( Expression ; * ) Statement
    void rule_AtStatement0(Object _Expression, Object _Statement) {
        Expr Expression = (Expr) _Expression;
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.AtStmt(pos(), Expression, Statement));
    }
    // Production: AtStatement ::= at ( Expression ; AtCaptureDeclaratorsopt ) Statement
    void rule_AtStatement1(Object _Expression, Object _AtCaptureDeclaratorsopt, Object _Statement) {
        Expr Expression = (Expr) _Expression;
        List<Node> AtCaptureDeclaratorsopt = (List<Node>) _AtCaptureDeclaratorsopt;
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.AtStmt(pos(), Expression, AtCaptureDeclaratorsopt, Statement));
    }
    // Production: AtStatement ::= athome ( HomeVariableList ) Statement
    // Production: AtStatement ::= athome ( HomeVariableList ; * ) Statement
    void rule_AtStatement2(Object _HomeVariableList, Object _Statement) {
        List<Expr> HomeVariableList = (List<Expr>) _HomeVariableList;
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.AtHomeStmt(pos(), HomeVariableList, Statement));
    }
    // Production: AtStatement ::= athome ( HomeVariableList ; AtCaptureDeclaratorsopt ) Statement
    void rule_AtStatement3(Object _HomeVariableList, Object _AtCaptureDeclaratorsopt, Object _Statement) {
        List<Expr> HomeVariableList = (List<Expr>) _HomeVariableList;
        List<Node> AtCaptureDeclaratorsopt = (List<Node>) _AtCaptureDeclaratorsopt;
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.AtHomeStmt(pos(), HomeVariableList, AtCaptureDeclaratorsopt, Statement));
    }
    // Production: AtStatement ::= athome Statement
    // Production: AtStatement ::= athome ( * ) Statement
    // Production: AtStatement ::= athome ( * ; * ) Statement
    void rule_AtStatement4(Object _Statement) {
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.AtHomeStmt(pos(), null, Statement));
    }
    // Production: AtStatement ::= athome ( * ; AtCaptureDeclaratorsopt ) Statement
    void rule_AtStatement5(Object _AtCaptureDeclaratorsopt, Object _Statement) {
        List<Node> AtCaptureDeclaratorsopt = (List<Node>) _AtCaptureDeclaratorsopt;
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.AtHomeStmt(pos(), null, AtCaptureDeclaratorsopt, Statement));
    }
    // Production: ConstructorBody ::= '=' ConstructorBlock
    void rule_ConstructorBody0(Object _ConstructorBlock) {
        Block ConstructorBlock = (Block) _ConstructorBlock;
        setResult(ConstructorBlock);
    }
    // Production: ConstructorBody ::= ConstructorBlock
    void rule_ConstructorBody1(Object _ConstructorBlock) {
        Block ConstructorBlock = (Block) _ConstructorBlock;
        setResult(ConstructorBlock);
    }
    // Production: ConstructorBody ::= '=' ExplicitConstructorInvocation
    void rule_ConstructorBody2(Object _ExplicitConstructorInvocation) {
        ConstructorCall ExplicitConstructorInvocation = (ConstructorCall) _ExplicitConstructorInvocation;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(ExplicitConstructorInvocation);
        setResult(nf.Block(pos(), l));
    }
    // Production: ConstructorBody ::= '=' AssignPropertyCall
    void rule_ConstructorBody3(Object _AssignPropertyCall) {
        Stmt AssignPropertyCall = (Stmt) _AssignPropertyCall;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(AssignPropertyCall);
        setResult(nf.Block(pos(), l));
    }
    // Production: WhenStatement ::= when '(' Expression ')' Statement
    void rule_WhenStatement0(Object _Expression, Object _Statement) {
        Expr Expression = (Expr) _Expression;
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.When(pos(), Expression, Statement));
    }
    // Production: AsyncStatement ::= async ClockedClauseopt Statement
    void rule_AsyncStatement0(Object _ClockedClauseopt, Object _Statement) {
        List<Expr> ClockedClauseopt = (List<Expr>) _ClockedClauseopt;
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.Async(pos(), ClockedClauseopt, Statement));
    }
    // Production: AsyncStatement ::= clocked async Statement
    void rule_AsyncStatement1(Object _Statement) {
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.Async(pos(), Statement, true));
    }
    // Production: MethodBody ::= '=' LastExpression ';'
    void rule_MethodBody0(Object _LastExpression) {
        Stmt LastExpression = (Stmt) _LastExpression;
        setResult(nf.Block(pos(), LastExpression));
    }
    // Production: MethodBody ::= '=' Annotationsopt '{' BlockStatementsopt LastExpression '}'
    void rule_MethodBody1(Object _Annotationsopt, Object _BlockStatementsopt, Object _LastExpression) {
        List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) _Annotationsopt;
        List<Stmt> BlockStatementsopt = (List<Stmt>) _BlockStatementsopt;
        Stmt LastExpression = (Stmt) _LastExpression;
        List<Stmt> l = new ArrayList<Stmt>();
        l.addAll(BlockStatementsopt);
        l.add(LastExpression);
        setResult((Block) ((X10Ext) nf.Block(pos(),l).ext()).annotations(Annotationsopt));
    }
    // Production: MethodBody ::= '=' Annotationsopt Block
    void rule_MethodBody2(Object _Annotationsopt, Object _Block) {
        List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) _Annotationsopt;
        Block Block = (Block) _Block;
        setResult((Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos()));
    }
    // Production: MethodBody ::= Annotationsopt Block
    void rule_MethodBody3(Object _Annotationsopt, Object _Block) {
        rule_MethodBody2(_Annotationsopt,_Block);
    }
    // Production: FieldDeclaration ::= Modifiersopt FieldKeyword FieldDeclarators ';'
    void rule_FieldDeclaration0(Object _Modifiersopt, Object _FieldKeyword, Object _FieldDeclarators) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        List<FlagsNode> FieldKeyword = (List<FlagsNode>) _FieldKeyword;
        List<Object[]> FieldDeclarators = (List<Object[]>) _FieldDeclarators;
        List<Node> modifiers = checkFieldModifiers(Modifiersopt);
        FlagsNode fn = extractFlags(modifiers, FieldKeyword);
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        for (Object[] o : FieldDeclarators) {
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
            if (name == null) name = nf.Id(pos, Name.makeFresh());
            TypeNode type = (TypeNode) o[3];
            if (type == null) type = nf.UnknownTypeNode(name.position().markCompilerGenerated());
            Expr init = (Expr) o[4];
            FieldDecl fd = nf.FieldDecl(pos, fn, type, name, init);
            fd = (FieldDecl) ((X10Ext) fd.ext()).annotations(extractAnnotations(modifiers));
            fd = (FieldDecl) ((X10Ext) fd.ext()).setComment(comment(getRhsFirstTokenIndex(1)));
            l.add(fd);
        }
        setResult(l);
    }
    // Production: FieldDeclaration ::= Modifiersopt FieldDeclarators ';'
    void rule_FieldDeclaration1(Object _Modifiersopt, Object _FieldDeclarators) {
        rule_FieldDeclaration0(_Modifiersopt,Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)),_FieldDeclarators);
    }
    // Production: Interfaces ::= implements InterfaceTypeList
    void rule_Interfaces0(Object _InterfaceTypeList) {
        List<TypeNode> InterfaceTypeList = (List<TypeNode>) _InterfaceTypeList;
        setResult(InterfaceTypeList);
    }
    // Production: ShiftExpression ::= ShiftExpression '<<' AdditiveExpression
    void rule_ShiftExpression1(Object _ShiftExpression, Object _AdditiveExpression) {
        Expr ShiftExpression = (Expr) _ShiftExpression;
        Expr AdditiveExpression = (Expr) _AdditiveExpression;
        setResult(nf.Binary(pos(), ShiftExpression, Binary.SHL, AdditiveExpression));
    }
    // Production: ShiftExpression ::= ShiftExpression '>>' AdditiveExpression
    void rule_ShiftExpression2(Object _ShiftExpression, Object _AdditiveExpression) {
        Expr ShiftExpression = (Expr) _ShiftExpression;
        Expr AdditiveExpression = (Expr) _AdditiveExpression;
        setResult(nf.Binary(pos(), ShiftExpression, Binary.SHR, AdditiveExpression));
    }
    // Production: ShiftExpression ::= ShiftExpression '>>>' AdditiveExpression
    void rule_ShiftExpression3(Object _ShiftExpression, Object _AdditiveExpression) {
        Expr ShiftExpression = (Expr) _ShiftExpression;
        Expr AdditiveExpression = (Expr) _AdditiveExpression;
        setResult(nf.Binary(pos(), ShiftExpression, Binary.USHR, AdditiveExpression));
    }
    // Production: IfThenStatement ::= if '(' Expression ')' Statement
    void rule_IfThenStatement0(Object _Expression, Object _Statement) {
        Expr Expression = (Expr) _Expression;
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.If(pos(), Expression, Statement));
    }
    // Production: StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
    void rule_StructDeclaration0(Object _Modifiersopt, Object _Identifier, Object _TypeParamsWithVarianceopt, Object _Propertiesopt, Object _WhereClauseopt, Object _Interfacesopt, Object _ClassBody) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        Id Identifier = (Id) _Identifier;
        List<TypeParamNode> TypeParamsWithVarianceopt = (List<TypeParamNode>) _TypeParamsWithVarianceopt;
        List<PropertyDecl> Propertiesopt = (List<PropertyDecl>) _Propertiesopt;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        List<TypeNode> Interfacesopt = (List<TypeNode>) _Interfacesopt;
        ClassBody ClassBody = (ClassBody) _ClassBody;
        List<Node> modifiers = checkClassModifiers(Modifiersopt);
        checkTypeName(Identifier);
        List<TypeParamNode> TypeParametersopt = TypeParamsWithVarianceopt;
        List<PropertyDecl> props = Propertiesopt;
        DepParameterExpr ci = WhereClauseopt;
        ClassDecl cd = nf.X10ClassDecl(pos(getLeftSpan(), getRightSpan()),
                extractFlags(modifiers, Flags.STRUCT), Identifier,
                TypeParametersopt, props, ci, null, Interfacesopt, ClassBody);
        cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(modifiers));
        setResult(cd);
    }
    // Production: ConstructorBlock ::= '{' ExplicitConstructorInvocationopt BlockStatementsopt '}'
    void rule_ConstructorBlock0(Object _ExplicitConstructorInvocationopt, Object _BlockStatementsopt) {
        Stmt ExplicitConstructorInvocationopt = (Stmt) _ExplicitConstructorInvocationopt;
        List<Stmt> BlockStatementsopt = (List<Stmt>) _BlockStatementsopt;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        if (ExplicitConstructorInvocationopt != null)
        {
            l.add(ExplicitConstructorInvocationopt);
        }
        l.addAll(BlockStatementsopt);
        setResult(nf.Block(pos(), l));
    }
    // Production: InclusiveOrExpression ::= InclusiveOrExpression '|' ExclusiveOrExpression
    void rule_InclusiveOrExpression1(Object _InclusiveOrExpression, Object _ExclusiveOrExpression) {
        Expr InclusiveOrExpression = (Expr) _InclusiveOrExpression;
        Expr ExclusiveOrExpression = (Expr) _ExclusiveOrExpression;
        setResult(nf.Binary(pos(), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression));
    }
    // Production: ResultType ::= ':' Type
    void rule_ResultType0(Object _Type) {
        TypeNode Type = (TypeNode) _Type;
        setResult(Type);
    }
    // Production: HasResultType ::= '<:' Type
    void rule_HasResultType1(Object _Type) {
        TypeNode Type = (TypeNode) _Type;
        setResult(nf.HasType(Type));
    }
    // Production: PropertyList ::= Property
    void rule_PropertyList0(Object _Property) {
        PropertyDecl Property = (PropertyDecl) _Property;
        List<PropertyDecl> l = new TypedList<PropertyDecl>(new LinkedList<PropertyDecl>(), PropertyDecl.class, false);
        l.add(Property);
        setResult(l);
    }
    // Production: PropertyList ::= PropertyList ',' Property
    void rule_PropertyList1(Object _PropertyList, Object _Property) {
        List<PropertyDecl> PropertyList = (List<PropertyDecl>) _PropertyList;
        PropertyDecl Property = (PropertyDecl) _Property;
        PropertyList.add(Property);
    }
    // Production: ConditionalAndExpression ::= ConditionalAndExpression '&&' InclusiveOrExpression
    void rule_ConditionalAndExpression1(Object _ConditionalAndExpression, Object _InclusiveOrExpression) {
        Expr ConditionalAndExpression = (Expr) _ConditionalAndExpression;
        Expr InclusiveOrExpression = (Expr) _InclusiveOrExpression;
        setResult(nf.Binary(pos(), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression));
    }
    // Production: SwitchLabels ::= SwitchLabel
    void rule_SwitchLabels0(Object _SwitchLabel) {
        Case SwitchLabel = (Case) _SwitchLabel;
        List<Case> l = new TypedList<Case>(new LinkedList<Case>(), Case.class, false);
        l.add(SwitchLabel);
        setResult(l);
    }
    // Production: SwitchLabels ::= SwitchLabels SwitchLabel
    void rule_SwitchLabels1(Object _SwitchLabels, Object _SwitchLabel) {
        List<SwitchElement> SwitchLabels = (List<SwitchElement>) _SwitchLabels;
        Case SwitchLabel = (Case) _SwitchLabel;
        SwitchLabels.add(SwitchLabel);
        //setResult(SwitchLabels);
    }
    // Production: ImportDeclarationsopt ::= %Empty
    void rule_ImportDeclarationsopt0() {
        setResult(new TypedList<Import>(new LinkedList<Import>(), Import.class, false));
    }
    // Production: IfThenElseStatement ::= if '(' Expression ')' Statement else Statement
    void rule_IfThenElseStatement0(Object _Expression, Object _s1, Object _s2) {
        Expr Expression = (Expr) _Expression;
        Stmt s1 = (Stmt) _s1;
        Stmt s2 = (Stmt) _s2;
        setResult(nf.If(pos(), Expression, s1, s2));
    }
    // Production: Identifieropt ::= Identifier
    void rule_Identifieropt1(Object _Identifier) {
        Id Identifier = (Id) _Identifier;
        setResult(Identifier);
    }
    // Production: ErrorPrimaryPrefix ::= Primary '.' ErrorId
    void rule_ErrorPrimaryPrefix0(Object _Primary) {
        Expr Primary = (Expr) _Primary;
        Object[] a = new Object[2];
        a[0] = Primary;
        a[1] = id(getRhsFirstTokenIndex(3));
        setResult(a);
    }
    // Production: ErrorSuperPrefix ::= super '.' ErrorId
    void rule_ErrorSuperPrefix0() {
        Object[] a = new Object[2];
        a[0] = pos(getRhsFirstTokenIndex(1));
        a[1] = id(getRhsFirstTokenIndex(3));
        setResult(a);
    }
    // Production: ErrorClassNameSuperPrefix ::= ClassName '.' super '.' ErrorId
    void rule_ErrorClassNameSuperPrefix0(Object _ClassName) {
        ParsedName ClassName = (ParsedName) _ClassName;
        Object[] a = new Object[3];
        a[0] = ClassName;
        a[1] = pos(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(3));
        a[2] = id(getRhsFirstTokenIndex(5));
        setResult(a);
    }
    // Production: AnnotatedType ::= Type Annotations
    void rule_AnnotatedType0(Object _Type, Object _Annotations) {
        TypeNode Type = (TypeNode) _Type;
        List<AnnotationNode> Annotations = (List<AnnotationNode>) _Annotations;
        TypeNode tn = Type;
        tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
        setResult(tn.position(pos()));
    }
    // Production: ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt OBSOLETE_Offersopt ConstructorBody
    void rule_ConstructorDeclaration0(Object _Modifiersopt, Object _TypeParametersopt, Object _FormalParameters, Object _WhereClauseopt, Object _HasResultTypeopt, Object _OBSOLETE_Offersopt, Object _Throwsopt, Object _ConstructorBody) {
        List<Modifier> Modifiersopt = (List<Modifier>) _Modifiersopt;
        List<TypeParamNode> TypeParametersopt = (List<TypeParamNode>) _TypeParametersopt;
        List<Formal> FormalParameters = (List<Formal>) _FormalParameters;
        DepParameterExpr WhereClauseopt = (DepParameterExpr) _WhereClauseopt;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = (TypeNode) _OBSOLETE_Offersopt;
        List<TypeNode> Throwsopt = (List<TypeNode>) _Throwsopt;
        Block ConstructorBody = (Block) _ConstructorBody;
        List<Node> modifiers = checkConstructorModifiers(Modifiersopt);
        ConstructorDecl cd = nf.X10ConstructorDecl(pos(),
                extractFlags(modifiers),
                nf.Id(pos(getRhsFirstTokenIndex(3)), TypeSystem.CONSTRUCTOR_NAME),
                HasResultTypeopt,
                TypeParametersopt,
                FormalParameters,
                WhereClauseopt,
                OBSOLETE_Offersopt,
                Throwsopt,
                ConstructorBody);
        cd = (ConstructorDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(modifiers));
        setResult(cd);
    }
    // Production: PostIncrementExpression ::= PostfixExpression '++'
    void rule_PostIncrementExpression0(Object _PostfixExpression) {
        Expr PostfixExpression = (Expr) _PostfixExpression;
        setResult(nf.Unary(pos(), PostfixExpression, Unary.POST_INC));
    }
    // Production: SwitchBlockStatementGroupsopt ::= %Empty
    void rule_SwitchBlockStatementGroupsopt0() {
        setResult(new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false));
    }
    // Production: Catches ::= CatchClause
    void rule_Catches0(Object _CatchClause) {
        Catch CatchClause = (Catch) _CatchClause;
        List<Catch> l = new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false);
        l.add(CatchClause);
        setResult(l);
    }
    // Production: Catches ::= Catches CatchClause
    void rule_Catches1(Object _Catches, Object _CatchClause) {
        List<Catch> Catches = (List<Catch>) _Catches;
        Catch CatchClause = (Catch) _CatchClause;
        Catches.add(CatchClause);
        //setResult(Catches);
    }
    // Production: CatchClause ::= catch '(' FormalParameter ')' Block
    void rule_CatchClause0(Object _FormalParameter, Object _Block) {
        X10Formal FormalParameter = (X10Formal) _FormalParameter;
        Block Block = (Block) _Block;
        setResult(nf.Catch(pos(), FormalParameter, Block));
    }
    // Production: FieldDeclarators ::= FieldDeclarator
    void rule_FieldDeclarators0(Object _FieldDeclarator) {
        Object[] FieldDeclarator = (Object[]) _FieldDeclarator;
        List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
        l.add(FieldDeclarator);
        setResult(l);
    }
    // Production: FieldDeclarators ::= FieldDeclarators ',' FieldDeclarator
    void rule_FieldDeclarators1(Object _FieldDeclarators, Object _FieldDeclarator) {
        List<Object[]> FieldDeclarators = (List<Object[]>) _FieldDeclarators;
        Object[] FieldDeclarator = (Object[]) _FieldDeclarator;
        FieldDeclarators.add(FieldDeclarator);
        // setResult(FieldDeclarators);
    }
    // Production: FormalParameters ::= '(' FormalParameterListopt ')'
    void rule_FormalParameters0(Object _FormalParameterListopt) {
        List<Formal> FormalParameterListopt = (List<Formal>) _FormalParameterListopt;
        setResult(FormalParameterListopt);
    }
    // Production: ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt '(' ArgumentListopt ')' ClassBodyopt
    void rule_ClassInstanceCreationExpression0(Object _TypeName, Object _TypeArgumentsopt, Object _ArgumentListopt, Object _ClassBodyopt) {
        ParsedName TypeName = (ParsedName) _TypeName;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        ClassBody ClassBodyopt = (ClassBody) _ClassBodyopt;
        if (ClassBodyopt == null)
            setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt));
        else setResult(nf.X10New(pos(), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt)) ;
    }
    // Production: ClassInstanceCreationExpression ::= Primary '.' new Identifier TypeArgumentsopt '(' ArgumentListopt ')' ClassBodyopt
    void rule_ClassInstanceCreationExpression2(Object _Primary, Object _Identifier, Object _TypeArgumentsopt, Object _ArgumentListopt, Object _ClassBodyopt) {
        Expr Primary = (Expr) _Primary;
        Id Identifier = (Id) _Identifier;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        ClassBody ClassBodyopt = (ClassBody) _ClassBodyopt;
        ParsedName b = new ParsedName(nf, ts, pos(), Identifier);
        if (ClassBodyopt == null)
            setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt));
        else setResult(nf.X10New(pos(), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
    }
    // Production: ClassInstanceCreationExpression ::= FullyQualifiedName '.' new Identifier TypeArgumentsopt '(' ArgumentListopt ')' ClassBodyopt
    void rule_ClassInstanceCreationExpression3(Object _FullyQualifiedName, Object _Identifier, Object _TypeArgumentsopt, Object _ArgumentListopt, Object _ClassBodyopt) {
        ParsedName FullyQualifiedName = (ParsedName) _FullyQualifiedName;
        Id Identifier = (Id) _Identifier;
        List<TypeNode> TypeArgumentsopt = (List<TypeNode>) _TypeArgumentsopt;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        ClassBody ClassBodyopt = (ClassBody) _ClassBodyopt;
        ParsedName b = new ParsedName(nf, ts, pos(), Identifier);
        if (ClassBodyopt == null)
            setResult(nf.X10New(pos(), FullyQualifiedName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt));
        else setResult(nf.X10New(pos(), FullyQualifiedName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt));
    }
    // Production: AtExpression ::= at ( Expression ) ClosureBody
    // Production: AtExpression ::= at ( Expression ; * ) ClosureBody
    void rule_AtExpression0(Object _Annotationsopt, Object _Expression, Object _ClosureBody) {
        List<AnnotationNode> Annotationsopt = (List<AnnotationNode>) _Annotationsopt;
        Expr Expression = (Expr) _Expression;
        Block ClosureBody = (Block) _ClosureBody;
        AtExpr r = nf.AtExpr(pos(), Expression, ClosureBody);
        r = (AtExpr) ((X10Ext) r.ext()).annotations(Annotationsopt);
        setResult(r);
    }
    // Production: AtExpression ::= at ( Expression ; AtCaptureDeclaratorsopt ) ClosureBody
    void rule_AtExpression1(Object _Expression, Object _AtCaptureDeclaratorsopt, Object _ClosureBody) {
        Expr Expression = (Expr) _Expression;
        List<Node> AtCaptureDeclaratorsopt = (List<Node>) _AtCaptureDeclaratorsopt;
        Block ClosureBody = (Block) _ClosureBody;
        setResult(nf.AtExpr(pos(), Expression, AtCaptureDeclaratorsopt, ClosureBody));
    }
    // Production: AtExpression ::= athome ( HomeVariableList ) ClosureBody
    // Production: AtExpression ::= athome ( HomeVariableList ; * ) ClosureBody
    void rule_AtExpression2(Object _HomeVariableList, Object _ClosureBody) {
        List<Expr> HomeVariableList = (List<Expr>) _HomeVariableList;
        Block ClosureBody = (Block) _ClosureBody;
        setResult(nf.AtHomeExpr(pos(), HomeVariableList, ClosureBody));
    }
    // Production: AtExpression ::= athome ( HomeVariableList ; AtCaptureDeclaratorsopt ) ClosureBody
    void rule_AtExpression3(Object _HomeVariableList, Object _AtCaptureDeclaratorsopt, Object _ClosureBody) {
        List<Expr> HomeVariableList = (List<Expr>) _HomeVariableList;
        List<Node> AtCaptureDeclaratorsopt = (List<Node>) _AtCaptureDeclaratorsopt;
        Block ClosureBody = (Block) _ClosureBody;
        setResult(nf.AtHomeExpr(pos(), HomeVariableList, AtCaptureDeclaratorsopt, ClosureBody));
    }
    // Production: AtExpression ::= athome ClosureBody
    // Production: AtExpression ::= athome ( * ) ClosureBody
    // Production: AtExpression ::= athome ( * ; * ) ClosureBody
    void rule_AtExpression4(Object _ClosureBody) {
        Block ClosureBody = (Block) _ClosureBody;
        setResult(nf.AtHomeExpr(pos(), null, ClosureBody));
    }
    // Production: AtExpression ::= athome ( * ; AtCaptureDeclaratorsopt ) ClosureBody
    void rule_AtExpression5(Object _AtCaptureDeclaratorsopt, Object _ClosureBody) {
        List<Node> AtCaptureDeclaratorsopt = (List<Node>) _AtCaptureDeclaratorsopt;
        Block ClosureBody = (Block) _ClosureBody;
        setResult(nf.AtHomeExpr(pos(), null, AtCaptureDeclaratorsopt, ClosureBody));
    }
    // Production: CompilationUnit ::= PackageDeclarationopt TypeDeclarationsopt
    void rule_CompilationUnit0(Object _PackageDeclarationopt, Object _TypeDeclarationsopt) {
        PackageNode PackageDeclarationopt = (PackageNode) _PackageDeclarationopt;
        List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) _TypeDeclarationsopt;
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
    }
    // Production: CompilationUnit ::= PackageDeclarationopt ImportDeclarations TypeDeclarationsopt
    void rule_CompilationUnit1(Object _PackageDeclarationopt, Object _ImportDeclarations, Object _TypeDeclarationsopt) {
        PackageNode PackageDeclarationopt = (PackageNode) _PackageDeclarationopt;
        List<Import> ImportDeclarations = (List<Import>) _ImportDeclarations;
        List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) _TypeDeclarationsopt;
        setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                PackageDeclarationopt,
                ImportDeclarations,
                TypeDeclarationsopt));
    }
    // Production: CompilationUnit ::= ImportDeclarations PackageDeclaration ImportDeclarationsopt TypeDeclarationsopt
    void rule_CompilationUnit2(Object _ImportDeclarations, Object _misplacedPackageDeclaration, Object _misplacedImportDeclarations, Object _TypeDeclarationsopt) {
        List<Import> ImportDeclarations = (List<Import>) _ImportDeclarations;
        PackageNode misplacedPackageDeclaration = (PackageNode) _misplacedPackageDeclaration;
        List<Import> misplacedImportDeclarations = (List<Import>) _misplacedImportDeclarations;
        List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) _TypeDeclarationsopt;
        syntaxError("Misplaced package declaration", misplacedPackageDeclaration.position());
        ImportDeclarations.addAll(misplacedImportDeclarations); // merge the two import lists
        setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                misplacedPackageDeclaration,
                ImportDeclarations,
                TypeDeclarationsopt));
    }
    // Production: CompilationUnit ::= PackageDeclaration ImportDeclarations PackageDeclaration ImportDeclarationsopt TypeDeclarationsopt
    void rule_CompilationUnit3(Object _PackageDeclaration, Object _ImportDeclarations, Object _misplacedPackageDeclaration, Object _misplacedImportDeclarations, Object _TypeDeclarationsopt) {
        PackageNode PackageDeclaration = (PackageNode) _PackageDeclaration;
        List<Import> ImportDeclarations = (List<Import>) _ImportDeclarations;
        PackageNode misplacedPackageDeclaration = (PackageNode) _misplacedPackageDeclaration;
        List<Import> misplacedImportDeclarations = (List<Import>) _misplacedImportDeclarations;
        List<TopLevelDecl> TypeDeclarationsopt = (List<TopLevelDecl>) _TypeDeclarationsopt;
        syntaxError("Misplaced package declaration, ignoring", misplacedPackageDeclaration.position());
        ImportDeclarations.addAll(misplacedImportDeclarations); // merge the two import lists
        setResult(nf.SourceFile(pos(getLeftSpan(), getRightSpan()),
                PackageDeclaration,
                ImportDeclarations,
                TypeDeclarationsopt));
    }
    // Production: Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
    void rule_Assignment0(Object _LeftHandSide, Object _AssignmentOperator, Object _AssignmentExpression) {
        Expr LeftHandSide = (Expr) _LeftHandSide;
        Assign.Operator AssignmentOperator = (Assign.Operator) _AssignmentOperator;
        Expr AssignmentExpression = (Expr) _AssignmentExpression;
        setResult(nf.Assign(pos(), LeftHandSide, AssignmentOperator, AssignmentExpression));
    }
    // Production: Assignment ::= ExpressionName '(' ArgumentListopt ')' AssignmentOperator AssignmentExpression
    void rule_Assignment1(Object _e1, Object _ArgumentListopt, Object _AssignmentOperator, Object _AssignmentExpression) {
        ParsedName e1 = (ParsedName) _e1;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        Assign.Operator AssignmentOperator = (Assign.Operator) _AssignmentOperator;
        Expr AssignmentExpression = (Expr) _AssignmentExpression;
        setResult(nf.SettableAssign(pos(), e1.toExpr(), ArgumentListopt, AssignmentOperator, AssignmentExpression));
    }
    // Production: Assignment ::= Primary '(' ArgumentListopt ')' AssignmentOperator AssignmentExpression
    void rule_Assignment2(Object _e1, Object _ArgumentListopt, Object _AssignmentOperator, Object _AssignmentExpression) {
        Expr e1 = (Expr) _e1;
        List<Expr> ArgumentListopt = (List<Expr>) _ArgumentListopt;
        Assign.Operator AssignmentOperator = (Assign.Operator) _AssignmentOperator;
        Expr AssignmentExpression = (Expr) _AssignmentExpression;
        setResult(nf.SettableAssign(pos(), e1, ArgumentListopt, AssignmentOperator, AssignmentExpression));
    }
    // Production: MethodModifiersopt ::= MethodModifiersopt property
    void rule_MethodModifiersopt1(Object _MethodModifiersopt) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        MethodModifiersopt.add(new FlagModifier(pos(getRhsFirstTokenIndex(2)), FlagModifier.PROPERTY));
    }
    // Production: MethodModifiersopt ::= MethodModifiersopt Modifier
    void rule_MethodModifiersopt2(Object _MethodModifiersopt, Object _Modifier) {
        List<Modifier> MethodModifiersopt = (List<Modifier>) _MethodModifiersopt;
        Modifier Modifier = (Modifier) _Modifier;
        MethodModifiersopt.add(Modifier);
    }
    // Production: LastExpression ::= Expression
    void rule_LastExpression0(Object _Expression) {
        Expr Expression = (Expr) _Expression;
        setResult(nf.X10Return(pos(), Expression, true));
    }
    // Production: VarKeyword ::= val
    void rule_VarKeyword0() {
        setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.FINAL)));
    }
    // Production: VarKeyword ::= var
    void rule_VarKeyword1() {
        setResult(Collections.singletonList(nf.FlagsNode(pos(), Flags.NONE)));
    }
    // Production: TypeArgumentsopt ::= %Empty
    void rule_TypeArgumentsopt0() {
        setResult(new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false));
    }
    // Production: Annotationsopt ::= %Empty
    void rule_Annotationsopt0() {
        setResult(new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false));
    }
    // Production: LoopIndexDeclarator ::= Identifier HasResultTypeopt
    void rule_LoopIndexDeclarator0(Object _Identifier, Object _HasResultTypeopt) {
        Id Identifier = (Id) _Identifier;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, HasResultTypeopt, null });
    }
    // Production: LoopIndexDeclarator ::= '[' IdentifierList ']' HasResultTypeopt
    void rule_LoopIndexDeclarator1(Object _IdentifierList, Object _HasResultTypeopt) {
        List<Id> IdentifierList = (List<Id>) _IdentifierList;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        setResult(new Object[] { pos(), null, IdentifierList, null, HasResultTypeopt, null });
    }
    // Production: LoopIndexDeclarator ::= Identifier '[' IdentifierList ']' HasResultTypeopt
    void rule_LoopIndexDeclarator2(Object _Identifier, Object _IdentifierList, Object _HasResultTypeopt) {
        Id Identifier = (Id) _Identifier;
        List<Id> IdentifierList = (List<Id>) _IdentifierList;
        TypeNode HasResultTypeopt = (TypeNode) _HasResultTypeopt;
        setResult(new Object[] { pos(), Identifier, IdentifierList, null, HasResultTypeopt, null });
    }
    // Production: FinishStatement ::= finish Statement
    void rule_FinishStatement0(Object _Statement) {
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.Finish(pos(),  Statement, false));
    }
    // Production: FinishStatement ::= clocked finish Statement
    void rule_FinishStatement1(Object _Statement) {
        Stmt Statement = (Stmt) _Statement;
        setResult(nf.Finish(pos(),  Statement, true));
    }
    // Production: Annotations ::= Annotation
    void rule_Annotations0(Object _Annotation) {
        AnnotationNode Annotation = (AnnotationNode) _Annotation;
        List<AnnotationNode> l = new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false);
        l.add(Annotation);
        setResult(l);
    }
    // Production: Annotations ::= Annotations Annotation
    void rule_Annotations1(Object _Annotations, Object _Annotation) {
        List<AnnotationNode> Annotations = (List<AnnotationNode>) _Annotations;
        AnnotationNode Annotation = (AnnotationNode) _Annotation;
        Annotations.add(Annotation);
    }
    // Production: ImportDeclarations ::= ImportDeclaration
    void rule_ImportDeclarations0(Object _ImportDeclaration) {
        Import ImportDeclaration = (Import) _ImportDeclaration;
        List<Import> l = new TypedList<Import>(new LinkedList<Import>(), Import.class, false);
        l.add(ImportDeclaration);
        setResult(l);
    }
    // Production: ImportDeclarations ::= ImportDeclarations ImportDeclaration
    void rule_ImportDeclarations1(Object _ImportDeclarations, Object _ImportDeclaration) {
        List<Import> ImportDeclarations = (List<Import>) _ImportDeclarations;
        Import ImportDeclaration = (Import) _ImportDeclaration;
        if (ImportDeclaration != null)
            ImportDeclarations.add(ImportDeclaration);
        //setResult(l);
    }
    // Production: TypeParameters ::= '[' TypeParameterList ']'
    void rule_TypeParameters0(Object _TypeParameterList) {
        List<TypeParamNode> TypeParameterList = (List<TypeParamNode>) _TypeParameterList;
        setResult(TypeParameterList);
    }
    // Production: EnhancedForStatement ::= for '(' LoopIndex in Expression ')' Statement
    void rule_EnhancedForStatement0(Object _LoopIndex, Object _Expression, Object _Statement) {
        X10Formal LoopIndex = (X10Formal) _LoopIndex;
        Expr Expression = (Expr) _Expression;
        Stmt Statement = (Stmt) _Statement;
        FlagsNode fn = LoopIndex.flags();
        if (! fn.flags().isFinal()) {
            syntaxError("Enhanced for loop may not have var loop index. " + LoopIndex, LoopIndex.position());
            fn = fn.flags(fn.flags().Final());
            LoopIndex = LoopIndex.flags(fn);
        }
        setResult(nf.ForLoop(pos(),
                LoopIndex,
                Expression,
                Statement));
    }
    // Production: EnhancedForStatement ::= for '(' Expression ')' Statement
    void rule_EnhancedForStatement1(Object _Expression, Object _Statement) {
        Expr Expression = (Expr) _Expression;
        Stmt Statement = (Stmt) _Statement;
        Id name = nf.Id(pos(), Name.makeFresh());
        TypeNode type = nf.UnknownTypeNode(pos().markCompilerGenerated());
        setResult(nf.ForLoop(pos(),
                nf.X10Formal(pos(), nf.FlagsNode(pos(), Flags.FINAL), type, name, null, true),
                Expression,
                Statement));
    }
    // Production: EmptyStatement ::= ';'
    void rule_EmptyStatement0() {
        setResult(nf.Empty(pos()));
    }
    // Production: FormalDeclarator ::= Identifier ResultType
    void rule_FormalDeclarator0(Object _Identifier, Object _ResultType) {
        Id Identifier = (Id) _Identifier;
        TypeNode ResultType = (TypeNode) _ResultType;
        setResult(new Object[] { pos(), Identifier, Collections.<Id>emptyList(), null, ResultType, null });
    }
    // Production: FormalDeclarator ::= '[' IdentifierList ']' ResultType
    void rule_FormalDeclarator1(Object _IdentifierList, Object _ResultType) {
        List<Id> IdentifierList = (List<Id>) _IdentifierList;
        TypeNode ResultType = (TypeNode) _ResultType;
        setResult(new Object[] { pos(), null, IdentifierList, null, ResultType, null });
    }
    // Production: FormalDeclarator ::= Identifier '[' IdentifierList ']' ResultType
    void rule_FormalDeclarator2(Object _Identifier, Object _IdentifierList, Object _ResultType) {
        Id Identifier = (Id) _Identifier;
        List<Id> IdentifierList = (List<Id>) _IdentifierList;
        TypeNode ResultType = (TypeNode) _ResultType;
        setResult(new Object[] { pos(), Identifier, IdentifierList, null, ResultType, null });
    }
    // Production: ForInitopt ::= %Empty
    void rule_ForInitopt0() {
        setResult(new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false));
    }
    // Production: FUTURE_ExistentialList ::= FormalParameter
    void rule_FUTURE_ExistentialList0(Object _FormalParameter) {
        X10Formal FormalParameter = (X10Formal) _FormalParameter;
        List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
        l.add(FormalParameter.flags(nf.FlagsNode(Position.compilerGenerated(FormalParameter.position()), Flags.FINAL)));
        setResult(l);
    }
    // Production: FUTURE_ExistentialList ::= FUTURE_ExistentialList ';' FormalParameter
    void rule_FUTURE_ExistentialList1(Object _FUTURE_ExistentialList, Object _FormalParameter) {
        List<Formal> FUTURE_ExistentialList = (List<Formal>) _FUTURE_ExistentialList;
        X10Formal FormalParameter = (X10Formal) _FormalParameter;
        FUTURE_ExistentialList.add(FormalParameter.flags(nf.FlagsNode(Position.compilerGenerated(FormalParameter.position()), Flags.FINAL)));
    }
    // Production: ClockedClause ::= clocked Arguments
    void rule_ClockedClause0(Object _Arguments) {
        List<Expr> Arguments = (List<Expr>) _Arguments;
        setResult(Arguments);
    }
	public void rule_Throws0(Object throwsList) {
		setResult(throwsList);
	}
	public void rule_ThrowsList0(Object type) {
		List<TypeNode> throwsList = new ArrayList<TypeNode>();
		throwsList.add((TypeNode)type);
		setResult(throwsList);
	}
 	public void rule_ThrowsList1(Object _throwsList, Object type) {
		List<TypeNode> throwsList = (List<TypeNode>)_throwsList;
		throwsList.add((TypeNode)type);
		setResult(throwsList);
	}
	public void rule_Throwsopt0() {
        setResult(Collections.<TypeNode>emptyList());
	}

}

