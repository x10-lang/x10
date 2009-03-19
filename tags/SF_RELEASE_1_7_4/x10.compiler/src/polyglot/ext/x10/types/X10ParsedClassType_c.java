/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 30, 2004
 */
package polyglot.ext.x10.types;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import polyglot.types.ParsedClassType_c;
import polyglot.ast.BooleanLit;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.GenParameterExpr;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.dom.DomGenerator;
import polyglot.ext.x10.dom.X10Dom;
import polyglot.ext.x10.dom.X10Dom.NodeLens;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Here_c;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.ext.x10.visit.PropagateAnnotationsVisitor;
import polyglot.frontend.MissingDependencyException;
import polyglot.frontend.Source;
import polyglot.frontend.goals.Goal;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.DeserializedClassInitializer;
import polyglot.types.FieldInstance;
import polyglot.types.LazyClassInitializer;
import polyglot.types.MemberInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.reflect.ClassFileLazyClassInitializer;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.TypeInputStream;
import polyglot.visit.NodeVisitor;

/** 6/2006 Modified so that every type is now potentially generic and dependent.
 * @author vj
 */
public class X10ParsedClassType_c extends ParsedClassType_c
implements X10ParsedClassType
{
	
	protected transient DepParameterExpr dep;
	protected boolean value;
	
	/** Build a variant of the root type, with the constraint expression. */
	public X10Type dep(DepParameterExpr dep) {
		X10ParsedClassType_c n = (X10ParsedClassType_c) copy();
		n.dep = dep;
		return n;
	}
	
	/** Get the type's constraint expression. */
	public DepParameterExpr dep() {
		return dep;
	}
	
	public List<Expr> propertyExprs() {
		if (dep == null) return Collections.EMPTY_LIST;
		return dep.args();
	}
	
	public Expr propertyExpr(int i) {
		List<Expr> l = this.propertyExprs();
		if (i < l.size()) {
			return (Expr) l.get(i);
		}
		return null;
	}
	
	public X10ClassType propertyExprs(List<Expr> l) {
		DepParameterExpr dep = this.dep;
		if (dep == null && l.isEmpty()) {
			return this;
		}
		else if (dep == null) {
			Expr e = l.get(0);
			
			TypeSystem ts = e.type().typeSystem();
			X10NodeFactory nf = (X10NodeFactory) ts.extensionInfo().nodeFactory();
			
			Expr true_ = nf.BooleanLit(position(), true);
			true_ = true_.type(ts.Boolean());
			
			dep = nf.DepParameterExpr(position(), l, true_);
			dep = (DepParameterExpr) dep.type(ts.Boolean());
		}
		else {
			dep = dep.args(l);
		}
		return (X10ClassType) this.dep(dep);
	}
	
//	private void writeObject(ObjectOutputStream out) throws IOException {
//		X10NodeFactory nf = (X10NodeFactory) ts.extensionInfo().nodeFactory();
//		out.defaultWriteObject();
//		DomGenerator v = new DomGenerator();
//		X10Dom dom = new X10Dom((X10TypeSystem) ts, nf);
//		dom.gen(v, "AST", dep);
//		out.writeObject(v.get());
//	}
//	
//	private void readObject(ObjectInputStream in) throws IOException,
//	ClassNotFoundException {
//		if (in instanceof TypeInputStream) {
//			ts = ((TypeInputStream) in).getTypeSystem();
//		}
//		X10NodeFactory nf = (X10NodeFactory) ts.extensionInfo().nodeFactory();
//		
//		in.defaultReadObject();
//		org.w3c.dom.Element e = (org.w3c.dom.Element) in.readObject();
//		X10Dom dom = new X10Dom((X10TypeSystem) ts, nf);
//		NodeLens lens = dom.new NodeLens();
//		Node node = dom.get(lens, e, "AST");
//		this.dep = (DepParameterExpr) node;
//	}

	protected List<X10ClassType> classAnnotations;
	
	public List<X10ClassType> classAnnotations() {
		if (! isRootType()) {
			return ((X10ParsedClassType) rootType()).classAnnotations();
		}
		polyglot.frontend.ExtensionInfo extensionInfo = typeSystem().extensionInfo();
		if (! classAnnotationsSet()) {
			X10Scheduler scheduler = (X10Scheduler) extensionInfo.scheduler();
			Goal g = scheduler.TypeObjectAnnotationsPropagated(this);
			if (job() == null) {
				this.classAnnotations = Collections.EMPTY_LIST;
			}
			if (job() != null && job() != scheduler.currentJob()) {
				boolean run = true;
				for (Iterator i = g.prerequisiteGoals(scheduler).iterator(); i.hasNext(); ) {
					Goal subgoal = (Goal) i.next();
					if (! subgoal.hasBeenReached()) {
						run = false;
						break;
					}
				}
				if (run) {
					NodeVisitor v = new PropagateAnnotationsVisitor(job(), typeSystem(), extensionInfo.nodeFactory());
					v = v.begin();
					job().ast(job().ast().visit(v));
				}
			}
			if (! classAnnotationsSet()) {
				throw new MissingDependencyException(g, false);
			}
		}
		return Collections.<X10ClassType>unmodifiableList(classAnnotations);
	}
	public boolean classAnnotationsSet() {
		if (! isRootType())
			return ((X10ParsedClassType) rootType()).classAnnotationsSet();
		return classAnnotations != null;
	}
	public void setClassAnnotations(List<X10ClassType> annotations) {
		assert isRootType();
		assert annotations != null;
//		System.out.println("setting class annotations for " + this + " to " + annotations);
		if (annotations.isEmpty())
			this.classAnnotations = Collections.EMPTY_LIST;
		else
			this.classAnnotations = new ArrayList<X10ClassType>(annotations);
	}
	public X10ClassType classAnnotationNamed(String name) {
		for (Iterator<X10ClassType> i = classAnnotations().iterator(); i.hasNext(); ) {
			X10ClassType ct = i.next();
			if (ct.fullName().equals(name)) {
				return ct;
			}
		}
		return null;
	}
	
	List<X10ClassType> annotations;
	 
	public List<X10ClassType> annotations() {
		if (annotations == null)
			return Collections.EMPTY_LIST;
//		if (! annotationsSet())
//			throw new MissingDependencyException(((X10Scheduler) typeSystem().extensionInfo().scheduler()).TypeObjectAnnotationsPropagated(this), false);
		return Collections.<X10ClassType>unmodifiableList(annotations);
	}
	public boolean annotationsSet() { return true || annotations != null; }
	public void setAnnotations(List<X10ClassType> annotations) {
		if (annotations == null) annotations = Collections.EMPTY_LIST;
		this.annotations = new ArrayList<X10ClassType>(annotations);
	}
	public X10TypeObject annotations(List<X10ClassType> annotations) {
		X10TypeObject n = (X10TypeObject) copy();
		n.setAnnotations(annotations);
		return n;
	}
	public List<X10ClassType> annotationMatching(Type t) {
		List<X10ClassType> l = new ArrayList<X10ClassType>();
		for (Iterator<X10ClassType> i = annotations().iterator(); i.hasNext(); ) {
			X10ClassType ct = i.next();
			if (ct.isSubtype(t)) {
				l.add(ct);
			}
		}
		return l;
	}
	
	/**
	 * @param ts
	 * @param init
	 * @param fromSource
	 */
	public X10ParsedClassType_c(TypeSystem ts,
			LazyClassInitializer init,
			Source fromSource)
	{
		super(ts, init, fromSource);
		
	}
	
	/** An instance of X10ParsedClassType_c obtained by reading a code
	 ** file defining a class C is said to tbe base representation of
	 ** C. X10 supports the creation of variants of the type obtained
	 ** by parsing C; these variants are obtained by instantiating
	 ** type parameters or by supplying dependent clauses. Each
	 ** variant is represented in the type system by an instance of
	 ** X10ClassType_c (with the appropriate fields set). In the
	 ** field baseClass we record the instance of X10ParsedClassType_c
	 ** obtained by reading the code from a file. This instance is
	 ** guaranteed to have a null depClaus and an empty list in the
	 ** typeParameters field.
	 
	 ** Now we can determine if two instances of X10ParsedClassType_c
	 ** correspond to the same base Class by simply == checking their
	 ** baseClass fields.
	 
	 */  
	protected Constraint depClause;
	/**
	 * The realClause is the conjunction of the depClause and the depClause
	 * for the underlying base class. Each instance of this class satisfies
	 * the realClause. When checking that A(:c) is a subtype of B(:d), check
	 * that A is a subtype of B, and that the real clause of A(:c) implies d. 
	 * (The real clause of A(:c), i.e. the depClause for A, will always imply
	 * the depClause for B if A is a subtype of B.)
	 */
	protected Constraint realClause; 
	protected boolean realClauseSet = false;
	protected SemanticException realClauseInvalid= null;
	
	protected Constraint classInvariant;
	
	protected List<Type> typeParameters;
	public void checkRealClause() throws SemanticException {
		if (realClauseInvalid!=null)
			throw realClauseInvalid;
	}
	/** Initially this points to itself. When a copy is made
	 * the value is not touched. So it continues to point to the
	 * object from which the variant was made.
	 */
	protected X10Type rootType = this;
	public X10Type rootType() { return rootType;}
	public boolean isRootType() { return rootType==this;}
	public boolean isParametric() { return typeParameters != null && ! typeParameters.isEmpty();}
	public List<Type> typeParameters() { return typeParameters;}
	public Constraint depClause() { return depClause; }
	public Constraint realClause()  {
		if (! realClauseSet) {
			if (isRootType()) {
				initRealClause();
			} else {
				Constraint result = rootType().realClause();
				result = result==null? new Constraint_c((X10TypeSystem) ts) : result.copy();
				if (depClause != null) {
					try {
						result.addIn(depClause);
					}
					catch (Failure f) {
						result.setInconsistent();
						realClauseInvalid = new SemanticException("The dependent clause is inconsistent with respect to the class invariant and property constraints.", position());
					}
				}
				realClause = result;
				realClauseSet = true;
			}
		}
		//assert realClause != null && realClause.entails(depClause);
		//assert (isRootType() || realClause != rootType.realClause());
		return realClause;
	}
	public C_Var selfVar() {
		return depClause()==null ? null : depClause().selfVar();
	}
	public void setSelfVar(C_Var v) {
		Constraint c = depClause();
		if (c==null) {
			depClause=new Constraint_c((X10TypeSystem) ts);
		}
		depClause.setSelfVar(v);
	}
	private void ensureClauses() {
		Constraint rc = realClause(); // forces it to be initialized.
		if (rc == null) {
			realClause = new Constraint_c((X10TypeSystem) ts);
		}
		if (depClause == null) 
			depClause = new Constraint_c((X10TypeSystem) ts);
	}
	/** This check needs to be generalized.
	 * We need to signal when there is a cycle in the following graph.
	 * Nodes: Classes or interfaces
	 * Edge from node A to node B: A has a property of type B, or A descends from B.
	 * The edges may be thought of thus: A has an edge to B if A needs B to be defined (in order for A 
	 * to be defined.)
	 * @return
	 */
	private boolean aPropertyIsRecursive() {
		boolean isRecursive = false;
		for (Iterator<FieldInstance> it = properties.iterator(); (!isRecursive) && it.hasNext();) {
			FieldInstance fi =  it.next();
			X10Type type = ((X10Type) fi.type());
			isRecursive = ((X10TypeSystem) ts).equalsWithoutClause(type, this) || ts.descendsFrom(type, this);
		}
		return isRecursive;
	}
	 /**
     * Set the realClause for this type. The realClause is the conjunction of the
     * depClause and the baseClause for the type -- it represents all the constraints
     * that are satisfied by an instance of this type. The baseClause is the invariant for
     * the base type. If the base type C has defined properties P1 p1, ..., Pk pk, 
     * and inherits from type B, then the baseClause for C is the baseClause for B
     * conjoined with r1[self.p1/self, self/this] && ... && rk[self.pk/self, self/this]
     * where ri is the realClause for Pi.
     * 
     * @return
     */
	private void initRealClause() {
		// Force properties to be initialized.
		assert (isRootType() && ! realClauseSet);
		List<FieldInstance> properties = properties();
		Constraint ci = classInvariant();
		
		HashMap<C_Var, C_Var> result = new HashMap<C_Var, C_Var>();
		if (ci != null)
			result = ci.constraints(result);
		
		// Add in constraints from the supertype.
		Type type = superType();
		if (type instanceof X10Type && type != null) {
			X10Type xType = (X10Type) type;
			Constraint rs = xType.realClause();
			// no need to change self, and no occurrence of this is possible in 
			// a type's base constraint.
			if (rs != null)
				result = rs.constraints(result); 
		}
		
		// Add in constraints from the interfaces.
		for (Iterator i = interfaces().iterator(); i.hasNext(); ) {
			Type it = (Type) i.next();
			if (it instanceof X10Type && it != null) {
				X10Type xType = (X10Type) it;
				Constraint rs = xType.realClause();
				// no need to change self, and no occurrence of this is possible in 
				// a type's base constraint.
				if (rs != null)
					result = rs.constraints(result); 
			} 
		}
		
		C_Var newThis = C_Special.Self;
		boolean aPropertyIsRecursive =  aPropertyIsRecursive();
		
		if (! aPropertyIsRecursive) {
			// add in the bindings from the property declarations.
			int n = properties.size();
			for (int i=0; i < n; i++) {
				FieldInstance fi = properties.get(i);
				type = fi.type();
				if (type instanceof X10Type) {
					X10Type xType = (X10Type) type;
					Constraint rs = xType.realClause();
					if (rs !=null) {
						C_Var newSelf = new C_Field_c(fi, C_Special.Self);
						result = rs.constraints(result, newSelf, newThis); 
					}
				}
			}
		}
		
		// Now add all these collected bindings to the constraint.
		Constraint realClause = new Constraint_c((X10TypeSystem) ts);
		if (! result.isEmpty()) {
			try {
				realClause = realClause.addBindings(result);
			}
			catch (Failure f) {
				realClause.setInconsistent();
				this.realClauseInvalid = new SemanticException("The class invariant and property constraints are inconsistent.", position());
			}
		}
		if (aPropertyIsRecursive) {
			//aPropertyIsRecursiveReport.report(1, "X10ParsedClassType_c: This type has a recursive property.");
			// Verify that the realclause, as it stands entails the assertions of the 
			// property.
			for (Iterator<FieldInstance> it = properties.iterator();  it.hasNext();) {
				FieldInstance fi =  it.next();
				C_Var var = new C_Field_c(fi, C_Special.Self);
				if (! realClause.entailsType(var)) {
					 this.realClause = realClause;
					 this.realClauseInvalid = 
					 new SemanticException("The real clause," + realClause 
							+ " does not satisfy constraints from the property declaration " 
							+ var.type() + " " + var + ".", position());
				}
			}
		}
		if (depClause !=null) {
			try {
				realClause = realClause.addIn(depClause);
			}
			catch (Failure f) {
				realClause.setInconsistent();
				this.realClauseInvalid = new SemanticException("The dependent clause is inconsistent with respect to the class invariant and property constraints.", position());
			}
		}
		this.realClause = realClause;
		this.realClauseSet = true;
	}
	public void addBinding(C_Var t1, C_Var t2) {
		ensureClauses();
		try {
			depClause = depClause.addBinding(t1, t2);
			realClause = realClause.addBinding(t1,t2);
		}
		catch (Failure f) {
			throw new InternalCompilerError("Cannot bind " + t1 + " to " + t2 + ".", f);
		}
	}
	public boolean isConstrained() { 
		Constraint rc = realClause();
		return rc != null && ! rc.valid();
	}
	public void setDepGen(Constraint d, List<Type> l) {
		depClause = d;
		if (realClauseSet) {
			try {
				realClause = realClause.addIn(d);
			}
			catch (Failure f) {
				realClause.setInconsistent();
				this.realClauseInvalid = new SemanticException("The dependent clause is inconsistent with respect to the class invariant and property constraints.", position());
			}
		}
		typeParameters = l;
	}
	public boolean consistent() {
		return realClause().consistent();
	}
	public X10ParsedClassType makeVariant() {
		return (X10ParsedClassType) makeVariant(new Constraint_c((X10TypeSystem) ts), null);
	}
	public X10ParsedClassType makeVariant(Constraint c) {
		return (X10ParsedClassType) makeVariant(c, null);
	}
	public X10Type makeDepVariant(Constraint d, List<Type> l) {
		if (d == null && (l == null || l.isEmpty())) return this;
		X10ParsedClassType_c n = (X10ParsedClassType_c) copy();
		if (l==null || l.isEmpty()) {
			n.typeParameters = typeParameters;
		} else {
			n.typeParameters = l;
		}
		n.depClause = d;
//		 do not set realClause, but if it is already set, then make sure you add d.
		if (((X10ParsedClassType_c) n.rootType).realClauseSet) {
			n.realClause = n.rootType.realClause().copy();
			try {
				n.realClause = n.realClause.addIn(n.depClause);
			}
			catch (Failure f) {
				n.realClause.setInconsistent();
				n.realClauseInvalid = new SemanticException("The dependent clause is inconsistent with respect to the class invariant and property constraints.", n.position());
				n.realClauseSet = true;
			}
		}
		
//		 hack, forced by decision to explicitly represent dist/region/array type logic.
		n.isDistSet = n.isRankSet = n.isOnePlaceSet = n.isRailSet = n.isSelfSet
		= n.isX10ArraySet = n.isZeroBasedSet = false;
	//	assert (n.isRootType() || n.realClause != n.rootType.realClause());
		return n;
		
	}
	public X10Type makeVariant(Constraint d, List<Type> l) { 
		// Need to pick up the typeparameters from this
		// made, and the realClause from the root type.
		if (d == null && (l == null || l.isEmpty())) return this;
		X10ParsedClassType_c n = (X10ParsedClassType_c) copy();
		n.typeParameters = (l==null || l.isEmpty())? typeParameters : l;
		
		n.depClause = d;
		n.realClause = n.rootType.realClause().copy();
		try {
			n.realClause = n.realClause.addIn(n.depClause);
		}
		catch (Failure f) {
			n.realClause.setInconsistent();
			n.realClauseInvalid = new SemanticException("The dependent clause is inconsistent with respect to the class invariant and property constraints.", n.position());
			n.realClauseSet = true;
		}
	
		// hack, forced by decision to explicitly represent dist/region/array type logic.
		n.isDistSet = n.isRankSet = n.isOnePlaceSet = n.isRailSet = n.isSelfSet
		= n.isX10ArraySet = n.isZeroBasedSet = false;
		return n;
	}
	
	X10Type noClauseVariant=null;
	public X10Type makeNoClauseVariant() {
		if (noClauseVariant != null) return noClauseVariant;
		if (! isRootType()) return noClauseVariant = rootType.makeNoClauseVariant();
		
		X10ParsedClassType_c n = (X10ParsedClassType_c) copy();
		n.typeParameters = null; //typeParameters;
		n.depClause = new Constraint_c((X10TypeSystem) ts);
		n.realClause = new Constraint_c((X10TypeSystem) ts);
		n.realClauseSet = true;
		n.isDistSet = n.isRankSet = n.isOnePlaceSet = n.isRailSet = n.isSelfSet
		= n.isX10ArraySet = n.isZeroBasedSet = false;
		n.dep = null;
		
		return noClauseVariant = n;
	}
	
	public C_Term propVal(String name) {
		return (realClause()==null) ? null : realClause().find(name);
	}
	
	public boolean typeEqualsImpl(Type o) {
		return equalsImpl(o);
		}
	public int hashCode() {
		return 
		(rootType == this ? super.hashCode() : rootType.hashCode() ) 
	//	+ (isConstrained() ? depClause.hashCode() : 0)
		+ (isParametric() ? typeParameters.hashCode() :0);
		
	}

	public boolean isCanonical() {
		boolean result =true;
		if (typeParameters != null) {
			Iterator it = typeParameters.iterator();
			while (it.hasNext() && result) {
				Type t = (Type) it.next();
				result &= t.isCanonical();
			}
		}
		return result;
		
	}    
	private Type translateType(Type t) {
		return t;
	}
	/**
	 * TODO: vj Fix major bug. Cannot make copies of FieldInstances. These are destructively modified in place
	 * to record whether they have constant values or not.
	 * 6/21/06. Hmm. This code should be triggered with what we have in CVS now if we have an array class
	 * with a field of type Parameter1.
	 * @param i
	 * @return
	 */
	private FieldInstance translateTypes(FieldInstance fi) {
		 fi.setType(translateType(fi.type()));
		 X10TypeSystem xts = (X10TypeSystem) ts;
		if (xts.equals(fi.container(), xts.distribution()) && fi.name().equals("UNIQUE")) {
				X10ParsedClassType ud = ((X10ParsedClassType)fi.type()).makeVariant();
				ud.setUniqueDist();
				ud.setRail();
				fi.setType(ud);
		 }
		 fi.setContainer(this);
		 return fi;
	}
	private MethodInstance translateTypes(MethodInstance i) {
		List<Type> formals = i.formalTypes();
		List<Type> nformals = new LinkedList<Type>();
		Iterator it = formals.iterator();
		while (it.hasNext())
			nformals.add(translateType((Type)it.next()));
		i.setFormalTypes(nformals);
		i.setReturnType(translateType(i.returnType()));
		i.setContainer(this);
		return i;
	}
	private ConstructorInstance translateTypes(ConstructorInstance i) {
		List<Type> formals = i.formalTypes();
		List<Type> nformals = new LinkedList<Type>();
		Iterator it = formals.iterator();
		while (it.hasNext())
			nformals.add(translateType((Type)it.next()));
		i.setFormalTypes(nformals);
		i.setContainer(this);
		return i;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.types.ReferenceType#fields()
	 */
	/*public List fields() {
	 List il = super.fields();
	 List lo = new LinkedList();
	 Iterator it = il.iterator();
	 while (it.hasNext()) {
	 FieldInstance ci = (FieldInstance) it.next();
	 lo.add(translateTypes(ci));
	 }
	 return lo;
	 }*/
	
	
	/**
	 * The class's constructors.
	 * A list of <code>ConstructorInstance</code>.
	 * @see polyglot.types.ConstructorInstance
	 */
	/* public List constructors() {
	 List il=super.constructors();
	 Iterator it = il.iterator();
	 while (it.hasNext()) {
	 Report.report(2, "[X10ParsedClassType] "  + it.next());
	 
	 }
	 return il;
	 /*List il = super.constructors();
	  List lo = new LinkedList();
	  Iterator it = il.iterator();
	  while (it.hasNext()) {
	  ConstructorInstance ci = (ConstructorInstance) it.next();
	  lo.add(translateTypes(ci));
	  }
	  return lo;}*/
	
	
	/** Get a field by name, or null. */
	public FieldInstance fieldNamed(String name) {
		FieldInstance fi = super.fieldNamed(name);
		
		if (fi == null)
			return null;
		return translateTypes(fi);
	}
	
	/* (non-Javadoc)
	 * @see polyglot.types.ReferenceType#methods()
	 */
	public List methods() {
		//Report.report(5, "X10ParsedClassTypes_c: methods in | (#" + this + ")|:");
		
		List<MethodInstance> methods = super.methods();
		List<MethodInstance> lo = new LinkedList<MethodInstance>();
		Iterator it = methods.iterator();
		while (it.hasNext()) {
			MethodInstance ci = (MethodInstance) it.next();
			MethodInstance m = translateTypes(ci);
			lo.add(m);
			//Report.report(5, "X10ParsedClassTypes_c: ... |" + m + "|:");
		}
		
		return lo;
	}
	
	/**
	 * The class's member classes.
	 * A list of <code>ClassType</code>.
	 * @see polyglot.types.ClassType
	 */
	public List memberClasses() {
		List<X10ClassType> bl = super.memberClasses();
		List<X10ClassType> bo = new LinkedList<X10ClassType>();
		Iterator it = bl.iterator();
		while (it.hasNext()) {
			X10ClassType ct = 
				(X10ClassType) ((X10ClassType) it.next()).makeVariant(null, typeParameters);
			
			// TODO: Figure out how to set the dependentClause;.
			bo.add(ct);
		}
		return bo;
	}
	
	/**
	 * Returns the member class with the given name, or null.
	 *
	 * Note that I'm not sure that this is what is supposed to
	 * happen in all cases (does a static inner class of a parametric
	 * type inherit the parameters of the outer class? always?
	 * should we check? how to check? what does the parser do?
	 * what about non-static?).  However, this may also just work
	 * perfectly as-is.
	 */
	/*  public ClassType memberClassNamed(String name) {
	 ClassType ct = super.memberClassNamed(name);
	 if (ct == null) return null;
	 X10ClassType result = (X10ClassType) ct.copy();
	 result.setTypeParameters(typeParameters);
	 result.setDepClause(depClause);
	 return result;
	 }*/
	
	/**
	 * This method probably does not currently work correctly if
	 * the we do something like
	 *
	 * class ParametricType<T> extends ParentType<T> {};
	 *
	 * However, that would require some additional support
	 * by the parser, too.
	 *
	 * @see polyglot.types.ReferenceType#superType()
	 */
	public Type superType() {
		
		// FIXME: parent class may use parameters (extends Base<T>)
		// that we need to instantiate (Base<Foo>)
		//  if (toString().startsWith("x10.lang.GenericReferenceArray"))
		//    Report.report(3, "X10ParsedClassType.superType " + this + "(#" + this.hashCode() + ") " + this.getClass() + " is |" + super.superType() + "|");
		return (rootType == this ? mySuperType()
				: ((X10ParsedClassType_c) rootType).superType());
	}
	
	public Type mySuperType() {
		
		init.initSuperclass();
		X10TypeSystem xts = (X10TypeSystem) typeSystem();
		if (xts.equals(superType, xts.Object()) 
				&& ! (xts.equals(this, xts.X10Object()))
				&& ! flags().isInterface()
				&& ! toString().startsWith("java.")
				&& ! toString().equals("x10.compilergenerated.Parameter1")) {
			//Report.report(1, "X10ParsedClass: setting supertype of |" +  this + "| to  x10.lang.Object.");
			superType=xts.X10Object();
		}
		
		return superType;
	}
	
	/**
	 * This method probably does not currently work correctly if
	 * the we do something like
	 *
	 * class ParametricType<T> implements MyInterface<T> {};
	 *
	 * However, that would require some additional support
	 * by the parser, too.
	 *
	 * @see polyglot.types.ReferenceType#interfaces()
	 */
	public List interfaces() {
		// FIXME: interfaces may use parameters (implements List<T>)
		// that we need to instantiate (List<Foo>)
		return super.interfaces();
	}
	
	/**
	 * Simple name of the type object. Anonymous classes do not have names.
	 *
	 * Q: does this have any semantics? Should (must??) we add the
	 * type parameters and dependent expressions here?
	 */
	/*  public String name() {
	 return super.name() + (typeParameters == null || typeParameters.isEmpty() ? "" : "_GENERIC");
	 }*/
	
	// Uncomment the method below for debugging only. The output will confuse the post compiler (javac).
	
	public String toStringUnused() { 
		if (false)
			Report.report(5,"X10ParsedClassType: toString |" + super.toString() + "|(#" 
					+ this.hashCode() + ") baseType = " + ( rootType.toString()) + " dep=" + depClause);
		return  
		((rootType == this) ? super.toString() : ((X10ParsedClassType_c) rootType).toString())
		+ (isParametric() ? "/"+"*T" + typeParameters.toString() + "*"+"/"  : "") 
		
		+ (depClause == null ? "" :  "/"+"*"+"(:" +  depClause.toString() + ")"+"*"+"/");
		//  + "/"+"*"+"(#" + hashCode() + ")"+"*"+"/";
	}

	public void print(CodeWriter w) {
		// [IP] FIXME: make this do something sensible
		super.print(w);
	}

	public String toString() {
		return  
		toStringForDisplay();
	}
	private static String getStackTrace() {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] trace = new Throwable().getStackTrace();
		for (int i=2; i < trace.length; i++)
			sb.append("\t").append(trace[i]).append("\n");
		return sb.toString();
	}
	public String toStringForDisplay(boolean includeClause) { 
		String clause = null;
		if (realClause != null && ! realClause.valid()) {
			clause = realClause.toString();
		}
		
		return  
		((rootType == this) ? super.toString() : ((X10ParsedClassType_c) rootType).toStringForDisplay(false))
		+ (includeClause && isParametric() ?  typeParameters.toString() : "") 
		+ (includeClause && clause!=null? clause : "");
	}
	public String toStringForDisplay() { 
                return toStringForDisplay(true);
	}
	
	
	public boolean equalsImpl(TypeObject toType) {
		if (toType instanceof X10Type) {
			X10Type other = (X10Type) toType;
			X10TypeSystem xts = (X10TypeSystem) ts;
			X10Type tb = this.rootType(), ob = other.rootType();
			boolean result = ((tb==this) ? super.equalsImpl(ob): tb==ob)
				&& xts.equivClause(this, other);
			return result;
		}
		return false;
		
	}
	protected boolean baseEquals(X10Type toType ) {
		return this.rootType() == toType.rootType();
	}
	public boolean equalsWithoutClauseImpl(X10Type other) {
		X10TypeSystem xts = (X10TypeSystem) ts;
		X10Type tb = this.rootType(), ob = other.rootType();
		boolean result = ((tb==this) ? super.equalsImpl(ob): tb.equalsWithoutClauseImpl(ob));
		return result;
		
	}
	public X10ClassType superClassRoot() {
		X10TypeSystem xt = (X10TypeSystem) typeSystem();
		X10ClassType x10LangObj = (X10ClassType) xt.X10Object();
		X10ClassType result = this;
		X10ClassType next = (X10ClassType) result.superType();
		while (next != null   ) {
			boolean value = xt.equals(x10LangObj, result);
			if (value)
				return x10LangObj;
			result=next;
			next=(X10ClassType) result.superType();
		}
		return result;
	}
	
	public boolean isJavaType() {
		TypeSystem ts = typeSystem();
		return ts.equals(superClassRoot(), ts.Object());
	}
	/**
	 * A parsed class is safe iff it explicitly has a flag saying so.
	 */
	public boolean safe() {
		return X10Flags.toX10Flags(flags()).isSafe();
	}
	
	
	public boolean isSubtypeImpl(Type toType ) {
		X10Type other = (X10Type) toType;
		X10TypeSystem xts = (X10TypeSystem) ts;
		X10Type tb = this.rootType(), ob = other.rootType();
		boolean result = (ts.typeEquals(tb,ob) || ts.descendsFrom(tb,ob)) &&
			xts.entailsClause(this, other)
			&& xts.equalTypeParameters(typeParameters(), other.typeParameters());
		return result;
	}
	
	public boolean descendsFromImpl(Type toType ) {
		X10Type other = (X10Type) toType;
		X10TypeSystem xts = (X10TypeSystem) ts;
		X10Type tb = this.rootType(), ob = other.rootType();
		boolean result = (tb==this ? super.descendsFromImpl(ob) : tb.descendsFromImpl(ob)) 
		&& xts.entailsClause(this, other) 
		&& xts.equalTypeParameters(this.typeParameters(),other.typeParameters());
		return result;
	}
	public boolean isImplicitCastValidImpl(Type toType) {
		boolean result = true;
		
			if (toType.isArray()) return false;
			X10Type targetType = (X10Type) toType;
			NullableType realTarget = targetType.toNullable();
			result = ts.isSubtype( this, targetType);
			
			if (result) return result;
			
			if (realTarget != null) {
				result = ts.isSubtype( this, realTarget.base());
			}
			
			return result;
		
	}
	
	/** Returns true iff a cast from this to <code>toType</code> is valid. */
	public boolean isCastValidImpl(Type toType) {
		X10TypeSystem xts = (X10TypeSystem) ts;
		X10Type targetType = (X10Type) toType;
		
		boolean result = (toType.isPrimitive() && ts.isCastValid(xts.X10Object(), this));
		if (result) return result;
		NullableType type = targetType.toNullable();
		if (type !=null) return isCastValidImpl(type.base());
		
		FutureType f = targetType.toFuture();
		if (f !=null) {
			// If we can cast the Future into this type, we can do the reverse
			return targetType.isCastValidImpl(this);
		}
		
		X10Type other = (X10Type) toType;
		result = ((X10ParsedClassType_c) makeNoClauseVariant()).superIsCastValidImpl(other.makeNoClauseVariant());
		if (!result ) return result;
		Constraint r = realClause().copy();
		try {
			r.addIn(other.realClause());
			result = r.consistent();
		}
		catch (Failure e) {
			return false;
		}
		return result;
	}
	boolean superIsCastValidImpl(Type toType) {
		return super.isCastValidImpl(toType);
	}
	boolean propertiesElaborated = false;
	
	public boolean propertiesElaborated() {
		if (! membersAdded()) return false;
		if (! signaturesResolved()) return false;
		if (propertiesElaborated) return true;
		if (properties == null) return false;
		for (Iterator<FieldInstance> i = properties.iterator(); i.hasNext(); ) {
			FieldInstance fi = i.next();
			X10Type t = (X10Type) fi.type();
                        if (t == this) throw new InternalCompilerError("loop on " + t, position());
			if (! t.propertiesElaborated()) 
				return false;
		}
		return true;
	}
	
	//List<FieldInstance> protoProperties = null;
	List<FieldInstance> properties = null;
	
	public List<FieldInstance> properties() {
		//Report.report(1, "X10ParsedClassType_c entering properties() on "  + this);
		if (! isRootType()) return rootType.properties();
		if (properties != null) {
			if (! propertiesElaborated()) {
				if (job() != null) {
					// This class is defined in a source file.
					ExtensionInfo.X10Scheduler scheduler = 
						(ExtensionInfo.X10Scheduler) typeSystem().extensionInfo().scheduler();
					//throw new MissingDependencyException(scheduler.TypeElaborated(job()), false);
				}
				else {
					// Loaded from a raw Java class file.  Properties cannot have dependent types.
					propertiesElaborated = true;
				}
			}
			return properties;
		}
		init.canonicalFields();
		init.initSuperclass();
		FieldInstance fi = fieldNamed(X10FieldInstance.MAGIC_PROPERTY_NAME);
		//Report.report(1, "X10ParsedClassType_c found " + fi+ " for " + this);
	 
		if (fi == null) {
			if ( Report.should_report(Report.types, 2))
				Report.report(2, "Type " + name + " has no properties.");
			properties = Collections.EMPTY_LIST;
			return properties;
		}
		
		String propertyNames = (String) fi.constantValue();
		properties = getPropertiesFromClass(propertyNames);
		//Report.report(1, "X10ParsedClassType_c properties() returns "  + properties);
		return properties;
		
	}
	
	List<FieldInstance> definedProperties = null;
	public List<FieldInstance> definedProperties() {
//		Report.report(1, "X10ParsedClassType_c entering definedProperties() on "  + this);
		if (! isRootType()) return rootType.definedProperties();
		if (definedProperties != null) {
			return definedProperties;
		}
		properties();
		List<FieldInstance> superP = ((X10ParsedClassType) superType()).properties();
		
		definedProperties = new ArrayList<FieldInstance>(properties);
		definedProperties.removeAll(superP);
		//Report.report(1, "X10ParsedClassType_c definedProperties() returns "  + definedProperties);
		return definedProperties;
	}
	protected List<FieldInstance> getPropertiesFromClass(String propertyNames) {
		List<FieldInstance> properties = getDefinedPropertiesFromClass(propertyNames);
		if (superType != null) 
			properties.addAll(((X10Type) superType).properties());
//		for (Iterator i = interfaces().iterator(); i.hasNext(); ) {
//			X10Type t = (X10Type) i.next();
//			properties.addAll(t.properties());
//		}
		if (Report.should_report(Report.types, 2))
			Report.report(2, "Type " + name + " has properties " + properties +".");
		return properties;
	}

	public List<FieldInstance> getDefinedPropertiesFromClass(String propertyNames) {
		List<FieldInstance> properties = new ArrayList<FieldInstance>();
		Scanner s = new Scanner(propertyNames);
		while (s.hasNext()) {
			String propName = s.next();
			FieldInstance prop = noncanonicalFieldNamed(propName);
			if (prop == null) 
				throw new InternalCompilerError("Type " 
						+ name + " has no property named " + propName); 
			properties.add(prop);
		}
		return properties;
	}
	public FieldInstance noncanonicalFieldNamed(String name) {
		init.initFields();
		for (Iterator i = fields.iterator(); i.hasNext(); ) {
			FieldInstance fi = (FieldInstance) i.next();
			if (fi.name().equals(name)) {
				return fi;
			}
		}
		return null;
	}
	public Constraint classInvariant() {
		if (! isRootType()) return ((X10ParsedClassType_c) rootType).classInvariant();
		if (classInvariant != null) {
			return classInvariant;
		}
		init.canonicalFields();
		FieldInstance fi = fieldNamed(X10FieldInstance.MAGIC_CI_PROPERTY_NAME);
		//Report.report(1, "X10ParsedClassType_c found " + fi+ " for " + this);
	 
		if (fi == null) {
			if ( Report.should_report(Report.types, 2))
				Report.report(2, "Type " + name + " has no properties.");
			classInvariant = new Constraint_c((X10TypeSystem) ts);
			return classInvariant;
		}
		NullableType nullableT = (NullableType) fi.type();
		classInvariant = nullableT.base().depClause();
	
		return classInvariant;
	}
	public NullableType toNullable() { return X10Type_c.toNullable(this);}
	public FutureType toFuture() { return X10Type_c.toFuture(this);}
	
	boolean isX10Array;
	boolean isX10ArraySet;
	public boolean isX10Array() {
		if (isX10ArraySet) return isX10Array;
		isX10ArraySet = true;
		
		return isX10Array=((X10TypeSystem) typeSystem()).isX10Array(this);
	}
	
	boolean isRect;
	boolean isRectSet;
	public boolean isRect() {
		if (isRail()) return true;
		if (isRectSet) return isRect;
		isRectSet = true;
		Constraint c = realClause();
		return isRect= c==null ? false : amIProperty("rect");
	}
	public void setRect() {
		setProperty("rect");
		isRect = isRectSet = true;
		if (isRankOne() && isZeroBased() && ! isRail()) setRail();
	}
	
	C_Var onePlace;
	boolean isOnePlaceSet;
	public C_Var onePlace() {
		if (isOnePlaceSet) return onePlace;
		isOnePlaceSet = true;
		Constraint c = realClause();
		return onePlace= c==null ? null : c.find("onePlace");
	}
	public void setOnePlace(C_Var onePlace) {
		isOnePlaceSet=true;
		this.onePlace = onePlace;
		setProperty("onePlace", onePlace);
		//Report.report(1, "X10ParsedClassType " + depClause);
	}
	public boolean hasLocalProperty() {
		C_Term onePlace = onePlace();
		return onePlace instanceof C_Here_c;
	}
	
	boolean isZeroBased;
	boolean isZeroBasedSet;
	public boolean isZeroBased() {
		//Report.report(1, "X10ParsedClassType_c: isZerobased" + isZeroBasedSet + " " + isZeroBased);
		if (isRail()) return true;
		if (isZeroBasedSet) return isZeroBased;
		
		Constraint c = realClause();
		isZeroBased= c==null ? false : amIProperty("zeroBased");
		//Report.report(1, "X10ParsedClassType_c: isZerobased set to " + isZeroBased);
		isZeroBasedSet = true;
		return isZeroBased;
		
	}
	public void setZeroBased() {
		setProperty("zeroBased");
		isZeroBased=isZeroBasedSet = true;
		if (isRect() && isRankOne() && ! isRail()) setRail();
	}
	
	boolean isRail;
	boolean isRailSet;
	public boolean isRail() {
		if (isRailSet) return isRail;
		isRailSet = true;
		Constraint c = realClause();
		return isRail=c == null? false : amIProperty("rail");
	}
	public void setRail() {
		setProperty("rail");
		if (! isRankSet) setRank(((X10TypeSystem) typeSystem()).ONE());
		if (! isZeroBased()) setZeroBased();
		if (! isRect()) setRect();
		isRail = isRailSet = true;
	}
	
	boolean isRankSet;
	C_Var rank;
	public C_Var rank() {
		if (isRail()) return ((X10TypeSystem) typeSystem()).ONE();
		if (isRankSet) return rank;
		
		Constraint c = realClause();
		rank = c==null? null : c.find("rank");
		/*if (c == null) {
			isRankSet = true;
			return rank = null;
		}
		rank = c.find("rank");*/
		if (rank == null && c!= null) {
			// build the synthetic term.
			C_Var var = c.selfVar();
			if (var !=null) {
				FieldInstance fi = definedFieldNamed("rank");
				//Report.report(1, "X10ParsedClassType: rank is " + rank + " var.type is " + var.type());
				rank = new C_Field_c(fi, var);
			}
		}
		//Report.report(1, "X1ParsedClassType rank of " + this + " is " + rank);
		isRankSet = true;
		return rank;
	}
	public void setRank(C_Var rank) {
		assert(rank !=null);
		setProperty("rank", rank);
		isRankSet=true;
		this.rank = rank;
		if (isRankOne() && isZeroBased() && isRect() && ! isRail())
			setRail();
	}
	
	public boolean isRankOne() {
		return isRail() || ((X10TypeSystem) typeSystem()).ONE().equals(rank());
	}
	public boolean isRankTwo() {
		return ((X10TypeSystem) typeSystem()).TWO().equals(rank());
	}
	public boolean isRankThree() {
		return ((X10TypeSystem) typeSystem()).THREE().equals(rank());
	}
	boolean isRegionSet;
	C_Var region;
	public C_Var region() {
		if (isRegionSet) return region;
		
		Constraint c = realClause();
		if (c == null)
			return region = null;
		region = c.find("region");
		if (region == null) {
			// build the synthetic term.
			C_Var var = c.selfVar();
			if (var !=null) {
				FieldInstance fi = definedFieldNamed("region");
				if (fi != null)
					region = new C_Field_c(fi, var);
			}
		}
		isRegionSet = true;
		//Report.report(1, "X1ParsedClassType region is " + region);
		C_Var result = region;
		return result;
	}
	public void setRegion(C_Var region) {
		setProperty("region", region);
		isRegionSet=true;
		this.region = region;
	}
	
	boolean isDistSet;
	C_Var dist;
	public C_Var distribution() {
		if (isDistSet) return dist;
		
		Constraint c = realClause();
		if (c == null)
			return dist = null;
		dist = c.find("distribution");
		if (dist == null) {
			// build the synthetic term.
			C_Var var = c.selfVar();
			if (var !=null) {
				FieldInstance fi = definedFieldNamed("distribution");
				
				dist = new C_Field_c(fi, var);
			}
		}
		isDistSet = true;
		//Report.report(1, "X1ParsedClassType dist is " + dist);
		C_Var result = dist;
		return result;
	}
	public void setDistribution(C_Var dist) {
		setProperty("distribution", dist);
		isDistSet=true;
		this.dist = dist;
	}
	C_Var self;
	boolean isSelfSet;
	public C_Var self() {
		if (isSelfSet) return self;
		Constraint c = realClause();
		if (c == null) return self=null;
		self = c.find("self");
		if (self == null) {
			// build the synthetic term.
			self = c.selfVar();
		}
		return self;
	}
	
	boolean isConstantDist;
	boolean isConstantDistSet;
	public boolean isConstantDist() {
		if (isConstantDistSet) return isConstantDist;
		isConstantDistSet = true;
		Constraint c = realClause();
		return isConstantDist= c==null ? false : this.fullName().equals("x10.lang.dist") && amIProperty("constant");
	}
	public void setConstantDist() {
		setProperty("constant");
		isConstantDist = isConstantDistSet = true;
	}
	
	boolean isUniqueDist;
	boolean isUniqueDistSet;
	public boolean isUniqueDist() {
		if (isUniqueDistSet) return isUniqueDist;
		isUniqueDistSet = true;
		Constraint c = realClause();
		return isUniqueDist= c==null ? false : this.fullName().equals("x10.lang.dist") && amIProperty("unique");
	}
	public void setUniqueDist() {
		setProperty("unique");
		isUniqueDist = isUniqueDistSet = true;
	}
	
	/**
	 * The arg must be a region type. Set the properties of this type (rank, isZeroBased, isRect)
	 * from arg.
	 * @param arg
	 */
	public void transferRegionProperties(X10ParsedClassType arg) {
		C_Var rank = arg.rank();
		C_Var region = arg.region();
		if (region == null && ((X10TypeSystem) ts).isRegion(arg))
			region = arg.self();
		acceptRegionProperties(region, rank, arg.isZeroBased(), arg.isRect(), arg.isRail());
	}
	public void acceptRegionProperties(C_Var region, C_Var rank, boolean isZeroBased, boolean isRect, boolean isRail) {
		if (region != null) setRegion(region);
		if (rank != null) setRank(rank);
		if (isZeroBased) setZeroBased();
		if (isRect) setRect();
		if (isRail) setRail();
	}
	public void setZeroBasedRectRankOne() {
		acceptRegionProperties(region, ((X10TypeSystem) typeSystem()).ONE(), true, true, true);
	}
	/** Set the value of this property on the constraints of this type. Should be called
	 * only within code that is transferring properties to this type from 
	 * properties of referenced types, e.g. an array is zeroBased if its region is. 
	 * 
	 * */
	protected void setProperty(String propName) {
		setProperty(propName, ((X10TypeSystem) typeSystem()).TRUE());
	}
	
	protected void setProperty(String propName, C_Var val)  {
		X10FieldInstance fi = definedFieldNamed(propName);
		//Report.report(1, "X10Parsedclass.setting property " + propName + " on " + this + "found fi=" + fi);
		if (fi != null &&  fi.isProperty()) {
			C_Var var = new C_Field_c(fi, C_Special.Self);
			addBinding(var, val);
		}
	}
	
	protected X10FieldInstance definedFieldNamed(String name) {
		ReferenceType x = this;
		
		X10FieldInstance fi = (X10FieldInstance) fieldNamed(name);
		while (fi == null && ! (x.equals(ts.Object()))) {
			x = (ReferenceType) x.superType();
			fi = (X10FieldInstance) x.fieldNamed(name);
		}
		return fi;
	}
	protected boolean amIProperty(String propName) {
		boolean result = false;
		try {
			X10FieldInstance fi = (X10FieldInstance) definedFieldNamed(propName);
			if (fi != null &&  fi.isProperty()) {
				C_Var term = new C_Field_c(fi, C_Special.Self);
				Constraint c = new Constraint_c((X10TypeSystem) ts);
				c.addTerm(term);
				return result = realClause().entails(c);
			}
			
		} catch (SemanticException z) {}
		
		return result;
	}
	boolean fieldsInitialized = false;
	public List fields() {
		// Report.report(1, "***X10ParsedClassTypes fields() invoked on " + this + " " + init.getClass());
		if (fieldsInitialized) 
			return fields;
		try {
			init.initFields();
			init.canonicalFields();
			// Report.report(1, "X10ParsedClassTypes " + this + ".fields() returning " + fields);
			fieldsInitialized=true;
			return Collections.unmodifiableList(fields);
		} catch (Throwable z) {
			//Report.report(1, "X10ParsedClassTypes caught  " + z);
		}
		return  Collections.unmodifiableList(fields);
	}

	public void setRealClause(Constraint realClause) {
		this.realClause = realClause;
		this.realClauseSet = true;
	}
	
	public void value(boolean flag) {
		value = flag;
	}

	public boolean isValue() {
		return value;
	}
}

