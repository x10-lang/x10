/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 1, 2004
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.constr.C_Here_c;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.TypeTranslator;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import polyglot.util.Position;

/**
 * Parts of this code are taken from the pao extension in the polyglot framework.
 *
 * @author Christoph von Praun
 * @author vj
 */
public interface X10TypeSystem extends TypeSystem {

	/** Instantiate the properties of baseType with the listed expressions. */
	X10ClassType instantiateType(X10ClassType baseType, List<Expr> args, X10NodeFactory nf);
	
	/** Add an annotation to a type object, optionally replacing existing annotations that are subtypes of annoType. */
	X10TypeObject addAnnotation(X10TypeObject o, X10ClassType annoType, boolean replace);
		
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

	NullableType createNullableType(Position p, X10NamedType t);
	FutureType createFutureType(Position p, X10NamedType t);
	/*ParametricType createParametricType(Position pos,
										X10ReferenceType type,
										List typeparameters,
										DepParameterExpr expr);*/

	/**
	 * Create a <code>ClosureType</code> with the given signature.
	 */
	ClosureType closure(Position p, Type returnType, List<Type> argTypes, List<Type> throwTypes);

	ClassType X10Object();
	ClassType parameter1();
	
	ClassType place();
	ClassType region();
	C_Here_c here();
	ClassType point();
	ClassType distribution();
	ClassType Activity();
	ClassType FutureActivity();
	ClassType array();
	ClassType clock();
	ClassType value();
	ClassType Runtime();
	ClassType OperatorPointwise();
	ClassType OperatorBinary();
	ClassType OperatorUnary();
	ClassType ArrayOperations();

	C_Lit FALSE();
	C_Lit TRUE();
	C_Lit NEG_ONE();
	C_Lit ZERO();
	C_Lit ONE();
	C_Lit TWO();
	C_Lit THREE();
	C_Lit NULL();
	
	CodeInstance asyncCodeInstance(boolean isStatic);

	/** Create a closure instance.
	 * @param pos Position of the closure.
	 * @param container Containing type of the closure.
	 * @param returnType The closure's return type.
	 * @param argTypes The closure's formal parameter types.
	 * @param excTypes The closure's exception throw types.
	 */
	ClosureInstance closureInstance(Position pos, ClassType typeContainer, MethodInstance methodContainer,
		Type returnType, List argTypes, List excTypes);

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

	/**
	 * Return the ClassType object for the x10.lang.Array interface.
	 */
	ClassType Array();

	// RMF 7/11/2006 - Added so that the parser can create a canonical type node
	// for "primitive types", which otherwise will cause disambiguation to fail.
	//
	// Technically, there are no primitive types in X10, but the compiler can't
	// quite treat types like "int" as ordinary object types - there are no source
	// or class files corresponding to them, and therefore disambiguating such
	// types will always fail. The solution is to prevent the compiler from
	// attempting to disambiguate them by giving the parser enough information
	// to create a CanonicalTypeNode for them. Currently this is only an issue
	// when such types are decorated with place specifiers, in which case the
	// ClassOrInterfaceType production fires, and the left-hand side type could
	// be either an ordinary class/interface or a "primitive type". In the
	// absence of place specifiers, the base Java grammar rule IntegralType
	// fires, which always creates a CanonicalTypeNode.
	/**
	 * @return true iff the given name is that of a "primitive type".
	 */
	public boolean isPrimitiveTypeName(String name);

	/** Return the method instance for runtime.Primitive.equals */
	 MethodInstance primitiveEquals();

	/** Return the method instance for runtime.T.tValue() */
	 MethodInstance getter(X10PrimitiveType t);

	/** Return the constructor instance for runtime.T.T(t) */
	 ConstructorInstance wrapper(X10PrimitiveType t);

	/** Return boxed type runtime.T for primitive t. */
	 X10NamedType boxedType(X10PrimitiveType t);
    
	/** Create a property instance. A property is a  final instance field
	 * that can be used to construct deptypes.
	 * @param pos Position of the field.
	 * @param container Containing type of the field.
	 * @param flags The field's flags.
	 * @param type The field's type.
	 * @param name The field's name.
	 */
    X10FieldInstance propertyInstance(Position pos, ReferenceType container,
                                Flags flags, Type type, String name);


  boolean isPrimitiveTypeArray(Type me);
 
     boolean isNullable(Type me) ;
     boolean isFuture(Type me) ;
     boolean isIndexable(Type me) ;
      boolean isX10Array(Type me) ;
      boolean isBooleanArray(Type me);
     boolean isCharArray(Type me) ;
     boolean isByteArray(Type me) ;
      boolean isShortArray(Type me) ;
      boolean isIntArray(Type me) ;
      boolean isLongArray(Type me) ;
     boolean isFloatArray(Type me);
      boolean isDoubleArray(Type me);
      boolean isClock(Type me) ;
      boolean isPoint(Type me);
      boolean isPlace(Type me);
      boolean isRegion(Type me);
      boolean isDistribution(Type me);
      boolean isDistributedArray(Type me);
     boolean isValueType( Type me);
	Type baseType(Type theType);
   
	/**
	 * Is a type constrained (i.e. its depClause is != null)
	 * If me is a nullable, then the basetype is checked.
	 * @param me Type to check
	 * @return true if type has a depClause.
	 */
	public boolean isTypeConstrained(Type me);
	
   VarInstance createSelf(X10Type t);
   TypeTranslator typeTranslator();
   boolean equivClause(X10Type m, X10Type o);
   boolean equivClause(Constraint m, Constraint o);
   boolean entailsClause(X10Type me, X10Type other);
   boolean entailsClause(Constraint me, Constraint other);
   FieldInstance fieldInstance(Position pos, ReferenceType container,
           Flags flags, String name, String initValue);
   
   
   /**
    * True if the two types are equal, ignoring their dep clauses.
    * @param other
    * @return
    */
   boolean equalsWithoutClause(X10Type me, X10Type other);
   boolean typeBaseEquals(Type me, Type other);
   boolean isBoxedType(Type type);
   String getGetterName(Type type);
   boolean equalTypeParameters(List<Type> a, List<Type> b);
   
   ConstructorInstance constructorInstance(Position pos,
			 ClassType container,
			 Flags flags, X10Type returnType, List argTypes,
			 List excTypes);
   Type boxedTypeToPrimitiveType(Type presumedBoxedType);
} // end of X10TypeSystem

