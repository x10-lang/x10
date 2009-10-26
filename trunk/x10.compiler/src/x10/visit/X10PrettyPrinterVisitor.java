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
package x10.visit;

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
import java.util.Map;
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
import polyglot.ast.Id_c;
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
import polyglot.util.StringUtil;
import polyglot.visit.InnerClassRemover;
import polyglot.visit.Translator;
import x10.Configuration;
import x10.ast.Async_c;
import x10.ast.AtEach_c;
import x10.ast.AtExpr_c;
import x10.ast.AtStmt_c;
import x10.ast.Atomic_c;
import x10.ast.Await_c;
import x10.ast.Clocked;
import x10.ast.ClosureCall;
import x10.ast.ClosureCall_c;
import x10.ast.Closure_c;
import x10.ast.Contains_c;
import x10.ast.DepParameterExpr;
import x10.ast.Finish_c;
import x10.ast.ForEach_c;
import x10.ast.ForLoop_c;
import x10.ast.Future_c;
import x10.ast.Here_c;
import x10.ast.Next_c;
import x10.ast.Now_c;
import x10.ast.PropertyDecl;
import x10.ast.PropertyDecl_c;
import x10.ast.SettableAssign_c;
import x10.ast.SubtypeTest_c;
import x10.ast.Tuple_c;
import x10.ast.TypeDecl_c;
import x10.ast.TypeParamNode;
import x10.ast.When_c;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10Cast;
import x10.ast.X10Cast_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ClockedLoop;
import x10.ast.X10ConstructorCall_c;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10Formal;
import x10.ast.X10Instanceof_c;
import x10.ast.X10IntLit_c;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10New_c;
import x10.ast.X10Special;
import x10.ast.X10Unary_c;
import x10.constraint.XAnd_c;
import x10.constraint.XConstraint;
import x10.constraint.XEQV_c;
import x10.constraint.XEquals_c;
import x10.constraint.XField_c;
import x10.constraint.XFormula_c;
import x10.constraint.XLit_c;
import x10.constraint.XLocal_c;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XNot_c;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.emitter.Emitter;
import x10.emitter.Expander;
import x10.emitter.Inline;
import x10.emitter.Join;
import x10.emitter.Loop;
import x10.emitter.RuntimeTypeExpander;
import x10.emitter.Template;
import x10.emitter.TypeExpander;
import x10.extension.X10Ext;
import x10.query.QueryEngine;
import x10.types.FunctionType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context;
import x10.types.X10Def;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10MethodInstance;
import x10.types.X10Type;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.XTypeTranslator.XTypeLit_c;


/**
 * Visitor on the AST nodes that for some X10 nodes triggers the template
 * based dumping mechanism (and for all others just defaults to the normal
 * pretty printing).
 *
 * @author Christian Grothoff
 * @author Igor Peshansky (template classes)
 * @author Rajkishore Barik 26th Aug 2006 (added loop optimizations)
 * @author vj Refactored Emitter out.
 */
public class X10PrettyPrinterVisitor extends X10DelegatingVisitor {
	public static final String X10_RUNTIME_TYPE_CLASS = "x10.types.Type";
	public static final String X10_FUN_CLASS_PREFIX = "x10.core.fun.Fun";
	public static final String X10_RUNTIME_CLASS = "x10.runtime.impl.java.Runtime";

	public static final int PRINT_TYPE_PARAMS = 1;
	public static final int BOX_PRIMITIVES = 2;
	public static final int NO_VARIANCE = 4;
	public static final int NO_QUALIFIER = 8;
	public static final boolean serialize_runtime_constraints = false;


	final public CodeWriter w;
	final public Translator tr;
	final public Emitter er;

	private static int nextId_;
	/* to provide a unique name for local variables introduce in the templates */
	public static Integer getUniqueId_() {
		return new Integer(nextId_++);
	}

	public static Name getId() {
		return Name.make("__var" + getUniqueId_() + "__");
	}

