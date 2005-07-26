/*
 * Testing quotes in strings
 */
public class StringTest {
	public int val;
	public StringTest() {
		val=10;
	}
	public boolean run() { 
		String foo="the number is "+val;
		if(!( val==10 && foo.equals("the number is "+"10"))) return false;
		if(foo.charAt(2) !='e') return false;
		return true;
		
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new StringTest()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }

}
