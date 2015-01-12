/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.lang;

import x10.compiler.NativeRep;
import x10.compiler.Native;
/**
 * Create a global reference encapsulating a given object.  The ref has
 * the property home specifying the place at which it was
 * created. Besides that, the ref offers only the operations of Any at a
 * place other than the one where it was created (its home place).  Two
 * such refs are == if and only if they were created at the same place
 * and at that place the objects they encapsulate are ==.  
 *
 * <p> At its home place, the value when applied to the empty list of
 * arguments returns its encapsulated value.
 */
@NativeRep("java", "x10.core.GlobalRef<#T$box>", null, "x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, #T$rtt)")
@NativeRep("c++", "x10::lang::GlobalRef< #T >", "x10::lang::GlobalRef< #T >", null)
public struct GlobalRef[T](
    @Native("java", "(#this).home")
    @Native("c++", "::x10::lang::Place::_make((#this)->location)")
    home:Place,
    @Native("java", "(#this).epoch")
    @Native("c++", "(#this)->epoch")
    epoch:Long) {T isref} {

    /** 
     * Create a value encapsulating the given object of type T.
     */
    @Native("c++", "::x10::lang::GlobalRef< #T >(#t)")
    public native def this(t:T):GlobalRef[T]{self.home==here}; 

    /** 
     * Can only be invoked at the place at which the value was
     * created. Returns the object encapsulated in the value.
     */
    @Native("java", "(#this).$apply$G()")
    @Native("c++", "(#this)->__apply()")
    public native operator this(){here == this.home}:T;

    /**
     * Unsafe native method to get value.
     * Assumes here == this.home; however this is not enforced
     * by a constraint because it would entail dynamic checks.
     * Must only be called at this.home !
     */
    @Native("java", "(#this).$apply$G()")
    @Native("c++", "(#this)->__apply()")
    private native def localApply():T;

    /**
     * Evaluates the given closure at (this.home), passing as a 
     * parameter the object that is encapsulated by this GlobalRef.
     * This is equivalent to the following idiom:
     *   if (here == this.home) return eval(this());
     *   else return at (this.home) eval(this());
     * However, because it does not use a place constraint on the
     * method, it avoids a dynamic place check on the first branch.
     */
    @Native("java", "x10.core.GlobalRef.LocalEval.<#T$box,#U$box>evalAtHome(#T$rtt,#U$rtt,#this,#eval)")
    @Native("c++", "::x10::lang::GlobalRef__LocalEval::evalAtHome< #T,#U >(#this, #eval)")
    public native def evalAtHome[U](eval:(T)=> U):U;

    /**
     * If (this.home == here), returns the object that is 
     * encapsulated by this GlobalRef.  If (this.home != here),
     * returns a copy at the current place.
     * This is equivalent to the following idiom:
     *   if (here == this.home) return this();
     *   else return at (this.home) this();
     * However, because it does not use a place constraint on the
     * method, it avoids a dynamic place check on the first branch.
     */
    @Native("java", "x10.core.GlobalRef.LocalEval.<#T$box>getLocalOrCopy(#T$rtt,#this)")
    @Native("c++", "::x10::lang::GlobalRef__LocalEval::getLocalOrCopy< #T >(#this)")
    public native def getLocalOrCopy():T;

    /** 
     * Called when the object referred to by the GlobalRef is no 
     * longer accesible from other places and therefore can
     * be removed from the data sturctures that keep objects
     * alive even when they are not live locally. 
     * Can only be invoked at the place at which the value was
     * created. 
     */
    @Native("java", "(#this).forget()")
    @Native("c++", "(#this)->forget()")
    public native def forget(){here == this.home}:void;

    /*
     * @Native methods from Any because the handwritten C++ code doesn't 100% match 
     * what the compiler would have generated.
     */

    @Native("java", "(#this).toString()")
    @Native("c++", "(#this)->toString()")
    public native def  toString():String;

    @Native("java", "(#this).equals(#that)")
    @Native("c++", "(#this)->equals(#that)")
    public native def equals(that:Any):Boolean;

    @Native("java", "(#this).hashCode()")
    @Native("c++", "(#this)->hashCode()")
    public native def hashCode():Int;

    @Native("java", "(#this).isNull()")
    @Native("c++", "(#this)->isNull()")
    public native def isNull():Boolean;

    private static class LocalEval {
        /**
         * Evaluates the given closure at the home place of <code>ref</code>.
         * This is equivalent to the following idiom:
         *   if (here == ref.home) return eval(ref);
         *   else return at (ref.home) eval(ref);
         * However, as it does not use a place constraint on the
         * method, it avoids a dynamic place check on the first branch.
         */
        public static def evalAtHome[T,U](ref:GlobalRef[T], eval:(T)=> U){T isref}:U {
            if (here == ref.home) {
                return eval(ref.localApply());
            } else {
                return at (ref.home) eval(ref());
            }
        }

        /**
         * If (ref.home == here), returns the object that is 
         * encapsulated by ref.  If (ref.home != here),
         * returns a copy at the current place.
         * This is equivalent to the following idiom:
         *   if (here == ref.home) return ref();
         *   else return at (ref.home) ref();
         * However, as it does not use a place constraint on the
         * method, it avoids a dynamic place check on the first branch.
         */
        public static def getLocalOrCopy[T](ref:GlobalRef[T]){T isref}:T {
            if (here == ref.home) {
                return ref.localApply();
            } else {
                return at (ref.home) ref();
            }
        }
    }

}
//public type GlobalRef[T](p:Place) {T isref} = GlobalRef[T]{self.home==p};
