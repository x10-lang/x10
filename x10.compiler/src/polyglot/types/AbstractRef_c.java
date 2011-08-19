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
