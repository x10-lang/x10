/*
 * Created on Nov 30, 2004
 *
 * 
 */
package polyglot.ext.x10.types;

import polyglot.types.ArrayType;
import polyglot.types.Type;

/**
 * @author vj
 *
 * 
 */
public interface X10ArrayType extends X10ReferenceType  {
    boolean isValue();
    Type base();

}
