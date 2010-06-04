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
	public Arity a;
	public int blk;
	public CallTableKey aslast;
	public Arity getArity(){
	    return a;
	}
	
	public CallTableVal(String s, int b){
	    scope = renameMethod(s);
	    blk = b;
	    aslast = null;
	    a = Arity.One;
	}
	public CallTableVal(String s, int b, Arity a){
	    scope = renameMethod(s);
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
	
	public String renameMethod(String name) {
	    if (name.contains("activity") && (name.contains(">"))) {
		int first_colon = name.indexOf(':');
		int second_colon = name.indexOf(':', first_colon + 1);
		String file_name = name.substring(first_colon + 1, second_colon);
		int last = name.indexOf('>');
		String line = name.substring(second_colon + 1, last);
		int hashed_name = file_name.hashCode();
		return "activity" + String.valueOf(hashed_name) + ":" + line;
	    }
	    return name;
	}
	public abstract int hashCode();
	public abstract String toString();
	public abstract boolean equals(Object o);
}
