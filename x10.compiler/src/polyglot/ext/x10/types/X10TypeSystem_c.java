package polyglot.ext.x10.types;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ext.jl.types.TypeSystem_c;
import polyglot.ext.x10.types.X10PrimitiveType_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.X10DelFactory_c;
import polyglot.ext.x10.ast.X10ExtFactory_c;
import polyglot.ext.x10.ast.X10NodeFactory_c;
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
	private static X10TypeSystem_c factory = null;
	public static X10TypeSystem_c getFactory() {
		return factory;
	}
	public X10TypeSystem_c() {
		super();
		factory = this;
	}
	
	
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
	
	/* Predefined x10.lang classes that need not be translated by polyglot */
	protected ClassType x10ObjectType_;
	public ClassType X10Object() {
		if ( x10ObjectType_ == null)
			x10ObjectType_ = load("x10.lang.Object"); // java file
		return x10ObjectType_;
	}
	
	protected ClassType placeType_;
	public ClassType place() {
		if ( placeType_ == null)
			placeType_ = load("x10.lang.place"); // java file
		return placeType_;
	}
	
	protected ClassType regionType_;
	public ClassType region() {
		if ( regionType_ == null)
			regionType_ = load("x10.lang.region"); // java file
		return regionType_;
	}
	
	protected ClassType pointType_;
	public ClassType point() {
		if ( pointType_ == null)
			pointType_ = load("x10.lang.point"); // java file
		return pointType_;
	}
	
	protected ClassType distributionType_;
	public ClassType distribution() {
		if ( distributionType_ == null)
			distributionType_ = load("x10.lang.distribution"); // java file
		return distributionType_;
	}
	
	protected ClassType activityType_;
	public ClassType Activity() {
		if ( activityType_ == null)
			activityType_ = load("x10.lang.Activity"); // java file
		return activityType_;
	}

	protected ClassType futureActivityType_;
	public ClassType FutureActivity() {
		if ( futureActivityType_ == null)
			futureActivityType_ = load("x10.lang.Activity$Expr"); // java file
		return futureActivityType_;
	}
	
	protected ClassType arrayType_;
	public ClassType array() {
		if ( arrayType_ == null)
			arrayType_ = load("x10.lang.array"); // java file
		return arrayType_;
	}
	
	protected ClassType clockType_;
	public ClassType clock() {
		if ( clockType_ == null)
			clockType_ = load("x10.lang.clock"); // java file
		return clockType_;
	}

	protected ClassType runtimeType_;
	public ClassType Runtime() {
		if ( runtimeType_ == null)
			runtimeType_ = load("x10.lang.Runtime"); // java file
		return runtimeType_;
	}
	
	protected ClassType intArrayPointwiseOpType_;
	public ClassType IntArrayPointwiseOp() {
		if ( intArrayPointwiseOpType_ == null) {
			intArray(); // ensure that intArray is loaded.
			intArrayPointwiseOpType_ = load("x10.lang.intArray$pointwiseOp"); // java file
		}
		return intArrayPointwiseOpType_;
	}
	
	protected ClassType doubleArrayPointwiseOpType_;
	public ClassType DoubleArrayPointwiseOp() {
		if ( doubleArrayPointwiseOpType_ == null)
			doubleArrayPointwiseOpType_ = load("x10.lang.doubleArray$pointwiseOp"); // java file
		return doubleArrayPointwiseOpType_;
	}
	
	public ClassType array(Type type, boolean isValueType, Expr distribution) {
		if (type.isInt())
			return intArray( isValueType, distribution );
		if (type.isDouble())
			return doubleArray( isValueType, distribution);
		if (type.isLong())
			return longArray( isValueType, distribution);
		throw new Error("X10 array types not yet implemented for base types other than int, double and long. ");
	}
	
	public ClassType array( Type type,  Expr distribution ) {
		if (type.isInt())
			return intArray(  distribution );
		if (type.isDouble())
			return doubleArray(  distribution );
		if (type.isLong())
			return longArray(  distribution );
		throw new Error("X10 array types not yet implemented for base types other than int, double and long. ");
	}
	
	public ClassType array(Type type, boolean isValue ) {
		if (type.isInt())
			return isValue ? intValueArray() : IntReferenceArray();
		if (type.isDouble())
		    return isValue ? doubleValueArray() : DoubleReferenceArray();
		if (type.isLong())
		    return isValue ? longValueArray() : LongReferenceArray();
		throw new Error("X10 array types not yet implemented for base types other than int, double and long. ");
	}
	
	public ClassType array(Type type ) {
		if (type.isInt())
		    return intArray( );
		if (type.isDouble())
		    return doubleArray();
		if (type.isLong())
		    return longArray();
		throw new Error("X10 array types not yet implemented for base types other than int, double. ");
	}
	public ClassType intArray(boolean isValueType, Expr distribution ) {
		return 
		isValueType ? intValueArray( distribution ) : IntReferenceArray( distribution );
	}
	
	protected ClassType intArrayType_;
	public ClassType intArray( Expr distribution ) {
		if ( intArrayType_ == null)
			intArrayType_ = load("x10.lang.intArray"); // java file
		return intArrayType_;
	}
	
	public ClassType intArray() {
		return intArray( null );
	}
	
	public ClassType intValueArray( ) {
		return intValueArray(null );
	}
	
	protected ClassType intValueArrayType_;
	public ClassType intValueArray( Expr distribution ) {
			// vj: should really load x10.lang.intValueArray, but we are cheating...
			// there is no difference in the public type (= methods) of x10.lang.intValueArray
		    // and x10.lang.intArray, so we reuse the implementation of x10.lang.intArray.
		    // The backend does not do anything special about value classes right now anyway,
		    // so there is no need to tell the implementation that this is a value class.
		    // This hack will need to be fixed once the compiler starts checking that
		    // value classes can only be extended by value classes. 
		return intArray( distribution );
	}
	public ClassType IntReferenceArray() {
		return IntReferenceArray( null );
	}
	
	protected ClassType intReferenceArrayType_;
	public ClassType IntReferenceArray( Expr distribution ) {
		if ( intReferenceArrayType_ == null)
			intReferenceArrayType_ = load("x10.lang.IntReferenceArray"); // java file
		// return intReferenceArrayType_.setParameter( "distribution", distribution );
		return intReferenceArrayType_;
	}

	protected ClassType longArrayPointwiseOpType_;
	public ClassType LongArrayPointwiseOp() {
		if ( longArrayPointwiseOpType_ == null)
			longArrayPointwiseOpType_ = load("x10.lang.longArray$pointwiseOp"); // java file
		return longArrayPointwiseOpType_;
	}
	public ClassType longArray(boolean isValueType, Expr distribution ) {
		return 
		isValueType ? longValueArray( distribution ) : LongReferenceArray( distribution );
	}
	
	protected ClassType longArrayType_;
	public ClassType longArray( Expr distribution ) {
		if ( longArrayType_ == null)
			longArrayType_ = load("x10.lang.longArray"); // java file
		return longArrayType_;
	}
	
	public ClassType longArray() {
		return longArray( null );
	}
	
	public ClassType longValueArray( ) {
		return longValueArray(null );
	}
	
	public ClassType longValueArray( Expr distribution ) {
		return longArray( distribution );
	}
	public ClassType LongReferenceArray() {
		return LongReferenceArray( null );
	}
	
	protected ClassType longReferenceArrayType_;
	public ClassType LongReferenceArray( Expr distribution ) {
		if ( longReferenceArrayType_ == null)
			longReferenceArrayType_ = load("x10.lang.LongReferenceArray"); // java file
		// return longReferenceArrayType_.setParameter( "distribution", distribution );
		return longReferenceArrayType_;
	}
	
	
	public ClassType doubleArray(boolean isValueType, Expr distribution ) {
		return 
		isValueType ? doubleValueArray( distribution ) : DoubleReferenceArray( distribution );
	}
	public ClassType doubleArray() {
		return doubleArray( null );
	}
	
	protected ClassType doubleArrayType_;
	public ClassType doubleArray( Expr distribution ) {
		if ( doubleArrayType_ == null)
			doubleArrayType_ = load("x10.lang.doubleArray"); // java file
		
		return doubleArrayType_;
	}
	
	public ClassType doubleValueArray( ) {
		return doubleValueArray(null );
	}
	
	protected ClassType doubleValueArrayType_;
	// see the todo for intValueArray.
	public ClassType doubleValueArray( Expr distribution ) {
			return doubleArray( distribution );
	}
	
	public ClassType DoubleReferenceArray( ) {
		return DoubleReferenceArray(null );
	}
	
	protected ClassType doubleReferenceArrayType_;
	public ClassType DoubleReferenceArray( Expr distribution ) {
		if ( doubleReferenceArrayType_ == null)
			doubleReferenceArrayType_ = load("x10.lang.DoubleReferenceArray"); // java file
         //	 return doubleReferenceArrayType_.setParameter( "distribution", distribution );
		return doubleReferenceArrayType_;
	}
	
	protected ClassType indexableType_ = null;
	public ClassType Indexable() {
		if ( indexableType_ == null) 
			indexableType_ = load("x10.lang.Indexable"); // java file
		return indexableType_;
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
