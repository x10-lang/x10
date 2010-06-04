package x10.finish.table;
public class CallTableMethodKey extends CallTableKey {

	private static final long serialVersionUID = 1L;
	public final String sig;
	public CallTableMethodKey(String s) {
		super(s);
		sig = genSignature();
		 

	}
	public String genSignature(){
	    return scope;
	}
	public String toString() {
		return scope;
	}
	/* equals and hashCode are to make sure an object of CallTableKey
	 * can be hashed in a HashMap */
	public boolean equals(Object o) {
	    boolean result = false;
	    if(o instanceof CallTableKey){
		result = this.scope.equals(((CallTableKey) o).scope);
	    }
	    return result;
	}

	public int hashCode() {
		return scope.hashCode();
	}
}