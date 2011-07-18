/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

/**
 * A <code>LocalInstance</code> contains type information for a local variable.
 */
public interface LocalDef extends VarDef {
    LocalInstance asInstance();
}
