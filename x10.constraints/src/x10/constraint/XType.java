package x10.constraint;


/**
 * All types of an XTerm should implement this interface. The main
 * purpose of the interface rather than using polyglot.types.Type 
 * directly is to avoid package dependencies. 
 *  
 * @author lshadare
 *
 */
public interface XType {
	/**
	 * Returns true if boolean
	 */
	boolean isBoolean(); 
	/**
	 * Returns true if the type is a primitive type such as Int, Bool
	 * @return
	 */
	boolean isPrimitive(); 
	
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
    String typetoSmtString(); 
}
