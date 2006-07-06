/*
 * Created on Oct 1, 2004
 */
package polyglot.ext.x10.types;

import polyglot.ast.Expr;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.ConstructorInstance;
import polyglot.types.Flags;
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

	NullableType createNullableType(Position p, X10Type t);
	FutureType createFutureType(Position p, Type t);
	/*ParametricType createParametricType(Position pos,
										X10ReferenceType type,
										List typeparameters,
										DepParameterExpr expr);*/
	ClassType X10Object();
	ClassType place();
	ClassType region();
	ClassType point();
	ClassType distribution();
	ClassType Activity();
	ClassType FutureActivity();
	ClassType array();
	ClassType clock();
	ClassType value();
	ClassType Runtime();
	ClassType BooleanArrayPointwiseOp();
	ClassType CharArrayPointwiseOp();
	ClassType ByteArrayPointwiseOp();
	ClassType ShortArrayPointwiseOp();
	ClassType IntArrayPointwiseOp();
	ClassType DoubleArrayPointwiseOp();
	ClassType FloatArrayPointwiseOp();
	ClassType LongArrayPointwiseOp();
	ReferenceType GenericArrayPointwiseOp(Type baseType);

	CodeInstance asyncCodeInstance();

	/**
	 * Provide a generic type constructor for arrays:
	 * Behaves the same as <Type>Array(isValueType, distribution).
	 * May throw an Error if not implemented for the given type.
	 * 1/13/2005 -- implemented for int and double only.
	 * TODO: implement for all native types.
	 */

	ReferenceType array(Type type, boolean isValueType, Expr distribution);

	/**
	 * Provide a generic type constructor for arrays:
	 * Behaves the same as <Type>Array(distribution).
	 * May throw an Error if not implemented for the given type.
	 * 1/13/2005 -- implemented for int and double only.
	 * TODO: implement for all native types.
	 */
	ReferenceType array(Type type, Expr distribution);

	/**
	 * Provide a generic type constructor for arrays:
	 * Behaves the same as <Type>Array(isValueType).
	 * May throw an Error if not implemented for the given type.
	 * 1/13/2005 -- implemented for int and double only.
	 * TODO: implement for all native types.
	 */
	ReferenceType array(Type type, boolean isValueType);

	/**
	 * Provide a generic type constructor for arrays:
	 * Behaves the same as <Type>Array().
	 * May throw an Error if not implemented for the given type.
	 * 1/13/2005 -- implemented for int and double only.
	 * TODO: implement for all native types.
	 */
	ReferenceType array(Type type);

	ClassType booleanArray(boolean isValueType, Expr distribution);
	ClassType booleanArray(Expr distribution);
	ClassType booleanArray();

	ClassType charArray(boolean isValueType, Expr distribution);
	ClassType charArray(Expr distribution);
	ClassType charArray();

	ClassType byteArray(boolean isValueType, Expr distribution);
	ClassType byteArray(Expr distribution);
	ClassType byteArray();

	ClassType shortArray(boolean isValueType, Expr distribution);
	ClassType shortArray(Expr distribution);
	ClassType shortArray();

	/**
	 * Return the parametric type of all X10 intArrays with the
	 * given values for the isValueType and distribution parameters.
	 * @param isValueType -- true if this type is a value type.
	 * @param distribution -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType intArray(boolean isValueType, Expr distribution);

	/**
	 * Return the parametric type of all X10 int arrays with the
	 * given distribution. The class returned is a superclass of
	 * intValueArray and IntReferenceArray.
	 * @param distribution -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType intArray(Expr distribution);

	/**
	 * Return the parametric type of all X10 int arrays with no
	 * constraints on the distribution.The class returned is a superclass of
	 * intValueArray and IntReferenceArray.
	 * @return -- the ClassType object
	 */
	ClassType intArray();

	ClassType floatArray(boolean isValueType, Expr distribution);
	ClassType floatArray(Expr distribution);
	ClassType floatArray();

	/**
	 * Return the parametric type of all X10 doubleArrays with the
	 * given values for the isValueType and distribution parameters.
	 * @param isValueType -- true if this type is a value type.
	 * @param distribution -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType doubleArray(boolean isValueType, Expr distribution);

	/**
	 * Return the parametric type of all X10 double arrays with the
	 * given distribution. The class returned is a superclass of
	 * doubleValueArray and DoubleReferenceArray.
	 * @param distribution -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType doubleArray(Expr distribution);

	/**
	 * Return the parametric type of all X10 double arrays with no
	 * constraints on the distribution.The class returned is a superclass of
	 * doubleValueArray and DoubleReferenceArray.
	 * @return -- the ClassType object
	 */
	ClassType doubleArray();

	/**
	 * Return the parametric type of all X10 longArrays with the
	 * given values for the isValueType and distribution parameters.
	 * @param isValueType -- true if this type is a value type.
	 * @param distribution -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType longArray(boolean isValueType, Expr distribution);

	/**
	 * Return the parametric type of all X10 long arrays with the
	 * given distribution. The class returned is a superclass of
	 * longValueArray and DoubleReferenceArray.
	 * @param distribution -- the underlying distribution for this type. May be null.
	 * @return -- the ClassType object
	 */
	ClassType longArray(Expr distribution);

	/**
	 * Return the parametric type of all X10 long arrays with no
	 * constraints on the distribution.The class returned is a superclass of
	 * longValueArray and DoubleReferenceArray.
	 * @return -- the ClassType object
	 */
	ClassType longArray();

	// TODO: Add similar support for arrays of long and boolean.

	/**
	 * Return the ClassType object for the x10.lang.Indexable interface.
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
    
	/** Create a property instance. A property is a public final instance field
	 * that can be used to construct deptypes.
	 * @param pos Position of the field.
	 * @param container Containing type of the field.
	 * @param flags The field's flags.
	 * @param type The field's type.
	 * @param name The field's name.
	 */
    PropertyInstance propertyInstance(Position pos, ReferenceType container,
                                Flags flags, Type type, String name);


} // end of X10TypeSystem

