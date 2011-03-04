/**
 * 
 */
package polyglot.ext.x10.types;

import polyglot.types.LocalInstance;

/**
 * @author vj
 *
 */
public interface X10LocalInstance extends LocalInstance {
	void setPositionInArgList(int i);
	int positionInArgList();

}
