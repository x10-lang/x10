public class CHA1withVirtualAndLibraryCalls {

    public static class A extends CHA1withVirtualAndLibraryCalls {
       public void printB (CHA1withVirtualAndLibraryCalls o) { }
       public void printA (CHA1withVirtualAndLibraryCalls o) { System.out.println(o.toString());}
    }
      
    public void printA (CHA1withVirtualAndLibraryCalls o) {o.printA(o);}
    
	public final static void main(String[] s) {
		System.out.println("Hello, world!");
	}
}
