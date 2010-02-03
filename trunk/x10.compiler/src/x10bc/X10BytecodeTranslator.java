package x10bc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Call;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.ast.Special;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.bytecode.rep.IClassGen;
import polyglot.bytecode.rep.ILabel;
import polyglot.bytecode.types.StackType;
import polyglot.bytecode.types.Type;
import polyglot.bytecode.AbstractTranslator;
import polyglot.bytecode.BranchTranslator;
import polyglot.bytecode.BytecodeTranslator;
import polyglot.bytecode.ClassTranslator;
import polyglot.bytecode.ExprTranslator;
import polyglot.bytecode.MethodContext;
import polyglot.bytecode.StmtTranslator;
import polyglot.dispatch.Dispatch;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

import x10.ast.ParExpr_c;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.visit.X10InnerClassRemover;
import x10.visit.X10LocalClassRemover;

public class X10BytecodeTranslator extends BytecodeTranslator {

    static public class X10ClassTranslator extends ClassTranslator {
        public X10ClassTranslator(Job job, TypeSystem ts, NodeFactory nf, BytecodeTranslator bc, ClassDef cd) {
            super(job, ts, nf, bc, cd);        	
        }

        public X10ClassTranslator(Job job, TypeSystem ts, NodeFactory nf, BytecodeTranslator bc, ClassDef cd, MethodContext context) {
            super(job, ts, nf, bc, cd, context);
        }

        public void visitChild(Node s, AbstractTranslator t) {
            System.out.println("visit " + s);
            new Dispatch.Dispatcher("visit").invoke(t, s);
        }

        protected Type typeFromPolyglotTypeV(polyglot.types.Type t) {
            return typeFromPolyglotType(t);
        }

        protected Type getSuperklass(final ClassDef sym) {
            System.out.println(sym.superType());
            if (sym.superType().toString().equals("x10.lang.Object")) {
                System.out.println("true");
                System.out.println(Type.typeFromClassName("x10.core.Ref"));
                return Type.typeFromClassName("x10.core.Ref");
            } else {
                System.out.println("false");
                polyglot.types.ClassType t = (ClassType) Types.get(sym.superType());
                assert ! t.flags().isInterface();
                System.out.println(typeFromPolyglotTypeV(t));
                return typeFromPolyglotTypeV(t);
            }
        }

        public ClassTranslator newClassTranslator(BytecodeTranslator bc, ClassDef cd) {
            return new X10ClassTranslator(job, ts, nf, bc, cd);
        }

		public ExprTranslator newExprTranslator(BytecodeTranslator bc, MethodContext context) {
			return new X10ExprTranslator(job, ts, nf, bc, context);
		}

		public StmtTranslator newStmtTranslator(BytecodeTranslator bc, MethodContext context) {
            return new X10StmtTranslator(job, ts, nf, bc, context);
		}
    }

    static public class X10StmtTranslator extends StmtTranslator {
        public X10StmtTranslator(Job job, TypeSystem ts, NodeFactory nf, BytecodeTranslator bc, MethodContext context) {
            super(job, ts, nf, bc, context);
        }

        public void visitChild(Node s, AbstractTranslator t) {
            System.out.println("visit " + s);
            new Dispatch.Dispatcher("visit").invoke(t, s);
        }

        protected Type typeFromPolyglotTypeV(polyglot.types.Type t) {
            return typeFromPolyglotType(t);
        }

        public void visit(final ConstructorCall n) {
            final int thisIndex = context.getThisIndex();

            // Call superclass constructors.
            il.ALOAD(thisIndex, context.localType(thisIndex), n.position());

            pushArguments(n.constructorInstance().formalTypes(), n.arguments());

            Type t;
            if (n.kind() == ConstructorCall.THIS) {
                System.out.println("THIS");
                t = typeFromPolyglotTypeV(context.currentClass.asType());
            } else if (context.currentClass.superType().toString().equals("x10.lang.Object")) {
                System.out.println("true");
                t = Type.typeFromClassName("x10.core.Ref");
            } else {
                System.out.println("false");
                t = typeFromPolyglotTypeV(Types.get(context.currentClass.superType()));
            }
            System.out.println(t);
            il.INVOKESPECIAL(t, "<init>", typeof(n.arguments()), Type.VOID, n.position());

            if (n.kind() == ConstructorCall.SUPER) {
                context.fieldInits.appendInstructions(il, context);
            }
        }

        public ClassTranslator newClassTranslator(BytecodeTranslator bc, ClassDef cd, MethodContext context) {
            return new X10ClassTranslator(job, ts, nf, bc, cd, context);
        }

        public StmtTranslator newStmtTranslator(BytecodeTranslator bc, MethodContext context) {
            return new X10StmtTranslator(job, ts, nf, bc, context);
        }

        public ExprTranslator newExprTranslator(BytecodeTranslator bc, MethodContext context) {
            return new X10ExprTranslator(job, ts, nf, bc, context);
        }

