/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 7, 2004
 */
package polyglot.ext.x10.visit;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Binary_c;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Cast;
import polyglot.ast.CharLit;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign_c;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.Import_c;
import polyglot.ast.Instanceof;
import polyglot.ast.IntLit_c;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.Special_c;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.ext.x10.Configuration;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.AtExpr_c;
import polyglot.ext.x10.ast.AtStmt_c;
import polyglot.ext.x10.ast.Atomic_c;
import polyglot.ext.x10.ast.Await_c;
import polyglot.ext.x10.ast.Clocked;
import polyglot.ext.x10.ast.ClosureCall;
import polyglot.ext.x10.ast.ClosureCall_c;
import polyglot.ext.x10.ast.Closure_c;
import polyglot.ext.x10.ast.Contains_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.Finish_c;
import polyglot.ext.x10.ast.ForEach_c;
import polyglot.ext.x10.ast.ForLoop_c;
import polyglot.ext.x10.ast.Future_c;
import polyglot.ext.x10.ast.Here_c;
import polyglot.ext.x10.ast.Next_c;
import polyglot.ext.x10.ast.Now_c;
import polyglot.ext.x10.ast.PropertyDecl;
import polyglot.ext.x10.ast.PropertyDecl_c;
import polyglot.ext.x10.ast.SettableAssign_c;
import polyglot.ext.x10.ast.SubtypeTest_c;
import polyglot.ext.x10.ast.Tuple_c;
import polyglot.ext.x10.ast.TypeDecl_c;
import polyglot.ext.x10.ast.TypeParamNode;
import polyglot.ext.x10.ast.When_c;
import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10Call;
import polyglot.ext.x10.ast.X10Call_c;
import polyglot.ext.x10.ast.X10CanonicalTypeNode;
import polyglot.ext.x10.ast.X10Cast;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10ClassDecl_c;
import polyglot.ext.x10.ast.X10ClockedLoop;
import polyglot.ext.x10.ast.X10ConstructorCall_c;
import polyglot.ext.x10.ast.X10ConstructorDecl_c;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10Formal_c;
import polyglot.ext.x10.ast.X10Instanceof_c;
import polyglot.ext.x10.ast.X10LocalDecl_c;
import polyglot.ext.x10.ast.X10MethodDecl_c;
import polyglot.ext.x10.ast.X10New_c;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.ast.X10Unary_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.query.QueryEngine;
import polyglot.ext.x10.types.ClosureType;
import polyglot.ext.x10.types.ConstrainedType;
import polyglot.ext.x10.types.MacroType;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.X10ArraysMixin;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Def;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.XTypeTranslator.XTypeLit_c;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MemberInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.NoClassException;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.Translator;
import x10.constraint.XAnd_c;
import x10.constraint.XConstraint;
import x10.constraint.XEQV_c;
import x10.constraint.XEquals_c;
import x10.constraint.XFailure;
import x10.constraint.XField_c;
import x10.constraint.XFormula_c;
import x10.constraint.XLit_c;
import x10.constraint.XLocal_c;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XNot_c;
import x10.constraint.XTerm;
import x10.constraint.XTerms;


/**
 * Visitor on the AST nodes that for some X10 nodes triggers the template
 * based dumping mechanism (and for all others just defaults to the normal
 * pretty printing).
 *
 * @author Christian Grothoff
 * @author Igor Peshansky (template classes)
 * @author Rajkishore Barik 26th Aug 2006 (added loop optimizations)
 */
public class X10PrettyPrinterVisitor extends X10DelegatingVisitor {
        public static final String X10_RUNTIME_TYPE_CLASS = "x10.types.Type";
        public static final String X10_FUN_CLASS_PREFIX = "x10.core.fun.Fun";
        public static final String X10_RUNTIME_CLASS = "x10.runtime.impl.java.Runtime";

        public static final boolean USE_JAVA_GENERICS = true;
        protected static final boolean serialize_runtime_constraints = false;
 
	private final CodeWriter w;
	private final Translator tr;

	private static int nextId_;
	/* to provide a unique name for local variables introduce in the templates */
	private static Integer getUniqueId_() {
		return new Integer(nextId_++);
	}

	public static Name getId() {
		return Name.make("__var" + getUniqueId_() + "__");
	}

	public X10PrettyPrinterVisitor(CodeWriter w, Translator tr) {
		this.w = w;
		this.tr = tr;
	}

	public void visit(Node n) {
                // Don't call through del; that would be recursive.
		n.translate(w, tr);
	}
	
	public void visit(LocalAssign_c n) {
	    Local l = n.local();
	    TypeSystem ts = tr.typeSystem();
	    if (n.operator() == Assign.ASSIGN || l.type().isNumeric() || l.type().isBoolean() || l.type().isSubtype(ts.String(), tr.context())) {
	        tr.print(n, l, w);
	        w.write(" ");
	        w.write(n.operator().toString());
	        w.write(" ");
	        coerce(n, n.right(), l.type());
	    }
	    else {
	        Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
	        Name methodName = X10Binary_c.binaryMethodName(op);
	        tr.print(n, l, w);
	        w.write(" = ");
	        tr.print(n, l, w);
	        w.write(".");
	        w.write(methodName.toString());
	        w.write("(");
	        tr.print(n, n.right(), w);
	        w.write(")");
	    }
	}

    public void visit(FieldAssign_c n) {
	    Type t = n.fieldInstance().type();

	    TypeSystem ts = tr.typeSystem();
	    if (n.operator() == Assign.ASSIGN || t.isNumeric() || t.isBoolean() || t.isSubtype(ts.String(), tr.context())) {
	            tr.print(n, n.target(), w);
	            w.write(".");
	            w.write(n.name().id().toString());
	            w.write(" ");
	            w.write(n.operator().toString());
	            w.write(" ");
	            coerce(n, n.right(), n.fieldInstance().type());
	    }
	    else if (n.target() instanceof TypeNode || n.target() instanceof Local || n.target() instanceof Lit) {
	        // target has no side effects--evaluate it more than once
	        Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
	        Name methodName = X10Binary_c.binaryMethodName(op);
                tr.print(n, n.target(), w);
                w.write(".");
                w.write(n.name().id().toString());
                w.write(" ");
	        w.write(" = ");
                tr.print(n, n.target(), w);
                w.write(".");
                w.write(n.name().id().toString());
	        w.write(".");
	        w.write(methodName.toString());
	        w.write("(");
	        tr.print(n, n.right(), w);
	        w.write(")");
	    }
	    else {
	        // x.f += e
	        // -->
	        // new Object() { T eval(R target, T right) { return (target.f = target.f.add(right)); } }.eval(x, e)
	        Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
	        Name methodName = X10Binary_c.binaryMethodName(op);
	        w.write("new java.lang.Object() {");
	        w.allowBreak(0, " ");
	        w.write("final ");
	        printType(n.type(), PRINT_TYPE_PARAMS);
	        w.write(" eval(");
	        printType(n.target().type(), PRINT_TYPE_PARAMS);
	        w.write(" target, ");
	        printType(n.right().type(), PRINT_TYPE_PARAMS);
	        w.write(" right) {");
	        w.allowBreak(0, " ");
	        w.write("return (target.");
	        w.write(n.name().id().toString());
	        w.write(" = ");
	        w.write("target.");
	        w.write(n.name().id().toString());
	        w.write(".");
	        w.write(methodName.toString());
	        w.write("(right));");
	        w.allowBreak(0, " ");
	        w.write("} }.eval(");
	        tr.print(n, n.target(), w);
	        w.write(", ");
	        tr.print(n, n.right(), w);
	        w.write(")");
	    }
	}

	public void coerce(Node parent, Expr e, Type expected) {
	    Type actual = e.type();
	    if (expected == actual) {
	        tr.print(parent, e, w);
	        return;	        
	    }
	    
	    w.write("((");
            printType(expected, PRINT_TYPE_PARAMS);
	    w.write(") ");
	    tr.print(parent, e, w);
	    w.write(")");
	    
//            w.write("x10.types.Types.<");
//            printType(expected, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
//            w.write(">javacast(");
//	    tr.print(parent, e, w);
//	    w.write(") ");
	}
	
	public void generateDispatchers(X10ClassDef cd) {
		if (true) return;
	    if (cd.flags().isInterface() || cd.flags().isAbstract()) {
	        return;
	    }
	    
	    X10ClassType ct = (X10ClassType) cd.asType();
	    
	    Collection<MethodInstance> seen = new ArrayList<MethodInstance>();
	    
	    // Remove methods where we generate the dispatcher directly.
            for (MethodDef md : cd.methods()) {
                MethodInstance mi = md.asInstance();
                mi = (MethodInstance) mi.container(ct);
                if (methodUsesClassParameter(mi.def()) && ! md.flags().isStatic())
                    seen.add(mi);
            }
            
            List<MethodInstance> methods = new ArrayList<MethodInstance>();
            getInheritedVirtualMethods(ct, methods);
            
            // Remove abstract or overridden methods.
            for (ListIterator<MethodInstance> i = methods.listIterator(); i.hasNext(); ) {
                MethodInstance mi = i.next();	        
                if (mi.flags().isAbstract()) {
                    i.remove();
                    continue;
                }
                X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
                MethodInstance mj = ts.findImplementingMethod(cd.asType(), mi, tr.context());
                if (mj != null && mj.def() != mi.def())
                    i.remove();
            }
            
            // Remove methods that don't need dispatchers
            // and reset containers.
            for (ListIterator<MethodInstance> i = methods.listIterator(); i.hasNext(); ) {
                MethodInstance mi = i.next();	        
                mi = (MethodInstance) mi.container(ct);
                if (!overridesMethodThatUsesClassParameter(mi))
                    i.remove();
                else if (methodUsesClassParameter(mi.def()))
                    i.remove();
                else
                    i.set(mi);
            }
            
            // Remove seen methods.
            for (ListIterator<MethodInstance> i = methods.listIterator(); i.hasNext(); ) {
                MethodInstance mi = i.next();	        
                if (seen(seen, mi))
                    i.remove();
            }
            
	    for (MethodInstance mi : methods) {
	        generateDispatcher((X10MethodInstance) mi, methodUsesClassParameter(mi.def()));
	    }
	}
	
	private boolean seen(Collection<MethodInstance> seen, MethodInstance mi) {
	    for (MethodInstance mj : seen) {
	        if (mi.name().equals(mj.name()) && mj.hasFormals(mi.formalTypes(), tr.context()))
	            return true;
	    }
	    seen.add(mi);
	    return false;
	}
	
	public void getInheritedVirtualMethods(X10ClassType ct, List<MethodInstance> methods) {
	    for (MethodInstance mi : ct.methods()) {
	        if (! mi.flags().isStatic()) 
	        	methods.add(mi);
	    }
	    Type sup = ct.superClass();
	    if (sup instanceof X10ClassType) {
	        getInheritedVirtualMethods((X10ClassType) sup, methods);
	    }
	    for (Type t : ct.interfaces()) {
	        if (t instanceof X10ClassType) {
	            getInheritedVirtualMethods((X10ClassType) t, methods);
	        }
	    }
	}
	
