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

package polyglot.ast;

import polyglot.util.*;

/**
 * <code>Ext</code> is the super type of all node extension objects.
 * It contains a pointer back to the node it is extending and a possibly-null
 * pointer to another extension node. 
 */
public abstract class Ext_c implements Ext {
    protected Node node;
    protected Ext ext;

    public Ext_c(Ext ext) {
        this.node = null;
        this.ext = ext;
    }

    public Ext_c() {
        this(null);
        this.node = null;
    }

    /** Initialize the extension object's pointer back to the node.
     * This also initializes the back pointers for all extensions of
     * the extension.
     */
    public void init(Node node) {
        if (this.node != null) {
            throw new InternalCompilerError("Already initialized.");
        }

        this.node = node;

        if (this.ext != null) {
            this.ext.init(node);
        }
    }

    /**
     * Return the node we ultimately extend.
     */
    public Node node() {
	return node;
    }

    /**
     * Return our extension object, or null.
     */
    public Ext ext() {
        return ext;
    }

    public Ext ext(Ext ext) {
        Ext old = this.ext;
        this.ext = null;

        Ext_c copy = (Ext_c) copy();

        copy.ext = ext;

        this.ext = old;

        return copy;
    }

    /**
     * Copy the extension.
     */
    public Ext_c copy() {
        try {
            Ext_c copy = (Ext_c) super.clone();
            if (ext != null) {
                copy.ext = (Ext) ext.copy();
            }
            copy.node = null; // uninitialize
            return copy;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalCompilerError("Java clone() weirdness.");
        }
    }

    public String toString() {
        return StringUtil.getShortNameComponent(getClass().getName());
    }

    /**
     * Dump the AST node for debugging purposes.
     */
    public void dump(CodeWriter w) {
      w.write(toString());
    }
}
