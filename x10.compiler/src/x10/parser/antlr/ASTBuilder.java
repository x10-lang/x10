package x10.parser.antlr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import javax.swing.JDialog;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import polyglot.ast.AmbExpr;
import polyglot.ast.AmbTypeNode;
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
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.FloatLit;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Import;
import polyglot.ast.IntLit;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
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
import polyglot.ast.IntLit.Kind;
import polyglot.ast.Unary.Operator;
import polyglot.frontend.FileSource;
import polyglot.lex.BooleanLiteral;
import polyglot.lex.CharacterLiteral;
import polyglot.lex.DoubleLiteral;
import polyglot.lex.FloatLiteral;
import polyglot.lex.StringLiteral;
import polyglot.parse.ParsedName;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.X10CompilerOptions;
import x10.ast.AmbMacroTypeNode;
import x10.ast.AnnotationNode;
import x10.ast.AtExpr;
import x10.ast.ClosureCall;
import x10.ast.DepParameterExpr;
import x10.ast.PropertyDecl;
import x10.ast.SettableAssign;
import x10.ast.Tuple;
import x10.ast.TypeDecl;
import x10.ast.TypeParamNode;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10Formal;
import x10.ast.X10Unary_c;
import x10.extension.X10Ext;
import x10.parser.X10Parsersym;
import x10.parserGen.*;
import x10.parserGen.X10Parser.*;
import x10.types.ParameterType;
import x10.types.checker.Converter;

public class ASTBuilder extends X10BaseListener implements X10Listener, polyglot.frontend.Parser {

    protected X10Parser p;
    protected X10Lexer lexer;
    CommonTokenStream tokens;

    protected X10CompilerOptions compilerOpts;
    protected ErrorQueue eq;
    protected ParserErrorListener err;
    protected DefaultErrorStrategy errorStrategy;
    protected TypeSystem ts;
    protected NodeFactory nf;
    protected FileSource srce;

