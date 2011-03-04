package x10.finish.table;
import java.io.Serializable;



public abstract class CallTableVal  implements Serializable {
	/**
	 * automatically added for serialization
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * An object of CallTableVal represents an instance of method invocation in an x10 program, and 
	 * the arity of an invocation means whether this invocation is in any loops or if-else 
	 * One: this invocation can be and only be called once, thus not in any loop or if-else
	 * ZeOrOne: this invocation is within in at least one if-else branch, but not in any loops
	 * Unbounded: this invocation is within in at least one loop, possibly in a if-else
	 * @author blshao
	 *
	 */
	public enum Arity {One, ZerOrOne, Unbounded}
	public final String scope;
	public final String name;
	public final int line;
	public final int column;
	public Arity a;
	public int blk;
	public CallTableKey aslast;
	public Arity getArity(){
	    return a;
	}
	
	public CallTableVal(String s, String n, int l, int c, int b){
	    scope = s;
	    name = n;
	    line = l;
	    column = c;
	    blk = b;
	    aslast = null;
	    a = Arity.One;
	}
	public CallTableVal(String s,String n, int l, int c, int b, Arity a){
	    scope = s;
	    name = n;
	    line = l;
	    column = c;
	    blk = b;
	    aslast = null;
	    this.a = a;
	}
	/*
	 * when set a new arity to this object, it must check its
	 * current arity. For example, if current invocation is within
	 * an loop and you want to set it to ZerOrOne, because you find 
	 * this invocation is also in a branch. The final arity will still be
	 * unbounded. 
	 */
	public void setArity(Arity tmparity){
	    /* One -> lowest priority
	     * Unbounded -> highest priority
	     */
	    switch(a){
	    case One:	
		a = tmparity;
		break;
	    case ZerOrOne: 
		switch(tmparity){
		case One: 
		   break;
		case ZerOrOne:
		    break;
		case Unbounded:
		    a = tmparity;
		}
		break;
	    case Unbounded:
		break;
	    }
	}
	public abstract int hashCode();
	public abstract String toString();
	public abstract boolean equals(Object o);
}
