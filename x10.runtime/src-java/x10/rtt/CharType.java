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


public class CharType extends RuntimeType<Character> {

	private static final long serialVersionUID = 1L;

    public CharType() {
//        super(char.class,
        super(Character.class,
              new Type[] {
                  new ParameterizedType(Types.COMPARABLE, UnresolvedType.THIS),
                  Types.STRUCT
              });
    }
    
    @Override
    public String typeName() {
        return "x10.lang.Char";
    }

    // for shortcut 
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Character;
    }

    @Override
    public Object makeArray(int length) {
        return new char[length];
    }
    
    @Override
    public Object makeArray(Object... elem) {
        char[] arr = new char[elem.length];
        for (int i = 0; i < elem.length; i++) {
            arr[i] = ((Character)elem[i]).charValue();
        }
        return arr;
    }
    
    @Override
    public Character getArray(Object array, int i) {
        return ((char[]) array)[i];
    }
    
    @Override
    public Character setArray(Object array, int i, Character v) {
        // avoid boxing again
//        return ((char[]) array)[i] = v;
        ((char[]) array)[i] = v;
        return v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((char[]) array).length;
    }

}
