package polyglot.types;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import polyglot.frontend.*;
import polyglot.frontend.Goal.Status;
import polyglot.util.TypeInputStream;

public class LazyRef_c<T> extends AbstractRef_c<T> implements LazyRef<T>, Serializable {
	private static final long serialVersionUID = -7466682011826272737L;

	Runnable resolver;

	/** Create a lazy ref initialized with error value v. */
	public LazyRef_c(T v) {
		this(v, new ErrorRunnable());
	}

	public static class ErrorRunnable implements Runnable {
		public void run() {
		}
	}

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

	public boolean isResolverSet() {
		return !(this.resolver instanceof ErrorRunnable);
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
		T o = super.getCached();
		if (o == null) return "null";
		return o.toString();
	}
}
