/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.rtt;


public class AnyType extends RuntimeType<Object> {

    private static final long serialVersionUID = 1L;

    // make sure deserialized RTT object is not duplicated
    private Object readResolve() throws java.io.ObjectStreamException {
        return Types.ANY;
    }

    public AnyType() {
        super(Object.class);
    }
    
    @Override
    public String typeName() {
        return "x10.lang.Any";
    }
    
    // for shortcut
    @Override
    public boolean isAssignableTo(Type<?> superType) {
        return superType == Types.ANY;
    };
    
}
