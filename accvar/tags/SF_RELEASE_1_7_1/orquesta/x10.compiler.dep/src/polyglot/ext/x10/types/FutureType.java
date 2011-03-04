/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 26, 2004
 *
 * 
 */
package polyglot.ext.x10.types;

import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.Type;


/** The type of futures. 
 * @author vj
 *
 * 
 */
public interface FutureType extends ReferenceType, X10NamedType {
    X10NamedType base();
    Ref<? extends X10NamedType> theBaseType();
    FutureType base(Ref<? extends X10NamedType> base);
}
