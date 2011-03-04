public class HWConstructorCall1 {
    public static class ThisCall extends HWConstructorCall1 {
    
    public final int y = 1000;
    
    public ThisCall(int x) {this(); System.out.println("Hello World! * " + x);}
    public ThisCall() {super(); System.out.println("Hello World! Nullary ");}
    
    }
    
    public final int x = 500;
    

    public HWConstructorCall1() {
      System.out.println("Hello, world! from HWConstructorCall");
    }
	public final static void main(String[] s) {
	  System.out.println((new ThisCall(5)).x);
	}
}
