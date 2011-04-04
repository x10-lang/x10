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

package x10.visit;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.ast.Allocation_c;
import polyglot.ast.Assert_c;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Block_c;
import polyglot.ast.Branch_c;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Case_c;
import polyglot.ast.Catch;
import polyglot.ast.Catch_c;
import polyglot.ast.Conditional_c;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Empty_c;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldAssign_c;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.FlagsNode_c;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.Id_c;
import polyglot.ast.If_c;
import polyglot.ast.Import_c;
import polyglot.ast.IntLit_c;
import polyglot.ast.Labeled_c;
import polyglot.ast.Lit;
import polyglot.ast.Lit_c;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.Local_c;
import polyglot.ast.Loop_c;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Node_c;
import polyglot.ast.PackageNode_c;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.Return_c;
import polyglot.ast.SourceFile;
import polyglot.ast.Special;
import polyglot.ast.Special_c;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit_c;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Throw_c;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.Try_c;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.frontend.Source;
import polyglot.types.ClassDef;
import polyglot.types.ConstructorDef;
import polyglot.types.ContainerType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.JavaArrayType;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarInstance;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.StringUtil;
import polyglot.visit.ContextVisitor;
import polyglot.visit.InnerClassRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.Translator;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.ast.AssignPropertyCall_c;
import x10.ast.ClosureCall;
import x10.ast.ClosureCall_c;
import x10.ast.Closure_c;
import x10.ast.ForLoop_c;
import x10.ast.HasZeroTest_c;
import x10.ast.LocalTypeDef_c;
import x10.ast.ParExpr;
import x10.ast.ParExpr_c;
import x10.ast.PropertyDecl;
import x10.ast.SettableAssign;
import x10.ast.SettableAssign_c;
import x10.ast.StmtExpr_c;
import x10.ast.StmtSeq_c;
import x10.ast.SubtypeTest_c;
import x10.ast.Tuple_c;
import x10.ast.TypeDecl_c;
import x10.ast.TypeParamNode;
import x10.ast.TypeParamNode_c;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10Cast_c;
import x10.ast.X10ClassBody_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ConstructorCall_c;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10Formal;
import x10.ast.X10Initializer_c;
import x10.ast.X10Instanceof_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10New;
import x10.ast.X10New_c;
import x10.ast.X10ProcedureCall;
import x10.ast.X10Special;
import x10.ast.X10Unary_c;
import x10.emitter.Emitter;
import x10.emitter.Expander;
import x10.emitter.Inline;
import x10.emitter.Join;
import x10.emitter.Loop;
import x10.emitter.RuntimeTypeExpander;
import x10.emitter.Template;
import x10.emitter.TryCatchExpander;
import x10.emitter.TypeExpander;
import x10.types.ConstrainedType;
import x10.types.FunctionType;
import x10.types.MethodInstance;
import x10.types.MethodInstance_c;
import x10.types.ParameterType;
import x10.types.ParameterType.Variance;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldInstance;
import x10.types.X10ParsedClassType_c;
import x10.types.constraints.SubtypeConstraint;
import x10.util.CollectionFactory;
import x10.util.HierarchyUtils;
import x10c.ast.X10CBackingArrayAccessAssign_c;
import x10c.ast.X10CBackingArrayAccess_c;
import x10c.ast.X10CBackingArrayNewArray_c;
import x10c.ast.X10CBackingArray_c;
import x10c.types.X10CContext_c;
import x10c.visit.ClosureRemover;

/**
 * Visitor on the AST nodes that for some X10 nodes triggers the template based
 * dumping mechanism (and for all others just defaults to the normal pretty
 * printing).
 * 
 * @author Christian Grothoff
 * @author Igor Peshansky (template classes)
 * @author Rajkishore Barik 26th Aug 2006 (added loop optimizations)
 * @author vj Refactored Emitter out.
 */
public class X10PrettyPrinterVisitor extends X10DelegatingVisitor {

    public static final String JAVA_LANG_OBJECT = "java.lang.Object";
    public static final String JAVA_LANG_CLASS = "java.lang.Class";
    public static final String JAVA_IO_SERIALIZABLE = "java.io.Serializable";
    public static final String JAVA_LANG_SYSTEM = "java.lang.System";

    public static final int PRINT_TYPE_PARAMS = 1;
    public static final int BOX_PRIMITIVES = 2;
    public static final int NO_VARIANCE = 4;
    public static final int NO_QUALIFIER = 8;

    public static final boolean isSelfDispatch = true;
    public static final boolean isGenericOverloading = true;
    // TODO workaround for XTENLANG-2625
    public static final boolean supportConstructorSplitting = false;

    public static final String X10_FUN_PACKAGE = "x10.core.fun";
    public static final String X10_FUN_CLASS_NAME_PREFIX = "Fun";
    public static final String X10_VOIDFUN_CLASS_NAME_PREFIX = "VoidFun";
    public static final String X10_FUN_CLASS_PREFIX = X10_FUN_PACKAGE+"."+X10_FUN_CLASS_NAME_PREFIX;
    public static final String X10_VOIDFUN_CLASS_PREFIX = X10_FUN_PACKAGE+"."+X10_VOIDFUN_CLASS_NAME_PREFIX;
    public static final String X10_CORE_STRING = "x10.core.String";
    public static final String X10_RUNTIME_TYPE_CLASS = "x10.rtt.Type";
    public static final String X10_RTT_TYPES = "x10.rtt.Types";
    public static final String X10_RUNTIME_CLASS = "x10.runtime.impl.java.Runtime";
    public static final String X10_RUNTIME_UTIL_UTIL = "x10.runtime.util.Util";

    public static final String MAIN_CLASS = "$Main";
    public static final String RTT_NAME = "$RTT";
    public static final String GETRTT_NAME = "$getRTT";
    public static final String GETPARAM_NAME = "$getParam";
    public static final String CONSTRUCTOR_METHOD_NAME = "$init";
    public static final String CREATION_METHOD_NAME = "$make";

    private static int nextId_;

    final public CodeWriter w;
    final public Translator tr;
    final public Emitter er;

    final private Configuration config;

    public X10PrettyPrinterVisitor(CodeWriter w, Translator tr) {
        this.w = w;
        this.tr = tr;
        this.er = new Emitter(w, tr);
        this.config = ((ExtensionInfo) tr.job().extensionInfo()).getOptions().x10_config;
    }

    /* to provide a unique name for local variables introduce in the templates */
    private static int getUniqueId_() {
        return nextId_++;
    }

    public static Name getId() {
        return Name.make("$var" + getUniqueId_());
    }

    @Override
    public void visit(Node n) {

        // invoke appropriate visit method for Java backend's specific nodes
        if (n instanceof X10CBackingArray_c) {
            visit((X10CBackingArray_c) n);
            return;
        }
        if (n instanceof X10CBackingArrayAccess_c) {
            visit((X10CBackingArrayAccess_c) n);
            return;
        }
        if (n instanceof X10CBackingArrayAccessAssign_c) {
            visit((X10CBackingArrayAccessAssign_c) n);
            return;
        }
        if (n instanceof X10CBackingArrayNewArray_c) {
            visit((X10CBackingArrayNewArray_c) n);
            return;
        }

        if (n instanceof FlagsNode_c) {
            visit((FlagsNode_c) n);
            return;
        }
        if (n instanceof TypeParamNode_c) {
            visit((TypeParamNode_c) n);
            return;
        }
        if (n instanceof X10Initializer_c) {
            visit((X10Initializer_c) n);
            return;
        }

        tr.job().compiler().errorQueue()
                .enqueue(ErrorInfo.SEMANTIC_ERROR, "Unhandled node type: " + n.getClass(), n.position());

        // Don't call through del; that would be recursive.
        n.translate(w, tr);
    }

    // ///////////////////////////////////////
    // handle Java backend's specific nodes
    // ///////////////////////////////////////

    public void visit(X10CBackingArray_c n) {
        // TODO:CAST
        w.write("(");
        w.write("(");
        JavaArrayType arrayType = (JavaArrayType) n.type();
        er.printType(arrayType.base(), 0);
        w.write("[]");
        w.write(")");
        er.prettyPrint(n.container(), tr);
        w.write(".");
        w.write("value");
        w.write(")");
    }

    public void visit(X10CBackingArrayAccess_c n) {
        // TODO:CAST
        w.write("(");
        w.write("(");
        er.printType(n.type(), PRINT_TYPE_PARAMS);
        w.write(")");

        er.prettyPrint(n.array(), tr);
        w.write("[");
        er.prettyPrint(n.index(), tr);
        w.write("]");
        w.write(")");
    }

    public void visit(X10CBackingArrayAccessAssign_c n) {
        er.prettyPrint(n.array(), tr);
        w.write("[");
        er.prettyPrint(n.index(), tr);
        w.write("]");

        w.write(n.operator().toString());

        er.prettyPrint(n.right(), tr);
    }

    public void visit(X10CBackingArrayNewArray_c n) {
        Type base = ((JavaArrayType) n.type()).base();
        if (base instanceof ParameterType) {
            w.write("(");
            er.printType(n.type(), 0);
            w.write(")");
            w.write(" ");
            new RuntimeTypeExpander(er, base).expand();
            w.write(".makeArray(");
            w.write(n.dims().get(0).toString());
            w.write(")");
            return;
        }
        w.write("new ");
        er.printType(((JavaArrayType) n.type()).base(), 0);
        for (Expr dim : n.dims()) {
            w.write("[");
            er.prettyPrint(dim, tr);
            w.write("]");
        }
        for (int i = 0; i < n.additionalDims(); i++)
            w.write("[]");
    }

    public void visit(FlagsNode_c n) {
        n.translate(w, tr);
    }

    public void visit(TypeParamNode_c n) {
        n.translate(w, tr);
    }

    public void visit(X10Initializer_c n) {
        w.write("static ");
        n.printBlock(n.body(), w, tr);
    }

    // ///////////////////////////////////////
    // handle X10 nodes
    // ///////////////////////////////////////

    @Override
    public void visit(Import_c c) {
        // don't generate any code at all--we should fully qualify all type
        // names
    }

