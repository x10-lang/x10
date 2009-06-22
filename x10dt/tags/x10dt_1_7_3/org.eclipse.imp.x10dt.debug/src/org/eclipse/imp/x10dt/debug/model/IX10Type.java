package org.eclipse.imp.x10dt.debug.model;
import org.eclipse.imp.x10dt.debug.model.IX10Declaration;
import org.eclipse.imp.x10dt.debug.model.IX10Type;

/**
 * X10DT Debugger Model Interface
 * 
 * @author mmk
 * @since 10/10/08
 */
public interface IX10Type {  // I'm guessing here: need to look more closely into 1.7 type system
	IX10Type getBaseType(); // e.g., Tree for Tree<MyNode>; null if not parameterized type
	IX10Declaration[] getMembers(); // of base type; should this be "members" or "fields"?
	IX10Type[] getTypeParameters(); // e.g., MyNode for Tree<MyNode>; is "type parameters" the correct terminology?
	boolean isAggregateType(); // whether it is an array type
	IX10Region getRegion(); // domain for aggregate type map; unsure of need: might be obtainable by runtime info associated with aggregate values
}
