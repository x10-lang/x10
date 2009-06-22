/*
 *
 * (C) Copyright IBM Corporation 2006
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Binary_c;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Cast;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign_c;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.Id;
import polyglot.ast.Import_c;
import polyglot.ast.Instanceof;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.Special_c;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.ast.Binary.Operator;
import polyglot.ext.x10.Configuration;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.Atomic_c;
import polyglot.ext.x10.ast.Await_c;
import polyglot.ext.x10.ast.Clocked;
import polyglot.ext.x10.ast.ClosureCall;
import polyglot.ext.x10.ast.ClosureCall_c;
import polyglot.ext.x10.ast.Closure_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.Finish_c;
import polyglot.ext.x10.ast.ForEach_c;
import polyglot.ext.x10.ast.ForLoop_c;
import polyglot.ext.x10.ast.Future;
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
import polyglot.ext.x10.ast.TypePropertyNode;
import polyglot.ext.x10.ast.When_c;
import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10Call_c;
import polyglot.ext.x10.ast.X10CanonicalTypeNode;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10ClassDecl_c;
import polyglot.ext.x10.ast.X10ClockedLoop;
import polyglot.ext.x10.ast.X10ConstructorCall_c;
import polyglot.ext.x10.ast.X10ConstructorDecl_c;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10Instanceof_c;
import polyglot.ext.x10.ast.X10MethodDecl_c;
import polyglot.ext.x10.ast.X10New_c;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.ast.X10Unary_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.query.QueryEngine;
import polyglot.ext.x10.types.ClosureType;
import polyglot.ext.x10.types.MacroType;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.PathType;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.ext.x10.types.X10ArraysMixin;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10Def;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.NoClassException;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.Translator;


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
        public static final String X10_RUNTIME_CLASS = "x10.runtime.Runtime";

        public static final boolean USE_JAVA_GENERICS = true;
        
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
	
	// Wrap every Expr in a cast.  Grr... only works if in a context expecting an expr.
//	       public void visitAppropriate(JL n) {
//	           if (n instanceof Expr) {
//	               Expr e = (Expr) n;
//	               w.write("((");
//	               printType(e.type(), true, false, false);
//	               w.write(") ");
//	               super.visitAppropriate(n);
//	               w.write(")");
//	           }
//	           else {
//	               super.visitAppropriate(n);
//	           }
//	       }

	public void visit(LocalAssign_c n) {
	    Local l = n.local();
	    TypeSystem ts = tr.typeSystem();
	    if (n.operator() == Assign.ASSIGN || l.type().isNumeric() || l.type().isBoolean() || l.type().isSubtype(ts.String())) {
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
	    if (n.operator() == Assign.ASSIGN || t.isNumeric() || t.isBoolean() || t.isSubtype(ts.String())) {
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
	        w.write("new java.lang.Object() { ");
	        printType(n.type(), PRINT_TYPE_PARAMS);
	        w.write(" eval(");
	        printType(n.target().type(), PRINT_TYPE_PARAMS);
	        w.write(" target, ");
	        printType(n.right().type(), PRINT_TYPE_PARAMS);
	        w.write(" right) {");
	        w.newline();
	        w.write("return (target.");
	        w.write(n.name().id().toString());
	        w.write(" = ");
	        w.write("target.");
	        w.write(n.name().id().toString());
	        w.write(".");
	        w.write(methodName.toString());
	        w.write("(right));");
	        w.newline();
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
	    w.write("(");
	    w.write("(");
	    printType(expected, PRINT_TYPE_PARAMS);
	    w.write(") ");
	    tr.print(parent, e, w);
	    w.write(") ");
	}
	
	public void visit(X10MethodDecl_c n) {
	    X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
	    
	    Flags flags = n.flags().flags();

	    if (n.name().id().toString().equals("main") &&
	            flags.isPublic() &&
	            flags.isStatic() &&
	            n.returnType().type().isVoid() &&
	            n.formals().size() == 1 &&
	            ((Formal)n.formals().get(0)).declType().typeEquals(ts.Rail(ts.String())))
	    {
	        new Template("Main", n.formals().get(0), n.body()).expand();
	        return;
	    }
	    
	    // Do not box methods of java.lang.Object
	    boolean shouldBox = true;
	    {
	        MethodInstance mi = n.methodDef().asInstance();
	        for (MethodInstance mj : mi.implemented()) {
	            if (mj.container().typeEquals(ts.Object())) {
	                shouldBox = false;
	            }
	        }
	    }
	    
	    Expander unboxedReturn = new TypeExpander(n.returnType().type(), true, false, false);
	    Expander boxedReturn = new TypeExpander(n.returnType().type(), true, true, false);
	    List<Expander> unboxedFormals = new ArrayList<Expander>();
	    List<Expander> boxedFormals = new ArrayList<Expander>();
	    for (Formal f : n.formals()) {
	        Expander e = new TypeExpander(f.type().type(), true, false, false);
	        unboxedFormals.add(e);
	        Expander e2 = new TypeExpander(f.type().type(), true, true, false);
	        boxedFormals.add(e2);
	    }
	    
	    boolean generateUnboxedVersion = false;

	    if (USE_JAVA_GENERICS) {
	        X10MethodDef def = (X10MethodDef) n.methodDef();
	        Type returnType = Types.get(def.returnType());
	        if (returnType.isBoolean() || returnType.isNumeric()) {
	            generateUnboxedVersion = true;
	        }
	        for (int i = 0; ! generateUnboxedVersion && i < def.formalTypes().size(); i++) {
	            Type argType = Types.get(def.formalTypes().get(i));
	            if (argType.isBoolean() || argType.isNumeric()) {
	                generateUnboxedVersion = true;
	            }
	        }

	        generateMethodDecl(n, false, false);
	    }
	    else {
	        generateMethodDecl((X10MethodDecl_c) n.name(n.name().id(Name.make(n.name().id() + "$"))), false, true);
	        generateMethodDecl(n, true, false);
	    }
	    
	    // Translation of generics:
	    // 1. add type arguments to all calls
	    // 2. translate all parameter types to Object
	    // 3. if a method overrides a generic method, create a dispatch
	    //    method with the formals as Object.
	    // 4. cast every expression to it's translated type.
            
	}
	
	private void generateMethodDecl(X10MethodDecl_c n, boolean box, boolean dispatch) {
	    X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();

	    Flags flags = n.flags().flags();

	    Context c = tr.context();

	    if (c.currentClass().flags().isInterface()) {
	        flags = flags.clearPublic();
	        flags = flags.clearAbstract();
	    }
	    
	    if (dispatch)
	        flags = flags.clearNative();

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
	            printType(n.typeParameters().get(i).type(), BOX_PRIMITIVES);
	        }
	        if (n.typeParameters().size() > 0)
	            w.write("> ");
	    }

	    printType(n.returnType().type(), PRINT_TYPE_PARAMS | (box ? BOX_PRIMITIVES : 0));
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
                w.write(X10_RUNTIME_TYPE_CLASS);
                w.write(" ");
                w.write(p.name().id().toString());
            }

            List<Expander> dispatchArgs = new ArrayList<Expander>();
            
	    for (int i = 0; i < n.formals().size(); i++) {
	        Formal f = n.formals().get(i);
	        if (! first) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	        first = false;
	        
	        tr.print(n, f.flags(), w);
	        printType(f.type().type(), PRINT_TYPE_PARAMS | (box ? BOX_PRIMITIVES : 0));
	        w.write(" ");

	        Name name = f.name().id();
	        if (name.toString().equals("")) {
	            name = Name.makeFresh("a");
	        }
	        tr.print(n, f.name().id(name), w);

	        dispatchArgs.add(new Join("", CollectionUtil.list("(", new TypeExpander(f.type().type(), true, true, false), ") ", name.toString())));
	    }
	    
	    w.end();
	    w.write(")");
	    
	    if (! n.throwTypes().isEmpty()) {
	        w.allowBreak(6);
	        w.write("throws ");

	        for (Iterator i = n.throwTypes().iterator(); i.hasNext(); ) {
	            TypeNode tn = (TypeNode) i.next();
	            tr.print(n, tn, w);

	            if (i.hasNext()) {
	                w.write(",");
	                w.allowBreak(4, " ");
	            }
	        }
	    }

	    w.end();

	    if (dispatch && ! flags.isAbstract() && ! c.currentClass().flags().isInterface()) {
	        w.write(" ");
	        w.write("{");
	        w.allowBreak(4);

	        if (! n.returnType().type().isVoid()) {
	            w.write(" return       ");
	        }

	        if (flags.isStatic()) {
	            printType(Types.get(n.methodDef().container()), 0);
	        }
	        else {
	            w.write("this");
	        }

	        w.write(".");
	        tr.print(n, n.name(), w);
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
	    else {
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
		        n.print(tn, w, tr);
		
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
		                n.printSubStmt(n.body().statements().get(0), w, tr);
		                w.allowBreak(0, " ");
		            }
		        }
		        for (String s : typeAssignments) {
		            w.write(s);
		            w.allowBreak(0, " ");
		        }
		        if (n.body().statements().size() > 0) {
                            if (n.body().statements().get(0) instanceof ConstructorCall)
                                n.printSubStmt(n.body().statements(n.body().statements().subList(1, n.body().statements().size())), w, tr);
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
	
	public void visit(TypeDecl_c n) {
	    // Do not write anything.
	    return;
	}
	
	public String getJavaRep(X10ClassDef def) {
	    try {
	        X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
	        Type rep = (Type) xts.systemResolver().find(QName.make("x10.compiler.NativeRep"));
	        List<Type> as = def.annotationsMatching(rep);
	        for (Type at : as) {
	            assertNumberOfInitializers(at, 2);
	            String lang = getPropertyInit(at, 0);
	            if (lang != null && lang.equals("java")) {
	                return getPropertyInit(at, 1);
	            }
	        }
	    }
	    catch (SemanticException e) {}
	    return null;
	}

	public void visit(X10ClassDecl_c n) {
	    X10ClassDef def = (X10ClassDef) n.classDef();
	    
	    // Do not generate code if the class is represented natively.
	    if (getJavaRep(def) != null) {
	        w.write(";");
	        w.newline();
	        return;
	    }

	    StringBuilder sb = new StringBuilder();
	    for (TypeParamNode t : n.typeParameters()) {
		if (sb.length() > 0) {
		    sb.append(", ");
		}
		sb.append("\"");
		sb.append(t.name().id());
		sb.append("\"");
	    }
	    
	    if (sb.length() > 0) {
		w.write("@x10.generics.Parameters({");
		w.write(sb.toString());
		w.write("})");
		w.allowBreak(0);
	    }
	    
//	    @Parameters({"T"})
//	    public class List implements Runnable {
//	    	public class T {};
//	    	public List() { }
//	    	@Synthetic public List(Class T) { this(); }
//	    	@Synthetic public static boolean instanceof$(Object o, String constraint) { assert(false); return true; }
//	    	public static boolean instanceof$(Object o, String constraint, boolean b) { /*check constraint*/; return b; }
//	    	public static Object cast$(Object o, String constraint) { /*check constraint*/; return (List)o; }
//	    	T x;
	    	
		Flags flags = n.flags().flags();
		
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
		    if (! ts.typeBaseEquals(sup, ts.Object()))
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
		
		if (def.isTopLevel() || def.isLocal())
		    generateRTType(def);
		generateRTTMethods(def);
		
		if (n.typeProperties().size() > 0) {
		    w.newline(4);
		    w.begin(0);
		    for (TypePropertyNode tp : n.typeProperties()) {
			w.write("@x10.generics.Synthetic public class ");
			n.print(tp.name(), w, tr);
			w.write(" { }");
			w.newline();
		    }
		    if (! n.flags().flags().isInterface()) {
			for (TypePropertyNode tp : n.typeProperties()) {
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
	}
	
	private String mangle(QName name) {
	    String mangle = name.toString().replace(".", "$");
	    return mangle;
	}
	
    private void generateRTType(X10ClassDef def) {
        w.newline();

        String mangle = mangle(def.fullName());

        if (def.asType().isGloballyAccessible()) {
            w.write("static ");
        }
        w.write("public class " + rttShortName(def) + " extends x10.types.RuntimeType<");
        printType(def.asType(), BOX_PRIMITIVES);
        w.write("> {");
        w.newline();
        w.begin(4);
        if (def.asType().isGloballyAccessible() && def.typeParameters().size() == 0) {
            w.write("public static final " + rttShortName(def) + " it = new " + rttShortName(def) + "();");
            w.newline();
            w.newline();
        }
        for (int i = 0; i <  def.typeParameters().size(); i++) {
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
            w.write("x10.types.Type ");
            w.write(pt.name().toString());
        }

        w.write(") {");
        w.begin(4);
        w.write("super(");
        printType(def.asType(), BOX_PRIMITIVES);
        w.write(".class);");
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
        w.write("if (! (o instanceof ");
        printType(def.asType(), BOX_PRIMITIVES);
        w.write(")) return false;");
        //		w.write("RTT ro = (RTT) new RTT(this, o);");
        //		w.newline();
        //		w.write("if (ro == null) return false;");
        //		w.newline();
        for (int i = 0; i <  def.typeParameters().size(); i++) {
            ParameterType pt = def.typeParameters().get(i);
            TypeProperty.Variance var = def.variances().get(i);
            w.write("if (! ");
            switch (var) {
            case INVARIANT:
                w.write("this.");
                w.write(pt.name().toString());
                w.write(".equals(");
                w.write("((");
                printType(def.asType(), BOX_PRIMITIVES);
                w.write(") o)." + "rtt_" + mangle(def.fullName()) + "_");
                w.write(pt.name().toString());
                w.write("()");
                w.write(")");
                break;
            case COVARIANT:
                w.write("this.");
                w.write(pt.name().toString());
                w.write(".isSubtype(");
                w.write("((");
                printType(def.asType(), BOX_PRIMITIVES);
                w.write(") o)." + "rtt_" + mangle(def.fullName()) + "_");
                w.write(pt.name().toString());
                w.write("()");
                w.write(")");
                break;
            case CONTRAVARIANT:
                w.write("((");
                printType(def.asType(), BOX_PRIMITIVES);
                w.write(") o)." + "rtt_" + mangle(def.fullName()) + "_");
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
        w.write("}");
        w.end();
        
        for (Ref<? extends ClassType> mtref : def.memberClasses()) {
            X10ClassType mt = (X10ClassType) Types.get(mtref);
            generateRTType(mt.x10Def());
        }
        
        w.write("}");
        w.newline();
    }
    
    void generateRTTMethods(X10ClassDef def) {
        // Generate RTTI methods, one for each parameter.
        for (int i = 0; i <  def.typeParameters().size(); i++) {
            ParameterType pt = def.typeParameters().get(i);
            TypeProperty.Variance var = def.variances().get(i);

            w.write("public x10.types.Type<?> " + "rtt_" + mangle(def.fullName()) + "_");
            w.write(pt.name().toString());

            if (def.flags().isInterface()) {
                w.write("();");
            }
            else {
                w.write("() { return this.");
                w.write(pt.name().toString());
                w.write("; }");
            }
            w.newline();
        }

        // Generate RTTI methods for each interface instantiation.
        if (! def.flags().isInterface()) {
            for (Type t : def.asType().interfaces()) {
                Type it = X10TypeMixin.baseType(t);
                if (it instanceof X10ClassType) {
                    X10ClassType ct = (X10ClassType) it;
                    X10ClassDef idef = ct.x10Def();
                    for (int i = 0; i < idef.typeParameters().size(); i++) {
                        ParameterType pt = idef.typeParameters().get(i);
                        w.write("public x10.types.Type<?> " + "rtt_" + mangle(idef.fullName()) + "_");
                        w.write(pt.name().toString());
                        w.write("() { return ");
                        new RuntimeTypeExpander(ct.typeArguments().get(i)).expand();
                        w.write("; }");
                        w.newline();
                    }
                }
            }
        }
    }

	public void visit(X10Cast_c c) {
	        TypeNode tn = c.castType();
	        
	        if (tn instanceof X10CanonicalTypeNode) {
	            X10CanonicalTypeNode xtn = (X10CanonicalTypeNode) tn;
	            Type t = X10TypeMixin.baseType(xtn.type());
	            DepParameterExpr dep = xtn.constraintExpr();
	            if (dep != null) {
	        	new Template("cast_deptype", xtn.typeRef(Types.ref(t)), c.expr(), dep.condition()).expand();
	            }
	            else {
	        	visit((Node)c);
	            }
	        }
	        else {
	            visit((Node)c);
	        }
	}
	
	public void visit(X10Instanceof_c c) {
	        TypeNode tn = c.compareType();
	        
	        if (tn instanceof X10CanonicalTypeNode) {
	            X10CanonicalTypeNode xtn = (X10CanonicalTypeNode) tn;
	            Type t = X10TypeMixin.baseType(xtn.type());
	            DepParameterExpr dep = xtn.constraintExpr();
	            if (dep != null) {
	        	if (t.isBoolean() || t.isNumeric()) {
	        	    new Template("instanceof_primitive_deptype", xtn.typeRef(Types.ref(t)), c.expr(), dep.condition()).expand();
	        	}
	        	else {
	        	    new Template("instanceof_deptype", xtn.typeRef(Types.ref(t)), c.expr(), dep.condition()).expand();
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
            Receiver target = c.target();
            Type t = target.type();
            boolean base = false;
            
            X10TypeSystem xts = (X10TypeSystem) t.typeSystem();

            X10MethodInstance mi = c.closureInstance();

            tr.print(c, target, w);
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

	public void visit(X10Call_c c) {
		Receiver target = c.target();
		Type t = target.type();
		boolean base = false;
		
		X10TypeSystem xts = (X10TypeSystem) t.typeSystem();

		X10MethodInstance mi = (X10MethodInstance) c.methodInstance();

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
		String pat = getJavaImplForDef(mi.x10Def());
		if (pat != null) {
		    Object[] components = new Object[1 + mi.typeParameters().size() * 3 + c.arguments().size()];
		    int i = 0;
		    components[i++] = c.target();
		    for (Type at : mi.typeParameters()) {
		        components[i++] = new TypeExpander(at, true, false, false);
		        components[i++] = new TypeExpander(at, true, true, false);
		        components[i++] = new RuntimeTypeExpander(at);
		    }
		    for (Expr e : c.arguments()) {
		        components[i++] = e;
		    }
		    dumpRegex("Native", components, tr, pat);
		    return;
		}

		
		if (target instanceof TypeNode) {
		    printType(t, 0);
		    w.write(".");
		    w.write(c.name().id().toString());
		}
		else {
		    // add a check that verifies if the target of the call is in place 'here'
		    // This is not needed for:

		    boolean needsHereCheck = true;
		    // calls on future literals
		    needsHereCheck &= ! (target instanceof Future);
		    // calls on this
		    needsHereCheck &= ! (target instanceof Special);
		    // calls on new objects
		    needsHereCheck &= ! (target instanceof New);
		    // others...
		    needsHereCheck &= QueryEngine.INSTANCE().needsHereCheck(c);

		    if (needsHereCheck) {
		        // don't annotate calls with implicit target, or this and super
		        // the template file only emits the target
		        new Template("place-check", new TypeExpander(t, true, false, false), target).expand();
		    }
		    else {
		        tr.print(c, target, w);
		    }
		    
		    w.write(".");
		    
		    // Call the unboxed version of the method if we know it exists.
		    boolean callUnboxedMethod = false;
		    
		    if (! USE_JAVA_GENERICS) {
		        for (MethodInstance mj : mi.overrides()) {
		            Type container = mj.container();
		            container = X10TypeMixin.baseType(container);
		            if (container instanceof X10ClassType) {
		                X10ClassType ct = (X10ClassType) container;
		                if (! ct.flags().isInterface()
		                && ct.x10Def().typeParameters().size() == 0) {
		                    callUnboxedMethod = true;
		                }
		            }
		        }
		    }

		    if (callUnboxedMethod) {
		        w.write(c.name().id().toString());
		        w.write("$");
		    }
		    else {
		        w.write(c.name().id().toString());
		    }
		}

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
	              TypeExpander ft = new TypeExpander(f.type().type(), true, true, false);
	              typeArgs.add(ft); // must box formals
	              formals.add(new Expander() {
	                public void expand(Translator tr) {
	                    printFormal(tr, n, f, true);
	                }
	              });
	        }
		
		TypeExpander ret = new TypeExpander(n.returnType().type(), true, true, false);
		typeArgs.add(ret);

		w.write("new x10.core.fun.Fun_0_" + n.formals().size() + "<");
		new Join(", ", typeArgs).expand(tr2);
		w.write(">() {");
		w.write("public ");
		ret.expand(tr2);
		w.write(" apply(");
		new Join(", ", formals).expand(tr2);
		w.write(") { ");
		tr2.print(n, n.body(), w);
		w.write("}");
		
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
		if (c.clocks().isEmpty())
			clocks = null;
		else if (c.clocks().size() == 1)
			clocks = new Template("clock", c.clocks().get(0));
		else {
			Integer id = getUniqueId_();
			clocks = new Template("clocked",
								  new Loop("clocked-loop", c.clocks(), new CircularList(id)),
								  id);
		}
		return clocks;
	}

	public void visit(Async_c a) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		new Template("Async",
					 a.place(),
					 processClocks(a),
					 a.body()).expand(tr2);
	}

	public void visit(Atomic_c a) {
		new Template("Atomic", a.body(), getUniqueId_()).expand();
	}

	public void visit(Here_c a) {
		new Template("here").expand();
	}

	public void visit(Await_c c) {
		new Template("await", c.expr(), getUniqueId_()).expand();
	}

	public void visit(Next_c d) {
		new Template("next").expand();
	}

	public void visit(Future_c f) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		new Template("Future", f.place(), new TypeExpander(f.returnType().type(), true, true, false), f.body(), new RuntimeTypeExpander(f.returnType().type())).expand(tr2);
	}
	
	public void visit(Formal_c f) {
	    if (f.name().id().toString().equals(""))
		f = (Formal_c) f.name(f.name().id(Name.makeFresh("a")));
	    visit((Node) f);
	}

	public void visit(ForLoop_c f) {
		X10Formal form = (X10Formal) f.formal();

		/* TODO: case: for (point p:D) -- discuss with vj */
		/* handled cases: exploded syntax like: for (point p[i,j]:D) and for (point [i,j]:D) */
		if (Configuration.LOOP_OPTIMIZATIONS && form.hasExplodedVars()) {
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
							 new Template("forloop",
									 form.flags(),
									 form.type(),
									 form.name(),
									 regVar,
									 new Join("\n", new Join("\n", f.locals()), f.body())
								 )
						 ).expand();
		} else
			new Template("forloop",
							 form.flags(),
							 form.type(),
							 form.name(),
							 f.domain(),
							 new Join("\n", new Join("\n", f.locals()), f.body())
						 ).expand();
	}

	private void processClockedLoop(String template, X10ClockedLoop l) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		new Template(template,
						 l.formal().flags(),
						 l.formal().type(),
						 l.formal().name(),
						 l.domain(),
						 new Join("\n", new Join("\n", l.locals()), l.body()),
						 processClocks(l),
						 new Join("\n", l.locals())
					 ).expand(tr2);
	}

	public void visit(ForEach_c f) {
		// System.out.println("X10PrettyPrinter.visit(ForEach c): |" + f.formal().flags().translate() + "|");
		processClockedLoop("foreach", f);
	}

	public void visit(AtEach_c f) {
		processClockedLoop("ateach", f);
	}

	public void visit(Now_c n) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		new Template("Now", n.clock(), n.body()).expand(tr2);
	}

	/*
	 * Field access -- this includes FieldAssign (because the left node of
	 * FieldAssign is a Field node!
	 */
	public void visit(Field_c n) {
		Receiver target = n.target();
		Type t = target.type();

		X10TypeSystem xts = (X10TypeSystem) t.typeSystem();

		X10FieldInstance fi = (X10FieldInstance) n.fieldInstance();

		String pat = getJavaImplForDef(fi.x10Def());
		if (pat != null) {
		    Object[] components = new Object[] { n.target() };
		    dumpRegex("Native", components, tr, pat);
		    return;
		}

		// access to field x10.lang.Object.location should not be checked
		boolean is_location_access;
		String f_name = n.fieldInstance().name().toString();
		StructType f_container = n.fieldInstance().container();
		is_location_access = f_name != null && "location".equals(f_name) && f_container instanceof ReferenceType;

		if (! (target instanceof TypeNode) &&	// don't annotate access to static vars
			t instanceof ReferenceType &&	// don't annotate access to instances of ordinary Java objects.
			! n.isTargetImplicit() &&
			! (target instanceof Special) &&
			! (t.isClass() && t.toClass().isAnonymous()) && // don't annotate anonymous classes: they're here
			! is_location_access &&
			QueryEngine.INSTANCE().needsHereCheck(n))
		{
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

	private Stmt optionalBreak(Stmt s) {
		NodeFactory nf = tr.nodeFactory();
		if (s.reachable())
			return nf.Break(s.position());
		// [IP] Heh, Empty cannot be unreachable either.  Who knew?
//		return nf.Empty(s.position());
		return null;
	}

	public void visit(When_c w) {
		Integer id = getUniqueId_();
		List breaks = new ArrayList(w.stmts().size());
		for (Iterator i = w.stmts().iterator(); i.hasNext(); ) {
			Stmt s = (Stmt) i.next();
			breaks.add(optionalBreak(s));
		}
		new Template("when",
						 w.expr(),
						 w.stmt(),
						 optionalBreak(w.stmt()),
						 new Loop("when-branch", w.exprs(), w.stmts(), breaks),
						 id
					 ).expand();
	}

	public void visit(Finish_c a) {
		new Template("finish", a.body(), getUniqueId_()).expand();
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
	            Type t = n.leftType();
	            
	            boolean nativeop = false;
	            if (t.isNumeric() || t.isBoolean() || t.isSubtype(ts.String())) {
	                nativeop = true;
	            }
	            
	            if (n.operator() == Assign.ASSIGN) {
	                tr.print(n, array, w);
	                w.write(".set(");
	                new Join(", ", index).expand(tr);
	                if (index.size() > 0)
	                    w.write(", ");
	                tr.print(n, n.right(), w);
	                w.write(")");
	            }
	            else if (! effects) {
	                Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
	                Name methodName = X10Binary_c.binaryMethodName(op);
	                tr.print(n, array, w);
	                w.write(".set(");
	                new Join(", ", index).expand(tr);
	                if (index.size() > 0)
	                    w.write(", ");
	                tr.print(n, array, w);
	                w.write(".apply(");
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
	                w.write(")");
	            }
	            else {
	                // new Object() { T eval(R target, T right) { return (target.f = target.f.add(right)); } }.eval(x, e)
	                Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
	                Name methodName = X10Binary_c.binaryMethodName(op);
	                w.write("new java.lang.Object() { ");
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
	                w.newline();
	                if (! n.type().isVoid()) {
	                    w.write("return ");
	                }
	                w.write("array.set(");
	                {
	                    int i = 0;
	                    for (Expr e : index) {
	                        if (i != 0)
	                            w.write(", ");
	                        w.write("i" + i);
	                        i++;
	                    }
	                }
	                if (index.size() > 0)
	                    w.write(", ");
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

	                w.write(");");
	                w.newline();
	                w.write("} }.eval(");
	                tr.print(n, array, w);
	                if (index.size() > 0)
	                    w.write(", ");
	                new Join(", ", index).expand();
	                w.write(", ");
	                tr.print(n, n.right(), w);
	                w.write(")");
	            }
	        }

//	public void visxit(SettableAssign_c a) {
//	    Expr array = a.array();
//	    List<Expr> index = a.index();
//	    if (index.size() == 1) {
//		String operator = a.operator().toString();
//		Expr theIndex = index.get(0);
//		X10TypeSystem ts = (X10TypeSystem) array.type().typeSystem();
//		Template template;
//		if (ts.isRail(array.type()) || ts.isValRail(array.type())) {
//		    Type baseType = X10ArraysMixin.railBaseType(array.type());
//		    template = new Template("rail_set", array, theIndex, a.right(), operator);		    
//		}
//		else {
//			// Use general template
//		    template = new Template("array_set", array, theIndex, a.right(), a.opString(a.operator()));
//		}
//
//		template.expand();
//		
//		//new Template("array_set",
//		//				 left.array(), left.index(), a.right(),
//		//				 a.opString(a.operator())
//		//			 ).expand();
//	    }
//	    else {
//		String tmpl = QueryEngine.INSTANCE().needsHereCheck(a)
//						  ? "array_set" : "array_set"; //"array_set_noplacecheck";
//		assert index.size() > 1;
//		Template template = new Template(tmpl,
//											 array, new Join(",", index),
//											 a.right(),
//											 a.opString(a.operator())
//										 );
//		TypeNode elt_type = getParameterType((X10Type)a.type());
//		if (elt_type != null)
//			template = new Template("parametric", elt_type, template);
//		template.expand();
//		//new Template("array_set",
//		//				 left.array(), new Join(",", index), a.right(),
//		//				 a.opString(a.operator())
//		//			 ).expand();
//	}
//	}
	
	String getPropertyInit(Type at, int index) {
	    at = X10TypeMixin.baseType(at);
	    if (at instanceof X10ClassType) {
		X10ClassType act = (X10ClassType) at;
		if (index < act.propertyInitializers().size()) {
		    Expr e = act.propertyInitializer(index);
		    if (e instanceof StringLit) {
			StringLit lit = (StringLit) e;
			String s = lit.value();
			return s;
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
	
	boolean printRepType(Type type, boolean printGenerics, boolean box, boolean inSuper) {
	    // If the type has a native representation, use that.
	    if (type instanceof X10ClassType) {
	        X10ClassDef cd = ((X10ClassType) type).x10Def();
	        String pat = getJavaRep(cd);
	        if (pat != null) {
	            List<Type> typeArguments = ((X10ClassType) type).typeArguments();
	            Object[] o = new Object[typeArguments.size()+1];
	            int i = 0;
	            NodeFactory nf = tr.nodeFactory();
	            o[i++] = new TypeExpander(type, printGenerics, box, inSuper);
	            for (Type a : typeArguments) {
	                o[i++] = new TypeExpander(a, printGenerics, true, inSuper);
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
	
	private void printType(Type type, boolean printTypeParams, boolean box, boolean inSuper, boolean ignoreQual) {
	    printType(type,
	              (printTypeParams ? PRINT_TYPE_PARAMS : 0) |
	              (box ? BOX_PRIMITIVES : 0) |
	              (inSuper ? NO_VARIANCE : 0) |
	              (ignoreQual ? NO_QUALIFIER : 0)); 
	}
	
	private void printType(Type type, int flags) {
	    boolean printTypeParams = (flags & PRINT_TYPE_PARAMS) != 0;
	    boolean box = (flags & BOX_PRIMITIVES) != 0;
	    boolean inSuper = (flags & NO_VARIANCE) != 0;
	    boolean ignoreQual = (flags & NO_QUALIFIER) != 0;

		X10TypeSystem xts = (X10TypeSystem) type.typeSystem();
		
		type = X10TypeMixin.baseType(type);

                if (type.isBoolean() || type.isNumeric() || type.isVoid()) {
                    String s = null;

                    if (box && ! type.isVoid()) {
                        s = tr.typeSystem().wrapperTypeString(type);
                    }
                    else {
                        if (type.isBoolean()) {
                            s = "boolean";
                        } else if (type.isByte()) {
                            s = "byte";
                        } else if (type.isShort()) {
                            s = "short";
                        } else if (type.isChar()) {
                            s = "char";
                        } else if (type.isInt()) {
                            s = "int";
                        } else if (type.isLong()) {
                            s = "long";
                        } else if (type.isFloat()) {
                            s = "float";
                        } else if (type.isDouble()) {
                            s = "double";
                        } else if (type.isVoid()) {
                            s = "void";
                        }

                        if (X10TypeSystem_c.SUPPORT_UNSIGNED) {
                            if (xts.isUByte(type)) {
                                s = "byte";
                            } else if (xts.isUShort(type)) {
                                s = "short";
                            } else if (xts.isUInt(type)) {
                                s = "int";
                            } else if (xts.isULong(type)) {
                                s = "long";
                            }
                        }
                    }
                    
                    if (s != null) {
                        w.write(s);
                        return;
                    }
                }

		if (printRepType(type, printTypeParams, box, inSuper))
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
		    w.write(X10_FUN_CLASS_PREFIX);
		    w.write("_" + ct.typeParameters().size());
		    w.write("_" + args.size());
		    if (USE_JAVA_GENERICS && printTypeParams) {
		        w.write("<");
		        String sep = "";
		        for (Type a : args) {
		            w.write(sep);
		            sep = ",";
		            printType(a, (printTypeParams ? PRINT_TYPE_PARAMS : 0) | BOX_PRIMITIVES);
		        }
		        w.write(sep);
		        printType(ret, (printTypeParams ? PRINT_TYPE_PARAMS : 0) | BOX_PRIMITIVES);
		        w.write(">");
		    }
		    return;
		}
		
		if (type instanceof PathType) {
			PathType pt = (PathType) type;
			Type t = pt.baseType();
			w.write("java.lang.Object");
			return;
		}
		
		// Shouldn't get here.
		if (type instanceof MacroType) {
		    MacroType mt = (MacroType) type;
		    printType(mt.definedType(), PRINT_TYPE_PARAMS);
		    return;
		}
		
		// This should be covered by X10ClassType.
		if (xts.isRail(type) || xts.isValRail(type)) {
			Type T = X10TypeMixin.getParameterType((X10Type) type, 0);
			if (T == null) {
			    w.write("java.lang.Object");
			}
			else {
			    printType(T, PRINT_TYPE_PARAMS);
			}
			w.write("[]");
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
                            
                            if (! inSuper) {
                                TypeProperty.Variance v = ct.x10Def().variances().get(i);
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
	    if (sub.type().isSubtype(sup.type())) {
	        w.write("true");
	    }
	    else {
	        w.write("false");
	    }
	}
	
	public void visit(X10Binary_c n) {
		Expr left = n.left();
		X10Type l = (X10Type) left.type();
		Expr right = n.right();
		X10Type r = (X10Type) right.type();
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		NodeFactory nf = tr.nodeFactory();
		Binary.Operator op = n.operator();
		
		if (l.isNumeric() && r.isNumeric()) {
		    visit((Binary_c)n);
		    return;
		}
		
		if (l.isBoolean() && r.isBoolean()) {
		    visit((Binary_c)n);
		    return;
		}
		
		if (l.isSubtype(xts.String()) || r.isSubtype(xts.String())) {
		    visit((Binary_c)n);
		    return;
		}
		
		if (op == Binary.EQ) {
		    new Template("equalsequals", left, right).expand();
		    return;
		}
		
		if (op == Binary.NE) {
		    new Template("notequalsequals", left, right).expand();
		    return;
		}
		
		Name methodName = X10Binary_c.binaryMethodName(op);
		if (methodName != null)
		    generateStaticOrInstanceCall(n.position(), left, methodName, right);
		else
		    throw new InternalCompilerError("No method to implement " + n, n.position());
		return;
		
		
//		Binary.Operator GT = Binary.GT;
//		Binary.Operator LT = Binary.LT;
//		Binary.Operator GE = Binary.GE;
//		Binary.Operator LE = Binary.LE;
//		if ((op == GT || op == LT || op == GE || op == LE) & (xts.isPoint(l) || xts.isPlace(l))) {
//			String name = op == GT ? "gt" : op == LT ? "lt" : op == GE ? "ge" : "le";
//			generateStaticOrInstanceCall(n.position(), left, name, right);
//			return;
//		}
//
//		Binary.Operator COND_OR = Binary.COND_OR;
//		Binary.Operator COND_AND = Binary.COND_AND;
//		if (op == COND_OR && (xts.isDistribution(l) ||
//				xts.isRegion(l) || xts.isPrimitiveTypeArray(l)))
//		{
//			generateStaticOrInstanceCall(n.position(), left, "union", right);
//			return;
//		}
//		if (op == COND_AND && (xts.isRegion(l) || xts.isDistribution(l))) {
//			generateStaticOrInstanceCall(n.position(), left, "intersection", right);
//			return;
//		}
//
//		Binary.Operator BIT_OR = Binary.BIT_OR;
//		Binary.Operator BIT_AND = Binary.BIT_AND;
//		// New for X10.
//		if (op == BIT_OR && (xts.isDistribution(l) ||
//					xts.isDistributedArray(l) || xts.isPlace(l))) {
//			generateStaticOrInstanceCall(n.position(), left, "restriction", right);
//			return;
//		}
//
//		Binary.Operator SUB = Binary.SUB;
//		Binary.Operator ADD = Binary.ADD;
//		Binary.Operator MUL = Binary.MUL;
//		Binary.Operator DIV = Binary.DIV;
//		// Modified for X10.
//		if (op == SUB && (xts.isDistribution(l) || xts.isRegion(l)) && ! xts.isPoint(r)) {
//			generateStaticOrInstanceCall(n.position(), left, "difference", right);
//			return;
//		}
//		if ((op == SUB || op == ADD || op == MUL || op == DIV) && xts.isPrimitiveTypeArray(l)) {
//			String name = op == SUB ? "sub" : op == ADD ? "add" : op == MUL ? "mul" : "div";
//			generateStaticOrInstanceCall(n.position(), left, name, right);
//			return;
//		}
//		if ((op == SUB || op == ADD || op == MUL || op == DIV) && xts.isPoint(l) && !xts.isSubtype(r, xts.String())) {
//			String name = op == SUB ? "sub" : op == ADD ? "add" : op == MUL ? "mul" : "div";
//			generateStaticOrInstanceCall(n.position(), left, name, right);
//			return;
//		}
//		if ((op == SUB || op == ADD || op == MUL || op == DIV) && xts.isPoint(r) && !xts.isSubtype(l, xts.String())) {
//			String name = "inv" + (op == SUB ? "sub" : op == ADD ? "add" : op == MUL ? "mul" : "div");
//			generateStaticOrInstanceCall(n.position(), right, name, left);
//			return;
//		}
//		// WARNING: it's important to delegate to the appropriate visit() here!
//		visit((Binary_c)n);
	}

	/**
	 * @param pos
	 * @param left TODO
	 * @param name
	 * @param right TODO
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
		    ClassDef objectDef = ((ClassType) xts.Object()).def();
		    MethodInstance mi = xts.findMethod(left.type(),
		                                       xts.MethodMatcher(left.type(), name, types), objectDef);
		    tr.print(null, nf.Call(pos, left, nf.Id(pos, name), args).methodInstance(mi).type(mi.returnType()), w);
		    //				printSubExpr(left, true, w, tr);
		    //				w.write(".");
		    //				w.write(name);
		    //				w.write("(");
		    //				printSubExpr(right, false, w, tr);
		    //				w.write(")");
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
	    if (cd.isMember())
	        return cd.name() + "$RTT";
	    else
	        return "RTT";
	}
	
	String rttName(X10ClassDef cd) {
	    if (cd.isTopLevel())
	        return cd.fullName() + "." + rttShortName(cd);
	    if (cd.isLocal())
	        return cd.fullName() + "." + rttShortName(cd);
	    if (cd.isMember()) {
	        X10ClassType container = (X10ClassType) Types.get(cd.container());
	        return rttName(container.x10Def()) + "." + rttShortName(cd);
	    }
	    assert false : "expected class " + cd;
	    return "";
	}

	private final class RuntimeTypeExpander extends Expander {
	    private final Type at;

	    private RuntimeTypeExpander(Type at) {
	        this.at = at;
	    }

	    public void expand(Translator tr) {
	        String s = typeof(at);
	        if (s != null) {
	            w.write(s);
	            return;
	        }
	        
	        if (at instanceof X10ClassType) {
	            X10ClassType ct = (X10ClassType) at;
	            X10ClassDef cd = ct.x10Def();
	            String pat = getJavaRep(cd);
	            if (pat == null) {
	                if (ct.isGloballyAccessible() && ct.typeArguments().size() == 0) {
	                    w.write(rttName(cd));
	                    w.write(".it");
	                }
	                else {
	                    w.write("new ");
	                    w.write(rttName(cd));
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
	    
	    public TypeExpander(Type t, boolean printGenerics, boolean box, boolean inSuper) {
	        this(t, (printGenerics ? PRINT_TYPE_PARAMS: 0) | (box ? BOX_PRIMITIVES : 0) | (inSuper ? NO_VARIANCE : 0));
	    }
	    
	    public String toString() {
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

	/**
	 * Join a given list of arguments with a given delimiter.
	 * Two or three arguments can also be specified separately.
	 * Do not join a circular list.
	 */
	public class Join extends Expander {
		private final String delimiter;
		private final List args;
		public Join(String delimiter, Object a, Object b) {
			this(delimiter, asList(a, b));
		}
		public Join(String delimiter, Object a, Object b, Object c) {
			this(delimiter, asList(a, b, c));
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

