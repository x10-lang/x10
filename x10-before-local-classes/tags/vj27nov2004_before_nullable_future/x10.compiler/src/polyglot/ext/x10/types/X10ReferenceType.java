/*
 * Created on Oct 2, 2004
 */
package polyglot.ext.x10.types;

import polyglot.types.ReferenceType;

/**
 * An X10ReferenceType adds the additional dimensions of possibly being a future type or
 * a nullable type to the 'ordinary' Java ReferenceTypes.
 * 
 * @author Christian Grothoff
 */
public interface X10ReferenceType 
    extends ReferenceType {

    public static int NULLABLE = 1; 

    public static int IS_FUTURE = 2;   

    public boolean isNullable();
    
    public boolean isFuture();
       
} // end of X10ReferenceType
