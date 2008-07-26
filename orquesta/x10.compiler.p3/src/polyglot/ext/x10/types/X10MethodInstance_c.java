/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.main.Report;
import polyglot.types.ArrayType;
import polyglot.types.DerefTransform;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.MethodInstance_c;
import polyglot.types.NullType;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XNameWrapper;
import x10.constraint.XPromise;
import x10.constraint.XRef_c;
import x10.constraint.XRoot;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

/**
 * A representation of a MethodInstance. This implements the requirement that method
 * annotations such as sequential, local, nonblocking, safe are preserved on overriding.
 * @author vj
 *
 */
public class X10MethodInstance_c extends MethodInstance_c implements X10MethodInstance {

	public static class NoClauseVariant implements Transformation<Type, Type> {
		public Type transform(Type o) {
			if (o instanceof ArrayType) {
				ArrayType at = (ArrayType) o;
				return at.base(Types.ref(transform(at.base())));
			}
			if (o instanceof ConstrainedType) {
				ConstrainedType ct = (ConstrainedType) o;
				return transform(Types.get(ct.baseType()));
			}
			return ((X10Type) o);
		}
	}
	
	public Object copy() { 
		return super.copy();
	}
	
	@Override
	public X10MethodInstance returnType(Type returnType) {
		return (X10MethodInstance) super.returnType(returnType);
	}

	public List<X10ClassType> annotations() {
	    return X10TypeObjectMixin.annotations(this);
	}
	public List<X10ClassType> annotationsMatching(Type t) {
	    return X10TypeObjectMixin.annotationsMatching(this, t);
	}
	
	public X10MethodInstance_c(TypeSystem ts, Position pos, Ref<? extends X10MethodDef> def) {
	    super(ts, pos, def);
	}
	
	XTerm body;

	public XTerm body() {
	    if (this.body == null)
		body = Types.get(x10Def().body());
	    return body;
	}

	public X10MethodInstance body(XTerm body) {
	    X10MethodInstance_c n = (X10MethodInstance_c) copy();
	    n.body = body;
	    return n;
	}

	/** Constraint on formal parameters. */
	protected XConstraint whereClause;
	public XConstraint whereClause() { return whereClause; }
	public X10MethodInstance whereClause(XConstraint s) { 
	    X10MethodInstance_c n = (X10MethodInstance_c) copy();
	    n.whereClause = s; 
	    return n;
	}
	

	    public List<Type> typeParameters;

	    public List<Type> typeParameters() {
		    if (this.typeParameters == null) {
			    this.typeParameters = new TransformingList<Ref<? extends Type>, Type>(x10Def().typeParameters(), new DerefTransform<Type>());
		    }

		    return typeParameters;
	    }

	    public X10MethodInstance typeParameters(List<Type> typeParameters) {
		    X10MethodInstance_c n = (X10MethodInstance_c) copy();
		    n.typeParameters = typeParameters;
		    return n;
	    }

	public void checkOverride(MethodInstance mj) throws SemanticException {
	    //  Report.report(1, "X10MethodInstance_c: " + this + " canOverrideImpl " + mj);
	    super.checkOverride(mj);
	    MethodInstance mi = this;
	    X10Flags miF = X10Flags.toX10Flags(mi.flags());
	    X10Flags mjF = X10Flags.toX10Flags(mj.flags());

	    // Report.report(1, "X10MethodInstance_c: " + this + " canOVerrideImpl " + mj);
	    if (! miF.hasAllAnnotationsOf(mjF)) {
	        if (Report.should_report(Report.types, 3))
	            Report.report(3, mi.flags() + " is more liberal than " +
	                          mj.flags());
	        throw new SemanticException(mi.signature() + " in " + mi.container() +
	                                    " cannot override " + 
	                                    mj.signature() + " in " + mj.container() + 
	                                    "; attempting to assign weaker " + 
	                                    "behavioral annotations", 
	                                    mi.position());
	    }
	}

