/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 28, 2004
 *
 */
package polyglot.ext.x10.types;

import polyglot.types.NullType_c;
import polyglot.types.TypeSystem;

/** Every X10 term must have a type. This is the type of the X10 term null.
 * Note that there is no X10 type called Null; only the term null.
 * @author vj
 *
 */
public class X10NullType_c extends NullType_c implements X10NullType {
    public X10NullType_c( TypeSystem ts ) {super(ts);}
    
    public boolean safe() { return true;}

    public String toString() { 
    	return "null";
    }
}
