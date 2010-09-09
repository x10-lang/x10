package x10.finish.table;
/**
 * 
 * class represents a finish block in x10
 */
public class CallTableScopeKey extends CallTableKey {
	
	private static final long serialVersionUID = 1L;
	/**
	 * true if this object represents a "finish" contruct in the program
	 * otherwise it represents an "at"
	 */
	public boolean is_finish;
	/**
	 * to calculate the arity of a method call from this block
	 */
	public int blk;
	
	public CallTableScopeKey(String s,String n, int line, int column, 
		int b, boolean f) {
		// finish = f;
		super(s,n,line,column);
		blk = b;
		is_finish = f;
	}
	
	public String genSignature(){
	    String tmp;
	    if(is_finish == true){
	    	    tmp = ".finish.";
	    }
	    else{
	    	    tmp = ".at.";
	    }
	    return (scope + tmp + line + "." + column);
	}
	
	public String toString() {
	    return genSignature();
	}

	public boolean equals(Object o) {
	    if (o instanceof CallTableScopeKey) {
		return this.toString().equals(((CallTableScopeKey) o).toString());
	    }
	    return false;
	}

	public int hashCode() {
	    return this.toString().hashCode();
	}
}