        public BranchTranslator newBranchTranslator(BytecodeTranslator bc, MethodContext context, ILabel branchTarget_, boolean branchOnTrue_) {
            return new X10BranchTranslator(job, ts, nf, bc, context, branchTarget_, branchOnTrue_);
        }
    }

    static public class X10ExprTranslator extends ExprTranslator {
        public X10ExprTranslator(Job job, TypeSystem ts, NodeFactory nf, BytecodeTranslator bc, MethodContext context) {
            super(job, ts, nf, bc, context);        	
        }

        public void visitChild(Node s, AbstractTranslator t) {
            System.out.println("visit " + s);
            new Dispatch.Dispatcher("visit").invoke(t, s);
        }

        protected Type typeFromPolyglotTypeV(polyglot.types.Type t) {
            return typeFromPolyglotType(t);
        }

		public void visit(ParExpr_c n) { visitExpr(n.expr()); }

        void promoteToPrimitive(Expr n, Type t) {
            visitExpr(n);
            coerce(typeof(n), t, n.position());
        }

        public void visit(final Call n) {
            System.out.println("visit Call " + n);
            if (n.target() instanceof Expr) {
                visitChild(n.target());
            }

            MethodInstance mi = n.methodInstance();
            System.out.println(mi.name().toString());

            if (mi.name().toString() == "operator+") {
                if (typeof(mi.container()).equals(Type.typeFromDescriptor("Ljava/lang/Integer;"))) {
                    promoteToPrimitive(n.arguments().get(0), Type.INT);
                    promoteToPrimitive(n.arguments().get(1), Type.INT);
                    il.IADD(n.position());
//                  n = n.methodInstance(mi.returnType(ts.Int()));
                    return;
                }
            }

            pushArguments(mi.formalTypes(), n.arguments());

            if (n.target() instanceof Special && ((Special) n.target()).kind() == Special.SUPER) {
                il.INVOKESPECIAL(typeof(mi.container()), mi.name().toString(), typeofTypes(mi.formalTypes()), typeof(mi.returnType()), n.position());
            }
            else if (n.target() instanceof TypeNode) {
                Type[] argTypes = typeofTypes(mi.formalTypes());
                System.out.println(typeof(mi.container()) + " " + mi.name().toString() + " " + argTypes[0] + (argTypes.length>1?argTypes[1]:""));
                if (typeof(mi.container()).equals(Type.STRING)) {
                    System.out.println("true1");
                    if (mi.name().toString().equals("valueOf")) {
                        System.out.println("true2");
                        if (argTypes[0].isObject()) {
                            System.out.println("true3");
                            System.out.println(il.currentStack().top());
                            if (il.currentStack().top().isObject()) {
                                argTypes[0] = Type.OBJECT;
                            } else {
                                System.out.println("true4");
                                argTypes[0] = il.currentStack().top();
                            }
                        }
                    }
                }
                il.INVOKESTATIC(typeof(mi.container()), mi.name().toString(), argTypes, typeof(mi.returnType()), n.position());
            }
            else if (mi.container().isClass() && mi.container().toClass().flags().isInterface()) {
                il.INVOKEINTERFACE(typeof(mi.container()), mi.name().toString(), typeofTypes(mi.formalTypes()), typeof(mi.returnType()), n.position());
            }
            else {
                il.INVOKEVIRTUAL(typeof(mi.container()), mi.name().toString(), typeofTypes(mi.formalTypes()), typeof(mi.returnType()), n.position());
            }
        }

        public void stringify(final Expr n) {
            visitChild(n);
            try {
                TypeSystem ts = n.type().typeSystem();
                MethodInstance mi = ts.findMethod(ts.String(), ts.MethodMatcher(ts.String(), Name.make("valueOf"), Collections.singletonList(n.type()), ts.emptyContext()));
                coerce(typeof(n.type()), typeof(mi.formalTypes().get(0)), n.position());
                Type[] argTypes = typeofTypes(mi.formalTypes());
                System.out.println(typeof(mi.container()) + " " + mi.name().toString() + " " + argTypes[0] + (argTypes.length>1?argTypes[1]:""));
                if (typeof(mi.container()).equals(Type.STRING)) {
                    System.out.println("true1");
                    if (mi.name().toString().equals("valueOf")) {
                        System.out.println("true2");
                        if (argTypes[0].isObject()) {
                            System.out.println("true3");
                            argTypes[0] = Type.OBJECT;
                        }
                    }
                }
                il.INVOKESTATIC(typeof(mi.container()), mi.name().toString(), argTypes, typeof(mi.returnType()), n.position());
            }
            catch (SemanticException e) {
                throw new InternalCompilerError(e);
            }
        }

        protected void coerce(final Type currentTop, final Type newTop, final Position pos) {
            coerce(il, currentTop, newTop, pos);
        }

        public ClassTranslator newClassTranslator(BytecodeTranslator bc, ClassDef cd, MethodContext context) {
            return new X10ClassTranslator(job, ts, nf, bc, cd, context);
        }

        public ExprTranslator newExprTranslator(BytecodeTranslator bc, MethodContext context) {
            return new X10ExprTranslator(job, ts, nf, bc, context);
        }