	public boolean isPropertyGetter() {
		assert container instanceof X10ParsedClassType;
		if (isJavaMethod()) return false;
		if (!formalTypes.isEmpty()) return false;
		for (FieldInstance fi : container.fields()) {
		    FieldDef fd = fi.def();
		    if (fd instanceof X10FieldDef) {
		        X10FieldDef xfd = (X10FieldDef) fd;
		        if (xfd.isProperty()) {
		            return true;
		        }
		    }
		}
		return false;
	}
	public boolean isJavaMethod() {
		assert container instanceof X10ParsedClassType;
		boolean result = ((X10ParsedClassType) container).isJavaType();
		return result;
	}
	public boolean isSafe() {
		assert container instanceof X10ParsedClassType;
		boolean result = ((X10ParsedClassType) container).safe();
		if (result) return true;
		X10Flags f = X10Flags.toX10Flags(flags());
		result = f.isSafe();
		return result;
	}
	protected static String myListToString(List l) {
		StringBuffer sb = new StringBuffer();
		
		for (Iterator i = l.iterator(); i.hasNext(); ) {
			Object o = i.next();
			sb.append(o.toString());
			
			if (i.hasNext()) {
				sb.append(", ");
			}
		}
		
		return sb.toString();
	}

	  /** Returns true if a call can be made with the given argument types. 
	   * Specialized to deal with the possibility of dependent types in the 
	   * arguments of the method which may contain references to arguments
	   * to the method.
	   * TODO: Take into account the deptype in the parameter list of the method.
	   */
	@Override
	public boolean callValid(Type thisType, List<Type> argTypes) {
		return X10MethodInstance_c.callValidImpl(this, thisType, argTypes);
	}
	
	public static boolean callValidImpl(X10ProcedureInstance<?> me, Type thisType, final List<Type> args) {
		// me should have been instantiated correctly; if so, the call is valid
	    if (true) return true;
	    try {
			instantiate(me, thisType, args);
			return true;
		}
		catch (SemanticException e) {
			return false;
		}
	}

	public boolean callValidNoClauses(Type thisType, List<Type> argTypes) {
	    X10MethodInstance_c me = (X10MethodInstance_c) this.formalTypes(new TransformingList<Type,Type>(this.formalTypes(), new X10MethodInstance_c.NoClauseVariant()));
	    return me.superCallValid(thisType, new TransformingList<Type,Type>(argTypes, new X10MethodInstance_c.NoClauseVariant()));
	}

	protected boolean superCallValid(Type thisType, List<Type> argTypes) {
	    return super.callValid(thisType, argTypes);
	}

    public X10MethodDef x10Def() {
        return (X10MethodDef) def();
    }
    
    public String toString() {
	    String s = designator() + " " + flags().translate() + container() + "." + signature() + (whereClause() != null ? whereClause() : "") + ": " + returnType();
	
	    if (! throwTypes().isEmpty()) {
		    s += " throws " + CollectionUtil.listToString(throwTypes());
	    }
	
	    return s;
    }
    
    public String signature() {
	    return name() + (typeParameters().isEmpty() ? "" : "[" + CollectionUtil.listToString(typeParameters()) + "]") + "(" + CollectionUtil.listToString(formalTypes()) + ")";
    }
    
    @Override
    public MethodInstance instantiate(Type receiverType,
    		List<Type> argumentTypes) throws SemanticException {

	    return instantiate(this, receiverType, argumentTypes);
    }

    public static <PI extends X10ProcedureInstance<?>> PI instantiate(PI me, Type thisType, final List<Type> args) throws SemanticException {
	List<Type> actualTypes = new ArrayList<Type>();
	List<Type> actuals = new ArrayList<Type>();
	X10TypeSystem xts = (X10TypeSystem) me.typeSystem();
	
	    // We've smuggled the type args in with the actual args.  Pull them out again.
	    for (Type t : args) {
		    Type base = X10TypeMixin.baseType(t);
		    if (base.typeEquals(((X10TypeSystem_c) xts).TypeType())) {
			    // t should be type{self==C}.  Add C to the typeActuals list.
			    XConstraint c = X10TypeMixin.xclause(t);
			    XVar v = X10TypeMixin.selfVar(c);
			    if (v instanceof XLit) {
				    XLit lit = (XLit) v;
				    if (lit.val() instanceof Type) {
					    actualTypes.add((Type) lit.val());
					    continue;
				    }
			    }
			    throw new InternalCompilerError("Could not extract smuggled type argument from " + t + ".", t.position());
		    }
		    else {
			    actuals.add(t);
		    }
	    }

	    return instantiate(me, thisType, actualTypes, actuals);
    }
    
