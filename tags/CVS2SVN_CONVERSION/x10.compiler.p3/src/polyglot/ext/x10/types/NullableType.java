/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.Type;

/**
 * @author vj
 */
public interface NullableType extends ReferenceType, X10NamedType {
    /** Ultimate base type; if this is nullable<...nullable<T>...>, then T. */
	X10NamedType ultimateBase();
    
    /** Base type; if this is nullable<T>, then T. */
	X10NamedType base();
	
    Ref<? extends X10NamedType> theBaseType();
    NullableType base(Ref<? extends X10NamedType> base);
}
