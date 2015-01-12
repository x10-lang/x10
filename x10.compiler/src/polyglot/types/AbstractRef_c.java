/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.types;

import java.io.Serializable;

import polyglot.frontend.Goal;

public abstract class AbstractRef_c<T> implements Ref<T>, Serializable {
	private static final long serialVersionUID = -669174456653180408L;

	T value;
	boolean known;
        
        public AbstractRef_c() {
        }
        
        /** Initialize the default value. */
        public AbstractRef_c(T v) {
        	this.value = v;
        	this.known = false;
        }

        public boolean known() {
        	return known;
        }

        public void update(T v) {
        	this.value = v;
        	this.known = true;
        }

        public void updateCache(T v) {
        	this.value = v;
//        	assert ! known();
        }
        
        public void update(T v, Goal goal) {
        	update(v);
        }
        
        /** Update the value to v only if there is not valid value for the view. */
        public void conditionalUpdate(T v) {
        	if (! known()) {
        		update(v);
        	}
        }
        
        public T getCached() {
        	return this.value;
        }

        public T get() {
        	return getCached();
        }
        
        public String toString() {
            return "REF(" + getCached() + ")";
        }
}