    public static <PI extends X10ProcedureInstance<?>> PI instantiate(PI me, Type thisType, List<Type> actualTypes, final List<Type> args) throws SemanticException {
	    X10TypeSystem xts = (X10TypeSystem) me.typeSystem();
	    final List<Type> formals = me.formalTypes();
	    final List<Type> typeFormals = me.typeParameters();
	    final List<Type> actuals = args;
	    List<XVar> actualTypeVars = new ArrayList<XVar>();

	    for (Type type : me.throwTypes()) {
		    XConstraint rc = X10TypeMixin.realX(type);
		    if (! rc.valid())
			    throw new SemanticException("Cannot throw a dependent type.", me.position());
	    }

	    XVar selfVar = X10TypeMixin.selfVar(thisType);
	    if (selfVar != null && selfVar.selfConstraint() == null) {
		final Type t = thisType;
		selfVar.setSelfConstraint(new XRef_c<XConstraint>() { public XConstraint compute() { return X10TypeMixin.realX(t); } });
	    }

	    if (selfVar == null) {
		    XConstraint c = X10TypeMixin.xclause(thisType);
		    c = (c == null) ? new XConstraint_c() : c.copy();

		    try {
			    selfVar = xts.xtypeTranslator().genEQV(c, thisType);
			    c.addSelfBinding(selfVar);
		    }
		    catch (XFailure e) {
			    throw new SemanticException(e.getMessage(), me.position());
		    }

		    thisType = X10TypeMixin.xclause(thisType, c);
	    }

	    // We'll subst selfVar for THIS.
	    XRoot THIS = xts.xtypeTranslator().transThis(thisType);

	    if (actuals.size() != formals.size()) {
		    throw new SemanticException("Call not valid; incorrect number of actual arguments.", me.position());
	    }
	    
	    if (actualTypes.size() != 0 && actualTypes.size() != typeFormals.size()) {
		    throw new SemanticException("Call not valid; incorrect number of actual type arguments.", me.position());
	    }
	    
	    XConstraint env  = new XConstraint_c();
	    
	    for (Type t : actualTypes) {
		XVar lit = (XVar) xts.xtypeTranslator().trans(t);
		actualTypeVars.add(lit);
	    }

	    if (actualTypeVars.size() == 0) {
		    // Generate a list of type vars to use for the type actuals.
		    // After checking if the call is valid, we'll solve for the vars.   
		    for (int i = 0; i < typeFormals.size(); i++) {
			    XVar v = env.genEQV(false);
			    actualTypeVars.add(v);
		    }
	    }
	    
	    // Given call e.m[T1,...,Tk](e1,...,en)
	    // and method T.m[X1,...,Xk](x1: S1,...,xn: Sn){c}
	    // We build the following environment:
	    // env = {X1==T1,...,Xk==Tk,x1==e1,...,xn==en}
	    // and check
	    // {e1.type <: S1, ..., en.type <: Sn, c}
	    
	    assert actualTypeVars.size() == typeFormals.size();
	    assert actuals.size() == formals.size();
	    
	    XRoot[] X = new XRoot[typeFormals.size()];
	    XVar[] Y = new XVar[typeFormals.size()];
	    XRoot[] x = new XRoot[formals.size()];
	    XVar[] y = new XVar[formals.size()];

	    for (int i = 0; i < typeFormals.size(); i++) {
		    Type xtype = typeFormals.get(i);

		    // TODO: should enforce this statically
		    assert xtype instanceof ParameterType : xtype + " is not a ParameterType, is a " + (xtype != null ? xtype.getClass().getName() : "null");
		    
		    XRoot xi = xts.xtypeTranslator().transTypeParam((ParameterType) xtype);
		    XVar yi = actualTypeVars.get(i);

		    X[i] = xi;
		    Y[i] = yi;
	    }

	    for (int i = 0; i < formals.size(); i++) {
		    Type xtype = formals.get(i);
		    Type ytype = actuals.get(i);

		    XConstraint yc = X10TypeMixin.realX(ytype);

		    XRoot xi;
		    XVar yi;

		    yi = X10TypeMixin.selfVar(yc);

		    if (yi == null) {
			    // This must mean that yi was not final, hence it cannot occur in 
			    // the dependent clauses of downstream yi's.
			    yi = xts.xtypeTranslator().genEQV(env, ytype, false);
		    }

		    XConstraint xc = X10TypeMixin.realX(xtype);
		    XVar self = X10TypeMixin.selfVar(xc);
		    if (self instanceof XRoot)
			    xi = (XRoot) self;
		    else
			    xi = (XRoot) xts.xtypeTranslator().genEQV(env, xtype, false);

		    try {
			    XConstraint yc2 = yc.substitute(yi, XSelf.Self);
			    env.addIn(yc2);
			    env.addBinding(xi, yi);
		    }
		    catch (XFailure f) {
			    // environment is inconsistent.
			    throw new SemanticException("Call invalid; calling environment is inconsistent.");
		    }

		    x[i] = xi;
		    y[i] = yi;
	    }

	    XConstraint bigquery = new XConstraint_c();

	    for (int i = 0; i < formals.size(); i++) {
		    Type xtype = formals.get(i);
		    Type ytype = actuals.get(i);
		    
		    XConstraint yc = X10TypeMixin.xclause(ytype);
		    try {
			yc = yc != null ? yc : new XConstraint_c();
			yc.addBinding(x[i], y[i]);
			yc.addSelfBinding(y[i]);
		    }
		    catch (XFailure e) {
		    }
		    ytype = X10TypeMixin.xclause(ytype, yc);

		    XConstraint query = new XConstraint_c();

		    try {
			    query.addAtom(xts.xtypeTranslator().transSubtype(ytype, xtype));
		    }
		    catch (XFailure f) {
			    throw new SemanticException("Call invalid; calling environment is inconsistent.");
		    }

		    try {
			    XConstraint query2 = query.substitute(selfVar, THIS);
			    XConstraint query3 = query2.substitute(Y, X);
			    XConstraint query4 = query3.substitute(y, x);
			    boolean result = env.entails(query4);
			    if (! result)
				    throw new SemanticException("Call invalid; actual parameter of type " + ytype + " is not a subtype of the formal parameter type " + xtype + ".");
			    bigquery.addIn(query4);
		    }
		    catch (XFailure f) {
			    // Substitution introduces inconsistency.
			    throw new SemanticException("Call invalid; calling environment is inconsistent.");
		    }
	    }

	    for (int i = 0; i < formals.size(); i++) {
		    Type xtype = formals.get(i);
		    Type ytype = actuals.get(i);

		    XVar yi = y[i];
		    XRoot xi = x[i];

		    Type ti = formals.get(i);

		    XConstraint query = X10TypeMixin.realX(ti);

		    if (query != null && ! query.valid()) {
			    try {
				    XConstraint query2 = query.substitute(selfVar, THIS);
				    XConstraint query3 = query2.substitute(Y, X);
				    XConstraint query4 = query3.substitute(y, x);
				    XConstraint query5 = query4.substitute(yi, XSelf.Self);

				    boolean result = env.entails(query5);
				    if (! result)
					    throw new SemanticException("Call invalid; actual parameter of type " + ytype + " is not a subtype of the formal parameter type " + xtype + ".");

				    bigquery.addIn(query5);
			    }
			    catch (XFailure f) {
				    // Substitution introduces inconsistency.
				    throw new SemanticException("Call invalid; calling environment is inconsistent.");
			    }
		    }
	    }

	    try {
		    XConstraint query = me.whereClause();
		    if (query != null) {
			    XConstraint query2 = query.substitute(selfVar, THIS);
			    XConstraint query3 = query2.substitute(Y, X);
			    XConstraint query4 = query3.substitute(y, x);
			    bigquery.addIn(query4);
		    }
	    }
	    catch (XFailure f) {
		    // Substitution introduces inconsistency.
		    throw new SemanticException("Call invalid; calling environment is inconsistent.");
	    }

	    try {
		    boolean result = env.entails(bigquery);
		    if (! result)
			    throw new SemanticException("Call invalid; calling environment does not entail calling environment.");
	    }
	    catch (XFailure f) {
		    throw new SemanticException("Call invalid; calling environment is inconsistent.");
	    }
	    
	    try {
		    env.addIn(bigquery);
		    
		    for (int i = 0; i < X.length; i++) {
			    env.addBinding(X[i], Y[i]);
		    }
		    for (int i = 0; i < x.length; i++) {
			    env.addBinding(x[i], y[i]);
		    }
		    
		    for (int i = 0; i < actualTypeVars.size(); i++) {
			    XVar v = actualTypeVars.get(i);
			    XLit lit = null;

			    XPromise p = env.lookup(v);

			    if (p != null && p.term() instanceof XLit) {
				    lit = (XLit) p.term();
			    }

			    if (lit != null && lit.val() instanceof Type) {
				    Y[i] = lit;
			    }
			    else {
				    throw new SemanticException("Could not infer type for type parameter " + typeFormals.get(i) + ".", me.position());
			    }
		    }
		    
		    XRoot[] x2 = new XRoot[x.length+1];
		    XVar[] y2 = new XVar[y.length+1];
		    x2[0] = THIS;
		    y2[0] = selfVar;
		    
		    System.arraycopy(x, 0, x2, 1, x.length);
		    System.arraycopy(y, 0, y2, 1, y.length);
		    
		    List<Type> newFormals = new ArrayList<Type>();
		    
		    for (Type t : me.formalTypes()) {
			    Type newT = subst(t, y2, x2, Y, X, typeFormals);
			    newFormals.add(newT);
		    }
		    
		    Type newReturnType = subst(me.returnType(), y2, x2, Y, X, typeFormals);
		    XConstraint newWhere = subst(me.whereClause(), y2, x2, Y, X, typeFormals);

		    // TODO: simplify PathType xs.T to constrained MacroType List.T(xs) == List.T{this==xs}
		    // TODO: <: constraint transitivity
		    // TODO: upper and lower bounds
		    
		    me = (PI) me.typeParameters(getTypes(Y));
		    me = (PI) me.returnType(newReturnType);
		    me = (PI) me.formalTypes(newFormals);
		    me = (PI) me.whereClause(newWhere);
	    }
	    catch (XFailure f) {
		    throw new SemanticException("Call invalid; calling environment and callee is inconsistent.");
	    }
	    
	    return me;
    }
    
    
    static List<Type> getTypes(XVar[] Y) {
	    return new TransformingList<XVar, Type>(Arrays.asList(Y), new Transformation<XVar, Type>() {
		    public Type transform(XVar o) {
			    return getType(o);
		    }
	    });
    }
    
