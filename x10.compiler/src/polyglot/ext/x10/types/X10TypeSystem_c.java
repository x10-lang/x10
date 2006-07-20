package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.ast.Expr;
import polyglot.ext.jl.types.MethodInstance_c;
import polyglot.ext.jl.types.TypeSystem_c;
import polyglot.frontend.Source;
import polyglot.main.Report;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.ConstructorInstance;
import polyglot.types.Flags;
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
public class X10TypeSystem_c extends TypeSystem_c implements X10TypeSystem {

	private static X10TypeSystem_c factory = null;
	public static X10TypeSystem_c getFactory() {
		return factory;
	}
    public static X10TypeSystem x10TypeSystem = null;
    public static void setTypeSystem(X10TypeSystem x) { x10TypeSystem = x;}
    public static X10TypeSystem getTypeSystem() { return x10TypeSystem;}
    
	public X10TypeSystem_c() {
		super();
		factory = this;
		unknownType = new X10UnknownType_c(this);
	}

	private final static Set primitiveTypeNames= new HashSet();

	static {
	    primitiveTypeNames.add("boolean");
	    primitiveTypeNames.add("byte");
	    primitiveTypeNames.add("char");
	    primitiveTypeNames.add("short");
	    primitiveTypeNames.add("int");
	    primitiveTypeNames.add("long");
	    primitiveTypeNames.add("float");
	    primitiveTypeNames.add("double");
	}

