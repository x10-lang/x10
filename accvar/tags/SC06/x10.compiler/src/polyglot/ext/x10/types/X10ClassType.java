/*
 * Created on Nov 30, 2004
 *
 * 
 */
package polyglot.ext.x10.types;

import polyglot.types.ClassType;

/** The representative of ClassType in the X10 type hierarchy. A class is a
 * reference; arrays are examples of references which are not classes.
 * 
 * @author vj
 *
 */
public interface X10ClassType extends ClassType, X10ReferenceType {

}
