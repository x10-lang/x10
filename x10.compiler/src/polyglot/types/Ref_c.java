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



/** Reference to a type object. */
public class Ref_c<T extends TypeObject> extends TypeObject_c implements Ref<T> {
    private static final long serialVersionUID = -794358517607166940L;

    T v;
    
    public Ref_c(T v) {
        super(v.typeSystem(), v.position(), v.errorPosition());
        assert v != null;
        this.v = v;
    }
    
    /** Return true if the reference is not null. */
    public boolean known() {
        return v != null;
    }
    
    public T getCached() {
    	return v;
    }
    
    public T get() {
        return v;
    }
    
    public String toString() {
        if (v == null) return "null";
        return v.toString();
    }

	public void update(T v) {
		this.v = v;
	}
	
	public void when(final Handler<T> h) {
		h.handle(v);
	}
}

