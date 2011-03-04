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

package x10bc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

import polyglot.ast.Call;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassBody_c;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.FlagsNode;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
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
import polyglot.types.ConstructorDef;
import polyglot.types.Flags;
import polyglot.types.MethodDef;
import polyglot.types.MethodDef_c;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.PrimitiveType_c;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.UniqueID;

import x10.Configuration;
import x10.ast.Closure_c;
import x10.ast.ParExpr_c;
import x10.ast.X10New_c;
import x10.emitter.Emitter;
import x10.types.ClosureDef;
import x10.types.X10ClassDef;
import x10.types.X10ClassDef_c;
import x10.types.X10ClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.visit.X10InnerClassRemover;
import x10.visit.X10LocalClassRemover;

public class X10BytecodeTranslator extends BytecodeTranslator {

    static public class X10ClassTranslator extends ClassTranslator {

        class MethodNameAdapter extends MethodAdapter {
            MethodNameAdapter(MethodVisitor mv) {
                super(mv);
            }
            public AnnotationVisitor visitAnnotationDefault() {
                System.out.println("visAnnDef");
                return mv.visitAnnotationDefault();
            }
            public AnnotationVisitor visitAnnotation(String dsc, boolean vis) {
                System.out.println("visAnn "+dsc+"|"+vis);
                return mv.visitAnnotation(dsc, vis);
            }
            public AnnotationVisitor visitParameterAnnotation(int prm, String dsc, boolean vis) {
                System.out.println("visPrmAnn "+prm+"|"+dsc+"|"+vis);
                return mv.visitParameterAnnotation(prm, dsc, vis);
            }
            public void visitAttribute(Attribute att) {
                System.out.println("visAtt "+att);
                mv.visitAttribute(att);
            }
            public void visitCode() {
                System.out.println("visCod");
                mv.visitEnd();
            }
            public void visitFrame(int typ, int nLc, Object[] loc, int nSt, Object[] stk) {
                System.out.println("visFrm "+typ+"|"+nLc+"|"+loc+"|"+nSt+"|"+stk);
                mv.visitFrame(typ, nLc, loc, nSt, stk);
            }
            public void visitInsn(int opc) {
                System.out.println("visIns "+opc);
                mv.visitInsn(opc);
            }
            public void visitIntInsn(int opc, int opr) {
                System.out.println("visIntIns "+opc+"|"+opr);
                mv.visitIntInsn(opc, opr);
            }
            public void visitVarInsn(int opc, int var) {
                System.out.println("visVarIns "+opc+"|"+var);
                mv.visitVarInsn(opc, var);
            }
            public void visitTypeInsn(int opc, String dsc) {
                System.out.println("visTypIns "+opc+"|"+dsc);
                mv.visitTypeInsn(opc, dsc.replaceFirst("Test2", classNameOfSymbol(currentClass)));
            }
            public void visitFieldInsn(int opc, String own, String nam, String dsc) {
                System.out.println("visFldIns "+opc+"|"+own+"|"+nam+"|"+dsc);
                mv.visitFieldInsn(opc, own.replaceFirst("Test2", classNameOfSymbol(currentClass)),
                                  nam, dsc.replaceFirst("Test2", classNameOfSymbol(currentClass)));
            }
            public void visitMethodInsn(int opc, String own, String nam, String dsc) {
                System.out.println("visMthIns "+opc+"|"+own+"|"+nam+"|"+dsc);
                mv.visitMethodInsn(opc, own.replaceFirst("Test2", classNameOfSymbol(currentClass)),
                                   nam, dsc.replaceFirst("Test2", classNameOfSymbol(currentClass)));
            }
            public void visitJumpInsn(int opc, Label lab) {
                System.out.println("visJmpIns "+opc+"|"+lab);
                mv.visitJumpInsn(opc, lab);
            }
            public void visitLabel(Label lab) {
                System.out.println("visLab "+lab);
                mv.visitLabel(lab);
            }
            public void visitLdcInsn(Object cst) {
                System.out.println("visLdcIns "+cst);
                mv.visitLdcInsn(cst);
            }
            public void visitIincInsn(int var, int inc) {
                System.out.println("visIncIns "+var+"|"+inc);
                mv.visitIincInsn(var, inc);
            }
            public void visitTableSwitchInsn(int min, int max, Label dft, Label[] lbs) {
                System.out.println("visTSwIns "+min+"|"+max+"|"+dft+"|"+lbs);
                mv.visitTableSwitchInsn(min, max, dft, lbs);
            }
            public void visitLookupSwitchInsn(Label dft, int[] kys, Label[] lbs) {
                System.out.println("visLSwIns "+dft+"|"+kys+"|"+lbs);
                mv.visitLookupSwitchInsn(dft, kys, lbs);
            }
            public void visitMultiANewArrayInsn(String dsc, int dms) {
                System.out.println("visMAAIns "+dsc+"|"+dms);
                mv.visitMultiANewArrayInsn(dsc, dms);
            }
            public void visitTryCatchBlock(Label sta, Label end, Label hdl, String typ) {
                System.out.println("visTrCBlk "+sta+"|"+end+"|"+hdl+"|"+typ);
                mv.visitTryCatchBlock(sta, end, hdl, typ);
            }
            public void visitLocalVariable(String nam, String dsc, String sig, Label sta, Label end, int ndx) {
                System.out.println("visLocVar "+nam+"|"+dsc+"|"+sig+"|"+sta+"|"+end+"|"+ndx);
                mv.visitLocalVariable(nam, dsc, sig, sta, end, ndx);
            }
            public void visitLineNumber(int lin, Label sta) {
                System.out.println("visLinNum "+lin+"|"+sta);
                mv.visitLineNumber(lin, sta);
            }
            public void visitMaxs(int mxS, int mxL) {
                System.out.println("visMxs "+mxS+"|"+mxL);
                mv.visitMaxs(mxS, mxL);
            }
            public void visitEnd() {
                System.out.println("visEnd");
                mv.visitEnd();
            }
        }
        class ClassNameAdapter extends ClassAdapter {
            ClassNameAdapter(ClassVisitor cv) {
                super(cv);
            }
            public void visit(int ver, int acc, String nam, String sig, String supNam, String[] intfas) {
                System.out.println(ver+"|"+acc+"|"+nam+"|"+sig+"|"+supNam+"|"+intfas);
                cv.visit(ver, acc, nam.replaceFirst("Test2", classNameOfSymbol(currentClass)), sig, supNam, intfas);
            }
            public void visitSource(String sor, String dbg) {
                System.out.println("visSor "+sor+"|"+dbg);
                cv.visitSource(fileName(currentClass.position()), dbg);
            }
            public void visitOuterClass(String own, String nam, String dsc) {
                System.out.println("visOut "+own+"|"+nam+"|"+dsc);
                cv.visitOuterClass(own.replaceFirst("Test2", classNameOfSymbol(currentClass)), nam, dsc);
            }
            public AnnotationVisitor visitAnnotation(String dsc, boolean vis) {
                System.out.println("visAnn "+dsc+"|"+vis);
                return cv.visitAnnotation(dsc, vis);
            }
            public void visitAttribute(Attribute att) {
                System.out.println("visAtt "+att);
                cv.visitAttribute(att);
            }
            public void visitInnerClass(String nam, String outNam, String innNam, int acc) {
                System.out.println("visInn "+nam+"|"+outNam+"|"+innNam+"|"+acc);
                cv.visitInnerClass(nam.replaceFirst("Test2", classNameOfSymbol(currentClass)),
                                   outNam==null? outNam : outNam.replaceFirst("Test2", classNameOfSymbol(currentClass)),
                                   innNam, acc);
            }
            public FieldVisitor visitField(int acc, String nam, String dsc, String sig, Object val) {
                System.out.println("visFld "+acc+"|"+nam+"|"+dsc+"|"+sig+"|"+val);
                return cv.visitField(acc, nam, dsc.replaceFirst("Test2", classNameOfSymbol(currentClass)), sig, val);
            }
            public MethodVisitor visitMethod(int acc, String nam, String dsc, String sig, String[] xcp) {
                System.out.println("visMth "+acc+"|"+nam+"|"+dsc+"|"+sig+"|"+xcp);
                MethodVisitor mv = cv.visitMethod(acc, nam, dsc.replaceFirst("Test2", classNameOfSymbol(currentClass)), sig, xcp);
                if (mv != null) mv = new MethodNameAdapter(mv);
                return mv;
            }
            public void visitEnd() {
                System.out.println("visEnd");
                cv.visitEnd();
            }
        }

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

