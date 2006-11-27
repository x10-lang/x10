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

import org.ovmj.util.Runabout;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.Binary_c;
import polyglot.ext.jl.ast.Call_c;
import polyglot.ext.jl.ast.Field_c;
import polyglot.ext.jl.ast.MethodDecl_c;
import polyglot.ext.x10.Configuration;
import polyglot.ext.x10.ast.ArrayConstructor_c;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.Atomic_c;
import polyglot.ext.x10.ast.Await_c;
import polyglot.ext.x10.ast.Clocked;
import polyglot.ext.x10.ast.Finish_c;
import polyglot.ext.x10.ast.ForEach_c;
import polyglot.ext.x10.ast.ForLoop_c;
import polyglot.ext.x10.ast.Future_c;
import polyglot.ext.x10.ast.Here_c;
import polyglot.ext.x10.ast.Next_c;
import polyglot.ext.x10.ast.Now_c;
import polyglot.ext.x10.ast.RemoteCall_c;
import polyglot.ext.x10.ast.When_c;
import polyglot.ext.x10.ast.X10ArrayAccess1Assign_c;
import polyglot.ext.x10.ast.X10ArrayAccess1Unary_c;
import polyglot.ext.x10.ast.X10ArrayAccess1_c;
import polyglot.ext.x10.ast.X10ArrayAccessAssign_c;
import polyglot.ext.x10.ast.X10ArrayAccessUnary_c;
import polyglot.ext.x10.ast.X10ArrayAccess_c;
import polyglot.ext.x10.ast.X10CastInfo;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10ClockedLoop;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10Instanceof_c;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.ast.X10Cast_c.X10CastHelper;
import polyglot.ext.x10.query.QueryEngine;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10ReferenceType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.LocalInstance;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;


/**
 * Visitor on the AST nodes that for some X10 nodes triggers the template
 * based dumping mechanism (and for all others just defaults to the normal
 * pretty printing).
 *
 * @author Christian Grothoff
 * @author Igor Peshansky (template classes)
 * @author Rajkishore Barik 26th Aug 2006 (added loop optimizations)
 */
public class X10PrettyPrinterVisitor extends Runabout {

	private final CodeWriter w;
	private final PrettyPrinter pp;

	private static int nextId_;
	/* to provide a unique name for local variables introduce in the templates */
	private static Integer getUniqueId_() {
		return new Integer(nextId_++);
	}

	public static String getId() {
		return "__var" + getUniqueId_() + "__";
	}

	public X10PrettyPrinterVisitor(CodeWriter w, PrettyPrinter pp) {
		this.w = w;
		this.pp = pp;
	}

	public void visit(Node n) {
		if (n.comment() != null)
			w.write(n.comment());
		n.prettyPrint(w, pp);
	}

	public void visit(X10Cast_c c) {
		if (c.isDynamicCheckNeeded()) {
			String castBaseType = ((X10Type) c.type()).baseType().toString();
			Expr exprToCast = c.expr();
	
			if (X10CastHelper.isSideEffectFree(exprToCast)) {
				this.applyInlineTemplate("cast-inlined", c, castBaseType, exprToCast, ((X10Type) c.type()));			
			} else {
				if (c.isPrimitiveCast())
					new Template("cast-primitive",
							 new Object[] { castBaseType, 
											X10CastHelper.getRuntimeConstraintTab(((X10Type) c.type())),
											exprToCast}).expand();
				else {
					new Template("cast-reference",
							 new Object[] { castBaseType, 
											X10CastHelper.getRuntimeConstraintTab(((X10Type) c.type())), 
											c.notNullRequired(),c.isToTypeNullable(), exprToCast}).expand();
				}
			}
		} else {
			visit((Node)c);
		}
	}

	public void visit(X10Instanceof_c c) {
		if (c.isDynamicCheckNeeded()) {
			String castBaseType = (((X10Type)c.compareType().type()).baseType()).toString();
			Expr exprToTest = c.expr();

			if (X10CastHelper.isSideEffectFree(exprToTest)) {
				this.applyInlineTemplate("instanceof-inlined", c, castBaseType, exprToTest, ((X10Type)c.compareType().type()));
			}
			else {
					new Template("instanceof-reference",
							 new Object[] {castBaseType, 
											X10CastHelper.getRuntimeConstraintTab(((X10Type) c.compareType().type())), 
											c.notNullRequired(), c.isToTypeNullable(), c.expr()}).expand();
			}
		} else {
			visit((Node) c);
		}
	}
	
