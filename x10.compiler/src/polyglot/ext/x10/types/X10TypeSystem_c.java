package polyglot.ext.x10.types;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ext.jl.types.TypeSystem_c;
import polyglot.ext.x10.types.X10PrimitiveType_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.frontend.Source;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.LazyClassInitializer;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.NullType;
import polyglot.types.ReferenceType;
import polyglot.types.UnknownType;

import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

import polyglot.main.Report;

/**
 * A TypeSystem implementation for X10.
 * 
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author vj
 *
 *
 */
public class X10TypeSystem_c 
extends TypeSystem_c 
implements X10TypeSystem {
	
	/**
	 * Requires: all type arguments are canonical.
	 *
	 * Returns true iff an implicit cast from fromType to toType is valid;
	 * in other words, every member of fromType is member of toType.
	 *
	 * Returns true iff child and ancestor are non-primitive
	 * types, and a variable of type child may be legally assigned
	 * to a variable of type ancestor.
	 *
	 */
	public boolean isImplicitCastValid(Type fromType, Type toType) {
		assert_(fromType);
		assert_(toType);
		if (Report.should_report("debug", 5))
			Report.report(5, "[X10TypeSystem_c] isImplicitCastValid |" + fromType + "| to |" + toType + "|?");
		boolean result = fromType.isImplicitCastValidImpl(toType);
		if (Report.should_report("debug", 5))
			Report.report(5, "[X10TypeSystem_c] ... " + result);
		return result;
	}
		
	public NullableType createNullableType( Position pos, ReferenceType type ) {
		return NullableType_c.makeNullable( this, pos, type );
	}
	
	public FutureType createFutureType( Position pos, Type type ) {
		return new FutureType_c( this, pos, type );
	}
	
	public ParametricType createParametricType ( Position pos, X10ReferenceType type, DepParameterExpr expr ) {
		return new ParametricType_c( this, pos, type, expr);
	}
	
	
	public X10ArrayType x10arrayOf(Type type, boolean isValue) {
		return x10arrayOf( type.position(), type, isValue);
	}
	public X10ArrayType x10arrayOf(Type type, DepParameterExpr expr) {
		return x10arrayOf( type.position(), type, expr);
	}
	public X10ArrayType x10arrayOf(Type type, boolean isValue, DepParameterExpr expr) {
		return x10arrayOf( type.position(), type, isValue, expr);
	}
	
	/**
	 * Return an X10 array of <code>type</code>
	 */
	public X10ArrayType x10arrayOf(Position pos, Type type) { 
		return new X10ArrayType_c( this, pos, type );
	}
	
	/**
	 * Return an X10 array of <code>type</code>
	 */
	public X10ArrayType x10arrayOf(Position pos, Type type, boolean isValue) {
		return new X10ArrayType_c( this, pos, type, isValue );
	}
	
	/**
	 * Return an X10 array of <code>type</code>
	 */
	public X10ArrayType x10arrayOf(Position pos, Type type, DepParameterExpr indexedSet) {
		return new X10ArrayType_c( this, pos, type, indexedSet);
	}
	
	/**
	 * Return an X10 array of <code>type</code>
	 */
	public X10ArrayType x10arrayOf(Position pos, Type type, boolean isValue, DepParameterExpr indexedSet) {
		return new X10ArrayType_c( this, pos, type, isValue, indexedSet);
	}
	
	protected UnknownType createUnknownType() {
		return new X10UnknownType_c( this );
	}
	
	protected NullType createNull() {
		return new X10NullType_c(this);
	}
	
	public ParsedClassType createClassType(LazyClassInitializer init, Source fromSource) {
		return new X10ParsedClassType_c(this, init, fromSource);    
	}
	
	
	
	/******************** Primitive types as Objects ******************/
	
	private static final String WRAPPER_PACKAGE = "x10.compilergenerated";
	
	public PrimitiveType createPrimitive(PrimitiveType.Kind kind) {
		return new X10PrimitiveType_c(this, kind);
	}
	
	/* predefined classes that need not be translated by polyglot */
	protected Type x10ObjectType_;
	protected Type placeType_;
	protected Type regionType_;
	protected Type pointType_;
	protected Type distributionType_;
	protected Type activityType_;
	protected Type futureActivityType_;
	protected Type arrayType_;
	protected Type clockType_;
	protected Type runtimeType_;
	protected Type intArrayPointwiseOpType_;
	protected Type doubleArrayPointwiseOpType_;
	protected Type intArrayType_;
	protected Type doubleArrayType_;
	protected Type doubleValueArrayType_;
	protected Type doubleReferenceArrayType_;
	protected Type intValueArrayType_;
	protected Type intReferenceArrayType_;

	
	public Type X10Object() {
		if ( x10ObjectType_ == null)
			x10ObjectType_ = load("x10.lang.Object"); // java file
		return x10ObjectType_;
	}
	public Type place() {
		if ( placeType_ == null)
			placeType_ = load("x10.lang.place"); // java file
		return placeType_;
	}
	public Type region() {
		if ( regionType_ == null)
			regionType_ = load("x10.lang.region"); // java file
		return regionType_;
	}
	public Type point() {
		if ( pointType_ == null)
			pointType_ = load("x10.lang.point"); // java file
		return pointType_;
	}
	public Type distribution() {
		if ( distributionType_ == null)
			distributionType_ = load("x10.lang.distribution"); // java file
		return distributionType_;
	}
	public Type Activity() {
		if ( activityType_ == null)
			activityType_ = load("x10.lang.Activity"); // java file
		return activityType_;
	}

	// TODO: vj -- check that  this is the right way to load a nested type.
	public Type FutureActivity() {
		if ( futureActivityType_ == null)
			futureActivityType_ = load("x10.lang.Activity.Expr"); // java file
		return futureActivityType_;
	}
	public Type array() {
		if ( arrayType_ == null)
			arrayType_ = load("x10.lang.array"); // java file
		return arrayType_;
	}
	public Type clock() {
		if ( clockType_ == null)
			clockType_ = load("x10.lang.clock"); // java file
		return clockType_;
	}

	public Type Runtime() {
		if ( runtimeType_ == null)
			runtimeType_ = load("x10.lang.Runtime"); // java file
		return runtimeType_;
	}
	public Type IntArrayPointwiseOp() {
		if ( intArrayPointwiseOpType_ == null)
			intArrayPointwiseOpType_ = load("x10.lang.intArray.pointwiseOp"); // java file
		return intArrayPointwiseOpType_;
	}
	public Type DoubleArrayPointwiseOp() {
		if ( doubleArrayPointwiseOpType_ == null)
			doubleArrayPointwiseOpType_ = load("x10.lang.doubleArray.pointwiseOp"); // java file
		return doubleArrayPointwiseOpType_;
	}
	public Type intArray() {
		if ( intArrayType_ == null)
			intArrayType_ = load("x10.lang.intArray"); // java file
		return intArrayType_;
	}
	public Type intValueArray() {
		if ( intValueArrayType_ == null)
			intValueArrayType_ = load("x10.lang.intValueArray"); // java file
		return intValueArrayType_;
	}
	public Type IntReferenceArray() {
		if ( intReferenceArrayType_ == null)
			intReferenceArrayType_ = load("x10.lang.IntReferenceArray"); // java file
		return intReferenceArrayType_;
	}
	public Type doubleArray() {
		if ( doubleArrayType_ == null)
			doubleArrayType_ = load("x10.lang.doubleArray"); // java file
		return doubleArrayType_;
	}
	public Type doubleValueArray() {
		if ( doubleValueArrayType_ == null)
			doubleValueArrayType_ = load("x10.lang.doubleValueArray"); // java file
		return doubleValueArrayType_;
	}
	public Type DoubleReferenceArray() {
		if ( doubleReferenceArrayType_ == null)
			doubleReferenceArrayType_ = load("x10.lang.DoubleReferenceArray"); // java file
		return doubleReferenceArrayType_;
	}
	
	public MethodInstance primitiveEquals() {
		String name = WRAPPER_PACKAGE + ".BoxedNumber";
		
		try {
			Type ct = (Type) systemResolver().find(name);
			
			List args = new LinkedList();
			args.add(X10Object());
			args.add(X10Object());
			
			for (Iterator i = ct.toClass().methods("equals", args).iterator();
			i.hasNext(); ) {
				
				MethodInstance mi = (MethodInstance) i.next();
				return mi;
			}
		}
		catch (SemanticException e) {
			throw new InternalCompilerError(e.getMessage());
		}
		
		throw new InternalCompilerError("Could not find equals method.");
	}
	
	public MethodInstance getter(PrimitiveType t) {
		String methodName = t.toString() + "Value";
		ConstructorInstance ci = wrapper(t);
		
		for (Iterator i = ci.container().methods().iterator();
		i.hasNext(); ) {
			MethodInstance mi = (MethodInstance) i.next();
			if (mi.name().equals(methodName) && mi.formalTypes().isEmpty()) {
				return mi;
			}
		}
		
		throw new InternalCompilerError("Could not find getter for " + t);
	}
	
	public Type boxedType(PrimitiveType t) {
		return wrapper(t).container();
	}
	
	public ConstructorInstance wrapper(PrimitiveType t) {
		String name = WRAPPER_PACKAGE + ".Boxed" + wrapperTypeString(t).substring("java.lang.".length());
		
		try {
			ClassType ct = ((Type) systemResolver().find(name)).toClass();
			
			for (Iterator i = ct.constructors().iterator(); i.hasNext(); ) {
				ConstructorInstance ci = (ConstructorInstance) i.next();
				if (ci.formalTypes().size() == 1) {
					Type argType = (Type) ci.formalTypes().get(0);
					if (equals(argType, t)) {
						return ci;
					}
				}
			}
		}
		catch (SemanticException e) {
			throw new InternalCompilerError(e.getMessage());
		}
		
		throw new InternalCompilerError("Could not find constructor for " + t);
	}
	
	
} // end of X10TypeSystem_c