        public void visit(final MethodDecl n) {
            Flags flags = n.flags().flags();

            System.out.println("MethodDecl " + n.name().id().toString());
    		if (n.name().id().toString().equals("main")) {
                System.out.println(flags.isPublic());
                System.out.println(flags.isStatic());
                System.out.println(n.returnType().type().isVoid());
                System.out.println(n.formals().size() == 1);
                System.out.println(n.formals().get(0).declType().isSubtype(((X10TypeSystem) ts).Rail(ts.String()), ts.emptyContext()));
            if (flags.isPublic() && flags.isStatic() && n.returnType().type().isVoid() && n.formals().size() == 1 &&
                n.formals().get(0).declType().isSubtype(((X10TypeSystem) ts).Rail(ts.String()), ts.emptyContext()))
            {
                try {
                    String mname = Configuration.COMPILER_FRAGMENT_DATA_DIRECTORY + "Test2$Main.class";
                    ClassReader cr = new ClassReader(X10BytecodeTranslator.class.getClassLoader().getResourceAsStream(mname));
                    ClassWriter cw = new ClassWriter(cr, 0);
                    ClassNameAdapter ca = new ClassNameAdapter(cw);
                    cr.accept(ca, 0);
                    FileOutputStream fw = new FileOutputStream(classNameOfSymbol(currentClass) + "$Main.class");
                    fw.write(cw.toByteArray());
                    fw.close();
                } catch (IOException e) {
                    System.out.println("Can't find/write $Main");
                }
                try {
                    String mname = Configuration.COMPILER_FRAGMENT_DATA_DIRECTORY + "Test2$Main$1.class";
                    ClassReader cr = new ClassReader(X10BytecodeTranslator.class.getClassLoader().getResourceAsStream(mname));
                    ClassWriter cw = new ClassWriter(cr, 0);
                    ClassNameAdapter ca = new ClassNameAdapter(cw);
                    cr.accept(ca, 0);
                    FileOutputStream fw = new FileOutputStream(classNameOfSymbol(currentClass) + "$Main$1.class");
                    fw.write(cw.toByteArray());
                    fw.close();
                } catch (IOException e) {
                    System.out.println("Can't find/write $Main$1");
                }
                try {
                    String mname = Configuration.COMPILER_FRAGMENT_DATA_DIRECTORY + "Test2$Main$2.class";
                    ClassReader cr = new ClassReader(X10BytecodeTranslator.class.getClassLoader().getResourceAsStream(mname));
                    ClassWriter cw = new ClassWriter(cr, 0);
                    ClassNameAdapter ca = new ClassNameAdapter(cw);
                    cr.accept(ca, 0);
                    FileOutputStream fw = new FileOutputStream(classNameOfSymbol(currentClass) + "$Main$2.class");
                    fw.write(cw.toByteArray());
                    fw.close();
                } catch (IOException e) {
                    System.out.println("Can't find/write $Main$2");
                }
            }}
            genMethod(n.methodDef().flags(), n.methodDef(), n.methodDef().name().toString(), n.formals(),
                      Types.get(n.methodDef().returnType()), n.throwTypes(), n.body());
        }