    @Override
    public void visit(PackageNode_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(X10ClassBody_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(X10ClassDecl_c n) {
        String className = n.classDef().name().toString();
        X10CContext_c context = (X10CContext_c) tr.context();
        if (n.classDef().isTopLevel() && !n.classDef().sourceFile().name().equals(className + ".x10")
                && !context.containsGeneratedClasses(n.classDef())) {
            context.addGeneratedClasses(n.classDef());
            // not include import
            SourceFile sf = tr.nodeFactory().SourceFile(n.position(), Collections.<TopLevelDecl> singletonList(n));
            if (n.classDef().package_() != null) {
                sf = sf.package_(tr.nodeFactory().PackageNode(n.position(), n.classDef().package_()));
            }
            sf = sf.source(new Source(n.classDef().name().toString() + ".x10", n.position().path(), null));
            tr.translate(sf);
            return;
        }
        TypeSystem xts = tr.typeSystem();
        X10ClassDef def = n.classDef();

        // Do not generate code if the class is represented natively.
        if (Emitter.getJavaRep(def) != null) {
            w.write(";");
            w.newline();
            return;
        }

        Flags flags = n.flags().flags();

        w.begin(0);
        if (flags.isInterface()) {
            w.write(flags.clearInterface().clearAbstract().translateJava());
        } else {
            w.write(flags.translateJava());
        }

        if (flags.isInterface()) {
            w.write("interface ");
        } else {
            w.write("class ");
        }

        tr.print(n, n.name(), w);

        List<TypeParamNode> typeParameters = n.typeParameters();
        if (typeParameters.size() > 0) {
            er.printTypeParams(n, context, typeParameters);
        }

        final TypeNode superClassNode = n.superClass();
        if (superClassNode != null || flags.isStruct()) {
            w.allowBreak(0);
            w.write("extends ");
            if (flags.isStruct()) {
                assert superClassNode == null : superClassNode;
                w.write("x10.core.Struct");
            } else {
                assert superClassNode != null;
                Type superType = superClassNode.type();
                // FIXME: HACK! If a class extends x10.lang.Object, swipe in
                // x10.core.Ref
                if (!xts.typeEquals(superType, xts.Object(), context))
                    er.printType(superType, PRINT_TYPE_PARAMS | BOX_PRIMITIVES | NO_VARIANCE);
                else
                    w.write("x10.core.Ref");
            }
        }

        // Filter out x10.lang.Any from the interfaces.
        List<TypeNode> interfaces = new ArrayList<TypeNode>();

        for (TypeNode tn : n.interfaces()) {
            if (!xts.isAny(tn.type())) {
                interfaces.add(tn);
            }
        }
        /*
         * Interfaces automatically extend Any if
         * (n.flags().flags().isInterface() && interfaces.isEmpty()) {
         * 
         * X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
         * interfaces.add(tr.nodeFactory().CanonicalTypeNode(n.position(),
         * ts.Any())); }
         */
        List<Type> alreadyPrintedTypes = new ArrayList<Type>();
        if (!interfaces.isEmpty()) {
            w.allowBreak(2);
            if (flags.isInterface()) {
                w.write("extends ");
            } else {
                w.write("implements ");
            }

            w.begin(0);
            for (Iterator<TypeNode> i = interfaces.iterator(); i.hasNext();) {
                TypeNode tn = i.next();
                if (!isSelfDispatch || (isSelfDispatch && !er.alreadyPrinted(alreadyPrintedTypes, tn.type()))) {
                    if (alreadyPrintedTypes.size() != 0) {
                        w.write(",");
                        w.allowBreak(0);
                    }
                    alreadyPrintedTypes.add(tn.type());
                    boolean isJavaNative = Emitter.isNativeRepedToJava(tn.type());
                    er.printType(tn.type(), (isSelfDispatch && !isJavaNative ? 0 : PRINT_TYPE_PARAMS) | BOX_PRIMITIVES
                            | NO_VARIANCE);
                }
            }
            w.end();
        }
        w.unifiedBreak(0);
        w.end();
        w.write("{");
        w.newline(4);
        w.begin(0);

        // print the serialVersionUID
        if (!flags.isInterface()) {
            // TODO compute serialVersionUID with the same logic as javac
            long serialVersionUID = 1L;
            w.write("private static final long serialVersionUID = " + serialVersionUID + "L;");
            w.newline();
        }

        // print the clone method
        boolean mutable_struct = false;
        try {
            if (def.isStruct() && !def.annotationsMatching(getType("x10.compiler.Mutable")).isEmpty()) {
                mutable_struct = true;
            }
        } catch (SemanticException e) {
        }
        if (mutable_struct) {
            w.write("public ");
            tr.print(n, n.name(), w);
            if (typeParameters.size() > 0) {
                er.printTypeParams(n, context, typeParameters);
            }
            w.write("clone() { try { return (");
            tr.print(n, n.name(), w);
            if (typeParameters.size() > 0) {
                er.printTypeParams(n, context, typeParameters);
            }
            w.write(")super.clone(); } catch (CloneNotSupportedException e) { e.printStackTrace() ; return null; } }");
            w.newline();
        }

        // XTENLANG-1102
        er.generateRTTInstance(def);

        // print the custom serializer
        if (subtypeOfCustomSerializer(def)) {
            er.generateCustomSerializer(def, n);
        } else {
            if (!def.flags().isInterface() && !config.NO_TRACES) {
                // override to trace serialization
                w.write("private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { ");
                w.write("if (x10.runtime.impl.java.Runtime.TRACE_SER) { ");
                w.write("java.lang.System.out.println(\"Serializer: writeObject(ObjectOutputStream) of \" + this + \" calling\"); ");
                w.write("} ");
                w.write("oos.defaultWriteObject(); }");
                w.newline();
            }
        }

        // TODO haszero
        if (needZeroValueConstructor(def)) {
            // if (needZeroValueConstructor2(def)) {
            er.generateZeroValueConstructor(def, n);
        }

        // print the constructor just for allocation
        if (supportConstructorSplitting && config.OPTIMIZE && config.SPLIT_CONSTRUCTORS && !def.flags().isInterface()) {
            w.write("// constructor just for allocation");
            w.newline();
            w.write("public " + def.name().toString() + "(");
            w.write("final " + JAVA_LANG_SYSTEM + "[] $dummy) { ");
            if (!(superClassNode != null && Emitter.isNativeRepedToJava(superClassNode.type()))) {
                w.write("super($dummy);");
            }
            w.write("}");
            w.newline();
        }

        if (isSelfDispatch) {
            er.generateDispatchMethods(def);
        }
        er.generateBridgeMethodsForGenerics(def);

        er.generateBridgeMethodsToOverrideWithCovReturn(def);

        // print the fields for the type params
        if (typeParameters.size() > 0) {
            w.newline(4);
            w.begin(0);
            if (!flags.isInterface()) {
                for (TypeParamNode tp : typeParameters) {
                    w.write("private ");
                    w.write(X10_RUNTIME_TYPE_CLASS);
                    // w.write("<"); n.print(tp, w, tr); w.write(">"); // TODO
                    w.write(" ");
                    // WIP XTENLANG-2463
                    n.print(tp.name(), w, tr);
                    // w.write(Emitter.mangleTypeVariable(tp.name().id()));
                    w.write(";");
                    w.newline();
                }
            }
            w.end();
        }

        // print the props
        if (!flags.isInterface()) {
            if (n.properties().size() > 0) {
                w.newline(4);
                w.begin(0);
                for (PropertyDecl pd : n.properties()) {
                    n.print(pd, w, tr);
                    w.newline();
                }
                w.end();
            }
        }

        setConstructorIds(def);

        // print the original body
        n.print(n.body(), w, tr);

        w.end();
        w.newline();
        w.write("}");
        w.newline(0);
    }

    private static final String CUSTOM_SERIALIZATION = "x10.io.CustomSerialization";

    private static boolean subtypeOfCustomSerializer(X10ClassDef def) {
        for (Ref<? extends Type> ref : def.interfaces()) {
            if (CUSTOM_SERIALIZATION.equals(ref.get().toString())) {
                return true;
            }
        }
        Ref<? extends Type> ref = def.superType();
        if (ref == null) return false;
        Type type = ref.get();
        if (type instanceof ConstrainedType) {
            type = ((ConstrainedType) type).baseType().get();
        }
        X10ClassDef superDef = (X10ClassDef) ((X10ParsedClassType_c) type).def();
        return subtypeOfCustomSerializer(superDef);
    }

    // TODO haszero
    /*
     * (Definition of haszero by Yoav) Formally, the following types haszero: a
     * type that can be null (e.g., Any, closures, but not a struct or
     * Any{self!=null}) Primitive structs (Short,UShort,Byte,UByte, Int, Long,
     * ULong, UInt, Float, Double, Boolean, Char) user defined structs without a
     * constraint and without a class invariant where all fields haszero.
     */
    private static boolean isPrimitiveStruct(Type type, TypeSystem xts) {
        return xts.isNumeric(type) || xts.isChar(type) || xts.isBoolean(type);
    }

    private static boolean isPrimitiveStruct(X10ClassDef def) {
        TypeSystem xts = def.typeSystem();
        X10ClassType type = def.asType();
        return isPrimitiveStruct(type, xts);
    }

    private static boolean needZeroValueConstructor(X10ClassDef def) {
        if (def.flags().isInterface()) return false;
        if (!def.flags().isStruct()) return false;
        // Note: we don't need zero value constructor for primitive structs
        // because they are cached in x10.rtt.Types class.
        if (isPrimitiveStruct(def)) return false;
        // TODO stop generating useless zero value constructor for user-defined
        // struct that does not have zero value
        // user-defined struct does not have zero value if it have a field of
        // type of either
        // 1) type parameter T that does not have haszero constraint
        // 2) any reference (i.e. non-struct) type that has {self != null}
        // consttaint
        // 3) any struct type (including primitive structs) that has any
        // constraint (e.g. Int{self != 0})
        // 4) any user-defined struct that does not have zero value
        return true;
    }

    private static boolean needZeroValueConstructor2(X10ClassDef def) {
        if (!def.flags().isStruct()) return false;
        return hasZeroValue(def.asType(), def.typeSystem());
    }

    private static boolean hasZeroValue(Type type, TypeSystem xts) {
        X10ClassType classType = (X10ClassType) type;
        if (classType.flags().isInterface()) return false;
        // if (!classType.flags().isStruct()) return false;
        // Note: we don't need zero value constructor for primitive structs
        // because they are cached in x10.rtt.Types class.
        if (isPrimitiveStruct(type, xts)) return false;

        // TODO
        if (false) {
            // if (true) {

            // user-defined struct type, parameter type or reference type

            // TODO stop generating useless zero value constructor for
            // user-defined struct that does not have zero value
            // user-defined struct does not have zero value if it have a field
            // of type of either
            // 1) type parameter T that does not have haszero constraint
            // 2) any reference (i.e. non-struct) type that has {self != null}
            // consttaint
            // 3) any struct type (including primitive structs) that has any
            // constraint (e.g. Int{self != 0})
            // 4) any user-defined struct that does not have zero value

            if (type instanceof ConstrainedType) {
                ConstrainedType constrainedType = (ConstrainedType) type;
                type = constrainedType.baseType().get();

                if (xts.isParameterType(type)) {
                    // parameter type T
                    ParameterType paramType = (ParameterType) type;
                    // TODO
                    // if (T doesn't have haszero constrait) return false;
                    List<SubtypeConstraint> terms = classType.x10Def().typeBounds().get().terms();
                    for (SubtypeConstraint sc : terms) {
                        if (sc.isHaszero()) {
                            Type superType = sc.supertype();
                            Type subType = sc.subtype();
                            System.out.println(superType);
                            System.out.println(subType);
                            // if (superType.equals(type)) return true;
                        }
                    }
                    return false;
                } else if (xts.isStruct(type)) {
                    // user-defined struct type
                    return false;
                } else {
                    // reference (i.e. non-struct) type
                    // TODO
                    // if (type has {self != null} constraint) return false;
                    x10.types.constraints.CConstraint constraint = constrainedType.constraint().get();
                    Set<x10.constraint.XTerm> terms = constraint.rootTerms();
                    for (x10.constraint.XTerm term : terms) {
                        System.out.println(term);
                    }
                }

            } else {
                if (xts.isParameterType(type)) {
                    // parameter type T
                    ParameterType paramType = (ParameterType) type;
                    // TODO
                    // if (T doesn't have haszero constrait) return false;
                    List<SubtypeConstraint> terms = classType.x10Def().typeBounds().get().terms();
                    for (SubtypeConstraint sc : terms) {
                        if (sc.isHaszero()) {
                            Type superType = sc.supertype();
                            Type subType = sc.subtype();
                            System.out.println(superType);
                            System.out.println(subType);
                            // if (superType.equals(type)) return true;
                        }
                    }
                    return false;
                } else if (xts.isStruct(type)) {
                    // user-defined struct type
                    // OK
                    // check instance fields
                } else {
                    // reference (i.e. non-struct) type
                    // OK
                    // check instance fields
                }

                // check instance fields recursively
                for (polyglot.types.FieldInstance field : classType.fields()) {
                    if (field.flags().isStatic()) continue;
                    if (!hasZeroValue(type, xts)) return false;
                }
            }

        }

        return true;
    }

    private void setConstructorIds(X10ClassDef def) {
        List<ConstructorDef> cds = def.constructors();
        int constructorId = 0;
        for (ConstructorDef cd : cds) {
            X10ConstructorDef xcd = (X10ConstructorDef) cd;
            List<Type> annotations = xcd.annotations();
            List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>();
            for (Type type : annotations) {
                ats.add(Types.ref(type));
            }
            boolean containsParamOrParameterized = false;
            List<Ref<? extends Type>> formalTypes = xcd.formalTypes();
            for (Ref<? extends Type> ref : formalTypes) {
                Type t = ref.get();
                if (Types.baseType(t) instanceof ParameterType || hasParams(t)) {
                    containsParamOrParameterized = true;
                    break;
                }
            }
            Type annotationType;
            if (containsParamOrParameterized) {
                annotationType = new ConstructorIdTypeForAnnotation(def).setIndex(constructorId++);
            } else {
                annotationType = new ConstructorIdTypeForAnnotation(def);
            }
            ats.add(Types.ref(annotationType));
            xcd.setDefAnnotations(ats);
        }
    }

    private boolean hasConstructorIdAnnotation(X10ConstructorDef condef) {
        List<Type> annotations = condef.annotations();
        for (Type an : annotations) {
            if (an instanceof ConstructorIdTypeForAnnotation) {
                return true;
            }
        }
        return false;
    }

    // if it isn't set id or don't have an annotation, return -1
    private int getConstructorId(X10ConstructorDef condef) {
        if (!hasConstructorIdAnnotation(condef)) {
            ContainerType st = condef.container().get();
            if (st instanceof X10ClassType) {
                X10ClassDef def = (X10ClassDef) ((X10ClassType) st).def();
                setConstructorIds(def);
            }
        }
        List<Type> annotations = condef.annotations();
        for (Type an : annotations) {
            if (an instanceof ConstructorIdTypeForAnnotation) {
                return ((ConstructorIdTypeForAnnotation) an).getIndex();
            }
        }
        return -1;
    }

    private Type getType(String name) throws SemanticException {
        return tr.typeSystem().systemResolver().findOne(QName.make(name));
    }

    private boolean isMutableStruct(Type t) {
        TypeSystem ts = tr.typeSystem();
        t = Types.baseType(ts.expandMacros(t));
        if (t.isClass()) {
            X10ClassType ct = (X10ClassType) t;
            try {
                if (ct.isX10Struct()) {
                    X10ClassDef cd = (X10ClassDef) ct.def();
                    if (!cd.annotationsMatching(getType("x10.compiler.Mutable")).isEmpty()) {
                        return true;
                    }
                }
            } catch (SemanticException e) {
            }
        }
        return false;
    }

    @Override
    public void visit(X10ConstructorDecl_c n) {

        printCreationMethodDecl(n);

        X10ClassType type = (X10ClassType) Types.get(n.constructorDef().container());
        if (supportConstructorSplitting && config.OPTIMIZE && config.SPLIT_CONSTRUCTORS
                && !n.name().toString().startsWith(ClosureRemover.STATIC_NESTED_CLASS_BASE_NAME)
                && !ConstructorSplitterVisitor.cannotSplitConstructor(type)) {

            printConstructorMethodDecl(n);
            return;
        }

        w.begin(0);

        tr.print(n,
                 tr.nodeFactory().FlagsNode(n.flags().position(),
                                            n.flags().flags().clearPrivate().clearProtected().Public()), w);
        tr.print(n, n.name(), w);

        List<String> typeAssignments = printConstuctorFormals(n);

        if (n.body() != null) {
            // if (typeAssignments.size() > 0) {
            w.write(" {");
            w.begin(4);
            if (n.body().statements().size() > 0) {
                Stmt firstStmt = getFirstStatement(n);
                if (firstStmt instanceof X10ConstructorCall_c) {
                    X10ConstructorCall_c cc = (X10ConstructorCall_c) firstStmt;
                    // n.printSubStmt(cc, w, tr);
                    printConstructorCallForJavaCtor(cc);
                    w.allowBreak(0, " ");
                    if (cc.kind() == ConstructorCall.THIS) typeAssignments.clear();
                }
            }
            for (String s : typeAssignments) {
                w.write(s);
                w.allowBreak(0, " ");
            }
            if (n.body().statements().size() > 0) {
                Stmt firstStmt = getFirstStatement(n);
                if (firstStmt instanceof X10ConstructorCall_c)
                    n.printSubStmt(n.body().statements(n.body().statements().subList(1, n.body().statements().size())),
                                   w, tr);
                // vj: the main body was not being written. Added next two
                // lines.
                else
                    n.printSubStmt(n.body(), w, tr);
            } else
                n.printSubStmt(n.body(), w, tr);
            w.write("}");
            w.end();
            // } else {
            // n.printSubStmt(n.body(), w, tr);
            // }
        } else {
            w.write(";");
        }
    }

    private void printCreationMethodDecl(X10ConstructorDecl_c n) {
        X10ClassType type = (X10ClassType) Types.get(n.constructorDef().container());

        if (type.flags().isAbstract()) {
            return;
        }

        w.write("// creation method for java code");
        w.newline();

        tr.print(n,
                 tr.nodeFactory().FlagsNode(n.flags().position(),
                                            n.flags().flags().clearPrivate().clearProtected().Public().Static()), w);

        List<ParameterType> typeParameters = type.x10Def().typeParameters();

        // TODO check without type bounds
        er.printMethodParams(typeParameters);

        er.printType(type, 0);

        w.write(" ");
        w.write(CREATION_METHOD_NAME);

        List<String> typeAssignments = printConstuctorFormals(n);

        w.write("{");
        w.begin(4);

        w.write("return ");

        if (supportConstructorSplitting && config.OPTIMIZE && config.SPLIT_CONSTRUCTORS
                && !n.name().toString().startsWith(ClosureRemover.STATIC_NESTED_CLASS_BASE_NAME)
                && !ConstructorSplitterVisitor.cannotSplitConstructor(Types.baseType(type))) {
            printAllocationCall(type);
            w.write(".");
            w.write(CONSTRUCTOR_METHOD_NAME);
        } else {
            w.write("new ");
            w.write(n.name().toString());
        }
        w.write("(");

        int size = n.formals().size();
        printArgumentsForTypeParams(typeParameters, size);

        List<Formal> formals = n.formals();
        for (int i = 0; i < formals.size(); i++) {
            Formal formal = formals.get(i);
            if (i != 0) {
                w.write(",");
            }
            tr.print(n, formal.name(), w);
        }

        printExtraArgments((X10ConstructorInstance) n.constructorDef().asInstance());

        w.write(")");

        w.write(";");

        w.end();
        w.write("}");

    }

    private Stmt getFirstStatement(X10ConstructorDecl_c n) {
        Stmt firstStmt = n.body().statements().get(0);
        if (firstStmt instanceof Block) {
            List<Stmt> statements = ((Block) firstStmt).statements();
            if (statements.size() == 1) {
                firstStmt = statements.get(0);
            }
        }
        return firstStmt;
    }

    private void printConstructorMethodDecl(X10ConstructorDecl_c n) {

        tr.print(n,
                 tr.nodeFactory().FlagsNode(n.flags().position(),
                                            n.flags().flags().clearPrivate().clearProtected().Public()), w);

        er.printType(n.constructorDef().container().get(), PRINT_TYPE_PARAMS | NO_VARIANCE);
        w.write(" ");
        w.write(CONSTRUCTOR_METHOD_NAME);

        List<String> typeAssignments = printConstuctorFormals(n);

        Block body = n.body();
        if (body != null) {

            body = (Block) body.visit(new NodeVisitor() {
                @Override
                public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                    if (n instanceof Return) {
                        NodeFactory nf = tr.nodeFactory();
                        return nf.Return(n.position(), nf.This(Position.COMPILER_GENERATED));
                    }
                    return n;
                }
            });

            // if (typeAssignments.size() > 0) {
            w.write(" {");
            w.begin(4);
            // if (body.statements().size() > 0) {
            // if (body.statements().get(0) instanceof X10ConstructorCall_c) {
            // X10ConstructorCall_c cc = (X10ConstructorCall_c)
            // body.statements().get(0);
            // n.printSubStmt(cc, w, tr);
            // w.allowBreak(0, " ");
            // if (cc.kind() == ConstructorCall.THIS) typeAssignments.clear();
            // }
            // }
            for (String s : typeAssignments) {
                w.write(s);
                w.allowBreak(0, " ");
            }
            if (body.statements().size() > 0) {
                if (body.statements().get(0) instanceof X10ConstructorCall_c)
                    n.printSubStmt(body.statements(body.statements()
                    /* .subList(1, body.statements().size()) */), w, tr);
                // vj: the main body was not being written. Added next
                // two lines.
                else
                    n.printSubStmt(body, w, tr);
            } else
                n.printSubStmt(body, w, tr);
            
            if (body.reachable()) {
                w.write("return this;");
            }

            w.write("}");
            w.end();
            // } else {
            // n.printSubStmt(body, w, tr);
            // }
        } else {
            w.write(";");
        }
    }

    private List<String> printConstuctorFormals(X10ConstructorDecl_c n) {
        w.write("(");

        w.begin(0);

        X10ConstructorDef ci = n.constructorDef();
        X10ClassType ct = (X10ClassType) Types.get(ci.container());
        List<String> typeAssignments = new ArrayList<String>();

        for (Iterator<ParameterType> i = ct.x10Def().typeParameters().iterator(); i.hasNext();) {
            ParameterType p = i.next();
            w.write("final ");
            w.write(X10_RUNTIME_TYPE_CLASS);
            w.write(" ");
            Name name = p.name();
            // TODO
            w.write(Emitter.mangleToJava(name));

            if (i.hasNext() || n.formals().size() > 0) {
                w.write(",");
                w.allowBreak(0, " ");
            }
            // TODO
            typeAssignments.add("this." + name + " = " + name + ";");
        }

        for (Iterator<Formal> i = n.formals().iterator(); i.hasNext();) {
            Formal f = i.next();
            n.print(f, w, tr);

            if (i.hasNext()) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }

        printExtraFormals(n);

        w.end();
        w.write(")");

        /*
         * if (! n.throwTypes().isEmpty()) { w.allowBreak(6);
         * w.write("throws ");
         * 
         * for (Iterator<TypeNode> i = n.throwTypes().iterator(); i.hasNext(); )
         * { TypeNode tn = (TypeNode) i.next(); er.printType(tn.type(),
         * PRINT_TYPE_PARAMS);
         * 
         * if (i.hasNext()) { w.write(","); w.allowBreak(4, " "); } } }
         */

        return typeAssignments;
    }

    private void printExtraFormals(X10ConstructorDecl_c n) {
        String dummy = "$dummy";
        int cid = getConstructorId(n.constructorDef());
        if (cid != -1) {
            w.write(",");
            int narg = 0;
            for (int i = 0; i < cid + 1; i++) {
                if (i % 256 == 0) {
                    if (i != 0) {
                        w.write(" " + dummy + narg++);
                        w.write(",");
                    }
                    w.write(JAVA_LANG_CLASS);
                } else {
                    w.write("[]");
                }
            }
            w.write(" " + dummy + narg);
        }
    }

    // ////////////////////////////////
    // Expr
    // ////////////////////////////////
    @Override
    public void visit(Allocation_c n) {
        Type type = n.type();
        printAllocationCall(type);
    }

    private void printAllocationCall(Type type) {
        w.write("new ");
        er.printType(type, PRINT_TYPE_PARAMS | NO_VARIANCE);
        w.write("((" + JAVA_LANG_SYSTEM + "[])null)");
    }

    @Override
    public void visit(LocalAssign_c n) {
        Local l = n.local();
        TypeSystem ts = tr.typeSystem();
        if (n.operator() == Assign.ASSIGN || l.type().isNumeric() || l.type().isBoolean()
                || l.type().isSubtype(ts.String(), tr.context())) {
            tr.print(n, l, w);
            w.write(" ");
            w.write(n.operator().toString());
            w.write(" ");
            er.coerce(n, n.right(), l.type());
            if (isMutableStruct(l.type())) {
                w.write(".clone()");
            }
        } else {
            Binary.Operator op = n.operator().binaryOperator();
            Name methodName = X10Binary_c.binaryMethodName(op);
            tr.print(n, l, w);
            w.write(" = ");
            tr.print(n, l, w);
            w.write(".");
            w.write(Emitter.mangleToJava(methodName));
            w.write("(");
            tr.print(n, n.right(), w);
            w.write(")");
        }
    }

    @Override
    public void visit(FieldAssign_c n) {
        Type t = n.fieldInstance().type();

        TypeSystem ts = tr.typeSystem();
        if (n.operator() == Assign.ASSIGN || t.isNumeric() || t.isBoolean() || t.isSubtype(ts.String(), tr.context())) {
            if (n.target() instanceof TypeNode)
                er.printType(n.target().type(), 0);
            else
                tr.print(n, n.target(), w);
            w.write(".");
            w.write(Emitter.mangleToJava(n.name().id()));
            w.write(" ");
            w.write(n.operator().toString());
            w.write(" ");
            er.coerce(n, n.right(), n.fieldInstance().type());
            if (isMutableStruct(n.fieldInstance().type())) {
                w.write(".clone()");
            }
        } else if (n.target() instanceof TypeNode || n.target() instanceof Local || n.target() instanceof Lit) {
            // target has no side effects--evaluate it more than once
            Binary.Operator op = n.operator().binaryOperator();
            Name methodName = X10Binary_c.binaryMethodName(op);
            if (n.target() instanceof TypeNode)
                er.printType(n.target().type(), 0);
            else
                tr.print(n, n.target(), w);
            w.write(".");
            w.write(Emitter.mangleToJava(n.name().id()));
            w.write(" ");
            w.write(" = ");
            tr.print(n, n.target(), w);
            w.write(".");
            w.write(Emitter.mangleToJava(n.name().id()));
            w.write(".");
            w.write(Emitter.mangleToJava(methodName));
            w.write("(");
            tr.print(n, n.right(), w);
            w.write(")");
        } else {
            // x.f += e
            // -->
            // new Object() { T eval(R target, T right) { return (target.f =
            // target.f.add(right)); } }.eval(x, e)
            Binary.Operator op = n.operator().binaryOperator();
            Name methodName = X10Binary_c.binaryMethodName(op);
            w.write("new " + JAVA_IO_SERIALIZABLE + "() {");
            w.allowBreak(0, " ");
            w.write("final ");
            er.printType(n.type(), PRINT_TYPE_PARAMS);
            w.write(" eval(");
            er.printType(n.target().type(), PRINT_TYPE_PARAMS);
            w.write(" target, ");
            er.printType(n.right().type(), PRINT_TYPE_PARAMS);
            w.write(" right) {");
            w.allowBreak(0, " ");
            w.write("return (target.");
            w.write(Emitter.mangleToJava(n.name().id()));
            w.write(" = ");
            w.write("target.");
            w.write(Emitter.mangleToJava(n.name().id()));
            w.write(".");
            w.write(Emitter.mangleToJava(methodName));
            w.write("(right));");
            w.allowBreak(0, " ");
            w.write("} }.eval(");
            tr.print(n, n.target(), w);
            w.write(", ");
            tr.print(n, n.right(), w);
            w.write(")");
        }
    }

    @Override
    public void visit(SettableAssign_c n) {
        SettableAssign_c a = n;
        Expr array = a.array();
        List<Expr> index = a.index();

        boolean effects = er.hasEffects(array);
        for (Expr e : index) {
            if (effects) break;
            if (er.hasEffects(e)) effects = true;
        }

        TypeSystem ts = tr.typeSystem();
        Context context = tr.context();
        Type t = n.leftType();

        boolean nativeop = false;
        if (t.isNumeric() || t.isBoolean() || t.isChar() || t.isSubtype(ts.String(), context)) {
            nativeop = true;
        }

        MethodInstance mi = n.methodInstance();
        boolean superUsesClassParameter = !mi.flags().isStatic(); // &&
                                                                  // overridesMethodThatUsesClassParameter(mi);

        if (n.operator() == Assign.ASSIGN) {
            // Look for the appropriate set method on the array and emit native
            // code if there is an
            // @Native annotation on it.
            List<Expr> args = new ArrayList<Expr>(index.size() + 1);
            // args.add(array);
            args.add(n.right());
            for (Expr e : index)
                args.add(e);
            String pat = Emitter.getJavaImplForDef(mi.x10Def());

            if (pat != null) {
                er.emitNativeAnnotation(pat, array, mi.typeParameters(), args, Collections.<Type> emptyList());
                return;
            } else {
                // otherwise emit the hardwired code.
                tr.print(n, array, w);
                w.write(".set");
                w.write("(");
                tr.print(n, n.right(), w);
                if (index.size() > 0) w.write(", ");
                new Join(er, ", ", index).expand(tr);
                w.write(")");
            }
        } else if (!effects) {
            Binary.Operator op = n.operator().binaryOperator();
            Name methodName = X10Binary_c.binaryMethodName(op);
            TypeSystem xts = ts;
            if (isPrimitiveRepedJava(t) && (xts.isRail(array.type()) || er.isIMC(array.type()))) {
                w.write("(");
                w.write("(");
                er.printType(t, 0);
                w.write("[])");
                tr.print(n, array, w);
                w.write(".value");
                w.write(")");
                w.write("[");
                new Join(er, ", ", index).expand(tr);
                w.write("]");
                w.write(" ");
                w.write(op.toString());
                w.write("=");
                w.write(" ");
                tr.print(n, n.right(), w);
                return;
            }

            tr.print(n, array, w);
            w.write(".set");
            w.write("((");
            tr.print(n, array, w);
            w.write(").$apply(");
            new Join(er, ", ", index).expand(tr);
            w.write(")");
            if (nativeop) {
                w.write(" ");
                w.write(op.toString());
                tr.print(n, n.right(), w);
            } else {
                w.write(".");
                w.write(Emitter.mangleToJava(methodName));
                w.write("(");
                tr.print(n, n.right(), w);
                w.write(")");
            }
            if (index.size() > 0) w.write(", ");
            new Join(er, ", ", index).expand(tr);
            w.write(")");
        } else {
            // new Object() { T eval(R target, T right) { return (target.f =
            // target.f.add(right)); } }.eval(x, e)
            Binary.Operator op = n.operator().binaryOperator();
            Name methodName = X10Binary_c.binaryMethodName(op);
            TypeSystem xts = ts;
            if (isPrimitiveRepedJava(t) && (xts.isRail(array.type()) || er.isIMC(array.type()))) {
                w.write("(");
                w.write("(");
                er.printType(t, 0);
                w.write("[])");
                tr.print(n, array, w);
                w.write(".value");
                w.write(")");
                w.write("[");
                new Join(er, ", ", index).expand(tr);
                w.write("]");
                w.write(" ");
                w.write(op.toString());
                w.write("=");
                w.write(" ");
                tr.print(n, n.right(), w);
                return;
            }

            w.write("new " + JAVA_IO_SERIALIZABLE + "() {");
            w.allowBreak(0, " ");
            w.write("final ");
            er.printType(n.type(), PRINT_TYPE_PARAMS);
            w.write(" eval(");
            er.printType(array.type(), PRINT_TYPE_PARAMS);
            w.write(" array");
            {
                int i = 0;
                for (Expr e : index) {
                    w.write(", ");
                    er.printType(e.type(), PRINT_TYPE_PARAMS);
                    w.write(" ");
                    w.write("i" + i);
                    i++;
                }
            }
            w.write(", ");
            er.printType(n.right().type(), PRINT_TYPE_PARAMS);
            w.write(" right) {");
            w.allowBreak(0, " ");
            if (!n.type().isVoid()) {
                w.write("return ");
            }
            w.write("array.set");
            w.write("(");

            w.write(" array.$apply(");
            {
                int i = 0;
                for (Expr e : index) {
                    if (i != 0) w.write(", ");
                    w.write("i" + i);
                    i++;
                }
            }
            w.write(")");
            if (nativeop) {
                w.write(" ");
                w.write(op.toString());
                w.write(" right");
            } else {
                w.write(".");
                w.write(Emitter.mangleToJava(methodName));
                w.write("(right)");
            }
            if (index.size() > 0) w.write(", ");
            {
                int i = 0;
                for (Expr e : index) {
                    if (i != 0) w.write(", ");
                    w.write("i" + i);
                    i++;
                }
            }
            w.write(");");
            w.allowBreak(0, " ");
            w.write("} }.eval(");
            tr.print(n, array, w);
            if (index.size() > 0) w.write(", ");
            new Join(er, ", ", index).expand();
            w.write(", ");
            tr.print(n, n.right(), w);
            w.write(")");
        }
    }

    @Override
    public void visit(X10Binary_c n) {
        Expr left = n.left();
        Type l = left.type();
        Expr right = n.right();
        Type r = right.type();
        TypeSystem xts = tr.typeSystem();
        Binary.Operator op = n.operator();

        if (l.isNumeric() && r.isNumeric() || l.isBoolean() && r.isBoolean() || l.isChar() && r.isChar()) {
            prettyPrint(n);
            return;
        }

        if (op == Binary.EQ) {
            // SYNOPSIS: #0 == #1
            String regex;
            // TODO generalize for reference type
            if (l.isNull() || r.isNull()) {
                regex = "((#0) == (#1))";
            } else {
                regex = "x10.rtt.Equality.equalsequals(#0,#1)";
            }
            er.dumpRegex("equalsequals", new Object[] { left, right }, tr, regex);
            return;
        }

        if (op == Binary.NE) {
            // SYNOPSIS: #0 != #1
            String regex;
            // TODO generalize for reference type
            if (l.isNull() || r.isNull()) {
                regex = "((#0) != (#1))";
            } else {
                regex = "(!x10.rtt.Equality.equalsequals(#0,#1))";
            }
            er.dumpRegex("notequalsequals", new Object[] { left, right }, tr, regex);
            return;
        }

        if (op == Binary.ADD && (l.isSubtype(xts.String(), tr.context()) || r.isSubtype(xts.String(), tr.context()))) {
            prettyPrint(n);
            return;
        }
        if (n.invert()) {
            Name methodName = X10Binary_c.invBinaryMethodName(op);
            if (methodName != null) {
                er.generateStaticOrInstanceCall(n.position(), right, methodName, left);
                return;
            }
        } else {
            Name methodName = X10Binary_c.binaryMethodName(op);
            if (methodName != null) {
                er.generateStaticOrInstanceCall(n.position(), left, methodName, right);
                return;
            }
        }
        throw new InternalCompilerError("No method to implement " + n, n.position());
    }

    // This is an enhanced version of Binary_c#prettyPrint(CodeWriter,
    // PrettyPrinter)
    private void prettyPrint(X10Binary_c n) {
        Expr left = n.left();
        Type l = left.type();
        Expr right = n.right();
        Type r = right.type();
        Binary.Operator op = n.operator();

        boolean asPrimitive = false;
        boolean asUnsignedPrimitive = false;
        if (op == Binary.EQ || op == Binary.NE) {
            if (l.isNumeric() && r.isNumeric() || l.isBoolean() && r.isBoolean() || l.isChar() && r.isChar()) {
                asPrimitive = true;

                if (l.isNumeric()
                        && !(l.isByte() || l.isShort() || l.isInt() || l.isLong() || l.isFloat() || l.isDouble())) {
                    asUnsignedPrimitive = true;
                }
            }
        }

        if (asPrimitive) {
            if (asUnsignedPrimitive && (op == Binary.NE)) w.write("!");
            // TODO:CAST
            w.write("((");
            er.printType(l, 0);
            w.write(") ");
        }
        n.printSubExpr(left, true, w, tr);
        if (asPrimitive) w.write(")");
        if (asUnsignedPrimitive) {
            w.write(".equals(");
        } else {
            w.write(" ");
            w.write(op.toString());
            w.allowBreak(n.type() == null || n.type().isJavaPrimitive() ? 2 : 0, " ");
        }
        if (asPrimitive) {
            // TODO:CAST
            w.write("((");
            er.printType(r, 0);
            w.write(") ");
        }
        n.printSubExpr(right, false, w, tr);
        if (asPrimitive) w.write(")");
        if (asUnsignedPrimitive) w.write(")");
    }

    @Override
    public void visit(X10Call_c c) {
        if (er.printInlinedCode(c) || er.printNativeMethodCall(c)) {
            return;
        }

        MethodInstance mi = c.methodInstance();
        Receiver target = c.target();

        // Check for properties accessed using method syntax. They may have
        // @Native annotations too.
        if (mi.flags().isProperty() && mi.formalTypes().size() == 0 && mi.typeParameters().size() == 0) {
            X10FieldInstance fi = (X10FieldInstance) mi.container().fieldNamed(mi.name());
            if (fi != null) {
                String pat2 = Emitter.getJavaImplForDef(fi.x10Def());
                if (pat2 != null) {
                    Object[] components = new Object[] { target };
                    er.dumpRegex("Native", components, tr, pat2);
                    return;
                }
            }
        }

        TypeSystem xts = tr.typeSystem();
        Type targetType = target.type();

        // When the target class is a generics , print a cast operation
        // explicitly.
        if (target instanceof TypeNode) {
            er.printType(targetType, 0);
        } else {
            // add a check that verifies if the target of the call is in place
            // 'here'
            // This is not needed for:

            if (!(target instanceof Special || target instanceof New)) {
                if (xts.isParameterType(targetType)) {
                    // TODO:CAST
                    w.write("(");
                    w.write("(");
                    er.printType(mi.container(), PRINT_TYPE_PARAMS); // TODO
                                                                     // check
                    w.write(")");

                    w.write(X10_RTT_TYPES);
                    w.write(".conversion(");
                    new RuntimeTypeExpander(er, Types.baseType(mi.container())).expand(tr);
                    w.write(",");

                    er.prettyPrint(target, tr);

                    w.write(")");

                    w.write(")");
                } else if (isSelfDispatch && (mi.typeParameters().size() > 0 || hasParams(mi.container()))) {
                    // TODO:CAST
                    w.write("(");
                    w.write("(");
                    er.printType(mi.container(), PRINT_TYPE_PARAMS);
                    w.write(")");
                    er.prettyPrint(target, tr);
                    w.write(")");
                } else {
                    er.prettyPrint(target, tr);
                }
            } else {
                er.prettyPrint(target, tr);
            }
        }

        w.write(".");

        List<Type> methodTypeParams = mi.typeParameters();
        if (methodTypeParams.size() > 0) {
            er.printMethodParams(methodTypeParams);
        }

        if (isMainMethod(mi)) {
            w.write(Emitter.mangleToJava(c.name().id()));
        } else {
            boolean invokeInterface = false;
            ContainerType st = mi.def().container().get();
            Type bst = Types.baseType(st);
            if (bst instanceof X10ClassType) {
                if (xts.isInterfaceType(bst) || (xts.isFunctionType(bst) && ((X10ClassType) bst).isAnonymous())) {
                    invokeInterface = true;
                }
            }

            boolean isDispatchMethod = false;
            if (isSelfDispatch) {
                Type tt = Types.baseType(target.type());
                if (tt instanceof X10ClassType && ((X10ClassType) tt).flags().isInterface()) {
                    if (containsTypeParam(mi.def().formalTypes())) {
                        isDispatchMethod = true;
                    }
                } else if (target instanceof ParExpr && ((ParExpr) target).expr() instanceof Closure_c) {
                    if (mi.formalTypes().size() != 0) {
                        isDispatchMethod = true;
                    }
                }
            }

            boolean instantiatesReturnType = false;
            List<MethodInstance> list = mi.implemented(tr.context());
            for (MethodInstance mj : list) {
                if (mj.container().typeEquals(mi.container(), tr.context())
                        && Types.baseType(mj.def().returnType().get()) instanceof ParameterType) {
                    instantiatesReturnType = true;
                    break;
                }
            }

            boolean isParamReturnType = Types.baseType(mi.def().returnType().get()) instanceof ParameterType
                    || instantiatesReturnType;

            er.printMethodName(mi.def(), invokeInterface, isDispatchMethod,
                               er.isSpecialType(mi.def().returnType().get()), isParamReturnType);
        }

        // print the argument list
        w.write("(");
        w.begin(0);

        List<Type> typeParameters = mi.typeParameters();
        int argumentSize = c.arguments().size();
        printArgumentsForTypeParams(typeParameters, argumentSize);

        boolean runAsync = false;
        if (mi.container().isClass()
                && ((X10ClassType) mi.container().toClass()).fullName().toString().equals("x10.lang.Runtime")) {
            if (mi.signature().startsWith("runAsync")) {
                runAsync = true;
            }
        }

        List<Expr> exprs = c.arguments();
        MethodDef def = c.methodInstance().def();
        for (int i = 0; i < exprs.size(); ++i) {
            Expr e = exprs.get(i);
            Type defType = def.formalTypes().get(i).get();
            if (runAsync && e instanceof Closure_c) {
                c.print(((Closure_c) e).methodContainer(mi), w, tr);
            }
            // else if (!er.isNoArgumentType(e)) {
            // new CastExpander(w, er, e).castTo(e.type(),
            // BOX_PRIMITIVES).expand();
            // }
            else {
                if (isPrimitiveRepedJava(e.type())) {
                    // e.g) m((Integer) a) for m(T a)
                    if (xts.isParameterType(defType)) {
                        // TODO:CAST
                        w.write("(");
                        er.printType(e.type(), BOX_PRIMITIVES);
                        w.write(")");
                        // e.g) m((int) a) for m(int a)
                    } else {
                        // TODO:CAST
                        w.write("(");
                        er.printType(e.type(), 0);
                        w.write(")");
                        if (e instanceof X10Call) {
                            Type targetType2 = ((X10Call) e).target().type();
                            if (!(tr.typeSystem()).isRail(targetType2)
                                    && xts.isParameterType(((X10Call) e).methodInstance().def().returnType().get())) {
                                // TODO:CAST
                                w.write("(");
                                er.printType(e.type(), BOX_PRIMITIVES);
                                w.write(")");
                            }
                        } else if (e instanceof ClosureCall) {
                            ClosureCall cl = (ClosureCall) e;
                            Expr expr = cl.target();
                            // if (expr instanceof ParExpr) {
                            // expr = expr;
                            // }
                            if (!(expr instanceof Closure_c)
                                    && xts.isParameterType(cl.closureInstance().def().returnType().get())) {
                                // TODO:CAST
                                w.write("(");
                                er.printType(e.type(), BOX_PRIMITIVES);
                                w.write(")");
                            }
                        }
                    }
                    w.write("(");
                    c.print(e, w, tr);
                    w.write(")");
                    if (isMutableStruct(e.type())) {
                        w.write(".clone()");
                    }
                }
                // XTENLANG-1704
                else {
                    // TODO:CAST
                    w.write("(");

                    Type castType = mi.formalTypes().get(i);
                    w.write("(");
                    er.printType(castType, 0);
                    w.write(")");

                    if (isString(e.type(), tr.context()) && !isString(castType, tr.context())) {
                        if (xts.isParameterType(castType)) {
                            w.write(X10_RTT_TYPES);
                            w.write(".conversion(");
                            new RuntimeTypeExpander(er, Types.baseType(castType)).expand(tr);
                            w.write(",");
                        } else {
                            w.write(X10_CORE_STRING);
                            w.write(".box");
                        }
                    }

                    w.write("(");
                    c.print(e, w, tr);
                    w.write(")");
                    if (isMutableStruct(e.type())) {
                        w.write(".clone()");
                    }
                    w.write(")");
                }
            }

            // if I is an interface and val i:I , t = type of the type of the
            // formal of method instance
            // i.m(a) => i.m(a,t)
            X10ClassType ct = null;
            if (isSelfDispatch && Types.baseType(targetType) instanceof X10ClassType) {
                ct = (X10ClassType) Types.baseType(targetType);
            } else if (isSelfDispatch && xts.isParameterType(targetType)) {
                ct = (X10ClassType) Types.baseType(mi.container());
            }
            if (ct != null
                    && ((ct.flags().isInterface() || (xts.isFunctionType(ct) && ct.isAnonymous())) && Emitter
                            .containsTypeParam(defType))) {
                w.write(",");
                new RuntimeTypeExpander(er, c.methodInstance().formalTypes().get(i)).expand();
            }
            if (i != exprs.size() - 1) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }
        w.end();
        w.write(")");
    }

    private void printArgumentsForTypeParams(List<? extends Type> typeParameters, int argumentSize) {
        for (Iterator<? extends Type> i = typeParameters.iterator(); i.hasNext();) {
            final Type at = i.next();
            new RuntimeTypeExpander(er, at).expand(tr);
            if (i.hasNext() || argumentSize > 0) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }
    }

    private boolean isMainMethod(MethodInstance mi) {
        return HierarchyUtils.isMainMethod(mi, tr.context());
    }

    @Override
    public void visit(X10Cast_c c) {
        TypeNode tn = c.castType();
        assert tn instanceof CanonicalTypeNode;

        Expr expr = c.expr();
        Type exprType = expr.type();

        switch (c.conversionType()) {
        case CHECKED:
        case PRIMITIVE:
        case SUBTYPE:
        case UNCHECKED:
            if (tn instanceof X10CanonicalTypeNode) {
                X10CanonicalTypeNode castTN = (X10CanonicalTypeNode) tn;

                Type castType = Types.baseType(castTN.type());
                Expander castTE = new TypeExpander(er, castType, PRINT_TYPE_PARAMS);
                Expander castRE = new RuntimeTypeExpander(er, castType);

                TypeSystem xts = exprType.typeSystem();

                // Note: constraint checking should be desugared when compiling
                // without NO_CHECKS flag

                // e.g. any as Int (any:Any), t as Int (t:T)
                if ((xts.isParameterType(exprType) || xts.isAny(Types.baseType(exprType))) && xts.isStruct(castType)) {
                    if (isPrimitiveRepedJava(castType)) {
                        w.write(X10_RTT_TYPES + ".as");
                        er.printType(castType, NO_QUALIFIER);
                        w.write("(");
                        c.printSubExpr(expr, w, tr);
                        w.write(")");
                    } else {
                        w.write("(");
                        w.write("(");
                        er.printType(castType, 0);
                        w.write(")");
                        w.write(X10_RTT_TYPES + ".asStruct(");
                        castRE.expand();
                        w.write(",");
                        c.printSubExpr(expr, w, tr);
                        w.write(")");
                        w.write(")");
                    }
                } else if (castType.isBoolean() || castType.isNumeric() || castType.isChar()) {
                    w.begin(0);
                    // for the case the method is a dispatch method and that
                    // returns Object.
                    // e.g. (Boolean) m(a)
                    if (castType.typeEquals(Types.baseType(exprType), tr.context())) {
                        if (expr instanceof X10Call || expr instanceof ClosureCall) {
                            w.write("(");
                            w.write("(");
                            er.printType(exprType, BOX_PRIMITIVES);
                            w.write(")");
                        }
                        c.printSubExpr(expr, w, tr);
                        if (expr instanceof X10Call || expr instanceof ClosureCall) w.write(")");
                    } else {
                        w.write("("); // put "(Type) expr" in parentheses.
                        w.write("(");
                        castTE.expand(tr);
                        w.write(")");
                        // e.g. d as Int (d:Double) -> (int)(double)(Double) d
                        if (exprType.isBoolean() || exprType.isNumeric() || exprType.isChar()) {
                            w.write(" ");
                            w.write("(");
                            er.printType(exprType, 0);
                            w.write(")");
                            w.write(" ");
                            if (!(expr instanceof Unary || expr instanceof Lit) && (expr instanceof X10Call)) {
                                w.write("(");
                                er.printType(exprType, BOX_PRIMITIVES);
                                w.write(")");
                            }
                        }
                        w.allowBreak(2, " ");
                        // HACK: (java.lang.Integer) -1
                        // doesn't parse correctly, but
                        // (java.lang.Integer) (-1)
                        // does
                        boolean needParan = expr instanceof Unary || expr instanceof Lit
                                || expr instanceof Conditional_c;
                        if (needParan) w.write("(");
                        c.printSubExpr(expr, w, tr);
                        if (needParan) w.write(")");
                        w.write(")");
                    }
                    w.end();
                } else if (exprType.isSubtype(castType, tr.context())) {
                    w.begin(0);
                    w.write("("); // put "(Type) expr" in parentheses.
                    w.write("(");
                    castTE.expand(tr);
                    w.write(")");

                    if (castType instanceof X10ClassType) {
                        X10ClassType ct = (X10ClassType) castType;
                        if (ct.hasParams()) {
                            boolean castToRawType = false;
                            for (Variance variance : ct.x10Def().variances()) {
                                if (variance != Variance.INVARIANT) {
                                    castToRawType = true;
                                    break;
                                }
                            }
                            if (castToRawType) {
                                // cast to raw type
                                // e.g. for covariant class C[+T]{} and
                                // C[Object] v = new C[String](),
                                // it generates class C<T>{} and C<Object> v =
                                // (C<Object>) (C) (new C<String>()).
                                w.write("(");
                                er.printType(castType, 0);
                                w.write(")");
                            }
                        }
                    }

                    w.allowBreak(2, " ");

                    if (isString(exprType, tr.context()) && !isString(castType, tr.context())) {
                        if (xts.isParameterType(castType)) {
                            w.write(X10_RTT_TYPES);
                            w.write(".conversion(");
                            castRE.expand();
                            w.write(",");
                        } else {
                            w.write(X10_CORE_STRING);
                            w.write(".box(");
                        }
                    }

                    boolean needParen = expr instanceof Unary
                            || expr instanceof Lit
                            || expr instanceof Conditional_c
                            || (expr instanceof X10Call && !xts.isParameterType(expr.type()) && xts
                                    .isRail(((X10Call) expr).target().type()));
                    if (needParen) w.write("(");
                    c.printSubExpr(expr, w, tr);
                    if (needParen) w.write(")");

                    if (isString(exprType, tr.context()) && !isString(castType, tr.context())) {
                        w.write(")");
                    }

                    w.write(")");
                    w.end();
                } else {
                    // SYNOPSIS: (#0) #1 #0=type #1=object #2=runtime type
                    String regex = X10_RTT_TYPES
                            + ".<#0>cast"
                            + (xts.isParameterType(exprType) || xts.isParameterType(castType)
                                    || isString(castType, tr.context()) ? "Conversion" : "") + "(#1,#2)";
                    er.dumpRegex("cast_deptype", new Object[] { castTE, expr, castRE }, tr, regex);
                }
            } else {
                throw new InternalCompilerError("Ambiguous TypeNode survived type-checking.", tn.position());
            }
            break;
        case UNBOXING:
            throw new InternalCompilerError("Unboxing conversions not yet supported.", tn.position());
        case UNKNOWN_IMPLICIT_CONVERSION:
            throw new InternalCompilerError("Unknown implicit conversion type after type-checking.", c.position());
        case UNKNOWN_CONVERSION:
            throw new InternalCompilerError("Unknown conversion type after type-checking.", c.position());
        case BOXING:
            throw new InternalCompilerError("Boxing conversion should have been rewritten.", c.position());
        }
    }

    @Override
    public void visit(Conditional_c n) {
        n.translate(w, tr);
    }

    /*
     * Field access -- this includes FieldAssign (because the left node of
     * FieldAssign is a Field node!
     */
    @Override
    public void visit(Field_c n) {
        Receiver target = n.target();
        Type targetType = target.type();

        TypeSystem xts = targetType.typeSystem();
        X10FieldInstance fi = (X10FieldInstance) n.fieldInstance();

        // print native field access
        String pat = Emitter.getJavaImplForDef(fi.x10Def());
        if (pat != null) {
            Object[] components = new Object[] { target };
            er.dumpRegex("Native", components, tr, pat);
            return;
        }

        if (target instanceof TypeNode) {
            TypeNode tn = (TypeNode) target;
            if (targetType instanceof ParameterType) {
                // Rewrite to the class declaring the field.
                FieldDef fd = fi.def();
                targetType = Types.get(fd.container());
                target = tn.typeRef(fd.container());
                n = (Field_c) n.target(target);
            }
        }

        // static access
        if (target instanceof TypeNode) {
            er.printType(targetType, 0);
            w.write(".");
            w.write(Emitter.mangleToJava(n.name().id()));
        } else {
            assert target instanceof Expr;
            w.begin(0);
            if (!n.isTargetImplicit()) {
                if (!(target instanceof Special || target instanceof New)
                        && (xts.isParameterType(targetType) || hasParams(fi.container()))) {
                    // TODO:CAST
                    w.write("(");
                    w.write("(");
                    er.printType(fi.container(), PRINT_TYPE_PARAMS);
                    w.write(")");
                    n.printSubExpr((Expr) target, w, tr);
                    w.write(")");
                } else {
                    n.printSubExpr((Expr) target, w, tr);
                }
                w.write(".");
                w.allowBreak(2, 3, "", 0);
            }
            tr.print(n, n.name(), w);
            w.end();
        }
    }

    @Override
    public void visit(X10Instanceof_c c) {
        TypeNode tn = c.compareType();

        // Note: constraint checking should be desugared when compiling without
        // NO_CHECKS flag

        Type t = tn.type();

        // Fix for XTENLANG-1099
        TypeSystem xts = tr.typeSystem();
        if (xts.typeEquals(xts.Object(), t, tr.context())) {

            /*
             * Because @NativeRep of x10.lang.Object is java.lang.Object, we
             * cannot compile "instanceof x10.lang.Object" as
             * "instanceof @NativeRep".
             */
            w.write(X10_RTT_TYPES);
            w.write(".instanceofObject(");
            tr.print(c, c.expr(), w);
            w.write(")");
            return;
        }

        // XTENLANG-1102
        if (t instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) t;
            X10ClassDef cd = ct.x10Def();
            String pat = Emitter.getJavaRTTRep(cd);

            if (t instanceof FunctionType) {
                FunctionType ft = (FunctionType) t;
                List<Type> args = ft.argumentTypes();
                Type ret = ft.returnType();
                if (ret.isVoid()) {
                    w.write(X10_VOIDFUN_CLASS_PREFIX);
                } else {
                    w.write(X10_FUN_CLASS_PREFIX);
                }
                w.write("_" + ft.typeParameters().size());
                w.write("_" + args.size());
                w.write("." + X10PrettyPrinterVisitor.RTT_NAME);
            } else if (pat == null && Emitter.getJavaRep(cd) == null && ct.isGloballyAccessible()
                    && cd.typeParameters().size() != 0) {
                w.write(cd.fullName().toString() + "." + X10PrettyPrinterVisitor.RTT_NAME);
            } else {
                new RuntimeTypeExpander(er, t).expand(tr);
            }
        } else {
            new RuntimeTypeExpander(er, t).expand(tr);
        }

        w.write(".");
        w.write("instanceof$(");
        tr.print(c, c.expr(), w);

        if (t instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) t;
            X10ClassDef cd = ct.x10Def();
            String pat = Emitter.getJavaRTTRep(cd);

            if (pat == null && Emitter.getJavaRep(cd) == null && ct.typeArguments() != null) {
                for (int i = 0; i < ct.typeArguments().size(); i++) {
                    w.write(", ");
                    new RuntimeTypeExpander(er, ct.typeArguments().get(i)).expand(tr);
                }
            }
        }
        w.write(")");
    }

    @Override
    public void visit(Lit_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(IntLit_c n) {
        String val = null;
        switch (n.kind()) {
        case ULONG:
            val = Long.toString(n.value()) + "L";
            val = "x10.lang.ULong." + CREATION_METHOD_NAME + "(" + val + ")";
            break;
        case LONG:
            val = Long.toString(n.value()) + "L";
            break;
        case UINT:
            val = Integer.toString((int) n.value());
            val = "x10.lang.UInt." + CREATION_METHOD_NAME + "(" + val + ")";
            break;
        case INT:
            val = Integer.toString((int) n.value());
            break;
        case USHORT:
            val = Short.toString((short) n.value());
            val = "x10.lang.UShort." + CREATION_METHOD_NAME + "((short) " + val + ")";
            break;
        case SHORT:
            val = Short.toString((short) n.value());
            break;
        case UBYTE:
            val = Byte.toString((byte) n.value());
            val = "x10.lang.UByte." + CREATION_METHOD_NAME + "((byte) " + val + ")";
            break;
        case BYTE:
            val = Byte.toString((byte) n.value());
            break;
        // default: // Int, Short, Byte
        // if (n.value() >= 0x80000000L)
        // val = "0x" + Long.toHexString(n.value());
        // else
        // val = Long.toString((int) n.value());
        }
        w.write(val);
    }

    @Override
    public void visit(StringLit_c n) {
        w.write("\"");
        w.write(StringUtil.escape(n.stringValue()));
        w.write("\"");
        // removed it since now we pass captured environment explicitly,
        // therefore the workaround is no longer needed.
        // w.write(".toString()"); // workaround for XTENLANG-2006. TODO remove
        // this when the bug get fixed.
    }

    @Override
    public void visit(Local_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(X10New_c c) {

        Type type = c.objectType().type();
        X10ConstructorInstance mi = c.constructorInstance();

        if (er.printNativeNew(c, mi)) return;
        
        if (supportConstructorSplitting && config.OPTIMIZE && config.SPLIT_CONSTRUCTORS
                && !type.name().toString().startsWith(ClosureRemover.STATIC_NESTED_CLASS_BASE_NAME)
                && !ConstructorSplitterVisitor.cannotSplitConstructor(Types.baseType(type))
                && !type.fullName().toString().startsWith("java.")) {

            printAllocationCall(type);

            w.write(".");

            w.write(CONSTRUCTOR_METHOD_NAME);
            printConstructorArgumentList(c, c, c.constructorInstance(), null);

            return;
        }

        if (c.qualifier() != null) {
            tr.print(c, c.qualifier(), w);
            w.write(".");
        }

        w.write("new ");

        if (c.qualifier() == null) {
            er.printType(type, PRINT_TYPE_PARAMS | NO_VARIANCE);
        } else {
            er.printType(type, PRINT_TYPE_PARAMS | NO_VARIANCE | NO_QUALIFIER);
        }

        printConstructorArgumentList(c, c, mi, type);

        if (c.body() != null) {
            w.write("{");
            tr.print(c, c.body(), w);
            w.write("}");
        }
    }

    private void printExtraArgments(X10ConstructorInstance mi) {
        ConstructorDef md = mi.def();
        if (md instanceof X10ConstructorDef) {
            int cid = getConstructorId((X10ConstructorDef) md);
            if (cid != -1) {
                w.write(",");
                for (int i = 0; i < cid + 1; i++) {
                    if (i % 256 == 0) {
                        if (i != 0) w.write(") null,");
                        w.write("(" + JAVA_LANG_CLASS);
                    } else {
                        w.write("[]");
                    }
                }
                w.write(") null");
            }
        }
    }

    @Override
    public void visit(Special_c n) {
        Context c = tr.context();
        /*
         * The InnerClassRemover will have replaced the
         */
        if (c.inAnonObjectScope() && n.kind() == Special.THIS && c.inStaticContext()) {
            w.write(InnerClassRemover.OUTER_FIELD_NAME.toString());
            return;
        }
        if ((((X10Translator) tr).inInnerClass() || c.inAnonObjectScope()) && n.qualifier() == null
                && n.kind() != X10Special.SELF && n.kind() != Special.SUPER) {
            er.printType(n.type(), 0);
            w.write(".");
        } else if (n.qualifier() != null && n.kind() != X10Special.SELF && n.kind() != Special.SUPER) {
            er.printType(n.qualifier().type(), 0);
            w.write(".");
        }

        w.write(n.kind().toString());
    }

    @Override
    public void visit(ParExpr_c n) {
        n.translate(w, tr);
    }

    public void visit(SubtypeTest_c n) {
        TypeNode sub = n.subtype();
        TypeNode sup = n.supertype();

        w.write("((");
        new RuntimeTypeExpander(er, sub.type()).expand(tr);
        w.write(")");
        if (n.equals()) {
            w.write(".equals(");
        } else {
            w.write(".isSubtype(");
        }
        new RuntimeTypeExpander(er, sup.type()).expand(tr);
        w.write("))");
    }

    @Override
    public void visit(HasZeroTest_c n) {
        TypeNode sub = n.parameter();

        w.write("((");
        new RuntimeTypeExpander(er, sub.type()).expand(tr);
        w.write(").hasZero())");
    }

    @Override
    public void visit(Tuple_c c) {
        Type t = Types.getParameterType(c.type(), 0);

        w.write("x10.core.RailFactory.<");
        er.printType(t, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
        w.write(">");
        w.write("makeArrayFromJavaArray(");
        new RuntimeTypeExpander(er, t).expand();
        w.write(", ");
        if (Types.baseType(t) instanceof ParameterType) {
            new RuntimeTypeExpander(er, t).expand();
            w.write(".makeArray(");
            new Join(er, ", ", c.arguments()).expand();
            w.write(")");
        } else {
            w.write("new ");
            er.printType(t, 0);
            w.write("[] {");
            new Join(er, ", ", c.arguments()).expand();
            w.write("}");
        }
        w.write(")");
    }

    @Override
    public void visit(Unary_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(X10Unary_c n) {
        Expr left = n.expr();
        Type l = left.type();
        Unary.Operator op = n.operator();

        if (op == Unary.POST_DEC || op == Unary.POST_INC || op == Unary.PRE_DEC || op == Unary.PRE_INC) {
            Expr expr = left;
            Type t = left.type();

            Expr target = null;
            List<Expr> args = null;
            List<TypeNode> typeArgs = null;
            MethodInstance mi = null;

            // Handle a(i)++ and a.apply(i)++
            if (expr instanceof ClosureCall) {
                ClosureCall e = (ClosureCall) expr;
                target = e.target();
                args = e.arguments();
                typeArgs = Collections.<TypeNode> emptyList(); // e.typeArgs();
                mi = e.closureInstance();
            } else if (expr instanceof X10Call) {
                X10Call e = (X10Call) expr;
                if (e.target() instanceof Expr && e.name().id() == ClosureCall.APPLY) {
                    target = (Expr) e.target();
                    args = e.arguments();
                    typeArgs = e.typeArguments();
                    mi = e.methodInstance();
                }
            }

            if (mi != null) {
                // MethodInstance setter = null;

                List<Type> setArgTypes = new ArrayList<Type>();
                List<Type> setTypeArgs = new ArrayList<Type>();
                for (Expr e : args) {
                    setArgTypes.add(e.type());
                }
                setArgTypes.add(expr.type());
                for (TypeNode tn : typeArgs) {
                    setTypeArgs.add(tn.type());
                }
                // try {
                // setter = ts.findMethod(target.type(), ts.MethodMatcher(t,
                // SettableAssign.SET, setTypeArgs, setArgTypes, tr.context()));
                // }
                // catch (SemanticException e) {
                // }

                // TODO: handle type args
                // TODO: handle setter method

                w.write("new " + JAVA_IO_SERIALIZABLE + "() {");
                w.allowBreak(0, " ");
                w.write("final ");
                er.printType(t, PRINT_TYPE_PARAMS);
                w.write(" eval(");
                er.printType(target.type(), PRINT_TYPE_PARAMS);
                w.write(" target");
                {
                    int i = 0;
                    for (Expr e : args) {
                        w.write(", ");
                        er.printType(e.type(), PRINT_TYPE_PARAMS);
                        w.write(" a" + (i + 1));
                        i++;
                    }
                }
                w.write(") {");
                w.allowBreak(0, " ");
                er.printType(left.type(), PRINT_TYPE_PARAMS);
                w.write(" old = ");
                String pat = Emitter.getJavaImplForDef(mi.x10Def());
                if (pat != null) {
                    Object[] components = new Object[args.size() + 1];
                    int j = 0;
                    components[j++] = "target";
                    {
                        int i = 0;
                        for (Expr e : args) {
                            components[j++] = "a" + (i + 1);
                            i++;
                        }
                    }
                    er.dumpRegex("Native", components, tr, pat);
                } else {
                    w.write("target.$apply(");
                    {
                        int i = 0;
                        for (Expr e : args) {
                            if (i > 0) w.write(", ");
                            w.write("a" + (i + 1));
                            i++;
                        }
                    }
                    w.write(")");
                }
                w.write(";");
                w.allowBreak(0, " ");
                er.printType(left.type(), PRINT_TYPE_PARAMS);
                w.write(" neu = (");
                er.printType(left.type(), PRINT_TYPE_PARAMS);
                w.write(") old");
                w.write((op == Unary.POST_INC || op == Unary.PRE_INC ? "+" : "-") + "1");
                w.write(";");
                w.allowBreak(0, " ");
                w.write("target.set(neu");
                {
                    int i = 0;
                    for (Expr e : args) {
                        w.write(", ");
                        w.write("a" + (i + 1));
                        i++;
                    }
                }
                w.write(");");
                w.allowBreak(0, " ");
                w.write("return ");
                w.write((op == Unary.PRE_DEC || op == Unary.PRE_INC ? "neu" : "old"));
                w.write(";");
                w.allowBreak(0, " ");
                w.write("}");
                w.allowBreak(0, " ");
                w.write("}.eval(");
                tr.print(n, target, w);
                w.write(", ");
                new Join(er, ", ", args).expand(tr);
                w.write(")");

                return;
            }
        }

        if (l.isNumeric() || l.isBoolean()) {
            visit((Unary_c) n);
            return;
        }

        Name methodName = X10Unary_c.unaryMethodName(op);
        if (methodName != null)
            er.generateStaticOrInstanceCall(n.position(), left, methodName);
        else
            throw new InternalCompilerError("No method to implement " + n, n.position());
        return;
    }

    @Override
    public void visit(final Closure_c n) {
        // System.out.println(this + ": " + n.position() + ": " + n +
        // " captures "+n.closureDef().capturedEnvironment());
        Translator tr2 = ((X10Translator) tr).inInnerClass(true);
        tr2 = tr2.context(n.enterScope(tr2.context()));

        List<Expander> typeArgs = new ArrayList<Expander>();
        for (final Formal f : n.formals()) {
            TypeExpander ft = new TypeExpander(er, f.type().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
            typeArgs.add(ft); // must box formals
        }

        boolean runAsync = false;
        MethodInstance_c mi = (MethodInstance_c) n.methodContainer();
        if (mi != null && mi.container().isClass()
                && ((X10ClassType) mi.container().toClass()).fullName().toString().equals("x10.lang.Runtime")
                && mi.signature().startsWith("runAsync")) {
            runAsync = true;
        }

        TypeExpander ret = new TypeExpander(er, n.returnType().type(), true, true, false);
        if (!n.returnType().type().isVoid()) {
            typeArgs.add(ret);
            w.write("new " + X10_FUN_CLASS_PREFIX + "_0_" + n.formals().size());
        } else {
            w.write("new " + X10_VOIDFUN_CLASS_PREFIX + "_0_" + n.formals().size());
        }

        if (typeArgs.size() > 0) {
            w.write("<");
            new Join(er, ", ", typeArgs).expand(tr2);
            w.write(">");
        }

        w.write("() {");

        List<Formal> formals = n.formals();
        // bridge
        boolean bridge = needBridge(n);
        if (bridge) {
            w.write("public final ");
            if (isSelfDispatch && n.returnType().type().isVoid() && n.formals().size() != 0) {
                w.write(JAVA_LANG_OBJECT);
            } else {
                ret.expand(tr2);
            }

            w.write(" ");

            er.printApplyMethodName(n, true);

            // print the formals
            w.write("(");
            for (int i = 0; i < formals.size(); ++i) {
                if (i != 0) w.write(",");
                er.printFormal(tr2, n, formals.get(i), true);

                if (isSelfDispatch) {
                    w.write(", ");
                    w.write(X10_RUNTIME_TYPE_CLASS);
                    w.write(" ");
                    w.write("t" + (i + 1));
                }
            }
            w.write(") { ");
            if (!n.returnType().type().isVoid()) {
                w.write("return ");
            }

            er.printApplyMethodName(n, Types.baseType(n.returnType().type()) instanceof ParameterType);

            w.write("(");
            String delim = "";
            for (Formal f : formals) {
                w.write(delim);
                delim = ",";
                if (isPrimitiveRepedJava(f.type().type())) {
                    // TODO:CAST
                    w.write("(");
                    er.printType(f.type().type(), 0);
                    w.write(")");
                }
                w.write(f.name().toString());
            }
            w.write(");");
            if (isSelfDispatch && n.returnType().type().isVoid() && n.formals().size() != 0) {
                w.write("return null;");
            }
            w.write("}");
            w.newline();
        }

        w.write("public final ");
        if (isSelfDispatch && !bridge && n.returnType().type().isVoid() && n.formals().size() != 0) {
            w.write(JAVA_LANG_OBJECT);
        } else {
            er.printType(n.returnType().type(), PRINT_TYPE_PARAMS);
        }

        w.write(" ");

        er.printApplyMethodName(n, Types.baseType(n.returnType().type()) instanceof ParameterType);

        w.write("(");
        for (int i = 0; i < formals.size(); i++) {
            if (i != 0) w.write(", ");
            er.printFormal(tr2, n, formals.get(i), false);
            if (isSelfDispatch && !bridge) {
                w.write(", ");
                w.write(X10_RUNTIME_TYPE_CLASS);
                w.write(" ");
                w.write("t" + (i + 1));
            }
        }

        w.write(")");

        // print the closure body
        w.write(" { ");

        List<Stmt> statements = n.body().statements();
        // boolean throwException = false;
        // boolean throwThrowable = false;
        // for (Stmt stmt : statements) {
        // final List<Type> throwables = getThrowables(stmt);
        // if (throwables == null) {
        // continue;
        // }
        // for (Type type : throwables) {
        // if (type != null) {
        // if (type.isSubtype(tr.typeSystem().Exception(), tr.context()) &&
        // !type.isSubtype(tr.typeSystem().RuntimeException(), tr.context())) {
        // throwException = true;
        // } else if (!type.isSubtype(tr.typeSystem().Exception(), tr.context())
        // && !type.isSubtype(tr.typeSystem().Error(), tr.context())) {
        // throwThrowable = true;
        // }
        // }
        // }
        // }

        TryCatchExpander tryCatchExpander = new TryCatchExpander(w, er, n.body(), null);
        if (runAsync) {
            tryCatchExpander.addCatchBlock("x10.runtime.impl.java.WrappedThrowable", "ex", new Expander(er) {
                public void expand(Translator tr) {
                    w.write("x10.lang.Runtime.pushException(ex);");
                }
            });
        }
        // if (throwThrowable) {
        // tryCatchExpander.addCatchBlock("java.lang.RuntimeException", "ex",
        // new Expander(er) {
        // public void expand(Translator tr) {
        // w.write("throw ex;");
        // }
        // });
        // tryCatchExpander.addCatchBlock("java.lang.Error", "er", new
        // Expander(er) {
        // public void expand(Translator tr) {
        // w.write("throw er;");
        // }
        // });
        // if (runAsync) {
        // tryCatchExpander.addCatchBlock("java.lang.Throwable", "t", new
        // Expander(er) {
        // public void expand(Translator tr) {
        // w.write("x10.lang.Runtime.pushException(new x10.runtime.impl.java.WrappedThrowable(t));");
        // }
        // });
        // } else {
        // tryCatchExpander.addCatchBlock("java.lang.Throwable", "t", new
        // Expander(er) {
        // public void expand(Translator tr) {
        // w.write("throw new x10.runtime.impl.java.WrappedThrowable(t);");
        // }
        // });
        // }
        // tryCatchExpander.expand(tr2);
        // }
        // else
        // if (throwException) {
        // tryCatchExpander.addCatchBlock("java.lang.RuntimeException", "ex",
        // new Expander(er) {
        // public void expand(Translator tr) {
        // w.write("throw ex;");
        // }
        // });
        //
        // if (runAsync) {
        // tryCatchExpander.addCatchBlock("java.lang.Exception", "ex", new
        // Expander(er) {
        // public void expand(Translator tr) {
        // w.write("x10.lang.Runtime.pushException(new x10.runtime.impl.java.WrappedThrowable(ex));");
        // }
        // });
        // } else {
        // tryCatchExpander.addCatchBlock("java.lang.Exception", "ex", new
        // Expander(er) {
        // public void expand(Translator tr) {
        // w.write("throw new x10.runtime.impl.java.WrappedThrowable(ex);");
        // }
        // });
        // }
        // tryCatchExpander.expand(tr2);
        // } else
        //
        if (runAsync) {
            tryCatchExpander.expand(tr2);
        } else {
            er.prettyPrint(n.body(), tr2);
        }

        if (isSelfDispatch && !bridge && n.returnType().type().isVoid() && n.formals().size() != 0) {
            w.write("return null;");
        }

        w.write("}");
        w.newline();

        Type type = n.type();
        type = Types.baseType(type);
        if (type instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) type;
            X10ClassDef def = ct.x10Def();

            // XTENLANG-1102
            Set<ClassDef> visited = CollectionFactory.newHashSet();

            visited = CollectionFactory.newHashSet();
            visited.add(def);
            if (!def.flags().isInterface()) {
                List<Type> types = new ArrayList<Type>();
                LinkedList<Type> worklist = new LinkedList<Type>();
                for (Type t : def.asType().interfaces()) {
                    Type it = Types.baseType(t);
                    worklist.add(it);
                }
                while (!worklist.isEmpty()) {
                    Type it = worklist.removeFirst();
                    if (it instanceof X10ClassType) {
                        X10ClassType ct2 = (X10ClassType) it;
                        X10ClassDef idef = ct2.x10Def();

                        if (visited.contains(idef)) continue;
                        visited.add(idef);

                        for (Type t : ct2.interfaces()) {
                            Type it2 = Types.baseType(t);
                            worklist.add(it2);
                        }

                        if (ct2.typeArguments() != null) types.addAll(ct2.typeArguments());
                    }
                }
                // To extend Any, the type requires getRTT even if it has no
                // type params (e.g. VoidFun_0_0).
                // if (types.size() > 0) {
                w.write("public x10.rtt.RuntimeType<?> " + X10PrettyPrinterVisitor.GETRTT_NAME + "() { return "
                        + X10PrettyPrinterVisitor.RTT_NAME + "; }");
                w.write("public x10.rtt.Type<?> " + X10PrettyPrinterVisitor.GETPARAM_NAME + "(int i) {");
                for (int i = 0; i < types.size(); i++) {
                    w.write("if (i ==" + i + ")");
                    Type t = types.get(i);
                    w.write(" return ");
                    new RuntimeTypeExpander(er, t).expand();
                    w.write(";");
                }
                w.write("return null;");
                w.newline();
                w.write("}");
                w.newline();
                // }
            }
        }

        w.write("}");
    }

    private boolean needBridge(final Closure_c n) {
        return containsPrimitive(n) || !n.returnType().type().isVoid()
                && !(Types.baseType(n.returnType().type()) instanceof ParameterType);
    }

    // private boolean throwException(List<Stmt> statements) {
    // for (Stmt stmt : statements) {
    // final List<Type> exceptions = getThrowables(stmt);
    // if (exceptions == null) {
    // continue;
    // }
    // for (Type type : exceptions) {
    // if (type != null) {
    // if (type.isSubtype(tr.typeSystem().Exception(), tr.context()) &&
    // !type.isSubtype(tr.typeSystem().RuntimeException(), tr.context())) {
    // return true;
    // } else if (!type.isSubtype(tr.typeSystem().Exception(), tr.context()) &&
    // !type.isSubtype(tr.typeSystem().Error(), tr.context())) {
    // return true;
    // }
    // }
    // }
    // }
    // return false;
    // }
    // private static List<Type> getThrowables(Stmt stmt) {
    // final List<Type> throwables = new ArrayList<Type>();
    // stmt.visit(
    // new NodeVisitor() {
    // @Override
    // public Node leave(Node old, Node n, NodeVisitor v) {
    // /* if (n instanceof X10Call_c) {
    // List<Type> throwTypes = ((X10Call_c) n).methodInstance().throwTypes();
    // if (throwTypes != null) throwables.addAll(throwTypes);
    // }
    // if (n instanceof Throw) {
    // throwables.add(((Throw) n).expr().type());
    // }
    // if (n instanceof X10New_c) {
    // List<Type> throwTypes = ((X10New_c) n).procedureInstance().throwTypes();
    // if (throwTypes != null) throwables.addAll(throwTypes);
    // }
    // */
    // return n;
    // }
    // });
    // return throwables;
    // }

    private boolean containsPrimitive(Closure_c n) {
        Type t = n.returnType().type();
        if (isPrimitiveRepedJava(t)) {
            return true;
        }
        for (Formal f : n.formals()) {
            Type type = f.type().type();
            if (isPrimitiveRepedJava(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void visit(ClosureCall_c c) {
        Expr target = c.target();
        Type targetType = target.type();
        boolean base = false;

        TypeSystem xts = targetType.typeSystem();

        MethodInstance mi = c.closureInstance();

        Expr expr = target;
        if (target instanceof ParExpr) {
            expr = ((ParExpr) target).expr();
        }
        boolean newClosure = expr instanceof Closure_c;

        if (!newClosure) {
            // TODO:CAST
            w.write("(");
            w.write("(");
            er.printType(mi.container(), PRINT_TYPE_PARAMS);
            w.write(")");

            if (xts.isParameterType(targetType)) {
                w.write(X10_RTT_TYPES);
                w.write(".conversion(");
                new RuntimeTypeExpander(er, Types.baseType(mi.container())).expand(tr);
                w.write(",");
                c.printSubExpr(target, w, tr);
                w.write(")");
            } else {
                c.printSubExpr(target, w, tr);
            }
            w.write(")");
        } else {
            c.printSubExpr(target, w, tr);
        }

        w.write(".");

        er.printApplyMethodName(mi, newClosure);

        // print the argument list
        w.write("(");
        w.begin(0);

        for (Iterator<Type> i = mi.typeParameters().iterator(); i.hasNext();) {
            final Type at = i.next();
            new RuntimeTypeExpander(er, at).expand(tr);
            if (i.hasNext() || c.arguments().size() > 0) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }

        List<Expr> l = c.arguments();
        for (int i = 0; i < l.size(); i++) {
            Expr e = l.get(i);

            Type castType = mi.formalTypes().get(i);

            if (isString(e.type(), tr.context()) && !isString(castType, tr.context())) {

                w.write("(");
                er.printType(castType, 0);
                w.write(")");

                if (xts.isParameterType(castType)) {
                    w.write(X10_RTT_TYPES);
                    w.write(".conversion(");
                    new RuntimeTypeExpander(er, Types.baseType(castType)).expand(tr);
                    w.write(",");
                } else {
                    w.write(X10_CORE_STRING);
                    w.write(".box(");
                }
            }

            c.print(e, w, tr);

            if (isMutableStruct(e.type())) {
                w.write(".clone()");
            }

            if (isString(e.type(), tr.context()) && !isString(castType, tr.context())) {
                w.write(")");
            }

            if (isSelfDispatch && (!newClosure || !needBridge((Closure_c) expr))) {
                w.write(",");
                new RuntimeTypeExpander(er, mi.formalTypes().get(i)).expand();
            }
            if (i != l.size() - 1) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }
        w.end();
        w.write(")");
    }

    @Override
    public void visit(StmtExpr_c n) {
        final Context c = tr.context();
        final ArrayList<LocalInstance> capturedVars = new ArrayList<LocalInstance>();
        // This visitor (a) finds all captured local variables,
        // (b) adds a qualifier to every "this", and
        // (c) rewrites fields and calls to use an explicit "this" target.
        n = (StmtExpr_c) n.visit(new ContextVisitor(tr.job(), tr.typeSystem(), tr.nodeFactory()) {
            public Node leaveCall(Node n) {
                if (n instanceof Local) {
                    Local l = (Local) n;
                    assert (l.name() != null) : l.position().toString();
                    VarInstance<?> found = c.findVariableSilent(l.name().id());
                    if (found != null) {
                        VarInstance<?> local = context().findVariableSilent(l.name().id());
                        if (found.equals(local)) {
                            assert (found.equals(l.localInstance())) : l.toString();
                            capturedVars.add(l.localInstance());
                        }
                    }
                }
                if (n instanceof Field_c) {
                    Field_c f = (Field_c) n;
                    return f.target(f.target()).targetImplicit(false);
                }
                if (n instanceof X10Call_c) {
                    X10Call_c l = (X10Call_c) n;
                    return l.target(l.target()).targetImplicit(false);
                }
                if (n instanceof Special_c) {
                    NodeFactory nf = nodeFactory();
                    Special_c s = (Special_c) n;
                    if (s.qualifier() == null) {
                        return s.qualifier(nf.CanonicalTypeNode(n.position(), s.type()));
                    }
                }
                return n;
            }
        }.context(c.pushBlock()));

        if (supportConstructorSplitting && config.OPTIMIZE && config.SPLIT_CONSTRUCTORS) {
            w.write("(new " + JAVA_IO_SERIALIZABLE + "() { ");
            er.printType(n.type(), PRINT_TYPE_PARAMS);
            w.write(" eval(");
            String delim = null;
            for (LocalInstance li : capturedVars) {
                if (!li.flags().isFinal()) {
                    if (delim == null) {
                        delim = ",";
                    } else {
                        w.write(",");
                    }
                    w.write("final ");
                    er.printType(li.type(), PRINT_TYPE_PARAMS);
                    w.write(" ");
                    w.write(Emitter.mangleToJava(li.name()));
                    // System.err.println("Bad statement expression: " + n +
                    // " at "
                    // + n.position()); // DEBUG
                    // n.dump(System.err); // DEBUG
                    // throw new
                    // InternalCompilerError("Statement expression uses non-final variable "
                    // + li + "(at "
                    // + li.position() + ") from the outer scope",
                    // n.position());
                }
            }
            w.write(") {");
            w.newline(4);
            w.begin(0);
            Translator tr = this.tr.context(c.pushBlock());
            List<Stmt> statements = n.statements();
            for (Stmt stmt : statements) {
                tr.print(n, stmt, w);
                w.newline();
            }
            w.write("return ");
            tr.print(n, n.result(), w);
            w.write(";");
            w.end();
            w.newline();
            w.write("} }.eval(");

            delim = null;
            for (LocalInstance li : capturedVars) {
                if (!li.flags().isFinal()) {
                    if (delim == null) {
                        delim = ",";
                    } else {
                        w.write(",");
                    }
                    w.write(Emitter.mangleToJava(li.name()));
                }
            }
            w.write("))");
            return;
        }
        for (LocalInstance li : capturedVars) {
            if (!li.flags().isFinal()) {
                System.err.println("Bad statement expression: " + n + " at " + n.position()); // DEBUG
                n.dump(System.err); // DEBUG
                throw new InternalCompilerError("Statement expression uses non-final variable " + li + "(at "
                        + li.position() + ") from the outer scope", n.position());
            }
        }
        w.write("(new " + JAVA_IO_SERIALIZABLE + "() { ");
        er.printType(n.type(), PRINT_TYPE_PARAMS);
        w.write(" eval() {");
        w.newline(4);
        w.begin(0);
        Translator tr = this.tr.context(c.pushBlock());
        List<Stmt> statements = n.statements();
        for (Stmt stmt : statements) {
            tr.print(n, stmt, w);
            w.newline();
        }
        w.write("return ");
        tr.print(n, n.result(), w);
        w.write(";");
        w.end();
        w.newline();
        w.write("} }.eval())");
    }

    // ////////////////////////////////
    // end of Expr
    // ////////////////////////////////

    @Override
    public void visit(FieldDecl_c n) {
        Flags flags;
        if (!n.flags().flags().isStatic()) {
            flags = n.flags().flags().clearFinal();
        } else {
            flags = n.flags().flags();
        }
        flags = flags.retainJava(); // ensure that X10Flags are not printed out
                                    // .. javac will not know what to do with
                                    // them.

        FieldDecl_c javaNode = (FieldDecl_c) n.flags(n.flags().flags(flags));

        // same with FiledDecl_c#prettyPrint(CodeWriter w, PrettyPrinter tr)
        FieldDef fieldDef = javaNode.fieldDef();
        boolean isInterface = fieldDef != null && fieldDef.container() != null
                && fieldDef.container().get().toClass().flags().isInterface();

        Flags f = javaNode.flags().flags();

        if (isInterface) {
            f = f.clearPublic();
            f = f.clearStatic();
            f = f.clearFinal();
        }

        w.write(f.translateJava());
        er.printType(javaNode.type().type(), PRINT_TYPE_PARAMS);
        // tr.print(javaNode, javaNode.type(), w);
        w.allowBreak(2, 2, " ", 1);
        tr.print(javaNode, javaNode.name(), w);

        if (javaNode.init() != null) {
            w.write(" =");
            w.allowBreak(2, " ");

            // X10 unique
            er.coerce(javaNode, javaNode.init(), javaNode.type().type());
        }

        w.write(";");
    }

    @Override
    public void visit(Formal_c f) {
        if (f.name().id().toString().equals("")) f = (Formal_c) f.name(f.name().id(Name.makeFresh("a")));
        f.translate(w, tr);
    }

    @Override
    public void visit(X10MethodDecl_c n) {
        if (er.printMainMethod(n)) {
            return;
        }
        er.generateMethodDecl(n, false);
    }

    // ////////////////////////////////
    // Stmt
    // ////////////////////////////////
    @Override
    public void visit(Block_c n) {
        String s = Emitter.getJavaImplForStmt(n, tr.typeSystem());
        if (s != null) {
            w.write(s);
        } else {
            n.translate(w, tr);
        }
    }

    @Override
    public void visit(StmtSeq_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(SwitchBlock_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(Assert_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(AssignPropertyCall_c n) {
        // TODO: initialize properties in the Java constructor
        List<X10FieldInstance> definedProperties = n.properties();
        List<Expr> arguments = n.arguments();
        int aSize = arguments.size();
        assert (definedProperties.size() == aSize);

        for (int i = 0; i < aSize; i++) {
            Expr arg = arguments.get(i);
            X10FieldInstance fi = definedProperties.get(i);
            w.write("this.");
            w.write(Emitter.mangleToJava(fi.name()));
            w.write(" = ");
            er.coerce(n, arg, fi.type());
            w.write(";");
            w.newline();
        }
    }

    @Override
    public void visit(Branch_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(Case_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(Catch_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(X10ConstructorCall_c c) {
        if (supportConstructorSplitting && config.OPTIMIZE && config.SPLIT_CONSTRUCTORS) {
            Expr target = c.target();
            if (target == null || target instanceof Special) {
                if (c.kind() == ConstructorCall.SUPER) {
                    ContainerType ct = c.constructorInstance().container();
                    if (Types.baseType(ct).typeEquals(tr.typeSystem().Object(), tr.context())
                            || Emitter.isNativeRepedToJava(ct) || er.isNativeClassToJava(ct)) {
                        return;
                    }
                    w.write("super");
                } else {
                    w.write("this");
                }
            } else {
                target.translate(w, tr);
            }
            w.write(".");
            w.write(CONSTRUCTOR_METHOD_NAME);
            printConstructorArgumentList(c, c, c.constructorInstance(), null);
            w.write(";");
            return;
        }
        printConstructorCallForJavaCtor(c);
    }

    private void printConstructorCallForJavaCtor(X10ConstructorCall_c c) {
        if (c.qualifier() != null) {
            tr.print(c, c.qualifier(), w);
            w.write(".");
        }
        w.write(c.kind() == ConstructorCall.THIS ? "this" : "super");
        printConstructorArgumentList(c, c, c.constructorInstance(), null);
        w.write(";");
    }

    private void printConstructorArgumentList(Node_c c, X10ProcedureCall p, X10ConstructorInstance mi, Type type) {
        w.write("(");
        w.begin(0);

        X10ClassType ct = (X10ClassType) mi.container();

        List<Type> ta = ct.typeArguments();
        boolean isJavaNative = type != null ? Emitter.isNativeRepedToJava(type) : false;
        if (ta != null && ta.size() > 0 && !isJavaNative) {
            printArgumentsForTypeParams(ta, p.arguments().size());
        }

        List<Expr> l = p.arguments();
        for (int i = 0; i < l.size(); i++) {
            Expr e = l.get(i);
            if (i < mi.formalTypes().size()) { // FIXME This is a workaround
                Type castType = mi.formalTypes().get(i);
                TypeSystem xts = tr.typeSystem();
                if (isString(e.type(), tr.context()) && !isString(castType, tr.context())) {

                    w.write("(");
                    er.printType(castType, 0);
                    w.write(")");

                    if (xts.isParameterType(castType)) {
                        w.write(X10_RTT_TYPES);
                        w.write(".conversion(");
                        new RuntimeTypeExpander(er, Types.baseType(castType)).expand(tr);
                        w.write(",");
                    } else {
                        w.write(X10_CORE_STRING);
                        w.write(".box(");
                    }
                    c.print(e, w, tr);
                    w.write(")");
                } else if (isSelfDispatch && !castType.typeEquals(e.type(), tr.context())) {
                    // TODO:CAST
                    w.write("(");
                    w.write("(");
                    er.printType(castType, PRINT_TYPE_PARAMS);
                    w.write(")");
                    w.write("(");
                    c.print(e, w, tr);
                    w.write(")");
                    w.write(")");
                } else {
                    c.print(e, w, tr);
                }
            } else {
                c.print(e, w, tr);
            }
            if (isMutableStruct(e.type())) {
                w.write(".clone()");
            }

            if (i != l.size() - 1) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }

        printExtraArgments(mi);

        w.end();
        w.write(")");
    }

    @Override
    public void visit(Empty_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(Eval_c n) {
        boolean semi = tr.appendSemicolon(true);
        Expr expr = n.expr();
        // XTENLANG-2000
        if (expr instanceof X10Call) {
            // support for back-end method inlining
            if (er.isMethodInlineTarget(tr.typeSystem(), ((X10Call) expr).target().type())
                    && ((X10Call) expr).methodInstance().name() == ClosureCall.APPLY) {
                w.write(X10_RUNTIME_UTIL_UTIL + ".eval(");
                n.print(expr, w, tr);
                w.write(")");
            } else if (er.isMethodInlineTarget(tr.typeSystem(), ((X10Call) expr).target().type())
                    && ((X10Call) expr).methodInstance().name() == SettableAssign.SET) {
                n.print(expr, w, tr);
            }
            // support for @Native
            else if (!expr.type().isVoid()
                    && Emitter.getJavaImplForDef(((X10Call) expr).methodInstance().x10Def()) != null) {
                w.write(X10_RUNTIME_UTIL_UTIL + ".eval(");
                n.print(expr, w, tr);
                w.write(")");
            } else {
                n.print(expr, w, tr);
            }
        }
        // when expr is StatementExpression(Assignment ||
        // [Pre/Post][De/In]crementExpression || MethodInvocation ||
        // ClassInstanceCreationExpression)
        else if (expr instanceof ClosureCall || expr instanceof Assign || expr instanceof Unary
                || expr instanceof X10New) {
            n.print(expr, w, tr);
        }
        // not a legal java statement
        else {
            w.write(X10_RUNTIME_UTIL_UTIL + ".eval(");
            n.print(expr, w, tr);
            w.write(")");
        }
        if (semi) {
            w.write(";");
        }
        tr.appendSemicolon(semi);
    }

    @Override
    public void visit(If_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(Labeled_c n) {
        Stmt statement = n.statement();
        if (statement instanceof Block_c) {
            w.write(n.labelNode() + ": ");
            w.write("{");
            Block_c block = (Block_c) statement;
            for (Stmt s : block.statements()) {
                tr.print(n, s, w);
            }
            w.write("}");
        } else {
            w.write(n.labelNode() + ": ");
            tr.print(n, statement, w);
        }
    }

    @Override
    public void visit(X10LocalDecl_c n) {

        // same with FieldDecl_c#prettyPrint(CodeWriter w, PrettyPrinter tr)
        boolean printSemi = tr.appendSemicolon(true);
        boolean printType = tr.printType(true);

        tr.print(n, n.flags(), w);
        if (printType) {
            tr.print(n, n.type(), w);
            w.write(" ");
        }
        tr.print(n, n.name(), w);

        if (n.init() != null) {
            w.write(" =");
            w.allowBreak(2, " ");

            // X10 unique
            er.coerce(n, n.init(), n.type().type());

            if (isMutableStruct(n.type().type())) {
                w.write(".clone()");
            }

        }
        // assign default value for access vars in at or async
        else if (!n.flags().flags().isFinal()) {
            Type type = Types.baseType(n.type().type());
            TypeSystem xts = tr.typeSystem();

            w.write(" =");
            w.allowBreak(2, " ");

            if (xts.isBoolean(type)) {
                w.write(" false");
            } else if (!xts.isParameterType(type) && !xts.isUnsigned(type) && (xts.isChar(type) || xts.isNumeric(type))) {
                w.write(" 0");
            } else {
                w.write(" null");
            }
        }

        if (printSemi) {
            w.write(";");
        }

        tr.printType(printType);
        tr.appendSemicolon(printSemi);
    }

    @Override
    public void visit(LocalTypeDef_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(Loop_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(Return_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(Switch_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(ForLoop_c f) {
        TypeSystem ts = tr.typeSystem();

        X10Formal form = (X10Formal) f.formal();

        Context context = tr.context();

        /* TODO: case: for (point p:D) -- discuss with vj */
        /*
         * handled cases: exploded syntax like: for (point p[i,j]:D) and for
         * (point [i,j]:D)
         */
        if (config.LOOP_OPTIMIZATIONS
                && form.hasExplodedVars()
                && (ts.isSubtype(f.domain().type(), ts.Region(), context) || ts.isSubtype(f.domain().type(), ts.Dist(),
                                                                                          context))
                && Types.toConstrainedType(f.domain().type()).isRect(context)) {
            String regVar = getId().toString();
            List<Name> idxs = new ArrayList<Name>();
            List<Name> lims = new ArrayList<Name>();
            List<Name> idx_vars = new ArrayList<Name>();
            List<Object> vals = new ArrayList<Object>();
            LocalDef[] lis = form.localInstances();
            int rank = lis.length;

            for (int i = 0; i < rank; i++) {
                idxs.add(getId());
                lims.add(getId());
                idx_vars.add(lis[i].name());
                vals.add(i);
            }

            Object body = f.body();

            if (!form.isUnnamed()) {
                // SYNOPSIS: #0=modifiers #1=type #2=var #3=idxs_list
                String regex = "#0 #1 #2 = x10.array.Point.make(#3);";
                Template template = Template.createTemplateFromRegex(er, "point-create", regex, form.flags(),
                                                                     form.type(), form.name(), new Join(er, ",", idxs));
                body = new Join(er, "\n", template, body);
            }

            // SYNOPSIS: #0=type #1=final_var #2=value_var
            String regex1 = "final #0 #1 = #2;";
            Loop loop1 = new Loop(er, "final-var-assign", regex1, new CircularList<String>("int"), idx_vars, idxs);
            body = new Join(er, "\n", loop1, body);

            // SYNOPSIS: #0=generated_index_var #1=region_var #2=index
            // #3=limit_var
            String regex2 = "for (int #0 = #1.min(#2), #3 = #1.max(#2); #0 <= #3; #0++)";
            Loop loop2 = new Loop(er, "forloop-mult-each", regex2, idxs, new CircularList<String>(regVar), vals, lims);
            // SYNOPSIS: #0=region_expr #1=region_var #2=rect_for_header
            // #3=rect_for_body #4=regular_for_iterator
            String regex = "{ x10.array.Region #1 = (#0).region(); if (#1.rect()) { #2 { #3 } } else { #4 }	}";
            er.dumpRegex("forloop-mult", new Object[] { f.domain(), regVar, loop2, body,
                    // new Template("forloop",
                    // form.flags(),
                    // form.type(),
                    // form.name(),
                    // regVar,
                    // new Join("\n", new Join("\n", f.locals()), f.body()),
                    // new TypeExpander(form.type().type(), PRINT_TYPE_PARAMS |
                    // BOX_PRIMITIVES)
                    // )
                    new Inline(er, "assert false;") }, tr, regex);
        } else {
            // SYNOPSIS: for (#0 #2: #1 in #3) #4 #5=unboxed type
            String regex = "for (x10.lang.Iterator #2__ = (#3).iterator(); #2__.hasNext$O(); ) { #0 #1 #2 = (#5) #2__.next$G(); #4 }";
            er.dumpRegex("forloop", new Object[] { form.flags(), form.type(), form.name(), f.domain(),
                    new Join(er, "\n", new Join(er, "\n", f.locals()), f.body()),
                    new TypeExpander(er, form.type().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES) }, tr, regex);
        }
    }

    @Override
    public void visit(Throw_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(Try_c c) {
        TryCatchExpander expander = new TryCatchExpander(w, er, c.tryBlock(), c.finallyBlock());
        final List<Catch> catchBlocks = c.catchBlocks();

        boolean isJavaCheckedExceptionCaught = false;
        for (int i = 0; i < catchBlocks.size(); ++i) {
            Type type = catchBlocks.get(i).catchType();
            if (type.toString().startsWith("java") && !type.isUncheckedException()) {
                // found Java checked exceptions caught here!!
                isJavaCheckedExceptionCaught = true;
            }
        }
        if (isJavaCheckedExceptionCaught) {
            final String temp = "$ex";
            expander.addCatchBlock("x10.runtime.impl.java.WrappedThrowable", temp, new Expander(er) {
                public void expand(Translator tr) {
                    w.newline();

                    for (int i = 0; i < catchBlocks.size(); ++i) {
                        Catch cb = catchBlocks.get(i);
                        Type type = cb.catchType();
                        if (!type.toString().startsWith("java") || type.isUncheckedException())
                        // if (type.isSubtype(tr.typeSystem().Error(),
                        // tr.context()) ||
                        // type.isSubtype(tr.typeSystem().RuntimeException(),
                        // tr.context()))
                        // nothing to do, since WrappedThrowable wrap only Java
                        // checked exceptions
                            continue;

                        if (i > 0) {
                            w.write("else ");
                        }
                        w.write("if (" + temp + ".getCause() instanceof ");
                        er.printType(cb.catchType(), 0);
                        w.write(") {");
                        w.newline();

                        cb.formal().translate(w, tr);
                        w.write(" = ");
                        // TODO:CAST
                        w.write("(");
                        er.printType(cb.catchType(), 0);
                        w.write(") " + temp + ".getCause();");
                        w.newline();

                        cb.body().translate(w, tr);
                        w.newline();

                        w.write("}");
                        w.newline();
                    }
                    w.write("else {");
                    w.newline();
                    w.write("throw " + temp + ";");
                    w.newline();
                    w.write("}");
                    w.newline();
                }
            });
        }

        for (int i = 0; i < catchBlocks.size(); ++i) {
            expander.addCatchBlock(catchBlocks.get(i));
        }

        expander.expand(tr);
    }

    // ////////////////////////////////
    // end of Stmt
    // ////////////////////////////////

    @Override
    public void visit(CanonicalTypeNode_c n) {
        Type t = n.type();
        if (t != null)
            er.printType(t, PRINT_TYPE_PARAMS);
        else
            // WARNING: it's important to delegate to the appropriate visit()
            // here!
            visit((Node) n);
    }

    @Override
    public void visit(TypeDecl_c n) {
        // Do not write anything.
        return;
    }

    @Override
    public void visit(Id_c n) {
        w.write(Emitter.mangleToJava(n.id()));
    }

    public static boolean isString(Type type, Context context) {
        return Types.baseType(type).typeEquals(type.typeSystem().String(), context);
    }

    public static boolean isPrimitiveRepedJava(Type t) {
        return t.isBoolean() || t.isByte() || t.isShort() || t.isInt() || t.isLong() || t.isFloat() || t.isDouble()
                || t.isChar();
    }

    public static boolean hasParams(Type t) {
        Type bt = Types.baseType(t);
        return (bt instanceof X10ClassType && ((X10ClassType) bt).hasParams());
    }

    public static boolean containsTypeParam(List<Ref<? extends Type>> list) {
        for (Ref<? extends Type> ref : list) {
            if (Emitter.containsTypeParam(ref.get())) {
                return true;
            }
        }
        return false;
    }

    /**
     * A list of one object that has an infinite circular iterator.
     */
    private static class CircularList<T> extends AbstractList<T> {
        private T o;

        public CircularList(T o) {
            this.o = o;
        }

        public Iterator<T> iterator() {
            return new Iterator<T>() {
                public boolean hasNext() {
                    return true;
                }

                public T next() {
                    return o;
                }

                public void remove() {
                    return;
                }
            };
        }

        public T get(int i) {
            return o;
        }

        public int size() {
            return -1;
        }
    }

    private static class ConstructorIdTypeForAnnotation extends X10ParsedClassType_c {
        private int i = -1;

        public ConstructorIdTypeForAnnotation(ClassDef def) {
            super(def);
        }

        private ConstructorIdTypeForAnnotation setIndex(int i) {
            assert i > -1;
            this.i = i;
            return this;
        }

        private int getIndex() {
            return i;
        }
    }
} // end of X10PrettyPrinterVisitor

