/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.types.Type;


/**
 * @author vj
 *
 */
public interface X10Type extends Type {

    
    /** Return a subtype of the basetype with the given
     * depclause and type parameters.
     * 
     * @param d
     * @param g
     * @return
     */
    X10Type makeVariant(DepParameterExpr d, List g);
    X10Type  baseType();
    List typeParameters();
    boolean isParametric();
    NullableType toNullable();
    FutureType toFuture();
    DepParameterExpr depClause();
    
    /** The list of properties of the class.
     
     * @return
     */
    List/*<PropertyInstance>*/ properties();

}
