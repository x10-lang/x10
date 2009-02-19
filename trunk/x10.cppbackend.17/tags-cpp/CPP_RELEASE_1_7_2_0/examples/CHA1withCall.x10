public class CHA1withCall {

    public static class A extends CHA1withCall {
       public void printB (String s) { System.out.println(s);}
    }
      
    public void PrintA (CHA1withCall o) {o.PrintA(o);}
    
	public final static void main(String[] s) {
		System.out.println("Hello, world!");
	}
}
