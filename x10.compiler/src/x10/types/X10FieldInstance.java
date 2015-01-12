/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.types;

import polyglot.ast.Expr;
import polyglot.ast.Receiver;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;

import polyglot.types.TypeObject;
import x10.types.constraints.CConstraint;
/**
 * Represents information about a Property. A property has the same
 * attributes as a Field, except that it is always public, instance and final
 * and has no initializer.
 * @author vj
 *
 */
public interface X10FieldInstance extends FieldInstance, TypeObject, X10Use<X10FieldDef> {
	
	public static final String MAGIC_PROPERTY_NAME = "propertyNames$";
	public static final String MAGIC_CI_PROPERTY_NAME = "classInvariant$";
	
	/** Is this field a property? */
	boolean isProperty();
	
	/** Type of the field with self==FI. */
	Type rightType();
	X10FieldInstance type(Type type, Type rightType);

	X10FieldInstance type(Type type);

	X10FieldInstance error(SemanticException e);
}
