/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.types;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.FieldInstance;
import polyglot.types.Matcher;
import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;

/** The representative of ClassType in the X10 type hierarchy. 
 * 
 * A class is a reference; arrays are examples of references which are not classes.
 * 
 * @author vj
 *
 */
public interface X10ClassType extends ClassType,  X10Use<X10ClassDef>, CodeInstance<X10ClassDef> {

	/** Property initializers, used in annotations. */
	List<Expr> propertyInitializers();
	Expr propertyInitializer(int i);
	X10ClassType propertyInitializers(List<Expr> inits);

	boolean isIdentityInstantiation();

	/**
	 * The list of properties of the class. 
	 * @return
	 */
	List<FieldInstance> properties();

	/**
	 * The sublist of properties defined at this class.
	 * All and exactly the properties in this list need to be 
	 * set in each constructor using a property(...) construct.
	 * @return
	 */
	List<FieldInstance> definedProperties();

	List<Type> typeArguments();
	X10ClassType typeArguments(List<Type> typeArgs);

	boolean hasParams();
	List<Type> typeMembers();

	MacroType typeMemberMatching(Matcher<Type> matcher);

	boolean isJavaType();

	X10ClassType container();
	X10ClassType container(ContainerType container);

	X10ClassType error(SemanticException e);
	
    /**
     * Return true if this type object represents an X10 struct.
     * @return
     */
    boolean isX10Struct();
    
    /**
     * Return a type object that is the same as this except that it will
     * return true to an isX10Struct() query.
     * @return
     */
    Type makeX10Struct();

}
