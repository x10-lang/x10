/*
 * Created on Oct 1, 2004
 */

package polyglot.ext.x10.types;

import polyglot.ast.Expr;
import polyglot.ext.x10.ast.GenParameterExpr;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.MethodInstance;
import polyglot.types.PrimitiveType;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;


/**
 * Parts of this code are taken from the pao extension in the polyglot framework.
 * 
 * @author Christoph von Praun
 * @author vj
 */

public interface X10TypeSystem extends TypeSystem {


    /**
     * Return a reference array of <code>base</code> type.
     */
    // X10ArrayType x10arrayOf(Position pos, Type base);

    /**
     * Return an array of <code>base</code> type -- a
     * <code>value</code> array if <code>isValue==true</code>, and a
     * <code>reference</code> array otherwise.
     */
    // X10ArrayType x10arrayOf(Position pos, Type base, boolean isValue);

    /**
     * Return a reference array of <code>base</code> type with a
     * dependent parameter, expr.
     */
    // X10ArrayType x10arrayOf(Position pos, Type base, DepParameterExpr expr);
    /**
     * Return an array of <code>base</code> type -- a <code>value</code> array
     * if <code>isValue==true</code>, and a <code>reference</code> array
     * otherwise -- with a dependent parameter, expr.
     */
    // X10ArrayType x10arrayOf(Position pos, Type base, boolean isValue, DepParameterExpr expr);

    NullableType createNullableType( Position p, ReferenceType t);
    FutureType createFutureType( Position p, Type t);
    ParametricType createParametricType ( Position pos,
                X10ReferenceType type,
                GenParameterExpr texpr,
                DepParameterExpr expr ) ;
    ClassType X10Object();
    ClassType place();
    ClassType region();
    ClassType point();
    ClassType distribution();
    ClassType Activity();
    ClassType FutureActivity();
    ClassType array();
    ClassType clock();
    ClassType Runtime();
    ClassType IntArrayPointwiseOp();
    ClassType DoubleArrayPointwiseOp();
    ClassType LongArrayPointwiseOp();
    ReferenceType GenericArrayPointwiseOp(X10ReferenceType baseType);
    
 
  
    /**Provide a generic type constructor for arrays:
     * Behaves the same as <Type>Array( isValueType, distribution ).
     * May throw an Error if not implemented for the given type.
     * 1/13/2005 -- implemented for int and double only.
     * TODO: implement for all native types.
     */
    
    ReferenceType array( Type type, boolean isValueType, Expr distribution);
    
    /**Provide a generic type constructor for arrays:
     * Behaves the same as <Type>Array( distribution ).
     * May throw an Error if not implemented for the given type.
     * 1/13/2005 -- implemented for int and double only.
     * TODO: implement for all native types.
     */
    ReferenceType array(Type type,  Expr distribution);
    
    /**Provide a generic type constructor for arrays:
     * Behaves the same as <Type>Array( isValueType ).
     * May throw an Error if not implemented for the given type.
     * 1/13/2005 -- implemented for int and double only.
     * TODO: implement for all native types.
     */
    ReferenceType array(Type type,  boolean isValueType );
    
    /**Provide a generic type constructor for arrays:
     * Behaves the same as <Type>Array( ).
     * May throw an Error if not implemented for the given type.
     * 1/13/2005 -- implemented for int and double only.
     * TODO: implement for all native types.
     */
    ReferenceType array(Type type);
    
    /** Return the parametric type of all X10 intArrays with the
	 * given values for the isValueType and distribution parameters.
	 * @param isValueType -- true if this type is a value type.
	 * @param distribution  -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType intArray(boolean isValueType, Expr distribution );
	
	/** Return the parametric type of all X10 int arrays with the
	 * given  distribution. The class returned is a superclass of
	 * intValueArray and IntReferenceArray.
	 * @param distribution  -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType intArray( Expr distribution );
	
	/** Return the parametric type of all X10 int arrays with no
	 *  constraints on the distribution.The class returned is a superclass of
	 * intValueArray and IntReferenceArray.
	 * @return -- the ClassType object
	 */
	ClassType intArray(); 
	