	public boolean isPrimitiveTypeName(String name) {
	    return primitiveTypeNames.contains(name);
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

	private HashMap nullableMap = new HashMap();
	/**
	 * Return a nullable type based on a given type.
	 * TODO: rename this to nullableType() -- the name is misleading.
	 */
	public NullableType createNullableType(Position pos, X10Type type) {
		NullableType t = (NullableType) nullableMap.get(type);
		if (t == null) {
			t = NullableType_c.makeNullable(this, pos, type);
			nullableMap.put(type, t);
		}
		return t;
	}

	public FutureType createFutureType(Position pos, Type type) {
		return new FutureType_c(this, pos, type);
	}

	/*public ParametricType createParametricType(Position pos,
											   X10ReferenceType type,
											   List typeparameters,
											   DepParameterExpr expr)
	{
		return new ParametricType_c(this, pos, type, typeparameters, expr);
	}*/

	protected UnknownType createUnknownType() {
		return unknownType;
	}

	/**
	 * [IP] TODO: this should be a special CodeInstance instead
	 */
	protected CodeInstance asyncCodeInstance_;
	public CodeInstance asyncCodeInstance() {
		if (asyncCodeInstance_ == null)
			asyncCodeInstance_ =
				new MethodInstance_c(this, Position.COMPILER_GENERATED,
						Runtime(), Public(), VOID_, "$dummyAsync$",
						Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		return asyncCodeInstance_;
	}

	protected NullType createNull() {
		return new X10NullType_c(this);
	}

	public ParsedClassType createClassType(LazyClassInitializer init, Source fromSource) {
        if (Report.should_report("debug",3))
        Report.report(3, "X10TypeSystem_c: Creating Class fromSource =|" + fromSource + "|");
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
		if (x10ObjectType_ == null)
			x10ObjectType_ = load("x10.lang.Object"); // java file
		return x10ObjectType_;
	}

	protected ClassType placeType_;
	public ClassType place() {
		if (placeType_ == null)
			placeType_ = load("x10.lang.place"); // java file
		return placeType_;
	}

	protected ClassType regionType_;
	public ClassType region() {
		if (regionType_ == null)
			regionType_ = load("x10.lang.region"); // java file
		return regionType_;
	}

	protected ClassType pointType_;
	public ClassType point() {
		if (pointType_ == null)
			pointType_ = load("x10.lang.point"); // java file
		return pointType_;
	}

	protected ClassType valueType_;
	public ClassType value() {
		if (valueType_ == null)
			valueType_ = load("x10.lang.ValueType"); // java file
		return valueType_;
	}

	protected ClassType distributionType_;
	public ClassType distribution() {
		if (distributionType_ == null)
			distributionType_ = load("x10.lang.dist"); // java file
		return distributionType_;
	}

	protected ClassType activityType_;
	public ClassType Activity() {
		if (activityType_ == null)
			activityType_ = load("x10.lang.Activity"); // java file
		return activityType_;
	}

	protected ClassType futureActivityType_;
	public ClassType FutureActivity() {
		if (futureActivityType_ == null)
			futureActivityType_ = load("x10.lang.Activity$Expr"); // java file
		return futureActivityType_;
	}

	protected ClassType arrayType_;
	public ClassType array() {
		if (arrayType_ == null)
			arrayType_ = load("x10.lang.array"); // java file
		return arrayType_;
	}

	protected ClassType clockType_;
	public ClassType clock() {
		if (clockType_ == null)
			clockType_ = load("x10.lang.clock"); // java file
		return clockType_;
	}

	protected ClassType runtimeType_;
	public ClassType Runtime() {
		if (runtimeType_ == null)
			runtimeType_ = load("x10.lang.Runtime"); // java file
		return runtimeType_;
	}

	protected ClassType booleanArrayPointwiseOpType_;
	public ClassType BooleanArrayPointwiseOp() {
		if (booleanArrayPointwiseOpType_ == null)
			booleanArrayPointwiseOpType_ = load("x10.lang.booleanArray$pointwiseOp"); // java file
		return booleanArrayPointwiseOpType_;
	}

	protected ClassType charArrayPointwiseOpType_;
	public ClassType CharArrayPointwiseOp() {
		if (charArrayPointwiseOpType_ == null)
			charArrayPointwiseOpType_ = load("x10.lang.charArray$pointwiseOp"); // java file
		return charArrayPointwiseOpType_;
	}

	protected ClassType byteArrayPointwiseOpType_;
	public ClassType ByteArrayPointwiseOp() {
		if (byteArrayPointwiseOpType_ == null)
			byteArrayPointwiseOpType_ = load("x10.lang.byteArray$pointwiseOp"); // java file
		return byteArrayPointwiseOpType_;
	}

	protected ClassType shortArrayPointwiseOpType_;
	public ClassType ShortArrayPointwiseOp() {
		if (shortArrayPointwiseOpType_ == null)
			shortArrayPointwiseOpType_ = load("x10.lang.shortArray$pointwiseOp"); // java file
		return shortArrayPointwiseOpType_;
	}

	protected ClassType intArrayPointwiseOpType_;
	public ClassType IntArrayPointwiseOp() {
		if (intArrayPointwiseOpType_ == null) {
			intArray(); // ensure that intArray is loaded.
			intArrayPointwiseOpType_ = load("x10.lang.intArray$pointwiseOp"); // java file
		}
		return intArrayPointwiseOpType_;
	}

	protected ClassType floatArrayPointwiseOpType_;
	public ClassType FloatArrayPointwiseOp() {
		if (floatArrayPointwiseOpType_ == null)
			floatArrayPointwiseOpType_ = load("x10.lang.floatArray$pointwiseOp"); // java file
		return floatArrayPointwiseOpType_;
	}

	protected ClassType doubleArrayPointwiseOpType_;
	public ClassType DoubleArrayPointwiseOp() {
		if (doubleArrayPointwiseOpType_ == null)
			doubleArrayPointwiseOpType_ = load("x10.lang.doubleArray$pointwiseOp"); // java file
		return doubleArrayPointwiseOpType_;
	}

	/**
	 * Factory method for ArrayTypes.
	 * Called only from jl.types.TypeSystem_c.
	 */
	protected ArrayType arrayType(Position pos, Type type) {
		return new X10ArrayType_c(this, pos, type);
	}

	public ReferenceType array(Type type, boolean isValueType, Expr distribution) {
		if (type.isBoolean()) return booleanArray(isValueType, distribution);
		if (type.isChar())    return charArray(isValueType, distribution);
		if (type.isByte())    return byteArray(isValueType, distribution);
		if (type.isShort())   return shortArray(isValueType, distribution);
		if (type.isInt())     return intArray(isValueType, distribution);
		if (type.isFloat())   return floatArray(isValueType, distribution);
		if (type.isDouble())  return doubleArray(isValueType, distribution);
		if (type.isLong())    return longArray(isValueType, distribution);
        List list = new LinkedList();
        list.add(type);
        ReferenceType result = genericArray(isValueType, distribution, list);
        return result;
	}

	public ReferenceType array(Type type, Expr distribution) {
        return array(type, false, distribution);
	}

	public ReferenceType array(Type type, boolean isValue) {
        return array(type, isValue, null);
	}

	public ReferenceType array(Type type) {
        return array(type, false, null);
	}

	public ClassType booleanArray(boolean isValueType, Expr distribution) {
		return isValueType
			? booleanValueArray(distribution)
			: BooleanReferenceArray(distribution);
	}

	public ClassType booleanArray() {
		return booleanArray(null);
	}

	protected X10ParsedClassType booleanArrayType_;
	public ClassType booleanArray(Expr distribution) {
		if (booleanArrayType_ == null)
			booleanArrayType_ = (X10ParsedClassType) load("x10.lang.booleanArray"); // java file

      
        return booleanArrayType_;
	}

	private ClassType booleanValueArray() {
		return booleanValueArray(null);
	}

	protected ClassType booleanValueArrayType_;
	// see the TODO for intValueArray.
	private ClassType booleanValueArray(Expr distribution) {
		return booleanArray(distribution);
	}

	private ClassType BooleanReferenceArray() {
		return BooleanReferenceArray(null);
	}

	protected X10ParsedClassType booleanReferenceArrayType_;
	private ClassType BooleanReferenceArray(Expr distribution) {
		if (booleanReferenceArrayType_ == null)
			booleanReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.BooleanReferenceArray"); // java file
            return booleanReferenceArrayType_;
	}

	public ClassType charArray(boolean isValueType, Expr distribution) {
		return isValueType
			? charValueArray(distribution)
			: CharReferenceArray(distribution);
	}

	public ClassType charArray() {
		return charArray(null);
	}

	protected X10ParsedClassType charArrayType_;
	public ClassType charArray(Expr distribution) {
		if (charArrayType_ == null)
			charArrayType_ = (X10ParsedClassType) load("x10.lang.charArray"); // java file
         
            return charArrayType_;
	}

	private ClassType charValueArray() {
		return charValueArray(null);
	}

	protected ClassType charValueArrayType_;
	// see the TODO for intValueArray.
	private ClassType charValueArray(Expr distribution) {
		return charArray(distribution);
	}

	private ClassType CharReferenceArray() {
		return CharReferenceArray(null);
	}

	protected X10ParsedClassType charReferenceArrayType_;
	private ClassType CharReferenceArray(Expr distribution) {
		if (charReferenceArrayType_ == null)
			charReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.CharReferenceArray"); // java file
       
     
        return charReferenceArrayType_;
	}

	public ClassType byteArray(boolean isValueType, Expr distribution) {
		return isValueType
			? byteValueArray(distribution)
			: ByteReferenceArray(distribution);
	}

	public ClassType byteArray() {
		return byteArray(null);
	}

	protected X10ParsedClassType byteArrayType_;
	public ClassType byteArray(Expr distribution) {
		if (byteArrayType_ == null)
			byteArrayType_ = (X10ParsedClassType) load("x10.lang.byteArray"); // java file
        
         return byteArrayType_;
	}

	private ClassType byteValueArray() {
		return byteValueArray(null);
	}

	protected ClassType byteValueArrayType_;
	// see the TODO for intValueArray.
	private ClassType byteValueArray(Expr distribution) {
		return byteArray(distribution);
	}

	private ClassType ByteReferenceArray() {
		return ByteReferenceArray(null);
	}

	protected X10ParsedClassType byteReferenceArrayType_;
	private ClassType ByteReferenceArray(Expr distribution) {
		if (byteReferenceArrayType_ == null)
			byteReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.ByteReferenceArray"); // java file
         return byteReferenceArrayType_;
	}

	public ClassType shortArray(boolean isValueType, Expr distribution) {
		return isValueType
			? shortValueArray(distribution)
			: ShortReferenceArray(distribution);
	}

	public ClassType shortArray() {
		return shortArray(null);
	}

	protected X10ParsedClassType shortArrayType_;
	public ClassType shortArray(Expr distribution) {
		if (shortArrayType_ == null)
			shortArrayType_ = (X10ParsedClassType) load("x10.lang.shortArray"); // java file

        
            
            return shortArrayType_;
	}

	private ClassType shortValueArray() {
		return shortValueArray(null);
	}

	protected ClassType shortValueArrayType_;
	// see the TODO for intValueArray.
	private ClassType shortValueArray(Expr distribution) {
		return shortArray(distribution);
	}

	private ClassType ShortReferenceArray() {
		return ShortReferenceArray(null);
	}

	protected X10ParsedClassType shortReferenceArrayType_;
	public ClassType ShortReferenceArray(Expr distribution) {
		if (shortReferenceArrayType_ == null)
			shortReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.ShortReferenceArray"); // java file
        return shortReferenceArrayType_;
	}

	public ClassType intArray(boolean isValueType, Expr distribution) {
		return isValueType
			? intValueArray(distribution)
			: IntReferenceArray(distribution);
	}

	protected X10ParsedClassType intArrayType_;
	public ClassType intArray(Expr distribution) {
		if (intArrayType_ == null)
			intArrayType_ = (X10ParsedClassType) load("x10.lang.intArray"); // java file
        return intArrayType_;
	}

	public ClassType intArray() {
		return intArray(null);
	}

	private ClassType intValueArray() {
		return intValueArray(null);
	}

	protected ClassType intValueArrayType_;
	private ClassType intValueArray(Expr distribution) {
		// vj: should really load x10.lang.intValueArray, but we are cheating...
		// there is no difference in the public type (= methods) of x10.lang.intValueArray
		// and x10.lang.intArray, so we reuse the implementation of x10.lang.intArray.
		// The backend does not do anything special about value classes right now anyway,
		// so there is no need to tell the implementation that this is a value class.
		// This hack will need to be fixed once the compiler starts checking that
		// value classes can only be extended by value classes.
		// [IP] FIXME
		return intArray(distribution);
	}

	private ClassType IntReferenceArray() {
		return IntReferenceArray(null);
	}

	protected X10ParsedClassType intReferenceArrayType_;
	private ClassType IntReferenceArray(Expr distribution) {
		if (intReferenceArrayType_ == null)
			intReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.IntReferenceArray"); // java file
		// return intReferenceArrayType_.setParameter("distribution", distribution);
         X10ClassType result = intReferenceArrayType_;
         return result;
	}

	protected X10ParsedClassType longArrayPointwiseOpType_;
	public ClassType LongArrayPointwiseOp() {
		if (longArrayPointwiseOpType_ == null)
			longArrayPointwiseOpType_ = (X10ParsedClassType) load("x10.lang.longArray$pointwiseOp"); // java file
          X10ClassType result = longArrayPointwiseOpType_;
       
          return result;
	}
      
      
	public ClassType longArray(boolean isValueType, Expr distribution) {
		return isValueType
			? longValueArray(distribution)
			: LongReferenceArray(distribution);
	}

	protected X10ParsedClassType longArrayType_;
	public ClassType longArray(Expr distribution) {
		if (longArrayType_ == null)
			longArrayType_ = (X10ParsedClassType) load("x10.lang.longArray"); // java file
          X10ClassType result = longArrayType_;
            return result;
	}

	public ClassType longArray() {
		return longArray(null);
	}

	private ClassType longValueArray() {
		return longValueArray(null);
	}

	private ClassType longValueArray(Expr distribution) {
		return longArray(distribution);
	}

	private ClassType LongReferenceArray() {
		return LongReferenceArray(null);
	}

	protected X10ParsedClassType longReferenceArrayType_;
	public ClassType LongReferenceArray(Expr distribution) {
		if (longReferenceArrayType_ == null)
			longReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.LongReferenceArray"); // java file
		// return longReferenceArrayType_.setParameter("distribution", distribution);
        X10ClassType result = longReferenceArrayType_;
        return result;
	}

	protected X10ReferenceType genericArrayPointwiseOpType_;
	public X10ReferenceType GenericArrayPointwiseOp() {
		if (genericArrayPointwiseOpType_ == null)
			genericArrayPointwiseOpType_
				= (X10ReferenceType) load("x10.lang.genericArray$pointwiseOp"); // java file
		X10ReferenceType result = genericArrayPointwiseOpType_;
        return genericArrayPointwiseOpType_;
	}

	public ReferenceType GenericArrayPointwiseOp(Type typeParam) {
		List l = new LinkedList();
		l.add(typeParam);
        
        X10ReferenceType result = (X10ReferenceType) GenericArrayPointwiseOp().makeVariant(null, l);
        return result;
	}

	public ClassType genericArray(boolean isValueType, Expr distribution, List types) {
		return isValueType
			? genericValueArray(distribution, types)
			: GenericReferenceArray(distribution, types);
	}

	protected X10ParsedClassType genericArrayType_;
	public ClassType genericArray(Expr distribution, List typeParams) {
		if (genericArrayType_ == null) {
			genericArrayType_ = (X10ParsedClassType) load("x10.lang.GenericReferenceArray"); // java file
        }
		// FIXME: was genericArray
		// Also: I'd like to eliminate the plehora of Array classes
		// in the runtime - CG
        //TODO: Convert distribution to a depclause.
        ClassType result = (ClassType) genericArrayType_.makeVariant(null, typeParams);
		return result;
	}

	public ClassType genericArray() {
		return genericArray(null, new LinkedList());
	}

	private ClassType genericValueArray() {
		return genericValueArray(null, new LinkedList());
	}

	private ClassType genericValueArray(Expr distribution, List types) {
		return genericArray(distribution, types);
	}

	private ClassType GenericReferenceArray() {
		return GenericReferenceArray(null, new LinkedList());
	}

	protected X10ParsedClassType genericReferenceArrayType_;
	private ClassType GenericReferenceArray(Expr distribution, List typeParams) {
	    if (genericReferenceArrayType_ == null) {
	        genericReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.GenericReferenceArray"); // java file
        }
	    // return a variant of genericReferenceArrayType_.
        // TODO: Also need to pass along distribution.
	    X10ClassType result = (X10ClassType) genericReferenceArrayType_.makeVariant(null, typeParams);
	    return result;
	}

	public ClassType floatArray(boolean isValueType, Expr distribution) {
		return isValueType
			? floatValueArray(distribution)
			: FloatReferenceArray(distribution);
	}

	public ClassType floatArray() {
		return floatArray(null);
	}

	protected X10ParsedClassType floatArrayType_;
	public ClassType floatArray(Expr distribution) {
		if (floatArrayType_ == null)
			floatArrayType_ = (X10ParsedClassType) load("x10.lang.floatArray"); // java file

          // TODO: Also need to pass along distribution.
        
		X10ClassType result = floatArrayType_;
		return result;
	}

	public ClassType floatValueArray() {
		return floatValueArray(null);
	}

	protected ClassType floatValueArrayType_;
	// see the TODO for intValueArray.
	private ClassType floatValueArray(Expr distribution) {
		return floatArray(distribution);
	}

	private ClassType FloatReferenceArray() {
		return FloatReferenceArray(null);
	}

	protected X10ParsedClassType floatReferenceArrayType_;
	private ClassType FloatReferenceArray(Expr distribution) {
		if (floatReferenceArrayType_ == null)
			floatReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.FloatReferenceArray"); // java file
        X10ClassType result = floatReferenceArrayType_;
        // TODO: Also need to pass along distribution.
        // result.setDepClause(depClauseFromDist(distribution));
    return result;
	}

	public ClassType doubleArray(boolean isValueType, Expr distribution) {
		return isValueType
			? doubleValueArray(distribution)
			: DoubleReferenceArray(distribution);
	}

	public ClassType doubleArray() {
		return doubleArray(null);
	}

	protected X10ParsedClassType doubleArrayType_;
	public ClassType doubleArray(Expr distribution) {
		if (doubleArrayType_ == null)
			doubleArrayType_ = (X10ParsedClassType) load("x10.lang.doubleArray"); // java file

        X10ClassType result = doubleArrayType_;
        // TODO: Also need to pass along distribution.
       
        // result.setDepClause(depClauseFromDist(distribution));
    return result;
	}

	private ClassType doubleValueArray() {
		return doubleValueArray(null);
	}

	protected X10ClassType doubleValueArrayType_;
	// see the todo for intValueArray.
	private ClassType doubleValueArray(Expr distribution) {
			return doubleArray(distribution);
	}

	private ClassType DoubleReferenceArray() {
		return DoubleReferenceArray(null);
	}

	protected X10ParsedClassType doubleReferenceArrayType_;
	private ClassType DoubleReferenceArray(Expr distribution) {
		if (doubleReferenceArrayType_ == null)
			doubleReferenceArrayType_ = (X10ParsedClassType) load("x10.lang.DoubleReferenceArray"); // java file
        X10ClassType result = doubleReferenceArrayType_;
        // TODO: Also need to pass along distribution.
       
        // result.setDepClause(depClauseFromDist(distribution));
    return result;
	}

	protected ClassType indexableType_ = null;
	public ClassType Indexable() {
		if (indexableType_ == null)
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
				 i.hasNext(); )
			{
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

		for (Iterator i = ci.container().methods().iterator(); i.hasNext(); ) {
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

	// RMF 11/1/2005 - Not having the "static" qualifier on interfaces causes problems,
	// e.g. for New_c.disambiguate(AmbiguityRemover), which assumes that instantiating
	// non-static types requires a "this" qualifier expression.
	// [IP] FIXME: why does the above matter when we supply the bits?
	public Flags flagsForBits(int bits) {
		Flags sf = super.flagsForBits(bits);
		if (sf.isInterface()) return sf.Static();
		return sf;
	}

	public PropertyInstance propertyInstance(Position pos,
	        ReferenceType container, Flags flags,
	        Type type, String name) {
	    assert_(container);
	    assert_(type);
	    return new PropertyInstance_c(this, pos, container, flags, type, name);
	}

//	/**
//	 * Allow all explicit casts (for experimental purposes).
//	 **/
//	public boolean isCastValid(Type fromType, Type toType) {
//		return true || super.isCastValid(fromType, toType);
//	}

} // end of X10TypeSystem_c

