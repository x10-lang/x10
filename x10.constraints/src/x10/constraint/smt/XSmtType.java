package x10.constraint.smt;

import x10.constraint.XType;
import x10.constraint.XTypeSystem;

public interface XSmtType extends XType {
	/**
	 * Returns true if boolean
	 */
	boolean isBoolean(); 
    /**
     * Return true if char.
     */
    boolean isChar();

    /**
     * Return true if byte.
     */
    boolean isByte();

    /**
     * Return true if short.
     */
    boolean isShort();

    /**
     * Return true if int.
     */
    boolean isInt();

    /**
     * Return true if long.
     */
    boolean isLong();

    /**
     * Return true if float.
     */
    boolean isFloat();

    /**
     * Return true if double.
     */
    boolean isDouble();

    /**
     * Return true if UByte
     */
    boolean isUByte();

    /**
     * Return true if UShort
     */
    boolean isUShort();

    /**
     * Return true if UInt
     */
    boolean isUInt();

    /**
     * Return true if ULong
     */
    boolean isULong();
    
    /**
     * Get the type system associated with this specific type.
     * @return the type system of this type
     */
    <T> XTypeSystem<? extends T> xTypeSystem(); 

}
