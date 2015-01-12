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

import java.io.*;

import polyglot.util.TypeInputStream;
import polyglot.util.InternalCompilerError;

public class LazyRef_c<T> extends AbstractRef_c<T> implements LazyRef<T>, Serializable {
	private static final long serialVersionUID = -7466682011826272737L;

	Runnable resolver;

    /** Create a lazy ref initialized with error value v. */
	public LazyRef_c(T v) {
		this(v, EMPTY_RESOLVER);
	}

    public static Runnable EMPTY_RESOLVER = new Runnable() {
		public void run() {
		}
	};
    public static Runnable THROW_RESOLVER = new Runnable() {
		public void run() {
            throw new InternalCompilerError("This resolver should never be called! Use ref.update(...) before calling ref.get()");
		}
	};

	/** Create a lazy ref initialized with error value v. */
	public LazyRef_c(T v, Runnable resolver) {
		super(v);
		this.resolver = resolver;
	}

	/** Goal that, when satisfied, will resolve the reference */
	public Runnable resolver() {
		return resolver;
	}

	public void setResolver(Runnable resolver) {
		this.resolver = resolver;
	}

	public boolean isThrowResolver() {
		return this.resolver== THROW_RESOLVER;
    }
	public boolean isResolverSet() {
		return this.resolver!= EMPTY_RESOLVER;
	}

	public T get() {
		if (! known()) {
			if (resolver == null) {
				assert false;
			}

			resolver.run();
            resolver = null; // for garbage collection
			known = true;
		}

		return super.get();
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		assert resolver != null : "resolver for " + this + " is null";
		assert resolver instanceof Serializable : "resolver for " + this + " not Serializable";
		assert known() : "resolver for " + this + " not reached";
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		if (in instanceof TypeInputStream) {
			// Mark the resolver as NEW to force re-resolution
			known = false;
		}
	}

	public String toString() {
	    if (known()) {
		T o = super.getCached();
		if (o == null) return "null";
		return o.toString();
	    } else {
	        return resolver.toString();
	    }
	}
}
