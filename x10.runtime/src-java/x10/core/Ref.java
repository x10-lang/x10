/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.core;



// Base class of all X10 ref objects -- should be generated, but we need this class to get Box to compile.
public abstract class Ref implements Any {
    
    private static final long serialVersionUID = 1L;
    
    // N.B. this is called implicitly by all subclasses of Ref
    public Ref() {}

    // constructor just for allocation
    public Ref(java.lang.System[] $dummy) {}

    // constructor for non-virtual call
    public final Ref x10$lang$Object$$init$S() {return this;}
    
	
    // not used
//    public static Ref $make() { return new Ref(); }

    /* TODO to be removed
    public void $init(Object out$){}
    
    // XTENLANG-1858: every Java class that could be an (non-static) inner class must have constructors with the outer instance parameter
    public Ref(Object out$) {}
    */

    @Override
    public java.lang.String toString() {
        return x10.lang.System.identityToString(this);
    }

    // not used (same as Java)
//    @Override
//    public int hashCode() {
//        return x10.lang.System.identityHashCode$O(this);
//    }
//
//    @Override
//    public boolean equals(java.lang.Object other) {
//        return x10.lang.System.identityEquals$O(this, other);
//    }
    
    // not used
//    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Ref $_obj, x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
//        return $_obj;
//    }

    // not used
//    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
//        Ref $_obj = new Ref((java.lang.System[]) null);
//        $deserializer.record_reference($_obj);
//        return $_deserialize_body($_obj, $deserializer);
//    }

    // not used (really?)
//    public short $_get_serialization_id() {
//         return $_serialization_id;
//    }
//    
//    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
//        
//    }

}
