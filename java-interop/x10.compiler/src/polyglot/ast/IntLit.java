/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

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
