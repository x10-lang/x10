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

import polyglot.ast.Binary;
import polyglot.ast.Binary_c;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Node_c;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.Special_c;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.ext.x10.Configuration;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.Atomic_c;
import polyglot.ext.x10.ast.Await_c;
import polyglot.ext.x10.ast.Clocked;
import polyglot.ext.x10.ast.Closure_c;
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
import polyglot.ext.x10.ast.TypeDecl_c;
import polyglot.ext.x10.ast.TypeParamNode;
import polyglot.ext.x10.ast.TypePropertyNode;
import polyglot.ext.x10.ast.When_c;
import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10Call_c;
import polyglot.ext.x10.ast.X10CanonicalTypeNode;
import polyglot.ext.x10.ast.X10CanonicalTypeNode_c;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10ClassDecl_c;
import polyglot.ext.x10.ast.X10ClockedLoop;
import polyglot.ext.x10.ast.X10ConstructorDecl_c;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10Instanceof_c;
import polyglot.ext.x10.ast.X10MethodDecl_c;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.ast.X10Unary_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.query.QueryEngine;
import polyglot.ext.x10.types.ClosureType;
import polyglot.ext.x10.types.ConstrainedType;
import polyglot.ext.x10.types.MacroType;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.PathType;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.ext.x10.types.X10ArraysMixin;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MemberDef;
import polyglot.types.MethodInstance;
import polyglot.types.NoClassException;
import polyglot.types.QName;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.UniqueID;
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
	
	public void visit(X10MethodDecl_c n) {
//            if (USE_JAVA_GENERICS) {
//                X10MethodDef mi = (X10MethodDef) n.methodDef();
//                    String sep = "<";
//                    for (int i = 0; i < mi.typeParameters().size(); i++) {
//                        w.write(sep);
//                        sep = ", ";
//
//                        printType(mi.typeParameters().get(i)., false, true);
//                    }
//                    if (mi.typeParameters().size() > 0)
//                        w.write(">");
//                }
//            }

	    visit((Node) n);
	}
	
	public void visit(X10ConstructorDecl_c n) {
		w.begin(0);
		
		tr.print(n, n.flags(), w);
		tr.print(n, n.name(), w);
		w.write("(");
		
		w.begin(0);
		
		for (Iterator i = n.typeParameters().iterator(); i.hasNext(); ) {
		    TypeParamNode p = (TypeParamNode) i.next();
		    w.write("x10.runtime.Type ");
		    n.print(p.name(), w, tr);
		    
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
		    n.printSubStmt(n.body(), w, tr);
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
		    n.print(n.superClass(), w, tr);
		}
		
		if (! n.interfaces().isEmpty()) {
		    w.allowBreak(2);
		    if (flags.isInterface()) {
		        w.write("extends ");
		    }
		    else {
		        w.write("implements ");
		    }
		
		    w.begin(0);
		    for (Iterator<TypeNode> i = n.interfaces().iterator(); i.hasNext(); ) {
		        TypeNode tn = (TypeNode) i.next();
		        n.print(tn, w, tr);
		
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
			    w.write("@x10.generics.Synthetic private final x10.runtime.Type ");
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
		            w.write("@x10.generics.Synthetic private final x10.runtime.Type ");
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

	public void visit(X10Cast_c c) {
	        TypeNode tn = c.castType();
	        
	        if (tn instanceof X10CanonicalTypeNode) {
	            X10CanonicalTypeNode xtn = (X10CanonicalTypeNode) tn;
	            Type t = X10TypeMixin.baseType(xtn.type());
	            DepParameterExpr dep = xtn.constraintExpr();
	            if (dep != null) {
	        	new Template("cast_deptype", new Object[] { xtn.typeRef(Types.ref(t)), c.expr(), dep.condition() }).expand();
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
	        	if (t.isPrimitive()) {
	        	    new Template("instanceof_primitive_deptype", new Object[] { xtn.typeRef(Types.ref(t)), c.expr(), dep.condition() }).expand();
	        	}
	        	else {
	        	    new Template("instanceof_deptype", new Object[] { xtn.typeRef(Types.ref(t)), c.expr(), dep.condition() }).expand();
	        	}
	            }
	            else {
	        	visit((Node)c);
	            }
	        }
	        else {
	            visit((Node)c);
	        }
	}

	public void visit(Special_c n) {
		polyglot.types.Context c = tr.context();
		if (((X10Translator) tr).inInnerClass() && n.qualifier() == null && n.kind() != X10Special.SELF) {
			printStaticType(n.type());
			w.write(".");
		}
		else if (n.qualifier() != null) {
		    printStaticType(n.qualifier().type());
		    w.write(".");
		}

		w.write(n.kind().toString());
	}

	public void visit(X10Call_c c) {
		// add a check that verifies if the target of the call is in place 'here'
		Receiver target = c.target();
		Type t = target.type();
		boolean base = false;
		
		X10TypeSystem xts = (X10TypeSystem) t.typeSystem();

		try {
		    X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
		    Type java = (Type) xts.systemResolver().find(QName.make("x10.compiler.Native"));
		    for (Type at : mi.annotationsMatching(java)) {
			String lang = getPropertyInit(at, 0);
			String lit = getPropertyInit(at, 1);
			if (lang != null && lang.equals("java")) {
			    String pat = lit;
			    Object[] components = new Object[c.arguments().size() + 1];
			    int i = 0;
			    components[i++] = c.target();
			    for (Expr e : c.arguments()) {
				components[i++] = e;
			    }
			    dumpRegex("Native", components, tr, pat);
			    return;
			}

		    }
		}
		catch (SemanticException e) {}

		// access to method x10.lang.Object.getLocation should not be checked
		boolean is_location_access;
		String f_name = c.methodInstance().name().toString();
		StructType f_container = c.methodInstance().container();
		is_location_access = f_name != null && "getLocation".equals(f_name) && f_container instanceof ReferenceType;

		boolean is_check_cast;
		is_check_cast = f_name != null && "checkCast".equals(f_name) && f_container instanceof ReferenceType;
		
		if (! (target instanceof TypeNode) &&	// don't annotate access to static vars
			! (target instanceof Future_c) &&
			t instanceof StructType &&	// don't annotate access to instances of ordinary Java objects.
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
			w.write(c.name().id().toString());
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
		} else {
		    if (target instanceof TypeNode) {
			if (t instanceof ConstrainedType) {
			    t = X10TypeMixin.baseType(t);
			}
			if (t instanceof X10ClassType) {
			    t.print(w);
			}
			else {
			    printType(t);
			}
			w.write(".");
			w.write(c.name().id().toString());
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

		    }
		    else {
			// WARNING: it's important to delegate to the appropriate visit() here!
			visit((Node)c);
		    }
		}
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
	    Type parmType = n.returnType().type().isPrimitive() ? n.returnType().type() : x10ts.Object(); // XXX: was param1
	    return new Template("array_init_closure", new Object[] {
		    parmType.toString(), new Join("\n", n.formals()), n.body()
	    })/*.expand()*/;
	}

	public void visit(Closure_c n) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		List formalTypes = new ArrayList();
		for (Formal f : n.formals()) {
		    formalTypes.add(f.type());
		}
		new Template("closure", new Object[] {
		    n.returnType(), new Join("\n", n.formals()), n.body(), n.formals().size(), new Join(", ", formalTypes)
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

	public void visit(MethodDecl_c dec) {
		TypeSystem ts = tr.typeSystem();
		if (dec.name().id().toString().equals("main") &&
			dec.flags().flags().isPublic() &&
			dec.flags().flags().isStatic() &&
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
		new Template("Future", f.place(), f.returnType(), f.body()).expand(tr2);
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
											 new Object[] {
												 form.flags(),
												 form.type(),
												 form.name(),
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
									 form.flags(),
									 form.type(),
									 form.name(),
									 regVar,
									 new Join("\n", new Join("\n", f.locals()), f.body())
								 })
						 }).expand();
		} else
			new Template("forloop",
						 new Object[] {
							 form.flags(),
							 form.type(),
							 form.name(),
							 f.domain(),
							 new Join("\n", new Join("\n", f.locals()), f.body())
						 }).expand();
	}

	private void processClockedLoop(String template, X10ClockedLoop l) {
		Translator tr2 = ((X10Translator) tr).inInnerClass(true);
		new Template(template,
					 new Object[] {
						 l.formal().flags(),
						 l.formal().type(),
						 l.formal().name(),
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
			new Template("place-check", t.translate(null), target).expand();
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

	/* FIXME: move this into New
	public void visit(ArrayConstructor_c a) {
		Type base_type = a.arrayBaseType().type();
		String kind = null;
		boolean refs_to_values = false;

		if (base_type.isPrimitive()) {
			kind = base_type.toPrimitive().name();
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
		Type type = a.type();
		
		if (runtimeName.equals("DoubleArray") 
				&& ((X10ArraysMixin.isRect(type) && X10ArraysMixin.isRankThree(type) && X10ArraysMixin.isZeroBased(type)))) {
			runtimeName += "3d";
		}
		if (runtimeName.equals("DoubleArray") 
				&& (X10ArraysMixin.isRail(type) || (X10ArraysMixin.isRect(type) && X10ArraysMixin.isRankOne(type) && X10ArraysMixin.isZeroBased(type)))) {
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
	*/

	private TypeNode getParameterType(Type at) {
	    NodeFactory nf = tr.nodeFactory();
	    Type parameterType = X10TypeMixin.getParameterType(at, 0);
	    if (parameterType != null)
	        return nf.CanonicalTypeNode(Position.COMPILER_GENERATED,
	                                    parameterType);
	    return null;
	}
	
	/*
	public void visit(X10ArrayAccess1_c a) {
		Template template;
		if ( QueryEngine.INSTANCE().isRectangularRankOneLowZero(a) && a.index().type().isPrimitive() ) {
			// Array being accesses has isZeroBased = isRankOne = isRect = true, and array index is an int (not a point)
			Type at = a.array().type();
			X10TypeSystem xts = (X10TypeSystem) at.typeSystem();
			
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
				template = new Template("array_get", a.array(), a.index(), a.type().translate(null));
		}
		else {
			// Use general template
			template = new Template("array_get", a.array(), a.index(), a.type().translate(null));
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
		Template template = new Template(tmpl, a.array(), new Join(",", index), a.type().translate(null));
		TypeNode elt_type = getParameterType((X10Type)a.array().type());
		if (elt_type != null)
			template = new Template("parametric", elt_type, template);
		template.expand();
		//new Template("array_get", a.array(), new Join(",", index)).expand();
	}
*/
	public void visit(SettableAssign_c a) {
	    Expr array = a.array();
	    List<Expr> index = a.index();
	    if (index.size() == 1) {
		String operator = a.operator().toString();
		Expr theIndex = index.get(0);
		X10TypeSystem ts = (X10TypeSystem) array.type().typeSystem();
		Template template;
		if (ts.isRail(array.type()) || ts.isValRail(array.type())) {
		    Type baseType = X10ArraysMixin.railBaseType(array.type());
		    template = new Template("rail_set", new Object[] { array, theIndex, a.right(), operator });		    
		}
		else {
			// Use general template
		    template = new Template("array_set", new Object[] { array, theIndex, a.right(), a.opString(a.operator())});
		}

		template.expand();
		
		//new Template("array_set",
		//			 new Object[] {
		//				 left.array(), left.index(), a.right(),
		//				 a.opString(a.operator())
		//			 }).expand();
	    }
	    else {
		String tmpl = QueryEngine.INSTANCE().needsHereCheck(a)
						  ? "array_set" : "array_set"; //"array_set_noplacecheck";
		assert index.size() > 1;
		Template template = new Template(tmpl,
										 new Object[] {
											 array, new Join(",", index),
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
	}
	/*
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
	    **** /
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
*/
	
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

	boolean printRepType(Type type) {
        // If the type has a native representation, use that.
        if (type instanceof X10ClassType) {
            X10ClassDef cd = ((X10ClassType) type).x10Def();
            String pat = getJavaRep(cd);
            if (pat != null) {
                List<Type> typeArguments = ((X10ClassType) type).typeArguments();
                Object[] o = new Object[typeArguments.size()+1];
                int i = 0;
                NodeFactory nf = tr.nodeFactory();
                o[i++] = nf.CanonicalTypeNode(Position.COMPILER_GENERATED, type);
                for (Type a : typeArguments) {
                    o[i++] = nf.CanonicalTypeNode(Position.COMPILER_GENERATED, a);
                }
                dumpRegex("NativeRep", o, tr, pat);
                return true;
            }
        }
        return false;
	}

	private void printStaticType(Type type) {
	    printType(type, false, false);
	}
	
	private void printType(Type type) {
	    printType(type, true, false);
	}
	
	private void printType(Type type, boolean printGenerics, boolean box) {
		X10TypeSystem xts = (X10TypeSystem) type.typeSystem();
		
		type = X10TypeMixin.baseType(type);

                if (type.isBoolean() || type.isNumeric() || type.isVoid()) {
                    String s = null;

                    if (box) {
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

		if (printRepType(type))
		    return;

		if (type instanceof ParameterType) {
		    w.write(((ParameterType) type).name().toString());
		    return;
		}
		
		if (type instanceof ClosureType) {
		    ClosureType ct = (ClosureType) type;
		    List<Type> args = ct.argumentTypes();
		    Type ret = ct.returnType();
		    w.write("x10.runtime.Fun_0_");
		    w.write("" + args.size());
		    if (printGenerics && USE_JAVA_GENERICS) {
		        w.write("<");
		        String sep = "";
		        for (Type a : args) {
		            w.write(sep);
		            sep = ",";
		            printType(a);
		        }
		        w.write(sep);
		        printType(ret);
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
		    printType(mt.definedType());
		    return;
		}
		
		// This should be covered by X10ClassType.
		if (xts.isRail(type) || xts.isValRail(type)) {
			Type T = X10TypeMixin.getParameterType((X10Type) type, 0);
			if (T == null) {
			    w.write("java.lang.Object");
			}
			else {
			    printType(T);
			}
			w.write("[]");
			return;
		}
		
		// Print the class name
		type.print(w);
		
                if (printGenerics && USE_JAVA_GENERICS) {
                    if (type instanceof X10ClassType) {
                        X10ClassType ct = (X10ClassType) type;
                        String sep = "<";
                        for (int i = 0; i < ct.typeArguments().size(); i++) {
                            w.write(sep);
                            sep = ", ";

                            Type a = ct.typeArguments().get(i);
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
                            printType(a, printGenerics, true);
                        }
                        if (ct.typeArguments().size() > 0)
                            w.write(">");
                    }
                }
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
		generateStaticOrInstanceCall(n.position(), left, methodName);
		return;
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
		
		Name methodName = X10Binary_c.binaryMethodName(op);
		generateStaticOrInstanceCall(n.position(), left, methodName, right);
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
			throw new InternalCompilerError("No translation for " + id + " found!", io);
		}
	}
} // end of X10PrettyPrinterVisitor