	private void applyInlineTemplate(String templateToInvoke, X10CastInfo c, 
			String castBaseType, Expr exprToCast, X10Type toType) {

			List [] conditionsList = X10CastHelper.getRuntimeConstraintInlined(toType, 
					exprToCast, c.isPrimitiveCast());
			Object conditionParam = null;
			if (conditionsList.length != 0) {
				conditionParam = new Loop("cast-util-runtime-constraint-inlined", conditionsList);
			}

			// it is not possible to have a these property true simultaneously
			// First means toType is non nullable and from type yes
			// Second means toType is nullable
			assert !(c.notNullRequired() && c.isToTypeNullable());

			Object nullableCheck = null;			
			if (c.notNullRequired() ) {
				nullableCheck = new Template("nullable-check",exprToCast, "");
			}
			if (c.isToTypeNullable()) {
				nullableCheck = new Template("nullable-check",exprToCast, "!"); 
			}

			Object instanceofCheck = null; 
				if (!c.isPrimitiveCast()) {
					if (c.notNullRequired()) {
						instanceofCheck = new Join("&&" , "", new Template("instanceof-java", castBaseType, exprToCast));
					} else if (c.isToTypeNullable()) {
						instanceofCheck = new Join("||" , "", new Template("instanceof-java", castBaseType, exprToCast));
					} else {
						instanceofCheck = new Template("instanceof-java", castBaseType, exprToCast);
					}
				}

			Object conditionCheck = 
				((conditionParam != null) ? 
					((!c.isPrimitiveCast() || c.notNullRequired() || c.isToTypeNullable()) ? 
							new Join("&&" , "", conditionParam) 
								: conditionParam) 
									: null);

			// call inlined cast template
			new Template(templateToInvoke, new Object[] {castBaseType,
							exprToCast,
							nullableCheck,
							instanceofCheck,
							conditionCheck}).expand();
	}
	
