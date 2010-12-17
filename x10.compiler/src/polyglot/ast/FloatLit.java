/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

/**
 * A <code>FloatLit</code> represents a literal in java of type
 * <code>float</code> or <code>double</code>.
 */
public interface FloatLit extends Lit 
{    
    /** Integer literal kinds: float (e.g., 0.0F) or double (e.g., 0.0). */
    public static enum Kind {
        FLOAT("float"),DOUBLE("double");

        public final String name;
        private Kind(String name) {
            this.name = name;
        }
        @Override public String toString() {
            return name;
        }
    }

    public static final Kind FLOAT = Kind.FLOAT;
    public static final Kind DOUBLE = Kind.DOUBLE;

    /** The kind of literal: FLOAT or DOUBLE. */
    Kind kind();

    /** Set the kind of literal: FLOAT or DOUBLE. */
    FloatLit kind(Kind kind);

    /** The literal's value. */
    double value();

    /** Set the literal's value. */
    FloatLit value(double value);
}
