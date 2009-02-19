public class HWConstructorCall2 {
    public static class ThisCall extends HWConstructorCall2 {
    
    public ThisCall(int x) {this(); System.out.println("Hello World! * " + x);}
    public ThisCall() {System.out.println("Hello World! Nullary ");}
    
    }
    

    public HWConstructorCall2() {
      System.out.println("Hello, world! from HWConstructorCall2");
    }
	public final static void main(String[] s) {
	  new ThisCall(5);
	}
}
