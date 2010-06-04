package x10.finish.table;
/**
 * 
 * class represents a finish block in x10
 */
public class CallTableFinishKey extends CallTableKey {
	
	private static final long serialVersionUID = 1L;
	public final String sig;
	/**
	 *  the start line of the finish instruction in the source code */
	public int line;
	/**
	 *  the start column of the finish instruction in the source  */
	public int column;
	
	/**
	 * true if this object represents a "finish" contruct in the program
	 * otherwise it represents an "at"
	 */
	public boolean is_finish;
	/**
	 * to calculate the arity of a method call from this block
	 */
	public int blk;
	
	public CallTableFinishKey(String s, int line, int column, 
		int b, boolean f) {
		// finish = f;
		super(s);
		this.line = line;
		this.column = column;
		blk = b;
		is_finish = f;
		sig = genSignature();
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
	    return sig;
	}

	public boolean equals(Object o) {
	    if (o instanceof CallTableFinishKey) {
		return this.toString().equals(((CallTableFinishKey) o).toString());
	    }
	    return false;
	}

	public int hashCode() {
	    return this.toString().hashCode();
	}
}
