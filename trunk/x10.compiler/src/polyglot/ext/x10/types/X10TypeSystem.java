/*
 * Created on Oct 1, 2004
 */

package polyglot.ext.x10.types;

import polyglot.types.*;
import polyglot.util.Position;
import polyglot.ast.Expr;
import polyglot.ext.x10.ast.DepParameterExpr;


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
    X10ArrayType x10arrayOf(Position pos, Type base);

    /**
     * Return an array of <code>base</code> type -- a
     * <code>value</code> array if <code>isValue==true</code>, and a
     * <code>reference</code> array otherwise.
     */
    X10ArrayType x10arrayOf(Position pos, Type base, boolean isValue);

    /**
     * Return a reference array of <code>base</code> type with a
     * dependent parameter, expr.
     */
    X10ArrayType x10arrayOf(Position pos, Type base, DepParameterExpr expr);
    /**
     * Return an array of <code>base</code> type -- a <code>value</code> array
     * if <code>isValue==true</code>, and a <code>reference</code> array
     * otherwise -- with a dependent parameter, expr.
     */
    X10ArrayType x10arrayOf(Position pos, Type base, boolean isValue, DepParameterExpr expr);

    NullableType createNullableType( Position p, ReferenceType t);
    FutureType createFutureType( Position p, Type t);
    ParametricType createParametricType ( Position pos, X10ReferenceType type, DepParameterExpr expr ) ;
    Type X10Object();
    Type place();
    Type region();
    Type point();
    Type distribution();
    Type Activity();
    Type FutureActivity();
    Type array();
    Type clock();
    Type Runtime();
    Type IntArrayPointwiseOp();
    Type DoubleArrayPointwiseOp();
    Type intArray();
    Type intValueArray();
    Type IntReferenceArray();
    Type doubleArray();
    Type doubleValueArray();
    Type DoubleReferenceArray();
    
    
    /** Return the method instance for runtime.Primitive.equals */
    public MethodInstance primitiveEquals();

    /** Return the method instance for runtime.T.tValue() */
    public MethodInstance getter(PrimitiveType t);

    /** Return the constructor instance for runtime.T.T(t) */
    public ConstructorInstance wrapper(PrimitiveType t);

    /** Return boxed type runtime.T for primitive t. */
    public Type boxedType(PrimitiveType t);
    
    
} // end of X10TypeSystem
