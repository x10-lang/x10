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

import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;


public class CharType extends RuntimeType<Character> {
    public CharType() {
        super(char.class);
    }
    
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Character;
    }

    @Override
    public Object makeArray(int length) {
        return new char[length];
    }
    
    @Override
    public Character getArray(Object array, int i) {
        return ((char[]) array)[i];
    }
    
    @Override
    public Character setArray(Object array, int i, Character v) {
        return ((char[]) array)[i] = v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((char[]) array).length;
    }

    @Override
    public Fun_0_2<Character, Character, Character> maxOperator() {
        return new Fun_0_2<Character, Character, Character>() {
            public Character apply(Character x, Character y) {
                return (x > y ? x : y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.CHAR; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.CHAR; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.CHAR; }
        };
    }
    @Override
    public Fun_0_2<Character, Character, Character> minOperator() {
        return new Fun_0_2<Character, Character, Character>() {
            public Character apply(Character x, Character y) {
                return (x < y ? x : y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.CHAR; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.CHAR; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.CHAR; }
        };
    }
    
    @Override
    public Character minValue() {
        return Character.MIN_VALUE;
    }

    @Override
    public Character maxValue() {
        return Character.MAX_VALUE;
    }
    
    @Override
    public Character zeroValue() {
        return (char) 0;
    }
    
    @Override
    public Character unitValue() {
        return (char) 1;
    }
}
