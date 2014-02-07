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

import polyglot.types.Type;
import polyglot.util.InternalCompilerError;

/**
 * An <code>IntLit</code> represents a literal in Java of an integer
 * type.
 */
public interface IntLit extends NumLit 
{
    /** Integer literal kinds: int (e.g., 0) or long (e.g., 0L). */
    public enum Kind {
        INT("Int"), LONG("Long"), BYTE("Byte"), SHORT("Short"), UINT("UInt"), ULONG("ULong"), UBYTE("UByte"), USHORT("UShort");
        private final String name;

        private Kind(String name) {
            this.name = name;
        }
        public String toString() { return name; }
        public Kind toSigned() {
            if (this==ULONG) return LONG;
            if (this==UINT) return INT;
            if (this==USHORT) return SHORT;
            if (this==UBYTE) return BYTE;
            return this;
        }
        public boolean isSigned() {
            return this == LONG || this == INT || this == SHORT || this == BYTE;
        }
        public boolean isUnsigned() {
            return this == ULONG || this == UINT || this == USHORT || this == UBYTE;
        }
        public static Kind get(Type t) {
            if (t.isLong()) return LONG;
            if (t.isInt()) return INT;
            if (t.isShort()) return SHORT;
            if (t.isByte()) return BYTE;
            if (t.isULong()) return ULONG;
            if (t.isUInt()) return UINT;
            if (t.isUShort()) return USHORT;
            if (t.isUByte()) return UBYTE;
            
            return INT; // default type for an integral literal is INT.
        }

    }

    public static final Kind INT   = Kind.INT;
    public static final Kind LONG  = Kind.LONG;
    public static final Kind BYTE  = Kind.BYTE;
    public static final Kind SHORT  = Kind.SHORT;
    public static final Kind UINT = Kind.UINT;
    public static final Kind ULONG = Kind.ULONG;
    public static final Kind UBYTE = Kind.UBYTE;
    public static final Kind USHORT = Kind.USHORT;


    /** Get the literal's value. */
    long value();

    /** Set the literal's value. */
    IntLit value(long value);

    /** Get the kind of the literal: INT or LONG. */
    Kind kind();

    /** Set the kind of the literal: INT or LONG. */
    IntLit kind(Kind kind);

    /** Is this a boundary case, i.e., max int or max long + 1? */
    boolean boundary();

    /** Print the string as a positive number. */
    String positiveToString();
}