    public ASTBuilder(X10CompilerOptions opts, TypeSystem t, NodeFactory n, FileSource source, ErrorQueue q) {
        compilerOpts = opts;
        ts = t;
        nf = n;
        srce = source;
        eq = q;

        String fileName = source.path();
        ANTLRInputStream input;
        try {
            input = new ANTLRInputStream(new FileInputStream(fileName));
            lexer = new X10Lexer(input);
            tokens = new CommonTokenStream(lexer);
            p = new X10Parser(tokens);
            p.removeErrorListeners();
            err = new ParserErrorListener(eq, fileName);
            errorStrategy = new DefaultErrorStrategy();
            p.addErrorListener(err);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (IOException e) {
            input = null;
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CompilationUnitContext getParseTree() {
        // Two stage parsing
        CompilationUnitContext tree = null;
        p.getInterpreter().setPredictionMode(PredictionMode.SLL);
        p.removeErrorListeners();
        p.setErrorHandler(new BailErrorStrategy());
        try {
            // First stage
            tree = p.compilationUnit();
        } catch (Exception ex) {
            // Second stage
            tokens.reset();
            p.reset();
            p.addErrorListener(err);
            p.setErrorHandler(errorStrategy);
            p.getInterpreter().setPredictionMode(PredictionMode.LL);
            tree = p.compilationUnit();
        }
        return tree;
    }

    @Override
    public Node parse() {
        CompilationUnitContext tree = getParseTree();
        if (compilerOpts.x10_config.DISPLAY_PARSE_TREE) {
            Future<JDialog> dialogHdl = tree.inspect(p);
            try {
                JDialog dialog = dialogHdl.get();
                dialog.setTitle(srce.toString());
                Utils.waitForClose(dialog);
            } catch (Exception e) {
                eq.enqueue(ErrorInfo.WARNING, srce + ": unable to display the parse tree.");
            }
        }
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, tree);
        SourceFile sf = tree.ast;
        return sf.source(srce);
    }

    // Utility functions

    /** Returns the position of a given parse tree node. */
    protected Position pos(ParserRuleContext ctx) {
        if (ctx.getStop() == null) {
            return new Position(null, srce.path(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        } else {
            return new Position(null, srce.path(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
        }
    }

    /** Returns the position of a given token. */
    private Position pos(Token t) {
        return new Position(null, srce.path(), t.getLine(), t.getCharPositionInLine());
    }

    private String comment(Position pos) {
        // IToken[] adjuncts = prsStream.getTokenAt(i).getPrecedingAdjuncts();
        // String s = null;
        // for (IToken a : adjuncts) {
        // String c = a.toString();
        // if (c.startsWith("/**") && c.endsWith("*/")) {
        // s = c;
        // }
        // }
        // return s;
        return null;
    }

    private void checkTypeName(Id identifier) {
        String filename = srce.name();
        String idname = identifier.id().toString();
        int dot = filename.lastIndexOf('.'), slash = filename.lastIndexOf('/', dot);
        if (slash == -1)
            slash = filename.lastIndexOf('\\', dot);
        String clean_filename = (slash >= 0 && dot >= 0 ? filename.substring(slash + 1, dot) : "");
        if ((!clean_filename.equals(idname)) && clean_filename.equalsIgnoreCase(idname))
            err.syntaxError("This type name does not match the name of the containing file: " + filename.substring(slash + 1), identifier.position());
    }


    // Temporary classes used to wrap modifiers.

    public static class Modifier {
    }

    public static class FlagModifier extends Modifier {
        public static int ABSTRACT = 0;
        public static int ATOMIC = 1;
        // public static int EXTERN = 2;
        public static int FINAL = 3;
        // public static int GLOBAL = 4;
        // public static int INCOMPLETE = 5;
        public static int NATIVE = 6;
        // public static int NON_BLOCKING = 7;
        public static int PRIVATE = 8;
        public static int PROPERTY = 9;
        public static int PROTECTED = 10;
        public static int PUBLIC = 11;
        // public static int SAFE = 12;
        // public static int SEQUENTIAL = 13;
        public static int CLOCKED = 14;
        public static int STATIC = 15;
        public static int TRANSIENT = 16;
        public static int NUM_FLAGS = TRANSIENT + 1;

        private Position pos;
        private int flag;

        public Position position() {
            return pos;
        }

        public int flag() {
            return flag;
        }

        public Flags flags() {
            if (flag == ABSTRACT)
                return Flags.ABSTRACT;
            if (flag == ATOMIC)
                return Flags.ATOMIC;
            // if (flag == EXTERN) return X10Flags.EXTERN;
            if (flag == FINAL)
                return Flags.FINAL;
            // if (flag == GLOBAL) return X10Flags.GLOBAL;
            // if (flag == INCOMPLETE) return X10Flags.INCOMPLETE;
            if (flag == NATIVE)
                return Flags.NATIVE;
            // if (flag == NON_BLOCKING) return X10Flags.NON_BLOCKING;
            if (flag == PRIVATE)
                return Flags.PRIVATE;
            if (flag == PROPERTY)
                return Flags.PROPERTY;
            if (flag == PROTECTED)
                return Flags.PROTECTED;
            if (flag == PUBLIC)
                return Flags.PUBLIC;
            // if (flag == SAFE) return X10Flags.SAFE;
            // if (flag == SEQUENTIAL) return X10Flags.SEQUENTIAL;
            if (flag == CLOCKED)
                return Flags.CLOCKED;
            if (flag == TRANSIENT)
                return Flags.TRANSIENT;
            if (flag == STATIC)
                return Flags.STATIC;
            assert (false);
            return null;
        }

        public String name() {
            if (flag == ABSTRACT)
                return "abstract";
            if (flag == ATOMIC)
                return "atomic";
            // if (flag == EXTERN) return "extern";
            if (flag == FINAL)
                return "final";
            // if (flag == GLOBAL) return "global";
            // if (flag == INCOMPLETE) return "incomplete";
            if (flag == NATIVE)
                return "native";
            // if (flag == NON_BLOCKING) return "nonblocking";
            if (flag == PRIVATE)
                return "private";
            if (flag == PROPERTY)
                return "property";
            if (flag == PROTECTED)
                return "protected";
            if (flag == PUBLIC)
                return "public";
            // if (flag == SAFE) return "safe";
            // if (flag == SEQUENTIAL) return "sequential";
            if (flag == CLOCKED)
                return "clocked";
            if (flag == STATIC)
                return "static";
            if (flag == TRANSIENT)
                return "transient";
            assert (false);
            return "?";
        }

        public static boolean classModifiers[] = new boolean[NUM_FLAGS];
        static {
            classModifiers[ABSTRACT] = true;
            classModifiers[FINAL] = true;
            classModifiers[PRIVATE] = true;
            classModifiers[PROTECTED] = true;
            classModifiers[PUBLIC] = true;
            // classModifiers[SAFE] = true;
            classModifiers[STATIC] = true;
            classModifiers[CLOCKED] = true;
            // classModifiers[GLOBAL] = true;
        }

        public boolean isClassModifier(int flag) {
            return classModifiers[flag];
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
            // methodModifiers[INCOMPLETE] = true;
            methodModifiers[NATIVE] = true;
            // methodModifiers[NON_BLOCKING] = true;
            methodModifiers[PRIVATE] = true;
            methodModifiers[PROPERTY] = true;
            methodModifiers[PROTECTED] = true;
            methodModifiers[PUBLIC] = true;
            // methodModifiers[SAFE] = true;
            // methodModifiers[SEQUENTIAL] = true;
            methodModifiers[STATIC] = true;
            // methodModifiers[CLOCKED] = true;
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

        public FlagModifier(Position pos, int flag) {
            this.pos = pos;
            this.flag = flag;
        }
    }

    private static class AnnotationModifier extends Modifier {
        private AnnotationNode annotation;

        public AnnotationNode annotation() {
            return annotation;
        }

        public AnnotationModifier(AnnotationNode annotation) {
            this.annotation = annotation;
        }
    }

    //
    // TODO: Say something!
    //
    private List<Node> checkModifiers(String kind, List<Modifier> modifiers, boolean legal_flags[]) {
        List<Node> l = new LinkedList<Node>();

        assert (modifiers.size() > 0);

        boolean flags[] = new boolean[FlagModifier.NUM_FLAGS]; // initialized to
                                                               // false
        for (int i = 0; i < modifiers.size(); i++) {
            Object element = modifiers.get(i);
            if (element instanceof FlagModifier) {
                FlagModifier modifier = (FlagModifier) element;
                l.addAll(Collections.singletonList(nf.FlagsNode(modifier.position(), modifier.flags())));

                if (!flags[modifier.flag()]) {
                    flags[modifier.flag()] = true;
                } else {
                    err.syntaxError("Duplicate specification of modifier: " + modifier.name(), modifier.position());
                }

                if (!legal_flags[modifier.flag()]) {
                    err.syntaxError("\"" + modifier.name() + "\" is not a valid " + kind + " modifier", modifier.position());
                }
            } else {
                AnnotationModifier modifier = (AnnotationModifier) element;
                l.addAll(Collections.singletonList(modifier.annotation()));
            }
        }

        return l;
    }

    private List<Node> checkClassModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, Flags.NONE)) : checkModifiers("class", modifiers,
                FlagModifier.classModifiers));
    }

    private List<Node> checkTypeDefModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> singletonList(nf.FlagsNode(Position.COMPILER_GENERATED, Flags.NONE)) : checkModifiers("typedef", modifiers,
                FlagModifier.typeDefModifiers));
    }

    private List<Node> checkFieldModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> emptyList() : checkModifiers("field", modifiers, FlagModifier.fieldModifiers));
    }

    private List<Node> checkVariableModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> emptyList() : checkModifiers("variable", modifiers, FlagModifier.variableModifiers));
    }

    private List<Node> checkMethodModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> emptyList() : checkModifiers("method", modifiers, FlagModifier.methodModifiers));
    }

    private List<Node> checkConstructorModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> emptyList() : checkModifiers("constructor", modifiers, FlagModifier.constructorModifiers));
    }

    private List<Node> checkInterfaceModifiers(List<Modifier> modifiers) {
        return (modifiers.size() == 0 ? Collections.<Node> emptyList() : checkModifiers("interface", modifiers, FlagModifier.interfaceModifiers));
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
                pos = pos == null ? fn.position() : new Position(pos, fn.position());
                Flags f = fn.flags();
                xf = xf.set(f);
            }
        }
        return nf.FlagsNode(pos == null ? Position.COMPILER_GENERATED : pos, xf);
    }


    // Lexer utility functions

    private long parseLong(String s, int radix, Position pos) {
        long x = 0L;

        s = s.toLowerCase();

        boolean reportedError = false;
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i);

            if (c < '0' || c > '9') {
                c = c - 'a' + 10;
            } else {
                c = c - '0';
            }

            if (c >= radix) {
                if (!reportedError) {
                    err.syntaxError("Invalid digit: '" + s.charAt(i) + "'", pos);
                    reportedError = true;
                }
            }

            x *= radix;
            x += c;
        }

        return x;
    }

    private long parseLong(String s, Position pos) {
        int radix;
        int start_index;
        int end_index;

        end_index = s.length();

        boolean isUnsigned = false;
        boolean isLong = true;
        long min = Long.MIN_VALUE;
        while (end_index > 0) {
            char lastCh = s.charAt(end_index - 1);
            if (lastCh == 'u' || lastCh == 'U')
                isUnsigned = true;
            // todo: long need special treatment cause we have overflows
            // for signed values that start with 0, we need to make them negative if they are above max value
            if (lastCh == 'n' || lastCh == 'N') {
                isLong = false;
                min = Integer.MIN_VALUE;
            }
            if (lastCh == 'y' || lastCh == 'Y') {
                isLong = false;
                min = Byte.MIN_VALUE;
            }
            if (lastCh == 's' || lastCh == 'S') {
                isLong = false;
                min = Short.MIN_VALUE;
            }
            if (lastCh != 'y' && lastCh != 'Y' && lastCh != 's' && lastCh != 'S' && lastCh != 'l' && lastCh != 'L' && lastCh != 'n' && lastCh != 'N' && lastCh != 'u'
                    && lastCh != 'U') {
                break;
            }
            end_index--;
        }
        long max = -min;

        if (s.charAt(0) == '0') {
            if (s.length() > 1 && (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
                radix = 16;
                start_index = 2;
            } else {
                radix = 8;
                start_index = 0;
            }
        }
 else {
            radix = 10;
            start_index = 0;
        }

        final long res = parseLong(s.substring(start_index, end_index), radix, pos);
        if (!isUnsigned && !isLong && radix != 10 && res >= max) {
            // need to make this value negative
            // e.g., 0xffUY == 255, 0xffY== 255-256 = -1 , 0xfeYU==254, 0xfeY== 254-256 = -2
            return res + min * 2;
        }
        return res;
    }

    private polyglot.lex.FloatLiteral float_lit(LiteralContext ctx) {
        String s = ctx.getText();
        try {
            int end_index = (s.charAt(s.length() - 1) == 'f' || s.charAt(s.length() - 1) == 'F' ? s.length() - 1 : s.length());
            float x = Float.parseFloat(s.substring(0, end_index));
            return new FloatLiteral(pos(ctx), x, X10Parsersym.TK_FloatingPointLiteral); // TODO: check this!!
        }
 catch (NumberFormatException e) {
            // unrecoverableSyntaxError = true;
            eq.enqueue(ErrorInfo.LEXICAL_ERROR, "Illegal float literal \"" + s + "\"", pos(ctx));
            return null;
        }
    }

    private polyglot.lex.DoubleLiteral double_lit(LiteralContext ctx) {
        String s = ctx.getText();
        try {
            int end_index = (s.charAt(s.length() - 1) == 'd' || s.charAt(s.length() - 1) == 'D' ? s.length() - 1 : s.length());
            double x = Double.parseDouble(s.substring(0, end_index));
            return new DoubleLiteral(pos(ctx), x, X10Parsersym.TK_DoubleLiteral); // TODO: Check this!!
        }
 catch (NumberFormatException e) {
            // unrecoverableSyntaxError = true;
            eq.enqueue(ErrorInfo.LEXICAL_ERROR, "Illegal float literal \"" + s + "\"", pos(ctx));
            return null;
        }
    }

    private polyglot.lex.BooleanLiteral boolean_lit(BooleanLiteralContext ctx) {
        return new BooleanLiteral(pos(ctx), ctx.start.getType() == X10Lexer.TRUE, ctx.start.getType());
    }

    private polyglot.lex.CharacterLiteral char_lit(LiteralContext ctx) {
        char x;
        String s = ctx.getText();
        if (s.charAt(1) == '\\') {
            switch (s.charAt(2)) {
            case 'u':
                x = (char) parseLong(s.substring(3, s.length() - 1), 16, pos(ctx));
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
                x = (char) parseLong(s.substring(2, s.length() - 1), 8, pos(ctx));
                if (x > 255) {
                    // unrecoverableSyntaxError = true;
                    eq.enqueue(ErrorInfo.LEXICAL_ERROR, "Illegal character literal " + s, pos(ctx));
                }
            }
        } else {
            assert (s.length() == 3);
            x = s.charAt(1);
        }

        return new CharacterLiteral(pos(ctx), x, X10Parsersym.TK_CharacterLiteral);
    }

    private polyglot.lex.StringLiteral string_lit(LiteralContext ctx) {
        String s = ctx.getText();
        char x[] = new char[s.length()];
        int j = 1, k = 0;
        while (j < s.length() - 1) {
            if (s.charAt(j) != '\\')
                x[k++] = s.charAt(j++);
            else {
                switch (s.charAt(j + 1)) {
                case 'u':
                    x[k++] = (char) parseLong(s.substring(j + 2, j + 6), 16, pos(ctx));
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
                default: {
                    int n = j + 1;
                    for (int l = 0; l < 3 && Character.isDigit(s.charAt(n)); l++)
                        n++;
                    char c = (char) parseLong(s.substring(j + 1, n), 8, pos(ctx));
                    if (c > 255) {
                        // unrecoverableSyntaxError = true;
                        eq.enqueue(ErrorInfo.LEXICAL_ERROR, "Illegal character (" + s.substring(j, n) + ") in string literal " + s, pos(ctx));
                    }
                    x[k++] = c;
                    j = n;
                }
                }
            }
            }

        return new StringLiteral(pos(ctx), new String(x, 0, k), X10Parsersym.TK_StringLiteral);
        }

    private IntLit getIntLit(LiteralContext ctx, Kind k) {
        return nf.IntLit(pos(ctx), k, parseLong(ctx.getText(), pos(ctx)));
    }


    // Grammar actions

    @Override
    public void exitCompilationUnit(CompilationUnitContext ctx) {
        List<Import> importDeclarationsopt = ctx.importDeclarationsopt().ast == null ? new TypedList<Import>(new LinkedList<Import>(), Import.class, false) : ctx
                .importDeclarationsopt().ast;
        List<TopLevelDecl> typeDeclarationsopt = ctx.typeDeclarationsopt().ast == null ? new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false)
                : ctx.typeDeclarationsopt().ast;

        PackageNode packageDeclaration = ctx.packageDeclaration() == null ? null : ctx.packageDeclaration().ast;
        ctx.ast = nf.SourceFile(pos(ctx), packageDeclaration, importDeclarationsopt, typeDeclarationsopt);

    }

    @Override
    public void exitImportDeclarationsopt(ImportDeclarationsoptContext ctx) {
        List<Import> l = new TypedList<Import>(new LinkedList<Import>(), Import.class, false);
        for (ImportDeclarationContext importDeclaration : ctx.importDeclaration()) {
            l.add(importDeclaration.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitPackageDeclaration(PackageDeclarationContext ctx) {
        List<AnnotationNode> Annotationsopt = ctx.annotationsopt().ast;
        ParsedName PackageName = ctx.packageName().ast;
        PackageNode pn = PackageName.toPackage();
        pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
        ctx.ast = pn;
    }

    @Override
    public void exitModifiersopt(ModifiersoptContext ctx) {
        List<Modifier> l = new LinkedList<Modifier>();
        for (ModifierContext m : ctx.modifier()) {
            l.add(m.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitModifierAnnotation(ModifierAnnotationContext ctx) {
        ctx.ast = new AnnotationModifier(ctx.annotation().ast);
    }

    @Override
    public void exitModifierPrivate(ModifierPrivateContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.PRIVATE);
    }

    @Override
    public void exitModifierNative(ModifierNativeContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.NATIVE);
    }

    @Override
    public void exitModifierTransient(ModifierTransientContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.TRANSIENT);
    }

    @Override
    public void exitModifierClocked(ModifierClockedContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.CLOCKED);
    }

    @Override
    public void exitModifierFinal(ModifierFinalContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.FINAL);
    }

    @Override
    public void exitModifierAbstract(ModifierAbstractContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.ABSTRACT);
    }

    @Override
    public void exitModifierAtomic(ModifierAtomicContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.ATOMIC);
    }

    @Override
    public void exitModifierStatic(ModifierStaticContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.STATIC);
    }

    @Override
    public void exitModifierProtected(ModifierProtectedContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.PROTECTED);
    }

    @Override
    public void exitModifierPublic(ModifierPublicContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.PUBLIC);
    }

    @Override
    public void exitMethodModifiersopt(MethodModifiersoptContext ctx) {
        List<Modifier> l = new LinkedList<Modifier>();
        for (MethodModifierContext m : ctx.methodModifier()) {
            l.add(m.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitMethodModifierModifier(MethodModifierModifierContext ctx) {
        ctx.ast = ctx.modifier().ast;
    }

    @Override
    public void exitMethodModifierProperty(MethodModifierPropertyContext ctx) {
        ctx.ast = new FlagModifier(pos(ctx), FlagModifier.PROPERTY);
    }

    @Override
    public void exitTypeDefDeclaration(TypeDefDeclarationContext ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        Id Identifier = ctx.identifier().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        List<Formal> FormalParameterList = ctx.formalParameterList() == null ? new ArrayList<Formal>() : ctx.formalParameterList().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode Type = ctx.type().ast;
        List<Node> modifiers = checkTypeDefModifiers(Modifiersopt);
        FlagsNode f = extractFlags(modifiers);
        List<AnnotationNode> annotations = extractAnnotations(modifiers);
        List<Formal> formals = new ArrayList<Formal>();
        for (Formal v : FormalParameterList) {
            FlagsNode flags = v.flags();
            if (!flags.flags().isFinal()) {
                err.syntaxError("Type definition parameters must be final.", v.position());
                v = v.flags(flags.flags(flags.flags().Final()));
            }
            formals.add(v);
        }
        TypeDecl cd = nf.TypeDecl(pos(ctx), f, Identifier, TypeParametersopt, formals, WhereClauseopt, Type);
        cd = (TypeDecl) ((X10Ext) cd.ext()).annotations(annotations);
        ctx.ast = cd;
    }

    @Override
    public void exitPropertiesopt(PropertiesoptContext ctx) {
        List<PropertyDecl> l = new TypedList<PropertyDecl>(new LinkedList<PropertyDecl>(), PropertyDecl.class, false);
        for (PropertyContext e : ctx.property()) {
            l.add(e.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitProperty(PropertyContext ctx) {
        List<AnnotationNode> Annotationsopt = ctx.annotationsopt().ast;
        Id Identifier = ctx.identifier().ast;
        TypeNode ResultType = ctx.resultType().ast;
        List<AnnotationNode> annotations = extractAnnotations(Annotationsopt);
        PropertyDecl cd = nf.PropertyDecl(pos(ctx), nf.FlagsNode(pos(ctx), Flags.PUBLIC.Final()), ResultType, Identifier);
        cd = (PropertyDecl) ((X10Ext) cd.ext()).annotations(annotations);
        ctx.ast = cd;
    }

    @Override
    public void exitMethodDeclarationSetOp(MethodDeclarationSetOpContext ctx) {
        ctx.ast = ctx.setOperatorDeclaration().ast;
    }

    @Override
    public void exitMethodDeclarationConversionOp(MethodDeclarationConversionOpContext ctx) {
        ctx.ast = ctx.conversionOperatorDeclaration().ast;
    }

    @Override
    public void exitMethodDeclarationBinaryOp(MethodDeclarationBinaryOpContext ctx) {
        ctx.ast = ctx.binaryOperatorDeclaration().ast;
    }

    @Override
    public void exitMethodDeclarationApplyOp(MethodDeclarationApplyOpContext ctx) {
        ctx.ast = ctx.applyOperatorDeclaration().ast;
    }

    @Override
    public void exitMethodDeclarationMethod(MethodDeclarationMethodContext ctx) {
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        Id Identifier = ctx.identifier().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        List<Formal> FormalParameters = ctx.formalParameters().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        List<TypeNode> Throwsopt = ctx.throwsopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        ProcedureDecl pd = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                Identifier, TypeParametersopt, FormalParameters, WhereClauseopt, OBSOLETE_Offersopt, Throwsopt, MethodBody);
        pd = (ProcedureDecl) ((X10Ext) pd.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = pd;
    }

    @Override
    public void exitMethodDeclarationPrefixOp(MethodDeclarationPrefixOpContext ctx) {
        ctx.ast = ctx.prefixOperatorDeclaration().ast;
    }

    @Override
    public void exitBinaryOperatorDecl(BinaryOperatorDeclContext ctx) {
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        X10Formal fp1 = ctx.fp1.ast;
        Binary.Operator BinOp = ctx.binOp().ast;
        X10Formal fp2 = ctx.fp2.ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        List<TypeNode> throwsopt = ctx.throwsopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot override binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx.binOp()), opName), TypeParametersopt, Arrays.<Formal> asList(fp1, fp2), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (!flags.flags().isStatic()) {
            err.syntaxError("Binary operator with two parameters must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = md;
    }

    @Override
    public void exitBinaryOperatorDeclThisLeft(BinaryOperatorDeclThisLeftContext ctx) {
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        Binary.Operator BinOp = ctx.binOp().ast;
        X10Formal fp2 = ctx.fp2.ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        List<TypeNode> throwsopt = ctx.throwsopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot override binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx.binOp()), opName), TypeParametersopt, Collections.<Formal> singletonList(fp2), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            err.syntaxError("Binary operator with this parameter cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = md;
    }

    @Override
    public void exitBinaryOperatorDeclThisRight(BinaryOperatorDeclThisRightContext ctx) {
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        X10Formal fp1 = ctx.fp1.ast;
        Binary.Operator BinOp = ctx.binOp().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        List<TypeNode> throwsopt = ctx.throwsopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            // [DC] doesn't look like this can ever happen?
            err.syntaxError("Cannot override binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx.binOp()), opName), TypeParametersopt, Collections.<Formal> singletonList(fp1), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            err.syntaxError("Binary operator with this parameter cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = md;
    }

    @Override
    public void exitPrefixOperatorDecl(PrefixOperatorDeclContext ctx) {
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        Unary.Operator PrefixOp = ctx.prefixOp().ast;
        X10Formal fp2 = ctx.formalParameter().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        List<TypeNode> throwsopt = ctx.throwsopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Unary_c.unaryMethodName(PrefixOp);
        if (opName == null) {
            err.syntaxError("Cannot override unary operator '" + PrefixOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx.prefixOp()), opName), TypeParametersopt, Collections.<Formal> singletonList(fp2), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (!flags.flags().isStatic()) {
            err.syntaxError("Unary operator with one parameter must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = md;
    }

    @Override
    public void exitPrefixOperatorDeclThis(PrefixOperatorDeclThisContext ctx) {
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        Unary.Operator PrefixOp = ctx.prefixOp().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        List<TypeNode> throwsopt = ctx.throwsopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Name opName = X10Unary_c.unaryMethodName(PrefixOp);
        if (opName == null) {
            err.syntaxError("Cannot override unary operator '" + PrefixOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx.prefixOp()), opName), TypeParametersopt, Collections.<Formal> emptyList(), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            err.syntaxError("Unary operator with this parameter cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = md;
    }

    @Override
    public void exitApplyOperatorDeclaration(ApplyOperatorDeclarationContext ctx) {
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        List<Formal> FormalParameters = ctx.formalParameters().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        List<TypeNode> throwsopt = ctx.throwsopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx), ClosureCall.APPLY), TypeParametersopt, FormalParameters, WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            err.syntaxError("operator() cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = md;
    }

    @Override
    public void exitSetOperatorDeclaration(SetOperatorDeclarationContext ctx) {
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        List<Formal> FormalParameters = ctx.formalParameters().ast;
        X10Formal fp2 = ctx.formalParameter().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        List<TypeNode> throwsopt = ctx.throwsopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx), SettableAssign.SET), TypeParametersopt, CollectionUtil.append(FormalParameters, Collections.singletonList(fp2)), WhereClauseopt,
                OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (flags.flags().isStatic()) {
            err.syntaxError("Set operator cannot be static.", md.position());
            md = md.flags(flags.flags(flags.flags().clearStatic()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = md;
    }

    @Override
    public void exitConversionOperatorDeclarationExplicit(ConversionOperatorDeclarationExplicitContext ctx) {
        ctx.ast = ctx.explicitConversionOperatorDeclaration().ast;
    }

    @Override
    public void exitConversionOperatorDeclarationImplicit(ConversionOperatorDeclarationImplicitContext ctx) {
        ctx.ast = ctx.implicitConversionOperatorDeclaration().ast;
    }

    @Override
    public void exitExplicitConversionOperatorDecl0(ExplicitConversionOperatorDecl0Context ctx) {
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        X10Formal fp1 = ctx.formalParameter().ast;
        TypeNode Type = ctx.type().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        List<TypeNode> throwsopt = ctx.throwsopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), Type, nf.Id(pos(ctx), Converter.operator_as), TypeParametersopt,
                Collections.<Formal> singletonList(fp1), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (!flags.flags().isStatic()) {
            err.syntaxError("Conversion operator must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = md;
    }

    @Override
    public void exitExplicitConversionOperatorDecl1(ExplicitConversionOperatorDecl1Context ctx) {
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        X10Formal fp1 = ctx.formalParameter().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        List<TypeNode> throwsopt = ctx.throwsopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx), Converter.operator_as), TypeParametersopt, Collections.<Formal> singletonList(fp1), WhereClauseopt, OBSOLETE_Offersopt, throwsopt, MethodBody);
        FlagsNode flags = md.flags();
        if (!flags.flags().isStatic()) {
            err.syntaxError("Conversion operator must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = md;
    }

    @Override
    public void exitImplicitConversionOperatorDeclaration(ImplicitConversionOperatorDeclarationContext ctx) {
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        X10Formal fp1 = ctx.formalParameter().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        List<TypeNode> throwsopt = ctx.throwsopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        Position bodyStart = MethodBody == null ? pos(ctx).endOf() : MethodBody.position().startOf();
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers), HasResultTypeopt == null ? nf.UnknownTypeNode(bodyStart.markCompilerGenerated()) : HasResultTypeopt,
                nf.Id(pos(ctx), Converter.implicit_operator_as), TypeParametersopt, Collections.<Formal> singletonList(fp1), WhereClauseopt, OBSOLETE_Offersopt, throwsopt,
                MethodBody);
        FlagsNode flags = md.flags();
        if (!flags.flags().isStatic()) {
            err.syntaxError("Conversion operator must be static.", md.position());
            md = md.flags(flags.flags(flags.flags().Static()));
        }
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = md;
    }

    @Override
    public void exitPropertyMethodDecl0(PropertyMethodDecl0Context ctx) {
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        Id Identifier = ctx.identifier().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        List<Formal> FormalParameters = ctx.formalParameters().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers, Flags.PROPERTY), HasResultTypeopt == null ? nf.UnknownTypeNode(pos(ctx).markCompilerGenerated())
                : HasResultTypeopt, Identifier, TypeParametersopt, FormalParameters, WhereClauseopt, null, // offersOpt
                Collections.<TypeNode> emptyList(), MethodBody);
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = md;
    }

    @Override
    public void exitPropertyMethodDecl1(PropertyMethodDecl1Context ctx) {
        err.syntaxError("This syntax is no longer supported. You must supply the property method formals, and if there are none, you can use an empty parenthesis '()'.", pos(ctx));
        List<Modifier> MethodModifiersopt = ctx.methodModifiersopt().ast;
        Id Identifier = ctx.identifier().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        Block MethodBody = ctx.methodBody().ast;
        List<Node> modifiers = checkMethodModifiers(MethodModifiersopt);
        MethodDecl md = nf.X10MethodDecl(pos(ctx), extractFlags(modifiers, Flags.PROPERTY), HasResultTypeopt == null ? nf.UnknownTypeNode(pos(ctx).markCompilerGenerated())
                : HasResultTypeopt, Identifier, Collections.<TypeParamNode> emptyList(), Collections.<Formal> emptyList(), WhereClauseopt, null, // offersOpt
                Collections.<TypeNode> emptyList(), MethodBody);
        md = (MethodDecl) ((X10Ext) md.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = md;
    }

    @Override
    public void exitExplicitConstructorInvocationThis(ExplicitConstructorInvocationThisContext ctx) {
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = nf.X10ThisCall(pos(ctx), TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitExplicitConstructorInvocationPrimaryThis(ExplicitConstructorInvocationPrimaryThisContext ctx) {
        Expr Primary = ctx.primary().ast;
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = nf.X10ThisCall(pos(ctx), Primary, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitExplicitConstructorInvocationSuper(ExplicitConstructorInvocationSuperContext ctx) {
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = nf.X10SuperCall(pos(ctx), TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitExplicitConstructorInvocationPrimarySuper(ExplicitConstructorInvocationPrimarySuperContext ctx) {
        Expr Primary = ctx.primary().ast;
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = nf.X10SuperCall(pos(ctx), Primary, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitInterfaceDeclaration(InterfaceDeclarationContext ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        Id Identifier = ctx.identifier().ast;
        List<TypeParamNode> TypeParamsWithVarianceopt = ctx.typeParamsWithVarianceopt().ast;
        List<PropertyDecl> Propertiesopt = ctx.propertiesopt().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        List<TypeNode> ExtendsInterfacesopt = ctx.extendsInterfacesopt().ast;
        ClassBody InterfaceBody = ctx.interfaceBody().ast;
        List<Node> modifiers = checkInterfaceModifiers(Modifiersopt);
        checkTypeName(Identifier);
        List<TypeParamNode> TypeParametersopt = TypeParamsWithVarianceopt;
        List<PropertyDecl> props = Propertiesopt;
        // we use the property syntax for annotation-interfaces:
        // public interface Pragma(pragma:Int) extends StatementAnnotation { ...
        // }
        DepParameterExpr ci = WhereClauseopt;
        FlagsNode fn = extractFlags(modifiers, Flags.INTERFACE);
        ClassDecl cd = nf.X10ClassDecl(pos(ctx), fn, Identifier, TypeParametersopt, props, ci, null, ExtendsInterfacesopt, InterfaceBody);
        cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = cd;
    }

    @Override
    public void exitAssignPropertyCall(AssignPropertyCallContext ctx) {
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = nf.AssignPropertyCall(pos(ctx), TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitTypeFunctionType(TypeFunctionTypeContext ctx) {
        ctx.ast = ctx.functionType().ast;
    }

    @Override
    public void exitTypeConstrainedType(TypeConstrainedTypeContext ctx) {
        ctx.ast = ctx.constrainedType().ast;
    }

    @Override
    public void exitTypeVoid(TypeVoidContext ctx) {
        ctx.ast = ctx.void_().ast;
    }

    @Override
    public void exitTypeAnnotations(TypeAnnotationsContext ctx) {
        TypeNode Type = ctx.type().ast;
        List<AnnotationNode> Annotations = ctx.annotations().ast;
        TypeNode tn = Type;
        tn = (TypeNode) ((X10Ext) tn.ext()).annotations((List<AnnotationNode>) Annotations);
        ctx.ast = (TypeNode) tn.position(pos(ctx));
    }

    @Override
    public void exitFunctionType(FunctionTypeContext ctx) {
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        List<Formal> FormalParameterListopt = ctx.formalParameterListopt().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        TypeNode Type = ctx.type().ast;
        ctx.ast = nf.FunctionTypeNode(pos(ctx), TypeParametersopt, FormalParameterListopt, WhereClauseopt, Type, OBSOLETE_Offersopt);
    }

    @Override
    public void exitClassType(ClassTypeContext ctx) {
        ctx.ast = ctx.namedType().ast;
    }

    @Override
    public void exitConstrainedType(ConstrainedTypeContext ctx) {
        ctx.ast = ctx.namedType().ast;
    }

    @Override
    public void exitVoid_(Void_Context ctx) {
        ctx.ast = nf.CanonicalTypeNode(pos(ctx), ts.Void());
    }

    @Override
    public void exitSimpleNamedType0(SimpleNamedType0Context ctx) {
        ParsedName TypeName = ctx.typeName().ast;
        ctx.ast = (AmbTypeNode) TypeName.toType();
    }

    @Override
    public void exitSimpleNamedType1(SimpleNamedType1Context ctx) {
        Expr Primary = ctx.primary().ast;
        Id Identifier = ctx.identifier().ast;
        ctx.ast = nf.AmbTypeNode(pos(ctx), Primary, Identifier);
    }

    @Override
    public void exitSimpleNamedType2(SimpleNamedType2Context ctx) {
        AmbTypeNode SimpleNamedType = ctx.simpleNamedType().ast;
        List<TypeNode> TypeArguments = ctx.typeArgumentsopt().ast;
        List<Expr> Arguments = ctx.argumentsopt().ast;
        TypeNode qualifier;
        if (ctx.depParameters() == null) {
            qualifier = nf.AmbMacroTypeNode(pos(ctx), SimpleNamedType.prefix(), SimpleNamedType.name(), TypeArguments, Arguments);
        } else {
            DepParameterExpr DepParameters = ctx.depParameters().ast;
            qualifier = nf.AmbDepTypeNode(pos(ctx), SimpleNamedType.prefix(), SimpleNamedType.name(), TypeArguments, Arguments, DepParameters);
        }
        Id Identifier = ctx.identifier().ast;
        ctx.ast = nf.AmbTypeNode(pos(ctx), qualifier, Identifier);
    }

    @Override
    public void exitParameterizedNamedType(ParameterizedNamedTypeContext ctx) {
        AmbTypeNode SimpleNamedType = ctx.simpleNamedType().ast;
        if (ctx.typeArguments() == null && ctx.arguments() == null) {
            ctx.ast = SimpleNamedType;
        } else {
            List<TypeNode> typeArguments = ctx.typeArguments() == null ? new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false) : ctx.typeArguments().ast;
            List<Expr> Arguments = ctx.arguments() == null ? new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false) : ctx.arguments().ast;
            AmbMacroTypeNode type = nf.AmbMacroTypeNode(pos(ctx), SimpleNamedType.prefix(), SimpleNamedType.name(), typeArguments, Arguments);
            ctx.ast = type;
        }
    }

    @Override
    public void exitDepNamedType(DepNamedTypeContext ctx) {
        if (ctx.parameterizedNamedType().ast instanceof AmbMacroTypeNode) {
            AmbMacroTypeNode ParameterizedNamedType = (AmbMacroTypeNode) ctx.parameterizedNamedType().ast;
            DepParameterExpr DepParameters = ctx.depParameters().ast;
            TypeNode type = nf.AmbDepTypeNode(pos(ctx), ParameterizedNamedType, DepParameters);
            ctx.ast = type;
        } else {
            AmbTypeNode SimpleNamedType = ctx.parameterizedNamedType().ast;
            TypedList<TypeNode> TypeArguments = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
            TypedList<Expr> Arguments = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
            DepParameterExpr DepParameters = ctx.depParameters().ast;
            TypeNode type = nf.AmbDepTypeNode(pos(ctx), SimpleNamedType.prefix(), SimpleNamedType.name(), TypeArguments, Arguments, DepParameters);
            ctx.ast = type;
        }
    }

    @Override
    public void exitNamedTypeNoConstraints(NamedTypeNoConstraintsContext ctx) {
        ctx.ast = ctx.parameterizedNamedType().ast;
    }

    @Override
    public void exitNamedType0(NamedType0Context ctx) {
        ctx.ast = ctx.namedTypeNoConstraints().ast;
    }

    @Override
    public void exitNamedType1(NamedType1Context ctx) {
        ctx.ast = ctx.depNamedType().ast;
    }

    @Override
    public void exitDepParameters(DepParametersContext ctx) {
        List<Formal> FUTURE_ExistentialListopt = new ArrayList<Formal>();
        List<Expr> ConstraintConjunctionopt = ctx.constraintConjunctionopt().ast;
        ctx.ast = nf.DepParameterExpr(pos(ctx), FUTURE_ExistentialListopt, ConstraintConjunctionopt);
    }

    @Override
    public void exitTypeParamsWithVarianceopt(TypeParamsWithVarianceoptContext ctx) {
        if (ctx.typeParamWithVarianceList() == null) {
            ctx.ast = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        } else {
            ctx.ast = ctx.typeParamWithVarianceList().ast;
        }
    }

    @Override
    public void exitTypeParametersopt(TypeParametersoptContext ctx) {
        if (ctx.typeParameterList() == null) {
            ctx.ast = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        } else {
            ctx.ast = ctx.typeParameterList().ast;
        }
    }

    @Override
    public void exitFormalParameters(FormalParametersContext ctx) {
        ctx.ast = ctx.formalParameterListopt().ast;
    }

    @Override
    public void exitConstraintConjunctionopt(ConstraintConjunctionoptContext ctx) {
        List<Expr> l = new ArrayList<Expr>();
        for (ExpressionContext e : ctx.expression()) {
            l.add(e.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitHasZeroConstraint(HasZeroConstraintContext ctx) {
        TypeNode t1 = ctx.type().ast;
        ctx.ast = nf.HasZeroTest(pos(ctx), t1);
    }

    @Override
    public void exitIsRefConstraint(IsRefConstraintContext ctx) {
        TypeNode t1 = ctx.type().ast;
        ctx.ast = nf.IsRefTest(pos(ctx), t1);
    }

    @Override
    public void exitSubtypeConstraint0(SubtypeConstraint0Context ctx) {
        TypeNode t1 = ctx.t1.ast;
        TypeNode t2 = ctx.t2.ast;
        ctx.ast = nf.SubtypeTest(pos(ctx), t1, t2, false);
    }

    @Override
    public void exitSubtypeConstraint1(SubtypeConstraint1Context ctx) {
        TypeNode t1 = ctx.t1.ast;
        TypeNode t2 = ctx.t2.ast;
        ctx.ast = nf.SubtypeTest(pos(ctx), t2, t1, false);
    }

    @Override
    public void exitWhereClauseopt(WhereClauseoptContext ctx) {
        if (ctx.depParameters() == null) {
            ctx.ast = null;
        } else {
            DepParameterExpr DepParameters = ctx.depParameters().ast;
            ctx.ast = DepParameters;
        }
    }

    @Override
    public void exitClassDeclaration(ClassDeclarationContext ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        Id Identifier = ctx.identifier().ast;
        List<TypeParamNode> TypeParamsWithVarianceopt = ctx.typeParamsWithVarianceopt().ast;
        List<PropertyDecl> Propertiesopt = ctx.propertiesopt().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode Superopt = ctx.superExtendsopt().ast;
        List<TypeNode> Interfacesopt = ctx.interfacesopt().ast;
        ClassBody ClassBody = ctx.classBody().ast;
        List<Node> modifiers = checkClassModifiers(Modifiersopt);
        checkTypeName(Identifier);
        List<TypeParamNode> TypeParametersopt = TypeParamsWithVarianceopt;
        List<PropertyDecl> props = Propertiesopt;
        DepParameterExpr ci = WhereClauseopt;
        FlagsNode f = extractFlags(modifiers);
        List<AnnotationNode> annotations = extractAnnotations(modifiers);
        ClassDecl cd = nf.X10ClassDecl(pos(ctx), f, Identifier, TypeParametersopt, props, ci, Superopt, Interfacesopt, ClassBody);
        cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(annotations);
        ctx.ast = cd;
    }

    @Override
    public void exitStructDeclaration(StructDeclarationContext ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        Id Identifier = ctx.identifier().ast;
        List<TypeParamNode> TypeParamsWithVarianceopt = ctx.typeParamsWithVarianceopt().ast;
        List<PropertyDecl> Propertiesopt = ctx.propertiesopt().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        List<TypeNode> Interfacesopt = ctx.interfacesopt().ast;
        ClassBody ClassBody = ctx.classBody().ast;
        List<Node> modifiers = checkClassModifiers(Modifiersopt);
        checkTypeName(Identifier);
        List<TypeParamNode> TypeParametersopt = TypeParamsWithVarianceopt;
        List<PropertyDecl> props = Propertiesopt;
        DepParameterExpr ci = WhereClauseopt;
        ClassDecl cd = nf.X10ClassDecl(pos(ctx), extractFlags(modifiers, Flags.STRUCT), Identifier, TypeParametersopt, props, ci, null, Interfacesopt, ClassBody);
        cd = (ClassDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = cd;
    }

    @Override
    public void exitConstructorDeclaration(ConstructorDeclarationContext ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        List<TypeParamNode> TypeParametersopt = ctx.typeParametersopt().ast;
        List<Formal> FormalParameters = ctx.formalParameters().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        List<TypeNode> Throwsopt = ctx.throwsopt().ast;
        Block ConstructorBody = ctx.constructorBody().ast;
        List<Node> modifiers = checkConstructorModifiers(Modifiersopt);
        ConstructorDecl cd = nf.X10ConstructorDecl(pos(ctx), extractFlags(modifiers), nf.Id(pos(ctx.id), TypeSystem.CONSTRUCTOR_NAME), HasResultTypeopt, TypeParametersopt,
                FormalParameters, WhereClauseopt, OBSOLETE_Offersopt, Throwsopt, ConstructorBody);
        cd = (ConstructorDecl) ((X10Ext) cd.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = cd;
    }

    @Override
    public void exitSuperExtendsopt(SuperExtendsoptContext ctx) {
        if (ctx.classType() == null) {
            ctx.ast = null;
        } else {
            ctx.ast = ctx.classType().ast;
        }
    }

    @Override
    public void exitVarKeyword0(VarKeyword0Context ctx) {
        ctx.ast = Collections.singletonList(nf.FlagsNode(pos(ctx), Flags.FINAL));
    }

    @Override
    public void exitVarKeyword1(VarKeyword1Context ctx) {
        ctx.ast = Collections.singletonList(nf.FlagsNode(pos(ctx), Flags.NONE));
    }

    @Override
    public void exitFieldDeclaration(FieldDeclarationContext ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        List<FlagsNode> FieldKeyword = ctx.varKeyword() == null ? Collections.singletonList(nf.FlagsNode(pos(ctx), Flags.FINAL)) : ctx.varKeyword().ast;
        List<Object[]> FieldDeclarators = ctx.fieldDeclarators().ast;
        List<Node> modifiers = checkFieldModifiers(Modifiersopt);
        FlagsNode fn = extractFlags(modifiers, FieldKeyword);
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        for (Object[] o : FieldDeclarators) {
            Position pos = (Position) o[0];
            Id name = (Id) o[1];
            if (name == null)
                name = nf.Id(pos, Name.makeFresh());
            TypeNode type = (TypeNode) o[3];
            if (type == null)
                type = nf.UnknownTypeNode(name.position().markCompilerGenerated());
            Expr init = (Expr) o[4];
            FieldDecl fd = nf.FieldDecl(pos, fn, type, name, init);
            fd = (FieldDecl) ((X10Ext) fd.ext()).annotations(extractAnnotations(modifiers));
            fd = (FieldDecl) ((X10Ext) fd.ext()).setComment(comment(pos(ctx))); // TODO
            l.add(fd);
        }
        ctx.ast = l;
    }

    @Override
    public void exitStatement0(Statement0Context ctx) {
        ctx.ast = ctx.annotationStatement().ast;
    }

    @Override
    public void exitStatement1(Statement1Context ctx) {
        ctx.ast = ctx.expressionStatement().ast;
    }

    @Override
    public void exitAnnotationStatement(AnnotationStatementContext ctx) {
        List<AnnotationNode> Annotationsopt = ctx.annotationsopt().ast;
        Stmt NonExpressionStatement = ctx.nonExpressionStatement().ast;
        if (NonExpressionStatement.ext() instanceof X10Ext) {
            NonExpressionStatement = (Stmt) ((X10Ext) NonExpressionStatement.ext()).annotations(Annotationsopt);
        }
        ctx.ast = (Stmt) NonExpressionStatement.position(pos(ctx));
    }

    @Override
    public void exitNonExpressionStatemen0(NonExpressionStatemen0Context ctx) {
        ctx.ast = ctx.block().ast;
    }

    @Override
    public void exitNonExpressionStatemen1(NonExpressionStatemen1Context ctx) {
        ctx.ast = ctx.emptyStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen2(NonExpressionStatemen2Context ctx) {
        ctx.ast = ctx.assertStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen3(NonExpressionStatemen3Context ctx) {
        ctx.ast = ctx.switchStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen4(NonExpressionStatemen4Context ctx) {
        ctx.ast = ctx.doStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen5(NonExpressionStatemen5Context ctx) {
        ctx.ast = ctx.breakStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen6(NonExpressionStatemen6Context ctx) {
        ctx.ast = ctx.continueStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen7(NonExpressionStatemen7Context ctx) {
        ctx.ast = ctx.returnStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen8(NonExpressionStatemen8Context ctx) {
        ctx.ast = ctx.throwStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen9(NonExpressionStatemen9Context ctx) {
        ctx.ast = ctx.tryStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen10(NonExpressionStatemen10Context ctx) {
        ctx.ast = ctx.labeledStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen11(NonExpressionStatemen11Context ctx) {
        ctx.ast = ctx.ifThenStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen13(NonExpressionStatemen13Context ctx) {
        ctx.ast = ctx.whileStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen14(NonExpressionStatemen14Context ctx) {
        ctx.ast = ctx.forStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen15(NonExpressionStatemen15Context ctx) {
        ctx.ast = ctx.asyncStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen16(NonExpressionStatemen16Context ctx) {
        ctx.ast = ctx.atStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen17(NonExpressionStatemen17Context ctx) {
        ctx.ast = ctx.atomicStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen18(NonExpressionStatemen18Context ctx) {
        ctx.ast = ctx.whenStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen19(NonExpressionStatemen19Context ctx) {
        ctx.ast = ctx.atEachStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen20(NonExpressionStatemen20Context ctx) {
        ctx.ast = ctx.finishStatement().ast;
    }

    @Override
    public void exitNonExpressionStatemen21(NonExpressionStatemen21Context ctx) {
        ctx.ast = ctx.assignPropertyCall().ast;
    }

    @Override
    public void exitNonExpressionStatemen22(NonExpressionStatemen22Context ctx) {
        ctx.ast = ctx.oBSOLETE_OfferStatement().ast;
    }

    @Override
    public void exitOBSOLETE_OfferStatement(OBSOLETE_OfferStatementContext ctx) {
        Expr Expression = ctx.expression().ast;
        ctx.ast = nf.Offer(pos(ctx), Expression);
    }

    @Override
    public void exitIfThenStatement(IfThenStatementContext ctx) {
        Expr Expression = ctx.expression().ast;
        Stmt s1 = ctx.s1.ast;
        if (ctx.s2 == null) {
            ctx.ast = nf.If(pos(ctx), Expression, s1);
        } else {
            Stmt s2 = ctx.s2.ast;
            ctx.ast = nf.If(pos(ctx), Expression, s1, s2);
        }
    }

    @Override
    public void exitEmptyStatement(EmptyStatementContext ctx) {
        ctx.ast = nf.Empty(pos(ctx));
    }

    @Override
    public void exitLabeledStatement(LabeledStatementContext ctx) {
        Id Identifier = ctx.identifier().ast;
        Stmt LoopStatement = ctx.loopStatement().ast;
        ctx.ast = nf.Labeled(pos(ctx), Identifier, LoopStatement);
    }

    @Override
    public void exitLoopStatement0(LoopStatement0Context ctx) {
        ctx.ast = ctx.forStatement().ast;
    }

    @Override
    public void exitLoopStatement1(LoopStatement1Context ctx) {
        ctx.ast = ctx.whileStatement().ast;
    }

    @Override
    public void exitLoopStatement2(LoopStatement2Context ctx) {
        ctx.ast = ctx.doStatement().ast;
    }

    @Override
    public void exitLoopStatement3(LoopStatement3Context ctx) {
        ctx.ast = ctx.atEachStatement().ast;
    }

    @Override
    public void exitExpressionStatement(ExpressionStatementContext ctx) {
        Expr StatementExpression = ctx.expression().ast;
        ctx.ast = nf.Eval(pos(ctx), StatementExpression);
    }

    @Override
    public void exitAssertStatement0(AssertStatement0Context ctx) {
        Expr Expression = ctx.expression().ast;
        ctx.ast = nf.Assert(pos(ctx), Expression);
    }

    @Override
    public void exitAssertStatement1(AssertStatement1Context ctx) {
        Expr expr1 = ctx.e1.ast;
        Expr expr2 = ctx.e2.ast;
        ctx.ast = nf.Assert(pos(ctx), expr1, expr2);
    }

    @Override
    public void exitSwitchStatement(SwitchStatementContext ctx) {
        Expr Expression = ctx.expression().ast;
        List<SwitchElement> SwitchBlock = ctx.switchBlock().ast;
        ctx.ast = nf.Switch(pos(ctx), Expression, SwitchBlock);
    }

    @Override
    public void exitSwitchBlock(SwitchBlockContext ctx) {
        List<SwitchElement> SwitchBlockStatementGroupsopt = ctx.switchBlockStatementGroupsopt().ast;
        List<Case> SwitchLabelsopt = ctx.switchLabelsopt().ast;
        SwitchBlockStatementGroupsopt.addAll(SwitchLabelsopt);
        ctx.ast = SwitchBlockStatementGroupsopt;
    }

    @Override
    public void exitSwitchBlockStatementGroup(SwitchBlockStatementGroupContext ctx) {
        List<Case> SwitchLabels = ctx.switchLabels().ast;
        List<Stmt> BlockStatements = ctx.blockStatements().ast;
        List<SwitchElement> l = new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false);
        l.addAll(SwitchLabels);
        l.add(nf.SwitchBlock(pos(ctx), BlockStatements));
        ctx.ast = l;
    }

    @Override
    public void exitSwitchBlockStatementGroupsopt(SwitchBlockStatementGroupsoptContext ctx) {
        List<SwitchElement> l = new TypedList<SwitchElement>(new LinkedList<SwitchElement>(), SwitchElement.class, false);
        for (SwitchBlockStatementGroupContext e : ctx.switchBlockStatementGroup()) {
            l.addAll(e.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitSwitchLabel0(SwitchLabel0Context ctx) {
        Expr ConstantExpression = ctx.constantExpression().ast;
        ctx.ast = nf.Case(pos(ctx), ConstantExpression);
    }

    @Override
    public void exitSwitchLabel1(SwitchLabel1Context ctx) {
        ctx.ast = nf.Default(pos(ctx));
    }

    @Override
    public void exitSwitchLabelsopt(SwitchLabelsoptContext ctx) {
        if (ctx.switchLabels() == null) {
            ctx.ast = new TypedList<Case>(new LinkedList<Case>(), Case.class, false);
        } else {
            ctx.ast = ctx.switchLabels().ast;
        }
    }

    @Override
    public void exitSwitchLabels(SwitchLabelsContext ctx) {
        List<Case> l = new TypedList<Case>(new LinkedList<Case>(), Case.class, false);
        for (SwitchLabelContext switchLabel : ctx.switchLabel()) {
            l.add(switchLabel.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitWhileStatement(WhileStatementContext ctx) {
        Expr Expression = ctx.expression().ast;
        Stmt Statement = ctx.statement().ast;
        ctx.ast = nf.While(pos(ctx), Expression, Statement);
    }

    @Override
    public void exitDoStatement(DoStatementContext ctx) {
        Stmt Statement = ctx.statement().ast;
        Expr Expression = ctx.expression().ast;
        ctx.ast = nf.Do(pos(ctx), Statement, Expression);
    }

    @Override
    public void exitForStatement0(ForStatement0Context ctx) {
        ctx.ast = ctx.basicForStatement().ast;
    }

    @Override
    public void exitForStatement1(ForStatement1Context ctx) {
        ctx.ast = ctx.enhancedForStatement().ast;
    }

    @Override
    public void exitBasicForStatement(BasicForStatementContext ctx) {
        @SuppressWarnings("unchecked")
        List<ForInit> ForInitopt = (List<ForInit>) ctx.forInitopt().ast;
        Expr Expressionopt = ctx.expressionopt().ast;
        @SuppressWarnings("unchecked")
        List<ForUpdate> ForUpdateopt = (List<ForUpdate>) ctx.forUpdateopt().ast;
        Stmt Statement = ctx.statement().ast;
        ctx.ast = nf.For(pos(ctx), ForInitopt, Expressionopt, ForUpdateopt, Statement);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void exitForInit0(ForInit0Context ctx) {
        ctx.ast = (List<ForInit>) ctx.statementExpressionList().ast;
    }

    @Override
    public void exitForInit1(ForInit1Context ctx) {
        List<LocalDecl> LocalVariableDeclaration = ctx.localVariableDeclaration().ast;
        List<ForInit> l = new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false);
        l.addAll(LocalVariableDeclaration);
        ctx.ast = l;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void exitForUpdate(ForUpdateContext ctx) {
        ctx.ast = (List<ForUpdate>) ctx.statementExpressionList().ast;
    }

    @Override
    public void exitStatementExpressionList(StatementExpressionListContext ctx) {
        List<Eval> l = new TypedList<Eval>(new LinkedList<Eval>(), Eval.class, false);
        for (ExpressionContext e : ctx.expression()) {
            l.add(nf.Eval(pos(e), e.ast));
        }
        ctx.ast = l;
    }

    @Override
    public void exitBreakStatement(BreakStatementContext ctx) {
        Id Identifieropt = ctx.identifieropt().ast;
        ctx.ast = nf.Break(pos(ctx), Identifieropt);
    }

    @Override
    public void exitContinueStatement(ContinueStatementContext ctx) {
        Id Identifieropt = ctx.identifieropt().ast;
        ctx.ast = nf.Continue(pos(ctx), Identifieropt);
    }

    @Override
    public void exitReturnStatement(ReturnStatementContext ctx) {
        Expr Expressionopt = ctx.expressionopt().ast;
        ctx.ast = nf.Return(pos(ctx), Expressionopt);
    }

    @Override
    public void exitThrowStatement(ThrowStatementContext ctx) {
        Expr Expression = ctx.expression().ast;
        ctx.ast = nf.Throw(pos(ctx), Expression);
    }

    @Override
    public void exitTryStatement0(TryStatement0Context ctx) {
        Block Block = ctx.block().ast;
        List<Catch> Catches = ctx.catches().ast;
        ctx.ast = nf.Try(pos(ctx), Block, Catches);
    }

    @Override
    public void exitTryStatement1(TryStatement1Context ctx) {
        Block Block = ctx.block().ast;
        List<Catch> Catchesopt = ctx.catchesopt().ast;
        Block Finally = ctx.finallyBlock().ast;
        ctx.ast = nf.Try(pos(ctx), Block, Catchesopt, Finally);
    }

    @Override
    public void exitCatches(CatchesContext ctx) {
        List<Catch> l = new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false);
        for (CatchClauseContext CatchClause : ctx.catchClause()) {
            l.add(CatchClause.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitCatchClause(CatchClauseContext ctx) {
        X10Formal FormalParameter = ctx.formalParameter().ast;
        Block Block = ctx.block().ast;
        ctx.ast = nf.Catch(pos(ctx), FormalParameter, Block);
    }

    @Override
    public void exitFinallyBlock(FinallyBlockContext ctx) {
        Block Block = ctx.block().ast;
        ctx.ast = Block;
    }

    @Override
    public void exitClockedClauseopt(ClockedClauseoptContext ctx) {
        List<Expr> Arguments;
        if (ctx.arguments() == null) {
            Arguments = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        } else {
            Arguments = ctx.arguments().ast;
        }
        ctx.ast = Arguments;
    }

    @Override
    public void exitAsyncStatement0(AsyncStatement0Context ctx) {
        List<Expr> ClockedClauseopt = ctx.clockedClauseopt().ast;
        Stmt Statement = ctx.statement().ast;
        ctx.ast = nf.Async(pos(ctx), ClockedClauseopt, Statement);
    }

    @Override
    public void exitAsyncStatement1(AsyncStatement1Context ctx) {
        Stmt Statement = ctx.statement().ast;
        ctx.ast = nf.Async(pos(ctx), Statement, true);
    }

    @Override
    public void exitAtStatement(AtStatementContext ctx) {
        Expr Expression = ctx.expression().ast;
        Stmt Statement = ctx.statement().ast;
        ctx.ast = nf.AtStmt(pos(ctx), Expression, Statement);
    }

    @Override
    public void exitAtomicStatement(AtomicStatementContext ctx) {
        Stmt Statement = ctx.statement().ast;
        // Position of here might be wrong
        ctx.ast = nf.Atomic(pos(ctx), nf.Here(pos(ctx)), Statement);
    }

    @Override
    public void exitWhenStatement(WhenStatementContext ctx) {
        Expr Expression = ctx.expression().ast;
        Stmt Statement = ctx.statement().ast;
        ctx.ast = nf.When(pos(ctx), Expression, Statement);
    }

    @Override
    public void exitAtEachStatement0(AtEachStatement0Context ctx) {
        Formal LoopIndex = ctx.loopIndex().ast;
        Expr Expression = ctx.expression().ast;
        List<Expr> ClockedClauseopt = ctx.clockedClauseopt().ast;
        Stmt Statement = ctx.statement().ast;
        FlagsNode fn = LoopIndex.flags();
        if (!fn.flags().isFinal()) {
            err.syntaxError("Enhanced ateach loop may not have var loop index. " + LoopIndex, LoopIndex.position());
            fn = fn.flags(fn.flags().Final());
            LoopIndex = LoopIndex.flags(fn);
        }
        ctx.ast = nf.AtEach(pos(ctx), LoopIndex, Expression, ClockedClauseopt, Statement);
    }

    @Override
    public void exitAtEachStatement1(AtEachStatement1Context ctx) {
        Expr Expression = ctx.expression().ast;
        Stmt Statement = ctx.statement().ast;
        Id name = nf.Id(pos(ctx), Name.makeFresh());
        TypeNode type = nf.UnknownTypeNode(pos(ctx).markCompilerGenerated());
        X10Formal LoopIndex = nf.X10Formal(pos(ctx), nf.FlagsNode(pos(ctx), Flags.FINAL), type, name, null, true);
        TypedList<Expr> ClockedClauseopt = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        ctx.ast = nf.AtEach(pos(ctx), LoopIndex, Expression, ClockedClauseopt, Statement);
    }

    @Override
    public void exitEnhancedForStatement0(EnhancedForStatement0Context ctx) {
        Formal LoopIndex = ctx.loopIndex().ast;
        Expr Expression = ctx.expression().ast;
        Stmt Statement = ctx.statement().ast;
        FlagsNode fn = LoopIndex.flags();
        if (!fn.flags().isFinal()) {
            err.syntaxError("Enhanced for loop may not have var loop index. " + LoopIndex, LoopIndex.position());
            fn = fn.flags(fn.flags().Final());
            LoopIndex = LoopIndex.flags(fn);
        }
        ctx.ast = nf.ForLoop(pos(ctx), LoopIndex, Expression, Statement);
    }

    @Override
    public void exitEnhancedForStatement1(EnhancedForStatement1Context ctx) {
        Expr Expression = ctx.expression().ast;
        Stmt Statement = ctx.statement().ast;
        Id name = nf.Id(pos(ctx), Name.makeFresh());
        TypeNode type = nf.UnknownTypeNode(pos(ctx).markCompilerGenerated());
        Formal LoopIndex = nf.X10Formal(pos(ctx), nf.FlagsNode(pos(ctx), Flags.FINAL), type, name, null, true);
        ctx.ast = nf.ForLoop(pos(ctx), LoopIndex, Expression, Statement);
    }

    @Override
    public void exitFinishStatement0(FinishStatement0Context ctx) {
        Stmt Statement = ctx.statement().ast;
        ctx.ast = nf.Finish(pos(ctx), Statement, false);
    }

    @Override
    public void exitFinishStatement1(FinishStatement1Context ctx) {
        Stmt Statement = ctx.statement().ast;
        ctx.ast = nf.Finish(pos(ctx), Statement, true);
    }

    @Override
    public void exitCastExpression0(CastExpression0Context ctx) {
        ctx.ast = ctx.primary().ast;
    }

    @Override
    public void exitCastExpression1(CastExpression1Context ctx) {
        ParsedName ExpressionName = ctx.expressionName().ast;
        ctx.ast = ExpressionName.toExpr();
    }

    @Override
    public void exitCastExpression2(CastExpression2Context ctx) {
        Expr CastExpression = ctx.castExpression().ast;
        TypeNode Type = ctx.type().ast;
        ctx.ast = nf.X10Cast(pos(ctx), Type, CastExpression);
    }

    @Override
    public void exitTypeParamWithVarianceList0(TypeParamWithVarianceList0Context ctx) {
        TypeParamNode TypeParameter = ctx.typeParameter().ast;
        List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        l.add(TypeParameter);
        ctx.ast = l;
    }

    @Override
    public void exitTypeParamWithVarianceList1(TypeParamWithVarianceList1Context ctx) {
        TypeParamNode OBSOLETE_TypeParamWithVariance = ctx.oBSOLETE_TypeParamWithVariance().ast;
        List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        l.add(OBSOLETE_TypeParamWithVariance);
        ctx.ast = l;
    }

    @Override
    public void exitTypeParamWithVarianceList2(TypeParamWithVarianceList2Context ctx) {
        List<TypeParamNode> TypeParamWithVarianceList = ctx.typeParamWithVarianceList().ast;
        TypeParamNode TypeParameter = ctx.typeParameter().ast;
        TypeParamWithVarianceList.add(TypeParameter);
        ctx.ast = TypeParamWithVarianceList;
    }

    @Override
    public void exitTypeParamWithVarianceList3(TypeParamWithVarianceList3Context ctx) {
        List<TypeParamNode> TypeParamWithVarianceList = ctx.typeParamWithVarianceList().ast;
        TypeParamNode OBSOLETE_TypeParamWithVariance = ctx.oBSOLETE_TypeParamWithVariance().ast;
        TypeParamWithVarianceList.add(OBSOLETE_TypeParamWithVariance);
        ctx.ast = TypeParamWithVarianceList;
    }

    @Override
    public void exitTypeParameterList(TypeParameterListContext ctx) {
        List<TypeParamNode> l = new TypedList<TypeParamNode>(new LinkedList<TypeParamNode>(), TypeParamNode.class, false);
        for (TypeParameterContext TypeParameter : ctx.typeParameter()) {
            l.add(TypeParameter.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitOBSOLETE_TypeParamWithVariance0(OBSOLETE_TypeParamWithVariance0Context ctx) {
        TypeParamNode TypeParameter = ctx.typeParameter().ast;
        err.syntaxError("Covariance is no longer supported.", pos(ctx));
        ctx.ast = (TypeParamNode) TypeParameter.variance(ParameterType.Variance.COVARIANT).position(pos(ctx));
    }

    @Override
    public void exitOBSOLETE_TypeParamWithVariance1(OBSOLETE_TypeParamWithVariance1Context ctx) {
        TypeParamNode TypeParameter = ctx.typeParameter().ast;
        err.syntaxError("Contravariance is no longer supported.", pos(ctx));
        ctx.ast = (TypeParamNode) TypeParameter.variance(ParameterType.Variance.CONTRAVARIANT).position(pos(ctx));
    }

    @Override
    public void exitTypeParameter(TypeParameterContext ctx) {
        Id Identifier = ctx.identifier().ast;
        ctx.ast = nf.TypeParamNode(pos(ctx), Identifier);
    }


    @Override
    public void exitClosureExpression(ClosureExpressionContext ctx) {
        List<Formal> FormalParameters = ctx.formalParameters().ast;
        DepParameterExpr WhereClauseopt = ctx.whereClauseopt().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        TypeNode HasResultType = HasResultTypeopt == null ? nf.UnknownTypeNode(Position.COMPILER_GENERATED) : HasResultTypeopt;
        TypeNode OBSOLETE_Offersopt = ctx.oBSOLETE_Offersopt().ast;
        Block ClosureBody = ctx.closureBody().ast;
        ctx.ast = nf.Closure(pos(ctx), FormalParameters, WhereClauseopt, HasResultType, ClosureBody);
    }

    @Override
    public void exitLastExpression(LastExpressionContext ctx) {
        Expr Expression = ctx.expression().ast;
        ctx.ast = nf.X10Return(pos(ctx), Expression, true);
    }

    @Override
    public void exitClosureBody0(ClosureBody0Context ctx) {
        Expr ConditionalExpression = ctx.expression().ast;
        ctx.ast = nf.Block(pos(ctx), nf.X10Return(pos(ctx), ConditionalExpression, true));
    }

    @Override
    public void exitClosureBody1(ClosureBody1Context ctx) {
        List<AnnotationNode> Annotationsopt = ctx.annotationsopt().ast;
        List<Stmt> BlockStatementsopt = ctx.blockStatementsopt().ast;
        Stmt LastExpression = ctx.lastExpression().ast;
        List<Stmt> l = new ArrayList<Stmt>();
        l.addAll(BlockStatementsopt);
        l.add(LastExpression);
        Block b = nf.Block(pos(ctx), l);
        b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
        ctx.ast = b;
    }

    @Override
    public void exitClosureBody2(ClosureBody2Context ctx) {
        List<AnnotationNode> Annotationsopt = ctx.annotationsopt().ast;
        Block Block = ctx.block().ast;
        Block b = Block;
        b = (Block) ((X10Ext) b.ext()).annotations(Annotationsopt);
        ctx.ast = (polyglot.ast.Block) b.position(pos(ctx));
    }

    @Override
    public void exitAtExpression(AtExpressionContext ctx) {
        List<AnnotationNode> Annotationsopt = ctx.annotationsopt().ast;
        Expr Expression = ctx.expression().ast;
        Block ClosureBody = ctx.closureBody().ast;
        AtExpr r = nf.AtExpr(pos(ctx), Expression, ClosureBody);
        r = (AtExpr) ((X10Ext) r.ext()).annotations(Annotationsopt);
        ctx.ast = r;
    }

    @Override
    public void exitOBSOLETE_FinishExpression(OBSOLETE_FinishExpressionContext ctx) {
        Expr Expression = ctx.expression().ast;
        Block Block = ctx.block().ast;
        ctx.ast = nf.FinishExpr(pos(ctx), Expression, Block);
    }

    @Override
    public void exitTypeName0(TypeName0Context ctx) {
        Id Identifier = ctx.identifier().ast;
        ctx.ast = new ParsedName(nf, ts, pos(ctx), Identifier);
    }

    @Override
    public void exitTypeName1(TypeName1Context ctx) {
        ParsedName TypeName = ctx.typeName().ast;
        Id Identifier = ctx.identifier().ast;
        // Position might be wrong
        ctx.ast = new ParsedName(nf, ts, pos(ctx), TypeName, Identifier);
    }

    @Override
    public void exitClassName(ClassNameContext ctx) {
        ctx.ast = ctx.typeName().ast;
    }

    @Override
    public void exitTypeArguments(TypeArgumentsContext ctx) {
        List<TypeNode> l = new ArrayList<TypeNode>();
        for (TypeContext Type : ctx.type()) {
            l.add(Type.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitPackageName0(PackageName0Context ctx) {
        ctx.ast = new ParsedName(nf, ts, pos(ctx), ctx.identifier().ast);
    }

    @Override
    public void exitPackageName1(PackageName1Context ctx) {
        ctx.ast = new ParsedName(nf, ts, pos(ctx), ctx.packageName().ast, ctx.identifier().ast);
    }

    @Override
    public void exitExpressionName0(ExpressionName0Context ctx) {
        ctx.ast = new ParsedName(nf, ts, pos(ctx), ctx.identifier().ast);
    }

    @Override
    public void exitExpressionName1(ExpressionName1Context ctx) {
        ctx.ast = new ParsedName(nf, ts, pos(ctx), ctx.fullyQualifiedName().ast, ctx.identifier().ast);
    }

    @Override
    public void exitMethodName0(MethodName0Context ctx) {
        ctx.ast = new ParsedName(nf, ts, pos(ctx), ctx.identifier().ast);
    }

    @Override
    public void exitMethodName1(MethodName1Context ctx) {
        ctx.ast = new ParsedName(nf, ts, pos(ctx), ctx.fullyQualifiedName().ast, ctx.identifier().ast);
    }

    @Override
    public void exitPackageOrTypeName0(PackageOrTypeName0Context ctx) {
        ctx.ast = new ParsedName(nf, ts, pos(ctx), ctx.identifier().ast);
    }

    @Override
    public void exitPackageOrTypeName1(PackageOrTypeName1Context ctx) {
        ctx.ast = new ParsedName(nf, ts, pos(ctx), ctx.packageOrTypeName().ast, ctx.identifier().ast);
    }

    @Override
    public void exitFullyQualifiedName0(FullyQualifiedName0Context ctx) {
        ctx.ast = new ParsedName(nf, ts, pos(ctx), ctx.identifier().ast);
    }

    @Override
    public void exitFullyQualifiedName1(FullyQualifiedName1Context ctx) {
        ctx.ast = new ParsedName(nf, ts, pos(ctx), ctx.fullyQualifiedName().ast, ctx.identifier().ast);
    }

    @Override
    public void exitImportDeclaration0(ImportDeclaration0Context ctx) {
        ParsedName TypeName = ctx.typeName().ast;
        ctx.ast = nf.Import(pos(ctx), Import.CLASS, QName.make(TypeName.toString()));
    }

    @Override
    public void exitImportDeclaration1(ImportDeclaration1Context ctx) {
        ParsedName PackageOrTypeName = ctx.packageOrTypeName().ast;
        ctx.ast = nf.Import(pos(ctx), Import.PACKAGE, QName.make(PackageOrTypeName.toString()));
    }

    @Override
    public void exitTypeDeclarationsopt(TypeDeclarationsoptContext ctx) {
        List<TopLevelDecl> l = new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false);
        for (TypeDeclarationContext typeDecl : ctx.typeDeclaration()) {
            l.add(typeDecl.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitTypeDeclaration0(TypeDeclaration0Context ctx) {
        ctx.ast = ctx.classDeclaration().ast;
    }

    @Override
    public void exitTypeDeclaration1(TypeDeclaration1Context ctx) {
        ctx.ast = ctx.structDeclaration().ast;
    }

    @Override
    public void exitTypeDeclaration2(TypeDeclaration2Context ctx) {
        ctx.ast = ctx.interfaceDeclaration().ast;
    }

    @Override
    public void exitTypeDeclaration3(TypeDeclaration3Context ctx) {
        ctx.ast = ctx.typeDefDeclaration().ast;
    }

    @Override
    public void exitTypeDeclaration4(TypeDeclaration4Context ctx) {
        ctx.ast = null;
    }

    @Override
    public void exitInterfacesopt(InterfacesoptContext ctx) {
        List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
        for (TypeContext Type : ctx.type()) {
            l.add(Type.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitClassBody(ClassBodyContext ctx) {
        List<ClassMember> ClassMemberDeclarationsopt = ctx.classMemberDeclarationsopt().ast;
        ctx.ast = nf.ClassBody(pos(ctx), ClassMemberDeclarationsopt);
    }

    @Override
    public void exitClassMemberDeclarationsopt(ClassMemberDeclarationsoptContext ctx) {
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        for (ClassMemberDeclarationContext ClassMember : ctx.classMemberDeclaration()) {
            l.addAll(ClassMember.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitClassMemberDeclaration0(ClassMemberDeclaration0Context ctx) {
        ctx.ast = ctx.interfaceMemberDeclaration().ast;
    }

    @Override
    public void exitClassMemberDeclaration1(ClassMemberDeclaration1Context ctx) {
        ConstructorDecl ConstructorDeclaration = ctx.constructorDeclaration().ast;
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        l.add(ConstructorDeclaration);
        ctx.ast = l;
    }

    @Override
    public void exitFormalDeclarators(FormalDeclaratorsContext ctx) {
        List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
        for (FormalDeclaratorContext FormalDeclarator : ctx.formalDeclarator()) {
            l.add(FormalDeclarator.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitFieldDeclarators(FieldDeclaratorsContext ctx) {
        List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
        for (FieldDeclaratorContext FieldDeclarator : ctx.fieldDeclarator()) {
            l.add(FieldDeclarator.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitVariableDeclaratorsWithType(VariableDeclaratorsWithTypeContext ctx) {
        List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
        for (VariableDeclaratorWithTypeContext VariableDeclaratorWithType : ctx.variableDeclaratorWithType()) {
            l.add(VariableDeclaratorWithType.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitVariableDeclarators(VariableDeclaratorsContext ctx) {
        List<Object[]> l = new TypedList<Object[]>(new LinkedList<Object[]>(), Object[].class, false);
        for (VariableDeclaratorContext VariableDeclarator : ctx.variableDeclarator()) {
            l.add(VariableDeclarator.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitVariableInitializer(VariableInitializerContext ctx) {
        ctx.ast = ctx.expression().ast;
    }

    @Override
    public void exitResultType(ResultTypeContext ctx) {
        ctx.ast = ctx.type().ast;
    }

    @Override
    public void exitHasResultType0(HasResultType0Context ctx) {
        TypeNode Type = ctx.resultType().ast;
        ctx.ast = Type;
    }

    @Override
    public void exitHasResultType1(HasResultType1Context ctx) {
        TypeNode Type = ctx.type().ast;
        ctx.ast = nf.HasType(Type);
    }

    @Override
    public void exitFormalParameterList(FormalParameterListContext ctx) {
        List<Formal> l = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
        for (FormalParameterContext FormalParameter : ctx.formalParameter()) {
            l.add(FormalParameter.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitLoopIndexDeclarator0(LoopIndexDeclarator0Context ctx) {
        Id Identifier = ctx.identifier().ast;
        List<Id> IdentifierList = Collections.<Id> emptyList();
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, null };
    }

    @Override
    public void exitLoopIndexDeclarator1(LoopIndexDeclarator1Context ctx) {
        Id Identifier = null;
        List<Id> IdentifierList = ctx.identifierList().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, null };
    }

    @Override
    public void exitLoopIndexDeclarator2(LoopIndexDeclarator2Context ctx) {
        Id Identifier = ctx.identifier().ast;
        List<Id> IdentifierList = ctx.identifierList().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, null };
    }

    TypeNode explodedType(Position p) {
        // exploded formals/locals are either Int or T (when exploding Array[T]).
        // nf.TypeNodeFromQualifiedName(p,QName.make("x10.lang.Int"));
        return nf.UnknownTypeNode(p);

    }

    List<Formal> createExplodedFormals(List<Id> exploded) {
        List<Formal> explodedFormals = new ArrayList<Formal>();
        for (Id id : exploded) {
            // exploded formals are always final (VAL)
            explodedFormals.add(nf.Formal(id.position(), nf.FlagsNode(id.position(), Flags.FINAL), explodedType(id.position()), id));
        }
        return explodedFormals;
    }

    @Override
    public void exitLoopIndex0(LoopIndex0Context ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        Object[] LoopIndexDeclarator = ctx.loopIndexDeclarator().ast;
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        X10Formal f;
        FlagsNode fn = extractFlags(modifiers, Flags.FINAL);
        Object[] o = LoopIndexDeclarator;
        Position pos = (Position) o[0];
        Id name = (Id) o[1];
        boolean unnamed = name == null;
        if (name == null)
            name = nf.Id(pos, Name.makeFresh());
        @SuppressWarnings("unchecked")
        List<Id> exploded = (List<Id>) o[2];
        DepParameterExpr guard = (DepParameterExpr) o[3];
        TypeNode type = (TypeNode) o[4];
        if (type == null)
            type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        List<Formal> explodedFormals = createExplodedFormals(exploded);
        f = nf.X10Formal(pos(ctx), fn, type, name, explodedFormals, unnamed);
        f = (X10Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = f;
    }

    @Override
    public void exitLoopIndex1(LoopIndex1Context ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        List<FlagsNode> VarKeyword = ctx.varKeyword().ast;
        Object[] LoopIndexDeclarator = ctx.loopIndexDeclarator().ast;
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        X10Formal f;
        FlagsNode fn = extractFlags(modifiers, VarKeyword);
        Object[] o = LoopIndexDeclarator;
        Position pos = (Position) o[0];
        Id name = (Id) o[1];
        boolean unnamed = name == null;
        if (name == null)
            name = nf.Id(pos, Name.makeFresh());
        @SuppressWarnings("unchecked")
        List<Id> exploded = (List<Id>) o[2];
        DepParameterExpr guard = (DepParameterExpr) o[3];
        TypeNode type = (TypeNode) o[4];
        if (type == null)
            type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        List<Formal> explodedFormals = createExplodedFormals(exploded);
        f = nf.X10Formal(pos(ctx), fn, type, name, explodedFormals, unnamed);
        f = (X10Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = f;
    }

    @Override
    public void exitFormalParameter0(FormalParameter0Context ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        Object[] FormalDeclarator = ctx.formalDeclarator().ast;
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        X10Formal f;
        FlagsNode fn = extractFlags(modifiers, Flags.FINAL);
        Object[] o = FormalDeclarator;
        Position pos = (Position) o[0];
        Id name = (Id) o[1];
        boolean unnamed = name == null;
        if (name == null)
            name = nf.Id(pos.markCompilerGenerated(), Name.makeFresh());
        @SuppressWarnings("unchecked")
        List<Id> exploded = (List<Id>) o[2];
        DepParameterExpr guard = (DepParameterExpr) o[3];
        TypeNode type = (TypeNode) o[4];
        if (type == null)
            type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        Expr init = (Expr) o[5];
        List<Formal> explodedFormals = createExplodedFormals(exploded);
        f = nf.X10Formal(pos(ctx), fn, type, name, explodedFormals, unnamed);
        f = (X10Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = f;
    }

    @Override
    public void exitFormalParameter1(FormalParameter1Context ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        List<FlagsNode> VarKeyword = ctx.varKeyword().ast;
        Object[] FormalDeclarator = ctx.formalDeclarator().ast;
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        X10Formal f;
        FlagsNode fn = extractFlags(modifiers, VarKeyword);
        Object[] o = FormalDeclarator;
        Position pos = (Position) o[0];
        Id name = (Id) o[1];
        boolean unnamed = name == null;
        if (name == null)
            name = nf.Id(pos.markCompilerGenerated(), Name.makeFresh());
        @SuppressWarnings("unchecked")
        List<Id> exploded = (List<Id>) o[2];
        DepParameterExpr guard = (DepParameterExpr) o[3];
        TypeNode type = (TypeNode) o[4];
        if (type == null)
            type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
        Expr init = (Expr) o[5];
        List<Formal> explodedFormals = createExplodedFormals(exploded);
        f = nf.X10Formal(pos(ctx), fn, type, name, explodedFormals, unnamed);
        f = (X10Formal) ((X10Ext) f.ext()).annotations(extractAnnotations(modifiers));
        ctx.ast = f;
    }

    @Override
    public void exitFormalParameter2(FormalParameter2Context ctx) {
        TypeNode Type = ctx.type().ast;
        X10Formal f;
        FlagsNode fn = nf.FlagsNode(pos(ctx).markCompilerGenerated(), Flags.FINAL);
        Id name = nf.Id(pos(ctx).markCompilerGenerated(), Name.makeFresh("id"));
        List<Formal> explodedFormals = Collections.<Formal> emptyList();
        boolean unnamed = true;
        f = nf.X10Formal(pos(ctx).markCompilerGenerated(), fn, Type, name, explodedFormals, unnamed);
        ctx.ast = f;
    }

    @Override
    public void exitOBSOLETE_Offersopt(OBSOLETE_OffersoptContext ctx) {
        TypeNode Type = ctx.type() == null ? null : ctx.type().ast;
        ctx.ast = Type;
    }

    @Override
    public void exitThrowsopt(ThrowsoptContext ctx) {
        List<TypeNode> throwsList = new ArrayList<TypeNode>();
        for (TypeContext type : ctx.type()) {
            throwsList.add(type.ast);
        }
        ctx.ast = throwsList;
    }

    @Override
    public void exitMethodBody0(MethodBody0Context ctx) {
        Stmt LastExpression = ctx.lastExpression().ast;
        ctx.ast = nf.Block(pos(ctx), LastExpression);
    }

    @Override
    public void exitMethodBody1(MethodBody1Context ctx) {
        List<AnnotationNode> Annotationsopt = ctx.annotationsopt().ast;
        List<Stmt> BlockStatementsopt = ctx.blockStatementsopt().ast;
        Stmt LastExpression = ctx.lastExpression().ast;
        List<Stmt> l = new ArrayList<Stmt>();
        l.addAll(BlockStatementsopt);
        l.add(LastExpression);
        ctx.ast = (Block) ((X10Ext) nf.Block(pos(ctx), l).ext()).annotations(Annotationsopt);
    }

    @Override
    public void exitMethodBody2(MethodBody2Context ctx) {
        List<AnnotationNode> Annotationsopt = ctx.annotationsopt().ast;
        Block Block = ctx.block().ast;
        ctx.ast = (Block) ((X10Ext) Block.ext()).annotations(Annotationsopt).position(pos(ctx));
    }

    @Override
    public void exitMethodBody3(MethodBody3Context ctx) {
        ctx.ast = null;
    }

    @Override
    public void exitConstructorBody0(ConstructorBody0Context ctx) {
        Block ConstructorBlock = ctx.constructorBlock().ast;
        ctx.ast = ConstructorBlock;
    }

    @Override
    public void exitConstructorBody1(ConstructorBody1Context ctx) {
        ConstructorCall ExplicitConstructorInvocation = ctx.explicitConstructorInvocation().ast;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(ExplicitConstructorInvocation);
        ctx.ast = nf.Block(pos(ctx), l);
    }

    @Override
    public void exitConstructorBody2(ConstructorBody2Context ctx) {
        Stmt AssignPropertyCall = ctx.assignPropertyCall().ast;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(AssignPropertyCall);
        ctx.ast = nf.Block(pos(ctx), l);
    }

    @Override
    public void exitConstructorBody3(ConstructorBody3Context ctx) {
        ctx.ast = null;
    }

    @Override
    public void exitConstructorBlock(ConstructorBlockContext ctx) {
        Stmt ExplicitConstructorInvocationopt = ctx.explicitConstructorInvocation() == null ? null : ctx.explicitConstructorInvocation().ast;
        List<Stmt> BlockStatementsopt = ctx.blockStatementsopt().ast;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        if (ExplicitConstructorInvocationopt != null) {
            l.add(ExplicitConstructorInvocationopt);
        }
        l.addAll(BlockStatementsopt);
        ctx.ast = nf.Block(pos(ctx), l);
    }

    @Override
    public void exitArguments(ArgumentsContext ctx) {
        ctx.ast = ctx.argumentList().ast;
    }

    @Override
    public void exitExtendsInterfacesopt(ExtendsInterfacesoptContext ctx) {
        List<TypeNode> l = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
        for (TypeContext type : ctx.type()) {
            l.add(type.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitInterfaceBody(InterfaceBodyContext ctx) {
        List<ClassMember> InterfaceMemberDeclarationsopt = ctx.interfaceMemberDeclarationsopt().ast;
        ctx.ast = nf.ClassBody(pos(ctx), InterfaceMemberDeclarationsopt);
    }

    @Override
    public void exitInterfaceMemberDeclarationsopt(InterfaceMemberDeclarationsoptContext ctx) {
        TypedList<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        for (InterfaceMemberDeclarationContext decl : ctx.interfaceMemberDeclaration()) {
            l.addAll(decl.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitInterfaceMemberDeclaration0(InterfaceMemberDeclaration0Context ctx) {
        ClassMember MethodDeclaration = ctx.methodDeclaration().ast;
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        l.add(MethodDeclaration);
        ctx.ast = l;
    }

    @Override
    public void exitInterfaceMemberDeclaration1(InterfaceMemberDeclaration1Context ctx) {
        ClassMember PropertyMethodDeclaration = ctx.propertyMethodDeclaration().ast;
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        l.add(PropertyMethodDeclaration);
        ctx.ast = l;
    }

    @Override
    public void exitInterfaceMemberDeclaration2(InterfaceMemberDeclaration2Context ctx) {
        List<ClassMember> FieldDeclaration = ctx.fieldDeclaration().ast;
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        l.addAll(FieldDeclaration);
        ctx.ast = l;
    }

    @Override
    public void exitInterfaceMemberDeclaration3(InterfaceMemberDeclaration3Context ctx) {
        ClassMember TypeDeclaration = (ClassMember) ctx.typeDeclaration().ast;
        List<ClassMember> l = new TypedList<ClassMember>(new LinkedList<ClassMember>(), ClassMember.class, false);
        if (TypeDeclaration != null) {
            l.add(TypeDeclaration);
        }
        ctx.ast = l;
    }

    @Override
    public void exitAnnotationsopt(AnnotationsoptContext ctx) {
        List<AnnotationNode> l;
        if (ctx.annotations() == null) {
            l = new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false);
        } else {
            l = ctx.annotations().ast;
        }
        ctx.ast = l;
    }

    @Override
    public void exitAnnotations(AnnotationsContext ctx) {
        List<AnnotationNode> l = new TypedList<AnnotationNode>(new LinkedList<AnnotationNode>(), AnnotationNode.class, false);
        for (AnnotationContext annotation : ctx.annotation()) {
            l.add(annotation.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitAnnotation(AnnotationContext ctx) {
        TypeNode NamedTypeNoConstraints = ctx.namedTypeNoConstraints().ast;
        ctx.ast = nf.AnnotationNode(pos(ctx), NamedTypeNoConstraints);
    }

    @Override
    public void exitIdentifier(IdentifierContext ctx) {
        ctx.ast = nf.Id(pos(ctx), ctx.start.getText());
    }


    @Override
    public void exitBlock(BlockContext ctx) {
        List<Stmt> BlockStatementsopt = ctx.blockStatementsopt().ast;
        ctx.ast = nf.Block(pos(ctx), BlockStatementsopt);
    }

    @Override
    public void exitBlockStatements(BlockStatementsContext ctx) {
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        for (BlockInteriorStatementContext blockInteriorStatement : ctx.blockInteriorStatement()) {
            l.addAll(blockInteriorStatement.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitBlockInteriorStatement0(BlockInteriorStatement0Context ctx) {
        ctx.ast = ctx.localVariableDeclarationStatement().ast;
    }

    @Override
    public void exitBlockInteriorStatement1(BlockInteriorStatement1Context ctx) {
        ClassDecl ClassDeclaration = ctx.classDeclaration().ast;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(nf.LocalClassDecl(pos(ctx), ClassDeclaration));
        ctx.ast = l;
    }

    @Override
    public void exitBlockInteriorStatement2(BlockInteriorStatement2Context ctx) {
        ClassDecl StructDeclaration = ctx.structDeclaration().ast;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(nf.LocalClassDecl(pos(ctx), StructDeclaration));
        ctx.ast = l;
    }

    @Override
    public void exitBlockInteriorStatement3(BlockInteriorStatement3Context ctx) {
        TypeDecl TypeDefDeclaration = ctx.typeDefDeclaration().ast;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(nf.LocalTypeDef(pos(ctx), TypeDefDeclaration));
        ctx.ast = l;
    }

    @Override
    public void exitBlockInteriorStatement4(BlockInteriorStatement4Context ctx) {
        Stmt Statement = ctx.statement().ast;
        List<Stmt> l = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        l.add(Statement);
        ctx.ast = l;
    }

    @Override
    public void exitIdentifierList(IdentifierListContext ctx) {
        List<Id> l = new TypedList<Id>(new LinkedList<Id>(), Id.class, false);
        for (IdentifierContext identifier : ctx.identifier()) {
            l.add(identifier.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitFormalDeclarator0(FormalDeclarator0Context ctx) {
        Id Identifier = ctx.identifier().ast;
        List<Id> IdentifierList = Collections.<Id> emptyList();
        TypeNode ResultType = ctx.resultType().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, ResultType, null };
    }

    @Override
    public void exitFormalDeclarator1(FormalDeclarator1Context ctx) {
        Id Identifier = null;
        List<Id> IdentifierList = ctx.identifierList().ast;
        TypeNode ResultType = ctx.resultType().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, ResultType, null };
    }

    @Override
    public void exitFormalDeclarator2(FormalDeclarator2Context ctx) {
        Id Identifier = ctx.identifier().ast;
        List<Id> IdentifierList = ctx.identifierList().ast;
        TypeNode ResultType = ctx.resultType().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, ResultType, null };
    }

    @Override
    public void exitFieldDeclarator0(FieldDeclarator0Context ctx) {
        Id Identifier = ctx.identifier().ast;
        TypeNode HasResultTypeopt = (TypeNode) ctx.hasResultType().ast;
        Expr VariableInitializer = null;
        ctx.ast = new Object[] { pos(ctx), Identifier, Collections.<Id> emptyList(), HasResultTypeopt, VariableInitializer };
    }

    @Override
    public void exitFieldDeclarator1(FieldDeclarator1Context ctx) {
        Id Identifier = ctx.identifier().ast;
        TypeNode HasResultTypeopt = (TypeNode) ctx.hasResultTypeopt().ast;
        Expr VariableInitializer = ctx.variableInitializer().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, Collections.<Id> emptyList(), HasResultTypeopt, VariableInitializer };
    }

    @Override
    public void exitVariableDeclarator0(VariableDeclarator0Context ctx) {
        Id Identifier = ctx.identifier().ast;
        List<Id> IdentifierList = Collections.<Id> emptyList();
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        Expr VariableInitializer = ctx.variableInitializer().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer };
    }

    @Override
    public void exitVariableDeclarator1(VariableDeclarator1Context ctx) {
        Id Identifier = null;
        List<Id> IdentifierList = ctx.identifierList().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        Expr VariableInitializer = ctx.variableInitializer().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer };
    }

    @Override
    public void exitVariableDeclarator2(VariableDeclarator2Context ctx) {
        Id Identifier = ctx.identifier().ast;
        List<Id> IdentifierList = ctx.identifierList().ast;
        TypeNode HasResultTypeopt = ctx.hasResultTypeopt().ast;
        Expr VariableInitializer = ctx.variableInitializer().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer };
    }

    @Override
    public void exitVariableDeclaratorWithType0(VariableDeclaratorWithType0Context ctx) {
        Id Identifier = ctx.identifier().ast;
        List<Id> IdentifierList = Collections.<Id> emptyList();
        TypeNode HasResultTypeopt = ctx.hasResultType().ast;
        Expr VariableInitializer = ctx.variableInitializer().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer };
    }

    @Override
    public void exitVariableDeclaratorWithType1(VariableDeclaratorWithType1Context ctx) {
        Id Identifier = null;
        List<Id> IdentifierList = ctx.identifierList().ast;
        TypeNode HasResultTypeopt = ctx.hasResultType().ast;
        Expr VariableInitializer = ctx.variableInitializer().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer };
    }

    @Override
    public void exitVariableDeclaratorWithType2(VariableDeclaratorWithType2Context ctx) {
        Id Identifier = ctx.identifier().ast;
        List<Id> IdentifierList = ctx.identifierList().ast;
        TypeNode HasResultTypeopt = ctx.hasResultType().ast;
        Expr VariableInitializer = ctx.variableInitializer().ast;
        ctx.ast = new Object[] { pos(ctx), Identifier, IdentifierList, null, HasResultTypeopt, VariableInitializer };
    }

    @SuppressWarnings("unchecked")
    @Override
    public void exitLocalVariableDeclarationStatement(LocalVariableDeclarationStatementContext ctx) {
        // Check if this cast if correct
        ctx.ast = (List<Stmt>) ((Object) ctx.localVariableDeclaration().ast);
    }

    List<LocalDecl> localVariableDeclaration(List<Modifier> Modifiersopt, List<FlagsNode> VarKeyword, List<Object[]> VariableDeclarators) {
        List<Node> modifiers = checkVariableModifiers(Modifiersopt);
        FlagsNode fn = VarKeyword == null ? extractFlags(modifiers, Flags.FINAL) : extractFlags(modifiers, VarKeyword);
        List<LocalDecl> l = new TypedList<LocalDecl>(new LinkedList<LocalDecl>(), LocalDecl.class, false);
        for (Object[] o : VariableDeclarators) {
            Position pos = (Position) o[0];
            Position compilerGen = pos.markCompilerGenerated();
            Id name = (Id) o[1];
            if (name == null)
                name = nf.Id(pos, Name.makeFresh());
            @SuppressWarnings("unchecked")
            List<Id> exploded = (List<Id>) o[2];
            DepParameterExpr guard = (DepParameterExpr) o[3];
            TypeNode type = (TypeNode) o[4];
            if (type == null)
                type = nf.UnknownTypeNode((name != null ? name.position() : pos).markCompilerGenerated());
            Expr init = (Expr) o[5];
            LocalDecl ld = nf.LocalDecl(pos, fn, type, name, init, exploded);
            ld = (LocalDecl) ((X10Ext) ld.ext()).annotations(extractAnnotations(modifiers));
            int index = 0;
            l.add(ld);
            if (exploded.size() > 0 && init == null) {
                err.syntaxError("An exploded point must have an initializer.", pos);
            }
            FlagsNode efn = extractFlags(modifiers, Flags.FINAL); // exploded vars are always final
            for (Id id : exploded) {
                TypeNode tni = init == null ? nf.CanonicalTypeNode(compilerGen, ts.Int()) : // we infer the type of the exploded components, however if there is no init, then we
                                                                                            // just assume Int to avoid cascading errors.
                        explodedType(id.position()); // UnknownType
                l.add(nf.LocalDecl(id.position(), efn, tni, id,
                        init != null ? nf.ClosureCall(compilerGen, nf.Local(compilerGen, name), Collections.<Expr> singletonList(nf.IntLit(compilerGen, IntLit.INT, index))) : null));
                index++;
            }
        }
        return l;
    }

    @Override
    public void exitLocalVariableDeclaration0(LocalVariableDeclaration0Context ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        List<FlagsNode> VarKeyword = ctx.varKeyword().ast;
        List<Object[]> VariableDeclarators = ctx.variableDeclarators().ast;
        ctx.ast = localVariableDeclaration(Modifiersopt, VarKeyword, VariableDeclarators);
    }

    @Override
    public void exitLocalVariableDeclaration1(LocalVariableDeclaration1Context ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        List<FlagsNode> VarKeyword = null;
        List<Object[]> VariableDeclarators = ctx.variableDeclaratorsWithType().ast;
        ctx.ast = localVariableDeclaration(Modifiersopt, VarKeyword, VariableDeclarators);
    }

    @Override
    public void exitLocalVariableDeclaration2(LocalVariableDeclaration2Context ctx) {
        List<Modifier> Modifiersopt = ctx.modifiersopt().ast;
        List<FlagsNode> VarKeyword = ctx.varKeyword().ast;
        List<Object[]> VariableDeclarators = ctx.formalDeclarators().ast;
        ctx.ast = localVariableDeclaration(Modifiersopt, VarKeyword, VariableDeclarators);
    }

    @Override
    public void exitPrimary0(Primary0Context ctx) {
        ctx.ast = nf.Here(pos(ctx));
    }

    @Override
    public void exitPrimary1(Primary1Context ctx) {
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        Tuple tuple = nf.Tuple(pos(ctx), ArgumentListopt);
        ctx.ast = tuple;
    }

    @Override
    public void exitPrimary2(Primary2Context ctx) {
        ctx.ast = ctx.literal().ast;
    }

    @Override
    public void exitPrimary3(Primary3Context ctx) {
        ctx.ast = nf.Self(pos(ctx));
    }

    @Override
    public void exitPrimary4(Primary4Context ctx) {
        ctx.ast = nf.This(pos(ctx));
    }

    @Override
    public void exitPrimary5(Primary5Context ctx) {
        ParsedName ClassName = ctx.className().ast;
        ctx.ast = nf.This(pos(ctx), ClassName.toType());
    }

    @Override
    public void exitPrimary6(Primary6Context ctx) {
        Expr Expression = ctx.expression().ast;
        ctx.ast = nf.ParExpr(pos(ctx), Expression);
    }

    @Override
    public void exitPrimary7(Primary7Context ctx) {
        ParsedName TypeName = ctx.typeName().ast;
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ClassBody ClassBodyopt = ctx.classBodyopt().ast;
        if (ClassBodyopt == null) {
            ctx.ast = nf.X10New(pos(ctx), TypeName.toType(), TypeArgumentsopt, ArgumentListopt);
        } else {
            ctx.ast = nf.X10New(pos(ctx), TypeName.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt);
        }
    }

    @Override
    public void exitPrimary8(Primary8Context ctx) {
        Expr Primary = ctx.primary().ast;
        Id Identifier = ctx.identifier().ast;
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ClassBody ClassBodyopt = ctx.classBodyopt().ast;
        ParsedName b = new ParsedName(nf, ts, pos(ctx), Identifier);
        if (ClassBodyopt == null) {
            ctx.ast = nf.X10New(pos(ctx), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt);
        } else {
            ctx.ast = nf.X10New(pos(ctx), Primary, b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt);
        }
    }

    @Override
    public void exitPrimary9(Primary9Context ctx) {
        ParsedName FullyQualifiedName = ctx.fullyQualifiedName().ast;
        Id Identifier = ctx.identifier().ast;
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ClassBody ClassBodyopt = ctx.classBodyopt().ast;
        ParsedName b = new ParsedName(nf, ts, pos(ctx), Identifier);
        if (ClassBodyopt == null) {
            ctx.ast = nf.X10New(pos(ctx), FullyQualifiedName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt);
        } else {
            ctx.ast = nf.X10New(pos(ctx), FullyQualifiedName.toExpr(), b.toType(), TypeArgumentsopt, ArgumentListopt, ClassBodyopt);
        }
    }

    @Override
    public void exitPrimary10(Primary10Context ctx) {
        Expr Primary = ctx.primary().ast;
        Id Identifier = ctx.identifier().ast;
        ctx.ast = nf.Field(pos(ctx), Primary, Identifier);
    }

    @Override
    public void exitPrimary11(Primary11Context ctx) {
        Id Identifier = ctx.identifier().ast;
        ctx.ast = nf.Field(pos(ctx), nf.Super(pos(ctx.s)), Identifier);
    }

    @Override
    public void exitPrimary12(Primary12Context ctx) {
        ParsedName ClassName = ctx.className().ast;
        Id Identifier = ctx.identifier().ast;
        ctx.ast = nf.Field(pos(ctx), nf.Super(pos(ctx.s), ClassName.toType()), Identifier);
    }

    @Override
    public void exitPrimary13(Primary13Context ctx) {
        ParsedName MethodName = ctx.methodName().ast;
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = nf.X10Call(pos(ctx), MethodName.prefix == null ? null : MethodName.prefix.toReceiver(), MethodName.name, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary14(Primary14Context ctx) {
        Expr Primary = ctx.primary().ast;
        Id Identifier = ctx.identifier().ast;
        List<TypeNode> TypeArguments = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = nf.X10Call(pos(ctx), Primary, Identifier, TypeArguments, ArgumentListopt);
    }

    @Override
    public void exitPrimary15(Primary15Context ctx) {
        Id Identifier = ctx.identifier().ast;
        List<TypeNode> TypeArguments = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = nf.X10Call(pos(ctx), nf.Super(pos(ctx.s)), Identifier, TypeArguments, ArgumentListopt);
    }

    @Override
    public void exitPrimary16(Primary16Context ctx) {
        ParsedName ClassName = ctx.className().ast;
        Id Identifier = ctx.identifier().ast;
        List<TypeNode> TypeArguments = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = nf.X10Call(pos(ctx), nf.Super(pos(ctx.s), ClassName.toType()), Identifier, TypeArguments, ArgumentListopt);
    }

    @Override
    public void exitPrimary17(Primary17Context ctx) {
        Expr Primary = ctx.primary().ast;
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        if (Primary instanceof Field) {
            Field f = (Field) Primary;
            ctx.ast = nf.X10Call(pos(ctx), f.target(), f.name(), TypeArgumentsopt, ArgumentListopt);
        } else if (Primary instanceof AmbExpr) {
            AmbExpr f = (AmbExpr) Primary;
            ctx.ast = nf.X10Call(pos(ctx), null, f.name(), TypeArgumentsopt, ArgumentListopt);
        } else {
            ctx.ast = nf.ClosureCall(pos(ctx), Primary, TypeArgumentsopt, ArgumentListopt);
        }
    }

    @Override
    public void exitPrimary18(Primary18Context ctx) {
        ParsedName ClassName = ctx.className().ast;
        TypeNode Type = ctx.type().ast;
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        Name opName = Converter.operator_as;
        ctx.ast = nf.X10ConversionCall(pos(ctx), ClassName.toType(), nf.Id(pos(ctx.type()), opName), Type, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary19(Primary19Context ctx) {
        ParsedName ClassName = ctx.className().ast;
        TypeNode Type = ctx.type().ast;
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        Name opName = Converter.implicit_operator_as;
        ctx.ast = nf.X10ConversionCall(pos(ctx), ClassName.toType(), nf.Id(pos(ctx.type()), opName), Type, TypeArgumentsopt, ArgumentListopt);
    }


    private X10Call prefixOperatorInvocation(Position pos, Expr OperatorPrefix, List<TypeNode> TypeArgumentsopt, List<Expr> ArgumentListopt) {
        if (OperatorPrefix instanceof Field) {
            Field f = (Field) OperatorPrefix;
            return nf.X10Call(pos, f.target(), f.name(), TypeArgumentsopt, ArgumentListopt);
        } else if (OperatorPrefix instanceof AmbExpr) {
            AmbExpr f = (AmbExpr) OperatorPrefix;
            return nf.X10Call(pos, null, f.name(), TypeArgumentsopt, ArgumentListopt);
        } else {
            throw new InternalCompilerError("Invalid operator prefix", OperatorPrefix.position());
        }
    }

    @Override
    public void exitPrimary20(Primary20Context ctx) {
        Binary.Operator BinOp = ctx.binOp().ast;
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), null, nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary21(Primary21Context ctx) {
        ParsedName FullyQualifiedName = ctx.fullyQualifiedName().ast;
        Binary.Operator BinOp = ctx.binOp().ast;
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), FullyQualifiedName.toReceiver(), nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary22(Primary22Context ctx) {
        Expr Primary = ctx.primary().ast;
        Binary.Operator BinOp = ctx.binOp().ast;
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), Primary, nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary23(Primary23Context ctx) {
        Binary.Operator BinOp = ctx.binOp().ast;
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.s)), nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary24(Primary24Context ctx) {
        ParsedName ClassName = ctx.className().ast;
        Binary.Operator BinOp = ctx.binOp().ast;
        Name opName = X10Binary_c.binaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.s), ClassName.toType()), nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary25(Primary25Context ctx) {
        Binary.Operator BinOp = ctx.binOp().ast;
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), null, nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary26(Primary26Context ctx) {
        ParsedName FullyQualifiedName = ctx.fullyQualifiedName().ast;
        Binary.Operator BinOp = ctx.binOp().ast;
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), FullyQualifiedName.toReceiver(), nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary27(Primary27Context ctx) {
        Expr Primary = ctx.primary().ast;
        Binary.Operator BinOp = ctx.binOp().ast;
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), Primary, nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary28(Primary28Context ctx) {
        Binary.Operator BinOp = ctx.binOp().ast;
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.s)), nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary29(Primary29Context ctx) {
        ParsedName ClassName = ctx.className().ast;
        Binary.Operator BinOp = ctx.binOp().ast;
        Name opName = X10Binary_c.invBinaryMethodName(BinOp);
        if (opName == null) {
            err.syntaxError("Cannot invoke binary operator '" + BinOp + "'.", pos(ctx));
            opName = Name.make("invalid operator");
        }
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.s), ClassName.toType()), nf.Id(pos(ctx.binOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary30(Primary30Context ctx) {
        Name opName = ClosureCall.APPLY;
        Expr OperatorPrefix = nf.Field(pos(ctx), null, nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary31(Primary31Context ctx) {
        ParsedName FullyQualifiedName = ctx.fullyQualifiedName().ast;
        Name opName = ClosureCall.APPLY;
        Expr OperatorPrefix = nf.Field(pos(ctx), FullyQualifiedName.toReceiver(), nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary32(Primary32Context ctx) {
        Expr Primary = ctx.primary().ast;
        Name opName = ClosureCall.APPLY;
        Expr OperatorPrefix = nf.Field(pos(ctx), Primary, nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary33(Primary33Context ctx) {
        Name opName = ClosureCall.APPLY;
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.s)), nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary34(Primary34Context ctx) {
        ParsedName ClassName = ctx.className().ast;
        Name opName = ClosureCall.APPLY;
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.s), ClassName.toType()), nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary35(Primary35Context ctx) {
        Name opName = SettableAssign.SET;
        Expr OperatorPrefix = nf.Field(pos(ctx), null, nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary36(Primary36Context ctx) {
        ParsedName FullyQualifiedName = ctx.fullyQualifiedName().ast;
        Name opName = SettableAssign.SET;
        Expr OperatorPrefix = nf.Field(pos(ctx), FullyQualifiedName.toReceiver(), nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary37(Primary37Context ctx) {
        Expr Primary = ctx.primary().ast;
        Name opName = SettableAssign.SET;
        Expr OperatorPrefix = nf.Field(pos(ctx), Primary, nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary38(Primary38Context ctx) {
        Name opName = SettableAssign.SET;
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.s)), nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }

    @Override
    public void exitPrimary39(Primary39Context ctx) {
        ParsedName ClassName = ctx.className().ast;
        Name opName = SettableAssign.SET;
        Expr OperatorPrefix = nf.Field(pos(ctx), nf.Super(pos(ctx.s), ClassName.toType()), nf.Id(pos(ctx.parenthesisOp()), opName));
        List<TypeNode> TypeArgumentsopt = ctx.typeArgumentsopt().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        ctx.ast = prefixOperatorInvocation(pos(ctx), OperatorPrefix, TypeArgumentsopt, ArgumentListopt);
    }


    @Override
    public void exitIntLiteral(IntLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.INT);
    }

    @Override
    public void exitLongLiteral(LongLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.LONG);
    }

    @Override
    public void exitByteLiteral(ByteLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.BYTE);
    }

    @Override
    public void exitUnsignedByteLiteral(UnsignedByteLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.UBYTE);
    }

    @Override
    public void exitShortLiteral(ShortLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.SHORT);
    }

    @Override
    public void exitUnsignedShortLiteral(UnsignedShortLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.USHORT);
    }

    @Override
    public void exitUnsignedIntLiteral(UnsignedIntLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.UINT);
    }

    @Override
    public void exitUnsignedLongLiteral(UnsignedLongLiteralContext ctx) {
        ctx.ast = getIntLit(ctx, IntLit.ULONG);
    }

    @Override
    public void exitFloatingPointLiteral(FloatingPointLiteralContext ctx) {
        polyglot.lex.FloatLiteral a = float_lit(ctx);
        ctx.ast = nf.FloatLit(pos(ctx), FloatLit.FLOAT, a.getValue().floatValue());
    }

    @Override
    public void exitDoubleLiteral(DoubleLiteralContext ctx) {
        polyglot.lex.DoubleLiteral a = double_lit(ctx);
        ctx.ast = nf.FloatLit(pos(ctx), FloatLit.DOUBLE, a.getValue().doubleValue());
    }

    @Override
    public void exitLiteral10(Literal10Context ctx) {
        ctx.ast = ctx.booleanLiteral().ast;
    }

    @Override
    public void exitBooleanLiteral(BooleanLiteralContext ctx) {
        ctx.ast = nf.BooleanLit(pos(ctx), boolean_lit(ctx).getValue().booleanValue());
    }

    @Override
    public void exitCharacterLiteral(CharacterLiteralContext ctx) {
        ctx.ast = nf.CharLit(pos(ctx), char_lit(ctx).getValue().charValue());
    }

    @Override
    public void exitStringLiteral(StringLiteralContext ctx) {
        ctx.ast = nf.StringLit(pos(ctx), string_lit(ctx).getValue());
    }

    @Override
    public void exitNullLiteral(NullLiteralContext ctx) {
        ctx.ast = nf.NullLit(pos(ctx));
    }

    @Override
    public void exitArgumentList(ArgumentListContext ctx) {
        List<Expr> l = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        for (ExpressionContext e : ctx.expression()) {
            l.add(e.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitFieldAccess0(FieldAccess0Context ctx) {
        Expr Primary = ctx.primary().ast;
        Id Identifier = ctx.identifier().ast;
        ctx.ast = nf.Field(pos(ctx), Primary, Identifier);
    }

    @Override
    public void exitFieldAccess1(FieldAccess1Context ctx) {
        Id Identifier = ctx.identifier().ast;
        ctx.ast = nf.Field(pos(ctx), nf.Super(pos(ctx.s)), Identifier);
    }

    @Override
    public void exitFieldAccess2(FieldAccess2Context ctx) {
        ParsedName ClassName = ctx.className().ast;
        Id Identifier = ctx.identifier().ast;
        ctx.ast = nf.Field(pos(ctx), nf.Super(pos(ctx.s), ClassName.toType()), Identifier);
    }

    @Override
    public void exitConditionalExpression0(ConditionalExpression0Context ctx) {
        ctx.ast = ctx.castExpression().ast;
    }

    @Override
    public void exitConditionalExpression1(ConditionalExpression1Context ctx) {
        Expr PostfixExpression = ctx.conditionalExpression().ast;
        Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.PLUS_PLUS:
            op = Unary.POST_INC;
            break;
        case X10Parser.MINUS_MINUS:
            op = Unary.POST_DEC;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Unary(pos(ctx), PostfixExpression, op);
    }

    @Override
    public void exitConditionalExpression2(ConditionalExpression2Context ctx) {
        List<AnnotationNode> Annotations = ctx.annotations().ast;
        Expr UnannotatedUnaryExpression = ctx.conditionalExpression().ast;
        Expr e = UnannotatedUnaryExpression;
        e = (Expr) ((X10Ext) e.ext()).annotations(Annotations);
        ctx.ast = (Expr) e.position(pos(ctx));
    }

    @Override
    public void exitConditionalExpression3(ConditionalExpression3Context ctx) {
        Expr UnaryExpressionNotPlusMinus = ctx.conditionalExpression().ast;
        Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.PLUS:
            op = Unary.POS;
            break;
        case X10Parser.MINUS:
            op = Unary.NEG;
            break;
        case X10Parser.PLUS_PLUS:
            op = Unary.PRE_INC;
            break;
        case X10Parser.MINUS_MINUS:
            op = Unary.PRE_DEC;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Unary(pos(ctx), op, UnaryExpressionNotPlusMinus);
    }

    @Override
    public void exitConditionalExpression4(ConditionalExpression4Context ctx) {
        Expr UnaryExpressionNotPlusMinus = ctx.conditionalExpression().ast;
        Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.TWIDDLE:
            op = Unary.BIT_NOT;
            break;
        case X10Parser.NOT:
            op = Unary.NOT;
            break;
        case X10Parser.XOR:
            op = Unary.CARET;
            break;
        case X10Parser.OR:
            op = Unary.BAR;
            break;
        case X10Parser.AND:
            op = Unary.AMPERSAND;
            break;
        case X10Parser.MULTIPLY:
            op = Unary.STAR;
            break;
        case X10Parser.DIVIDE:
            op = Unary.SLASH;
            break;
        case X10Parser.REMAINDER:
            op = Unary.SLASH;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Unary(pos(ctx), op, UnaryExpressionNotPlusMinus);
    }

    @Override
    public void exitConditionalExpression5(ConditionalExpression5Context ctx) {
        Expr RangeExpression = ctx.e1.ast;
        Expr UnaryExpression = ctx.e2.ast;
        Expr regionCall = nf.Binary(pos(ctx), RangeExpression, Binary.DOT_DOT, UnaryExpression);
        ctx.ast = regionCall;
    }

    @Override
    public void exitConditionalExpression6(ConditionalExpression6Context ctx) {
        Expr MultiplicativeExpression = ctx.e1.ast;
        Expr RangeExpression = ctx.e2.ast;
        polyglot.ast.Binary.Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.MULTIPLY:
            op = Binary.MUL;
            break;
        case X10Parser.DIVIDE:
            op = Binary.DIV;
            break;
        case X10Parser.REMAINDER:
            op = Binary.MOD;
            break;
        case X10Parser.STARSTAR:
            op = Binary.STARSTAR;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Binary(pos(ctx), MultiplicativeExpression, op, RangeExpression);
    }

    @Override
    public void exitConditionalExpression7(ConditionalExpression7Context ctx) {
        Expr AdditiveExpression = ctx.e1.ast;
        Expr MultiplicativeExpression = ctx.e2.ast;
        polyglot.ast.Binary.Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.PLUS:
            op = Binary.ADD;
            break;
        case X10Parser.MINUS:
            op = Binary.SUB;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Binary(pos(ctx), AdditiveExpression, op, MultiplicativeExpression);
    }

    @Override
    public void exitConditionalExpression8(ConditionalExpression8Context ctx) {
        ctx.ast = ctx.hasZeroConstraint().ast;
    }

    @Override
    public void exitConditionalExpression9(ConditionalExpression9Context ctx) {
        ctx.ast = ctx.isRefConstraint().ast;
    }

    @Override
    public void exitConditionalExpression10(ConditionalExpression10Context ctx) {
        ctx.ast = ctx.subtypeConstraint().ast;
    }

    @Override
    public void exitConditionalExpression11(ConditionalExpression11Context ctx) {
        Expr expr1 = ctx.e1.ast;
        Expr expr2 = ctx.e2.ast;
        polyglot.ast.Binary.Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.LEFT_SHIFT:
            op = Binary.SHL;
            break;
        case X10Parser.RIGHT_SHIFT:
            op = Binary.SHR;
            break;
        case X10Parser.UNSIGNED_RIGHT_SHIFT:
            op = Binary.USHR;
            break;
        case X10Parser.ARROW:
            op = Binary.ARROW;
            break;
        case X10Parser.LARROW:
            op = Binary.LARROW;
            break;
        case X10Parser.FUNNEL:
            op = Binary.FUNNEL;
            break;
        case X10Parser.LFUNNEL:
            op = Binary.LFUNNEL;
            break;
        case X10Parser.NOT:
            op = Binary.BANG;
            break;
        case X10Parser.DIAMOND:
            op = Binary.DIAMOND;
            break;
        case X10Parser.BOWTIE:
            op = Binary.DIAMOND;
            break;
        default:
            op = null;
            assert false;
        }
        Expr call = nf.Binary(pos(ctx), expr1, op, expr2);
        ctx.ast = call;
    }

    @Override
    public void exitConditionalExpression12(ConditionalExpression12Context ctx) {
        Expr RelationalExpression = ctx.conditionalExpression().ast;
        TypeNode Type = ctx.type().ast;
        ctx.ast = nf.Instanceof(pos(ctx), RelationalExpression, Type);
    }

    @Override
    public void exitConditionalExpression13(ConditionalExpression13Context ctx) {
        Expr RelationalExpression = ctx.e1.ast;
        Expr ShiftExpression = ctx.e2.ast;
        polyglot.ast.Binary.Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.LESS:
            op = Binary.LT;
            break;
        case X10Parser.GREATER:
            op = Binary.GT;
            break;
        case X10Parser.LESS_EQUAL:
            op = Binary.LE;
            break;
        case X10Parser.GREATER_EQUAL:
            op = Binary.GE;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Binary(pos(ctx), RelationalExpression, op, ShiftExpression);
    }

    private static TypeContext isType(ParserRuleContext ctx) {
        if (ctx instanceof ConditionalExpression26Context) {
            return ((ConditionalExpression26Context) ctx).type();
        }
        if (ctx instanceof ConstantExpressionContext) {
            return isType(((ConstantExpressionContext) ctx).expression());
        }
        if (ctx instanceof ExpressionContext) {
            return isType(((ExpressionContext) ctx).assignmentExpression());
        }
        if (ctx instanceof AssignmentExpression1Context) {
            return isType(((AssignmentExpression1Context) ctx).conditionalExpression());
        }
        if (ctx instanceof ConditionalExpression0Context) {
            return isType(((ConditionalExpression0Context) ctx).castExpression());
        }
        if (ctx instanceof CastExpression0Context) {
            return isType(((CastExpression0Context) ctx).primary());
        }
        if (ctx instanceof Primary6Context) {
            return isType(((Primary6Context) ctx).expression());
        }
        return null;
    }


    @Override
    public void exitConditionalExpression14(ConditionalExpression14Context ctx) {
        TypeContext t1ctx = isType(ctx.e1);
        TypeContext t2ctx = isType(ctx.e2);
        if (t1ctx == null && t2ctx == null || ctx.op.getType() != X10Parser.EQUAL_EQUAL) {
            // Comparison between expressions
            Expr EqualityExpression = ctx.e1.ast;
            Expr RelationalExpression = ctx.e2.ast;
            polyglot.ast.Binary.Operator op;
            switch (ctx.op.getType()) {
            case X10Parser.EQUAL_EQUAL:
                op = Binary.EQ;
                break;
            case X10Parser.NOT_EQUAL:
                op = Binary.NE;
                break;
            default:
                op = null;
                assert false;
            }
            ctx.ast = nf.Binary(pos(ctx), EqualityExpression, op, RelationalExpression);
        } else {
            // Comparison between types
            TypeNode t1 = t1ctx.ast;
            TypeNode t2 = t2ctx.ast;
            ctx.ast = nf.SubtypeTest(pos(ctx), t1, t2, true);
        }
    }

    // @Override
    // public void exitConditionalExpression15(ConditionalExpression15Context ctx) {
    // TypeNode t1 = ctx.t1.ast;
    // TypeNode t2 = ctx.t2.ast;
    // ctx.ast = nf.SubtypeTest(pos(ctx), t1, t2, true);
    // }

    @Override
    public void exitConditionalExpression16(ConditionalExpression16Context ctx) {
        Expr EqualityExpression = ctx.e1.ast;
        Expr RelationalExpression = ctx.e2.ast;
        polyglot.ast.Binary.Operator op;
        switch (ctx.op.getType()) {
        case X10Parser.TWIDDLE:
            op = Binary.TWIDDLE;
            break;
        case X10Parser.NTWIDDLE:
            op = Binary.NTWIDDLE;
            break;
        default:
            op = null;
            assert false;
        }
        ctx.ast = nf.Binary(pos(ctx), EqualityExpression, op, RelationalExpression);
    }

    @Override
    public void exitConditionalExpression17(ConditionalExpression17Context ctx) {
        Expr AndExpression = ctx.e1.ast;
        Expr EqualityExpression = ctx.e2.ast;
        ctx.ast = nf.Binary(pos(ctx), AndExpression, Binary.BIT_AND, EqualityExpression);
    }

    @Override
    public void exitConditionalExpression18(ConditionalExpression18Context ctx) {
        Expr ExclusiveOrExpression = ctx.e1.ast;
        Expr AndExpression = ctx.e2.ast;
        ctx.ast = nf.Binary(pos(ctx), ExclusiveOrExpression, Binary.BIT_XOR, AndExpression);
    }

    @Override
    public void exitConditionalExpression19(ConditionalExpression19Context ctx) {
        Expr InclusiveOrExpression = ctx.e1.ast;
        Expr ExclusiveOrExpression = ctx.e2.ast;
        ctx.ast = nf.Binary(pos(ctx), InclusiveOrExpression, Binary.BIT_OR, ExclusiveOrExpression);
    }

    @Override
    public void exitConditionalExpression20(ConditionalExpression20Context ctx) {
        Expr ConditionalAndExpression = ctx.e1.ast;
        Expr InclusiveOrExpression = ctx.e2.ast;
        ctx.ast = nf.Binary(pos(ctx), ConditionalAndExpression, Binary.COND_AND, InclusiveOrExpression);
    }

    @Override
    public void exitConditionalExpression21(ConditionalExpression21Context ctx) {
        Expr ConditionalOrExpression = ctx.e1.ast;
        Expr ConditionalAndExpression = ctx.e2.ast;
        ctx.ast = nf.Binary(pos(ctx), ConditionalOrExpression, Binary.COND_OR, ConditionalAndExpression);
    }

    @Override
    public void exitConditionalExpression22(ConditionalExpression22Context ctx) {
        ctx.ast = ctx.closureExpression().ast;
    }

    @Override
    public void exitConditionalExpression23(ConditionalExpression23Context ctx) {
        ctx.ast = ctx.atExpression().ast;
    }

    @Override
    public void exitConditionalExpression24(ConditionalExpression24Context ctx) {
        ctx.ast = ctx.oBSOLETE_FinishExpression().ast;
    }

    @Override
    public void exitConditionalExpression25(ConditionalExpression25Context ctx) {
        Expr ConditionalOrExpression = ctx.e1.ast;
        Expr Expression = ctx.e2.ast;
        Expr ConditionalExpression = ctx.e3.ast;
        ctx.ast = nf.Conditional(pos(ctx), ConditionalOrExpression, Expression, ConditionalExpression);
    }

    private static boolean isEquality(ParserRuleContext ctx) {
        if (ctx instanceof ConditionalExpression14Context) {
            return true;
        }
        if (ctx instanceof ConstantExpressionContext) {
            return isEquality(ctx.getParent());
        }
        if (ctx instanceof ExpressionContext) {
            return isEquality(ctx.getParent());
        }
        if (ctx instanceof AssignmentExpression1Context) {
            return isEquality(ctx.getParent());
        }
        if (ctx instanceof ConditionalExpression0Context) {
            return isEquality(ctx.getParent());
        }
        if (ctx instanceof CastExpression0Context) {
            return isEquality(ctx.getParent());
        }
        if (ctx instanceof Primary6Context) {
            return isEquality(ctx.getParent());
        }
        return false;
    }

    @Override
    public void exitConditionalExpression26(ConditionalExpression26Context ctx) {
        if (isEquality(ctx.getParent())) {
            ctx.ast = null;
        } else {
            err.syntaxError("A type is not allowed as an expression", pos(ctx));
        }
    }

    @Override
    public void exitAssignmentExpression0(AssignmentExpression0Context ctx) {
        ctx.ast = ctx.assignment().ast;
    }

    @Override
    public void exitAssignmentExpression1(AssignmentExpression1Context ctx) {
        ctx.ast = ctx.conditionalExpression().ast;
    }

    @Override
    public void exitAssignment0(Assignment0Context ctx) {
        Expr LeftHandSide = ctx.leftHandSide().ast;
        Assign.Operator AssignmentOperator = ctx.assignmentOperator().ast;
        Expr AssignmentExpression = ctx.assignmentExpression().ast;
        ctx.ast = nf.Assign(pos(ctx), LeftHandSide, AssignmentOperator, AssignmentExpression);
    }

    @Override
    public void exitAssignment1(Assignment1Context ctx) {
        ParsedName e1 = ctx.expressionName().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        Assign.Operator AssignmentOperator = ctx.assignmentOperator().ast;
        Expr AssignmentExpression = ctx.assignmentExpression().ast;
        ctx.ast = nf.SettableAssign(pos(ctx), e1.toExpr(), ArgumentListopt, AssignmentOperator, AssignmentExpression);
    }

    @Override
    public void exitAssignment2(Assignment2Context ctx) {
        Expr e1 = ctx.primary().ast;
        List<Expr> ArgumentListopt = ctx.argumentListopt().ast;
        Assign.Operator AssignmentOperator = ctx.assignmentOperator().ast;
        Expr AssignmentExpression = ctx.assignmentExpression().ast;
        ctx.ast = nf.SettableAssign(pos(ctx), e1, ArgumentListopt, AssignmentOperator, AssignmentExpression);
    }

    @Override
    public void exitLeftHandSide0(LeftHandSide0Context ctx) {
        ParsedName ExpressionName = ctx.expressionName().ast;
        ctx.ast = ExpressionName.toExpr();
    }

    @Override
    public void exitLeftHandSide1(LeftHandSide1Context ctx) {
        ctx.ast = ctx.fieldAccess().ast;
    }

    @Override
    public void exitExpression(ExpressionContext ctx) {
        ctx.ast = ctx.assignmentExpression().ast;
    }

    @Override
    public void exitConstantExpression(ConstantExpressionContext ctx) {
        ctx.ast = ctx.expression().ast;
    }

    @Override
    public void exitAssignmentOperator0(AssignmentOperator0Context ctx) {
        ctx.ast = Assign.ASSIGN;
    }

    @Override
    public void exitAssignmentOperator1(AssignmentOperator1Context ctx) {
        ctx.ast = Assign.MUL_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator2(AssignmentOperator2Context ctx) {
        ctx.ast = Assign.DIV_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator3(AssignmentOperator3Context ctx) {
        ctx.ast = Assign.MOD_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator4(AssignmentOperator4Context ctx) {
        ctx.ast = Assign.ADD_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator5(AssignmentOperator5Context ctx) {
        ctx.ast = Assign.SUB_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator6(AssignmentOperator6Context ctx) {
        ctx.ast = Assign.SHL_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator7(AssignmentOperator7Context ctx) {
        ctx.ast = Assign.SHR_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator8(AssignmentOperator8Context ctx) {
        ctx.ast = Assign.USHR_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator9(AssignmentOperator9Context ctx) {
        ctx.ast = Assign.BIT_AND_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator10(AssignmentOperator10Context ctx) {
        ctx.ast = Assign.BIT_XOR_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator11(AssignmentOperator11Context ctx) {
        ctx.ast = Assign.BIT_OR_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator12(AssignmentOperator12Context ctx) {
        ctx.ast = Assign.DOT_DOT_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator13(AssignmentOperator13Context ctx) {
        ctx.ast = Assign.ARROW_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator14(AssignmentOperator14Context ctx) {
        ctx.ast = Assign.LARROW_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator15(AssignmentOperator15Context ctx) {
        ctx.ast = Assign.FUNNEL_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator16(AssignmentOperator16Context ctx) {
        ctx.ast = Assign.LFUNNEL_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator17(AssignmentOperator17Context ctx) {
        ctx.ast = Assign.STARSTAR_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator18(AssignmentOperator18Context ctx) {
        ctx.ast = Assign.DIAMOND_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator19(AssignmentOperator19Context ctx) {
        ctx.ast = Assign.BOWTIE_ASSIGN;
    }

    @Override
    public void exitAssignmentOperator20(AssignmentOperator20Context ctx) {
        ctx.ast = Assign.TWIDDLE_ASSIGN;
    }

    @Override
    public void exitPrefixOp0(PrefixOp0Context ctx) {
        ctx.ast = Unary.POS;
    }

    @Override
    public void exitPrefixOp1(PrefixOp1Context ctx) {
        ctx.ast = Unary.NEG;
    }

    @Override
    public void exitPrefixOp2(PrefixOp2Context ctx) {
        ctx.ast = Unary.NOT;
    }

    @Override
    public void exitPrefixOp3(PrefixOp3Context ctx) {
        ctx.ast = Unary.BIT_NOT;
    }

    @Override
    public void exitPrefixOp4(PrefixOp4Context ctx) {
        ctx.ast = Unary.CARET;
    }

    @Override
    public void exitPrefixOp5(PrefixOp5Context ctx) {
        ctx.ast = Unary.BAR;
    }

    @Override
    public void exitPrefixOp6(PrefixOp6Context ctx) {
        ctx.ast = Unary.AMPERSAND;
    }

    @Override
    public void exitPrefixOp7(PrefixOp7Context ctx) {
        ctx.ast = Unary.STAR;
    }

    @Override
    public void exitPrefixOp8(PrefixOp8Context ctx) {
        ctx.ast = Unary.SLASH;
    }

    @Override
    public void exitPrefixOp9(PrefixOp9Context ctx) {
        ctx.ast = Unary.PERCENT;
    }

    @Override
    public void exitBinOp0(BinOp0Context ctx) {
        ctx.ast = Binary.ADD;
    }

    @Override
    public void exitBinOp1(BinOp1Context ctx) {
        ctx.ast = Binary.SUB;
    }

    @Override
    public void exitBinOp2(BinOp2Context ctx) {
        ctx.ast = Binary.MUL;
    }

    @Override
    public void exitBinOp3(BinOp3Context ctx) {
        ctx.ast = Binary.DIV;
    }

    @Override
    public void exitBinOp4(BinOp4Context ctx) {
        ctx.ast = Binary.MOD;
    }

    @Override
    public void exitBinOp5(BinOp5Context ctx) {
        ctx.ast = Binary.BIT_AND;
    }

    @Override
    public void exitBinOp6(BinOp6Context ctx) {
        ctx.ast = Binary.BIT_OR;
    }

    @Override
    public void exitBinOp7(BinOp7Context ctx) {
        ctx.ast = Binary.BIT_XOR;
    }

    @Override
    public void exitBinOp8(BinOp8Context ctx) {
        ctx.ast = Binary.COND_AND;
    }

    @Override
    public void exitBinOp9(BinOp9Context ctx) {
        ctx.ast = Binary.COND_OR;
    }

    @Override
    public void exitBinOp10(BinOp10Context ctx) {
        ctx.ast = Binary.SHL;
    }

    @Override
    public void exitBinOp11(BinOp11Context ctx) {
        ctx.ast = Binary.SHR;
    }

    @Override
    public void exitBinOp12(BinOp12Context ctx) {
        ctx.ast = Binary.USHR;
    }

    @Override
    public void exitBinOp13(BinOp13Context ctx) {
        ctx.ast = Binary.GE;
    }

    @Override
    public void exitBinOp14(BinOp14Context ctx) {
        ctx.ast = Binary.LE;
    }

    @Override
    public void exitBinOp15(BinOp15Context ctx) {
        ctx.ast = Binary.GT;
    }

    @Override
    public void exitBinOp16(BinOp16Context ctx) {
        ctx.ast = Binary.LT;
    }

    @Override
    public void exitBinOp17(BinOp17Context ctx) {
        ctx.ast = Binary.EQ;
    }

    @Override
    public void exitBinOp18(BinOp18Context ctx) {
        ctx.ast = Binary.NE;
    }

    @Override
    public void exitBinOp19(BinOp19Context ctx) {
        ctx.ast = Binary.DOT_DOT;
    }

    @Override
    public void exitBinOp20(BinOp20Context ctx) {
        ctx.ast = Binary.ARROW;
    }

    @Override
    public void exitBinOp21(BinOp21Context ctx) {
        ctx.ast = Binary.LARROW;
    }

    @Override
    public void exitBinOp22(BinOp22Context ctx) {
        ctx.ast = Binary.FUNNEL;
    }

    @Override
    public void exitBinOp23(BinOp23Context ctx) {
        ctx.ast = Binary.LFUNNEL;
    }

    @Override
    public void exitBinOp24(BinOp24Context ctx) {
        ctx.ast = Binary.STARSTAR;
    }

    @Override
    public void exitBinOp25(BinOp25Context ctx) {
        ctx.ast = Binary.TWIDDLE;
    }

    @Override
    public void exitBinOp26(BinOp26Context ctx) {
        ctx.ast = Binary.NTWIDDLE;
    }

    @Override
    public void exitBinOp27(BinOp27Context ctx) {
        ctx.ast = Binary.BANG;
    }

    @Override
    public void exitBinOp28(BinOp28Context ctx) {
        ctx.ast = Binary.DIAMOND;
    }

    @Override
    public void exitBinOp29(BinOp29Context ctx) {
        ctx.ast = Binary.BOWTIE;
    }

    @Override
    public void exitHasResultTypeopt(HasResultTypeoptContext ctx) {
        ctx.ast = ctx.hasResultType() == null ? null : ctx.hasResultType().ast;
    }

    @Override
    public void exitTypeArgumentsopt(TypeArgumentsoptContext ctx) {
        if (ctx.typeArguments() == null) {
            ctx.ast = new TypedList<TypeNode>(new LinkedList<TypeNode>(), TypeNode.class, false);
        } else {
            ctx.ast = ctx.typeArguments().ast;
        }
    }

    @Override
    public void exitArgumentListopt(ArgumentListoptContext ctx) {
        if (ctx.argumentList() == null) {
            ctx.ast = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        } else {
            ctx.ast = ctx.argumentList().ast;
        }
    }

    @Override
    public void exitArgumentsopt(ArgumentsoptContext ctx) {
        if (ctx.arguments() == null) {
            ctx.ast = new TypedList<Expr>(new LinkedList<Expr>(), Expr.class, false);
        } else {
            ctx.ast = ctx.arguments().ast;
        }
    }

    @Override
    public void exitIdentifieropt(IdentifieroptContext ctx) {
        ctx.ast = ctx.identifier() == null ? null : ctx.identifier().ast;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void exitForInitopt(ForInitoptContext ctx) {
        if (ctx.forInit() == null) {
            ctx.ast = new TypedList<ForInit>(new LinkedList<ForInit>(), ForInit.class, false);
        } else {
            ctx.ast = (List<ForInit>) ctx.forInit().ast;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void exitForUpdateopt(ForUpdateoptContext ctx) {
        if (ctx.forUpdate() == null) {
            ctx.ast = new TypedList<ForUpdate>(new LinkedList<ForUpdate>(), ForUpdate.class, false);
        } else {
            ctx.ast = (List<ForUpdate>) ctx.forUpdate().ast;
        }
    }

    @Override
    public void exitExpressionopt(ExpressionoptContext ctx) {
        ctx.ast = ctx.expression() == null ? null : ctx.expression().ast;
    }

    @Override
    public void exitCatchesopt(CatchesoptContext ctx) {
        if (ctx.catches() == null) {
            ctx.ast = new TypedList<Catch>(new LinkedList<Catch>(), Catch.class, false);
        } else {
            ctx.ast = ctx.catches().ast;
        }
    }

    @Override
    public void exitBlockStatementsopt(BlockStatementsoptContext ctx) {
        if (ctx.blockStatements() == null) {
            ctx.ast = new TypedList<Stmt>(new LinkedList<Stmt>(), Stmt.class, false);
        } else {
            ctx.ast = ctx.blockStatements().ast;
        }
    }

    @Override
    public void exitClassBodyopt(ClassBodyoptContext ctx) {
        ctx.ast = ctx.classBody() == null ? null : ctx.classBody().ast;
    }

    @Override
    public void exitFormalParameterListopt(FormalParameterListoptContext ctx) {
        if (ctx.formalParameterList() == null) {
            ctx.ast = new TypedList<Formal>(new LinkedList<Formal>(), Formal.class, false);
        } else {
            ctx.ast = ctx.formalParameterList().ast;
        }
    }

}