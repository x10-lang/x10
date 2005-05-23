package polyglot.ext.x10.types;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ext.jl.types.TypeSystem_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.frontend.Source;
import polyglot.main.Report;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.LazyClassInitializer;
import polyglot.types.MethodInstance;
import polyglot.types.NullType;
import polyglot.types.ParsedClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.UnknownType;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

/**
 * A TypeSystem implementation for X10.
 * 
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author vj
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
	
	public ParametricType createParametricType ( Position pos, X10ReferenceType type, 
	        List typeparameters,
	        DepParameterExpr expr ) {
		return new ParametricType_c( this, pos, type, typeparameters, expr);
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

    protected ClassType valueType_;
    public ClassType value() {
        if ( valueType_ == null)
            valueType_ = load("x10.lang.ValueType"); // java file
        return valueType_;
    }

    protected ClassType distributionType_;
    public ClassType distribution() {
        if ( distributionType_ == null)
            distributionType_ = load("x10.lang.dist"); // java file
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
	
	protected ClassType booleanArrayPointwiseOpType_;
    public ClassType BooleanArrayPointwiseOp() {
        if ( booleanArrayPointwiseOpType_ == null)
            booleanArrayPointwiseOpType_ = load("x10.lang.booleanArray$pointwiseOp"); // java file
        return booleanArrayPointwiseOpType_;
    }
    
    protected ClassType charArrayPointwiseOpType_;
    public ClassType CharArrayPointwiseOp() {
        if ( charArrayPointwiseOpType_ == null)
            charArrayPointwiseOpType_ = load("x10.lang.charArray$pointwiseOp"); // java file
        return charArrayPointwiseOpType_;
    }
    
    protected ClassType byteArrayPointwiseOpType_;
    public ClassType ByteArrayPointwiseOp() {
        if ( byteArrayPointwiseOpType_ == null)
            byteArrayPointwiseOpType_ = load("x10.lang.byteArray$pointwiseOp"); // java file
        return byteArrayPointwiseOpType_;
    }
    
    protected ClassType shortArrayPointwiseOpType_;
    public ClassType ShortArrayPointwiseOp() {
        if ( shortArrayPointwiseOpType_ == null)
            shortArrayPointwiseOpType_ = load("x10.lang.shortArray$pointwiseOp"); // java file
        return shortArrayPointwiseOpType_;
    }
    
    protected ClassType intArrayPointwiseOpType_;
	public ClassType IntArrayPointwiseOp() {
		if ( intArrayPointwiseOpType_ == null) {
			intArray(); // ensure that intArray is loaded.
			intArrayPointwiseOpType_ = load("x10.lang.intArray$pointwiseOp"); // java file
		}
		return intArrayPointwiseOpType_;
	}
    
    protected ClassType floatArrayPointwiseOpType_;
    public ClassType FloatArrayPointwiseOp() {
        if ( floatArrayPointwiseOpType_ == null)
            floatArrayPointwiseOpType_ = load("x10.lang.floatArray$pointwiseOp"); // java file
        return floatArrayPointwiseOpType_;
    }
    
	protected ClassType doubleArrayPointwiseOpType_;
	public ClassType DoubleArrayPointwiseOp() {
		if ( doubleArrayPointwiseOpType_ == null)
			doubleArrayPointwiseOpType_ = load("x10.lang.doubleArray$pointwiseOp"); // java file
		return doubleArrayPointwiseOpType_;
	}

	 /**
     * Factory method for ArrayTypes.
     * vj 05/23 -- I dont believe this is called anymore. Called only from jl.types.TypeSystem_c.
     */
    protected ArrayType arrayType(Position pos, Type type) {
    	return new X10ArrayType_c(this, pos, type);
    }

	
	public ReferenceType array(Type type, boolean isValueType, Expr distribution) {
        if (type.isBoolean())
            return booleanArray( isValueType, distribution);
        if (type.isChar())
            return charArray( isValueType, distribution);
        if (type.isByte())
            return byteArray( isValueType, distribution);
        if (type.isShort())
            return shortArray( isValueType, distribution);
        if (type.isInt())
			return intArray( isValueType, distribution );
        if (type.isFloat())
            return floatArray( isValueType, distribution);
        if (type.isDouble())
			return doubleArray( isValueType, distribution);
		if (type.isLong())
			return longArray( isValueType, distribution);
		List list = new LinkedList();
		list.add(type);
		return this.createParametricType(Position.COMPILER_GENERATED,
		        (X10ReferenceType) genericArray(isValueType, distribution),
		        list,
		        null);
	}
	
	public ReferenceType array( Type type,  Expr distribution ) {
        if (type.isBoolean())
            return booleanArray(distribution);
        if (type.isChar())
            return charArray(distribution);
        if (type.isByte())
            return byteArray(distribution);
        if (type.isShort())
            return shortArray(distribution);
        if (type.isInt())
            return intArray(distribution );
        if (type.isFloat())
            return floatArray(distribution);
        if (type.isDouble())
            return doubleArray(distribution);
        if (type.isLong())
            return longArray(distribution);
        List list = new LinkedList();
        list.add(type);
        return this.createParametricType(Position.COMPILER_GENERATED,
                (X10ReferenceType) genericArray(distribution),
                list,
                null);
	}
	
	public ReferenceType array(Type type, boolean isValue ) {
        if (type.isBoolean())
            return isValue ? booleanValueArray() : BooleanReferenceArray();
        if (type.isChar())
            return isValue ? charValueArray() : CharReferenceArray();
        if (type.isByte())
            return isValue ? byteValueArray() : ByteReferenceArray();
        if (type.isShort())
            return isValue ? shortValueArray() : ShortReferenceArray();
        if (type.isInt())
			return isValue ? intValueArray() : IntReferenceArray();
        if (type.isFloat())
            return isValue ? floatValueArray() : FloatReferenceArray();
        if (type.isDouble())
		    return isValue ? doubleValueArray() : DoubleReferenceArray();
		if (type.isLong())
		    return isValue ? longValueArray() : LongReferenceArray();
		List list = new LinkedList();
		list.add(type);
		return this.createParametricType(Position.COMPILER_GENERATED,
		        (X10ReferenceType) genericValueArray(),
		        list,
		        null);
	}
	
	public ReferenceType array(Type type ) {
        if (type.isBoolean())
            return booleanArray();
        if (type.isChar())
            return charArray();
        if (type.isByte())
            return byteArray();
        if (type.isShort())
            return shortArray();
        if (type.isInt())
		    return intArray( );
        if (type.isFloat())
            return floatArray();
        if (type.isDouble())
		    return doubleArray();
		if (type.isLong())
		    return longArray();
		List list = new LinkedList();
        list.add(type);
        return this.createParametricType(Position.COMPILER_GENERATED,
            (X10ReferenceType) genericArray(),
            list,
            null);
	}
	
    public ClassType booleanArray(boolean isValueType, Expr distribution ) {
        return 
        isValueType ? booleanValueArray( distribution ) : BooleanReferenceArray( distribution );
    }
    public ClassType booleanArray() {
        return booleanArray( null );
    }
    
    protected ClassType booleanArrayType_;
    public ClassType booleanArray( Expr distribution ) {     
        if ( booleanArrayType_ == null)
            booleanArrayType_ = load("x10.lang.booleanArray"); // java file
        
        return booleanArrayType_;
    }
    
    public ClassType booleanValueArray( ) {
        return booleanValueArray(null );
    }
    
    protected ClassType booleanValueArrayType_;
    // see the todo for intValueArray.
    public ClassType booleanValueArray( Expr distribution ) {
            return booleanArray( distribution );
    }
    
    public ClassType BooleanReferenceArray( ) {
        return BooleanReferenceArray(null );
    }
    
    protected ClassType booleanReferenceArrayType_;
    public ClassType BooleanReferenceArray( Expr distribution ) {
        if ( booleanReferenceArrayType_ == null)
            booleanReferenceArrayType_ = load("x10.lang.BooleanReferenceArray"); // java file
         //  return booleanReferenceArrayType_.setParameter( "distribution", distribution );
        return booleanReferenceArrayType_;
    }
    
    public ClassType charArray(boolean isValueType, Expr distribution ) {
        return 
        isValueType ? charValueArray( distribution ) : CharReferenceArray( distribution );
    }
    public ClassType charArray() {
        return charArray( null );
    }
    
    protected ClassType charArrayType_;
    public ClassType charArray( Expr distribution ) {     
        if ( charArrayType_ == null)
            charArrayType_ = load("x10.lang.charArray"); // java file
        
        return charArrayType_;
    }
    
    public ClassType charValueArray( ) {
        return charValueArray(null );
    }
    
    protected ClassType charValueArrayType_;
    // see the todo for intValueArray.
    public ClassType charValueArray( Expr distribution ) {
            return charArray( distribution );
    }
    
    public ClassType CharReferenceArray( ) {
        return CharReferenceArray(null );
    }
    
    protected ClassType charReferenceArrayType_;
    public ClassType CharReferenceArray( Expr distribution ) {
        if ( charReferenceArrayType_ == null)
            charReferenceArrayType_ = load("x10.lang.CharReferenceArray"); // java file
         //  return charReferenceArrayType_.setParameter( "distribution", distribution );
        return charReferenceArrayType_;
    }
    
    public ClassType byteArray(boolean isValueType, Expr distribution ) {
        return 
        isValueType ? byteValueArray( distribution ) : ByteReferenceArray( distribution );
    }
    public ClassType byteArray() {
        return byteArray( null );
    }
    
    protected ClassType byteArrayType_;
    public ClassType byteArray( Expr distribution ) {     
        if ( byteArrayType_ == null)
            byteArrayType_ = load("x10.lang.byteArray"); // java file
        
        return byteArrayType_;
    }
    
    public ClassType byteValueArray( ) {
        return byteValueArray(null );
    }
    
    protected ClassType byteValueArrayType_;
    // see the todo for intValueArray.
    public ClassType byteValueArray( Expr distribution ) {
            return byteArray( distribution );
    }
    
    public ClassType ByteReferenceArray( ) {
        return ByteReferenceArray(null );
    }
    
    protected ClassType byteReferenceArrayType_;
    public ClassType ByteReferenceArray( Expr distribution ) {
        if ( byteReferenceArrayType_ == null)
            byteReferenceArrayType_ = load("x10.lang.ByteReferenceArray"); // java file
         //  return byteReferenceArrayType_.setParameter( "distribution", distribution );
        return byteReferenceArrayType_;
    }
    
    public ClassType shortArray(boolean isValueType, Expr distribution ) {
        return 
        isValueType ? shortValueArray( distribution ) : ShortReferenceArray( distribution );
    }
    public ClassType shortArray() {
        return shortArray( null );
    }
    
    protected ClassType shortArrayType_;
    public ClassType shortArray( Expr distribution ) {     
        if ( shortArrayType_ == null)
            shortArrayType_ = load("x10.lang.shortArray"); // java file
        
        return shortArrayType_;
    }
    
    public ClassType shortValueArray( ) {
        return shortValueArray(null );
    }
    
    protected ClassType shortValueArrayType_;
    // see the todo for intValueArray.
    public ClassType shortValueArray( Expr distribution ) {
            return shortArray( distribution );
    }
    
    public ClassType ShortReferenceArray( ) {
        return ShortReferenceArray(null );
    }
    
    protected ClassType shortReferenceArrayType_;
    public ClassType ShortReferenceArray( Expr distribution ) {
        if ( shortReferenceArrayType_ == null)
            shortReferenceArrayType_ = load("x10.lang.ShortReferenceArray"); // java file
         //  return shortReferenceArrayType_.setParameter( "distribution", distribution );
        return shortReferenceArrayType_;
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

    protected X10ReferenceType genericArrayPointwiseOpType_;
    public X10ReferenceType GenericArrayPointwiseOp() {
        if ( genericArrayPointwiseOpType_ == null)
            genericArrayPointwiseOpType_
                = (X10ReferenceType) load("x10.lang.genericArray$pointwiseOp"); // java file
        return genericArrayPointwiseOpType_;
    }
    public ReferenceType GenericArrayPointwiseOp(Type typeParam) {
        List l = new LinkedList();
        l.add(typeParam);
        return new ParametricType_c(this,
                Position.COMPILER_GENERATED,
                GenericArrayPointwiseOp(),
                l,
                null);
    }
    public ClassType genericArray(boolean isValueType, Expr distribution ) {
        return 
        isValueType ? genericValueArray( distribution ) : GenericReferenceArray( distribution );
    }
    
    protected ClassType genericArrayType_;
    public ClassType genericArray( Expr distribution ) {
        if ( genericArrayType_ == null)
            genericArrayType_ = load("x10.lang.GenericReferenceArray"); // java file
         // FIXME: was genericArray
        // Also: I'd like to eliminate the plehora of Array classes
        // in the runtime - CG
        return genericArrayType_;
    }
    
    public ClassType genericArray() {
        return genericArray( null );
    }
    
    public ClassType genericValueArray( ) {
        return genericValueArray(null );
    }
    
    public ClassType genericValueArray( Expr distribution ) {
        return genericArray( distribution );
    }
    public ClassType GenericReferenceArray() {
        return GenericReferenceArray( null );
    }
    
    protected ClassType genericReferenceArrayType_;
    public ClassType GenericReferenceArray( Expr distribution ) {
        if ( genericReferenceArrayType_ == null)
            genericReferenceArrayType_ = load("x10.lang.GenericReferenceArray"); // java file
        // return genericReferenceArrayType_.setParameter( "distribution", distribution );
        return genericReferenceArrayType_;
    }
    
    public ClassType floatArray(boolean isValueType, Expr distribution ) {
        return 
        isValueType ? floatValueArray( distribution ) : FloatReferenceArray( distribution );
    }
    public ClassType floatArray() {
        return floatArray( null );
    }
    
    protected ClassType floatArrayType_;
    public ClassType floatArray( Expr distribution ) {     
        if ( floatArrayType_ == null)
            floatArrayType_ = load("x10.lang.floatArray"); // java file
        
        return floatArrayType_;
    }
    
    public ClassType floatValueArray( ) {
        return floatValueArray(null );
    }
    
    protected ClassType floatValueArrayType_;
    // see the todo for intValueArray.
    public ClassType floatValueArray( Expr distribution ) {
            return floatArray( distribution );
    }
    
    public ClassType FloatReferenceArray( ) {
        return FloatReferenceArray(null );
    }
    
    protected ClassType floatReferenceArrayType_;
    public ClassType FloatReferenceArray( Expr distribution ) {
        if ( floatReferenceArrayType_ == null)
            floatReferenceArrayType_ = load("x10.lang.FloatReferenceArray"); // java file
         //  return floatReferenceArrayType_.setParameter( "distribution", distribution );
        return floatReferenceArrayType_;
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
