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

package polyglot.types;

/**
 * A <code>MemberInstance</code> is an entity that can be a member of
 * a class.
 */
public interface MemberDef extends Def
{
    /**
     * Return the member's flags.
     */
    Flags flags();
    
    /**
     * Destructively set the member's flags.
     * @param flags
     */
    void setFlags(Flags flags);

    /**
     * Return the member's containing type.
     */
    Ref<? extends ContainerType> container();
    
    /**
     * Destructively set the member's container.
     * @param container
     */
    void setContainer(Ref<? extends ContainerType> container);
}