	public X10PrettyPrinterVisitor(CodeWriter w, Translator tr) {
		this.w = w;
		this.tr = tr;
		this.er = new Emitter(w,tr);
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
			er.coerce(n, n.right(), l.type());
		}
		else {
			Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
			Name methodName = X10Binary_c.binaryMethodName(op);
			tr.print(n, l, w);
			w.write(" = ");
			tr.print(n, l, w);
			w.write(".");
			w.write(Emitter.mangleIdentifier(methodName).toString());
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
			w.write(Emitter.mangleIdentifier(n.name().id()).toString());
			w.write(" ");
			w.write(n.operator().toString());
			w.write(" ");
			er.coerce(n, n.right(), n.fieldInstance().type());
		}
		else if (n.target() instanceof TypeNode || n.target() instanceof Local || n.target() instanceof Lit) {
			// target has no side effects--evaluate it more than once
			Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
			Name methodName = X10Binary_c.binaryMethodName(op);
			tr.print(n, n.target(), w);
			w.write(".");
			w.write(Emitter.mangleIdentifier(n.name().id()).toString());
			w.write(" ");
			w.write(" = ");
			tr.print(n, n.target(), w);
			w.write(".");
			w.write(Emitter.mangleIdentifier(n.name().id()).toString());
			w.write(".");
			w.write(Emitter.mangleIdentifier(methodName).toString());
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
			er.printType(n.type(), PRINT_TYPE_PARAMS);
			w.write(" eval(");
			er.printType(n.target().type(), PRINT_TYPE_PARAMS);
			w.write(" target, ");
			er.printType(n.right().type(), PRINT_TYPE_PARAMS);
			w.write(" right) {");
			w.allowBreak(0, " ");
			w.write("return (target.");
			w.write(Emitter.mangleIdentifier(n.name().id()).toString());
			w.write(" = ");
			w.write("target.");
			w.write(Emitter.mangleIdentifier(n.name().id()).toString());
			w.write(".");
			w.write(Emitter.mangleIdentifier(methodName).toString());
			w.write("(right));");
			w.allowBreak(0, " ");
			w.write("} }.eval(");
			tr.print(n, n.target(), w);
			w.write(", ");
			tr.print(n, n.right(), w);
			w.write(")");
		}
	}


	public void visit(X10MethodDecl_c n) {
		X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();

		Flags flags = n.flags().flags();

		if (n.name().id().toString().equals("main") &&
				flags.isPublic() &&
				flags.isStatic() &&
				n.returnType().type().isVoid() &&
				n.formals().size() == 1 &&
				n.formals().get(0).declType().isSubtype(ts.Rail(ts.String()), tr.context()))
		{
			Expander throwsClause = new Inline(er, "");
			if (n.throwTypes().size() > 0) {
				List<Expander> l = new ArrayList<Expander>();
				for (TypeNode tn : n.throwTypes()) {
					l.add(new TypeExpander(er, tn.type(), PRINT_TYPE_PARAMS));
				}
				throwsClause = new Join(er, "", "throws ", new Join(er, ", ", l));
			}

			new Template(er, "Main", n.formals().get(0), n.body(), tr.context().currentClass().name(), throwsClause).expand();
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
		er.generateMethodDecl(n, boxPrimitives);
	}



	public void visit(Id_c n) {
		w.write(Emitter.mangleIdentifier(n.id()).toString());
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

		for (Iterator<ParameterType> i = ct.x10Def().typeParameters().iterator(); i.hasNext(); ) {
			ParameterType p = (ParameterType) i.next();
			w.write("final ");
			w.write(X10_RUNTIME_TYPE_CLASS);
			w.write(" ");
			w.write(Emitter.mangleIdentifier(p.name()).toString());

			typeAssignments.add("this." + p.name() + " = " + p.name() + ";");

			if (i.hasNext() || n.formals().size() > 0) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}

		for (Iterator<Formal> i = n.formals().iterator(); i.hasNext(); ) {
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

			for (Iterator<TypeNode> i = n.throwTypes().iterator(); i.hasNext(); ) {
				TypeNode tn = (TypeNode) i.next();
				er.printType(tn.type(), PRINT_TYPE_PARAMS);

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



	public void visit(X10ClassDecl_c n) {
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		X10Context context = (X10Context) tr.context();

		X10ClassDef def = (X10ClassDef) n.classDef();

		// Do not generate code if the class is represented natively.
		if (er.getJavaRep(def) != null) {
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

		if (n.typeParameters().size() > 0) {
			w.write("<");
			w.begin(0);
			String sep = "";
			for (TypeParamNode tp : n.typeParameters()) {
				w.write(sep);
				n.print(tp, w, tr);
				List<Type> sups = tp.upperBounds();
				if (sups.size() > 0) {
					//TODO: Decide what to do with multiple upper bounds. Not sure how Java will handle this.
					w.write(" extends ");
					er.printType(sups.get(0), PRINT_TYPE_PARAMS | NO_VARIANCE);
				}
				sep = ", ";
			}
			w.end();
			w.write(">");
		}

		if (n.superClass() != null) {
			w.allowBreak(0);
			w.write("extends ");
			er.printType(n.superClass().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES | NO_VARIANCE);
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
				er.printType(tn.type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES | NO_VARIANCE);
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

		er.generateRTTMethods(def);

//		boolean isValueType = xts.isValueType(def.asType(), (X10Context) tr.context());
		if (def.isTopLevel()) {
			er.generateRTType(def);
		}

		// Generate dispatcher methods.
		er.generateDispatchers(def);

		if (n.typeParameters().size() > 0) {
			w.newline(4);
			w.begin(0);
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
			er.generateRTType(def);
		}
	}





	public void visit(X10Cast_c c) {
		TypeNode tn = c.castType();
		assert tn instanceof CanonicalTypeNode;
		Expr expr = c.expr();
		Type type = expr.type();


		switch (c.conversionType()) {
		case CHECKED:
		case PRIMITIVE:
		case SUBTYPE:
			if (tn instanceof X10CanonicalTypeNode) {
				X10CanonicalTypeNode xtn = (X10CanonicalTypeNode) tn;

				Type t = X10TypeMixin.baseType(xtn.type());
				Expander ex = new TypeExpander(er, t, PRINT_TYPE_PARAMS);

				Expander bex = (type.isBoolean() || type.isNumeric() || type.isChar()) 
				? new TypeExpander(er, type, PRINT_TYPE_PARAMS | BOX_PRIMITIVES) 
				: null;
				Expander uex = (type.isBoolean() || type.isNumeric() || type.isChar()) 
				? new TypeExpander(er, type, PRINT_TYPE_PARAMS) 
				: null;
				Expander rt = new RuntimeTypeExpander(er, t);

				String template= t.isBoolean() || t.isNumeric() || t.isChar() ? "primitive_cast_deptype" : "cast_deptype";

				DepParameterExpr dep = xtn.constraintExpr();
				if (dep != null) {
					List<Expr> conds = dep.condition();
					X10Context xct = (X10Context) tr.context();
					boolean inAnonObjectScope = xct.inAnonObjectScope();
					xct.setAnonObjectScope();
					new Template(er, template, ex, expr, rt, new Join(er, " && ", conds)).expand();
					xct.restoreAnonObjectScope(inAnonObjectScope);
				}
				else if (t.isBoolean() || t.isNumeric() || t.isChar() || type.isSubtype(t, tr.context())) {
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
					if (expr instanceof Unary || expr instanceof Lit)
						w.write("(");
					c.printSubExpr(expr, w, tr);
					if (expr instanceof Unary || expr instanceof Lit)
						w.write(")");
						w.write(")");
						w.end();
				}
				else {
					new Template(er, template, ex, expr, rt, "true").expand();
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
					new Template(er, "instanceof_primitive_deptype", xtn.typeRef(Types.ref(t)), c.expr(), new Join(er, " && ", dep.condition())).expand();
				}
				else {
					new Template(er, "instanceof_deptype", xtn.typeRef(Types.ref(t)), c.expr(), new Join(er, " && ", dep.condition())).expand();
				}
				return;
			}
		}

		Type t = tn.type();
		new RuntimeTypeExpander(er, t).expand(tr);
		w.write(".");
		w.write("instanceof$(");
		tr.print(c, c.expr(), w);
		w.write(")");
	}


	public void visit(Special_c n) {
		X10Context c = (X10Context) tr.context();
		/*
		 * The InnerClassRemover will have replaced the
		 */
		if (c.inAnonObjectScope() && n.kind() == Special.THIS
				&& c.inStaticContext()) {
			w.write(InnerClassRemover.OUTER_FIELD_NAME.toString());
			return;
		}
		if ((((X10Translator) tr).inInnerClass() || c.inAnonObjectScope() )
				&& n.qualifier() == null && n.kind() != X10Special.SELF) {
			er.printType(n.type(), 0);
			w.write(".");
		}
		else if (n.qualifier() != null) {
			er.printType(n.qualifier().type(), 0);
			w.write(".");
		}

		w.write(n.kind().toString());
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
			new RuntimeTypeExpander(er, at).expand(tr);
			if (i.hasNext() || c.arguments().size() > 0) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}

		List<Expr> l = c.arguments();
		for (Iterator<Expr> i = l.iterator(); i.hasNext(); ) {
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
			er.printType(n.objectType().type(), PRINT_TYPE_PARAMS | NO_VARIANCE);
		}
		else {
			er.printType(n.objectType().type(), PRINT_TYPE_PARAMS | NO_VARIANCE | NO_QUALIFIER);
		}

		w.write("(");
		w.begin(0);

		X10ConstructorInstance mi = (X10ConstructorInstance) n.constructorInstance();
		X10ClassType ct = (X10ClassType) mi.container();

		for (Iterator<Type> i = ct.typeArguments().iterator(); i.hasNext(); ) {
			final Type at = i.next();
			new RuntimeTypeExpander(er, at).expand(tr);
			if (i.hasNext() || c.arguments().size() > 0) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}

		List<Expr> l = c.arguments();
		for (Iterator<Expr> i = l.iterator(); i.hasNext(); ) {
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
			new RuntimeTypeExpander(er, at).expand(tr);
			if (i.hasNext() || c.arguments().size() > 0) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}

		List<Expr> l = c.arguments();
		for (Iterator<Expr> i = l.iterator(); i.hasNext(); ) {
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
		new Template(er, "tuple", 
				new TypeExpander(er, t, true, true, false),
				new RuntimeTypeExpander(er, t), 
				new TypeExpander(er, t, true, false, false),
				new Join(er, ",", c.arguments())).expand();
	}



	public void visit(X10Call_c c) {
		X10Context context = (X10Context) tr.context();

		Receiver target = c.target();
		Type t = target.type();
		boolean base = false;

		X10TypeSystem xts = (X10TypeSystem) t.typeSystem();

		X10MethodInstance mi = (X10MethodInstance) c.methodInstance();

		String pat = er.getJavaImplForDef(mi.x10Def());
		if (pat != null) {
			boolean needsHereCheck = er.needsHereCheck(target, context);
			Template tmp = null; 
			if (needsHereCheck && ! (target instanceof TypeNode || target instanceof New)) {
				tmp = new Template(er, "place-check", new TypeExpander(er, target.type(), true, false, false), target);
			}
			er.emitNativeAnnotation(pat, null == tmp ? target : tmp, mi.typeParameters(), c.arguments());
			return;
		}

		// Check for properties accessed using method syntax.  They may have @Native annotations too.
		if (X10Flags.toX10Flags(mi.flags()).isProperty() && mi.formalTypes().size() == 0 && mi.typeParameters().size() == 0) {
			X10FieldInstance fi = (X10FieldInstance) mi.container().fieldNamed(mi.name());
			if (fi != null) {
				String pat2 = er.getJavaImplForDef(fi.x10Def());
				if (pat2 != null) {
					Object[] components = new Object[] { target };
					er.dumpRegex("Native", components, tr, pat2);
					return;
				}
			}
		}

		if (target instanceof TypeNode) {
			er.printType(t, 0);
		}
		else {

			// add a check that verifies if the target of the call is in place 'here'
			// This is not needed for:

			boolean needsHereCheck = er.needsHereCheck((Expr) target, context);

			if (! (target instanceof Special || target instanceof New)) {
				w.write("(");

				if (needsHereCheck) {
					// don't annotate calls with implicit target, or this and super
					// the template file only emits the target
					new Template(er, "place-check", new TypeExpander(er, t, true, false, false), target).expand();
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
				new TypeExpander(er, at, PRINT_TYPE_PARAMS | BOX_PRIMITIVES).expand(tr);
				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(0, " ");
				}
			}
			w.write(">");
		}

		w.write(Emitter.mangleIdentifier(c.name().id()).toString());

		w.write("(");
		w.begin(0);

		for (Iterator<Type> i = mi.typeParameters().iterator(); i.hasNext(); ) {
			final Type at = i.next();
			new RuntimeTypeExpander(er, at).expand(tr);
			if (i.hasNext() || c.arguments().size() > 0) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}

		List<Expr> l = c.arguments();
		for (Iterator<Expr> i = l.iterator(); i.hasNext(); ) {
			Expr e = i.next();
			c.print(e, w, tr);
			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		w.end();
		w.write(")");
	}



	public void visit(final Closure_c n) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		tr2 = tr2.context(n.enterScope(tr2.context()));

		List<Expander> formals = new ArrayList<Expander>();
		List<Expander> typeArgs = new ArrayList<Expander>();
		for (final Formal f : n.formals()) {
			TypeExpander ft = new TypeExpander(er, f.type().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
			typeArgs.add(ft); // must box formals
			formals.add(new Expander(er) {
				public void expand(Translator tr) {
					er.printFormal(tr, n, f, true);
				}
			});
		}

		TypeExpander ret = new TypeExpander(er, n.returnType().type(), true, true, false);
		if (!n.returnType().type().isVoid()) {
			typeArgs.add(ret);
			w.write("new x10.core.fun.Fun_0_" + n.formals().size());
		}
		else {		
			w.write("new x10.core.fun.VoidFun_0_" + n.formals().size());
		}

		if (typeArgs.size() > 0) {
			w.write("<");
			new Join(er, ", ", typeArgs).expand(tr2);
			w.write(">");
		}

		w.write("() {");
		w.write("public final ");
		ret.expand(tr2);
		w.write(" apply(");
		new Join(er, ", ", formals).expand(tr2);
		w.write(") { ");
		tr2.print(n, n.body(), w);
		w.write("}");
		w.newline();

		Type t = n.type();
		t = X10TypeMixin.baseType(t);
		if (t instanceof X10ClassType) {
			X10ClassType ct = (X10ClassType) t;
			er.generateRTTMethods(ct.x10Def());
		}

		w.write("}");
	}

	public void visit(FieldDecl_c n) {
		if (er.hasAnnotation(n, QName.make("x10.lang.shared"))) {
			w.write ("volatile ");
		}
		
		// Hack to ensure that X10Flags are not printed out .. javac will
		// not know what to do with them.
		Flags flags = X10Flags.toX10Flags(n.flags().flags());

		visit((Node) n.flags(n.flags().flags(flags)));
	}

	public void visit(PropertyDecl_c dec) {
		//		polyglot.types.Context c = tr.context();
		// Don't generate property declarations for fields.
		//		if (c.currentClass().flags().isInterface()) {
		//			return;
		//		}
		super.visit(dec);
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
		if (Configuration.LOOP_OPTIMIZATIONS && form.hasExplodedVars() && (ts.isSubtype(f.domain().type(), ts.Region(), context) || ts.isSubtype(f.domain().type(), ts.Dist(), context)) && X10TypeMixin.isRect(f.domain().type(), context)) {
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
				vals.add(new Integer(i));
			}

			Object body = f.body();

			if (!form.isUnnamed())
				body = new Join(er, "\n",
						new Template(er, "point-create",
								form.flags(),
								form.type(),
								form.name(),
								new Join(er, ",", idxs)
						),
						body);

			body = new Join(er, "\n",
					new Loop(er, "final-var-assign", new CircularList<String>("int"), idx_vars, idxs),
					body);

			new Template(er, "forloop-mult",
					f.domain(),
					regVar,
					new Loop(er, "forloop-mult-each", idxs, new CircularList<String>(regVar), vals, lims),
					body,
					//							 new Template("forloop",
					//									 form.flags(),
					//									 form.type(),
					//									 form.name(),
					//									 regVar,
					//									 new Join("\n", new Join("\n", f.locals()), f.body()),
					//     			   			   	     new TypeExpander(form.type().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES)
					//								 )
					new Inline(er, "assert false;")
			).expand();
		} else
			new Template(er, "forloop",
					form.flags(),
					form.type(),
					form.name(),
					f.domain(),
					new Join(er, "\n", new Join(er, "\n", f.locals()), f.body()),
					new TypeExpander(er, form.type().type(), PRINT_TYPE_PARAMS | BOX_PRIMITIVES)
			).expand();
	}



	public void visit(ForEach_c f) {
		assert (false);
		// System.out.println("X10PrettyPrinter.visit(ForEach c): |" + f.formal().flags().translate() + "|");
		er.processClockedLoop("foreach", f);
	}

	public void visit(AtEach_c f) {
		assert (false);
		er.processClockedLoop("ateach", f);
	}

	public void visit(Now_c n) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		new Template(er, "Now", n.clock(), n.body()).expand(tr2);
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

		String pat = er.getJavaImplForDef(fi.x10Def());
		if (pat != null) {
			Object[] components = new Object[] { target };
			er.dumpRegex("Native", components, tr, pat);
			return;
		}

		if (target instanceof TypeNode) {
			er.printType(t, 0);
			w.write(".");
			w.write(Emitter.mangleIdentifier(n.name().id()).toString());
		}
		else {

			boolean is_location_access = xts.isReferenceOrInterfaceType(fi.container(), context) && fi.name().equals(Name.make("location"));
			boolean needsHereCheck = er.needsHereCheck((Expr) target, context) && ! is_location_access;

			if (needsHereCheck) {
				// no check required for implicit targets, this and super
				// the template file only emits the target
				new Template(er, "place-check", new TypeExpander(er, t, true, false, false), target).expand();
				// then emit '.' and name of the field.
				w.write(".");
				w.write(Emitter.mangleIdentifier(n.name().id()).toString());
			} else
				// WARNING: it's important to delegate to the appropriate visit() here!
				visit((Node)n);

		}
	}

	public void visit(IntLit_c n) {
		String val;
		if (n.kind() == X10IntLit_c.ULONG) {
			val = Long.toString(n.value()) + "L";
		}
		else if (n.kind() == IntLit_c.LONG) {
			val = Long.toString(n.value()) + "L";
		}
		else if (n.kind() == X10IntLit_c.UINT) {
			if (n.value() >= 0x80000000L)
				val = "0x" + Long.toHexString(n.value() & 0xffffffffL);
			else
				val = Long.toString(n.value() & 0xffffffffL);
		}
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

	public void visit(SettableAssign_c n) {
		SettableAssign_c a = n;
		Expr array = a.array();
		List<Expr> index = a.index();

		boolean effects = er.hasEffects(array);
		for (Expr e : index) {
			if (effects)
				break;
			if (er.hasEffects(e))
				effects = true;
		}

		TypeSystem ts = tr.typeSystem();
		X10Context context = (X10Context) tr.context();
		Type t = n.leftType();

		boolean needsHereCheck = er.needsHereCheck(array, context);
		Template tmp = null; 
		if (needsHereCheck) {
			tmp = new Template(er, "place-check", new TypeExpander(er, array.type(), true, false, false), array);
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
			String pat = er.getJavaImplForDef(mi.x10Def());

			if (pat != null) {
				er.emitNativeAnnotation(pat, null == tmp ? array : tmp, mi.typeParameters(), args);
				return;
			} else {
				// otherwise emit the hardwired code.
				er.arrayPrint(n, array, w, tmp);
				w.write(".set");
				w.write("(");
				tr.print(n, n.right(), w);
				if (index.size() > 0)
					w.write(", ");
				new Join(er, ", ", index).expand(tr);
				w.write(")");
			}
		}
		else if (! effects) {
			Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
			Name methodName = X10Binary_c.binaryMethodName(op);
			er.arrayPrint(n, array, w, tmp);
			w.write(".set");
			w.write("((");
			tr.print(n, array, w);
			w.write(").apply(");
			new Join(er, ", ", index).expand(tr);
			w.write(")");
			if (nativeop) {
				w.write(" ");
				w.write(op.toString());
				tr.print(n, n.right(), w);
			}
			else {
				w.write(".");
				w.write(Emitter.mangleIdentifier(methodName).toString());
				w.write("(");
				tr.print(n, n.right(), w);
				w.write(")");
			}
			if (index.size() > 0)
				w.write(", ");
			new Join(er, ", ", index).expand(tr);
			w.write(")");
		}
		else {
			// new Object() { T eval(R target, T right) { return (target.f = target.f.add(right)); } }.eval(x, e)
			Binary.Operator op = SettableAssign_c.binaryOp(n.operator());
			Name methodName = X10Binary_c.binaryMethodName(op);
			w.write("new java.lang.Object() {");
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
				w.write(Emitter.mangleIdentifier(methodName).toString());
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
			er.arrayPrint(n, array, w, tmp);
			if (index.size() > 0)
				w.write(", ");
			new Join(er, ", ", index).expand();
			w.write(", ");
			tr.print(n, n.right(), w);
			w.write(")");
		}
	}

	public void visit(CanonicalTypeNode_c n) {
		Type t = n.type();
		if (t != null)
			er.printType(t, PRINT_TYPE_PARAMS);
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
				typeArgs = Collections.EMPTY_LIST; // e.typeArgs();
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
				er.printType(t, PRINT_TYPE_PARAMS);
				w.write(" eval(");
				er.printType(target.type(), PRINT_TYPE_PARAMS);
				w.write(" target");
				{int i = 0;
				for (Expr e : args) {
					w.write(", ");
					er.printType(e.type(), PRINT_TYPE_PARAMS);
					w.write(" a" + (i+1));
					i++;
				}}
				w.write(") {");
				w.allowBreak(0, " ");
				er.printType(left.type(), PRINT_TYPE_PARAMS);
				w.write(" old = ");
				String pat = er.getJavaImplForDef(mi.x10Def());
				if (pat != null) {
					Object[] components = new Object[args.size()+1];
					int j = 0;
					components[j++] = "target";
					{int i = 0;
					for (Expr e : args) {
						components[j++] = "a" + (i+1);
						i++;
					}}
					er.dumpRegex("Native", components, tr, pat);
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
				er.printType(left.type(), PRINT_TYPE_PARAMS);
				w.write(" neu = (");
				er.printType(left.type(), PRINT_TYPE_PARAMS);
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
				new Join(er, ", ", args).expand(tr);
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
			er.generateStaticOrInstanceCall(n.position(), left, methodName);
		else
			throw new InternalCompilerError("No method to implement " + n, n.position());
		return;
	}

	public void visit(SubtypeTest_c n) {
		// TODO: generate a real run-time test: if sub and sup are parameters, then this should test the actual parameters.
		TypeNode sub = n.subtype();
		TypeNode sup = n.supertype();

		w.write("((");
		new RuntimeTypeExpander(er, sub.type()).expand(tr);
		w.write(")");
		if (n.equals()) {
			w.write(".equals(");
		}
		else {
			w.write(".isSubtype(");
		}
		new RuntimeTypeExpander(er, sup.type()).expand(tr);
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
			new Template(er, "equalsequals", left, right).expand();
			return;
		}

		if (op == Binary.NE) {
			new Template(er, "notequalsequals", left, right).expand();
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
				er.generateStaticOrInstanceCall(n.position(), right, methodName, left);
				return;
			}
		}
		else {
			Name methodName = X10Binary_c.binaryMethodName(op);
			if (methodName != null) {
				er.generateStaticOrInstanceCall(n.position(), left, methodName, right);
				return;
			}
		}
		throw new InternalCompilerError("No method to implement " + n, n.position());
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

} // end of X10PrettyPrinterVisitor

