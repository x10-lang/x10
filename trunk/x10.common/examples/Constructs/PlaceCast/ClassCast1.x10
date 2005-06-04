import x10.lang.*;

public value class ClassCast1 {

	    public boolean run() {    
	    	final (nullable ClassCast1)[] A = { null, new ClassCast1()};
	    	nullable ClassCast1 val = (A[0] == null) ? null : A[1];
	    	return new ClassCast1() == (ClassCast1) val; // should throw a ClassCastException
	    }
	        
	    public static void main(String[] args) {
	    	boolean b= false;
	    	try {
	    		finish b=(new ClassCast1()).run();
	    	} catch (Throwable e) {
	    		if (e instanceof ClassCastException)
	    		  b = true;
	    	}
	        System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
	        x10.lang.Runtime.setExitCode(b?0:1);
	    }

}