    static Type getType(XVar Y) {
	    if (Y instanceof XLit) {
		    XLit lit = (XLit) Y;
		    if (lit.val() instanceof Type)
			    return (Type) lit.val();
	    }
	    return null;
    }
    
    private static XConstraint subst(XConstraint c, XVar[] y, XRoot[] x, XVar[] Y, XRoot[] X, List<Type> typeFormals) throws SemanticException {
	    assert y.length == x.length;
	    assert Y.length == X.length;
	    
	    if (c == null)
		    return null;

	    for (int i = 0; i < X.length; i++) {
		    Type ty = getType(Y[i]);
		    if (ty == null)
			    throw new SemanticException("Cannot infer type for parameter " + X[i] + ".");
		    c = subst(c, ty, X[i], typeFormals.get(i));
	    }
	    
	    try {
		    c = c.substitute(y, x);
	    }
	    catch (XFailure e) {
		    throw new SemanticException("Cannot instantiate formal parameters on actuals.");
	    }

	    return c;
    }

    static Type subst(Type t,  XVar[] y, XRoot[] x, XVar[] Y, XRoot[] X, List<Type> typeFormals) throws SemanticException {
	    assert y.length == x.length;
	    assert Y.length == X.length;

	    for (int i = 0; i < X.length; i++) {
		    Type ty = getType(Y[i]);
		    if (ty == null)
			    throw new SemanticException("Cannot infer type for parameter " + X[i] + ".");
		    t = subst(t, ty, X[i], typeFormals.get(i));
	    }
	    
	    t = subst(t, y, x);

	    return t;
    }
    
