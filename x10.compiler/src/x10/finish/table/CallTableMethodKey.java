package x10.finish.table;
public class CallTableMethodKey extends CallTableKey {

	private static final long serialVersionUID = 1L;
	
	public CallTableMethodKey(String s, String n, int l, int c) {
		super(s,n,l,c);	}

	public String toString() {
		return scope+"."+name+"."+line+"."+column;
	}
	/* equals and hashCode are to make sure an object of CallTableKey
	 * can be hashed in a HashMap */
	public boolean equals(Object o) {
	    boolean result = false;
	    if(o instanceof CallTableKey){
		result = genSignature().equals(((CallTableKey) o).genSignature());
	    }
	    return result;
	}

	public int hashCode() {
		return scope.hashCode();
	}
}