	public void visit(Call_c c) {
		if (c instanceof RemoteCall_c) {
			// TODO assert false - that is not implemented yet
			throw new RuntimeException("not implemented");
		}
		// add a check that verifies if the target of the call is in place 'here'
		Receiver target = c.target();
		Type t = target.type();
		boolean base = false;

		// access to method x10.lang.Object.getLocation should not be checked
		boolean is_location_access;
		String f_name = c.methodInstance().name();
		ReferenceType f_container = c.methodInstance().container();
		is_location_access = f_name != null && "getLocation".equals(f_name) && f_container instanceof X10ReferenceType;

		if (! (target instanceof TypeNode) &&	// don't annotate access to static vars
			! (target instanceof Future_c) &&
			t instanceof X10ReferenceType &&	// don't annotate access to instances of ordinary Java objects.
			! c.isTargetImplicit() &&
			! (target instanceof Special) &&
			! is_location_access &&
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
				c.print(e, w, pp);
				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(0, " ");
				}
			}
			w.end();
			w.write(")");
		} else
			c.prettyPrint(w, pp);
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

	public void visit(MethodDecl_c dec) {
		if (dec.comment() != null)
			w.write(dec.comment());
		if (dec.name().equals("main") &&
			dec.flags().isPublic() &&
			dec.flags().isStatic() &&
			dec.returnType().type().isVoid() &&
			(dec.formals().size() == 1) &&
			((Formal)dec.formals().get(0)).type().toString().equals("java.lang.String[]"))
		{
			new Template("Main", dec.formals().get(0), dec.body()).expand();
		} else
			dec.prettyPrint(w, pp);
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
		new Template("Async",
					 a.place(),
					 processClocks(a),
					 a.body()).expand();
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
		new Template("Future", f.place(), f.stmt(), f.body()).expand();
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
			LocalInstance[] lis = form.localInstances();
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
												 form.name(),
												 new Join(",", idxs)
											 }),
								body);

			body = new Join("\n",
							new Loop("final-var-assign", new CircularList("int"), idx_vars, idxs),
							body);

			new Template("forloop-mult",
						 new Object[] {
							 f.domain(),
							 regVar,
							 new Loop("forloop-mult-each", idxs, new CircularList(regVar), vals, lims),
							 body,
							 new Template("forloop",
								 new Object[] {
									 form.flags().translate(),
									 form.type(),
									 form.name(),
									 regVar,
									 new Join("\n", new Join("\n", f.locals()), f.body())
								 })
						 }).expand();
		} else
			new Template("forloop",
						 new Object[] {
							 form.flags().translate(),
							 form.type(),
							 form.name(),
							 f.domain(),
							 new Join("\n", new Join("\n", f.locals()), f.body())
						 }).expand();
	}

	private void processClockedLoop(String template, X10ClockedLoop l) {
		new Template(template,
					 new Object[] {
						 l.formal().flags().translate(),
						 l.formal().type(),
						 l.formal().name(),
						 l.domain(),
						 new Join("\n", new Join("\n", l.locals()), l.body()),
						 processClocks(l),
						 new Join("\n", l.locals())
					 }).expand();
	}

	public void visit(ForEach_c f) {
		// System.out.println("X10PrettyPrinter.visit(ForEach c): |" + f.formal().flags().translate() + "|");
		processClockedLoop("foreach", f);
	}

	public void visit(AtEach_c f) {
		processClockedLoop("ateach", f);
	}

	public void visit(Now_c n) {
		new Template("Now", n.clock(), n.body()).expand();
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
		is_location_access = f_name != null && "location".equals(f_name) && f_container instanceof X10ReferenceType;

		if (! (target instanceof TypeNode) &&	// don't annotate access to static vars
			t instanceof X10ReferenceType &&	// don't annotate access to instances of ordinary Java objects.
			! n.isTargetImplicit() &&
			! (target instanceof Special) &&
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
			n.prettyPrint(w, pp);
	}

	private Stmt optionalBreak(Stmt s) {
		X10NodeFactory_c nf = X10NodeFactory_c.getNodeFactory();
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
		
		if (runtimeName.equals("DoubleArray") && type.isRect() &&  type.isRankThree() && type.isZeroBased()) {
			runtimeName += "3d";
		}
		//Report.report(1, "GOLDEN: X10PrettyPrintVisitor type is " + type + "runtimeName is " + runtimeName);
		// End typs-driven dispatch.s
		Object init = a.initializer();
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
					 }).expand();
	}

	private TypeNode getParameterType(X10Type at) {
		if (at.isParametric()) {
			NodeFactory nf = at.typeSystem().extensionInfo().nodeFactory();
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
			a.prettyPrint(w, pp);
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
			a.prettyPrint(w, pp);
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

	/**
	 * Pretty-print a given object.
	 *
	 * @param o object to print
	 */
	private void prettyPrint(Object o) {
		if (o instanceof Expander) {
			((Expander) o).expand();
		} else if (o instanceof Node) {
			((Node) o).del().prettyPrint(w, pp);
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
	private void dump(String id, Object[] components) {
		String regex = translate(id);
		int len = regex.length();
		int pos = 0;
		int start = 0;
		while (pos < len) {
			if (regex.charAt(pos) == '#') {
				w.write(regex.substring(start, pos));
				Integer idx = new Integer(regex.substring(pos+1,pos+2));
				pos++;
				start = pos+1;
				if (idx.intValue() >= components.length)
					throw new InternalCompilerError("Template '"+id+"' uses #"+idx);
				prettyPrint(components[idx.intValue()]);
			}
			pos++;
		}
		w.write(regex.substring(start));
	}

	/**
	 * An abstract class for sub-template expansion.
	 */
	public abstract class Expander { public abstract void expand(); }

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
			dump(id, args);
		}
	}

	/**
	 * A list of one object that has an infinite circular iterator.
	 */
	public static class CircularList extends AbstractList {
		private Object o;
		public CircularList(Object o) { this.o = o; }
		public Iterator iterator() {
			return new Iterator() {
				public boolean hasNext() { return true; }
				public Object next() { return o; }
				public void remove() { return; }
			};
		}
		public Object get(int i) { return o; }
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
		public void expand() {
			w.write("/* Loop: { */");
			Object[] args = new Object[lists.length];
			Iterator[] iters = new Iterator[lists.length];
			// Parallel iterators over all argument lists
			for (int j = 0; j < lists.length; j++)
				iters[j] = lists[j].iterator();
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < args.length; j++)
					args[j] = iters[j].next();
				dump(id, args);
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
		public void expand() {
			w.write("/* Join: { */");
			int N = args.size();
			for (Iterator i = args.iterator(); i.hasNext(); ) {
				prettyPrint(i.next());
				if (i.hasNext())
					prettyPrint(delimiter);
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
			trans = "/* template:"+id+" { */" + trans + "/* } */";
			translationCache_.put(id, trans);
			is.close();
			return trans;
		} catch (IOException io) {
			throw new Error("No translation for " + id + " found!", io);
		}
	}
} // end of X10PrettyPrinterVisitor