        public BranchTranslator newBranchTranslator(BytecodeTranslator bc, MethodContext context, ILabel branchTarget_, boolean branchOnTrue_) {
            return new X10BranchTranslator(job, ts, nf, bc, context, branchTarget_, branchOnTrue_);
        }
    }

    static public class X10BranchTranslator extends BranchTranslator {
        public X10BranchTranslator(Job job, TypeSystem ts, NodeFactory nf, BytecodeTranslator bc, MethodContext context, ILabel branchTarget_, boolean branchOnTrue_) {
            super(job, ts, nf, bc, context, branchTarget_, branchOnTrue_);        	
        }

        public void visitChild(Node s, AbstractTranslator t) {
            System.out.println("visit " + s);
            new Dispatch.Dispatcher("visit").invoke(t, s);
        }

        public ExprTranslator newExprTranslator(BytecodeTranslator bc, MethodContext context) {
            return new X10ExprTranslator(job, ts, nf, bc, context);
        }

        public BranchTranslator newBranchTranslator(BytecodeTranslator bc, MethodContext context, ILabel branchTarget_, boolean branchOnTrue_) {
            return new X10BranchTranslator(job, ts, nf, bc, context, branchTarget_, branchOnTrue_);
        }
    }

    public X10BytecodeTranslator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    private static Type typeFromPolyglotType(polyglot.types.Type t) {
        boolean boxPrimitives = true;
        if (t.isArray())
            return Type.array(typeFromPolyglotType(t.toArray().base()));
        t = X10TypeMixin.baseType(t);
        if (t instanceof X10ClassType) {
            System.out.println("X10ClassType");
            X10ClassDef cd = ((X10ClassType) t).x10Def();
            String pat = getJavaRep(cd);
            if (pat != null) {
                if (boxPrimitives) {
                    String[] s = new String[] { "boolean", "byte", "char", "short", "int", "long", "float", "double" };
                    String[] w = new String[] { "java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double" };
                    for (int i = 0; i < s.length; i++) {
                        if (pat.equals(s[i])) {
                            System.out.println("primitive " + s[i]);
                            pat = w[i];
                            break;
                        }
                    }
                }
                System.out.println(cd + " -> " + Type.typeFromClassName(pat));
                return Type.typeFromClassName(pat);
            }
            System.out.println("no @NativeRep");
        }
        System.out.println(t + " => " + Type.typeFromPolyglotType(t));
        return Type.typeFromPolyglotType(t);
    }

    private static String getJavaRep(X10ClassDef def) {
        return getJavaRepParam(def, 1);
    }

    private static String getJavaRTTRep(X10ClassDef def) {
        return getJavaRepParam(def, 3);
    }

    private static String getJavaRepParam(X10ClassDef def, int i) {
        try {
            X10TypeSystem xts = (X10TypeSystem) def.typeSystem();
            polyglot.types.Type rep = (polyglot.types.Type) xts.systemResolver().find(QName.make("x10.compiler.NativeRep"));
            List<polyglot.types.Type> as = def.annotationsMatching(rep);
            for (polyglot.types.Type at : as) {
                assertNumberOfInitializers(at, 4);
                String lang = getPropertyInit(at, 0);
                if (lang != null && lang.equals("java")) {
                    return getPropertyInit(at, i);
                }
            }
        }
        catch (SemanticException e) {}
        return null;
    }

    private static String getPropertyInit(polyglot.types.Type at, int index) {
        at = X10TypeMixin.baseType(at);
        if (at instanceof X10ClassType) {
            X10ClassType act = (X10ClassType) at;
            if (index < act.propertyInitializers().size()) {
                Expr e = act.propertyInitializer(index);
                if (e.isConstant()) {
                    Object v = e.constantValue();
                    if (v instanceof String) {
                        return (String) v;
                    }
                }
            }
        }
        return null;
    }

    private static void assertNumberOfInitializers(polyglot.types.Type at, int len) {
        at = X10TypeMixin.baseType(at);
        if (at instanceof X10ClassType) {
            X10ClassType act = (X10ClassType) at;
            assert len == act.propertyInitializers().size();
        }
    }

    public void visit(SourceFile n) {
        n = (SourceFile) n.visit(new X10LocalClassRemover(job, ts, nf).context(ts.emptyContext()));
        n = (SourceFile) n.visit(new X10InnerClassRemover(job, ts, nf).context(ts.emptyContext()));
        
        for (TopLevelDecl d : n.decls()) {
            if (d instanceof ClassDecl) {
                ClassDecl cd = (ClassDecl) d;
                ClassDef def = cd.classDef();
                ClassBody body = cd.body();
                IClassGen cg = newClassTranslator(this, def).translateClass(cd, body);
                genClass(n, Types.get(def.package_()), cg);
            }
        }
    }
    
    public ClassTranslator newClassTranslator(BytecodeTranslator bc, ClassDef cd) {
        return new X10ClassTranslator(job, ts, nf, bc, cd);
    }
}
