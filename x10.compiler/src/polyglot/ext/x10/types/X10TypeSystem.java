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
    ArrayType arrayOf(Position pos, Type base);

    /**
     * Return an array of <code>base</code> type -- a
     * <code>value</code> array if <code>isValue==true</code>, and a
     * <code>reference</code> array otherwise.
     */
    ArrayType arrayOf(Position pos, Type base, boolean isValue);

    /**
     * Return a reference array of <code>base</code> type with a
     * dependent parameter, expr.
     */
    ArrayType arrayOf(Position pos, Type base, DepParameterExpr expr);
    /**
     * Return an array of <code>base</code> type -- a <code>value</code> array
     * if <code>isValue==true</code>, and a <code>reference</code> array
     * otherwise -- with a dependent parameter, expr.
     */
    ArrayType arrayOf(Position pos, Type base, 
		      boolean isValue, DepParameterExpr expr);

    NullableType createNullableType( Position p, ReferenceType t);
    FutureType createFutureType( Position p, Type t);
    ClassType X10Object();
    public ParsedClassType getRuntimeType();
    public ParsedClassType getActivityType();
    public ParsedClassType getFutureActivityType();
    public ParsedClassType getFutureType();
    public ParsedClassType getX10ObjectType();
    public ParsedClassType getPlaceType();
    
    /** Return the method instance for runtime.Primitive.equals */
    public MethodInstance primitiveEquals();

    /** Return the method instance for runtime.T.tValue() */
    public MethodInstance getter(PrimitiveType t);

    /** Return the constructor instance for runtime.T.T(t) */
    public ConstructorInstance wrapper(PrimitiveType t);

    /** Return boxed type runtime.T for primitive t. */
    public Type boxedType(PrimitiveType t);
    
    
} // end of X10TypeSystem
