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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import java.util.List;

/**
 * A <code>ClassBody</code> represents the body of a class or interface
 * declaration or the body of an anonymous class.
 */
public interface ClassBody extends Term
{
    /**
     * List of the class's members.
     * @return A list of {@link polyglot.ast.ClassMember ClassMember}.
     */
    List<ClassMember> members();

    /**
     * Set the class's members.
     * @param members A list of {@link polyglot.ast.ClassMember ClassMember}.
     */
    ClassBody members(List<ClassMember> members);

    /**
     * Add a member to the class, returning a new node.
     */
    ClassBody addMember(ClassMember member);
}
