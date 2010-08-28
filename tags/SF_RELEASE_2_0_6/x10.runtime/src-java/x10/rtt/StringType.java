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

package x10.rtt;


public class StringType extends RuntimeType<String> {

    // version with only base class
    public StringType() {
        super(String.class);
    }

    // version with based class nad parents
    public StringType(Type<?>... parents) {
        super(String.class, parents);
    }

    @Override
    public String typeName() {
        return "x10.lang.String";
    }

    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.String;
    }

    @Override
    public Object makeArray(int length) {
        return new String[length];
    }

    @Override
    public Object makeArray(Object... elem) {
        byte[] arr = new byte[elem.length];
        for (int i = 0; i < elem.length; i++) {
            arr[i] = ((Number)elem[i]).byteValue();
        }
        return arr;
    }
    
    @Override
    public String getArray(Object array, int i) {
        return ((String[]) array)[i];
    }
    
    @Override
    public String setArray(Object array, int i, String v) {
        return ((String[]) array)[i] = v;
    }
    
    @Override
    public int arrayLength(Object array) {
        return ((String[]) array).length;
    }
}