	public void generateDispatcher(X10MethodInstance md, boolean usesClassParam) {
	    X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();

	    Flags flags = md.flags();
	    flags = X10Flags.toX10Flags(flags).clearExtern();
	    flags = flags.clearNative();

	    // Hack to ensure that X10Flags are not printed out .. javac will
	    // not know what to do with them.
	    flags = X10Flags.toX10Flags(flags);

	    w.begin(0);
	    w.write(flags.translate());

	    if (USE_JAVA_GENERICS) {
	        String sep = "<";
	        for (int i = 0; i < md.typeParameters().size(); i++) {
	            w.write(sep);
	            sep = ", ";
	            printType(md.typeParameters().get(i), PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
	        }
	        if (md.typeParameters().size() > 0)
	            w.write("> ");
	    }

	    printType(md.returnType(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
	    w.allowBreak(2, 2, " ", 1);
	    w.write(md.name().toString());

	    w.write("(");

	    w.allowBreak(2, 2, "", 0);
	    w.begin(0);
	    boolean first = true;

	    // Add a formal parameter of type Type for each type parameters.
	    for (Type p : md.typeParameters()) {
	        if (! first) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	        first = false;
	        w.write("final ");
	        w.write(X10_RUNTIME_TYPE_CLASS);
	        w.write(" ");
	        Type pt = p;
	        assert pt instanceof ParameterType;
	        w.write(((ParameterType) pt).name().toString());
	    }

	    List<Expander> dispatchArgs = new ArrayList<Expander>();

	    if (USE_JAVA_GENERICS) {
	        for (Type pt : md.typeParameters()) {
	            dispatchArgs.add(new Inline(((ParameterType) pt).name().toString()));
	        }
	    }

	    for (int i = 0; i < md.formalTypes().size(); i++) {
	        Type f = md.formalTypes().get(i);
	        if (! first) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	        first = false;

	        w.write(Flags.FINAL.translate());
	        printType(f, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
	        w.write(" ");

	        Name name = Name.make("a" + (i+1));
	        w.write(name.toString());

	        dispatchArgs.add(new Join("", CollectionUtil.list("(", new TypeExpander(f, true, true, false), ") ", name.toString())));
	    }

	    w.end();
	    w.write(")");

	    if (! md.throwTypes().isEmpty()) {
	        w.allowBreak(6);
	        w.write("throws ");

	        for (Iterator<Type> i = md.throwTypes().iterator(); i.hasNext(); ) {
	            Type t = i.next();
	            printType(t, PRINT_TYPE_PARAMS);
	            if (i.hasNext()) {
	                w.write(",");
	                w.allowBreak(4, " ");
	            }
	        }
	    }

	    w.end();
	    
	    if (isAbstract(md)) {
	    	w.write(";");
	    	return;
	    }
	    
	    w.write(" ");
	    w.write("{");
	    w.allowBreak(4);
	    
	    if (! md.returnType().isVoid()) {
	        w.write(" return       ");
	    }
	    
	    String pat = getJavaImplForDef(md.x10Def());
		if (pat != null) {
			String target = "this";
			emitNativeAnnotation(pat, target, md.typeParameters(), dispatchArgs);
			w.write("; }");
			return;
		}

	    w.write("this");
	    w.write(".");
	    

            if (USE_JAVA_GENERICS) {
                String sep = "<";
                for (int i = 0; i < md.typeParameters().size(); i++) {
                    w.write(sep);
                    sep = ", ";
                    printType(md.typeParameters().get(i), PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
                }
                if (md.typeParameters().size() > 0)
                    w.write("> ");
            }

	    w.write(md.name().toString());
	    w.write("(");
	    w.begin(0);

	    for (int i = 0; i < dispatchArgs.size(); i++) {
	        if (i != 0) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	        dispatchArgs.get(i).expand(tr);
	    }

	    w.end();
	    w.write(");");
	    w.allowBreak(0, " ");
	    w.write("}");
	}

	boolean isAbstract(MemberInstance mi) {
		if (mi.flags().isAbstract())
			return true;
		Type t = X10TypeMixin.baseType(mi.container());
		if (t instanceof ClassType) {
			ClassType ct = (ClassType) t;
			if (ct.flags().isInterface())
				return true;
		}
		return false;
	}
	
	public void visit(X10MethodDecl_c n) {
	    X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
	    
	    Flags flags = n.flags().flags();

	    if (n.name().id().toString().equals("main") &&
	            flags.isPublic() &&
	            flags.isStatic() &&
	            n.returnType().type().isVoid() &&
	            n.formals().size() == 1 &&
	            n.formals().get(0).declType().typeEquals(ts.Rail(ts.String()), tr.context()))
	    {
	    	Expander throwsClause = new Inline("");
	    	if (n.throwTypes().size() > 0) {
	    		List<Expander> l = new ArrayList<Expander>();
	    		for (TypeNode tn : n.throwTypes()) {
	    			l.add(new TypeExpander(tn.type(), PRINT_TYPE_PARAMS));
	    		}
	    		throwsClause = new Join("", "throws ", new Join(", ", l));
	    	}
	    	
	        new Template("Main", n.formals().get(0), n.body(), tr.context().currentClass().name(), throwsClause).expand();
	        return;
	    }

	    X10MethodInstance mi = (X10MethodInstance) n.methodDef().asInstance();
	    
	    boolean boxPrimitives = true;
	    
	    if (mi.name() == Name.make("hashCode") && mi.formalTypes().size() == 0)
	    	boxPrimitives = false;
	    if (mi.name() == Name.make("equals") && mi.formalTypes().size() == 1)
	    	boxPrimitives = false;
	    if (mi.name() == Name.make("hasNext") && mi.formalTypes().size() == 0)
	    	boxPrimitives = false;
	    
// Olivier: uncomment hack below to generate java code with unboxed primitives 
//	    if (mi.name() != Name.make("apply") && mi.name() != Name.make("write") && mi.name() != Name.make("read") && mi.name() != Name.make("set"))
//            boxPrimitives = false;
	    generateMethodDecl(n, boxPrimitives);
	}

    private boolean overridesMethodThatUsesClassParameter(MethodInstance mi) {
        if (methodUsesClassParameter(mi.def()))
            return true;

        for (MethodInstance mj : mi.implemented(tr.context())) {
            boolean usesParameter = methodUsesClassParameter(mj.def());
            if (usesParameter)
                return true;
        }

        return false;
    }

    private boolean methodUsesClassParameter(MethodDef md) {
        Type container = Types.get(md.container());
        container = X10TypeMixin.baseType(container);
        if (container instanceof X10ClassType) {
            X10ClassDef cd = ((X10ClassType) container).x10Def();
            
            if (cd.typeParameters().size() == 0)
                return false;

            if (getJavaRep(cd) != null)
                return false;

            for (Ref<? extends Type> tref : md.formalTypes()) {
                Type t = Types.get(tref);
                if (t instanceof ParameterType) {
                    ParameterType pt = (ParameterType) t;
                    Def d = Types.get(pt.def());
                    if (d == cd) {
                        return true;
                    }
                }
            }
            Type t = Types.get(md.returnType());
            if (t instanceof ParameterType) {
                ParameterType pt = (ParameterType) t;
                Def d = Types.get(pt.def());
                if (d == cd) {
                    return true;
                }
            }
        }

        return false;
    }
	
	private void generateMethodDecl(X10MethodDecl_c n, boolean boxPrimitives) {
	   X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();

	    Flags flags = n.flags().flags();

	    Context c = tr.context();

	    if (c.currentClass().flags().isInterface()) {
	        flags = flags.clearPublic();
	        flags = flags.clearAbstract();
	    }
	    
	    // Hack to ensure that X10Flags are not printed out .. javac will
	    // not know what to do with them.
	    flags = X10Flags.toX10Flags(flags);

	    w.begin(0);
	    w.write(flags.translate());

	    if (USE_JAVA_GENERICS) {
	        String sep = "<";
	        for (int i = 0; i < n.typeParameters().size(); i++) {
	            w.write(sep);
	            sep = ", ";
	            printType(n.typeParameters().get(i).type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
	        }
	        if (n.typeParameters().size() > 0)
	            w.write("> ");
	    }

	    printType(n.returnType().type(), PRINT_TYPE_PARAMS | (boxPrimitives ? BOX_PRIMITIVES : 0));
            w.allowBreak(2, 2, " ", 1);
            tr.print(n, n.name(), w);
            
            w.write("(");

            w.allowBreak(2, 2, "", 0);
            w.begin(0);
            boolean first = true;

            // Add a formal parameter of type Type for each type parameters.
            for (TypeParamNode p : n.typeParameters()) {
                if (! first) {
                    w.write(",");
                    w.allowBreak(0, " ");
                }
                first = false;
                
                w.write("final ");
                w.write(X10_RUNTIME_TYPE_CLASS);
                w.write(" ");
                w.write(p.name().id().toString());
            }

	    for (int i = 0; i < n.formals().size(); i++) {
	        Formal f = n.formals().get(i);
	        if (! first) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	        first = false;
	        
	        tr.print(n, f.flags(), w);
	        printType(f.type().type(), PRINT_TYPE_PARAMS | (boxPrimitives ? BOX_PRIMITIVES : 0));
	        w.write(" ");

	        Name name = f.name().id();
	        if (name.toString().equals("")) {
	            name = Name.makeFresh("a");
	        }
	        tr.print(n, f.name().id(name), w);
	    }
	    
	    w.end();
	    w.write(")");
	    
	    if (! n.throwTypes().isEmpty()) {
	        w.allowBreak(6);
	        w.write("throws ");

	       
	        for (Iterator<TypeNode> i = n.throwTypes().iterator(); i.hasNext(); ) {
	           TypeNode tn = (TypeNode) i.next();
	            // vj 09/26/08: Changed to print out translated version of throw type
	            // tr.print(n, tn, w);
	           // TODO: Nate to check.
	            printType(tn.type(), PRINT_TYPE_PARAMS);

	            if (i.hasNext()) {
	                w.write(",");
	                w.allowBreak(4, " ");
	            }
	        }
	    }

	    w.end();

	    if (n.body() != null) {

	        //	                if (! unboxFormals.isEmpty()) {
	        //	            for (Map.Entry<Name,Formal> e : unboxFormals.entrySet()) {
	        //	                Name newName = e.getKey();
	        //	                Formal f = e.getValue();
	        //	                tr.print(n, f.flags(), w);
	        //	                printType(f.type().type(), true, false, false); // don't box
	        //	                w.write(" ");
	        //	                tr.print(n, f.name(), w);
	        //	                w.write(" = ");
	        //	                w.write(newName.toString());
	        //	                w.write(";");
	        //	            }
	        //	            w.allowBreak(0, " ");

	        tr.print(n, n.body(), w);
	    }
	    else {
	        w.write(";");
	    }
	}
	
	public void visit(X10ConstructorDecl_c n) {
		w.begin(0);
		
		tr.print(n, n.flags(), w);
		tr.print(n, n.name(), w);
		w.write("(");
		
		w.begin(0);
		
		X10ConstructorDef ci = (X10ConstructorDef) n.constructorDef();
		X10ClassType ct = (X10ClassType) Types.get(ci.container());
		List<String> typeAssignments = new ArrayList<String>();
		
		
		
		for (Iterator i = ct.x10Def().typeParameters().iterator(); i.hasNext(); ) {
		    ParameterType p = (ParameterType) i.next();
		    w.write("final ");
		    w.write(X10_RUNTIME_TYPE_CLASS);
		    w.write(" ");
		    w.write(p.name().toString());
		
		    typeAssignments.add("this." + p.name() + " = " + p.name() + ";");
		    
		    if (i.hasNext() || n.formals().size() > 0) {
			w.write(",");
			w.allowBreak(0, " ");
		    }
		}
		
		for (Iterator i = n.formals().iterator(); i.hasNext(); ) {
		    Formal f = (Formal) i.next();
		    n.print(f, w, tr);
		
		    if (i.hasNext()) {
		        w.write(",");
		        w.allowBreak(0, " ");
		    }
		}
		
		w.end();
		w.write(")");
		
		if (! n.throwTypes().isEmpty()) {
		    w.allowBreak(6);
		    w.write("throws ");
		
		    for (Iterator i = n.throwTypes().iterator(); i.hasNext(); ) {
		        TypeNode tn = (TypeNode) i.next();
		        printType(tn.type(), PRINT_TYPE_PARAMS);
		
		        if (i.hasNext()) {
		            w.write(",");
		            w.allowBreak(4, " ");
		        }
		    }
		}
		
		w.end();
		
		if (n.body() != null) {
		    if (typeAssignments.size() > 0) {
		        w.write(" {");
		        w.begin(4);
		        if (n.body().statements().size() > 0) {
		            if (n.body().statements().get(0) instanceof ConstructorCall) {
		            	ConstructorCall cc = (ConstructorCall) n.body().statements().get(0);
		                n.printSubStmt(cc, w, tr);
		                w.allowBreak(0, " ");
		                if (cc.kind() == ConstructorCall.THIS)
		                	typeAssignments.clear();
		            }
		        }
		        for (String s : typeAssignments) {
		            w.write(s);
		            w.allowBreak(0, " ");
		        }
		        if (n.body().statements().size() > 0) {
                            if (n.body().statements().get(0) instanceof ConstructorCall)
                                n.printSubStmt(n.body().statements(n.body().statements().subList(1, n.body().statements().size())), w, tr);
                            // vj: the main body was not being written. Added next two lines.
                            else 
                            	 n.printSubStmt(n.body(), w, tr);
		        }
		        else
		            n.printSubStmt(n.body(), w, tr);
		        w.write("}");
		        w.end();
		    }
		    else {
		        n.printSubStmt(n.body(), w, tr);
		    }
		}
		else {
		    w.write(";");
		}
	}
	
	public void visit(Contains_c n) {
	    assert false;
	}
	
	public void visit(TypeDecl_c n) {
	    // Do not write anything.
	    return;
	}
	
	public String getJavaRep(X10ClassDef def) {
		return getJavaRepParam(def, 1);
	}
	public String getJavaRTTRep(X10ClassDef def) {
		return getJavaRepParam(def, 3);
	}
	
	public String getJavaRepParam(X10ClassDef def, int i) {
		try {
			X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
			Type rep = (Type) xts.systemResolver().find(QName.make("x10.compiler.NativeRep"));
			List<Type> as = def.annotationsMatching(rep);
			for (Type at : as) {
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

	public void visit(X10ClassDecl_c n) {
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		X10Context context = (X10Context) tr.context();

		X10ClassDef def = (X10ClassDef) n.classDef();
	    
	    // Do not generate code if the class is represented natively.
	    if (getJavaRep(def) != null) {
	        w.write(";");
	        w.newline();
	        return;
	    }

//	    StringBuilder sb = new StringBuilder();
//	    for (TypeParamNode t : n.typeParameters()) {
//		if (sb.length() > 0) {
//		    sb.append(", ");
//		}
//		sb.append("\"");
//		sb.append(t.name().id());
//		sb.append("\"");
//	    }
	    
//	    if (sb.length() > 0) {
//		w.write("@x10.generics.Parameters({");
//		w.write(sb.toString());
//		w.write("})");
//		w.allowBreak(0);
//	    }
	    
//	    @Parameters({"T"})
//	    public class List implements Runnable {
//	    	public class T {};
//	    	public List() { }
//	    	@Synthetic public List(Class T) { this(); }
//	    	@Synthetic public static boolean instanceof$(Object o, String constraint) { assert(false); return true; }
//	    	public static boolean instanceof$(Object o, String constraint, boolean b) { /*check constraint*/; return b; }
//	    	public static Object cast$(Object o, String constraint) { /*check constraint*/; return (List)o; }
//	    	T x;
	    	
		X10Flags flags = X10Flags.toX10Flags( n.flags().flags());
		
		w.begin(0);
		if (flags.isInterface()) {
		    w.write(X10Flags.toX10Flags(flags.clearInterface().clearAbstract()).translate());
		}
		else {
		    w.write(X10Flags.toX10Flags(flags).translate());
		}
		
		if (flags.isInterface()) {
		    w.write("interface ");
		}
		else {
		    w.write("class ");
		}
		
		tr.print(n, n.name(), w);
		
		if (USE_JAVA_GENERICS) {
		    if (n.typeParameters().size() > 0) {
		        w.write("<");
		        w.begin(0);
		        String sep = "";
		        for (TypeParamNode tp : n.typeParameters()) {
		            w.write(sep);
		            n.print(tp, w, tr);
		            sep = ", ";
		        }
		        w.end();
		        w.write(">");
		    }
		}
		
		if (n.superClass() != null) {
		    w.allowBreak(0);
		    w.write("extends ");
		    printType(n.superClass().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES | NO_VARIANCE);
		}
		
		// Filter out x10.lang.Object from the interfaces.
		List<TypeNode> interfaces = new ArrayList<TypeNode>();
		
		for (TypeNode tn: n.interfaces()) {
		    Type sup = tn.type();
		    X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		    if (! ts.typeBaseEquals(sup, ts.Object(), tr.context()))
		            interfaces.add(tn);
		}
		
		if (! interfaces.isEmpty()) {
		    w.allowBreak(2);
		    if (flags.isInterface()) {
		        w.write("extends ");
		    }
		    else {
		        w.write("implements ");
		    }
		
		    w.begin(0);
		    for (Iterator<TypeNode> i = interfaces.iterator(); i.hasNext(); ) {
		        TypeNode tn = (TypeNode) i.next();
		        printType(tn.type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES | NO_VARIANCE);
		        if (i.hasNext()) {
		            w.write(",");
		            w.allowBreak(0);
		        }
		    }
		    w.end();
		}
		w.unifiedBreak(0);
		w.end();
		w.write("{");
		
		// Generate the run-time type.  We have to wrap in a class since n might be an interface
		// and interfaces can't have static methods.
		
		generateRTTMethods(def);
		
		boolean isValueType = xts.isValueType(def.asType(), (X10Context) tr.context());
		if (def.isTopLevel()) {
		    generateRTType(def);
		}

		// Generate dispatcher methods.
		generateDispatchers(def);
		
		if (n.typeParameters().size() > 0) {
		    w.newline(4);
		    w.begin(0);
		    if (! USE_JAVA_GENERICS) {
		        for (TypeParamNode tp : n.typeParameters()) {
		            w.write("@x10.generics.Synthetic public class ");
		            n.print(tp.name(), w, tr);
		            w.write(" { }");
		            w.newline();
		        }
		    }
		    if (! n.flags().flags().isInterface()) {
		        for (TypeParamNode tp : n.typeParameters()) {
		            w.write("private final ");
		            w.write(X10_RUNTIME_TYPE_CLASS);
		            w.write(" ");
		            n.print(tp.name(), w, tr);
		            w.write(";");
		            w.newline();
		        }
		    }
		    w.end();
		}
		
		if (! n.flags().flags().isInterface()) {
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
		
		n.print(n.body(), w, tr);
		w.write("}");
		w.newline(0);
		
		if (def.isLocal()) {
			generateRTType(def);
		}
	}
	
	private String mangle(QName name) {
	    String mangle = name.toString().replace(".", "$");
	    return mangle;
	}
	
	private void javacast(Node parent, Type t, int flags, Expr e) {
	    w.write("((");
	    printType(t, flags);
            w.write(") ");
            tr.print(parent, e, w);
            w.write(")");
            
//	    w.write("x10.types.Types.<");
//            printType(t, flags | BOX_PRIMITIVES);
//            w.write(">javacast(");
//	    tr.print(parent, e, w);
//	    w.write(")");
	}
	private void javacast(Type t, int flags, Expander e) {
	    w.write("((");
	    printType(t, flags);
	    w.write(") ");
	    e.expand(tr);
	    w.write(")");
//            w.write("x10.types.Types.<");
//            printType(t, flags | BOX_PRIMITIVES);
//            w.write(">javacast(");
//	    e.expand(tr);
//	    w.write(")");
	}
	private void javacast(Type t, int flags, String e) {
            w.write("((");
            printType(t, flags);
            w.write(") ");
	    w.write(e);
            w.write(")");
//            w.write("x10.types.Types.<");
//            printType(t, flags | BOX_PRIMITIVES);
//            w.write(">javacast(");
//	    w.write(e);
//	    w.write(")");
	}
	
    private void generateRTType(X10ClassDef def) {
        w.newline();

        String mangle = mangle(def.fullName());

        boolean isConstrained = def.classInvariant() != null && !def.classInvariant().get().valid();

        String superClass = "x10.types.RuntimeType";
        if (serialize_runtime_constraints && isConstrained) { // constrained type; treat specially
            superClass = "x10.types.ConstrainedType";
        }

        if (def.asType().isGloballyAccessible()) {
            w.write("public static ");
        }
        w.write("class ");
        
        Expander rttShortName;

        if (def.typeParameters().size() == 0) {
        	rttShortName = new Join("", rttShortName(def), "");
        }
        else {
        	X10ClassType ct = (X10ClassType) def.asType();
        	rttShortName = new Join(", ", ct.typeArguments());
        	List args = new ArrayList();
			for (int i = 0; i < ct.typeArguments().size(); i++) {
				Type a = ct.typeArguments().get(i);
				args.add(new TypeExpander(a, PRINT_TYPE_PARAMS | BOX_PRIMITIVES));
			}
			rttShortName = new Join("", rttShortName(def), "<", new Join(", ", args), ">");
        }

        rttShortName.expand(tr);
        
        w.write(" extends ");
        w.write(superClass);
        w.write("<");
        printType(def.asType(), BOX_PRIMITIVES | PRINT_TYPE_PARAMS | NO_VARIANCE);
        w.write("> {");
        w.newline();
        w.begin(4);
        if (def.asType().isGloballyAccessible() && def.typeParameters().size() == 0) {
            w.write("public static final ");
            rttShortName.expand(tr);
            w.write(" it = new ");
            rttShortName.expand(tr);
            w.write("();");
            w.newline();
            w.newline();
        }
        for (int i = 0; i < def.typeParameters().size(); i++) {
            ParameterType pt = def.typeParameters().get(i);
            w.write("public final x10.types.Type ");
            w.write(pt.name().toString());
            w.write(";");
            w.newline();
        }
        w.newline();
        w.write("public " + rttShortName(def) + "(");

        for (int i = 0; i <  def.typeParameters().size(); i++) {
            ParameterType pt = def.typeParameters().get(i);
            if (i != 0)
                w.write(", ");
            w.write("final x10.types.Type ");
            w.write(pt.name().toString());
        }

        w.write(") {");
        w.begin(4);
        w.write("super(");
        
        if (serialize_runtime_constraints && isConstrained) { // constrained type; treat specially
            w.write("new x10.types.RuntimeType<");
            printType(def.asType(), BOX_PRIMITIVES);
            w.write(">(");
            printType(def.asType(), BOX_PRIMITIVES);
            w.write(".class");
            w.write(")");
//            new RuntimeTypeExpander(def.asType()).expand(tr); // Cannot do this, because we are *defining* T.it here
            w.write(", ");
            w.write("null, "); // TODO
            XConstraint constraint = def.classInvariant().get();
            assert (constraint != null);
            serializeConstraint(constraint);
        }
        else {
            printType(def.asType(), BOX_PRIMITIVES);
            w.write(".class");
        }
        w.write(");");
        w.newline();
        for (int i = 0; i <  def.typeParameters().size(); i++) {
            ParameterType pt = def.typeParameters().get(i);
            w.write("this.");
            w.write(pt.name().toString());
            w.write(" = ");
            w.write(pt.name().toString());
            w.write(";");
            w.newline();
        }
        w.end();
        w.write("}");
        w.newline();
        w.write("public boolean instanceof$(java.lang.Object o) {");
        w.newline();
        w.begin(4);
        if (def.asType().isInnerClass()) {
            w.write("try { ");
            printType(def.asType(), BOX_PRIMITIVES);
            w.write(" zzz = (");
            printType(def.asType(), BOX_PRIMITIVES);
            w.write(") o; } catch (ClassCastException xyzzy) { return false; }");
        }
        else {
            w.write("if (! (o instanceof ");
            printType(def.asType(), BOX_PRIMITIVES);
            w.write(")) return false;");
        }
        w.newline();
        //		w.write("RTT ro = (RTT) new RTT(this, o);");
        //		w.newline();
        //		w.write("if (ro == null) return false;");
        //		w.newline();
        for (int i = 0; i <  def.typeParameters().size(); i++) {
            ParameterType pt = def.typeParameters().get(i);
            ParameterType.Variance var = def.variances().get(i);
            w.write("if (! ");
            switch (var) {
            case INVARIANT:
                w.write("this.");
                w.write(pt.name().toString());
                w.write(".equals(");
                javacast(def.asType(), BOX_PRIMITIVES, "o");
                w.write("." + "rtt_" + mangle(def.fullName()) + "_");
                w.write(pt.name().toString());
                w.write("()");
                w.write(")");
                break;
            case COVARIANT:
                w.write("this.");
                w.write(pt.name().toString());
                w.write(".isSubtype(");
                javacast(def.asType(), BOX_PRIMITIVES, "o");
                w.write("." + "rtt_" + mangle(def.fullName()) + "_");
                w.write(pt.name().toString());
                w.write("()");
                w.write(")");
                break;
            case CONTRAVARIANT:
                javacast(def.asType(), BOX_PRIMITIVES, "o");
                w.write("." + "rtt_" + mangle(def.fullName()) + "_");
                w.write(pt.name().toString());
                w.write("()");
                w.write(".isSubtype(");
                w.write("this.");
                w.write(pt.name().toString());
                w.write(")");
                break;
            }
            w.write(") return false;");
            w.newline();
        }
        w.write("return true;");
        w.end();
        w.newline();
        w.write("}");
        w.newline();
        
        for (Ref<? extends ClassType> mtref : def.memberClasses()) {
            X10ClassType mt = (X10ClassType) Types.get(mtref);
            generateRTType(mt.x10Def());
        }
        
        w.write("public java.util.List<x10.types.Type<?>> getTypeParameters() {");
        w.newline();
        w.begin(4);
        if (def.typeParameters().isEmpty())
            w.write("return null;");
        else {
            w.write("return java.util.Arrays.asList(new x10.types.Type<?>[] { ");
            w.newline();
            w.begin(4);
            for (int i = 0; i <  def.typeParameters().size(); i++) {
                ParameterType pt = def.typeParameters().get(i);
                if (i != 0)
                    w.write(", ");
                w.write(pt.name().toString());
            }
            w.end();
            w.newline();
            w.write(" });");
        }
        w.end();
        w.newline();
        w.write("}");
        w.end();
        w.newline();
        
        w.write("}");
        w.newline();
    }

    private void serializeConstraint(XConstraint constraint) {
//        String serializedConstraint = serializedForm(constraint);
//        StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED, serializedConstraint);
//        tr.print(null, lit, w);
        w.write("new x10.constraint.XConstraint_c() {{");
        w.newline(4);
        w.begin(0);
        w.write("try {");
        w.newline(4);
        w.begin(0);
        List<XTerm> terms = constraint.constraints();
        for (XTerm term : terms) {
            w.write("addTerm(");
            w.begin(0);
            serializeTerm(term, constraint);
            w.end();
            w.write(");");
            w.newline();
            //XConstraint_c c = new XConstraint_c(); c.addTerm(XTerms.makeEquals(XTerms.makeField(c.self(), XTerms.makeName("p")), XTerms.makeLit(2)))
        }
        w.end();
        w.newline();
        w.write("} catch (x10.constraint.XFailure f) {");
        w.newline(4);
        w.begin(0);
        w.write("setInconsistent();");
        w.end();
        w.newline();
        w.write("}");
        w.end();
        w.newline();
        w.write("}}");
    }
    
    private static final String XTERMS = "x10.constraint.XTerms";
    private void serializeTerm(XTerm term, XConstraint parent) {
        if (term.equals(parent.self())) {
            w.write("self()");
        } else
        if (term == XTerms.OPERATOR) {
            w.write(XTERMS+".OPERATOR");
        } else
        if (term instanceof XAnd_c) {
            w.write(XTERMS+".makeAnd(");
            w.begin(0);
            serializeTerm(((XAnd_c)term).left(), parent);
            w.write(",");
            w.allowBreak(0, " ");
            serializeTerm(((XAnd_c)term).right(), parent);
            w.end();
            w.write(")");
        } else
        if (term instanceof XEquals_c) {
            w.write(XTERMS+".makeEquals(");
            w.begin(0);
            serializeTerm(((XEquals_c)term).left(), parent);
            w.write(",");
            w.allowBreak(0, " ");
            serializeTerm(((XEquals_c)term).right(), parent);
            w.end();
            w.write(")");
        } else
        if (term instanceof XNot_c) {
            w.write(XTERMS+".makeNot(");
            w.begin(0);
            serializeTerm(((XNot_c)term).unaryArg(), parent);
            w.end();
            w.write(")");
        } else
        if (term instanceof XFormula_c) {
            if (!((XFormula_c)term).isAtomicFormula())
                throw new RuntimeException("Non-atomic formula encountered: "+term);
            w.write(XTERMS+".makeAtom(");
            w.begin(0);
            serializeName(((XFormula_c)term).operator());
            List<XTerm> arguments = ((XFormula_c)term).arguments();
            for (XTerm arg : arguments) {
                w.write(",");
                w.allowBreak(0, " ");
                serializeTerm(arg, parent);
            }
            w.end();
            w.write(")");
        } else
        if (term instanceof XTypeLit_c) {
            w.write("new x10.types.ConstrainedType.XTypeLit_c(");
            w.begin(0);
            new RuntimeTypeExpander(((XTypeLit_c)term).type()).expand(tr);
            w.end();
            w.write(")");
        } else
        if (term instanceof XLit_c) {
            Object val = ((XLit_c)term).val();
            w.write(XTERMS+".makeLit(");
            w.begin(0);
            if (val == null) {
                w.write("null");
            } else
            if (val instanceof Boolean || val instanceof Number) {
                w.write(val.toString());
            } else
            if (val instanceof Character) {
                CharLit lit = tr.nodeFactory().CharLit(Position.COMPILER_GENERATED, ((Character) val).charValue());
                tr.print(null, lit, w);
            } else
            if (val instanceof String) {
                StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED, (String) val);
                tr.print(null, lit, w);
            } else
                if (val instanceof QName) {
                    StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED, ((QName) val).toString());
                    tr.print(null, lit, w);
                } else
            if (val instanceof XName) {
                serializeName((XName) val);
            } else
            if (val.getClass() == Object.class) {
                StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED, val.toString());
                tr.print(null, lit, w);
            } else
            {
                throw new RuntimeException("Unknown value type "+val.getClass());
            }
            w.end();
            w.write(")");
        } else
        if (term instanceof XField_c) {
            w.write(XTERMS+".makeField((x10.constraint.XVar)");
            w.begin(0);
            serializeTerm(((XField_c)term).receiver(), parent);
            w.write(",");
            w.allowBreak(0, " ");
            serializeName(((XField_c)term).field());
            w.end();
            w.write(")");
        } else
        if (term instanceof XEQV_c) {
            w.write("genEQV(");
            w.begin(0);
            serializeName(((XEQV_c)term).name());
            w.write(",");
            w.allowBreak(0, " ");
            w.write(""+((XEQV_c)term).isEQV());
            w.end();
            w.write(")");
        } else
        if (term instanceof XLocal_c) {
            w.write(XTERMS+".makeLocal(");
            w.begin(0);
            serializeName(((XLocal_c)term).name());
            w.end();
            w.write(")");
        } else
        {
            throw new RuntimeException("Unknown term type "+term.getClass()+": "+term);
        }
    }
    
    private void serializeName(XName n) {
        assert (n instanceof XNameWrapper);
        XNameWrapper name = (XNameWrapper) n;
        w.write(XTERMS+".makeName(");
        w.begin(0);
        Object val = name.val();
        if (val == null) {
            w.write("null");
        } else
        if (val instanceof Def) {
            StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED, val.toString());
            tr.print(null, lit, w);
        } else
        if (val instanceof ParameterType) {
            StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED, val.toString());
            tr.print(null, lit, w);
        } else
        if (val instanceof Binary.Operator) {
            StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED, val.toString());
            tr.print(null, lit, w);
        } else
        if (val instanceof Unary.Operator) {
            StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED, val.toString());
            tr.print(null, lit, w);
        } else
        if (val instanceof String) {
       		StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED, val.toString());
       		tr.print(null, lit, w);
       	} else
        if (val.getClass() == Object.class) {
            StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED, val.toString());
            tr.print(null, lit, w);
        } else
        {
            throw new RuntimeException("Unknown value type "+val.getClass());
        }
        w.write(",");
        w.allowBreak(0, " ");
        StringLit lit = tr.nodeFactory().StringLit(Position.COMPILER_GENERATED, name.toString());
        tr.print(null, lit, w);
        w.end();
        w.write(")");
    }

    void generateRTTMethods(X10ClassDef def) {
    	generateRTTMethods(def, false);
    }
    
    void generateRTTMethods(X10ClassDef def, boolean boxed) {
    	Set<ClassDef> visited = new HashSet<ClassDef>();
    	visited.add(def);

    	// Generate RTTI methods, one for each parameter.
        for (int i = 0; i <  def.typeParameters().size(); i++) {
            ParameterType pt = def.typeParameters().get(i);
            ParameterType.Variance var = def.variances().get(i);
            
            w.write("public x10.types.Type<?> " + "rtt_" + mangle(def.fullName()) + "_");
            w.write(pt.name().toString());
            w.write("()");

            if (def.flags().isInterface()) {
                w.write(";");
            }
            else if (! boxed) {
                w.write(" { return this.");
                w.write(pt.name().toString());
                w.write("; }");
            }
            else {
            	w.write(" { throw new java.lang.RuntimeException(); }");
            }
            w.newline();
        }

        // Generate RTTI methods for each interface instantiation.
        if (! def.flags().isInterface()) {
                LinkedList<Type> worklist = new LinkedList<Type>();
                
            for (Type t : def.asType().interfaces()) {
                Type it = X10TypeMixin.baseType(t);
                worklist.add(it);
            }
            
            while (! worklist.isEmpty()) {
                Type it = worklist.removeFirst();
                
                if (it instanceof X10ClassType) {
                    X10ClassType ct = (X10ClassType) it;
                    X10ClassDef idef = ct.x10Def();
                    
                    if (visited.contains(idef))
                    	continue;
                    visited.add(idef);
                    
                    for (Type t : ct.interfaces()) {
                        Type it2 = X10TypeMixin.baseType(t);
                        worklist.add(it2);
                    }

                    for (int i = 0; i < idef.typeParameters().size(); i++) {
                        ParameterType pt = idef.typeParameters().get(i);
                        w.write("public x10.types.Type<?> " + "rtt_" + mangle(idef.fullName()) + "_");
                        w.write(pt.name().toString());
                        w.write("() { ");

                        if (! boxed) {
                        	w.write("return ");
                        	Type at = ct.typeArguments().get(i);
                        	new RuntimeTypeExpander(at).expand();
                        }
                        else {
                        	w.write("throw new java.lang.RuntimeException()");
                        }

                        w.write("; }");
                        w.newline();
                    }
                }
            }
        }
    }


	public void visit(X10Cast_c c) {
	        TypeNode tn = c.castType();
	        assert tn instanceof CanonicalTypeNode;
	        
	        switch (c.conversionType()) {
	        case CHECKED:
	        case PRIMITIVE:
	        case SUBTYPE:
	            if (tn instanceof X10CanonicalTypeNode) {
	                X10CanonicalTypeNode xtn = (X10CanonicalTypeNode) tn;

	                Type t = X10TypeMixin.baseType(xtn.type());
	                Expander ex = new TypeExpander(t, PRINT_TYPE_PARAMS);
	                Expander bex = (c.expr().type().isBoolean() || c.expr().type().isNumeric() || c.expr().type().isChar()) ? new TypeExpander(c.expr().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES) : null;
	                Expander uex = (c.expr().type().isBoolean() || c.expr().type().isNumeric() || c.expr().type().isChar()) ? new TypeExpander(c.expr().type(), PRINT_TYPE_PARAMS) : null;
	                Expander rt = new RuntimeTypeExpander(t);
	                
	                String template;
	                if (t.isBoolean() || t.isNumeric() || t.isChar()) {
	                    template = "primitive_cast_deptype";
	                }
	                else {
	                    template = "cast_deptype";
	                }
	                
	                DepParameterExpr dep = xtn.constraintExpr();
	                if (dep != null) {
	                    new Template(template, ex, c.expr(), rt, new Join(" && ", dep.condition())).expand();
	                }
	                else if (t.isBoolean() || t.isNumeric() || t.isChar() || c.expr().type().isSubtype(t, tr.context())) {
	                    w.begin(0);
	                    w.write("("); // put "(Type) expr" in parentheses.
	                    w.write("(");
	                    ex.expand(tr);
	                    w.write(")");
	                    if (bex != null) {
	                        w.write(" ");
	                        w.write("(");
	                        uex.expand(tr);
	                        w.write(")");
	                        w.write(" ");
	                        w.write("(");
	                        bex.expand(tr);
	                        w.write(")");
	                    }
	                    w.allowBreak(2, " ");
                            // HACK: (java.lang.Integer) -1
                            //       doesn't parse correctly, but
                            //       (java.lang.Integer) (-1)
                            //       does
                            if (c.expr() instanceof Unary || c.expr() instanceof Lit)
                                w.write("(");
	                    c.printSubExpr(c.expr(), w, tr);
                            if (c.expr() instanceof Unary || c.expr() instanceof Lit)
                                w.write(")");
	                    w.write(")");
	                    w.end();
	                }
	                else {
	                    new Template(template, ex, c.expr(), rt, "true").expand();
	                }
	            }
	            else {
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
	
	public void visit(X10Instanceof_c c) {
	        TypeNode tn = c.compareType();
	        
	        if (tn instanceof X10CanonicalTypeNode) {
	            X10CanonicalTypeNode xtn = (X10CanonicalTypeNode) tn;
	            Type t = X10TypeMixin.baseType(xtn.type());
	            DepParameterExpr dep = xtn.constraintExpr();
	            if (dep != null) {
	        	if (t.isBoolean() || t.isNumeric() || t.isChar()) {
	        	    new Template("instanceof_primitive_deptype", xtn.typeRef(Types.ref(t)), c.expr(), new Join(" && ", dep.condition())).expand();
	        	}
	        	else {
	        	    new Template("instanceof_deptype", xtn.typeRef(Types.ref(t)), c.expr(), new Join(" && ", dep.condition())).expand();
	        	}
	        	return;
	            }
	        }
	        
	        Type t = tn.type();
	        new RuntimeTypeExpander(t).expand(tr);
	        w.write(".");
	        w.write("instanceof$(");
	        tr.print(c, c.expr(), w);
	        w.write(")");
	}


	public void visit(Special_c n) {
		polyglot.types.Context c = tr.context();
		if (((X10Translator) tr).inInnerClass() && n.qualifier() == null && n.kind() != X10Special.SELF) {
			printType(n.type(), 0);
			w.write(".");
		}
		else if (n.qualifier() != null) {
		    printType(n.qualifier().type(), 0);
		    w.write(".");
		}

		w.write(n.kind().toString());
	}
	
	String getJavaImplForDef(X10Def o) {
	    X10TypeSystem xts = (X10TypeSystem) o.typeSystem();
	    try {
	        Type java = (Type) xts.systemResolver().find(QName.make("x10.compiler.Native"));
	        List<Type> as = o.annotationsMatching(java);
	        for (Type at : as) {
	            assertNumberOfInitializers(at, 2);
	            String lang = getPropertyInit(at, 0);
	            if (lang != null && lang.equals("java")) {
	                String lit = getPropertyInit(at, 1);
	                return lit;
	            }
	        }
	    }
	    catch (SemanticException e) {}
	    return null;
	}
	
	public void visit(X10ConstructorCall_c c) {
	    X10ConstructorCall_c n = c;
	    
	    if (n.qualifier() != null) {
	        tr.print(c, n.qualifier(), w);
	        w.write(".");
	    }
	    
	    w.write(c.kind() == ConstructorCall.THIS ? "this" : "super");
	    
	    w.write("(");
	    w.begin(0);
	    
	    X10ConstructorInstance mi = (X10ConstructorInstance) n.constructorInstance();
	    X10ClassType ct = (X10ClassType) mi.container();
	    
	    for (Iterator<Type> i = ct.typeArguments().iterator(); i.hasNext(); ) {
	        final Type at = i.next();
	        new RuntimeTypeExpander(at).expand(tr);
	        if (i.hasNext() || c.arguments().size() > 0) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	    }
	    
	    List l = c.arguments();
	    for (Iterator i = l.iterator(); i.hasNext(); ) {
	        Expr e = (Expr) i.next();
	        c.print(e, w, tr);
	        if (i.hasNext()) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	    }
	    
	    w.end();
	    w.write(");");	
	}
	public void visit(X10New_c c) {
	    X10New_c n = c;
	    if (n.qualifier() != null) {
	    	
	        tr.print(c, n.qualifier(), w);
	        w.write(".");
	    }

	    w.write("new ");
	    
	    if (n.qualifier() == null) {
	        printType(n.objectType().type(), PRINT_TYPE_PARAMS | NO_VARIANCE);
	    }
	    else {
	        printType(n.objectType().type(), PRINT_TYPE_PARAMS | NO_VARIANCE | NO_QUALIFIER);
	    }
	    
	    w.write("(");
	    w.begin(0);

	    X10ConstructorInstance mi = (X10ConstructorInstance) n.constructorInstance();
	    X10ClassType ct = (X10ClassType) mi.container();
	    
	    for (Iterator<Type> i = ct.typeArguments().iterator(); i.hasNext(); ) {
	        final Type at = i.next();
	        new RuntimeTypeExpander(at).expand(tr);
	        if (i.hasNext() || c.arguments().size() > 0) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	    }

	    List l = c.arguments();
	    for (Iterator i = l.iterator(); i.hasNext(); ) {
	        Expr e = (Expr) i.next();
	        c.print(e, w, tr);
	        if (i.hasNext()) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	    }
	    w.end();
	    w.write(")");
	    
	    if (c.body() != null) {
	        w.write("{");
	        tr.print(c, c.body(), w);
	        w.write("}");
	    }
	}
	
	public void visit(Import_c c) {
	    // don't generate any code at all--we should fully qualify all type names
	}

	public void visit(ClosureCall_c c) {
            Expr target = c.target();
            Type t = target.type();
            boolean base = false;
            
            X10TypeSystem xts = (X10TypeSystem) t.typeSystem();

            X10MethodInstance mi = c.closureInstance();

            c.printSubExpr(target, w, tr);
            w.write(".");
            w.write("apply");
            w.write("(");
            w.begin(0);

            for (Iterator<Type> i = mi.typeParameters().iterator(); i.hasNext(); ) {
                final Type at = i.next();
                new RuntimeTypeExpander(at).expand(tr);
                if (i.hasNext() || c.arguments().size() > 0) {
                    w.write(",");
                    w.allowBreak(0, " ");
                }
            }

            List l = c.arguments();
            for (Iterator i = l.iterator(); i.hasNext(); ) {
                Expr e = (Expr) i.next();
                c.print(e, w, tr);
                if (i.hasNext()) {
                    w.write(",");
                    w.allowBreak(0, " ");
                }
            }
            w.end();
            w.write(")");
	}
	
	public void visit(Tuple_c c) {
	    Type t = X10TypeMixin.getParameterType(c.type(), 0);
            new Template("tuple", 
                         new TypeExpander(t, true, true, false),
                         new RuntimeTypeExpander(t), 
                         new TypeExpander(t, true, false, false),
                         new Join(",", c.arguments())).expand();
	}

    /*
	 * For "java" annotations:
	 *
	 * Given a method with signature:
	 *     def m[X, Y](x, y);
	 * and a call
	 *     o.m[A, B](a, b);
	 * #0 = o
	 * #1 = A
	 * #2 = boxed representation of A
	 * #3 = run-time Type object for A
	 * #4 = B
	 * #5 = boxed representation of B
	 * #6 = run-time Type object for B
	 * #7 = a
	 * #8 = b
	 */
	private void emitNativeAnnotation(String pat, Object target, List<Type> types, List<? extends Object> args) {
		Object[] components = new Object[1 + types.size() * 3 + args.size()];
		int i = 0;
		components[i++] = target;
		for (Type at : types) {
			components[i++] = new TypeExpander(at, true, false, false);
			components[i++] = new TypeExpander(at, true, true, false);
			components[i++] = new RuntimeTypeExpander(at);
		}
		for (Object e : args) {
			components[i++] = e;
		}
		dumpRegex("Native", components, tr, pat);
	}
	
	public void visit(X10Call_c c) {
            X10Context context = (X10Context) tr.context();

		Receiver target = c.target();
		Type t = target.type();
		boolean base = false;
		
		X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		
		X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
		
		String pat = getJavaImplForDef(mi.x10Def());
		if (pat != null) {
			boolean needsHereCheck = needsHereCheck(target, context);
			Template tmp = null; 
			if (needsHereCheck && ! (target instanceof TypeNode || target instanceof New)) {
				tmp = new Template("place-check", new TypeExpander(target.type(), true, false, false), target);
			}
			emitNativeAnnotation(pat, null == tmp ? target : tmp, mi.typeParameters(), c.arguments());
			return;
		}

		// Check for properties accessed using method syntax.  They may have @Native annotations too.
		if (X10Flags.toX10Flags(mi.flags()).isProperty() && mi.formalTypes().size() == 0 && mi.typeParameters().size() == 0) {
			X10FieldInstance fi = (X10FieldInstance) mi.container().fieldNamed(mi.name());
			if (fi != null) {
				String pat2 = getJavaImplForDef(fi.x10Def());
				if (pat2 != null) {
				    Object[] components = new Object[] { target };
				    dumpRegex("Native", components, tr, pat2);
					return;
				}
			}
		}

		if (target instanceof TypeNode) {
		    printType(t, 0);
		}
                else {
                    
                    // add a check that verifies if the target of the call is in place 'here'
                    // This is not needed for:

                    boolean needsHereCheck = needsHereCheck((Expr) target, context);

                    if (! (target instanceof Special || target instanceof New)) {
                        w.write("(");

                        if (needsHereCheck) {
                            // don't annotate calls with implicit target, or this and super
                            // the template file only emits the target
                            new Template("place-check", new TypeExpander(t, true, false, false), target).expand();
                        }
                        else {
                            tr.print(c, target, w);
                        }

                        w.write(")");
                    }
                    else {
                        tr.print(c, target, w);
                    }
                }
		
		w.write(".");

		if (mi.typeParameters().size() > 0) {
		    w.write("<");
		    for (Iterator<Type> i = mi.typeParameters().iterator(); i.hasNext(); ) {
		        final Type at = i.next();
		        new TypeExpander(at, PRINT_TYPE_PARAMS | BOX_PRIMITIVES).expand(tr);
		        if (i.hasNext()) {
		            w.write(",");
		            w.allowBreak(0, " ");
		        }
		    }
		    w.write(">");
		}

		w.write(c.name().id().toString());

		w.write("(");
		w.begin(0);

		for (Iterator<Type> i = mi.typeParameters().iterator(); i.hasNext(); ) {
		    final Type at = i.next();
		    new RuntimeTypeExpander(at).expand(tr);
		    if (i.hasNext() || c.arguments().size() > 0) {
		        w.write(",");
		        w.allowBreak(0, " ");
		    }
		}

		List l = c.arguments();
		for (Iterator i = l.iterator(); i.hasNext(); ) {
		    Expr e = (Expr) i.next();
		    c.print(e, w, tr);
		    if (i.hasNext()) {
		        w.write(",");
		        w.allowBreak(0, " ");
		    }
		}
		w.end();
		w.write(")");
	}
	
	public void printFormal(Translator tr, Node n, Formal f, boolean mustBox) {
	    tr.print(n, f.flags(), w);
	    printType(f.type().type(), PRINT_TYPE_PARAMS | (mustBox ? BOX_PRIMITIVES : 0));
	    w.write(" ");

	    Name name = f.name().id();
	    if (name.toString().equals(""))
	        tr.print(n, f.name().id(Name.makeFresh("a")), w);
	    else {
	        tr.print(n, f.name(), w);
	    }
	}

	public void visit(final Closure_c n) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		tr2 = tr2.context(n.enterScope(tr2.context()));

		List<Expander> formals = new ArrayList();
		List<Expander> typeArgs = new ArrayList();
		for (final Formal f : n.formals()) {
	              TypeExpander ft = new TypeExpander(f.type().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
	              typeArgs.add(ft); // must box formals
	              formals.add(new Expander() {
	                public void expand(Translator tr) {
	                    printFormal(tr, n, f, true);
	                }
	              });
	        }

		TypeExpander ret = new TypeExpander(n.returnType().type(), true, true, false);
		if (!n.returnType().type().isVoid()) {
		    typeArgs.add(ret);
		    w.write("new x10.core.fun.Fun_0_" + n.formals().size());
		}
		else {		
		    w.write("new x10.core.fun.VoidFun_0_" + n.formals().size());
		}
		
		if (typeArgs.size() > 0) {
		    w.write("<");
		    new Join(", ", typeArgs).expand(tr2);
		    w.write(">");
		}
		
		w.write("() {");
		w.write("public final ");
		ret.expand(tr2);
		w.write(" apply(");
		new Join(", ", formals).expand(tr2);
		w.write(") { ");
		tr2.print(n, n.body(), w);
		w.write("}");
		w.newline();
		
		Type t = n.type();
		t = X10TypeMixin.baseType(t);
		if (t instanceof X10ClassType) {
		    X10ClassType ct = (X10ClassType) t;
		    generateRTTMethods(ct.x10Def());
		}
		
		w.write("}");
	}

	X10ClassType annotationNamed(TypeSystem ts, Node o, QName name) throws SemanticException {
		// Nate's code. This one.
		if (o.ext() instanceof X10Ext) {
			X10Ext ext = (X10Ext) o.ext();
			X10ClassType baseType = (X10ClassType) ts.systemResolver().find(name);
			List<X10ClassType> ats = ext.annotationMatching(baseType);
			if (ats.size() > 1) {
				throw new SemanticException("Expression has more than one " + name + " annotation.", o.position());
			}
			if (!ats.isEmpty()) {
				X10ClassType at = ats.get(0);
				return at;
			}
		}
		return null;
	}

	private boolean hasAnnotation(Node dec, QName name) {
		try {
			X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
			if (annotationNamed(ts, dec, name) != null)
				return true;
		} catch (NoClassException e) {
			if (!e.getClassName().equals(name.toString()))
				throw new InternalCompilerError("Something went terribly wrong", e);
		} catch (SemanticException e) {
			throw new InternalCompilerError("Something is terribly wrong", e);
		}
		return false;
	}
	
	public void visit(FieldDecl_c n) {
		if (hasAnnotation(n, QName.make("x10.lang.shared"))) {
			w.write ("volatile ");
		}
		visit((Node) n);
	}

	public void visit(PropertyDecl_c dec) {
//		polyglot.types.Context c = tr.context();
		// Don't generate property declarations for fields.
//		if (c.currentClass().flags().isInterface()) {
//			return;
//		}
		super.visit(dec);
	}

	private Template processClocks(Clocked c) {
		assert (null != c.clocks());
		Template clocks = null;
//		if (c.clocks().isEmpty())
//			clocks = null;
//		else if (c.clocks().size() == 1)
//			clocks = new Template("clock", c.clocks().get(0));
//		else {
			Integer id = getUniqueId_();
			clocks = new Template("clocked",
								  new Loop("clocked-loop", c.clocks(), new CircularList(id)),
								  id);
//		}
		return clocks;
	}

	public void visit(Async_c a) {
	    assert false;
//		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
//		new Template("Async",
//				a.place(),
//				processClocks(a),
//				a.body(),
//				a.position().nameAndLineString().replace("\\", "\\\\")).expand(tr2);
	}
	
	public void visit(AtStmt_c a) {
	    assert false;
//		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
//		new Template("At",
//					 a.place(),
//					 a.body(),
//                     getUniqueId_(),
//                     a.position().nameAndLineString().replace("\\", "\\\\")).expand(tr2);
	}

	public void visit(Atomic_c a) {
	    assert false;
//		new Template("Atomic", a.body(), getUniqueId_()).expand();
	}

	public void visit(Here_c a) {
	    assert false;
//		new Template("here").expand();
	}

	public void visit(Await_c c) {
        assert false;
//		new Template("await", c.expr(), getUniqueId_()).expand();
	}

	public void visit(Next_c d) {
        assert false;
//		new Template("next").expand();
	}

	public void visit(Future_c f) {
	    assert false;
//		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
//		new Template("Future", f.place(), new TypeExpander(f.returnType().type(), true, true, false, false), f.body(), new RuntimeTypeExpander(f.returnType().type()), f.position().nameAndLineString().replace("\\", "\\\\")).expand(tr2);
	}
	
	public void visit(AtExpr_c f) {
	    assert false;
//		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
//		new Template("AtExpr", f.place(), new TypeExpander(f.returnType().type(), true, true, false, false), f.body(), new RuntimeTypeExpander(f.returnType().type()), f.position().nameAndLineString().replace("\\", "\\\\")).expand(tr2);
	}

	public void visit(X10LocalDecl_c n) {
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
            tr.print(n, n.init(), w);
        }

        if (printSemi) {
            w.write(";");
        }

        tr.printType(printType);
        tr.appendSemicolon(printSemi);
	}

	public void visit(X10Formal_c n) {
	    tr.print(n, n.flags(), w);
	    tr.print(n, n.type(), w);
	    w.write(" ");
	    tr.print(n, n.name(), w);
	}

	public void visit(Formal_c f) {
	    if (f.name().id().toString().equals(""))
		f = (Formal_c) f.name(f.name().id(Name.makeFresh("a")));
	    visit((Node) f);
	}

	public void visit(ForLoop_c f) {
	        X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
		
	        X10Formal form = (X10Formal) f.formal();
	        
	        X10Context context = (X10Context) tr.context();

		/* TODO: case: for (point p:D) -- discuss with vj */
		/* handled cases: exploded syntax like: for (point p[i,j]:D) and for (point [i,j]:D) */
		if (Configuration.LOOP_OPTIMIZATIONS && form.hasExplodedVars() && (ts.isSubtype(f.domain().type(), ts.Region(), context) || ts.isSubtype(f.domain().type(), ts.Dist(), context)) && X10ArraysMixin.isRect(f.domain().type(), context)) {
			String regVar = getId().toString();
			List idxs = new ArrayList();
			List lims = new ArrayList();
			List idx_vars = new ArrayList();
			List vals = new ArrayList();
			LocalDef[] lis = form.localInstances();
			int rank = lis.length;

			for (int i = 0; i < rank; i++) {
				idxs.add(getId());
				lims.add(getId());
				idx_vars.add(lis[i].name());
				vals.add(new Integer(i));
			}

			Object body = f.body();

			if (!form.isUnnamed())
				body = new Join("\n",
								new Template("point-create",
												 form.flags(),
												 form.type(),
												 form.name(),
												 new Join(",", idxs)
								),
								body);

			body = new Join("\n",
							new Loop("final-var-assign", new CircularList<String>("int"), idx_vars, idxs),
							body);

			new Template("forloop-mult",
							 f.domain(),
							 regVar,
							 new Loop("forloop-mult-each", idxs, new CircularList<String>(regVar), vals, lims),
							 body,
//							 new Template("forloop",
//									 form.flags(),
//									 form.type(),
//									 form.name(),
//									 regVar,
//									 new Join("\n", new Join("\n", f.locals()), f.body()),
//     			   			   	     new TypeExpander(form.type().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES)
//								 )
							 new Inline("assert false;")
						 ).expand();
		} else
			new Template("forloop",
							 form.flags(),
							 form.type(),
							 form.name(),
							 f.domain(),
							 new Join("\n", new Join("\n", f.locals()), f.body()),
							 new TypeExpander(form.type().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES)
						 ).expand();
	}

	private void processClockedLoop(String template, X10ClockedLoop l) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		new Template(template,
						 /* #0 */ l.formal().flags(),
                         /* #1 */ l.formal().type(),
                         /* #2 */ l.formal().name(),
                         /* #3 */ l.domain(),
                         /* #4 */ new Join("\n", new Join("\n", l.locals()), l.body()),
                         /* #5 */ processClocks(l),
                         /* #6 */ new Join("\n", l.locals()),
                         /* #7 */ new TypeExpander(l.formal().type().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES),
                         /* #8 */ l.position().nameAndLineString().replace("\\", "\\\\")
					 ).expand(tr2);
	}

	public void visit(ForEach_c f) {
        assert (false);
		// System.out.println("X10PrettyPrinter.visit(ForEach c): |" + f.formal().flags().translate() + "|");
		processClockedLoop("foreach", f);
	}

	public void visit(AtEach_c f) {
        assert (false);
		processClockedLoop("ateach", f);
	}

	public void visit(Now_c n) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		new Template("Now", n.clock(), n.body()).expand(tr2);
	}
	
	boolean needsHereCheck( Receiver target, X10Context context) {
            boolean needsHereCheck = true;
            // calls on new objects
            needsHereCheck &= ! (target instanceof New);
            // others...
            needsHereCheck &= QueryEngine.INSTANCE().needsHereCheck(target.type(), context);

            if (needsHereCheck) {
                if (target instanceof X10Cast) {
                    X10Cast c = (X10Cast) target;
                    if (c.conversionType() == X10Cast.ConversionType.CHECKED) {
                        return needsHereCheck(c.expr(), context);
                    }
                }
            }
	    return needsHereCheck;
	}

	/*
	 * Field access -- this includes FieldAssign (because the left node of
	 * FieldAssign is a Field node!
	 */
	public void visit(Field_c n) {
		Receiver target = n.target();
		Type t = target.type();
		
		X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	        X10Context context = (X10Context) tr.context();
		X10FieldInstance fi = (X10FieldInstance) n.fieldInstance();
		
		if (target instanceof TypeNode) {
		    TypeNode tn = (TypeNode) target;
		    if (t instanceof ParameterType) {
		        // Rewrite to the class declaring the field.
		        FieldDef fd = fi.def();
		        t = Types.get(fd.container());
		        target = tn.typeRef(fd.container());
		        n = (Field_c) n.target(target);
		    }
		}

		String pat = getJavaImplForDef(fi.x10Def());
		if (pat != null) {
		    Object[] components = new Object[] { target };
		    dumpRegex("Native", components, tr, pat);
		    return;
		}

		if (target instanceof TypeNode) {
		    printType(t, 0);
		    w.write(".");
		    w.write(n.name().id().toString());
		}
		else {

                    boolean is_location_access = xts.isReferenceOrInterfaceType(fi.container(), context) && fi.name().equals(Name.make("location"));
                    boolean needsHereCheck = needsHereCheck((Expr) target, context) && ! is_location_access;

		    if (needsHereCheck) {
		        // no check required for implicit targets, this and super
		        // the template file only emits the target
		        new Template("place-check", new TypeExpander(t, true, false, false), target).expand();
		        // then emit '.' and name of the field.
		        w.write(".");
		        w.write(n.name().id().toString());
		    } else
		        // WARNING: it's important to delegate to the appropriate visit() here!
		        visit((Node)n);

		}
	}

	public void visit(IntLit_c n) {
	    String val;
	    if (n.kind() == IntLit_c.LONG)
	        val = Long.toString(n.value()) + "L";
	    else if (n.kind() == IntLit_c.INT) {
	        if (n.value() >= 0x80000000L)
	            val = "0x" + Long.toHexString(n.value());
	        else
	            val = Long.toString((int) n.value());
	    } else
	        throw new InternalCompilerError("Unrecognized IntLit kind " + n.kind());
	    w.write(val);
	}

//	private Stmt optionalBreak(Stmt s) {
//		NodeFactory nf = tr.nodeFactory();
//		if (s.reachable())
//			return nf.Break(s.position());
//		// [IP] Heh, Empty cannot be unreachable either.  Who knew?
////		return nf.Empty(s.position());
//		return null;
//	}

	public void visit(When_c w) {
	    assert false;
//		Integer id = getUniqueId_();
//		List breaks = new ArrayList(w.stmts().size());
//		for (Iterator i = w.stmts().iterator(); i.hasNext(); ) {
//			Stmt s = (Stmt) i.next();
//			breaks.add(optionalBreak(s));
//		}
//		new Template("when",
//						 w.expr(),
//						 w.stmt(),
//						 optionalBreak(w.stmt()),
//						 new Loop("when-branch", w.exprs(), w.stmts(), breaks),
//						 id
//					 ).expand();
	}

	public void visit(Finish_c a) {
	    assert false;
//		new Template("finish", a.body(), getUniqueId_()).expand();
	}

	private TypeNode getParameterType(Type at) {
	    NodeFactory nf = tr.nodeFactory();
	    Type parameterType = X10TypeMixin.getParameterType(at, 0);
	    if (parameterType != null)
	        return nf.CanonicalTypeNode(Position.COMPILER_GENERATED,
	                                    parameterType);
	    return null;
	}
	
	public boolean hasEffects(Receiver e) {
	    if (e instanceof TypeNode)
	        return false;
	    if (e instanceof Local)
	        return false;
	    if (e instanceof Lit)
	        return false;
	    if (e instanceof Field) {
	        Field f = (Field) e;
	        return hasEffects(f.target());
	    }
	    if (e instanceof Unary) {
	        Unary u = (Unary) e;
	        if (u.operator() == Unary.BIT_NOT || u.operator() == Unary.NOT || u.operator() == Unary.POS || u.operator() == Unary.NEG)
	            return hasEffects(u.expr());
	    }
	    if (e instanceof Binary) {
	        Binary b = (Binary) e;
	        return hasEffects(b.left()) || hasEffects(b.right());
	    }
	    if (e instanceof Cast) {
	        Cast c = (Cast) e;
	        return hasEffects(c.expr());
	    }
	    if (e instanceof Instanceof) {
	        Instanceof i = (Instanceof) e;
	        return hasEffects(i.expr());
	    }
	    // HACK: Rail.apply has no effects
	    if (e instanceof ClosureCall) {
	        ClosureCall c = (ClosureCall) e;
	        Expr target = c.target();
	        if (hasEffects(target))
	            return true;
	        for (Expr a : c.arguments()) {
	            if (hasEffects(a))
	                return true;
	        }
	        X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
	        if (ts.isRail(target.type()) || ts.isValRail(target.type()))
	            return false;
	    }
	    if (e instanceof Call) {
	        Call c = (Call) e;
	        Receiver target = c.target();
	        if (hasEffects(target))
	            return true;
	        for (Expr a : c.arguments()) {
	            if (hasEffects(a))
	                return true;
	        }
	        X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
	        if (c.name().id().equals(Name.make("apply")))
	            if (ts.isRail(target.type()) || ts.isValRail(target.type()))
	                return false;
	    }
	    return true;
	}

	public void arrayPrint(Node n, Node array, CodeWriter w, Template tmp ) {
		if (null == tmp) {
			tr.print(n, array, w);
		} else {
			w.write("(");
			tmp.expand();
			w.write(")");
		}
		
	}
	
	public void visit(SettableAssign_c n) {
	    SettableAssign_c a = n;
	    Expr array = a.array();
	    List<Expr> index = a.index();

	    boolean effects = hasEffects(array);
	    for (Expr e : index) {
	        if (effects)
	            break;
	        if (hasEffects(e))
	            effects = true;
	    }

	    TypeSystem ts = tr.typeSystem();
	    X10Context context = (X10Context) tr.context();
	    Type t = n.leftType();

        boolean needsHereCheck = needsHereCheck(array, context);
        Template tmp = null; 
        if (needsHereCheck) {
        	tmp = new Template("place-check", new TypeExpander(array.type(), true, false, false), array);
        }
      
	    boolean nativeop = false;
	    if (t.isNumeric() || t.isBoolean() || t.isChar() || t.isSubtype(ts.String(), context)) {
	        nativeop = true;
	    }

	    X10MethodInstance mi = (X10MethodInstance) n.methodInstance();
	    boolean superUsesClassParameter = ! mi.flags().isStatic() ; // && overridesMethodThatUsesClassParameter(mi);

	    if (n.operator() == Assign.ASSIGN) {
	    	// Look for the appropriate set method on the array and emit native code if there is an
	    	// @Native annotation on it.
	    	List<Expr> args = new ArrayList<Expr>(index.size()+1);
	    	//args.add(array);
	    	args.add(n.right());
	    	for (Expr e : index) args.add(e);
	    	String pat = getJavaImplForDef(mi.x10Def());

	    	if (pat != null) {
	    		emitNativeAnnotation(pat, null == tmp ? array : tmp, mi.typeParameters(), args);
	    		return;
	    	} else {
	    		// otherwise emit the hardwired code.
	    		arrayPrint(n, array, w, tmp);
	    		w.write(".set");
	    		w.write("(");
	    		tr.print(n, n.right(), w);
	    		if (index.size() > 0)
	    		    w.write(", ");
	    		new Join(", ", index).expand(tr);
	    		w.write(")");
	    	}
	    }
	    else if (! effects) {
	        Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
	        Name methodName = X10Binary_c.binaryMethodName(op);
	        arrayPrint(n, array, w, tmp);
	        w.write(".set");
	        w.write("((");
	        tr.print(n, array, w);
	        w.write(").apply(");
	        new Join(", ", index).expand(tr);
	        w.write(")");
	        if (nativeop) {
	            w.write(" ");
	            w.write(op.toString());
	            tr.print(n, n.right(), w);
	        }
	        else {
	            w.write(".");
	            w.write(methodName.toString());
	            w.write("(");
	            tr.print(n, n.right(), w);
	            w.write(")");
	        }
	        if (index.size() > 0)
	            w.write(", ");
	        new Join(", ", index).expand(tr);
	        w.write(")");
	    }
	    else {
	        // new Object() { T eval(R target, T right) { return (target.f = target.f.add(right)); } }.eval(x, e)
	        Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
	        Name methodName = X10Binary_c.binaryMethodName(op);
	        w.write("new java.lang.Object() {");
	        w.allowBreak(0, " ");
	        w.write("final ");
	        printType(n.type(), PRINT_TYPE_PARAMS);
	        w.write(" eval(");
	        printType(array.type(), PRINT_TYPE_PARAMS);
	        w.write(" array");
	        {
	            int i = 0;
	            for (Expr e : index) {
	                w.write(", ");
	                printType(e.type(), PRINT_TYPE_PARAMS);
	                w.write(" ");
	                w.write("i" + i);
	                i++;
	            }
	        }
	        w.write(", ");
	        printType(n.right().type(), PRINT_TYPE_PARAMS);
	        w.write(" right) {");
	        w.allowBreak(0, " ");
	        if (! n.type().isVoid()) {
	            w.write("return ");
	        }
	        w.write("array.set");
    		w.write("(");
	        
	        w.write(" array.apply(");
	        {
	            int i = 0;
	            for (Expr e : index) {
	                if (i != 0)
	                    w.write(", ");
	                w.write("i" + i);
	                i++;
	            }
	        }
	        w.write(")");
	        if (nativeop) {
	            w.write(" ");
	            w.write(op.toString());
	            w.write(" right");
	        }
	        else {
	            w.write(".");
	            w.write(methodName.toString());
	            w.write("(right)");
	        }
	        if (index.size() > 0)
	            w.write(", ");
	        {
	            int i = 0;
	            for (Expr e : index) {
	                if (i != 0)
	                    w.write(", ");
	                w.write("i" + i);
	                i++;
	            }
	        }
	        w.write(");");
	        w.allowBreak(0, " ");
	        w.write("} }.eval(");
	        arrayPrint(n, array, w, tmp);
	        if (index.size() > 0)
	            w.write(", ");
	        new Join(", ", index).expand();
	        w.write(", ");
	        tr.print(n, n.right(), w);
	        w.write(")");
	    }
	}
	
	String getPropertyInit(Type at, int index) {
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
	
	void assertNumberOfInitializers(Type at, int len) {
	    at = X10TypeMixin.baseType(at);
	    if (at instanceof X10ClassType) {
	        X10ClassType act = (X10ClassType) at;
	        assert len == act.propertyInitializers().size();
	    }
	}
	
	boolean printRepType(Type type, boolean printGenerics, boolean boxPrimitives, boolean inSuper) {
		if (type.isVoid()) {
			w.write("void");
			return true;
		}
		
	    // If the type has a native representation, use that.
	    if (type instanceof X10ClassType) {
	        X10ClassDef cd = ((X10ClassType) type).x10Def();
	        String pat = getJavaRep(cd);
	        if (pat != null) {
	        	if (boxPrimitives) {
	        		String[] s = new String[] { "boolean", "byte", "char", "short", "int", "long", "float", "double" };
	        		String[] w = new String[] { "java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double" };
	        		for (int i = 0; i < s.length; i++) {
	        			if (pat.equals(s[i])) {
	        				pat = w[i];
	        				break;
	        			}
	        		}
	        	}
	            List<Type> typeArguments = ((X10ClassType) type).typeArguments();
	            Object[] o = new Object[typeArguments.size()+1];
	            int i = 0;
	            NodeFactory nf = tr.nodeFactory();
	            o[i++] = new TypeExpander(type, printGenerics, boxPrimitives, inSuper);
	            for (Type a : typeArguments) {
	                o[i++] = new TypeExpander(a, printGenerics, true, inSuper);
	            }
	            if (! printGenerics) {
	                pat = pat.replaceAll("<.*>", "");
	            }
	            dumpRegex("NativeRep", o, tr, pat);
	            return true;
	        }
	    }
	    
	    return false;
	}
	
	public static final int PRINT_TYPE_PARAMS = 1;
	public static final int BOX_PRIMITIVES = 2;
	public static final int NO_VARIANCE = 4;
	public static final int NO_QUALIFIER = 8;
	
	private void printType(Type type, int flags) {
	    boolean printTypeParams = (flags & PRINT_TYPE_PARAMS) != 0;
	    boolean boxPrimitives = (flags & BOX_PRIMITIVES) != 0;
	    boolean inSuper = (flags & NO_VARIANCE) != 0;
	    boolean ignoreQual = (flags & NO_QUALIFIER) != 0;
	    
		X10TypeSystem xts = (X10TypeSystem) type.typeSystem();
		
		type = X10TypeMixin.baseType(type);
		
		if (type instanceof X10ClassType) { 
		    X10ClassType ct = (X10ClassType) type;
		    if (ct.isAnonymous()) {
		        if (ct.interfaces().size() > 0) {
		            printType(ct.interfaces().get(0), flags);
		            return;
		        }
		        else if (ct.superClass() != null) {
		            printType(ct.superClass(), flags);
		            return;
		        }
		        else {
		            assert false;
		            printType(xts.Object(), flags);
		            return;
		        }
		    }
		}

		if (printRepType(type, printTypeParams, boxPrimitives, inSuper))
		    return;

		if (type instanceof ParameterType) {
		    if (USE_JAVA_GENERICS) {
		        w.write(((ParameterType) type).name().toString());
		    }
		    else {
		        w.write("java.lang.Object");
		    }
		    return;
		}
		
		if (type instanceof ClosureType) {
		    ClosureType ct = (ClosureType) type;
		    List<Type> args = ct.argumentTypes();
		    Type ret = ct.returnType();
		    if (ret.isVoid()) {
		        w.write("x10.core.fun.VoidFun");
		    }
		    else {
		        w.write("x10.core.fun.Fun");
		    }
		    w.write("_" + ct.typeParameters().size());
		    w.write("_" + args.size());
		    if (USE_JAVA_GENERICS && printTypeParams && args.size() + (ret.isVoid() ? 0 : 1) > 0) {
		        w.write("<");
		        String sep = "";
		        for (Type a : args) {
		            w.write(sep);
		            sep = ",";
		            printType(a, (printTypeParams ? PRINT_TYPE_PARAMS : 0) | BOX_PRIMITIVES);
		        }
		        if (! ret.isVoid()) {
		            w.write(sep);
		            printType(ret, (printTypeParams ? PRINT_TYPE_PARAMS : 0) | BOX_PRIMITIVES);
		        }
		        w.write(">");
		    }
		    return;
		}

		// Shouldn't get here.
		if (type instanceof MacroType) {
		    MacroType mt = (MacroType) type;
		    printType(mt.definedType(), PRINT_TYPE_PARAMS);
		    return;
		}
				
		// Print the class name
		if (ignoreQual) {
		    if (type instanceof X10ClassType) {
				w.write(((X10ClassType) type).name().toString());
		    }
		    else {
		        type.print(w);
		    }
		}
		else {
			type.print(w);
		}

		if (printTypeParams && USE_JAVA_GENERICS) {
			if (type instanceof X10ClassType) {
				X10ClassType ct = (X10ClassType) type;
				String sep = "<";
				for (int i = 0; i < ct.typeArguments().size(); i++) {
					w.write(sep);
					sep = ", ";

					Type a = ct.typeArguments().get(i);

					final boolean variance = false;
					if (! inSuper && variance) {
						ParameterType.Variance v = ct.x10Def().variances().get(i);
						switch (v) {
						case CONTRAVARIANT:
							w.write("? super ");
							break;
						case COVARIANT:
							w.write("? extends ");
							break;
						case INVARIANT:
							break;
						}
					}
					printType(a, (printTypeParams ? PRINT_TYPE_PARAMS : 0) | BOX_PRIMITIVES);
				}
				if (ct.typeArguments().size() > 0)
					w.write(">");
			}
		}
	}
	
	public void visit(CanonicalTypeNode_c n) {
	    Type t = n.type();
	    if (t != null)
	        printType(t, PRINT_TYPE_PARAMS);
	    else
	        // WARNING: it's important to delegate to the appropriate visit() here!
	        visit((Node)n);
	}

	public void visit(X10Unary_c n) {
		Expr left = n.expr();
		X10Type l = (X10Type) left.type();
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		NodeFactory nf = tr.nodeFactory();
		Unary.Operator op = n.operator();
		
		if (op == Unary.POST_DEC || op == Unary.POST_INC || op == Unary.PRE_DEC || op == Unary.PRE_INC) {
		    Expr expr = left;
		    Type t = left.type();

		    Expr target = null;
                    List<Expr> args = null;
                    List<TypeNode> typeArgs = null;
                    X10MethodInstance mi = null;

                    // Handle a(i)++ and a.apply(i)++
                    if (expr instanceof ClosureCall) {
                        ClosureCall e = (ClosureCall) expr;
                        target = e.target();
                        args = e.arguments();
                        typeArgs = e.typeArgs();
                        mi = e.closureInstance();
                    }
                    else if (expr instanceof X10Call) {
                        X10Call e = (X10Call) expr;
                        if (e.target() instanceof Expr && e.name().id() == Name.make("apply")) {
                            target = (Expr) e.target();
                            args = e.arguments();
                            typeArgs = e.typeArguments();
                            mi = (X10MethodInstance) e.methodInstance();
                        }
                    }
                    
                    X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
                    if (mi != null) {
                        MethodInstance setter = null;
                        
                        List<Type> setArgTypes = new ArrayList<Type>();
                        List<Type> setTypeArgs = new ArrayList<Type>();
                        for (Expr e : args) {
                            setArgTypes.add(e.type());
                        }
                        setArgTypes.add(expr.type());
                        for (TypeNode tn : typeArgs) {
                            setTypeArgs.add(tn.type());
                        }
                        try {
                            setter = ts.findMethod(target.type(), ts.MethodMatcher(t, Name.make("set"), setTypeArgs, setArgTypes, tr.context()));
                        }
                        catch (SemanticException e) {
                        }
                        
                        // TODO: handle type args
                        // TODO: handle setter method
                        
                        w.write("new java.lang.Object() {");
                        w.allowBreak(0, " ");
                        w.write("final ");
                        printType(t, PRINT_TYPE_PARAMS);
                        w.write(" eval(");
                        printType(target.type(), PRINT_TYPE_PARAMS);
                        w.write(" target");
                        {int i = 0;
                        for (Expr e : args) {
                            w.write(", ");
                            printType(e.type(), PRINT_TYPE_PARAMS);
                            w.write(" a" + (i+1));
                            i++;
                        }}
                        w.write(") {");
                        w.allowBreak(0, " ");
                        printType(left.type(), PRINT_TYPE_PARAMS);
                        w.write(" old = ");
                        String pat = getJavaImplForDef(mi.x10Def());
                        if (pat != null) {
                            Object[] components = new Object[args.size()+1];
                            int j = 0;
                            components[j++] = "target";
                            {int i = 0;
                            for (Expr e : args) {
                                components[j++] = "a" + (i+1);
                                i++;
                            }}
                            dumpRegex("Native", components, tr, pat);
                        }
                        else {
                            w.write("target.apply(");
                            {int i = 0;
                            for (Expr e : args) {
                                if (i > 0) w.write(", ");
                                w.write("a" + (i+1));
                                i++;
                            }}
                            w.write(")");
                        }
                        w.write(";");
                        w.allowBreak(0, " ");
                        printType(left.type(), PRINT_TYPE_PARAMS);
                        w.write(" neu = (");
                        printType(left.type(), PRINT_TYPE_PARAMS);
                        w.write(") old");
                        w.write((op == Unary.POST_INC || op == Unary.PRE_INC ? "+" : "-") + "1");
                        w.write(";");
                        w.allowBreak(0, " ");
                        w.write("target.set(neu");
                        {int i = 0;
                        for (Expr e : args) {
                            w.write(", ");
                            w.write("a" + (i+1));
                            i++;
                        }}
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
                        new Join(", ", args).expand(tr);
                        w.write(")");
                        
                        return;
                    }
		}
		
		if (l.isNumeric() || l.isBoolean()) {
		    visit((Unary_c)n);
		    return;
		}
		
		Name methodName = X10Unary_c.unaryMethodName(op);
		if (methodName != null)
		    generateStaticOrInstanceCall(n.position(), left, methodName);
		else
		    throw new InternalCompilerError("No method to implement " + n, n.position());
		return;
	}
	
	public void visit(SubtypeTest_c n) {
	    // TODO: generate a real run-time test: if sub and sup are parameters, then this should test the actual parameters.
	    TypeNode sub = n.subtype();
	    TypeNode sup = n.supertype();
	    
	    w.write("((");
	    new RuntimeTypeExpander(sub.type()).expand(tr);
	    w.write(")");
	    if (n.equals()) {
	        w.write(".equals(");
	    }
	    else {
	        w.write(".isSubtype(");
	    }
	    new RuntimeTypeExpander(sup.type()).expand(tr);
	    w.write("))");
	    
//	    if (sub.type().isSubtype(sup.type())) {
//	        w.write("true");
//	    }
//	    else {
//	        w.write("false");
//	    }
	}
	
	public void visit(X10Binary_c n) {
		Expr left = n.left();
		X10Type l = (X10Type) left.type();
		Expr right = n.right();
		X10Type r = (X10Type) right.type();
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		NodeFactory nf = tr.nodeFactory();
		Binary.Operator op = n.operator();
		
		
		if (op == Binary.EQ) {
			new Template("equalsequals", left, right).expand();
			return;
		}
		
		if (op == Binary.NE) {
			new Template("notequalsequals", left, right).expand();
			return;
		}
		
		if (l.isNumeric() && r.isNumeric()) {
		    visit((Binary_c)n);
		    return;
		}
		
		if (l.isBoolean() && r.isBoolean()) {
		    visit((Binary_c)n);
		    return;
		}
		
		if (op == Binary.ADD && (l.isSubtype(xts.String(), tr.context()) || r.isSubtype(xts.String(), tr.context()))) {
		    visit((Binary_c)n);
		    return;
		}
		if (n.invert()) {
		    Name methodName = X10Binary_c.invBinaryMethodName(op);
		    if (methodName != null) {
		        generateStaticOrInstanceCall(n.position(), right, methodName, left);
		        return;
		    }
		}
		else {
		    Name methodName = X10Binary_c.binaryMethodName(op);
		    if (methodName != null) {
		        generateStaticOrInstanceCall(n.position(), left, methodName, right);
		        return;
		    }
		}
		throw new InternalCompilerError("No method to implement " + n, n.position());
	}

	/**
         * @param pos
         * @param left
         *                TODO
         * @param name
         * @param right
         *                TODO
         */
	private void generateStaticOrInstanceCall(Position pos, Expr left, Name name, Expr... right) {
	    List<Expr> sargs = new ArrayList();
	    List<Type> stypes = new ArrayList();
	    sargs.add(left);
	    stypes.add(left.type());
	    for (Expr e: right) {
	        sargs.add(e);
	        stypes.add(e.type());
	    }
	    List<Type> types = stypes.subList(1, stypes.size());
	    List<Expr> args = sargs.subList(1, sargs.size());

	    X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
	    NodeFactory nf = tr.nodeFactory();
	    try {
	        MethodInstance mi = xts.findMethod(left.type(),
	                                           xts.MethodMatcher(left.type(), name, types, tr.context()));
	        tr.print(null, nf.Call(pos, left, nf.Id(pos, name), args).methodInstance(mi).type(mi.returnType()), w);
	    }
	    catch (SemanticException e) {
	        throw new InternalCompilerError(e.getMessage(), pos, e);
	    }
	}

	/**
	 * Pretty-print a given object.
	 *
	 * @param o object to print
	 */
	private void prettyPrint(Object o, Translator tr) {
		if (o instanceof Expander) {
			((Expander) o).expand(tr);
		} else if (o instanceof Node) {
			((Node) o).del().translate(w, tr);
		} else if (o instanceof Type) {
			throw new InternalCompilerError("Should not attempt to pretty-print a type");
		} else if (o != null) {
			w.write(o.toString());
		}
	}

	/**
	 * Expand a given template with given parameters.
	 *
	 * @param id xcd filename for the template
	 * @param components arguments to the template.
	 */
	private void dump(String id, Object[] components, Translator tr) {
		String regex = translate(id);
		dumpRegex(id, components, tr, regex);
	}

	private void dumpRegex(String id, Object[] components, Translator tr, String regex) {
	    for (int i = 0; i < components.length; i++) {
	        assert ! (components[i] instanceof Object[]);
	    }
	    int len = regex.length();
	    int pos = 0;
	    int start = 0;
	    while (pos < len) {
	    	if (regex.charAt(pos) == '\n') {
	    		w.write(regex.substring(start, pos));
	    		w.newline(0);
	    		start = pos+1;
	    	}
	    	else
	    	if (regex.charAt(pos) == '#') {
	    		w.write(regex.substring(start, pos));
	    		Integer idx = new Integer(regex.substring(pos+1,pos+2));
	    		pos++;
	    		start = pos+1;
	    		if (idx.intValue() >= components.length)
	    			throw new InternalCompilerError("Template '"+id+"' uses #"+idx);
	    		prettyPrint(components[idx.intValue()], tr);
	    	}
	    	pos++;
	    }
	    w.write(regex.substring(start));
	}
	
	String rttShortName(X10ClassDef cd) {
		if (cd.isMember() || cd.isLocal())
			return cd.name() + "$RTT";
		else
			return "RTT";
	}
	
	String rttName(X10ClassDef cd) {
		if (cd.isTopLevel())
			return cd.fullName() + "." + rttShortName(cd);
		if (cd.isLocal())
			return rttShortName(cd);
		if (cd.isMember()) {
			X10ClassType container = (X10ClassType) Types.get(cd.container());
			return rttName(container.x10Def()) + "." + rttShortName(cd);
		}
		assert false : "unexpected class " + cd;
		return "";
	}
	
	private final class RuntimeTypeExpander extends Expander {
	    private final Type at;

	    private RuntimeTypeExpander(Type at) {

	        if (at instanceof X10ClassType) {
	            X10ClassType ct = (X10ClassType) at;

	            if (ct.isAnonymous()) {
	                if (ct.interfaces().size() > 0) {
	                    ct = (X10ClassType) ct.interfaces().get(0);
	                }
	                else if (ct.superClass() != null) {
	                    ct = (X10ClassType) ct.superClass();
	                }
	            }

	            at = ct;
	        }

	        this.at = at;
	    }

	    public void expand(Translator tr) {
	        String s = typeof(at);
	        if (s != null) {
	            w.write(s);
	            return;
	        }
	        
	        if (at instanceof ParameterType) {
	            ParameterType pt = (ParameterType) at;
	            w.write(pt.name().toString());
	            return;
	        }

	        if (at instanceof ClosureType) {
	            ClosureType ct = (ClosureType) at;
	            List<Type> args = ct.argumentTypes();
	            Type ret = ct.returnType();
	            w.write("new ");
	            if (ret.isVoid()) {
	                w.write("x10.core.fun.VoidFun");
	            }
	            else {
	                w.write("x10.core.fun.Fun");
	            }
	            w.write("_" + ct.typeParameters().size());
	            w.write("_" + args.size());
	            w.write(".RTT(");
	            String sep = "";
	            for (Type a : args) {
	                w.write(sep);
	                sep = ",";
	                new RuntimeTypeExpander(a).expand(tr);
	            }
	            if (! ret.isVoid()) {
	                w.write(sep);
	                new RuntimeTypeExpander(ret).expand(tr);
	            }
	            w.write(")");
	            return;
	        }

	        if (at instanceof X10ClassType) {
	            X10ClassType ct = (X10ClassType) at;
	            X10ClassDef cd = ct.x10Def();
	            String pat = getJavaRTTRep(cd);
	            
	            // Check for @NativeRep with null RTT class
	            if (pat == null && getJavaRep(cd) != null) {
	            	w.write("x10.types.Types.runtimeType(");
	            	printType(at, 0);
	            	w.write(".class");
	            	w.write(")");
	            	return;
	            }
	            
	            if (pat == null) {
	                if (ct.isGloballyAccessible() && ct.typeArguments().size() == 0) {
	                    w.write(rttName(cd));
	                    w.write(".it");
	                }
	                else {
	                    w.write("new ");
	                    w.write(rttName(cd));
	                    
	                    w.write("<");
	                    for (int i = 0; i < ct.typeArguments().size(); i++) {
	                    	if (i != 0)
	                    		w.write(", ");
	                    	new TypeExpander(ct.typeArguments().get(i), PRINT_TYPE_PARAMS | BOX_PRIMITIVES).expand(tr);
	                    }
	                    w.write(">");

	                    w.write("(");
	                    for (int i = 0; i < ct.typeArguments().size(); i++) {
	                        if (i != 0)
	                            w.write(", ");
	                        new RuntimeTypeExpander(ct.typeArguments().get(i)).expand(tr);
	                    }
	                    w.write(")");
	                }
	                return;
	            }
	            else {
	            	Object[] components = new Object[1 + ct.typeArguments().size() * 2];
	            	int i = 0;
	            	components[i++] = new TypeExpander(ct, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
	            	for (Type at : ct.typeArguments()) {
	            		components[i++] = new TypeExpander(at, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
	            		components[i++] = new RuntimeTypeExpander(at);
	            	}
	            	dumpRegex("Native", components, tr, pat);
	            	return;
	            }
	        }
	        
	        if (at instanceof ConstrainedType) {
	            ConstrainedType ct = (ConstrainedType) at;
	            Type base = ct.baseType().get();
	            if (serialize_runtime_constraints) {
	                XConstraint constraint = ct.constraint().get();
	                w.write("new x10.types.ConstrainedType(");
	                new RuntimeTypeExpander(base).expand(tr);
	                w.write(", ");
	                w.write("null, ");
	                serializeConstraint(constraint);
	                w.write(")");
	            }
	            else {
	                new RuntimeTypeExpander(base).expand(tr);
	            }
	            return;
	        }

	        w.write("x10.types.Types.runtimeType(");
	        printType(at, 0);
	        w.write(".class");
	        w.write(")");
	    }

	    String typeof(Type t) {
	        if (t.isBoolean())
	            return "x10.types.Types.BOOLEAN";
	        if (t.isByte())
	            return "x10.types.Types.BYTE";
	        if (t.isShort())
	            return "x10.types.Types.SHORT";
	        if (t.isChar())
	            return "x10.types.Types.CHAR";
	        if (t.isInt())
	            return "x10.types.Types.INT";
	        if (t.isLong())
	            return "x10.types.Types.LONG";
	        if (t.isFloat())
	            return "x10.types.Types.FLOAT";
	        if (t.isDouble())
	            return "x10.types.Types.DOUBLE";
	        return null;
	    }
	}

    /**
	 * An abstract class for sub-template expansion.
	 */
	public abstract class Expander {
		public void expand() {
			expand(X10PrettyPrinterVisitor.this.tr);
		}

		public abstract void expand(Translator tr);
	}
	/**
	 * An abstract class for sub-template expansion.
	 */
	public class Inline extends Expander {
		private final String str;
		public Inline(String str) {
			this.str = str;
		}
		public void expand(Translator tr) {
			prettyPrint(str, tr);
		}
	}

	/**
	 * Expand a given template with the given set of arguments.
	 * Equivalent to a Loop with an array of singleton lists.
	 * If the template has zero, one, two, or three arguments, the
	 * arguments can be passed in directly to the constructor.
	 */
	public class Template extends Expander {
		private final String id;
		//private final String template;
		private final Object[] args;
		public Template(String id, Object... args) {
			this.id = id;
			this.args = args;
		}
		public void expand() {
			expand(X10PrettyPrinterVisitor.this.tr);
		}
		public void expand(Translator tr) {
			dump(id, args, tr);
		}
	}

	/**
	 * A list of one object that has an infinite circular iterator.
	 */
	public static class CircularList<T> extends AbstractList<T> {
		private T o;
		public CircularList(T o) { this.o = o; }
		public Iterator<T> iterator() {
			return new Iterator<T>() {
				public boolean hasNext() { return true; }
				public T next() { return o; }
				public void remove() { return; }
			};
		}
		public T get(int i) { return o; }
		public int size() { return -1; }
	}

	/**
	 * Expand a given template in a loop with the given set of arguments.
	 * For the loop body, pass in an array of Lists of identical length
	 * (each list representing all instances of a given argument),
	 * which will be translated into array-length repetitions of the
	 * loop body template.
	 * If the template has only one argument, a single list can be used.
	 */
	public class Loop extends Expander {
		private final String id;
		//private final String template;
		private final List[] lists;
		private final int N;
		public Loop(String id, List arg) {
			this(id, new List[] { arg });
		}
		public Loop(String id, List arg1, List arg2) {
			this(id, new List[] { arg1, arg2 });
		}
		public Loop(String id, List arg1, List arg2, List arg3) {
			this(id, new List[] { arg1, arg2, arg3 });
		}
		public Loop(String id, List arg1, List arg2, List arg3, List arg4) {
			this(id, new List[] { arg1, arg2, arg3, arg4 });
		}
		public Loop(String id, List[] components) {
			this.id = id;
			//this.template = translate(id);
			this.lists = components;
			// Make sure we have at least one parameter
			assert(lists.length > 0);
			int n = -1;
			int i = 0;
			for (; i < lists.length && n == -1; i++)
				n = lists[i].size();
			// Make sure the lists are all of the same size or circular
			for (; i < lists.length; i++)
				assert(lists[i].size() == n || lists[i].size() == -1);
			this.N = n;
		}
		public void expand(Translator tr) {
			w.write("/* Loop: { */");
			Object[] args = new Object[lists.length];
			Iterator[] iters = new Iterator[lists.length];
			// Parallel iterators over all argument lists
			for (int j = 0; j < lists.length; j++)
				iters[j] = lists[j].iterator();
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < args.length; j++)
					args[j] = iters[j].next();
				dump(id, args, tr);
			}
			w.write("/* } */");
		}
	}
	
	public class TypeExpander extends Expander {
	    Type t;
	    int flags;

	    public TypeExpander(Type t, int flags) {
	        this.t = t;
	        this.flags = flags;
	    }
	    
	    public TypeExpander(Type t, boolean printGenerics, boolean boxPrimitives, boolean inSuper) {
	        this(t, (printGenerics ? PRINT_TYPE_PARAMS: 0) | (boxPrimitives ? BOX_PRIMITIVES : 0) | (inSuper ? NO_VARIANCE : 0));
	    }
	    
	    public String toString() {
	    	if ((flags & BOX_PRIMITIVES) != 0)
	    		return "BP<" + t.toString() + ">";
	        return t.toString();
	    }

	    @Override
	    public void expand(Translator tr) {
//	        Translator old = X10PrettyPrinterVisitor.this.tr;
	        try {
//	            X10PrettyPrinterVisitor.this.tr = tr;
	            printType(t, flags);
	        }
	        finally {
//	            X10PrettyPrinterVisitor.this.tr = old;
	        }
	    }
	}

	private static List asList(Object a, Object b) {
		List l = new ArrayList(2); l.add(a); l.add(b); return l;
	}
	private static List asList(Object a, Object b, Object c) {
		List l = new ArrayList(3); l.add(a); l.add(b); l.add(c); return l;
	}
	private static List asList(Object a, Object b, Object c, Object d) {
		List l = new ArrayList(3); l.add(a); l.add(b); l.add(c); l.add(d); return l;
	}
	private static List asList(Object a, Object b, Object c, Object d, Object e) {
	    List l = new ArrayList(3); l.add(a); l.add(b); l.add(c); l.add(d); l.add(e); return l;
	}

	/**
	 * Join a given list of arguments with a given delimiter.
	 * Two or three arguments can also be specified separately.
	 * Do not join a circular list.
	 */
	public class Join extends Expander {
		private final String delimiter;
		private final List args;
		public Join(String delimiter, Object a) {
			this(delimiter, Collections.singletonList(a));
		}
		public Join(String delimiter, Object a, Object b) {
			this(delimiter, asList(a, b));
		}
		public Join(String delimiter, Object a, Object b, Object c) {
			this(delimiter, asList(a, b, c));
		}
		public Join(String delimiter, Object a, Object b, Object c, Object d) {
			this(delimiter, asList(a, b, c, d));
		}
		public Join(String delimiter, Object a, Object b, Object c, Object d, Object e) {
		    this(delimiter, asList(a, b, c, d, e));
		}
		public Join(String delimiter, List args) {
			this.delimiter = delimiter;
			this.args = args;
		}
        public void expand(Translator tr) {
			w.write("/* Join: { */");
			int N = args.size();
			for (Iterator i = args.iterator(); i.hasNext(); ) {
				prettyPrint(i.next(), tr);
				if (i.hasNext())
					prettyPrint(delimiter, tr);
			}
			w.write("/* } */");
		}
	}

	static HashMap translationCache_ = new HashMap();

	static String translate(String id) {
		String cached = (String) translationCache_.get(id);
		if (cached != null)
			return cached;
		try {
			String rname = Configuration.COMPILER_FRAGMENT_DATA_DIRECTORY + id + ".xcd"; // xcd = x10 compiler data/definition
			InputStream is = X10PrettyPrinterVisitor.class.getClassLoader().getResourceAsStream(rname);
			if (is == null)
				throw new IOException("Cannot find resource '"+rname+"'");
			byte[] b = new byte[is.available()];
			for (int off = 0; off < b.length; ) {
				int read = is.read(b, off, b.length - off);
				off += read;
			}
			String trans = new String(b, "UTF-8");
			// Skip initial lines that start with "// SYNOPSIS: "
			// (spaces matter!)
			while (trans.indexOf("// SYNOPSIS: ") == 0)
				trans = trans.substring(trans.indexOf('\n')+1);
			// Remove one trailing newline (if any)
			if (trans.lastIndexOf('\n') == trans.length()-1)
				trans = trans.substring(0, trans.length()-1);
			boolean newline = trans.lastIndexOf('\n') == trans.length()-1;
			trans = "/* template:"+id+" { */" + trans + "/* } */";
			// If the template ends in a newline, add it after the footer
			if (newline)
				trans = trans + "\n";
			translationCache_.put(id, trans);
			is.close();
			return trans;
		} catch (IOException io) {
			throw new InternalCompilerError("No translation for " + id + " found!", io);
		}
	}
} // end of X10PrettyPrinterVisitor

