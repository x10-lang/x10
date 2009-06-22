package hello.matt;
public class HelloX10 {
	String _greeting;
    public static void main(final String[] args) {
    	new HelloX10().speakMany();
    	// distribution d;
    	finish async {
    		for (int i=0; i<4; i++) {
    			final int index=i;
    			async /*(d[i/2])*/ {
    				HelloX10 x;
    				(x=(index<args.length ? new HelloX10(args[index]) : new HelloX10())).speak(index);
    			}
    		}
    	}
    	System.out.println("Done");
    }
    
    public HelloX10() {
    	this("Hello");
    }
    public HelloX10(String greeting) {
    	_greeting=greeting;
    }
    public void speakMany() {
    	finish async {
    		for (int i=0; i<4; i++) {
    			final int index=i;
    			async /*(d[i/2])*/ {
    				HelloX10 x;
    				new HelloX10().speak(100+index);
    			}
    		}
    	}
    	System.out.println("Done");
    }
    public void speak(int i) {
    	System.out.printf ("%s from Activity [%d]@%s\n", _greeting, i, here);
    }
}