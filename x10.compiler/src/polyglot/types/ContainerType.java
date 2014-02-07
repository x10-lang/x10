/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.types;

import java.util.List;

import x10.types.MethodInstance;

/**
 * A container type is an X10 class or interface or struct type. It contains members such
 * as other types, fields, methods. (Used to be called StructType in Polyglot.)
 *
 */
public interface ContainerType extends Type {

    /**
     * Return a list of a all the type's members.
     * @return A list of <code>MemberInstance</code>.
     * @see polyglot.types.MemberDef
     */
    List<MemberInstance<?>> members();
    
    /**
     * Return the type's fields.
     * @return A list of <code>FieldInstance</code>.
     * @see polyglot.types.FieldDef
     */
    List<FieldInstance> fields();
    
    /**
     * Return the field named <code>name</code>, or null.
     */
    FieldInstance fieldNamed(Name name);

    /**
     * Return the type's methods.
     * @return A list of <code>MethodInstance</code>.
     * @see polyglot.types.MethodDef
     */
    List<MethodInstance> methods();

    /**
     * Return the methods named <code>name</code>, if any.
     * @param name Name of the method to search for.
     * @return A list of <code>MethodInstance</code>.
     * @see polyglot.types.MethodDef
     */
    List<MethodInstance> methodsNamed(Name name);

    /**
     * Return the methods named <code>name</code> with the given formal
     * parameter types, if any.
     * @param name Name of the method to search for.
     * @param argTypes A list of <code>Type</code>.
     * @return A list of <code>MethodInstance</code>.
     * @see polyglot.types.Type
     * @see polyglot.types.MethodDef
     */
    List<MethodInstance> methods(Name name, List<Type> argTypes, Context context);

    /**
     * Return the true if the type has the given method.
     */
    boolean hasMethod(MethodInstance mi, Context context);

}
