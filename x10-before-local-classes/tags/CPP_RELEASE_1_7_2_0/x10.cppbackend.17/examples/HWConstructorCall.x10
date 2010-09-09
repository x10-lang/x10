public class HWConstructorCall {
    public static class ThisCall extends HWConstructorCall {
    
    public ThisCall(int x) {this(); System.out.println("Hello World! * " + x);}
    public ThisCall() {super(); System.out.println("Hello World! Nullary ");}
    
    }
    

    public HWConstructorCall() {
      System.out.println("Hello, world! from HWConstructorCall");
    }
	public final static void main(String[] s) {
	  new ThisCall(5);
	}
}
