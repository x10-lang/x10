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


public class ByteType extends RuntimeType<Byte> {

	private static final long serialVersionUID = 1L;

    public ByteType() {
//        super(byte.class,
        super(Byte.class,
              new Type[] {
                  new ParameterizedType(Types.COMPARABLE, UnresolvedType.THIS),
                  Types.STRUCT
              });
    }
    
    @Override
    public String typeName() {
        return "x10.lang.Byte";
    }

    // for shortcut 
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Byte;
    }

    @Override
    public Object makeArray(int length) {
        return new byte[length];
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
    public Byte getArray(Object array, int i) {
        return ((byte[]) array)[i];
    }
    
//    @Override
//    public Byte setArray(Object array, int i, Byte v) {
//        // avoid boxing again
////        return ((byte[]) array)[i] = v;
//        ((byte[]) array)[i] = v;
//        return v;
//    }
    @Override
    public void setArray(Object array, int i, Byte v) {
        ((byte[]) array)[i] = v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((byte[]) array).length;
    }

}