        public ClassTranslator newClassTranslator(BytecodeTranslator bc, ClassDef cd) {
            return new X10ClassTranslator(job, ts, nf, bc, cd);
        }

        public ClassTranslator newClassTranslator(BytecodeTranslator bc, ClassDef cd, MethodContext context) {
            return new X10ClassTranslator(job, ts, nf, bc, cd, context);
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

        public void visit(final X10New_c n) {
            if (n.body() != null) {
                IClassGen cg = newClassTranslator(bc, n.anonType(), context).translateClass(n, n.body());
                context.cg().addInnerClass(cg);
	        }

	        alloc((ClassType) X10TypeMixin.baseType(n.type()), n.constructorInstance().formalTypes(), n.arguments(), n.position());
	    }

        public void visit(final Closure_c n) throws SemanticException {
            Position pos = n.position();
            ClosureDef cloDef = n.closureDef();
            System.out.println("Closure Type " + n.type());

            X10ClassDef clDef = new X10ClassDef_c(ts, null);

            Flags flags = Flags.PUBLIC.set(Flags.FINAL);
            MethodDef meDef = ts.methodDef(pos, Types.ref(clDef.asType()), flags, cloDef.returnType(), Name.make("apply"),
                                           cloDef.formalTypes(), cloDef.throwTypes());
            MethodDecl meDecl = nf.MethodDecl(pos, nf.FlagsNode(pos, flags), n.returnType(), nf.Id(pos, "apply"),
                                              n.formals(), n.throwTypes(), n.body());
            meDecl = meDecl.methodDef(meDef);

            String clName = UniqueID.newID("Anon");
            Id clId = nf.Id(pos, clName);
            TypeNode superClass = nf.CanonicalTypeNode(pos, ts.Object());
            polyglot.types.Type functype = n.type();
            assert (functype.isClass() && functype.toClass().interfaces().size() > 0);
            functype = functype.toClass().interfaces().get(0);
            TypeNode iface = nf.CanonicalTypeNode(pos, functype);
            List<TypeNode> interfaces = Collections.singletonList(iface);
            ClassBody body = nf.ClassBody(pos, Collections.singletonList((ClassMember) meDecl));
            ClassDecl clDecl = nf.ClassDecl(pos, nf.FlagsNode(pos, Flags.NONE), clId, superClass, interfaces, body);
            clDef.outer(Types.ref(context.currentClass));
            clDef.superType(superClass.typeRef());
            clDef.addInterface(Types.ref(functype));
            clDef.setFlags(Flags.NONE);
            clDef.kind(ClassDef.MEMBER);
            clDef.name(Name.make(clName));
            clDecl = clDecl.classDef(clDef);

            ConstructorDecl coDecl = nf.ConstructorDecl(pos, nf.FlagsNode(pos, Flags.PUBLIC), clId,
                                                        Collections.EMPTY_LIST, Collections.EMPTY_LIST, null);
            ConstructorDef coDef = ts.constructorDef(pos, Types.ref(clDef.asType()), Flags.NONE,
                                                     Collections.<Ref<? extends polyglot.types.Type>>emptyList(),
                                                     Collections.<Ref<? extends polyglot.types.Type>>emptyList());
            coDecl = coDecl.constructorDef(coDef);            
            ConstructorCall coCall = nf.SuperCall(pos, Collections.EMPTY_LIST);
            coCall = coCall.constructorInstance(ts.createConstructorInstance(pos, Types.ref(coDef)));
            coDecl = (ConstructorDecl) coDecl.body(nf.Block(pos, coCall));

            clDecl = clDecl.body(clDecl.body().addMember(coDecl));

            IClassGen cg = newClassTranslator(bc, clDef, context).translateClass(clDecl, clDecl.body());
            context.cg().addInnerClass(cg);

            alloc((ClassType) X10TypeMixin.baseType(clDef.asType()), Collections.<polyglot.types.Type>emptyList(),
                                                                     Collections.<Expr>emptyList(), pos);
	    }

        protected void promote(Expr n, polyglot.types.Type t) {
            visitExpr(n);
            System.out.println(il.currentStack().top());
            coerce(il.currentStack().top(), typeof(t), n.position());
        }

        protected void pushArguments(List<polyglot.types.Type> formalTypes, List<Expr> args) {
            assert formalTypes.size() == args.size();
            for (int i = 0; i < args.size(); i++) {
                polyglot.types.Type t = formalTypes.get(i);
                Expr arg = args.get(i);
                promote(arg, t);
            }
        }

        void promoteToPrimitive(Expr n, Type t) {
            visitExpr(n);
            coerce(il.currentStack().top(), t, n.position());
        }

        public void visit(final Call n) {
            System.out.println("visit Call " + n);
            if (n.target() instanceof Expr) {
                visitChild(n.target());
            }

            MethodInstance mi = n.methodInstance();
            System.out.println(mi.name().toString());

            if (mi.name().toString() == "operator+") {
                if (typeof(mi.container()).equals(Type.INT)) {
                    promoteToPrimitive(n.arguments().get(0), Type.INT);
                    promoteToPrimitive(n.arguments().get(1), Type.INT);
                    il.IADD(n.position());
                    return;
                }
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
                System.out.println(typeof(mi.container()) + " " + mi.name().toString() + " " + (argTypes.length>0?argTypes[0]:"") + (argTypes.length>1?argTypes[1]:""));
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
                System.out.println(typeof(mi.container()) + " " + mi.name().toString() + " " + (argTypes.length>0?argTypes[0]:"") + (argTypes.length>1?argTypes[1]:""));
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
            System.out.println("coerce " + currentTop + " to " + newTop);
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

    protected static Type typeFromPolyglotType(polyglot.types.Type t) {
        boolean boxPrimitives = false;
        boolean printGenerics = false;
        if (t.isArray())
            return Type.array(typeFromPolyglotType(t.toArray().base()));
        t = X10TypeMixin.baseType(t);
        if (t instanceof X10ClassType) {
            System.out.println("X10ClassType");
            X10ClassDef cd = ((X10ClassType) t).x10Def();
            String pat = getJavaRep(cd);
            if (pat != null) {
                String[] s = new String[] { "boolean", "byte", "char", "short", "int", "long", "float", "double" };
                String[] w = new String[] { "java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Short",
                                            "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double" };
                Type[] bcp = new Type[] { Type.BOOLEAN, Type.BYTE, Type.CHAR, Type.SHORT, Type.INT, Type.LONG, Type.FLOAT, Type.DOUBLE};
                for (int i = 0; i < s.length; i++) {
                    if (pat.equals(s[i])) {
                        System.out.println("primitive " + s[i]);
                        if (boxPrimitives) {
                            pat = w[i];
                            break;
                        } else {
                            System.out.println(cd + " -> " + bcp[i]);
                            return bcp[i];
                        }
                    }
                }
                if (! printGenerics) {
                    pat = pat.replaceAll("<.*>", "");
                }
                System.out.println(cd + " -> " + Type.typeFromClassName(pat));
                return Type.typeFromClassName(pat);
            }
            System.out.println("no @NativeRep");
        }
        Type bct = Type.typeFromPolyglotType(t);
        if (bct.desc().equals("Lx10/lang/VoidFun_0_0;")) {
            bct = Type.typeFromDescriptor("Lx10/core/fun/VoidFun_0_0;");
        }
        System.out.println(t + " => " + bct);
        return bct;
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
