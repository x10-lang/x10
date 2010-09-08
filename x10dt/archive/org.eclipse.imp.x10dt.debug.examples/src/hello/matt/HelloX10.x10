package hello.matt;
public class HelloX10 {
	String _greeting;
	int[.] intArray = new int[[1:4]] (point p[i]) { return i; };
    public static void main(final String[] args) {
    	new HelloX10().greetMany(args);
    }
    
    public HelloX10() {
    	this("Hello");
    }
    public HelloX10(String greeting) {
    	_greeting=greeting;
    }
    
    public void greetMany(final String args[]) {
    	finish async {
    		for (int i=0; i<4; i++) {
    			final int index=i;
    			async {  
    				HelloX10 x = index<args.length ? new HelloX10(args[index]) : new HelloX10();
    				x.greet(index);
    			}
    		}
    	}
    	System.out.println("Done");
    }
     
    public void greet(int i) {
    	place foo = here;
    	place bar = this.location;
    	place baz = location;
    	place bum = foo.location;
     	place p0 = bum.FIRST_PLACE;
    	place pn = bum.LAST_PLACE;
    	int k = bum.MAX_PLACES;
    	if (foo!=bar) System.out.printf("here != this.location: foo: %s; bar: %s\n", foo, bar);
    	System.out.printf ("%s from Activity [%d]@%s\n", _greeting, i, here);
    }
}