/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package x10.ast;

import java.util.List;

/**
 * A <code>ClassBody</code> represents the body of a class or interface
 * declaration or the body of an anonymous class.
 */
public interface ClassBody extends Term
{
    /**
     * List of the class's members.
     * @return A list of {@link x10.ast.ClassMember ClassMember}.
     */
    List<ClassMember> members();

    /**
     * Set the class's members.
     * @param members A list of {@link x10.ast.ClassMember ClassMember}.
     */
    ClassBody members(List<ClassMember> members);

    /**
     * Add a member to the class, returning a new node.
     */
    ClassBody addMember(ClassMember member);
}
