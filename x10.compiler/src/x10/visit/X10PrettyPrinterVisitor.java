/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.visit;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import polyglot.ast.NullLit_c;
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
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ContainerType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.FieldInstance_c;
import polyglot.types.Flags;
import polyglot.types.JavaArrayType;
import polyglot.types.JavaArrayType_c;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.Package;
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
import x10.ast.HasZeroTest_c;
import x10.ast.LocalTypeDef_c;
import x10.ast.OperatorNames;
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
import x10.emitter.Join;
import x10.emitter.RuntimeTypeExpander;
import x10.emitter.TryCatchExpander;
import x10.emitter.TypeExpander;
import x10.extension.X10Ext;
import x10.types.ConstrainedType;
import x10.types.FunctionType;
import x10.types.MethodInstance;
import x10.types.MethodInstance_c;
import x10.types.ParameterType;
import x10.types.ParameterType.Variance;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10CodeDef;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldDef_c;
import x10.types.X10FieldInstance;
import x10.types.X10FieldInstance_c;
import x10.types.X10MethodDef;
import x10.types.X10ParsedClassType_c;
import x10.types.X10TypeEnv;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.TypeConstraint;
import x10.util.AnnotationUtils;
import x10.util.CollectionFactory;
import x10.util.HierarchyUtils;
import x10c.ast.X10CBackingArrayAccessAssign_c;
import x10c.ast.X10CBackingArrayAccess_c;
import x10c.ast.X10CBackingArrayNewArray_c;
import x10c.ast.X10CBackingArray_c;
import x10c.types.X10CContext_c;
import x10c.visit.ClosureRemover;
import x10c.visit.InlineHelper;
import x10cpp.visit.ASTQuery;

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
    public static final String JAVA_IO_SERIALIZABLE = "java.io.Serializable";
    public static final String X10_CORE_REF = "x10.core.Ref";
    public static final String X10_CORE_STRUCT = "x10.core.Struct";
    public static final String X10_CORE_ANY = "x10.core.Any";

    public static final String CONSTRUCTOR_FOR_ZERO_VALUE_DUMMY_PARAM_TYPE = "java.lang.System";
    public static final String CONSTRUCTOR_FOR_ALLOCATION_DUMMY_PARAM_TYPE = "java.lang.System[]";

    public static final int PRINT_TYPE_PARAMS = 1;
    public static final int BOX_PRIMITIVES = 2;
    public static final int NO_VARIANCE = 4;
    public static final int NO_QUALIFIER = 8;

    public static final boolean useSelfDispatch = true;
    // XTENLANG-2993
    public static final boolean generateSpecialDispatcher = true;
    public static final boolean generateSpecialDispatcherNotUse = false;  // TODO to be removed
    public static final boolean supportGenericOverloading = true;
    public static final boolean supportConstructorSplitting = true;
    // XTENLANG-3058
    public static final boolean supportTypeConstraintsWithErasure = true;
    // XTENLANG-3090 (switched back to use java assertion)
    private static final boolean useJavaAssertion = true;
    // XTENLANG-3086
    public static final boolean supportUpperBounds = false;
    // Support numbered parameters for @Native (e.g. @Native("java","#0.toString()")) for backward compatibility.
    public static final boolean supportNumberedParameterForNative = true;
    // Expose existing special dispatcher through special interfaces (e.g. Arithmetic, Bitwise)
    public static final boolean exposeSpecialDispatcherThroughSpecialInterface = false;

    // N.B. should be as short as file name length which is valid on all supported platforms (e.g. NAME_MAX on linux).
    public static final int longestTypeName = 255; // use hash code if type name becomes longer than some threshold.

    public static final String X10_FUN_PACKAGE = "x10.core.fun";
    public static final String X10_FUN_CLASS_NAME_PREFIX = "Fun";
    public static final String X10_VOIDFUN_CLASS_NAME_PREFIX = "VoidFun";
    public static final String X10_FUN_CLASS_PREFIX = X10_FUN_PACKAGE+"."+X10_FUN_CLASS_NAME_PREFIX;
    public static final String X10_VOIDFUN_CLASS_PREFIX = X10_FUN_PACKAGE+"."+X10_VOIDFUN_CLASS_NAME_PREFIX;
    public static final String X10_RTT_TYPE = "x10.rtt.Type";
    public static final String X10_RTT_TYPES = "x10.rtt.Types";
    public static final String X10_RUNTIME_IMPL_JAVA_X10GENERATED = "x10.runtime.impl.java.X10Generated";
    public static final String X10_RUNTIME_IMPL_JAVA_RUNTIME = "x10.runtime.impl.java.Runtime";
    public static final String X10_RUNTIME_IMPL_JAVA_EVALUTILS = "x10.runtime.impl.java.EvalUtils";
    public static final String X10_RUNTIME_IMPL_JAVA_ARRAYUTILS = "x10.runtime.impl.java.ArrayUtils";

    public static final String X10_RUNTIME_IMPL_JAVA_THROWABLEUTILS = "x10.runtime.impl.java.ThrowableUtils";
    public static final String ENSURE_X10_EXCEPTION = "ensureX10Exception";
    
    public static final String JAVA_LANG_THROWABLE = "java.lang.Throwable";
    public static final String JAVA_LANG_ERROR = "java.lang.Error";

    public static final String MAIN_CLASS = "$Main";
    public static final String RTT_NAME = "$RTT";
    public static final String GETRTT_NAME = "$getRTT";
    public static final String GETPARAM_NAME = "$getParam";
    public static final String INITPARAMS_NAME = "$initParams";
    public static final String CONSTRUCTOR_METHOD_NAME = "$init";
    public static String CONSTRUCTOR_METHOD_NAME(ClassDef cd) {
        return InlineHelper.makeSuperBridgeName(cd, Name.make(CONSTRUCTOR_METHOD_NAME)).toString();
    }
    public static final String CONSTRUCTOR_METHOD_NAME_FOR_REFLECTION = "$initForReflection";
    public static final String BOX_METHOD_NAME = "$box";
    public static final String UNBOX_METHOD_NAME = "$unbox";

    private static final QName ASYNC_CLOSURE = QName.make("x10.compiler.AsyncClosure");
    private static final QName REMOTE_INVOCATION = QName.make("x10.compiler.RemoteInvocation");

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
        w.write("[(int)");
        er.prettyPrint(n.index(), tr);
        w.write("]");
        w.write(")");
    }

    public void visit(X10CBackingArrayAccessAssign_c n) {
        er.prettyPrint(n.array(), tr);
        w.write("[(int)");
        er.prettyPrint(n.index(), tr);
        w.write("]");

        w.write(n.operator().toString());

        boolean closeParen = false;
        if (isPrimitive(n.type()) && isBoxedType(n.right().type())) {
            closeParen = er.printUnboxConversion(n.type());
        }
        er.prettyPrint(n.right(), tr);
        if (closeParen) w.write(")");
    }

    public void visit(X10CBackingArrayNewArray_c n) {
        Type base = ((JavaArrayType) n.type()).base();
        if (base.isParameterType()) {
            w.write("(");
            er.printType(n.type(), 0);
            w.write(")");
            w.write(" ");
            // XTENLANG-3032 following code only works with non-primitives
            /*
            new RuntimeTypeExpander(er, base).expand();
            w.write(".makeArray(");
            w.write(n.dims().get(0).toString());
            w.write(")");
            */
            w.write("new java.lang.Object[");
            er.prettyPrint(n.dims().get(0), tr);
            w.write("]");
            return;
        }
        w.write("new ");
        er.printType(base, 0);
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

    private ClassType X10JavaSerializable_;
    private ClassType X10JavaSerializable() {
        if (X10JavaSerializable_ == null)
            X10JavaSerializable_ = tr.typeSystem().load(Emitter.X10_JAVA_SERIALIZABLE_CLASS);
        return X10JavaSerializable_;
    }

    private boolean canCastToX10JavaSerializable(X10ClassDecl_c n, Type type, Context context) {
        type = Types.baseType(type);

        Type leastUpperBound = null;

        if (type.isParameterType()) {
            if (!supportUpperBounds) return true;
            
            X10ClassDef def = n.classDef();
            Ref<TypeConstraint> tc = def.typeGuard();
            if (tc == null) return true; // no upperbounds
            
            Context c2 = context.pushBlock();
            c2.setName(" ClassGuard for |" + def.name() + "| ");
            c2.setTypeConstraintWithContextTerms(tc);
            X10TypeEnv tenv = tr.typeSystem().env(c2);
            List<Type> upperBounds = tenv.upperBounds(type);
            
            Iterator<Type> it = upperBounds.iterator();
            while (it.hasNext()) {
                Type upperBound = Types.baseType(it.next());
                if (upperBound.isParameterType()) {
                    return canCastToX10JavaSerializable(n, upperBound, context);
                }
                if (upperBound.isClass()) {
                    if (!upperBound.toClass().flags().isInterface()) {
                        if (leastUpperBound == null || upperBound.isSubtype(leastUpperBound, context)) {
                            leastUpperBound = upperBound;
                        }
                    }
                }
            }
        } else if (type.isClass() && !type.toClass().flags().isInterface()) {
            // FIXME uncomment the following requires X10JavaSerializable.class before compiling it.
            // leave it for now because of no immediate problem.
//            leastUpperBound = ftype;
        }

        return leastUpperBound == null || leastUpperBound.isSubtype(X10JavaSerializable(), context);
    }

    @Override
    public void visit(X10ClassDecl_c n) {
        X10ClassDef def = n.classDef();
        X10CContext_c context = (X10CContext_c) tr.context();

        // class name and source file name is different. this is the case when StringHelper is defined in String.x10.
        if (def.isTopLevel() && !def.sourceFile().name().equals(def.name().toString() + ".x10")
                && !context.containsGeneratedClasses(def)) {
            context.addGeneratedClasses(def);
            // not include import
            SourceFile sf = tr.nodeFactory().SourceFile(n.position(), Collections.<TopLevelDecl> singletonList(n));
            if (def.package_() != null) {
                sf = sf.package_(tr.nodeFactory().PackageNode(n.position(), def.package_()));
            }
            sf = sf.source(new Source(def.name().toString() + ".x10", n.position().path(), null));
            tr.translate(sf);
            return;
        }

        // Do not generate code if the class is represented natively.
        if (Emitter.getJavaRep(def) != null) {
            w.write(";");
            w.newline();
            return;
        }

        TypeSystem xts = tr.typeSystem();
    	String mangledDefName = Emitter.mangleToJava(def.name());
    	String mangledDefQName = Emitter.mangleQName(def.asType().fullName()).toString();

        Flags flags = n.flags().flags();

        w.begin(0);
        w.write("@"+X10_RUNTIME_IMPL_JAVA_X10GENERATED);
        w.allowBreak(0);
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
        if (!flags.isInterface()) {
            // [DC] all target classes use an extends clause now, even for roots (they extend x.c.Ref)
	        w.write(" extends ");
	        if (flags.isStruct()) {
	            assert superClassNode == null : superClassNode;
	            w.write(X10_CORE_STRUCT);
	        } else {
	            if (superClassNode == null) {
	                // [DC] this is a root
	                w.write(X10_CORE_REF);
	            } else {
	                Type superType = superClassNode.type();
	                er.printType(superType, PRINT_TYPE_PARAMS | BOX_PRIMITIVES | NO_VARIANCE);
	            }
	        }
        }

        // Filter out x10.lang.Any from the interfaces.
        List<TypeNode> interfaces = new ArrayList<TypeNode>();

        for (TypeNode tn : n.interfaces()) {
            if (!xts.isAny(tn.type())) {
                interfaces.add(tn);
            }
        }
    	// N.B. We cannot represent it with Type node since x10.core.Any is @NativeRep'ed to java.lang.Object instead of to x10.core.Any
        /*
         * Interfaces automatically extend Any if
         * (n.flags().flags().isInterface() && interfaces.isEmpty()) {
         * 
         * X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
         * interfaces.add(tr.nodeFactory().CanonicalTypeNode(n.position(),
         * ts.Any())); }
         */
        if (!interfaces.isEmpty()) {
            if (flags.isInterface()) {
                w.write(" extends ");
            } else {
                w.write(" implements ");
            }

            List<Type> alreadyPrintedTypes = new ArrayList<Type>();
            for (Iterator<TypeNode> i = interfaces.iterator(); i.hasNext();) {
                TypeNode tn = i.next();
                if (!useSelfDispatch || (useSelfDispatch && !Emitter.alreadyPrinted(alreadyPrintedTypes, tn.type()))) {
                    if (alreadyPrintedTypes.size() != 0) {
                        w.write(", ");
                    }
                    alreadyPrintedTypes.add(tn.type());
                    // the 1st formal parameter of x10.lang.Comparable[T].compareTo(T) must be erased since it implements java.lang.Comparable/*<T>*/.compareTo(Object).
                    // for x10.lang.Point implements java.lang.Comparable/*<x10.lang.Point>*/
                    er.printType(tn.type(), (useSelfDispatch ? 0 : PRINT_TYPE_PARAMS) | BOX_PRIMITIVES | NO_VARIANCE);
                }
            }

            if (!subtypeOfCustomSerializer(def)) {
                if (alreadyPrintedTypes.size() != 0) {
                    w.write(", ");
                }
                w.write(Emitter.X10_JAVA_SERIALIZABLE_CLASS);
            }

        } else if (!def.flags().isInterface() && !(def.asType().toString().equals(CUSTOM_SERIALIZATION))) {
            w.write(" implements " + Emitter.X10_JAVA_SERIALIZABLE_CLASS);
        } else {
        	// make all interfaces extend x10.core.Any
        	// N.B. We cannot represent it with Type node since x10.core.Any is @NativeRep'ed to java.lang.Object instead of to x10.core.Any
        	if (flags.isInterface() && !xts.isAny(def.asType())) {
        	    w.write(" extends " + X10_CORE_ANY);
        	}
        }
        w.unifiedBreak(0);
        w.end();
        w.write("{");
        w.newline(4);
        w.begin(0);

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
            w.write(")super.clone(); } catch (java.lang.CloneNotSupportedException e) { e.printStackTrace() ; return null; } }");
            w.newline();
        }

        // XTENLANG-1102
        er.generateRTTInstance(def);

        // Redirect java serialization to x10 serialization.
        if (!flags.isInterface() && !flags.isAbstract()) {
            w.write("private Object writeReplace() throws java.io.ObjectStreamException {");
            w.newline(4);
            w.begin(0);
            w.write("return new x10.serialization.SerializationProxy(this);");
            w.end();
            w.newline();
            w.writeln("}");
            w.newline();
        }

        // Generate compiler-supported serialization/deserialization code
        if (subtypeOfCustomSerializer(def)) {
            er.generateCustomSerializer(def, n);            
        } else if (subtypeOfUnserializable(def)) {
            w.write("public void " + Emitter.SERIALIZE_METHOD + "(" + Emitter.X10_JAVA_SERIALIZER_CLASS + " $serializer) throws java.io.IOException {");
            w.newline(4);
            w.begin(0);
            w.write("throw new x10.io.NotSerializableException(\"Can't serialize "+def.fullName()+"\");");
            w.end();
            w.newline();
            w.writeln("}");
            w.newline();
        } else {
            if (!def.flags().isInterface()) {
                X10ClassType ct = def.asType();
                ASTQuery query = new ASTQuery(tr);
                // Cannonical ordering of fields by sorting by name.
                FieldInstance[] orderedFields = new FieldInstance[ct.fields().size()];
                for (int i=0; i<ct.fields().size(); i++) {
                    orderedFields[i] = ct.fields().get(i);
                }
                Arrays.sort(orderedFields, new Comparator<FieldInstance>() {
                    public int compare(FieldInstance arg0, FieldInstance arg1) {
                        return arg0.name().toString().compareTo(arg1.name().toString());
                    }});

                //_deserialize_body method
                w.write("public static ");
                //                if (supportUpperBounds)
                if (typeParameters.size() > 0) {
                    er.printTypeParams(n, context, typeParameters);
                    w.write(" ");
                }
                w.write(Emitter.X10_JAVA_SERIALIZABLE_CLASS + " " + Emitter.DESERIALIZE_BODY_METHOD + "(");
                er.printType(def.asType(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
                w.write(" $_obj, " + Emitter.X10_JAVA_DESERIALIZER_CLASS + " $deserializer) throws java.io.IOException {");
                w.newline(4);
                w.begin(0);

                if (!config.NO_TRACES && !config.OPTIMIZE) {
                    w.write("if (" + X10_RUNTIME_IMPL_JAVA_RUNTIME + ".TRACE_SER) { ");
                    w.write(X10_RUNTIME_IMPL_JAVA_RUNTIME + ".printTraceMessage(\"X10JavaSerializable: " + Emitter.DESERIALIZE_BODY_METHOD + "() of \" + "  + mangledDefName + ".class + \" calling\"); ");
                    w.writeln("} ");
                }

                er.deserializeSuperClass(superClassNode);

                List<ParameterType> parameterTypes = ct.x10Def().typeParameters();

                // Deserialize type parameters
                for (Iterator<? extends Type> i = parameterTypes.iterator(); i.hasNext(); ) {
                    final Type at = i.next();
                    w.write("$_obj.");
                    er.printType(at, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
                    w.writeln(" = (" + X10_RTT_TYPE + ") $deserializer.readObject();");
                }

                // Deserialize the instance variables of this class , we do not serialize transient or static variables
                List<FieldInstance> specialTransients = null;
                for (FieldInstance f : orderedFields) {
                    String str;
                    if (f instanceof X10FieldInstance && !query.ifdef(((X10FieldInstance) f).x10Def())) continue;
                    if (f.flags().isStatic() || query.isSyntheticField(f.name().toString()))
                        continue;
                    if (f.flags().isTransient()) {
                        if (!((X10FieldInstance_c)f).annotationsMatching(xts.TransientInitExpr()).isEmpty()) {
                            if (specialTransients == null) {
                                specialTransients = new ArrayList<FieldInstance>();
                            }
                            specialTransients.add(f);
                        }
                        continue;
                    }
                    if (f.type().isParameterType()) {
                        w.write("$_obj." + Emitter.mangleToJava(f.name()) + " = ");
                        if (supportUpperBounds) {
                            w.write("(");
                            er.printType(f.type(), BOX_PRIMITIVES);
                            w.write(") ");
                        }
                        w.writeln("$deserializer.readObject();");
                    } else if ((str = needsCasting(f.type())) != null) {
                        // Want these to be readInteger and so on.....  These do not need a explicit cast cause we are calling special methods
                        w.writeln("$_obj." + Emitter.mangleToJava(f.name()) + " = $deserializer.read" + str + "();");
                    } else if (xts.isJavaArray(f.type())) {
                        w.write("$_obj." + Emitter.mangleToJava(f.name()) + " = ");
                        w.write("(");
                        er.printType(f.type(), BOX_PRIMITIVES);
                        w.write(") ");
                        w.write("$deserializer.readObject();");
                    } else if (f.type().isArray() && f.type() instanceof JavaArrayType_c && ((JavaArrayType_c)f.type()).base().isParameterType()) {
                        // This is to get the test case XTENLANG_2299 to compile. Hope its a generic fix
                        w.write("$_obj." + Emitter.mangleToJava(f.name()) + " = ");
                        // not needed because readObject takes type parameters
                        //                            w.write("(");
                        //                            er.printType(f.type(), BOX_PRIMITIVES);
                        //                            w.write(") ");                            
                        w.writeln("$deserializer.readObject();");
                    } else {
                        // deserialize the variable and cast it back to the correct type
                        w.write("$_obj." + Emitter.mangleToJava(f.name()) + " = ");
                        // not needed because readObject takes type parameters
                        //                            w.write("(");
                        //                            er.printType(f.type(), BOX_PRIMITIVES);
                        //                            w.write(") ");                            
                        w.writeln("$deserializer.readObject();");
                    }            
                }
                                
                if (specialTransients != null) {
                    w.newline();
                    w.writeln("/* fields with @TransientInitExpr annotations */");
                    for (FieldInstance tf:specialTransients) {
                        Expr initExpr = getInitExpr(((X10FieldInstance_c)tf).annotationsMatching(xts.TransientInitExpr()).get(0));
                        if (initExpr != null) {
                            X10CContext_c ctx = (X10CContext_c) tr.context();
                            w.write("$_obj." + Emitter.mangleToJava(tf.name()) + " = ");
                            String old = ctx.setOverideNameForThis("$_obj");
                            tr.print(n, initExpr, w);
                            ctx.setOverideNameForThis(old);
                            w.writeln(";");
                        }
                    }
                    w.newline();
                } 

                w.write("return $_obj;");
                w.end();
                w.newline();
                w.writeln("}");
                w.newline();

                // _deserializer  method
                w.write("public static " + Emitter.X10_JAVA_SERIALIZABLE_CLASS + " " + Emitter.DESERIALIZER_METHOD + "(" + Emitter.X10_JAVA_DESERIALIZER_CLASS + " $deserializer) throws java.io.IOException {");
                w.newline(4);
                w.begin(0);

                if (def.constructors().size() == 0 || def.flags().isAbstract()) {
                    w.write("return null;");
                } else {
                    if (def.isStruct()) {
                        //TODO Keith get rid of this
                        if (!mangledDefName.equals("PlaceLocalHandle")) {
                            w.write(mangledDefQName + " $_obj = new " + mangledDefQName + "((" + CONSTRUCTOR_FOR_ALLOCATION_DUMMY_PARAM_TYPE + ") null");
                            // N.B. in custom deserializer, initialize type params with null
                            for (ParameterType typeParam : def.typeParameters()) {
                                w.write(", (" + X10_RTT_TYPE + ") null");
                            }
                            w.write(");");
                            w.newline();
                        } else {
                            w.writeln(mangledDefQName + " $_obj = new " + mangledDefQName + "(null, (" + CONSTRUCTOR_FOR_ZERO_VALUE_DUMMY_PARAM_TYPE + ") null);");
                        }
                    } else {
                        if (def.flags().isAbstract()) {
                            w.write(mangledDefQName + " $_obj = (" + mangledDefQName + ") ");
                            // call 1-phase constructor
                            w.write("new " + mangledDefQName);
                            w.writeln("();");
                        } else {
                            w.write(mangledDefQName + " $_obj = new " + mangledDefQName + "(");
                            if (supportConstructorSplitting
                                // XTENLANG-2830
                                /*&& !ConstructorSplitterVisitor.isUnsplittable(Types.baseType(def.asType()))*/
                                && !def.flags().isInterface()) {
                                w.write("(" + CONSTRUCTOR_FOR_ALLOCATION_DUMMY_PARAM_TYPE + ") null");
                                // N.B. in custom deserializer, initialize type params with null
                                for (ParameterType typeParam : def.typeParameters()) {
                                    w.write(", (" + X10_RTT_TYPE + ") null");
                                }
                                w.write(");");
                                w.newline();
                            } else {
                                w.writeln(");");
                            }
                        }
                    }
                    if (!def.isStruct()) {
                        w.writeln("$deserializer.record_reference($_obj);");
                    }
                    w.write("return " + Emitter.DESERIALIZE_BODY_METHOD + "($_obj, $deserializer);");
                }
                w.end();
                w.newline();
                w.writeln("}");
                w.newline();

                // _serialize()
                w.write("public void " + Emitter.SERIALIZE_METHOD + "(" + Emitter.X10_JAVA_SERIALIZER_CLASS + " $serializer) throws java.io.IOException {");
                w.newline(4);
                w.begin(0);

                // Serialize the super class first
                er.serializeSuperClass(superClassNode);

                // Serialize any type parameters
                for (Iterator<? extends Type> i = parameterTypes.iterator(); i.hasNext(); ) {
                    final Type at = i.next();
                    w.write("$serializer.write(this.");
                    er.printType(at, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
                    w.writeln(");");
                }

                // Serialize the public variables of this class , we do not serialize transient or static variables
                for (FieldInstance f : orderedFields) {
                    if (f instanceof X10FieldInstance && !query.ifdef(((X10FieldInstance) f).x10Def())) continue;
                    if (f.flags().isStatic() || query.isSyntheticField(f.name().toString()))
                        continue;
                    if (f.flags().isTransient()) // don't serialize transient fields
                        continue;
                    String fieldName = Emitter.mangleToJava(f.name());
                    w.writeln("$serializer.write(this." + fieldName + ");");
                }
                w.end();
                w.newline();
                w.writeln("}");
                w.newline();
            }
        }

        if (needZeroValueConstructor(def)) {
            er.generateZeroValueConstructor(def, n);
        }

        // print the constructor just for allocation
        if (supportConstructorSplitting
            // XTENLANG-2830
            /*&& !ConstructorSplitterVisitor.isUnsplittable(Types.baseType(def.asType()))*/
            && !def.flags().isInterface()) {
            w.write("// constructor just for allocation");
            w.newline();
            w.write("public " + mangledDefName + "(final " + CONSTRUCTOR_FOR_ALLOCATION_DUMMY_PARAM_TYPE + " $dummy");
            List<String> params = new ArrayList<String>();
            for (ParameterType p : def.typeParameters()) {
                String param = Emitter.mangleParameterType(p);
                w.write(", final " + X10_RTT_TYPE + " " + param);
                params.add(param);
            }
            w.write(") {");
            w.newline(4);
            w.begin(0);
            // call super constructor
            if (flags.isStruct()
                || (superClassNode != null && Emitter.isNativeRepedToJava(superClassNode.type()))
                ) {
                // call default constructor instead of "constructor just for allocation"
            }
            else if (superClassNode != null && superClassNode.type().toClass().isJavaType()) {
                boolean hasDefaultConstructor = false;
                ConstructorDef ctorWithFewestParams = null;
                for (ConstructorDef ctor : superClassNode.type().toClass().def().constructors()) {
                    List<Ref<? extends Type>> formalTypes = ctor.formalTypes();
                    if (formalTypes.size() == 0) {
                        hasDefaultConstructor = true;
                        break;
                    }
                    if (ctorWithFewestParams == null || ctor.formalTypes().size() < ctorWithFewestParams.formalTypes().size()) {
                        ctorWithFewestParams = ctor;
                    }
                }
                if (hasDefaultConstructor) {
                    // call default constructor instead of "constructor just for allocation"
                } else {
                    // XTENLANG-3070
                    // If super class does not have default constructor, call the constructor with the fewest parameters
                    // with all the parameters null or zero.
                    // FIXME This fixes post-compilation error but it may still cause runtime error.
                    assert ctorWithFewestParams != null;
                    w.write("super(");
                    Iterator<Ref<? extends Type>> iter = ctorWithFewestParams.formalTypes().iterator();
                    while (iter.hasNext()) {
                        Type formalType = iter.next().get();
                        if (formalType.isReference()) {
                            w.write("(");
                            er.printType(formalType, 0);
                            w.write(") null");
                        }
                        else if (formalType.isByte() || formalType.isShort()) {
                            w.write("(");
                            er.printType(formalType, 0);
                            w.write(") 0");
                        }
                        else if (formalType.isInt()) {
                            w.write("0");
                        }
                        else if (formalType.isLong()) {
                            w.write("0L");
                        }
                        else if (formalType.isFloat()) {
                            w.write("0.0F");
                        }
                        else if (formalType.isDouble()) {
                            w.write("0.0");
                        }
                        else if (formalType.isChar()) {
                            w.write("'\\0'");
                        }
                        else if (formalType.isBoolean()) {
                            w.write("false");
                        }
                        if (iter.hasNext()) {
                            w.write(", ");
                        }
                    }
                    w.write(");");
                    w.newline();
                }
            }
            else {
                // call "constructor just for allocation"
            	// [DC] if the class doesn't extend anything, don't bother calling super()
                if (def.superType() != null) {
                    w.write("super($dummy");
                    printArgumentsForTypeParamsPreComma(def.superType().get().toClass().typeArguments(), false);
                    w.write(");");
                    w.newline();
                }
            }
            printInitParams(def.asType(), params);
            w.end();
            w.newline();
	    
            w.write("}");
            w.newline();

            w.newline();
        }

        if (useSelfDispatch) {
            er.generateDispatchMethods(def);
        }
        er.generateBridgeMethods(def);

        // print the fields for the type params
        if (typeParameters.size() > 0) {
            w.begin(0);
            if (!flags.isInterface()) {
                for (TypeParamNode tp : typeParameters) {
                    w.write("private ");
                    w.write(X10_RTT_TYPE);
                    // w.write("<"); n.print(tp, w, tr); w.write(">"); // TODO
                    w.write(" ");
                    w.write(Emitter.mangleParameterType(tp));
                    w.write(";");
                    w.newline();
                }
                w.newline();

                w.write("// initializer of type parameters");
                w.newline();
                w.write("public static void ");
                w.write(INITPARAMS_NAME);
                w.write("(final ");
                tr.print(n, n.name(), w);
                /*
                w.write("<");
                boolean first = true;
                for (TypeParamNode tp : typeParameters) {
                    if (first) {
                        first = false;
                    } else {
                        w.write(",");
                    }
                    w.write("?");
                }
                w.write(">");
                */
                w.write(" $this");
                for (TypeParamNode tp : typeParameters) {
                    w.write(", final ");
                    w.write(X10_RTT_TYPE);
                    // w.write("<"); n.print(tp, w, tr); w.write(">"); // TODO
                    w.write(" ");
                    w.write(Emitter.mangleParameterType(tp));
                }
                w.write(") {");
                w.newline(4);
                w.begin(0);
                for (TypeParamNode tp : typeParameters) {
                    w.write("$this.");
                    w.write(Emitter.mangleParameterType(tp));
                    w.write(" = ");
                    w.write(Emitter.mangleParameterType(tp));
                    w.write(";");
                    w.newline();
                }
                w.end();
                w.newline();
                w.write("}");
                w.newline();
            }
            w.end();
        }

        setConstructorIds(def);

        // print synthetic types for parameter mangling
        printExtraTypes(def);

        // print the props
        if (!flags.isInterface()) {
            if (n.properties().size() > 0) {
                w.newline();
                w.writeln("// properties");
                w.begin(0);
                for (PropertyDecl pd : n.properties()) {
                    n.print(pd, w, tr);
                    w.newline();
                }
                w.end();
            }
        }

        w.end();
        w.newline();

        // print the original body
        n.print(n.body(), w, tr);

        w.write("}");
        w.newline();
    }

    private Expr getInitExpr(Type at) {
        at = Types.baseType(at);
        if (at instanceof X10ClassType) {
            X10ClassType act = (X10ClassType) at;
            if (0 < act.propertyInitializers().size()) {
                return act.propertyInitializer(0);
            }
        }
        return null;
    }

    // used by custom serializer
    public static String needsCasting(Type type) {
        type = Types.baseType(type);
        if (isPrimitive(type)) {
            String name = type.name().toString();
            if (type.isUnsignedNumeric()) {
                return name.substring(name.lastIndexOf(".") + 1 + 1); // x10.lang.UInt -> Int
            } else {
                return name.substring(name.lastIndexOf(".") + 1); // x10.lang.Int -> Int
            }
        }
        return null;
    }

    private static final String CUSTOM_SERIALIZATION = "x10.io.CustomSerialization";
    public static final String SERIALIZER = "x10.io.Serializer";
    public static final String DESERIALIZER = "x10.io.Deserializer";
    
    private static boolean subtypeOfCustomSerializer(X10ClassDef def) {
        return subtypeOfInterface(def, CUSTOM_SERIALIZATION);
    }

    private static final String UNSERIALIZABLE = "x10.io.Unserializable";
    private static boolean subtypeOfUnserializable(X10ClassDef def) {
        return subtypeOfInterface(def, UNSERIALIZABLE);
    }

    private static boolean subtypeOfInterface(X10ClassDef def, String interfaceName) {
        for (Ref<? extends Type> ref : def.interfaces()) {
            if (interfaceName.equals(ref.get().toString())) {
                return true;
            }
        }
        Ref<? extends Type> ref = def.superType();
        if (ref == null) return false;
        Type type = ref.get();
        if (type instanceof ConstrainedType) {
            type = ((ConstrainedType) type).baseType().get();
        }
        X10ClassDef superDef = ((X10ParsedClassType_c) type).def();
        return subtypeOfInterface(superDef, interfaceName);
    }

    /*
     * (Definition of haszero by Yoav) Formally, the following types haszero: a
     * type that can be null (e.g., Any, closures, but not a struct or
     * Any{self!=null}) Primitive structs (Short,UShort,Byte,UByte, Int, Long,
     * ULong, UInt, Float, Double, Boolean, Char) user defined structs without a
     * constraint and without a class invariant where all fields haszero.
     */
    private static boolean needZeroValueConstructor(X10ClassDef def) {
        if (def.flags().isInterface()) return false;
        if (!def.flags().isStruct()) return false;
        // Note: we don't need zero value constructor for primitive structs
        // because they are cached in x10.rtt.Types class.
        if (isPrimitive(def.asType())) return false;
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

    private static void setConstructorIds(X10ClassDef def) {
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
                Type bt = Types.baseType(t);
                // XTENLANG-3259 to avoid post-compilation error with Java constructor with Comparable parameter.
                if (bt.isParameterType() || (hasParams(t) && !Emitter.isNativeRepedToJava(t)) || bt.isUnsignedNumeric()) {
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

    private static boolean hasConstructorIdAnnotation(X10ConstructorDef condef) {
        List<Type> annotations = condef.annotations();
        for (Type an : annotations) {
            if (an instanceof ConstructorIdTypeForAnnotation) {
                return true;
            }
        }
        return false;
    }

    // if it isn't set id or don't have an annotation, return -1
    private static int getConstructorId(X10ConstructorDef condef) {
        if (!hasConstructorIdAnnotation(condef)) {
            ContainerType st = condef.container().get();
            if (st.isClass()) {
                X10ClassDef def = st.toClass().def();
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
            X10ClassType ct = t.toClass();
            try {
                if (ct.isX10Struct()) {
                    X10ClassDef cd = ct.def();
                    if (!cd.annotationsMatching(getType("x10.compiler.Mutable")).isEmpty()) {
                        return true;
                    }
                }
            } catch (SemanticException e) {
            }
        }
        return false;
    }

    public static boolean isSplittable(Type type) {
        return supportConstructorSplitting
        && !type.name().toString().startsWith(ClosureRemover.STATIC_NESTED_CLASS_BASE_NAME)
        && !ConstructorSplitterVisitor.isUnsplittable(Types.baseType(type));
    }

    @Override
    public void visit(X10ConstructorDecl_c n) {

        // Checks whether this is the constructor corresponding to CustomSerialization
        boolean isCustomSerializable = false;
        if (n.formals().size() == 1 && DESERIALIZER.equals(n.formals().get(0).type().toString())) {
             isCustomSerializable = true;
        }
        printCreationMethodDecl(n);

        X10ClassType type = Types.get(n.constructorDef().container()).toClass();
        if (isSplittable(type)) {
            printConstructorMethodDecl(n, isCustomSerializable);
            return;
        }

        w.begin(0);

        tr.print(n,
                 tr.nodeFactory().FlagsNode(n.flags().position(),
                                            n.flags().flags().clearPrivate().clearProtected().Public()), w);
        tr.print(n, n.name(), w);

        List<String> params = printConstructorFormals(n, true);

        if (n.body() != null) {
            // if (typeAssignments.size() > 0) {
            w.write(" {");
            w.newline(4);
            w.begin(0);
            if (n.body().statements().size() > 0) {
                Stmt firstStmt = getFirstStatement(n);
                if (firstStmt instanceof X10ConstructorCall_c) {
                    X10ConstructorCall_c cc = (X10ConstructorCall_c) firstStmt;
                    // n.printSubStmt(cc, w, tr);
                    printConstructorCallForJavaCtor(cc);
                    w.allowBreak(0);
                    if (cc.kind() == ConstructorCall.THIS) params.clear();
                }
            }
            printInitParams(type, params);
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
            w.end();
            w.newline();
            w.write("}");
            // } else {
            // n.printSubStmt(n.body(), w, tr);
            // }
        } else {
            w.write(";");
        }
        w.end();
        w.newline();
    }

    private void printCreationMethodDecl(X10ConstructorDecl_c n) {
        X10ClassType type = Types.get(n.constructorDef().container()).toClass();

        if (type.flags().isAbstract()) {
            return;
        }


        List<ParameterType> typeParameters = type.x10Def().typeParameters();

        boolean isSplittable = isSplittable(type);

        List<Formal> formals = n.formals();

        // N.B. we don't generate 1-phase constructor here, since it will be generated as a normal compilation result of X10 constructor.
        if (isSplittable) {
        w.write("// creation method for java code (1-phase java constructor)");
        w.newline();

        tr.print(n,
                 tr.nodeFactory().FlagsNode(n.flags().position(),
                                            n.flags().flags().clearPrivate().clearProtected().Public()), w);

        // N.B. printing type parameters causes post compilation error for XTENLANG_423 and GenericInstanceof16
        er.printType(type, NO_QUALIFIER);

        printConstructorFormals(n, true);

        boolean isFirst = true;
        for (Ref<? extends Type> _throws : n.constructorDef().throwTypes()) {
            if (isFirst) {
                w.write(" throws ");
                isFirst = false;
            } else {
                w.write(", ");                
            }
            er.printType(_throws.get(), 0);
        }


        w.write(" {");
        w.newline(4);
        w.begin(0);

            w.write("this");
            w.write("((" + CONSTRUCTOR_FOR_ALLOCATION_DUMMY_PARAM_TYPE + ") null");
            printArgumentsForTypeParamsPreComma(typeParameters, false);
            w.write(")");
            
            w.write(";"); w.newline();
            
            w.write(CONSTRUCTOR_METHOD_NAME(type.toClass().def()));
        w.write("(");

        for (int i = 0; i < formals.size(); i++) {
            Formal formal = formals.get(i);
            if (i != 0) {
                w.write(", ");
            }
            tr.print(n, formal.name(), w);
        }

        printExtraArgments((X10ConstructorInstance) n.constructorDef().asInstance());

        w.write(")");

        w.write(";");

        w.end();
        w.newline();
        w.write("}");
        w.newline();

        }
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

    private void printInitParams(Type type, List<String> params) {
        if (params.size() > 0) {
            er.printType(type, 0);
            w.write(".");
            w.write(INITPARAMS_NAME);
            w.write("(this");
            for (String param : params) {
                w.write(", ");
                w.write(param);
            }
            w.writeln(");");
        }
    }

    private void printConstructorMethodDecl(X10ConstructorDecl_c n, boolean isCustomSerializable) {

        w.newline();
        w.writeln("// constructor for non-virtual call");

        String methodName = null;

        Flags ctorFlags = n.flags().flags().clearPrivate().clearProtected().Public().Final();
        tr.print(n, tr.nodeFactory().FlagsNode(n.flags().position(), ctorFlags), w);

        er.printType(n.constructorDef().container().get(), PRINT_TYPE_PARAMS | NO_VARIANCE);
        w.write(" ");
        String ctorName = CONSTRUCTOR_METHOD_NAME(n.constructorDef().container().get().toClass().def()); 
        w.write(ctorName);

        List<String> params = printConstructorFormals(n, false);

        boolean isFirst = true;
        for (Ref<? extends Type> _throws : n.constructorDef().throwTypes()) {
            if (isFirst) {
                w.write(" throws ");
                isFirst = false;
            } else {
                w.write(", ");                
            }
            er.printType(_throws.get(), 0);
        }

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
            w.newline(4);
            w.begin(0);
            // if (body.statements().size() > 0) {
            // if (body.statements().get(0) instanceof X10ConstructorCall_c) {
            // X10ConstructorCall_c cc = (X10ConstructorCall_c)
            // body.statements().get(0);
            // n.printSubStmt(cc, w, tr);
            // w.allowBreak(0);
            // if (cc.kind() == ConstructorCall.THIS) typeAssignments.clear();
            // }
            // }

            // If this is the custom serialization constructor we refractor it out into a new method and call it here
            if (isCustomSerializable) {

                // We cant use the same method name in all classes cause it creates and endless loop cause whn super.init is called it calls back to this method
                methodName = n.returnType().type().fullName().toString().replace(".", "$") + "$" + CONSTRUCTOR_METHOD_NAME_FOR_REFLECTION;
                w.writeln(methodName + "(" + n.formals().get(0).name() + ");");
            } else {
                printConstructorBody(n, body);
            }

            if (body.reachable()) {
                w.newline();
                w.write("return this;");
            }
            w.end();
            w.newline();

            w.write("}");
            // } else {
            // n.printSubStmt(body, w, tr);
            // }
        } else {
            w.write(";");
        }
        w.newline();

        // Refactored method that can be called by reflection
        if (isCustomSerializable) {
            w.newline();
            w.write("public void " + methodName + "(" + DESERIALIZER +  " " + n.formals().get(0).name() + ") {");
            w.newline(4);
            w.begin(0);
            n.printSubStmt(body, w, tr);
            w.end();
            w.newline();
            w.write("}");
            w.newline();
        }

    }

    private void printConstructorBody(X10ConstructorDecl_c n, Block body) {
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
    }

    private List<String> printConstructorFormals(X10ConstructorDecl_c n, boolean forceParams) {
        w.write("(");

        w.begin(0);

        X10ConstructorDef ci = n.constructorDef();
        X10ClassType ct = Types.get(ci.container()).toClass();
        List<String> params = new ArrayList<String>();

        if (forceParams) {
        for (Iterator<ParameterType> i = ct.x10Def().typeParameters().iterator(); i.hasNext();) {
            ParameterType p = i.next();
            w.write("final ");
            w.write(X10_RTT_TYPE);
            w.write(" ");
            String name = Emitter.mangleParameterType(p);
            w.write(name);
            if (i.hasNext() || n.formals().size() > 0) {
                w.write(", ");
            }
            params.add(name);
        }
        }

        for (Iterator<Formal> i = n.formals().iterator(); i.hasNext();) {
            Formal f = i.next();
            n.print(f, w, tr);

            if (i.hasNext()) {
                w.write(", ");
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
         * if (i.hasNext()) { w.write(","); w.allowBreak(4); } } }
         */

        return params;
    }

    private void printExtraFormals(X10ConstructorDecl_c n) {
        String dummy = "$dummy";
        int cid = getConstructorId(n.constructorDef());
        if (cid != -1) {
            String extraTypeName = getExtraTypeName(n.constructorDef());
            w.write(", " + extraTypeName + " " + dummy);
        }
    }

    private static String getMangledMethodSuffix(X10ConstructorDef md) {
        ClassType ct = (ClassType) md.container().get();
        List<Ref<? extends Type>> formalTypes = md.formalTypes();
        String methodSuffix = Emitter.getMangledMethodSuffix(ct, formalTypes, true);
        assert methodSuffix.length() > 0;
        return methodSuffix;
    }
    private static String asTypeName(Type containerType, String methodSuffix) {
        X10ClassDef def = containerType.toClass().def();
        String name = def.fullName().toString(); // x10.regionarray.DistArray.LocalState
        Ref<? extends Package> pkg = def.package_();
        if (pkg != null) {
            String packageName = pkg.toString(); // x10.regionarray
            int packageNameLength = packageName.length();
            if (packageNameLength > 0) packageNameLength += 1; // x10.regionarray.
            name = name.substring(packageNameLength); // DistArray.LocalState
        }        
        if (name.length() + 1/*$*/ + methodSuffix.length() + 6/*.class*/> longestTypeName) {
            // if method suffix is too long for file name, replace it with hash code representation of it to avoid post-compilation error. 
            String typeName = "$_" + Integer.toHexString(methodSuffix.hashCode());
//            System.out.println("asTypeName: " + name + ": " + methodSuffix + " -> " + typeName);
            return typeName;            
        } else {
            return methodSuffix;            
        }
    }
    private static String getExtraTypeName(X10ConstructorDef md) {
        assert getConstructorId(md) != -1;
        return asTypeName(md.container().get(), getMangledMethodSuffix(md));
    }
    // should be called after setConstructorIds(def)
    private void printExtraTypes(X10ClassDef def) {
        HashSet<String> extraTypeNames = new HashSet<String>();
        List<ConstructorDef> cds = def.constructors();
        for (ConstructorDef cd : cds) {
            X10ConstructorDef xcd = (X10ConstructorDef) cd;
            int cid = getConstructorId(xcd);
            if (cid != -1) {
                String methodSuffix = getMangledMethodSuffix(xcd);
                String extraTypeName = asTypeName(cd.container().get(), methodSuffix);
                if (!extraTypeNames.contains(extraTypeName)) {
                    extraTypeNames.add(extraTypeName);
                    if (!extraTypeName.equals(methodSuffix)) {
                        w.writeln("// synthetic type for parameter mangling for " + methodSuffix);
                    } else {
                        w.writeln("// synthetic type for parameter mangling");
                    }
                    w.writeln("public static final class " + extraTypeName + " {}");
                }
            }
        }
    }

    private void printConstructorParams(X10ConstructorDecl_c n) {
        w.write("(");

        w.begin(0);

        X10ConstructorDef ci = n.constructorDef();
        X10ClassType ct = Types.get(ci.container()).toClass();

        for (Iterator<Formal> i = n.formals().iterator(); i.hasNext();) {
            Formal f = i.next();
            w.write(f.name().toString());   // TODO mangle?
            if (i.hasNext()) {
                w.write(",");
            }
        }

        printExtraParams(n);

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
         * if (i.hasNext()) { w.write(","); w.allowBreak(4); } } }
         */
    }

    private void printExtraParams(X10ConstructorDecl_c n) {
        String dummy = "$dummy";
        int cid = getConstructorId(n.constructorDef());
        if (cid != -1) {
            w.write(", " + dummy);
        }
    }

    // ////////////////////////////////
    // Expr
    // ////////////////////////////////
    @Override
    public void visit(Allocation_c n) {
        Type type = n.type();
        printAllocationCall(type, type.toClass().typeArguments());
    }

    private void printAllocationCall(Type type, List<? extends Type> typeParams) {
        w.write("new ");
        er.printType(type, PRINT_TYPE_PARAMS | NO_VARIANCE);
        w.write("((" + CONSTRUCTOR_FOR_ALLOCATION_DUMMY_PARAM_TYPE + ") null");
        printArgumentsForTypeParamsPreComma(typeParams, false);
        w.write(")");
    }

    @Override
    public void visit(LocalAssign_c n) {
        Local l = n.local();
        TypeSystem ts = tr.typeSystem();
        if (n.operator() == Assign.ASSIGN || isPrimitive(l.type()) || l.type().isString()) {
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

    // XTENLANG-3287
    private static boolean isFormalTypeErased(X10CodeDef codedef) {
        if (!(codedef instanceof X10MethodDef)) return false;
        X10MethodDef def = (X10MethodDef) codedef;
        if (def.flags().isStatic()) return false;
        String methodName = def.name().toString();
        List<Ref<? extends Type>> formalTypes = def.formalTypes();
        int numFormals = formalTypes.size();

        // the 1st parameter of x10.lang.Comparable[T].compareTo(T)
        if (methodName.equals("compareTo") && numFormals == 1) return true;

        return false;
    }

    @Override
    public void visit(FieldAssign_c n) {
        Type t = n.fieldInstance().type();

        TypeSystem ts = tr.typeSystem();
        if (n.operator() == Assign.ASSIGN || isPrimitive(t) || t.isString()) {
            if (n.target() instanceof TypeNode)
                er.printType(n.target().type(), 0);
            else {
                // XTENLANG-3206, XTENLANG-3208
                if (ts.isParameterType(n.target().type()) || hasParams(n.fieldInstance().container()) || isFormalTypeErased(tr.context().currentCode())) {
                    // TODO:CAST
                    w.write("(");
                    w.write("(");
                    er.printType(n.fieldInstance().container(), PRINT_TYPE_PARAMS);
                    w.write(")");
                    tr.print(n, n.target(), w);
                    w.write(")");
                } else {
                    tr.print(n, n.target(), w);
                }
            }
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
            // TODO pretty print
            w.write("new " + JAVA_IO_SERIALIZABLE + "() {");
            w.allowBreak(0);
            w.write("final ");
            er.printType(n.type(), PRINT_TYPE_PARAMS);
            w.write(" eval(");
            er.printType(n.target().type(), PRINT_TYPE_PARAMS);
            w.write(" target, ");
            er.printType(n.right().type(), PRINT_TYPE_PARAMS);
            w.write(" right) {");
            w.allowBreak(0);
            w.write("return (target.");
            w.write(Emitter.mangleToJava(n.name().id()));
            w.write(" = ");
            w.write("target.");
            w.write(Emitter.mangleToJava(n.name().id()));
            w.write(".");
            w.write(Emitter.mangleToJava(methodName));
            w.write("(right));");
            w.allowBreak(0);
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
        if (isPrimitive(t) || t.isString()) {
            nativeop = true;
        }

        MethodInstance mi = n.methodInstance();
        boolean superUsesClassParameter = !mi.flags().isStatic(); // &&
                                                                  // overridesMethodThatUsesClassParameter(mi);

        if (n.operator() == Assign.ASSIGN) {
            // Look for the appropriate set method on the array and emit native
            // code if there is an @Native annotation on it.
            String pat = Emitter.getJavaImplForDef(mi.x10Def());
            if (pat != null) {
        		List<String> params = new ArrayList<String>(index.size());
                List<Expr> args = new ArrayList<Expr>(index.size() + 1);
                // args.add(array);
                args.add(n.right());
                for (int i = 0; i < index.size(); ++i) {
        		    params.add(mi.def().formalNames().get(i).name().toString());
                	args.add(index.get(i));
                }
            	
                er.emitNativeAnnotation(pat, array, mi.x10Def().typeParameters(), mi.typeParameters(), params, args, Collections.<ParameterType>emptyList(), Collections.<Type> emptyList());
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
            if (isPrimitive(t) && isRail(array.type())) {
                w.write("(");
                w.write("(");
                er.printType(t, 0);
                w.write("[])");
                tr.print(n, array, w);
                w.write(".value");
                w.write(")");
                // LONG_RAIL: unsafe int cast
                w.write("[(int)");
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
            if (isPrimitive(t) && isRail(array.type())) {
                w.write("(");
                w.write("(");
                er.printType(t, 0);
                w.write("[])");
                tr.print(n, array, w);
                w.write(".value");
                w.write(")");
                // LONG_RAIL: unsafe int cast
                w.write("[(int)");
                new Join(er, ", ", index).expand(tr);
                w.write("]");
                w.write(" ");
                w.write(op.toString());
                w.write("=");
                w.write(" ");
                tr.print(n, n.right(), w);
                return;
            }

            // TODO pretty print
            w.write("new " + JAVA_IO_SERIALIZABLE + "() {");
            w.allowBreak(0);
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
            w.allowBreak(0);
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
            w.allowBreak(0);
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
            // TODO generalize for reference type
            if (l.isNull() || r.isNull()) {
            	// ((#0) == (#1))
                w.write("((");
                er.prettyPrint(left, tr);
                w.write(") == (");
                er.prettyPrint(right, tr);
                w.write("))");
            } else {
                // x10.rtt.Equality.equalsequals(#0,#1)
                w.write("x10.rtt.Equality.equalsequals(");
                if (needExplicitBoxing(l)) {
                    er.printBoxConversion(l);
                }
                w.write("("); // required for printBoxConversion
                er.prettyPrint(left, tr);
                w.write(")");
                w.write(",");
                if (needExplicitBoxing(r)) {
                    er.printBoxConversion(r);
                }
                w.write("("); // required for printBoxConversion
                er.prettyPrint(right, tr);
            	w.write("))");
            }
            return;
        }

        if (op == Binary.NE) {
            // SYNOPSIS: #0 != #1
            // TODO generalize for reference type
            if (l.isNull() || r.isNull()) {
            	// ((#0) != (#1))
                w.write("((");
                er.prettyPrint(left, tr);
                w.write(") != (");
                er.prettyPrint(right, tr);
                w.write("))");
            } else {
            	// (!x10.rtt.Equality.equalsequals(#0,#1))
            	w.write("(!x10.rtt.Equality.equalsequals(");
                if (needExplicitBoxing(l)) {
                    er.printBoxConversion(l);
                }
                w.write("(");
                er.prettyPrint(left, tr);
                w.write(")");
            	w.write(",");
                if (needExplicitBoxing(r)) {
                    er.printBoxConversion(r);
                }
                w.write("(");
                er.prettyPrint(right, tr);
            	w.write(")))");
            }
            return;
        }

        if (op == Binary.ADD && (l.isString() || r.isString())) {
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
        if (op == Binary.EQ || op == Binary.NE) {
            if (l.isNumeric() && r.isNumeric() || l.isBoolean() && r.isBoolean() || l.isChar() && r.isChar()) {
                asPrimitive = true;
            }
        }

        boolean needParenl = false;
        if (asPrimitive) {
            // TODO:CAST
            w.write("(");
        	w.write("(");
        	er.printType(l, 0);
        	w.write(") ");
        }
        n.printSubExpr(left, true, w, tr);
        if (needParenl) w.write(")");
        if (asPrimitive) w.write(")");
        w.write(" ");
        w.write(op.toString());
        w.write(" ");
        if (asPrimitive) {
            // TODO:CAST
            w.write("(");
        	w.write("(");
        	er.printType(r, 0);
        	w.write(") ");
        }
        n.printSubExpr(right, false, w, tr);
        if (asPrimitive) w.write(")");
    }

    private static boolean allMethodsFinal(X10ClassDef def) {
    	return def.flags().isFinal() || def.isStruct();
    }
    private static boolean doesNotHaveMethodBody(X10ClassDef def) {
    	// for Comparable[T].compareTo(T)
    	// TODO expand @Native annotation of interface method to the types that implement the interface and don't have its implementation.
    	return def.flags().isInterface();
//    	return false;
    }
    private static boolean canBeNonVirtual(X10ClassDef def) {
    	return allMethodsFinal(def) || doesNotHaveMethodBody(def);
    }
    
    // TODO consolidate isPrimitive(Type) and needExplicitBoxing(Type).
    // return all X10 types that are mapped to Java primitives and require explicit boxing
    public static boolean needExplicitBoxing(Type t) {
        return isPrimitive(t);
    }
    public static boolean isBoxedType(Type t) {
        // void is included here, because synthetic methods have no definition and are reported as having type (void)
        return !(isPrimitive(t) || t.isVoid());
    }

    @Override
    public void visit(X10Call_c c) {
        if (er.printInlinedCode(c)) {
            return;
        }

        if (c.isConstant()) {
            Type t = Types.baseType(c.type());
            if (isPrimitive(t) || t.isNull() || isString(t)) {
                er.prettyPrint(c.constantValue().toLit(tr.nodeFactory(), tr.typeSystem(), t, Position.COMPILER_GENERATED), tr);
                return;
            }
        }

        // XTENLANG-2680 invoke final methods as non-virtual call for optimization
        final MethodInstance mi = c.methodInstance();
        final Receiver target = c.target();
        final Type targetType = target.type();
        final ContainerType containerType = mi.container();
    	assert containerType.isClass();
    	// N.B. structs are implicitly final. all methods of final classes are final. invoke final methods as non-virtual call.
    	boolean invokeNativeAsNonVirtual = !Emitter.supportNativeMethodDecl || mi.flags().isStatic() || mi.flags().isFinal()
    	|| canBeNonVirtual(containerType.toClass().x10Def())
    	|| (targetType.isClass() && canBeNonVirtual(targetType.toClass().x10Def()))
    	;
        if (invokeNativeAsNonVirtual && er.printNativeMethodCall(c)) {
            return;
        }

        // Check for properties accessed using method syntax. They may have
        // @Native annotations too.
        if (mi.flags().isProperty() && mi.formalTypes().size() == 0 && mi.typeParameters().size() == 0) {
            X10FieldInstance fi = (X10FieldInstance) containerType.fieldNamed(mi.name());
            if (fi != null) {
                String pat2 = Emitter.getJavaImplForDef(fi.x10Def());
                if (pat2 != null) {
                    Map<String,Object> components = new HashMap<String,Object>();
                    int i = 0;
                    Object component;
                    component = target;
                    if (supportNumberedParameterForNative)
                    components.put(String.valueOf(i++), component);
                    // TODO need check
                    components.put(fi.x10Def().name().toString(), component);
                    er.dumpRegex("Native", components, tr, pat2);
                    return;
                }
            }
        }

        TypeSystem xts = tr.typeSystem();

        // When the target class is a generics , print a cast operation
        // explicitly.
        if (target instanceof TypeNode) {
            er.printType(targetType, BOX_PRIMITIVES);
        } else {
            // add a check that verifies if the target of the call is in place
            // 'here'
            // This is not needed for:

            if (!(target instanceof Special || target instanceof New)) {
                if (isSpecialType(targetType) && isBoxedType(containerType)) {
                	er.printBoxConversion(targetType);
                    w.write("(");
                    er.prettyPrint(target, tr);
                    w.write(")");
                } else
                if (xts.isParameterType(targetType)) {
                    // TODO:CAST
                    w.write("(");
                    w.write("(");
                    er.printType(containerType, PRINT_TYPE_PARAMS); // TODO
                                                                     // check
                    w.write(")");

                    w.write(X10_RTT_TYPES);
                    w.write(".conversion(");
                    new RuntimeTypeExpander(er, Types.baseType(containerType)).expand(tr);
                    w.write(",");

                    er.prettyPrint(target, tr);

                    w.write(")");

                    w.write(")");
                } else if ((useSelfDispatch && (mi.typeParameters().size() > 0 || hasParams(containerType) || isFormalTypeErased(tr.context().currentCode()))) ||
                           (target instanceof NullLit_c)) {
                    // TODO:CAST
                    w.write("(");
                    w.write("(");
                    er.printType(containerType, PRINT_TYPE_PARAMS);
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

        // print type parameters
        List<Type> methodTypeParams = mi.typeParameters();
        if (methodTypeParams.size() > 0) {
            er.printMethodParams(methodTypeParams);
        }

        // print method name
        if (isMainMethod(mi) || mi.container().toClass().isJavaType()) {
            w.write(Emitter.mangleToJava(c.name().id()));
        } else {
            boolean invokeInterface = false;
            ContainerType st = mi.def().container().get();
            if (Emitter.isInterfaceOrFunctionType(xts, st)) {
            	invokeInterface = true;
            }

            boolean isDispatchMethod = false;
            if (useSelfDispatch) {
                if (xts.isInterfaceType(containerType)) {
                	// XTENLANG-2723 stop passing rtt to java raw class's methods (reverted in r21635)
                	if (containsTypeParam(mi.def().formalTypes()) /*&& !Emitter.isNativeRepedToJava(containerType)*/) {
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
                if (mj.container().typeEquals(containerType, tr.context()) && mj.def().returnType().get().isParameterType()) {
                    instantiatesReturnType = true;
                    break;
                }
            }

            MethodDef md = mi.def();
            boolean isParamReturnType = md.returnType().get().isParameterType() || instantiatesReturnType;

            if (c.nonVirtual()) {
                Name name = InlineHelper.makeSuperBridgeName(mi.container().toClass().def(), mi.name());
                List<MethodInstance> bridges = targetType.toClass().methodsNamed(name);
                assert (bridges.size()==1);
                md = bridges.get(0).def();
                isParamReturnType = false;
                w.write("/"+"*"+"non-virtual"+"*"+"/");
            }

            // call
            // XTENLANG-2993
            // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
//            Type returnTypeForDispatcher = md.returnType().get();
            Type returnTypeForDispatcher = isPrimitive(mi.returnType()) && isPrimitiveGenericMethod(mi) ? mi.returnType() : md.returnType().get();
            // for X10PrettyPrinterVisitor.exposeSpecialDispatcherThroughSpecialInterface
//            boolean isSpecialReturnType = isSpecialType(md.returnType().get());
            boolean isSpecialReturnType = isPrimitive(mi.returnType()) && isPrimitiveGenericMethod(mi) ? true : isSpecialType(md.returnType().get());            
            er.printMethodName(md, invokeInterface, isDispatchMethod, generateSpecialDispatcher && !generateSpecialDispatcherNotUse, returnTypeForDispatcher, isSpecialReturnType, isParamReturnType);
        }

        // print the argument list
        w.write("(");
        w.begin(0);

        List<Type> typeParameters = mi.typeParameters();
        int argumentSize = c.arguments().size();
        printArgumentsForTypeParams(typeParameters, argumentSize == 0);

        boolean runAsync = false;
        if (Types.baseType(containerType).isRuntime()) {
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
                if (isPrimitive(e.type())) {
                    boolean forceBoxing = false;
                    if (!Emitter.canMangleMethodName(def)) {
                        // for methods with non-manglable names, we box argument
                        // if any of the implemented methods has argument of generic type
                        // in corresponding position
                        for (MethodInstance supermeth : c.methodInstance().implemented(tr.context())) {
                            if (isBoxedType(supermeth.def().formalTypes().get(i).get())) {
                                forceBoxing = true;
                                break;
                            }
                        }
                    }
                    // e.g) m((Integer) a) for m(T a)
                    boolean closeParen = false; // unbalanced closing parenthesis needed?
                    // N.B. @NativeRep'ed interface (e.g. Comparable) does not use dispatch method nor mangle method. primitives need to be boxed to allow instantiating type parameter.
                    if (isBoxedType(defType) || forceBoxing) {
                        // this can print something like '(int)' or 'UInt.$box' depending on the type
                        // we require the parentheses to be printed below 
                        er.printBoxConversion(e.type());
                        // e.g) m((int) a) for m(int a)
                    } else {
                        // TODO:CAST
                        w.write("(");
                        er.printType(e.type(), 0);
                        w.write(")");
                        if (e instanceof X10Call) {
                        } else if (e instanceof ClosureCall) {
                            ClosureCall cl = (ClosureCall) e;
                            Expr expr = cl.target();
                            // if (expr instanceof ParExpr) {
                            // expr = expr;
                            // }
                            if (!(expr instanceof Closure_c)
                                    && xts.isParameterType(cl.closureInstance().def().returnType().get())) {
                                // TODO:CAST
                                closeParen = er.printUnboxConversion(e.type());
                                w.write("(");
                                er.printType(e.type(), BOX_PRIMITIVES);
                                w.write(")");
                            }
                        }
                    }
                    w.write("("); // it is important to add parentheses here, as some call may have been issued above
                    c.print(e, w, tr);
                    w.write(")");
                    if (closeParen) w.write(")");
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

                    if (isString(e.type()) && !isString(castType)) {
                        if (xts.isParameterType(castType)) {
                            w.write(X10_RTT_TYPES);
                            w.write(".conversion(");
                            new RuntimeTypeExpander(er, Types.baseType(castType)).expand(tr);
                            w.write(",");
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

        	// XTENLANG-2723 stop passing rtt to java raw class's methods (reverted in r21635)
            if (useSelfDispatch && Emitter.isInterfaceOrFunctionType(xts, containerType) /*&& !Emitter.isNativeRepedToJava(containerType)*/ && Emitter.containsTypeParam(defType)) {
            	// if I is an interface and val i:I, t = type of the formal of method instance
            	// i.m(a) => i.m(a,t)
            	if (xts.isParameterType(containerType) || hasParams(containerType)) {
                    w.write(", ");
            		new RuntimeTypeExpander(er, c.methodInstance().formalTypes().get(i)).expand();
            	}
            }

            if (i != exprs.size() - 1) {
                w.write(", ");
            }
        }
        w.end();
        w.write(")");
    }

    private void printArgumentsForTypeParams(List<? extends Type> typeParameters, boolean isLast) {
        for (Iterator<? extends Type> i = typeParameters.iterator(); i.hasNext();) {
            final Type at = i.next();
            new RuntimeTypeExpander(er, at).expand(tr);
            if (i.hasNext() || !isLast) {
                w.write(", ");
            }
        }
    }

    private void printArgumentsForTypeParamsPreComma(List<? extends Type> typeParameters, boolean isFirst) {
        if (typeParameters == null) return;
        for (Type at : typeParameters) {
            if (isFirst) {
                isFirst = false;
            } else {
                w.write(", ");
            }
            new RuntimeTypeExpander(er, at).expand(tr);            
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
                Expander exprRE = new RuntimeTypeExpander(er, exprType);

                TypeSystem xts = exprType.typeSystem();

                // Note: constraint checking should be desugared when compiling
                // without NO_CHECKS flag

                // e.g. any as Int (any:Any), t as Int (t:T)
                if (isBoxedType(exprType) && xts.isStruct(castType)) {
                	// N.B. castType.isUnsignedNumeric() must be before isPrimitive(castType)
                	// since Int and UInt are @NativeRep'ed to the same Java primive int.
                	if (castType.isUnsignedNumeric()) {
                        w.write(X10_RTT_TYPES + ".as" + castType.name().toString());
                        w.write("(");
                        c.printSubExpr(expr, w, tr);
                        w.write(",");
                    	exprRE.expand();
                        w.write(")");
                    }
                    else if (isPrimitive(castType)) {
                        w.write(X10_RTT_TYPES + ".as");
                        er.printType(castType, NO_QUALIFIER);
                        w.write("(");
                        c.printSubExpr(expr, w, tr);
                        w.write(",");
                    	exprRE.expand();
                        w.write(")");
                    }
                    else {
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
                } else if (isPrimitive(castType)) {
                    w.begin(0);
                    // for the case the method is a dispatch method and that
                    // returns Object.
                    // e.g. (Boolean) m(a)
                    if (castType.typeEquals(Types.baseType(exprType), tr.context())) {
                        boolean closeParen = false;
                        if (expr instanceof X10Call) {
                            X10Call call = (X10Call)expr;
                            MethodInstance mi = call.methodInstance();
                            if (!isPrimitiveGenericMethod(mi) && 
                                ((isBoxedType(mi.def().returnType().get()) && !er.isInlinedCall(call)) || Emitter.isDispatcher(mi)) )
                                closeParen = er.printUnboxConversion(castType);
                        } else if (expr instanceof ClosureCall) {
                            ClosureCall call = (ClosureCall)expr;
                            if (isBoxedType(call.closureInstance().def().returnType().get()))
                                closeParen = er.printUnboxConversion(castType);
                        }
                        c.printSubExpr(expr, w, tr);
                        if (closeParen) w.write(")");
                    } else {
                        w.write("("); // put "(Type) expr" in parentheses.
                        w.write("(");
                        castTE.expand(tr);
                        w.write(")");
                        // e.g. d as Int (d:Double) -> (int)(double)(Double) d
                        if (isPrimitive(exprType)) {
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
                        // TODO pretty print
                        w.allowBreak(2);
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

                    if (castType.isClass()) {
                        X10ClassType ct = castType.toClass();
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

                    // TODO pretty print
                    w.allowBreak(2);

                    boolean closeParen = false; // provide extra closing parenthesis
                    if (isString(exprType) && !isString(castType)) {
                        if (xts.isParameterType(castType)) {
                            w.write(X10_RTT_TYPES);
                            w.write(".conversion(");
                            castRE.expand();
                            w.write(",");
                        } else {
                            // box only if converting to function type
                            if (xts.isFunctionType(castType)) {
                            	er.printBoxConversion(exprType);
                            }
                            w.write("(");
                        }
                        closeParen = true;
                    } else if (needExplicitBoxing(exprType) && isBoxedType(castType)) {
                    	er.printBoxConversion(exprType);
                    	w.write("(");
                        closeParen = true;
                    }
                        
                    boolean needParen = expr instanceof Unary
                            || expr instanceof Lit
                            || expr instanceof Conditional_c;
                    if (needParen) w.write("(");
                    c.printSubExpr(expr, w, tr);
                    if (needParen) w.write(")");

                    if (closeParen)
                        w.write(")");

                    w.write(")");
                    w.end();
                } else {
                    // SYNOPSIS: (#0) #1
                    //  -> Types.<#0>cast(#1,#2)   #0=type #1=expr #2=runtime type
                    //  -> Types.<#0>castConversion(#1,#2)   #0=type #1=expr #2=runtime type
                    w.write(X10_RTT_TYPES + ".<");
                    er.prettyPrint(castTE, tr);
                    boolean convert = xts.isParameterType(exprType) || !xts.isAny(Types.baseType(exprType)) && xts.isParameterType(castType) || isString(castType);
                    w.write("> cast" + (convert ? "Conversion" : "") + "(");
                    boolean closeParen = false;
                    if (needExplicitBoxing(exprType) && isBoxedType(castType)) {
                        er.printBoxConversion(exprType);
                        w.write("(");
                        closeParen = true;
                    }
                    er.prettyPrint(expr, tr);
                    if (closeParen) w.write(")");
                    w.write(",");
                    er.prettyPrint(castRE, tr);
                    w.write(")");
                }
            } else {
                throw new InternalCompilerError("Ambiguous TypeNode survived type-checking.", tn.position());
            }
            break;
        case BOXING:
            er.printBoxConversion(c.type());
            w.write("(");
            er.prettyPrint(c.expr(), tr);
            w.write(")");
            break;
        case UNBOXING:
            boolean closeParen;
            closeParen = er.printUnboxConversion(c.type());
            er.prettyPrint(c.expr(), tr);
            if (closeParen) w.write(")");
            break;
        case UNKNOWN_IMPLICIT_CONVERSION:
            throw new InternalCompilerError("Unknown implicit conversion type after type-checking.", c.position());
        case UNKNOWN_CONVERSION:
            throw new InternalCompilerError("Unknown conversion type after type-checking.", c.position());
        }
    }

    @Override
    public void visit(Conditional_c n) {
        n.translate(w, tr);
    }

    /*
     * Field access -- this includes only access of fields for read;
     * see visit(FieldAssign_c) for write access.
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
            Map<String,Object> components = new HashMap<String,Object>();
            int i = 0;
            Object component;
            component = target;
            if (supportNumberedParameterForNative)
            components.put(String.valueOf(i++), component);
            components.put("this", component);
            // TODO is this needed?
//            components.put(fi.x10Def().name().toString(), component);
            er.dumpRegex("Native", components, tr, pat);
            return;
        }

        if (target instanceof TypeNode) {
            TypeNode tn = (TypeNode) target;
            if (targetType.isParameterType()) {
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
            boolean closeParen = false;
            Type fieldType = n.fieldInstance().def().type().get();
            if (xts.isStruct(target.type()) && !isBoxedType(n.type()) && isBoxedType(fieldType)) {
                closeParen = er.printUnboxConversion(n.type());
            }
            w.begin(0);
            if (!n.isTargetImplicit()) {
                if ((target instanceof NullLit_c) ||
                    (!(target instanceof Special || target instanceof New) && (xts.isParameterType(targetType) || hasParams(fi.container()) || isFormalTypeErased(tr.context().currentCode())))) {
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
            }
            else {
                tr.print(n, target, w);
                w.write(".");
            }
            tr.print(n, n.name(), w);
            if (closeParen) w.write(")");
            w.end();
        }
    }

    @Override
    public void visit(X10Instanceof_c c) {
        // Note: constraint checking should be desugared when compiling without
        // NO_CHECKS flag

        Type t = c.compareType().type();

        // XTENLANG-1102
        if (t.isClass()) {
            X10ClassType ct = t.toClass();
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
                w.write("." + RTT_NAME);
            } else if (pat == null && !ct.isJavaType() && Emitter.getJavaRep(cd) == null && ct.isGloballyAccessible()
                    && cd.typeParameters().size() != 0) {
            	String rttString = RuntimeTypeExpander.getRTT(Emitter.mangleQName(cd.fullName()).toString(), RuntimeTypeExpander.hasConflictingField(ct, tr));
            	w.write(rttString);
            } else {
                new RuntimeTypeExpander(er, t).expand(tr);
            }
        } else {
            new RuntimeTypeExpander(er, t).expand(tr);
        }

        w.write(".");
        w.write("isInstance(");

        Type exprType = Types.baseType(c.expr().type());
        boolean needParen = false;
        if (needExplicitBoxing(exprType)) {
        	er.printBoxConversion(exprType);
        	w.write("(");
        	needParen = true;
        }

        tr.print(c, c.expr(), w);

        if (needParen) {
        	w.write(")");
        }

        if (t.isClass()) {
            X10ClassType ct = t.toClass();
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
        case BYTE:
        case UBYTE:
            val = "((byte) " + Byte.toString((byte) n.value()) + ")";
            break;
        case SHORT:
        case USHORT:
            val = "((short) " + Short.toString((short) n.value()) + ")";
            break;
        case INT:
        case UINT:
            val = Integer.toString((int) n.value());
            break;
        case LONG:
        case ULONG:
            val = Long.toString(n.value()) + "L";
            break;
        // default: // Int, Short, Byte
        // if (n.value() >= 0x80000000L)
        // val = "0x" + Long.toHexString(n.value());
        // else
        // val = Long.toString((int) n.value());
        }
        if (!n.type().isLongOrLess()) {
            assert (Types.baseType(n.type()).isAny());
            w.write("(");
            er.printBoxConversion(n.constantValue().getLitType(tr.typeSystem()));
            w.write("(");
        }
        w.write(val);
        if (!n.type().isLongOrLess()) {
            w.write("))");
        }
    }

    @Override
    public void visit(StringLit_c n) {
        w.write("\"");
        w.write(StringUtil.escape(n.value()));
        w.write("\"");
        // N.B. removed it since now we pass captured environment explicitly,
        // therefore the workaround is no longer needed.
        // w.write(".toString()"); // workaround for XTENLANG-2006.
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
        
        if (isSplittable(type) && !type.fullName().toString().startsWith("java.")) {

            printAllocationCall(type, mi.container().toClass().typeArguments());

            w.write(".");

            w.write(CONSTRUCTOR_METHOD_NAME(type.toClass().def()));
            printConstructorArgumentList(c, c, c.constructorInstance(), null, false);

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

        printConstructorArgumentList(c, c, mi, type, true);

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
                String extraTypeName = getExtraTypeName((X10ConstructorDef) md);
                w.write(", (");
                // print as qualified name
                er.printType(md.container().get(), 0); w.write(".");
                w.write(extraTypeName + ") null");
            }
        }
    }

    @Override
    public void visit(Special_c n) {
        X10CContext_c c = (X10CContext_c) tr.context();
        if (n.kind() == Special.THIS && c.getOverideNameForThis() != null) {
            w.write(c.getOverideNameForThis());
            return;
        }
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
            w.write(".isAssignableTo(");
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

        w.write(X10_RUNTIME_IMPL_JAVA_ARRAYUTILS + ".<");
        er.printType(t, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
        w.write("> ");
        w.write("makeRailFromJavaArray(");
        new RuntimeTypeExpander(er, t).expand();
        w.write(", ");
        if (t.isParameterType()) {
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

                // TODO pretty print
                w.write("new " + JAVA_IO_SERIALIZABLE + "() {");
                w.allowBreak(0);
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
                w.allowBreak(0);
                er.printType(left.type(), PRINT_TYPE_PARAMS);
                w.write(" old = ");
                String pat = Emitter.getJavaImplForDef(mi.x10Def());
                if (pat != null) {
                    Map<String,Object> components = new HashMap<String,Object>();
                    int j = 0;
                    Object component;
                    component = "target";
                    if (supportNumberedParameterForNative)
                    components.put(String.valueOf(j++), component);
                    components.put("target", component);
                    {
                        int i = 0;
                        for (Expr e : args) {
                            component = "a" + (i + 1);
                            if (supportNumberedParameterForNative)
                            components.put(String.valueOf(j++), component);
                            // TODO need check
                            components.put(mi.def().formalNames().get(i).name().toString(), component);
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
                w.allowBreak(0);
                er.printType(left.type(), PRINT_TYPE_PARAMS);
                w.write(" neu = (");
                er.printType(left.type(), PRINT_TYPE_PARAMS);
                w.write(") old");
                w.write((op == Unary.POST_INC || op == Unary.PRE_INC ? "+" : "-") + "1");
                w.write(";");
                w.allowBreak(0);
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
                w.allowBreak(0);
                w.write("return ");
                w.write((op == Unary.PRE_DEC || op == Unary.PRE_INC ? "neu" : "old"));
                w.write(";");
                w.allowBreak(0);
                w.write("}");
                w.allowBreak(0);
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
                && mi.container().toClass().fullName().toString().equals("x10.lang.Runtime")
                && mi.signature().startsWith("runAsync")) {
            runAsync = true;
        }

        TypeExpander ret = new TypeExpander(er, n.returnType().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
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
            if (useSelfDispatch && n.returnType().type().isVoid() && n.formals().size() != 0) {
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

                if (useSelfDispatch) {
                    w.write(", ");
                    w.write(X10_RTT_TYPE);
                    w.write(" ");
                    w.write("t" + (i + 1));
                }
            }
            w.write(") { ");
            if (!n.returnType().type().isVoid()) {
                w.write("return ");
            }

            er.printApplyMethodName(n, n.returnType().type().isParameterType());

            w.write("(");
            String delim = "";
            for (Formal f : formals) {
                w.write(delim);
                delim = ",";
                if (isPrimitive(f.type().type())) {
                    // TODO:CAST
                    w.write("(");
                    er.printType(f.type().type(), 0);
                    w.write(")");
                }
                w.write(f.name().toString());
            }
            w.write(");");
            if (useSelfDispatch && n.returnType().type().isVoid() && n.formals().size() != 0) {
                w.write("return null;");
            }
            w.write("}");
            w.newline();
        }

        w.write("public final ");
        if (useSelfDispatch && !bridge && n.returnType().type().isVoid() && n.formals().size() != 0) {
            w.write(JAVA_LANG_OBJECT);
        } else {
            er.printType(n.returnType().type(), PRINT_TYPE_PARAMS);
        }

        w.write(" ");

        er.printApplyMethodName(n, n.returnType().type().isParameterType());

        w.write("(");
        for (int i = 0; i < formals.size(); i++) {
            if (i != 0) w.write(", ");
            er.printFormal(tr2, n, formals.get(i), false);
            if (useSelfDispatch && !bridge) {
                w.write(", ");
                w.write(X10_RTT_TYPE);
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

        // TODO remove wrapping with UnknownJavaThrowable
//        TryCatchExpander tryCatchExpander = new TryCatchExpander(w, er, n.body(), null);
//        if (runAsync) {
//            tryCatchExpander.addCatchBlock(X10_IMPL_UNKNOWN_JAVA_THROWABLE, "ex", new Expander(er) {
//                public void expand(Translator tr) {
//                    w.write("x10.lang.Runtime.pushException(ex);");
//                }
//            });
//        }

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
        // w.write("x10.lang.Runtime.pushException(new " + X10_IMPL_UNKNOWN_JAVA_THROWABLE + "(t));");
        // }
        // });
        // } else {
        // tryCatchExpander.addCatchBlock("java.lang.Throwable", "t", new
        // Expander(er) {
        // public void expand(Translator tr) {
        // w.write("throw new " + X10_IMPL_UNKNOWN_JAVA_THROWABLE + "(t);");
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
        // w.write("x10.lang.Runtime.pushException(new " + X10_IMPL_UNKNOWN_JAVA_THROWABLE + "(ex));");
        // }
        // });
        // } else {
        // tryCatchExpander.addCatchBlock("java.lang.Exception", "ex", new
        // Expander(er) {
        // public void expand(Translator tr) {
        // w.write("throw new " + X10_IMPL_UNKNOWN_JAVA_THROWABLE + "(ex);");
        // }
        // });
        // }
        // tryCatchExpander.expand(tr2);
        // } else
        //

        // TODO remove wrapping with UnknownJavaThrowable
//        if (runAsync)
//            tryCatchExpander.expand(tr2);
//        else
            er.prettyPrint(n.body(), tr2);

        if (useSelfDispatch && !bridge && n.returnType().type().isVoid() && n.formals().size() != 0) {
            w.write("return null;");
        }

        w.write("}");
        w.newline();

        Type type = n.type();
        type = Types.baseType(type);
        if (type.isClass()) {
            X10ClassType ct = type.toClass();
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
                    if (it.isClass()) {
                        X10ClassType ct2 = it.toClass();
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
                // To extend Any, the type requires getRTT even if it has no type params (e.g. VoidFun_0_0).
                // if (types.size() > 0) {
                w.write("public x10.rtt.RuntimeType<?> " + GETRTT_NAME + "() { return " + RTT_NAME + "; }");
                w.newline();
                w.newline();

                w.write("public x10.rtt.Type<?> " + GETPARAM_NAME + "(int i) { ");
                for (int i = 0; i < types.size(); i++) {
                    w.write("if (i == " + i + ")");
                    Type t = types.get(i);
                    w.write(" return ");
                    new RuntimeTypeExpander(er, t).expand();
                    w.write("; ");
                }
                w.write("return null; ");
                w.write("}");
                w.newline();

                w.newline();

                // }
            }
        }

        w.write("}");
    }

    private boolean needBridge(final Closure_c n) {
        return containsPrimitive(n) || !n.returnType().type().isVoid() && !n.returnType().type().isParameterType();
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
        if (isPrimitive(t)) {
            return true;
        }
        for (Formal f : n.formals()) {
            Type type = f.type().type();
            if (isPrimitive(type)) {
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
                w.write(", ");
            }
        }

        List<Expr> l = c.arguments();
        for (int i = 0; i < l.size(); i++) {
            Expr e = l.get(i);

            Type castType = mi.formalTypes().get(i);
            Type defType = mi.def().formalTypes().get(i).get();

            boolean closeParen = false;
            if (isString(e.type()) && !isString(castType)) {

                w.write("(");
                er.printType(castType, 0);
                w.write(")");

                if (xts.isParameterType(castType)) {
                    w.write(X10_RTT_TYPES);
                    w.write(".conversion(");
                    new RuntimeTypeExpander(er, Types.baseType(castType)).expand(tr);
                    w.write(",");
                    closeParen = true;
                }
            } if (!isBoxedType(e.type()) /*&& isBoxedType(defType)*/) {
                // primitives need to be boxed 
                er.printBoxConversion(e.type());
                w.write("(");
                closeParen = true;
            }

            c.print(e, w, tr);

            if (isMutableStruct(e.type())) {
                w.write(".clone()");
            }

            if (closeParen) {
                w.write(")");
            }

            if (useSelfDispatch && (!newClosure || !needBridge((Closure_c) expr))) {
                w.write(", ");
                new RuntimeTypeExpander(er, mi.formalTypes().get(i)).expand();
            }
            if (i != l.size() - 1) {
                w.write(", ");
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
                        if (found.def() == local.def()) {
                            assert (found.def() == l.localInstance().def()) : l.toString();
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

        /*
         * N.B. this is workaround for front-end bug that generates non-final variable access
         * in the body of statement expression when constructor splitting is enabled.
         */
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

        // print volatile modifier
        boolean isVolatile = false;
        try {
            if (!((X10FieldDef_c)fieldDef).annotationsMatching(getType("x10.compiler.Volatile")).isEmpty()) {
                isVolatile = true;
            }
        } catch (SemanticException e) {
        }
        if (isVolatile) {
            w.write("volatile ");
        }

        w.write(f.translateJava());
        er.printType(javaNode.type().type(), PRINT_TYPE_PARAMS);
        // tr.print(javaNode, javaNode.type(), w);
        w.write(" ");
        tr.print(javaNode, javaNode.name(), w);

        if (javaNode.init() != null) {
            w.write(" = ");

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
        // should be able to assert n.name() is not typeName here, once we stop generating such decls somewhere in the frontend...
        if (er.printMainMethod(n)) {
            return;
        }
        er.generateMethodDecl(n, false);
    }

    // ////////////////////////////////
    // Stmt
    // ////////////////////////////////
    
    public static void catchAndThrowAsX10Exception(CodeWriter w) {
        String TEMPORARY_EXCEPTION_VARIABLE_NAME = Name.makeFresh("exc").toString();
        w.write("catch (" + JAVA_LANG_THROWABLE + " " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
        w.newline(4);
        w.begin(0);
        w.write("throw " + X10_RUNTIME_IMPL_JAVA_THROWABLEUTILS + "." + ENSURE_X10_EXCEPTION + "(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
        w.end();
        w.newline();
        w.writeln("}");
    }

    @Override
    public void visit(Block_c n) {
        String s = Emitter.getJavaImplForStmt(n, tr.typeSystem());
        if (s != null) {
            w.write("try {"); // XTENLANG-2686: handle Java exceptions inside @Native block
            w.newline(4);
            w.begin(0);
            w.write(s);
            w.end();
            w.newline();
            w.write("}"); // XTENLANG-2686
            w.newline();
            catchAndThrowAsX10Exception(w); // XTENLANG-2686
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
        if (!tr.job().extensionInfo().getOptions().assertions)
            return;

        if (useJavaAssertion) {
            n.translate(w, tr);
        } else {
            Expr cond = ((Assert_c) n).cond();
            Expr errorMessage = ((Assert_c) n).errorMessage();

            w.write("if (!" + X10_RUNTIME_IMPL_JAVA_RUNTIME + ".DISABLE_ASSERTIONS && ");
            w.write("!(");
            tr.print(n, cond, w);
            w.write(")");
            w.write(") {");
            w.write("throw new x10.lang.AssertionError(");

            if (errorMessage != null) {
                w.write("java.lang.String.valueOf(");
                tr.print(n, errorMessage, w);
                w.write(")");
            }

            w.write(");");
            w.write("}");
        }

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
        w.write("catch (");
        n.printBlock(n.formal(), w, tr);
        w.write(")");
        n.printSubStmt(n.body(), w, tr);
    }

    @Override
    public void visit(X10ConstructorCall_c c) {
        ContainerType ct = c.constructorInstance().container();
        if (isSplittable(ct)
            || ct.name().toString().startsWith(ClosureRemover.STATIC_NESTED_CLASS_BASE_NAME) // is this needed?
            ) {
            TypeSystem ts = tr.typeSystem();
            Expr target = c.target();
            if (target == null || target instanceof Special) {
                if (c.kind() == ConstructorCall.SUPER) {
                    if (Emitter.isNativeRepedToJava(ct) || Emitter.isNativeClassToJava(ct)) {
                        return;
                    }
                    w.write("/*super.*/");
                } else {
                    w.write("/*this.*/");
                }
            } else {
                if (c.kind() == ConstructorCall.SUPER) {
                    target.translate(w, tr);
                    w.write(".");
                    // invoke constructor for non-virtual call directly
                    String ctorName = CONSTRUCTOR_METHOD_NAME(ct.toClass().def()); 
                    w.write(ctorName);
                    printConstructorArgumentList(c, c, c.constructorInstance(), null, false);
                    w.write(";");
                    return;
                }
                target.translate(w, tr);
                w.write(".");
            }
            w.write(CONSTRUCTOR_METHOD_NAME(ct.toClass().def()));
            printConstructorArgumentList(c, c, c.constructorInstance(), null, false);
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
        printConstructorArgumentList(c, c, c.constructorInstance(), null, true);
        w.write(";");
    }

    private void printConstructorArgumentList(Node_c c, X10ProcedureCall p, X10ConstructorInstance mi, Type type, boolean forceParams) {
        w.write("(");
        w.begin(0);

        if (forceParams) {
        X10ClassType ct = mi.container().toClass();
        List<Type> ta = ct.typeArguments();
        boolean isJavaNative = type != null ? Emitter.isNativeRepedToJava(type) : false;
        if (ta != null && ta.size() > 0 && !isJavaNative) {
            printArgumentsForTypeParams(ta, p.arguments().size() == 0);
        }
        }

        List<Expr> l = p.arguments();
        for (int i = 0; i < l.size(); i++) {
            Expr e = l.get(i);
            if (i < mi.formalTypes().size()) { // FIXME This is a workaround
                Type castType = mi.formalTypes().get(i);
                Type defType = mi.def().formalTypes().get(i).get();
                TypeSystem xts = tr.typeSystem();
                if (isString(e.type()) && !isString(castType)) {

                    w.write("(");
                    er.printType(castType, 0);
                    w.write(")");

                    if (xts.isParameterType(castType)) {
                        w.write(X10_RTT_TYPES);
                        w.write(".conversion(");
                        new RuntimeTypeExpander(er, Types.baseType(castType)).expand(tr);
                        w.write(",");
                    } else {
                        w.write("(");
                    }
                    c.print(e, w, tr);
                    w.write(")");
                } else if (useSelfDispatch && !castType.typeEquals(e.type(), tr.context())) {
                    w.write("(");
                    if (needExplicitBoxing(e.type()) && isBoxedType(defType)) {
                        er.printBoxConversion(e.type());
                    } else {
                        // TODO:CAST
                        w.write("(");
                        // XTENLANG-2895 use erasure to implement co/contra-variance of function type
                        // XTENLANG-3259 to avoid post-compilation error with Java constructor with Comparable parameter.
                        er.printType(castType, (xts.isFunctionType(castType) || Emitter.isNativeRepedToJava(castType)) ? 0 : PRINT_TYPE_PARAMS);
                        w.write(")");
                    }
                    w.write("(");       // printBoxConvesion assumes parentheses around expression
                    c.print(e, w, tr);
                    w.write(")");
                    w.write(")");
                } else {
                    if (needExplicitBoxing(castType) && defType.isParameterType()) {
                        er.printBoxConversion(castType);
                        w.write("(");
                        c.print(e, w, tr);
                        w.write(")");
                    } else {
                        c.print(e, w, tr);
                    }
                }
            } else {
                c.print(e, w, tr);
            }
            if (isMutableStruct(e.type())) {
                w.write(".clone()");
            }

            if (i != l.size() - 1) {
                w.write(", ");
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
                w.write(X10_RUNTIME_IMPL_JAVA_EVALUTILS + ".eval(");
                n.print(expr, w, tr);
                w.write(")");
            } else if (er.isMethodInlineTarget(tr.typeSystem(), ((X10Call) expr).target().type())
                    && ((X10Call) expr).methodInstance().name() == SettableAssign.SET) {
                n.print(expr, w, tr);
            }
            // support for @Native
            else if (!expr.type().isVoid()
                    && Emitter.getJavaImplForDef(((X10Call) expr).methodInstance().x10Def()) != null) {
                w.write(X10_RUNTIME_IMPL_JAVA_EVALUTILS + ".eval(");
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
            w.write(X10_RUNTIME_IMPL_JAVA_EVALUTILS + ".eval(");
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
            w.newline(4);
            w.begin(0);
            Block_c block = (Block_c) statement;
            for (Stmt s : block.statements()) {
                tr.print(n, s, w);
            }
            w.end();
            w.newline();
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
            if (supportTypeConstraintsWithErasure) {
                er.printType(n.type().type(), 0);
            } else
            tr.print(n, n.type(), w);
            w.write(" ");
        }
        tr.print(n, n.name(), w);

        if (n.init() != null) {
            w.write(" = ");

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

            w.write(" = ");

            if (xts.isBoolean(type)) {
                w.write(" false");
            } else if (!xts.isParameterType(type) &&
                    (xts.isChar(type) || xts.isNumeric(type))) {
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
    public void visit(Throw_c n) {
        n.translate(w, tr);
    }

    @Override
    public void visit(Try_c c) {
        TryCatchExpander expander = new TryCatchExpander(w, er, c.tryBlock(), c.finallyBlock());
        final List<Catch> catchBlocks = c.catchBlocks();

        boolean isConstrainedThrowableCaught = false; // XTENLANG-2384
        for (int i = 0; i < catchBlocks.size(); ++i) {
            Type type = catchBlocks.get(i).catchType();
            if (type instanceof ConstrainedType) // XTENLANG-2384: Check if there is a constained type in catchBlocks
                isConstrainedThrowableCaught = true;
        }

        // XTENLANG-2384: If there is a constrained type, generate if sequence instead of catch sequence
        if (isConstrainedThrowableCaught) {
            final String temp = "$ex";
            int convRequired = 0;
            expander.addCatchBlock(JAVA_LANG_THROWABLE, temp, new Expander(er) {
                public void expand(Translator tr) {
                    w.newline();
                    for (int i = 0; i < catchBlocks.size(); ++i) {
                        Catch cb = catchBlocks.get(i);
                        Type type = cb.catchType();
                        w.write("if (" + temp + " instanceof ");
                        er.printType(type, 0);
                        if (type instanceof ConstrainedType) {
                            ConstrainedType ctype = (ConstrainedType)type;
                            //w.write(" && true/* Constraint condition check */"); // TODO: add constraint check here
                        }
                        w.write(")"); w.newline();
                        cb.body().translate(w, tr);
                        w.write("else "); w.newline();
                    }
                    // should not come here
                    w.write("{ "+ temp + ".printStackTrace(); assert false; }"); w.newline();
                }
            });
        } else { // XTENLANG-2384: Normal case, no constrained type in catchBlocks
            final String temp = "$ex";
            for (int i = 0; i < catchBlocks.size(); ++i) {
                Catch catchBlock = catchBlocks.get(i);
                expander.addCatchBlock(catchBlock);
            }
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

    public static boolean isString(Type type) {
        return Types.baseType(type).isString();
    }
    
    public static boolean isRail(Type type) {
        return Types.baseType(type).isRail();
    }


    // TODO consolidate isPrimitive(Type) and needExplicitBoxing(Type).
    public static boolean isPrimitive(Type t) {
        return t.isBoolean() || t.isChar()  || t.isNumeric();
    }

    public static boolean isSpecialType(Type type) {
        return isPrimitive(Types.baseType(type));
    }
    
    /**
     * Returns true if the method does not satisfy the boxing rules. 
     * The currently existing example is Java array access methods, which are declared as generic methods,
     * but are implemented without boxing using @Native snippets.
     * @param mi - MethodInstance
     * @return true if method should be treated specially wrt argument and return value boxing.
     */
    public static boolean isPrimitiveGenericMethod(MethodInstance mi) {
        QName fullName = mi.container().fullName();
        if (fullName.toString().equals("x10.interop.Java.array")) return true;
        if (exposeSpecialDispatcherThroughSpecialInterface) {
            Name name = mi.name();
            List<LocalInstance> formalNames = mi.formalNames();
            if (fullName.equals(Emitter.X10_LANG_ARITHMETIC)) {
                // dispatch method ($G->$I etc.)
                if (formalNames != null && formalNames.size() == 1 &&
                    (OperatorNames.PLUS.equals(name) || OperatorNames.MINUS.equals(name) || OperatorNames.STAR.equals(name) || OperatorNames.SLASH.equals(name)))
                    return true;
            }
            else if (fullName.equals(Emitter.X10_LANG_BITWISE)) {
                // dispatch method ($G->$I etc.)
                if (formalNames != null && formalNames.size() == 1 &&
                    (OperatorNames.AMPERSAND.equals(name) || OperatorNames.BAR.equals(name) || OperatorNames.CARET.equals(name)))
                    return true;
            }
            else if (fullName.equals(Emitter.X10_LANG_REDUCIBLE)) {
                // dispatch method ($G->$I etc.)
                if (formalNames != null && formalNames.size() == 2 &&
                    (OperatorNames.APPLY.equals(name)))
                    return true;
            }
            else if (fullName.equals(Emitter.X10_LANG_ITERATOR)) {
                // special return type ($G->$O)
                if ((formalNames == null || formalNames.size() == 0) &&
                    ("next".equals(name.toString())))
                    return true;
            }
            else if (fullName.equals(Emitter.X10_LANG_SEQUENCE)) {
                // special return type ($G->$O)
                if (formalNames != null && formalNames.size() == 1 &&
                    (OperatorNames.APPLY.equals(name)))
                    return true;
            }
        }
        return false;
    }

    public static boolean isSpecialTypeForDispatcher(Type type) {
        // XTENLANG-2993
        return isSpecialType(type) || type.isVoid();
    }

    public static boolean hasParams(Type t) {
        Type bt = Types.baseType(t);
        TypeSystem ts = bt.typeSystem();
        return (bt.isClass() && !ts.isJavaArray(bt) && bt.toClass().hasParams());
    }

    public static boolean containsTypeParam(List<Ref<? extends Type>> list) {
        for (Ref<? extends Type> ref : list) {
            if (Emitter.containsTypeParam(ref.get())) {
                return true;
            }
        }
        return false;
    }

    private final static class ConstructorIdTypeForAnnotation extends X10ParsedClassType_c {
        private static final long serialVersionUID = 1L;
        private int i = -1;
        private ConstructorIdTypeForAnnotation(X10ClassDef def) {
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
