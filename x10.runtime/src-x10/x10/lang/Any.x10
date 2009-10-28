/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.core.Any", null, null)
//@NativeRep("c++", "ERROR!", "ERROR!", null)
public interface Any {
    @Native("java", "#0.getClass().toString()")
    //@Native("c++", "x10aux::to_string(#0)")
    property global def typeName(): String;

    @Native("java", "x10.lang.Place.place(x10.core.Ref.location(#0))")
    //@Native("c++", "ERROR: loc!")
    property global def loc():Place;

    @Native("java", "x10.core.Ref.at(#0, #1.id)")
    //@Native("c++", "ERROR: at(Place)!")
    property global def at(p:Place):Boolean;

    @Native("java", "x10.core.Ref.at(#0, #1)")
    //@Native("c++", "ERROR: at(Object)!")
    property global def at(r:Object):Boolean;
}