	/** Return the parametric type of all X10 int value arrays with the
	 * given  distribution. 
	 * @param distribution  -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType intValueArray( Expr distribution );
	
	/**Return the parametric type of all X10 int value arrays with no constraints on the
	 * given  distribution. 
	 * @return -- the ClassType object
	 */
	ClassType intValueArray(); 
	
	/** Return the parametric type of all X10 int reference arrays with the
	 * given  distribution. 
	 * @param distribution  -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
    ClassType IntReferenceArray( Expr distribution );
    
    /**Return the parametric type of all X10 int reference arrays with no constraints on the
	 * given  distribution. 
	 *
	 * @return -- the ClassType object
	 */
    ClassType IntReferenceArray();
    
    /** Return the parametric type of all X10 doubleArrays with the
	 * given values for the isValueType and distribution parameters.
	 * @param isValueType -- true if this type is a value type.
	 * @param distribution  -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType doubleArray(boolean isValueType, Expr distribution );
	
	/** Return the parametric type of all X10 double arrays with the
	 * given  distribution. The class returned is a superclass of
	 * doubleValueArray and DoubleReferenceArray.
	 * @param distribution  -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType doubleArray( Expr distribution );
	
	/** Return the parametric type of all X10 double arrays with no
	 *  constraints on the distribution.The class returned is a superclass of
	 * doubleValueArray and DoubleReferenceArray.
	 * @return -- the ClassType object
	 */
	ClassType doubleArray(); 
	
	/** Return the parametric type of all X10 double value arrays with the
	 * given  distribution. 
	 * @param distribution  -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType doubleValueArray( Expr distribution );
	
	/**Return the parametric type of all X10 double value arrays with no constraints on the
	 * given  distribution. 
	 * @return -- the ClassType object
	 */
	ClassType doubleValueArray(); 
	
	/** Return the parametric type of all X10 double reference arrays with the
	 * given  distribution. 
	 * @param distribution  -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
    ClassType DoubleReferenceArray( Expr distribution );
    
    /**Return the parametric type of all X10 double reference arrays with no constraints on the
	 * given  distribution. 
	 *
	 * @return -- the ClassType object
	 */
    ClassType DoubleReferenceArray();
    
    /** Return the parametric type of all X10 longArrays with the
	 * given values for the isValueType and distribution parameters.
	 * @param isValueType -- true if this type is a value type.
	 * @param distribution  -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType longArray(boolean isValueType, Expr distribution );
	
	/** Return the parametric type of all X10 long arrays with the
	 * given  distribution. The class returned is a superclass of
	 * longValueArray and DoubleReferenceArray.
	 * @param distribution  -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType longArray( Expr distribution );
	
	/** Return the parametric type of all X10 long arrays with no
	 *  constraints on the distribution.The class returned is a superclass of
	 * longValueArray and DoubleReferenceArray.
	 * @return -- the ClassType object
	 */
	ClassType longArray(); 
	
	/** Return the parametric type of all X10 long value arrays with the
	 * given  distribution. 
	 * @param distribution  -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType longValueArray( Expr distribution );
	
	/**Return the parametric type of all X10 long value arrays with no constraints on the
	 * given  distribution. 
	 * @return -- the ClassType object
	 */
	ClassType longValueArray(); 
	
	/** Return the parametric type of all X10 long reference arrays with the
	 * given  distribution. 
	 * @param distribution  -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
    ClassType LongReferenceArray( Expr distribution );
    
    /**Return the parametric type of all X10 long reference arrays with no constraints on the
	 * given  distribution. 
	 *
	 * @return -- the ClassType object
	 */
    ClassType LongReferenceArray();
    
    // TODO: Add similar support for arrays of long and boolean.
    
    /** Return the ClassType object for the x10.lang.Indexable interface.
     * 
     */
    ClassType Indexable();
    
    /** Return the method instance for runtime.Primitive.equals */
    public MethodInstance primitiveEquals();

    /** Return the method instance for runtime.T.tValue() */
    public MethodInstance getter(PrimitiveType t);

    /** Return the constructor instance for runtime.T.T(t) */
    public ConstructorInstance wrapper(PrimitiveType t);

    /** Return boxed type runtime.T for primitive t. */
    public Type boxedType(PrimitiveType t);
    
    
} // end of X10TypeSystem
