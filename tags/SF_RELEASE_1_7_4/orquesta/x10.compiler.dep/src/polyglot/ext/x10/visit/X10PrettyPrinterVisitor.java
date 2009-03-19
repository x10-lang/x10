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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Binary;
import polyglot.ast.Binary_c;
import polyglot.ast.Call_c;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.Special_c;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.Configuration;
import polyglot.ext.x10.ast.ArrayConstructor_c;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.Atomic_c;
import polyglot.ext.x10.ast.Await_c;
import polyglot.ext.x10.ast.Clocked;
import polyglot.ext.x10.ast.Closure;
import polyglot.ext.x10.ast.Closure_c;
import polyglot.ext.x10.ast.Finish_c;
import polyglot.ext.x10.ast.ForEach_c;
import polyglot.ext.x10.ast.ForLoop_c;
import polyglot.ext.x10.ast.FutureNode_c;
import polyglot.ext.x10.ast.Future_c;
import polyglot.ext.x10.ast.Here_c;
import polyglot.ext.x10.ast.Next_c;
import polyglot.ext.x10.ast.Now_c;
import polyglot.ext.x10.ast.NullableNode_c;
import polyglot.ext.x10.ast.PropertyDecl_c;
import polyglot.ext.x10.ast.RemoteCall_c;
import polyglot.ext.x10.ast.When_c;
import polyglot.ext.x10.ast.X10ArrayAccess1Assign_c;
import polyglot.ext.x10.ast.X10ArrayAccess1Unary_c;
import polyglot.ext.x10.ast.X10ArrayAccess1_c;
import polyglot.ext.x10.ast.X10ArrayAccessAssign_c;
import polyglot.ext.x10.ast.X10ArrayAccessUnary_c;
import polyglot.ext.x10.ast.X10ArrayAccess_c;
import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10ClockedLoop;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10Instanceof_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.query.QueryEngine;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.LocalDef;
import polyglot.types.MethodInstance;
import polyglot.types.NoClassException;
import polyglot.types.NoMemberException;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
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

	private final CodeWriter w;
	private final Translator tr;

	private static int nextId_;
	/* to provide a unique name for local variables introduce in the templates */
	private static Integer getUniqueId_() {
		return new Integer(nextId_++);
	}

	public static String getId() {
		return "__var" + getUniqueId_() + "__";
	}

	public X10PrettyPrinterVisitor(CodeWriter w, Translator tr) {
		this.w = w;
		this.tr = tr;
	}

	public void visit(Node n) {
		n.translate(w, tr);
	}

	public void visit(X10Cast_c c) {
		// [IP] FIXME: process type correctly
		visit((Node)c);
	}
	
	public void visit(X10Instanceof_c c) {
		// [IP] FIXME: process type correctly
		visit((Node) c);
	}

	public void visit(Special_c n) {
		polyglot.types.Context c = tr.context();
		if (((X10Translator) tr).inInnerClass() && n.qualifier() == null) {
			printType(n.type());
			w.write(".");
		}

		visit((Node) n);
	}

	public void visit(RemoteCall_c c) {
		// TODO assert false - that is not implemented yet
		throw new InternalCompilerError("not implemented", c.position());
	}

	public void visit(Call_c c) {
		assert (!(c instanceof RemoteCall_c));
		// add a check that verifies if the target of the call is in place 'here'
		Receiver target = c.target();
		Type t = target.type();
		boolean base = false;

		// access to method x10.lang.Object.getLocation should not be checked
		boolean is_location_access;
		String f_name = c.methodInstance().name();
		ReferenceType f_container = c.methodInstance().container();
		is_location_access = f_name != null && "getLocation".equals(f_name) && f_container instanceof ReferenceType;

		X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		boolean is_check_cast;
		is_check_cast = f_name != null && "checkCast".equals(f_name) && f_container instanceof ReferenceType;
		
		if (! (target instanceof TypeNode) &&	// don't annotate access to static vars
			! (target instanceof Future_c) &&
			t instanceof ReferenceType &&	// don't annotate access to instances of ordinary Java objects.
			! c.isTargetImplicit() &&
			! (target instanceof Special) &&
			! (t.isClass() && t.toClass().isAnonymous()) && // don't annotate anonymous classes: they're here
			! is_location_access &&
			! is_check_cast &&
			QueryEngine.INSTANCE().needsHereCheck(c))
		{
			// don't annotate calls with implicit target, or this and super
			// the template file only emits the target
			new Template("place-check", t.translate(null), target).expand();
			// then emit '.', name of the method and argument list.
			w.write(".");
			w.write(c.name());
			w.write("(");
			w.begin(0);
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
		} else
			// WARNING: it's important to delegate to the appropriate visit() here!
			visit((Node)c);
	}

	/**
	 * Like visit(Closure_c), but does special processing for closures that
	 * perform array initialization, since the array runtime's Operator.Pointwise
	 * interface adds a dummy argument to the various apply(...) methods.
	 */
	protected Object handleArrayInitClosure(Closure_c n) {
	    if (n == null)
	    	return n;
	    X10TypeSystem x10ts = (X10TypeSystem) this.tr.typeSystem();
	    Type parmType = n.returnType().type().isPrimitive() ? n.returnType().type() : x10ts.parameter1();
	    return new Template("array_init_closure", new Object[] {
		    parmType.toString(), new Join("\n", n.formals()), n.body()
	    })/*.expand()*/;
	}

	public void visit(Closure_c n) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		new Template("closure", new Object[] {
		    n.returnType(), new Join("\n", n.formals()), n.body()
	    }).expand(tr2);
	}

	public void visit(Binary_c binary) {
		if ((binary.operator().equals(polyglot.ast.Binary.EQ) ||
			 binary.operator().equals(polyglot.ast.Binary.NE)) &&
			(binary.left().type() instanceof ReferenceType) &&
			(binary.right().type() instanceof ReferenceType)
			/*&&
			(binary.left().type() instanceof ValueType) &&
			(binary.right().type() instanceof ValueType) */)
		{
			new Template(binary.operator().equals(polyglot.ast.Binary.EQ)
							? "equalsequals"
							: "notequalsequals",
						 binary.left(), binary.right()).expand();
		} else {
			visit((Node)binary);
		}
	}

	X10ClassType annotationNamed(TypeSystem ts, Node o, String name) throws SemanticException {
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

	private boolean hasAnnotation(Node dec, String name) {
		try {
			X10TypeSystem ts = (X10TypeSystem) tr.typeSystem();
			if (annotationNamed(ts, dec, name) != null)
				return true;
		} catch (NoClassException e) {
			if (!e.getClassName().equals(name))
				throw new InternalCompilerError("Something went terribly wrong", e);
		} catch (SemanticException e) {
			throw new InternalCompilerError("Something is terribly wrong", e);
		}
		return false;
	}

	public void visit(FieldDecl_c n) {
		if (hasAnnotation(n, "x10.lang.shared")) {
			w.write ("volatile ");
		}
		n.translate(w, tr);
	}

	public void visit(MethodDecl_c dec) {
		TypeSystem ts = tr.typeSystem();
		if (dec.name().equals("main") &&
			dec.flags().isPublic() &&
			dec.flags().isStatic() &&
			dec.returnType().type().isVoid() &&
			(dec.formals().size() == 1) &&
			((Formal)dec.formals().get(0)).type().type().typeEquals(ts.arrayOf(ts.String())))
		{
			new Template("Main", dec.formals().get(0), dec.body()).expand();
		} else
			// WARNING: it's important to delegate to the appropriate visit() here!
			visit((Node)dec);
	}
	
	public void visit(PropertyDecl_c dec) {
		polyglot.types.Context c = tr.context();
		// Don't generate property declarations for fields.
		if (c.currentClass().flags().isInterface()) {
			return;
		}
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
		new Template("Future", f.place(), f.stmt(), f.body()).expand(tr2);
	}

	public void visit(ForLoop_c f) {
		X10Formal form = (X10Formal) f.formal();

		/* TODO: case: for (point p:D) -- discuss with vj */
		/* handled cases: exploded syntax like: for (point p[i,j]:D) and for (point [i,j]:D) */
		if (Configuration.LOOP_OPTIMIZATIONS && form.hasExplodedVars()) {
			String regVar = getId();
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
											 new Object[] {
												 form.flags().translate(),
												 form.type(),
												 form.id().id(),
												 new Join(",", idxs)
											 }),
								body);

			body = new Join("\n",
							new Loop("final-var-assign", new CircularList<String>("int"), idx_vars, idxs),
							body);

			new Template("forloop-mult",
						 new Object[] {
							 f.domain(),
							 regVar,
							 new Loop("forloop-mult-each", idxs, new CircularList<String>(regVar), vals, lims),
							 body,
							 new Template("forloop",
								 new Object[] {
									 form.flags().translate(),
									 form.type(),
									 form.id().id(),
									 regVar,
									 new Join("\n", new Join("\n", f.locals()), f.body())
								 })
						 }).expand();
		} else
			new Template("forloop",
						 new Object[] {
							 form.flags().translate(),
							 form.type(),
							 form.id().id(),
							 f.domain(),
							 new Join("\n", new Join("\n", f.locals()), f.body())
						 }).expand();
	}

	private void processClockedLoop(String template, X10ClockedLoop l) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		new Template(template,
					 new Object[] {
						 l.formal().flags().translate(),
						 l.formal().type(),
						 l.formal().id().id(),
						 l.domain(),
						 new Join("\n", new Join("\n", l.locals()), l.body()),
						 processClocks(l),
						 new Join("\n", l.locals())
					 }).expand(tr2);
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

		// access to field x10.lang.Object.location should not be checked
		boolean is_location_access;
		String f_name = n.fieldInstance().name();
		ReferenceType f_container = n.fieldInstance().container();
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
			new Template("place-check", t.translate(null), target).expand();
			// then emit '.' and name of the field.
			w.write(".");
			w.write(n.name());
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
					 new Object[] {
						 w.expr(),
						 w.stmt(),
						 optionalBreak(w.stmt()),
						 new Loop("when-branch", w.exprs(), w.stmts(), breaks),
						 id
					 }).expand();
	}

	public void visit(Finish_c a) {
		new Template("finish", a.body(), getUniqueId_()).expand();
	}

	private static final String USER_DEFINED = "user-defined";
	private static final HashMap/*<String,String>*/ arrayTypeToRuntimeName = new HashMap();
	static {
		arrayTypeToRuntimeName.put("boolean", "BooleanArray");
		arrayTypeToRuntimeName.put("byte", "ByteArray");
		arrayTypeToRuntimeName.put("char", "CharArray");
		arrayTypeToRuntimeName.put("short", "ShortArray");
		arrayTypeToRuntimeName.put("int", "IntArray");
		arrayTypeToRuntimeName.put("long", "LongArray");
		arrayTypeToRuntimeName.put("float", "FloatArray");
		arrayTypeToRuntimeName.put("double", "DoubleArray");
		arrayTypeToRuntimeName.put(USER_DEFINED, "GenericArray");
	}

	public void visit(ArrayConstructor_c a) {
		Type base_type = a.arrayBaseType().type();
		String kind = null;
		boolean refs_to_values = false;

		if (base_type.isPrimitive()) {
			kind = base_type.toPrimitive().kind().toString();
		}
		else { // this is a User-defined[?] ? array
			kind = USER_DEFINED;
			X10TypeSystem xt = (X10TypeSystem) base_type.typeSystem();
			refs_to_values = (base_type instanceof X10Type &&
								 (xt.isValueType(base_type)));
		}
		String runtimeName = (String) arrayTypeToRuntimeName.get(kind);
		if (runtimeName == null)
			throw new Error("Unknown array type.");
		
		// vj: Code illustrating type-driven dispatch
		X10ParsedClassType type = (X10ParsedClassType) a.type();
		
		if (runtimeName.equals("DoubleArray") 
				&& ((type.isRect() && type.isRankThree() && type.isZeroBased()))) {
			runtimeName += "3d";
		}
		if (runtimeName.equals("DoubleArray") 
				&& (type.isRail() || (type.isRect() && type.isRankOne() && type.isZeroBased()))) {
			runtimeName += "1d";
		}
		
		//Report.report(1, "GOLDEN: X10PrettyPrintVisitor type is " + type + "runtimeName is " + runtimeName);
		// End typs-driven dispatch.s
		Object init = (a.initializer() instanceof Closure) ? handleArrayInitClosure((Closure_c) a.initializer()) : a.initializer();
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		String tmpl = Configuration.ARRAY_OPTIMIZATIONS ?
				"array_specialized_init" : "array_new_init";
		new Template(tmpl,
					 new Object[] {
						 runtimeName,
						 a.distribution(),
						 init != null ? init : "(x10.array.Operator.Pointwise)null",
						 new Boolean(a.isSafe()),
						 new Boolean(!a.isValue()),
						 new Boolean(refs_to_values)
					 }).expand(tr2);
	}

	private TypeNode getParameterType(X10Type at) {
		if (X10TypeMixin.isParametric(at)) {
			NodeFactory nf = tr.nodeFactory();
			return nf.CanonicalTypeNode(Position.COMPILER_GENERATED,
										(Type)at.typeParameters().get(0));
		}
		return null;
	}
	
	private String runtimeClassNameForPrimitiveArray(X10TypeSystem xt, Type base_type) {
		String arrayClass;
		if (xt.isBooleanArray(base_type)) arrayClass = "x10.array.sharedmemory.BooleanArray_c";
		else if (xt.isCharArray(base_type)) arrayClass = "x10.array.sharedmemory.CharArray_c";
		else if (xt.isByteArray(base_type)) arrayClass = "x10.array.sharedmemory.ByteArray_c";
		else if (xt.isShortArray(base_type)) arrayClass = "x10.array.sharedmemory.ShortArray_c";
		else if (xt.isIntArray(base_type)) arrayClass = "x10.array.sharedmemory.IntArray_c";
		else if (xt.isLongArray(base_type)) arrayClass = "x10.array.sharedmemory.LongArray_c";
		else if (xt.isFloatArray(base_type)) arrayClass = "x10.array.sharedmemory.FloatArray_c";
		else if (xt.isDoubleArray(base_type)) arrayClass = "x10.array.sharedmemory.DoubleArray_c";
		else throw new Error("Unknown primitive array type.");
		return arrayClass;
	}

	public void visit(X10ArrayAccess1_c a) {
		Template template;
		if ( QueryEngine.INSTANCE().isRectangularRankOneLowZero(a) && a.index().type().isPrimitive() ) {
			// Array being accesses has isZeroBased = isRankOne = isRect = true, and array index is an int (not a point)
			Type at = a.array().type();
			X10TypeSystem xts = (X10TypeSystem) at.typeSystem();
			if (xts.isNullable(at)) 
				at = ((NullableType) at).base();
			
			if (xts.isPrimitiveTypeArray(at)) {
				// Create template optimized for primitive base type with direct access to arr_ field
				String arrayClass = runtimeClassNameForPrimitiveArray(xts, at);
				template = new Template("array_get_primitive_rect_rank_1_low_0", a.array(), a.index(), arrayClass);
			}
			else if ( xts.isX10Array(at)) {
				// Create template with call to getBackingArray()
				template = new Template("array_get_rect_rank_1_low_0", a.array(), a.index());
			}
			else
				// Some other kind of array e.g., distribution ==> use general template
				template = new Template("array_get", a.array(), a.index());
		}
		else {
			// Use general template
			template = new Template("array_get", a.array(), a.index());
		}

		TypeNode elt_type = getParameterType((X10Type)a.array().type());
		if (elt_type != null)
			template = new Template("parametric", elt_type, template);
		template.expand();
		//new Template("array_get", a.array(), a.index()).expand();
	}

	public void visit(X10ArrayAccess_c a) {
		List index = a.index();
		assert index.size() > 1;
		String tmpl = QueryEngine.INSTANCE().needsHereCheck(a)
						  ? "array_get" : "array_get"; //"array_get_noplacecheck";
		Template template = new Template(tmpl, a.array(), new Join(",", index));
		TypeNode elt_type = getParameterType((X10Type)a.array().type());
		if (elt_type != null)
			template = new Template("parametric", elt_type, template);
		template.expand();
		//new Template("array_get", a.array(), new Join(",", index)).expand();
	}

	public void visit(X10ArrayAccess1Assign_c a) {
		String operator = a.operator().toString();
		X10ArrayAccess1_c left = (X10ArrayAccess1_c)a.left();
		Template template;
		if ( QueryEngine.INSTANCE().isRectangularRankOneLowZero(left) && left.index().type().isPrimitive() ) {
			// Array being accesses has isZeroBased = isRankOne = isRect = true, and array index is an int (not a point)
			Type base_type = left.array().type();
			X10TypeSystem xt = (X10TypeSystem) base_type.typeSystem();
			
			if (xt.isPrimitiveTypeArray(base_type)) {
//				 Create template optimized for primitive base type with direct access to arr_ field
				String arrayClass = runtimeClassNameForPrimitiveArray(xt, base_type);
				template = new Template("array_set_primitive_rect_rank_1_low_0", new Object[] { left.array(), left.index(), a.right(), operator, arrayClass});
			}
			else if ( xt.isX10Array(base_type)) {
				// Create template with call to getBackingArray()
				template = new Template("array_set_rect_rank_1_low_0", new Object[] { left.array(), left.index(), a.right(), operator});
			}
			else 
				// Some other kind of array e.g., distribution ==> use general template
				template = new Template("array_set", new Object[] { left.array(), left.index(), a.right(), a.opString(a.operator())});
		}
		else {
			// Use general template
			template = new Template("array_set", new Object[] { left.array(), left.index(), a.right(), a.opString(a.operator())});
		}

		TypeNode elt_type = getParameterType((X10Type)a.type());
		if (elt_type != null)
			template = new Template("parametric", elt_type, template);
		template.expand();
		
		//new Template("array_set",
		//			 new Object[] {
		//				 left.array(), left.index(), a.right(),
		//				 a.opString(a.operator())
		//			 }).expand();
	}

	public void visit(X10ArrayAccessAssign_c a) {
		String tmpl = QueryEngine.INSTANCE().needsHereCheck(a)
						  ? "array_set" : "array_set"; //"array_set_noplacecheck";
		X10ArrayAccess_c left = (X10ArrayAccess_c) a.left();
		List index = left.index();
		assert index.size() > 1;
		Template template = new Template(tmpl,
										 new Object[] {
											 left.array(), new Join(",", index),
											 a.right(),
											 a.opString(a.operator())
										 });
		TypeNode elt_type = getParameterType((X10Type)a.type());
		if (elt_type != null)
			template = new Template("parametric", elt_type, template);
		template.expand();
		//new Template("array_set",
		//			 new Object[] {
		//				 left.array(), new Join(",", index), a.right(),
		//				 a.opString(a.operator())
		//			 }).expand();
	}

	public void visit(X10ArrayAccess1Unary_c a) {
		if (a.expr() instanceof ArrayAccess) {
			// WARNING: it's important to delegate to the appropriate visit() here!
			visit((Node)a);
			return;
		}
	
		String operator = a.operator().toString();

		X10ArrayAccess1_c expr = (X10ArrayAccess1_c) a.expr();
		Template template;
		if ( QueryEngine.INSTANCE().isRectangularRankOneLowZero(expr) && expr.index().type().isPrimitive() ) {
			// Array being accesses has isZeroBased = isRankOne = isRect = true, and array index is an int (not a point)
			Type base_type = expr.array().type();
			X10TypeSystem xt = (X10TypeSystem) base_type.typeSystem();
			
			if (xt.isPrimitiveTypeArray(base_type)) {
				// Create template optimized for primitive base type with direct access to arr_ field
				String arrayClass = runtimeClassNameForPrimitiveArray(xt, base_type);
				String tmpl = a.operator().isPrefix() ? "array_unary_prefix_primitive_rect_rank_1_low_0" : "array_unary_postfix_primitive_rect_rank_1_low_0" ;
				template = new Template(tmpl, new Object[]{expr.array(), expr.index(), arrayClass, operator});
			}
			else if ( xt.isX10Array(base_type)) {
				// Create template with call to getBackingArray()
				String tmpl = a.operator().isPrefix() ? "array_unary_prefix_rect_rank_1_low_0" : "array_unary_postfix_rect_rank_1_low_0" ;
				template = new Template(tmpl, expr.array(), expr.index(), operator);
			}
			else 
				// Some other kind of array e.g., distribution ==> use general template
				template = new Template("array_unary", expr.array(), expr.index(), a.opString(a.operator()));
		}
		else {
			// Use general template
			template = new Template("array_unary", expr.array(), expr.index(), a.opString(a.operator()));
		}
		
		TypeNode elt_type = getParameterType((X10Type)a.type());
		if (elt_type != null)
			template = new Template("parametric", elt_type, template);
		template.expand();
		//new Template("array_unary", expr.array(), expr.index(),
		//			 a.opString(a.operator())).expand();
		/****
		String tmpl;
		String operator;
		if ( QueryEngine.INSTANCE().isRectangularRankOneLowZero(a) ) {
			Unary.Operator op = a.operator();
		    if ( op == Unary.BIT_NOT ) {
		    }
		    else if ( op == Unary.NEG     ) {
		    }
		    else if ( op == Unary.POST_INC ) {
		    }
		    else if ( op == Unary.POST_DEC ) {
		    }
		    else if ( op == Unary.PRE_INC ) {
		    }
		    else if ( op == Unary.PRE_DEC ) {
		    }
		    else if ( op == Unary.POS     ) {
		    }
		    else if ( op == Unary.NOT     ) {
		    }

			tmpl = "array_set_rect_rank_1_low_0";
			operator = a.operator().toString();
		}
		else {
		  	tmpl = "array_unary" ;
		  	operator = a.opString(a.operator());
		}

		X10ArrayAccess1_c expr = (X10ArrayAccess1_c) a.expr();
		Template template = new Template(tmpl,
										 expr.array(), expr.index(),
										 a.opString(a.operator()));
		TypeNode elt_type = getParameterType((X10Type)a.type());
		if (elt_type != null)
			template = new Template("parametric", elt_type, template);
		template.expand();
		//new Template("array_unary", expr.array(), expr.index(),
		//			 a.opString(a.operator())).expand();
	    ****/
	}

	public void visit(X10ArrayAccessUnary_c a) {
		// [IP] This test is probably superfluous
		if (a.expr() instanceof ArrayAccess) {
			// WARNING: it's important to delegate to the appropriate visit() here!
			visit((Node)a);
			return;
		}
		String tmpl = QueryEngine.INSTANCE().needsHereCheck(a)
						  ? "array_unary" : "array_unary"; //"array_unary_noplacecheck";
		X10ArrayAccess_c expr = (X10ArrayAccess_c) a.expr();
		List index = expr.index();
		assert index.size() > 1;
		Template template = new Template(tmpl,
										 expr.array(), new Join(",", index),
										 a.opString(a.operator()));
		TypeNode elt_type = getParameterType((X10Type)a.type());
		if (elt_type != null)
			new Template("parametric", elt_type, template);
		template.expand();
		//new Template("array_unary", expr.array(), new Join(",", index),
		//			 a.opString(a.operator())).expand();
	}

	private void printType(Type type) {
//		X10Type t = (X10Type) type;
//		X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
//		if (ts.isNullable(t)) {
//			w.write("/"+"*"+"nullable"+"*"+"/");
//			printType(X10Type_c.toNullable(t).base());
//		} else if (t.isArray()) {
//			printType(t.toArray().base());
//			w.write("[]");
//		} else
//			w.write(t.toString());
		type.print(w);
	}

	public void visit(NullableNode_c n) {
//		System.out.println("Pretty-printing nullable type node for "+n);
		Type t = n.type();
		if (t != null)
			printType(t);
		else
			// WARNING: it's important to delegate to the appropriate visit() here!
			visit((Node)n);
	}

	public void visit(FutureNode_c n) {
//		System.out.println("Pretty-printing future type node for "+n);
		Type t = n.type();
		if (t != null)
			printType(t);
		else
			// WARNING: it's important to delegate to the appropriate visit() here!
			visit((Node)n);
	}

	public void visit(CanonicalTypeNode_c n) {
//		System.out.println("Pretty-printing canonical type node for "+n);
		Type t = n.type();
		if (t != null)
			printType(t);
		else
			// WARNING: it's important to delegate to the appropriate visit() here!
			visit((Node)n);
	}

	public void visit(X10Binary_c n) {
		Expr left = n.left();
		X10Type l = (X10Type) left.type();
		Expr right = n.right();
		X10Type r = (X10Type) right.type();
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		NodeFactory nf = tr.nodeFactory();
		Binary.Operator op = n.operator();

		Binary.Operator GT = Binary.GT;
		Binary.Operator LT = Binary.LT;
		Binary.Operator GE = Binary.GE;
		Binary.Operator LE = Binary.LE;
		if ((op == GT || op == LT || op == GE || op == LE) & (xts.isPoint(l) || xts.isPlace(l))) {
			String name = op == GT ? "gt" : op == LT ? "lt" : op == GE ? "ge" : "le";
			generateStaticOrInstanceCall(n.position(), left, name, right);
			return;
		}

		Binary.Operator COND_OR = Binary.COND_OR;
		Binary.Operator COND_AND = Binary.COND_AND;
		if (op == COND_OR && (xts.isDistribution(l) ||
				xts.isRegion(l) || xts.isPrimitiveTypeArray(l)))
		{
			generateStaticOrInstanceCall(n.position(), left, "union", right);
			return;
		}
		if (op == COND_AND && (xts.isRegion(l) || xts.isDistribution(l))) {
			generateStaticOrInstanceCall(n.position(), left, "intersection", right);
			return;
		}

		Binary.Operator BIT_OR = Binary.BIT_OR;
		Binary.Operator BIT_AND = Binary.BIT_AND;
		// New for X10.
		if (op == BIT_OR && (xts.isDistribution(l) ||
					xts.isDistributedArray(l) || xts.isPlace(l))) {
			generateStaticOrInstanceCall(n.position(), left, "restriction", right);
			return;
		}

		Binary.Operator SUB = Binary.SUB;
		Binary.Operator ADD = Binary.ADD;
		Binary.Operator MUL = Binary.MUL;
		Binary.Operator DIV = Binary.DIV;
		// Modified for X10.
		if (op == SUB && (xts.isDistribution(l) || xts.isRegion(l)) && ! xts.isPoint(r)) {
			generateStaticOrInstanceCall(n.position(), left, "difference", right);
			return;
		}
		if ((op == SUB || op == ADD || op == MUL || op == DIV) && xts.isPrimitiveTypeArray(l)) {
			String name = op == SUB ? "sub" : op == ADD ? "add" : op == MUL ? "mul" : "div";
			generateStaticOrInstanceCall(n.position(), left, name, right);
			return;
		}
		if ((op == SUB || op == ADD || op == MUL || op == DIV) && xts.isPoint(l) && !xts.isSubtype(r, xts.String())) {
			String name = op == SUB ? "sub" : op == ADD ? "add" : op == MUL ? "mul" : "div";
			generateStaticOrInstanceCall(n.position(), left, name, right);
			return;
		}
		if ((op == SUB || op == ADD || op == MUL || op == DIV) && xts.isPoint(r) && !xts.isSubtype(l, xts.String())) {
			String name = "inv" + (op == SUB ? "sub" : op == ADD ? "add" : op == MUL ? "mul" : "div");
			generateStaticOrInstanceCall(n.position(), right, name, left);
			return;
		}
		// WARNING: it's important to delegate to the appropriate visit() here!
		visit((Binary_c)n);
	}

	/**
	 * @param pos
	 * @param left TODO
	 * @param name
	 * @param right TODO
	 */
	private void generateStaticOrInstanceCall(Position pos, Expr left, String name, Expr right) {
		X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
		NodeFactory nf = tr.nodeFactory();
		ReferenceType lType = (ReferenceType) left.type();
		Type rType = right.type();
		try {
			try {
				MethodInstance mi = xts.findMethod(lType, name,
						Collections.singletonList(rType), xts.Object().def());
				tr.print(null, nf.Call(pos, left, nf.Id(pos, name), right).methodInstance(mi).type(mi.returnType()), w);
//				printSubExpr(left, true, w, tr);
//				w.write(".");
//				w.write(name);
//				w.write("(");
//				printSubExpr(right, false, w, tr);
//				w.write(")");
			} catch (NoMemberException e) {
				MethodInstance mi = xts.findMethod(xts.ArrayOperations(), name,
						Arrays.asList(new Type[] {lType, rType}), xts.Object().def());
				tr.print(null, nf.Call(pos, nf.CanonicalTypeNode(pos, xts.ArrayOperations()),
						nf.Id(pos, name), left, right).methodInstance(mi), w);
//				w.write("x10.lang.ArrayOperations.");
//				w.write(name);
//				w.write("(");
//				printSubExpr(left, false, w, tr);
//				w.write(", ");
//				printSubExpr(right, false, w, tr);
//				w.write(")");
				return;
			}
		} catch (SemanticException e) {
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
		public Template(String id) {
			this(id, new Object[] { });
		}
		public Template(String id, Object arg) {
			this(id, new Object[] { arg });
		}
		public Template(String id, Object arg1, Object arg2) {
			this(id, new Object[] { arg1, arg2 });
		}
		public Template(String id, Object arg1, Object arg2, Object arg3) {
			this(id, new Object[] { arg1, arg2, arg3 });
		}
		public Template(String id, Object[] args) {
			this.id = id;
			//this.template = translate(id);
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
			throw new Error("No translation for " + id + " found!", io);
		}
	}
} // end of X10PrettyPrinterVisitor