    static Type subst(Type t, XVar[] actual, XRoot[] var) throws SemanticException {
	    X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
	    if (t instanceof ClosureType) {
		    // TODO
	    }
	    if (t instanceof ArrayType) {
		    ArrayType at = (ArrayType) t;
		    Type newBase = subst(at.base(), actual, var);
		    return at.base(Types.ref(newBase));
	    }
	    if (t instanceof ParametrizedType) {
		    ParametrizedType pt = (ParametrizedType) t;
		    List<Type> typeParams = new ArrayList<Type>();
		    List<Type> formalTypes = new ArrayList<Type>();
		    List<XVar> formals = new ArrayList<XVar>();
		    for (Type p : pt.typeParameters()) {
			    Type p2 = subst(p, actual, var);
			    typeParams.add(p2);
		    }
		    for (Type p : pt.formalTypes()) {
			    Type p2 = subst(p, actual, var);
			    formalTypes.add(p2);
		    }
		    for (XVar v : pt.formals()) {
			    XVar v2 = v;
			    for (int i = 0; i < actual.length; i++) {
				    v2 = (XVar) v2.subst(actual[i], var[i]);
			    }
			    formals.add(v2);
		    }
		    t = pt.typeParameters(typeParams).formals(formals).formalTypes(formalTypes);
	    }
	    
	    if (t instanceof ConstrainedType) {
		    ConstrainedType ct = (ConstrainedType) t;
		    Type base = subst(ct.baseType().get(), actual, var);
		    t = ct.baseType(Types.ref(base));
		    // fall through
	    }
	    
	    XConstraint c = X10TypeMixin.xclause(t);
	    if (c != null) {
		    try {
			    c = c.substitute(actual, var);
		    }
		    catch (XFailure e) {
			    throw new SemanticException("Cannot instantiate formal parameters on actuals.");
		    }
		    return X10TypeMixin.xclause(t, c);
	    }
	    return t;
    }

    static Type subst(Type t, Type actual, XRoot var, Type formal) throws SemanticException {
	    X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
	    if (ts.typeEquals(t, formal))
		    return actual;
	    if (t instanceof ClosureType) {
		    // TODO
	    }
	    if (t instanceof ArrayType) {
		    // TODO
	    }
	    XConstraint c = X10TypeMixin.xclause(t);
	    if (c != null) {
		    c = subst(c, actual, var, formal);
		    return X10TypeMixin.xclause(t, c);
	    }
	    return t;
    }

    private static XConstraint subst(XConstraint c, Type actual, XRoot var, Type formal) throws SemanticException {
	    X10TypeSystem ts = (X10TypeSystem) actual.typeSystem();
	    if (c == null)
		    return c;
	    try {
		    /// FIXME: replace parameterType with a var elsewhere!
		    return c.substitute(ts.xtypeTranslator().trans(actual), var);
	    }
	    catch (XFailure e) {
		    throw new SemanticException("Cannot instantiate type parameter " + formal + " on type " + actual + ".");
	    }
    }
    

}
